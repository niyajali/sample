package template.core.base.datastore

import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import template.core.base.datastore.cache.CacheManager
import template.core.base.datastore.contracts.DataStoreChange
import template.core.base.datastore.contracts.ReactiveDataStore
import template.core.base.datastore.flow.ValueObserver
import template.core.base.datastore.notification.ChangeNotifier
import template.core.base.datastore.serialization.SerializationStrategy
import template.core.base.datastore.strategy.TypeHandler
import template.core.base.datastore.validation.DataStoreValidator

class ReactiveUserPreferencesDataStore(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher,
    private val typeHandlers: List<TypeHandler<Any>>,
    private val serializationStrategy: SerializationStrategy,
    private val validator: DataStoreValidator,
    private val cacheManager: CacheManager<String, Any>,
    private val changeNotifier: ChangeNotifier,
    private val valueObserver: ValueObserver,
) : ReactiveDataStore {

    // Delegate to the base enhanced implementation
    private val enhancedDataStore = EnhancedUserPreferencesDataStore(
        settings, dispatcher, typeHandlers, serializationStrategy, validator, cacheManager,
    )

    override suspend fun <T> putValue(key: String, value: T): Result<Unit> {
        return withContext(dispatcher) {
            val oldValue = if (hasKey(key).getOrDefault(false)) {
                enhancedDataStore.getValue(key, value).getOrNull()
            } else null

            val result = enhancedDataStore.putValue(key, value)

            if (result.isSuccess) {
                val change = if (oldValue != null) {
                    DataStoreChange.ValueUpdated(key, oldValue, value)
                } else {
                    DataStoreChange.ValueAdded(key, value)
                }
                changeNotifier.notifyChange(change)
            }

            result
        }
    }

    override suspend fun <T> getValue(key: String, default: T): Result<T> {
        return enhancedDataStore.getValue(key, default)
    }

    override suspend fun <T> putSerializableValue(
        key: String,
        value: T,
        serializer: KSerializer<T>,
    ): Result<Unit> {
        return withContext(dispatcher) {
            val oldValue = if (hasKey(key).getOrDefault(false)) {
                enhancedDataStore.getSerializableValue(key, value, serializer).getOrNull()
            } else null

            val result = enhancedDataStore.putSerializableValue(key, value, serializer)

            if (result.isSuccess) {
                val change = if (oldValue != null) {
                    DataStoreChange.ValueUpdated(key, oldValue, value)
                } else {
                    DataStoreChange.ValueAdded(key, value)
                }
                changeNotifier.notifyChange(change)
            }

            result
        }
    }

    override suspend fun <T> getSerializableValue(
        key: String,
        default: T,
        serializer: KSerializer<T>,
    ): Result<T> {
        return enhancedDataStore.getSerializableValue(key, default, serializer)
    }

    override suspend fun removeValue(key: String): Result<Unit> {
        return withContext(dispatcher) {
            val oldValue = if (hasKey(key).getOrDefault(false)) {
                runCatching { settings.getString(key, "") }.getOrNull()
            } else null

            val result = enhancedDataStore.removeValue(key)

            if (result.isSuccess) {
                changeNotifier.notifyChange(
                    DataStoreChange.ValueRemoved(key, oldValue),
                )
            }

            result
        }
    }

    override suspend fun clearAll(): Result<Unit> {
        return withContext(dispatcher) {
            val result = enhancedDataStore.clearAll()

            if (result.isSuccess) {
                changeNotifier.notifyChange(DataStoreChange.StoreCleared())
            }

            result
        }
    }

    // Flow-based observation methods
    override fun <T> observeValue(key: String, default: T): Flow<T> {
        return valueObserver.createDistinctValueFlow(key, default) {
            enhancedDataStore.getValue(key, default)
        }
    }

    override fun <T> observeSerializableValue(
        key: String,
        default: T,
        serializer: KSerializer<T>,
    ): Flow<T> {
        return valueObserver.createDistinctValueFlow(key, default) {
            enhancedDataStore.getSerializableValue(key, default, serializer)
        }
    }

    override fun observeKeys(): Flow<Set<String>> {
        return channelFlow {
            send(settings.keys)
            changeNotifier.observeChanges().map { settings.keys }.collect { keys ->
                send(keys)
            }
        }.distinctUntilChanged()
    }

    override fun observeSize(): Flow<Int> {
        return changeNotifier.observeChanges()
            .onStart { emit(DataStoreChange.ValueAdded("", null)) }
            .map { getSize().getOrDefault(0) }
            .distinctUntilChanged()
    }

    override fun observeChanges(): Flow<DataStoreChange> {
        return changeNotifier.observeChanges()
    }

    override suspend fun hasKey(key: String): Result<Boolean> = enhancedDataStore.hasKey(key)
    override suspend fun getAllKeys(): Result<Set<String>> = enhancedDataStore.getAllKeys()
    override suspend fun getSize(): Result<Int> = enhancedDataStore.getSize()
    override suspend fun invalidateCache(key: String): Result<Unit> = enhancedDataStore.invalidateCache(key)
    override suspend fun invalidateAllCache(): Result<Unit> = enhancedDataStore.invalidateAllCache()
    override fun getCacheSize(): Int = enhancedDataStore.getCacheSize()
}
