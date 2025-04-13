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

package com.google.samples.apps.nowinandroid.shared.di

import android.app.Activity
import android.util.Log
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.JankStats.OnFrameListener
import com.google.samples.apps.nowinandroid.shared.util.ProfileVerifierLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal actual val jankStatsModule: Module
    get() = module {
        single<CoroutineScope> {
            CoroutineScope(SupervisorJob() + Dispatchers.Default)
        }
        singleOf(::ProfileVerifierLogger)
        factory { (activity: Activity) ->
            JankStats.createAndTrack(
                activity.window,
                providesOnFrameListener(),
            )
        }
    }


fun providesOnFrameListener(): OnFrameListener = OnFrameListener { frameData ->
    // Make sure to only log janky frames.
    if (frameData.isJank) {
        // We're currently logging this but would better report it to a backend.
        Log.v("NiA Jank", frameData.toString())
    }
}
