package template.core.base.datastore.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.serialization.KSerializer
import template.core.base.datastore.contracts.DataStoreChange
import template.core.base.datastore.contracts.ReactiveDataStore

class DefaultReactiveUserPreferencesRepository(
    private val reactiveDataStore: ReactiveDataStore,
) : ReactiveUserPreferencesRepository {

    // Delegate base operations
    override suspend fun <T> savePreference(key: String, value: T): Result<Unit> {
        println("[Repository] savePreference: key=$key, value=$value")
        return reactiveDataStore.putValue(key, value)
    }

    override suspend fun <T> getPreference(key: String, default: T): Result<T> {
        return reactiveDataStore.getValue(key, default)
    }

    override suspend fun <T> saveSerializablePreference(
        key: String,
        value: T,
        serializer: KSerializer<T>,
    ): Result<Unit> {
        return reactiveDataStore.putSerializableValue(key, value, serializer)
    }

    override suspend fun <T> getSerializablePreference(
        key: String,
        default: T,
        serializer: KSerializer<T>,
    ): Result<T> {
        return reactiveDataStore.getSerializableValue(key, default, serializer)
    }

    override suspend fun removePreference(key: String): Result<Unit> {
        println("[Repository] removePreference: key=$key")
        return reactiveDataStore.removeValue(key)
    }

    override suspend fun clearAllPreferences(): Result<Unit> {
        println("[Repository] clearAllPreferences")
        return reactiveDataStore.clearAll()
    }

    override suspend fun hasPreference(key: String): Boolean {
        return reactiveDataStore.hasKey(key)
    }

    // Reactive operations
    override fun <T> observePreference(key: String, default: T): Flow<T> {
        return reactiveDataStore.observeValue(key, default)
    }

    override fun <T> observeSerializablePreference(
        key: String,
        default: T,
        serializer: KSerializer<T>,
    ): Flow<T> {
        return reactiveDataStore.observeSerializableValue(key, default, serializer)
    }

    override fun observeAllKeys(): Flow<Set<String>> {
        println("[Repository] observeAllKeys: flow created")
        return reactiveDataStore.observeKeys().also { println("[Repository] observeAllKeys: flow returned") }
    }

    override fun observePreferenceCount(): Flow<Int> {
        return reactiveDataStore.observeSize()
    }

    override fun observePreferenceChanges(): Flow<DataStoreChange> {
        return reactiveDataStore.observeChanges()
    }

    override fun observePreferenceChanges(key: String): Flow<DataStoreChange> {
        return reactiveDataStore.observeChanges()
            .filter { it.key == key || it.key == "*" }
    }
}