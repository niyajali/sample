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

package com.google.samples.apps.nowinandroid.sync.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import org.koin.android.ext.android.inject

private const val SYNC_TOPIC_SENDER = "/topics/sync"

internal class SyncNotificationsService : FirebaseMessagingService() {

    private val syncManager: SyncManager by inject()

    override fun onMessageReceived(message: RemoteMessage) {
        if (SYNC_TOPIC_SENDER == message.from) {
            syncManager.requestSync()
        }
    }
}
