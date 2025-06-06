/*
 * Copyright 2025 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.ui.util

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * Interface defining browser operations that can be performed across platforms
 */
actual object BrowserLauncher {
    /**
     * Launches a URL in the platform-specific browser implementation
     * @param url The URL to open
     * @param toolbarColor Optional color for the browser toolbar (may be ignored on some platforms)
     */
    actual fun openUrl(url: String, toolbarColor: Int?) {
        val nsUrl = NSURL.URLWithString(url)
        nsUrl?.let {
            if (UIApplication.sharedApplication.canOpenURL(nsUrl)) {
                UIApplication.sharedApplication.openURL(nsUrl)
            }
        }
    }
}