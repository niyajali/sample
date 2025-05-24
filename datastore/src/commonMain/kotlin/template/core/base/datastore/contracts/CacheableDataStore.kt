package template.core.base.datastore.contracts

import kotlinx.coroutines.flow.Flow

/**
 * Interface for data storage with caching capabilities.
 */
interface CacheableDataStore : TypedDataStore {
    suspend fun invalidateCache(key: String): Result<Unit>
    suspend fun invalidateAllCache(): Result<Unit>
    fun getCacheSize(): Int
}