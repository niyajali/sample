/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package template.core.base.datastore.di

import com.russhwolf.settings.Settings
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mifos.core.common.di.AppDispatchers
import template.core.base.datastore.ReactiveUserPreferencesDataStore
import template.core.base.datastore.cache.CacheManager
import template.core.base.datastore.cache.LRUCacheManager
import template.core.base.datastore.contracts.ReactiveDataStore
import template.core.base.datastore.flow.DefaultValueObserver
import template.core.base.datastore.flow.ValueObserver
import template.core.base.datastore.notification.ChangeNotifier
import template.core.base.datastore.notification.DefaultChangeNotifier
import template.core.base.datastore.operators.PreferenceFlowOperators
import template.core.base.datastore.repository.DefaultReactiveUserPreferencesRepository
import template.core.base.datastore.repository.ReactiveUserPreferencesRepository
import template.core.base.datastore.serialization.JsonSerializationStrategy
import template.core.base.datastore.serialization.SerializationStrategy
import template.core.base.datastore.strategy.BooleanTypeHandler
import template.core.base.datastore.strategy.DoubleTypeHandler
import template.core.base.datastore.strategy.FloatTypeHandler
import template.core.base.datastore.strategy.IntTypeHandler
import template.core.base.datastore.strategy.LongTypeHandler
import template.core.base.datastore.strategy.StringTypeHandler
import template.core.base.datastore.strategy.TypeHandler
import template.core.base.datastore.validation.DataStoreValidator
import template.core.base.datastore.validation.DefaultDataStoreValidator

val CoreDatastoreModule = module {
    single { Settings() }
    single<DataStoreValidator> { DefaultDataStoreValidator() }
    single<SerializationStrategy> { JsonSerializationStrategy() }
    single<CacheManager<String, Any>> { LRUCacheManager(maxSize = 200) }

    // Reactive components
    single<ChangeNotifier> { DefaultChangeNotifier() }
    single<ValueObserver> { DefaultValueObserver(get()) }

    // Type handlers
    single<List<TypeHandler<Any>>> {
        listOf(
            IntTypeHandler(),
            StringTypeHandler(),
            BooleanTypeHandler(),
            LongTypeHandler(),
            FloatTypeHandler(),
            DoubleTypeHandler(),
        ) as List<TypeHandler<Any>>
    }

    // Reactive data store
    single<ReactiveDataStore> {
        ReactiveUserPreferencesDataStore(
            settings = get(),
            dispatcher = get(named(AppDispatchers.IO.name)),
            typeHandlers = get(),
            serializationStrategy = get(),
            validator = get(),
            cacheManager = get(),
            changeNotifier = get(),
            valueObserver = get(),
        )
    }

    // Reactive repository
    single<ReactiveUserPreferencesRepository> {
        DefaultReactiveUserPreferencesRepository(get())
    }

    single<PreferenceFlowOperators> { PreferenceFlowOperators(get()) }
}
