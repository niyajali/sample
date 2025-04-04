/*
 * Copyright 2023 The Android Open Source Project
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
plugins {
    alias(libs.plugins.nowinandroid.kotlin.multiplatform.library)
    alias(libs.plugins.nowinandroid.kotlin.multiplatform.koin)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose)
}

android {
    namespace = "com.google.samples.apps.nowinandroid.core.notifications"
}

kotlin {
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            export(libs.kmp.notifier)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.core.model)
            implementation(projects.core.common)
            implementation(libs.koin.core)
            implementation(compose.runtime)
            implementation(compose.components.resources)
            implementation(libs.kmp.notifier)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
        }
    }
}
