package template.core.base.datastore.contracts

import kotlinx.coroutines.flow.Flow

/**
 * Base interface for data storage operations.
 * @param T The type of data to be stored
 */
interface DataStore {
    suspend fun hasKey(key: String): Boolean
    suspend fun removeValue(key: String): Result<Unit>
    suspend fun clearAll(): Result<Unit>
    suspend fun getAllKeys(): Set<String>
    suspend fun getSize(): Int
}