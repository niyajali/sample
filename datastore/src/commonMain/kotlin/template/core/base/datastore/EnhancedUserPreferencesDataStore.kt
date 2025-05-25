package template.core.base.datastore

import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import template.core.base.datastore.cache.CacheManager
import template.core.base.datastore.contracts.CacheableDataStore
import template.core.base.datastore.serialization.SerializationStrategy
import template.core.base.datastore.strategy.TypeHandler
import template.core.base.datastore.validation.DataStoreValidator

class EnhancedUserPreferencesDataStore(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher,
    private val typeHandlers: List<TypeHandler<Any>>,
    private val serializationStrategy: SerializationStrategy,
    private val validator: DataStoreValidator,
    private val cacheManager: CacheManager<String, Any>,
) : CacheableDataStore {

    override suspend fun <T> putValue(key: String, value: T): Result<Unit> = withContext(dispatcher) {
        runCatching {
            validator.validateKey(key).getOrThrow()
            validator.validateValue(value).getOrThrow()

            val handler = findTypeHandler(value)
                ?: throw UnsupportedOperationException("No handler found for type: ${value?.let { it::class } ?: "null"}")

            @Suppress("UNCHECKED_CAST")
            val typedHandler = handler as TypeHandler<T>
            val result = typedHandler.put(settings, key, value)

            if (result.isSuccess) {
                cacheManager.put(key, value as Any)
            }

            result.getOrThrow()
        }
    }

    override suspend fun <T> getValue(key: String, default: T): Result<T> = withContext(dispatcher) {
        runCatching {
            validator.validateKey(key).getOrThrow()

            // Check cache first
            @Suppress("UNCHECKED_CAST")
            cacheManager.get(key)?.let {
                return@runCatching it as T
            }

            val handler = findTypeHandler(default)
                ?: throw UnsupportedOperationException("No handler found for type: ${default?.let { it::class } ?: "null"}")

            @Suppress("UNCHECKED_CAST")
            val typedHandler = handler as TypeHandler<T>
            val result = typedHandler.get(settings, key, default)

            if (result.isSuccess) {
                cacheManager.put(key, result.getOrThrow() as Any)
            }

            result.getOrThrow()
        }
    }

    override suspend fun <T> putSerializableValue(
        key: String,
        value: T,
        serializer: KSerializer<T>,
    ): Result<Unit> = withContext(dispatcher) {
        runCatching {
            validator.validateKey(key).getOrThrow()
            validator.validateValue(value).getOrThrow()

            val serializedResult = serializationStrategy.serialize(value, serializer)
            serializedResult.fold(
                onSuccess = { serializedData ->
                    val result = runCatching { settings.putString(key, serializedData) }
                    if (result.isSuccess) {
                        cacheManager.put(key, value as Any)
                    }
                    result.getOrThrow()
                },
                onFailure = { throw it },
            )
        }
    }

    override suspend fun <T> getSerializableValue(
        key: String,
        default: T,
        serializer: KSerializer<T>,
    ): Result<T> = withContext(dispatcher) {
        runCatching {
            validator.validateKey(key).getOrThrow()

            // Check cache first
            @Suppress("UNCHECKED_CAST")
            cacheManager.get(key)?.let {
                return@runCatching it as T
            }

            if (!settings.hasKey(key)) {
                cacheManager.put(key, default as Any)
                return@runCatching default
            }

            val serializedData = runCatching { settings.getString(key, "") }
                .getOrElse { throw it }

            if (serializedData.isEmpty()) {
                return@runCatching default
            }

            serializationStrategy.deserialize(serializedData, serializer).fold(
                onSuccess = { value ->
                    cacheManager.put(key, value as Any)
                    value
                },
                onFailure = {
                    default // Return default on deserialization failure
                },
            )
        }
    }

    override suspend fun hasKey(key: String): Result<Boolean> = withContext(dispatcher) {
        runCatching { settings.hasKey(key) || cacheManager.containsKey(key) }
    }

    override suspend fun removeValue(key: String): Result<Unit> = withContext(dispatcher) {
        runCatching {
            settings.remove(key)
            cacheManager.remove(key)
            Unit
        }
    }

    override suspend fun clearAll(): Result<Unit> = withContext(dispatcher) {
        runCatching {
            settings.clear()
            cacheManager.clear()
            Unit
        }
    }

    override suspend fun getAllKeys(): Result<Set<String>> = withContext(dispatcher) {
        runCatching { settings.keys }
    }

    override suspend fun getSize(): Result<Int> = withContext(dispatcher) {
        runCatching { settings.size }
    }

    override suspend fun invalidateCache(key: String): Result<Unit> = withContext(dispatcher) {
        runCatching {
            cacheManager.remove(key)
            Unit
        }
    }

    override suspend fun invalidateAllCache(): Result<Unit> = withContext(dispatcher) {
        runCatching {
            cacheManager.clear()
            Unit
        }
    }

    override fun getCacheSize(): Int = cacheManager.size()

    private fun findTypeHandler(value: Any?): TypeHandler<Any>? {
        return typeHandlers.find { it.canHandle(value) }
    }
}