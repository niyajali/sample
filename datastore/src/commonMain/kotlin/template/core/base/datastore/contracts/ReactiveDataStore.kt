package template.core.base.datastore.contracts

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

interface ReactiveDataStore : CacheableDataStore {
    fun <T> observeValue(key: String, default: T): Flow<T>
    fun <T> observeSerializableValue(
        key: String,
        default: T,
        serializer: KSerializer<T>,
    ): Flow<T>

    fun observeKeys(): Flow<Set<String>>
    fun observeSize(): Flow<Int>
    fun observeChanges(): Flow<DataStoreChange>
}