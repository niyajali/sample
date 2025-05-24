package template.core.base.datastore.contracts

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

/**
 * Interface for type-safe data storage operations.
 * @param T The type of data to be stored
 * @param K The type of the key used to identify data
 */
interface TypedDataStore : DataStore {
    suspend fun <T> putValue(key: String, value: T): Result<Unit>
    suspend fun <T> getValue(key: String, default: T): Result<T>
    suspend fun <T> putSerializableValue(
        key: String,
        value: T,
        serializer: KSerializer<T>,
    ): Result<Unit>

    suspend fun <T> getSerializableValue(
        key: String,
        default: T,
        serializer: KSerializer<T>,
    ): Result<T>
}