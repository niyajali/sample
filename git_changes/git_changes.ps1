# Git Changes Archiver for Windows
# This PowerShell script detects new and modified files in a Git repository and creates a ZIP archive
# Usage: .\git_changes.ps1 [path_to_folder] [-IncludeZip] [-Output filename.zip]

param (
    [string]$TargetPath = ".",
    [switch]$IncludeZip = $false,
    [string]$Output = "git_changes_$(Get-Date -Format 'yyyyMMdd_HHmmss').zip",
    [switch]$Help = $false
)

# Function to display usage
function Show-Usage {
    Write-Host "Git Changes Archiver for Windows"
    Write-Host "Usage: .\git_changes.ps1 [path_to_folder] [-IncludeZip] [-Output filename.zip]"
    Write-Host "  [path_to_folder]: Path to the folder to check (default: current directory)"
    Write-Host "  -IncludeZip:      Also include zip files in changes"
    Write-Host "  -Output:          Specify output zip filename (default: git_changes_YYYYMMDD_HHMMSS.zip)"
    Write-Host "  -Help:            Show this help message"
    Write-Host ""
    Write-Host "Examples:"
    Write-Host "  .\git_changes.ps1                         # Check current directory"
    Write-Host "  .\git_changes.ps1 .\src                   # Check src directory"
    Write-Host "  .\git_changes.ps1 -IncludeZip             # Check current directory and zip files"
    Write-Host "  .\git_changes.ps1 .\src -Output changes.zip  # Check src directory and save to changes.zip"
}

# Function to check if inside a git repository
function Test-GitRepo {
    try {
        $null = git rev-parse --is-inside-work-tree
        return $true
    } catch {
        return $false
    }
}

# Function to collect all changed files
function Get-ChangedFiles {
    param (
        [string]$Path
    )

    $changedFiles = @()
    $repoRoot = git rev-parse --show-toplevel

    # Get to the root of the git repository
    Push-Location $repoRoot

    try {
        # Modified files (not staged)
        $modifiedFiles = git diff --name-only HEAD -- $Path 2>$null
        foreach ($file in $modifiedFiles) {
            if (Test-Path $file -PathType Leaf) {
                $changedFiles += $file
            }
        }

        # Staged files
        $stagedFiles = git diff --name-only --cached -- $Path 2>$null
        foreach ($file in $stagedFiles) {
            if ((Test-Path $file -PathType Leaf) -and ($changedFiles -notcontains $file)) {
                $changedFiles += $file
            }
        }

        # New (untracked) files
        $untrackedFiles = git ls-files --others --exclude-standard $Path 2>$null
        foreach ($file in $untrackedFiles) {
            if ((Test-Path $file -PathType Leaf) -and ($changedFiles -notcontains $file)) {
                $changedFiles += $file
            }
        }
    } finally {
        Pop-Location
    }

    return $changedFiles
}

# Function to collect changes in zip files
function Get-ZipChanges {
    param (
        [string]$Path,
        [array]$ExistingFiles
    )

    $zipFiles = @()
    $repoRoot = git rev-parse --show-toplevel

    # Find all zip files in the specified path
    $allZipFiles = Get-ChildItem -Path $Path -Filter "*.zip" -Recurse -File

    foreach ($zipFile in $allZipFiles) {
        $relativePath = $zipFile.FullName.Substring($repoRoot.Length + 1).Replace("\", "/")
        Write-Host "Processing zip file: $relativePath"

        # Check if zip file itself is tracked
        $isTracked = $false
        try {
            $null = git ls-files --error-unmatch $relativePath 2>$null
            $isTracked = $true
        } catch {
            $isTracked = $false
        }

        # If zip is new or modified, add it to the results
        if ((-not $isTracked) -or
            ((git diff --name-only HEAD 2>$null) -contains $relativePath) -or
            ((git diff --name-only --cached 2>$null) -contains $relativePath)) {
            if ($ExistingFiles -notcontains $relativePath) {
                $zipFiles += $relativePath
            }
        }
    }

    return $zipFiles
}

# Function to display all found changes
function Show-Changes {
    param (
        [array]$Files,
        [string]$Path
    )

    Write-Host "=== Changes in $Path ==="

    if ($Files.Count -eq 0) {
        Write-Host "No changes found."
        return
    }

    # Group files by status
    $modified = @()
    $new = @()
    $staged = @()

    foreach ($file in $Files) {
        try {
            $null = git ls-files --error-unmatch $file 2>$null
            # File is tracked
            if ((git diff --name-only HEAD 2>$null) -contains $file) {
                $modified += $file
            } elseif ((git diff --name-only --cached 2>$null) -contains $file) {
                $staged += $file
            }
        } catch {
            # File is new/untracked
            $new += $file
        }
    }

    # Display modified files
    Write-Host "Modified files ($($modified.Count)):"
    if ($modified.Count -gt 0) {
        foreach ($file in $modified) {
            Write-Host "  - $file"
        }
    } else {
        Write-Host "  None"
    }

    # Display new files
    Write-Host "`nNew files ($($new.Count)):"
    if ($new.Count -gt 0) {
        foreach ($file in $new) {
            Write-Host "  - $file"
        }
    } else {
        Write-Host "  None"
    }

    # Display staged files
    Write-Host "`nStaged files ($($staged.Count)):"
    if ($staged.Count -gt 0) {
        foreach ($file in $staged) {
            Write-Host "  - $file"
        }
    } else {
        Write-Host "  None"
    }
}

# Function to create zip archive with all changed files
function New-ZipArchive {
    param (
        [array]$Files,
        [string]$OutputFile
    )

    if ($Files.Count -eq 0) {
        Write-Host "No changes found to archive."
        return $false
    }

    $repoRoot = git rev-parse --show-toplevel

    # Create temporary directory for zip creation
    $tempDir = [System.IO.Path]::Combine([System.IO.Path]::GetTempPath(), [System.IO.Path]::GetRandomFileName())
    New-Item -Path $tempDir -ItemType Directory -Force | Out-Null

    Write-Host "Preparing files for archive..."

    # Copy all files to temp directory with full path structure
    foreach ($file in $Files) {
        $sourcePath = [System.IO.Path]::Combine($repoRoot, $file)
        $destPath = [System.IO.Path]::Combine($tempDir, $file)

        if (Test-Path $sourcePath -PathType Leaf) {
            # Create directory structure
            $destDir = [System.IO.Path]::GetDirectoryName($destPath)
            if (-not (Test-Path $destDir)) {
                New-Item -Path $destDir -ItemType Directory -Force | Out-Null
            }

            # Copy file
            Copy-Item -Path $sourcePath -Destination $destPath -Force
            Write-Host "  Added: $file"
        }
    }

    # Create the zip archive using Compress-Archive
    $outputPath = [System.IO.Path]::Combine($repoRoot, $OutputFile)

    # Remove existing file if it exists
    if (Test-Path $outputPath) {
        Remove-Item -Path $outputPath -Force
    }

    Write-Host "Creating zip archive: $OutputFile"
    $files = Get-ChildItem -Path $tempDir -Recurse -File

    if ($files.Count -gt 0) {
        Compress-Archive -Path "$tempDir\*" -DestinationPath $outputPath -Force
        Write-Host "Archive created successfully: $OutputFile"
        Write-Host "Contains $($Files.Count) files"
    } else {
        Write-Host "No files to archive."
    }

    # Clean up temporary directory
    Remove-Item -Path $tempDir -Recurse -Force

    return $true
}

# Main execution
if ($Help) {
    Show-Usage
    exit 0
}

# Check if running in a Git repository
if (-not (Test-GitRepo)) {
    Write-Host "Error: Not inside a Git repository."
    exit 1
}

# Check if the specified path exists
if (-not (Test-Path $TargetPath)) {
    Write-Host "Error: Directory '$TargetPath' does not exist."
    Show-Usage
    exit 1
}

# Collect all changed files
$changedFiles = Get-ChangedFiles -Path $TargetPath

# Also check zip files if requested
if ($IncludeZip) {
    $zipFiles = Get-ZipChanges -Path $TargetPath -ExistingFiles $changedFiles
    $changedFiles = $changedFiles + $zipFiles
}

# Display changes
Show-Changes -Files $changedFiles -Path $TargetPath

# Create zip archive
if ($changedFiles.Count -gt 0) {
    $success = New-ZipArchive -Files $changedFiles -OutputFile $Output
}

Write-Host "`nDone!"