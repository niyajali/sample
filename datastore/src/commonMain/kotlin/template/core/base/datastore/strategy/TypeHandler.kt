package template.core.base.datastore.strategy

import com.russhwolf.settings.Settings

/**
 * Interface for handling type conversions in data storage.
 * @param T The type to be handled
 */
interface TypeHandler<T> {
    suspend fun put(settings: Settings, key: String, value: T): Result<Unit>
    suspend fun get(settings: Settings, key: String, default: T): Result<T>
    fun canHandle(value: Any?): Boolean
} 