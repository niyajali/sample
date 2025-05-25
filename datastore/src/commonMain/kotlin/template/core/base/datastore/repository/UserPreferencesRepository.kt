package template.core.base.datastore.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

/**
 * Interface for managing user preferences.
 */
interface UserPreferencesRepository {
    suspend fun <T> savePreference(key: String, value: T): Result<Unit>
    suspend fun <T> getPreference(key: String, default: T): Result<T>
    suspend fun <T> saveSerializablePreference(
        key: String,
        value: T,
        serializer: KSerializer<T>,
    ): Result<Unit>

    suspend fun <T> getSerializablePreference(
        key: String,
        default: T,
        serializer: KSerializer<T>,
    ): Result<T>

    suspend fun removePreference(key: String): Result<Unit>
    suspend fun clearAllPreferences(): Result<Unit>
    suspend fun hasPreference(key: String): Boolean
}