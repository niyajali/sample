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

package com.google.samples.apps.nowinandroid.sync.di

import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.google.samples.apps.nowinandroid.core.data.di.dataModule
import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import com.google.samples.apps.nowinandroid.sync.status.FirebaseSyncSubscriber
import com.google.samples.apps.nowinandroid.sync.status.SyncSubscriber
import com.google.samples.apps.nowinandroid.sync.status.WorkManagerSyncManager
import com.google.samples.apps.nowinandroid.sync.workers.SyncWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val syncKoinModule: Module = module {
    includes(dataModule)

    singleOf(::WorkManagerSyncManager) bind SyncManager::class
    singleOf(::FirebaseSyncSubscriber) bind SyncSubscriber::class
    single<FirebaseMessaging> { Firebase.messaging }
    workerOf(::SyncWorker)
}
