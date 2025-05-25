package template.core.base.datastore.serialization

import kotlinx.serialization.KSerializer

/**
 * Strategy for handling serialization operations.
 * Follows Single Responsibility Principle.
 */
interface SerializationStrategy {
    suspend fun <T> serialize(value: T, serializer: KSerializer<T>): Result<String>
    suspend fun <T> deserialize(data: String, serializer: KSerializer<T>): Result<T>
}