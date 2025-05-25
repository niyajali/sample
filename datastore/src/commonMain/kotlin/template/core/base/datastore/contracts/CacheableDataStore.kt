package template.core.base.datastore.contracts

import kotlinx.coroutines.flow.Flow

/**
 * Interface for data storage with caching capabilities.
 */
interface CacheableDataStore : DataStore {
    suspend fun <T> putValue(key: String, value: T): Result<Unit>
    suspend fun <T> getValue(key: String, default: T): Result<T>
    suspend fun <T> putSerializableValue(key: String, value: T, serializer: kotlinx.serialization.KSerializer<T>): Result<Unit>
    suspend fun <T> getSerializableValue(key: String, default: T, serializer: kotlinx.serialization.KSerializer<T>): Result<T>
    suspend fun invalidateCache(key: String): Result<Unit>
    suspend fun invalidateAllCache(): Result<Unit>
    fun getCacheSize(): Int
}