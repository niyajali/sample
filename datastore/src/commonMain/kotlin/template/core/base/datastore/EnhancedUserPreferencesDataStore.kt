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

    override suspend fun <T> putValue(key: String, value: T): Result<Unit> {
        return withContext(dispatcher) {
            validator.validateKey(key).getOrElse { return@withContext Result.failure(it) }
            validator.validateValue(value).getOrElse { return@withContext Result.failure(it) }

            val handler = findTypeHandler(value)
                ?: return@withContext Result.failure(
                    UnsupportedOperationException("No handler found for type: ${value!!::class}"),
                )

            @Suppress("UNCHECKED_CAST")
            val typedHandler = handler as TypeHandler<T>
            val result = typedHandler.put(settings, key, value)

            if (result.isSuccess) {
                cacheManager.put(key, value as Any)
            }

            result
        }
    }

    override suspend fun <T> getValue(key: String, default: T): Result<T> {
        return withContext(dispatcher) {
            validator.validateKey(key).getOrElse { return@withContext Result.failure(it) }

            // Check cache first
            @Suppress("UNCHECKED_CAST")
            cacheManager.get(key)?.let {
                return@withContext Result.success(it as T)
            }

            val handler = findTypeHandler(default)
                ?: return@withContext Result.failure(
                    UnsupportedOperationException("No handler found for type: ${default!!::class}"),
                )

            @Suppress("UNCHECKED_CAST")
            val typedHandler = handler as TypeHandler<T>
            val result = typedHandler.get(settings, key, default)

            if (result.isSuccess) {
                cacheManager.put(key, result.getOrThrow() as Any)
            }

            result
        }
    }

    override suspend fun <T> putSerializableValue(
        key: String,
        value: T,
        serializer: KSerializer<T>,
    ): Result<Unit> {
        return withContext(dispatcher) {
            validator.validateKey(key).getOrElse { return@withContext Result.failure(it) }
            validator.validateValue(value).getOrElse { return@withContext Result.failure(it) }

            val serializedResult = serializationStrategy.serialize(value, serializer)
            serializedResult.fold(
                onSuccess = { serializedData ->
                    val result = runCatching { settings.putString(key, serializedData) }
                    if (result.isSuccess) {
                        cacheManager.put(key, value as Any)
                    }
                    result
                },
                onFailure = { Result.failure(it) },
            )
        }
    }

    override suspend fun <T> getSerializableValue(
        key: String,
        default: T,
        serializer: KSerializer<T>,
    ): Result<T> {
        return withContext(dispatcher) {
            validator.validateKey(key).getOrElse { return@withContext Result.failure(it) }

            // Check cache first
            @Suppress("UNCHECKED_CAST")
            cacheManager.get(key)?.let {
                return@withContext Result.success(it as T)
            }

            if (!settings.hasKey(key)) {
                cacheManager.put(key, default as Any)
                return@withContext Result.success(default)
            }

            val serializedData = runCatching { settings.getString(key, "") }
                .getOrElse { return@withContext Result.failure(it) }

            if (serializedData.isEmpty()) {
                return@withContext Result.success(default)
            }

            serializationStrategy.deserialize(serializedData, serializer).fold(
                onSuccess = { value ->
                    cacheManager.put(key, value as Any)
                    Result.success(value)
                },
                onFailure = {
                    Result.success(default) // Return default on deserialization failure
                },
            )
        }
    }

    override suspend fun hasKey(key: String): Boolean {
        return settings.hasKey(key) || cacheManager.containsKey(key)
    }

    override suspend fun removeValue(key: String): Result<Unit> {
        return withContext(dispatcher) {
            runCatching {
                settings.remove(key)
                cacheManager.remove(key)
            }

            Result.success(Unit)
        }
    }

    override suspend fun clearAll(): Result<Unit> {
        return withContext(dispatcher) {
            runCatching {
                settings.clear()
                cacheManager.clear()
            }
        }
    }

    override suspend fun getAllKeys(): Set<String> {
        return settings.keys
    }

    override suspend fun getSize(): Int {
        return settings.size
    }

    override suspend fun invalidateCache(key: String): Result<Unit> {
        return runCatching {
            cacheManager.remove(key)
            Unit
        }
    }

    override suspend fun invalidateAllCache(): Result<Unit> {
        return runCatching {
            cacheManager.clear()
        }
    }

    override fun getCacheSize(): Int = cacheManager.size()

    private fun findTypeHandler(value: Any?): TypeHandler<Any>? {
        return typeHandlers.find { it.canHandle(value) }
    }
}