/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.google.samples.apps.nowinandroid.NiaBuildType

plugins {
    alias(libs.plugins.nowinandroid.android.application)
    alias(libs.plugins.nowinandroid.android.application.compose)
    alias(libs.plugins.nowinandroid.android.application.flavors)
    alias(libs.plugins.nowinandroid.android.application.jacoco)
//    alias(libs.plugins.nowinandroid.android.application.firebase)
    alias(libs.plugins.nowinandroid.kotlin.multiplatform.koin)
    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutLibraries)
}

android {
    defaultConfig {
        applicationId = "com.google.samples.apps.nowinandroid"
        versionCode = 8
        versionName = "0.1.2" // X.Y.Z; X = Major, Y = minor, Z = Patch level

        // Custom test runner to set up Hilt dependency graph
        testInstrumentationRunner = "com.google.samples.apps.nowinandroid.core.testing.KoinTestRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = NiaBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            applicationIdSuffix = NiaBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.named("debug").get()
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "com.google.samples.apps.nowinandroid"
}

dependencies {
    implementation(projects.niaShared)

    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.analytics)
    implementation(projects.sync.work)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.androidx.startup)
    implementation(libs.koin.androidx.workmanager)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.metrics)

    debugImplementation(libs.androidx.compose.ui.testManifest)
    debugImplementation(projects.uiTestHiltManifest)

    testImplementation(projects.core.dataTest)
    testImplementation(libs.koin.test)
    testImplementation(projects.sync.syncTest)
    testImplementation(libs.kotlin.test)

    testDemoImplementation(libs.androidx.navigation.testing)
    testDemoImplementation(libs.robolectric)
    testDemoImplementation(libs.roborazzi)
    testDemoImplementation(projects.core.screenshotTesting)
    testDemoImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.dataTest)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.koin.android.test)
    androidTestImplementation(libs.kotlin.test)

    baselineProfile(projects.benchmarks)
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false

    // Make use of Dex Layout Optimizations via Startup Profiles
    dexLayoutOptimization = true
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}
