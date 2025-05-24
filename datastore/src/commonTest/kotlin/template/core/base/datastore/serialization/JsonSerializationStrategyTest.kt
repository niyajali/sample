package template.core.base.datastore.serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsonSerializationStrategyTest {
    private val strategy = JsonSerializationStrategy(Json { encodeDefaults = true })

    @Serializable
    data class Data(val id: Int, val name: String)

    @Test
    fun serializeAndDeserialize_Success() = kotlinx.coroutines.test.runTest {
        val data = Data(1, "abc")
        val serialized = strategy.serialize(data, Data.serializer())
        assertTrue(serialized.isSuccess)
        val deserialized = strategy.deserialize(serialized.getOrThrow(), Data.serializer())
        assertTrue(deserialized.isSuccess)
        assertEquals(data, deserialized.getOrThrow())
    }

    @Test
    fun deserialize_FailureOnCorruptData() = kotlinx.coroutines.test.runTest {
        val result = strategy.deserialize("not a json", Data.serializer())
        assertTrue(result.isFailure)
    }
} 