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
plugins {
    alias(libs.plugins.nowinandroid.kotlin.multiplatform.library)
    alias(libs.plugins.nowinandroid.android.library.compose)
    alias(libs.plugins.nowinandroid.android.library.jacoco)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose)
}

android {
    namespace = "com.google.samples.apps.nowinandroid.core.ui"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.analytics)
            api(projects.core.designsystem)
            api(projects.core.model)

            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.coil)
            implementation(libs.coil.compose)

            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            api(libs.androidx.metrics)
            implementation(libs.androidx.browser)
        }

        androidInstrumentedTest.dependencies {
            implementation(projects.core.testing)
            implementation(libs.bundles.androidx.compose.ui.test)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "ui.generated.resources"
}