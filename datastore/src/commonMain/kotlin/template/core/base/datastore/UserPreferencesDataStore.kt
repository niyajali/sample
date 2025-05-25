/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package template.core.base.datastore

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer

/**
 * A data store class for managing user preferences using a settings storage mechanism.
 * This class provides methods to store, retrieve, and manage key-value pairs in a thread-safe manner
 * using coroutines and a specified dispatcher.
 *
 * @property settings The underlying settings storage implementation used to persist preferences.
 * @property dispatcher The [CoroutineDispatcher] used to execute storage operations, ensuring thread safety.
 */
class UserPreferencesDataStore(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher,
) {

    /**
     * Stores a value associated with the specified key in the data store.
     * Supports primitive types directly and custom types via a provided [KSerializer].
     * Returns [Result.success] on success or [Result.failure] on error.
     */
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    suspend fun <T> putValue(
        key: String,
        value: T,
        serializer: KSerializer<T>? = null,
    ): Result<Unit> = withContext(dispatcher) {
        runCatching {
            when (value) {
                is Int -> settings.putInt(key, value)
                is Long -> settings.putLong(key, value)
                is Float -> settings.putFloat(key, value)
                is Double -> settings.putDouble(key, value)
                is String -> settings.putString(key, value)
                is Boolean -> settings.putBoolean(key, value)
                else -> {
                    require(serializer != null) { "Unsupported type or no serializer provided for ${value?.let { it::class } ?: "null"}" }
                    settings.encodeValue(
                        serializer = serializer,
                        value = value,
                        key = key,
                    )
                }
            }
        }
    }

    /**
     * Retrieves a value associated with the specified key from the data store.
     * Returns [Result.success] with the value or [Result.failure] on error.
     */
    @Suppress("UNCHECKED_CAST")
    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    suspend fun <T> getValue(
        key: String,
        default: T,
        serializer: KSerializer<T>? = null,
    ): Result<T> = withContext(dispatcher) {
        runCatching {
            when (default) {
                is Int -> settings.getInt(key, default) as T
                is Long -> settings.getLong(key, default) as T
                is Float -> settings.getFloat(key, default) as T
                is Double -> settings.getDouble(key, default) as T
                is String -> settings.getString(key, default) as T
                is Boolean -> settings.getBoolean(key, default) as T
                else -> {
                    require(serializer != null) { "Unsupported type or no serializer provided for ${default?.let { it::class } ?: "null"}" }
                    settings.decodeValue(
                        serializer = serializer,
                        key = key,
                        defaultValue = default,
                    )
                }
            }
        }
    }

    /**
     * Checks if the specified [key] exists in the data store.
     * Returns [Result.success] with true/false or [Result.failure] on error.
     */
    suspend fun hasKey(key: String): Result<Boolean> = withContext(dispatcher) {
        runCatching { settings.hasKey(key) }
    }

    /**
     * Removes the value associated with the specified [key] from the data store.
     * Returns [Result.success] on success or [Result.failure] on error.
     */
    suspend fun removeValue(key: String): Result<Unit> = withContext(dispatcher) {
        runCatching { settings.remove(key) }
    }

    /**
     * Clears all stored preferences in the data store.
     * Returns [Result.success] on success or [Result.failure] on error.
     */
    suspend fun clearAll(): Result<Unit> = withContext(dispatcher) {
        runCatching { settings.clear() }
    }

    /**
     * Retrieves all keys currently stored in the data store.
     * Returns [Result.success] with the set of keys or [Result.failure] on error.
     */
    suspend fun getAllKeys(): Result<Set<String>> = withContext(dispatcher) {
        runCatching { settings.keys }
    }

    /**
     * Returns the total number of key-value pairs stored in the data store.
     * Returns [Result.success] with the count or [Result.failure] on error.
     */
    suspend fun getSize(): Result<Int> = withContext(dispatcher) {
        runCatching { settings.size }
    }
}
