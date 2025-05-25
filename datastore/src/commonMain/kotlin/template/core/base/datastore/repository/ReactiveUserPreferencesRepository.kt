package template.core.base.datastore.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import template.core.base.datastore.contracts.DataStoreChange

interface ReactiveUserPreferencesRepository : UserPreferencesRepository {
    fun <T> observePreference(key: String, default: T): Flow<T>
    fun <T> observeSerializablePreference(
        key: String,
        default: T,
        serializer: KSerializer<T>,
    ): Flow<T>

    fun observeAllKeys(): Flow<Set<String>>
    fun observePreferenceCount(): Flow<Int>
    fun observePreferenceChanges(): Flow<DataStoreChange>
    fun observePreferenceChanges(key: String): Flow<DataStoreChange>
}