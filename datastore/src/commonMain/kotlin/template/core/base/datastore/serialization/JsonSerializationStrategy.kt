package template.core.base.datastore.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class JsonSerializationStrategy(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
) : SerializationStrategy {

    override suspend fun <T> serialize(value: T, serializer: KSerializer<T>): Result<String> {
        return runCatching { json.encodeToString(serializer, value) }
    }

    override suspend fun <T> deserialize(data: String, serializer: KSerializer<T>): Result<T> {
        return runCatching { json.decodeFromString(serializer, data) }
    }
}