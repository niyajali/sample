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

package com.google.samples.apps.nowinandroid.sync.di

import com.google.samples.apps.nowinandroid.core.data.Synchronizer
import com.google.samples.apps.nowinandroid.core.data.di.dataModule
import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import com.google.samples.apps.nowinandroid.core.network.NiaDispatchers
import com.google.samples.apps.nowinandroid.core.network.asQualifier
import com.google.samples.apps.nowinandroid.core.network.di.coroutineScopesModule
import com.google.samples.apps.nowinandroid.sync.status.StubSyncSubscriber
import com.google.samples.apps.nowinandroid.sync.status.SyncNetworkManager
import com.google.samples.apps.nowinandroid.sync.status.SyncSubscriber
import com.google.samples.apps.nowinandroid.sync.workers.SyncNetworkData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

actual val syncModule: Module
    get() = module {
        includes(coroutineScopesModule, dataModule)
        single<CoroutineDispatcher>{ get(NiaDispatchers.IO.asQualifier) }
        single<CoroutineScope>{ get(named("ApplicationScope")) }

        singleOf(::SyncNetworkData) bind Synchronizer::class
        singleOf(::SyncNetworkManager) bind SyncManager::class
        singleOf(::StubSyncSubscriber) bind SyncSubscriber::class
    }