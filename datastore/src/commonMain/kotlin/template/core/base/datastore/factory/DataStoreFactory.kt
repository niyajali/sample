package template.core.base.datastore.factory

import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import template.core.base.datastore.EnhancedUserPreferencesDataStore
import template.core.base.datastore.cache.LRUCacheManager
import template.core.base.datastore.contracts.CacheableDataStore
import template.core.base.datastore.serialization.JsonSerializationStrategy
import template.core.base.datastore.strategy.BooleanTypeHandler
import template.core.base.datastore.strategy.DoubleTypeHandler
import template.core.base.datastore.strategy.FloatTypeHandler
import template.core.base.datastore.strategy.IntTypeHandler
import template.core.base.datastore.strategy.LongTypeHandler
import template.core.base.datastore.strategy.StringTypeHandler
import template.core.base.datastore.strategy.TypeHandler
import template.core.base.datastore.validation.DefaultDataStoreValidator

object DataStoreFactory {

    fun createCacheableDataStore(
        settings: Settings,
        dispatcher: CoroutineDispatcher,
        cacheSize: Int = 100
    ): CacheableDataStore {
        return EnhancedUserPreferencesDataStore(
            settings = settings,
            dispatcher = dispatcher,
            typeHandlers = listOf(
                IntTypeHandler(),
                StringTypeHandler(),
                BooleanTypeHandler(),
                LongTypeHandler(),
                FloatTypeHandler(),
                DoubleTypeHandler()
            ) as List<TypeHandler<Any>>,
            serializationStrategy = JsonSerializationStrategy(),
            validator = DefaultDataStoreValidator(),
            cacheManager = LRUCacheManager(maxSize = cacheSize)
        )
    }
}