package template.core.base.datastore

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class UserPreferencesDataStoreTest {
    private val testDispatcher = StandardTestDispatcher()
    private val settings = MapSettings()
    private val dataStore = UserPreferencesDataStore(settings, testDispatcher)

    @Serializable
    data class CustomData(val id: Int, val name: String)

    @Test
    fun putAndGet_PrimitiveTypes() = runTest(testDispatcher) {
        dataStore.putValue("int", 42)
        assertEquals(42, dataStore.getValue("int", 0))

        dataStore.putValue("long", 123L)
        assertEquals(123L, dataStore.getValue("long", 0L))

        dataStore.putValue("float", 3.14f)
        assertEquals(3.14f, dataStore.getValue("float", 0f))

        dataStore.putValue("double", 2.71)
        assertEquals(2.71, dataStore.getValue("double", 0.0))

        dataStore.putValue("string", "hello")
        assertEquals("hello", dataStore.getValue("string", ""))

        dataStore.putValue("boolean", true)
        assertEquals(true, dataStore.getValue("boolean", false))
    }

    @Test
    fun putAndGet_CustomType_WithSerializer() = runTest(testDispatcher) {
        val custom = CustomData(1, "test")
        dataStore.putValue("custom", custom, CustomData.serializer())
        assertEquals(custom, dataStore.getValue("custom", CustomData(0, ""), CustomData.serializer()))
    }

    @Test
    fun putValue_ThrowsWithoutSerializer() = runTest(testDispatcher) {
        val custom = CustomData(2, "fail")
        assertFailsWith<IllegalArgumentException> {
            dataStore.putValue("fail", custom)
        }
    }

    @Test
    fun getValue_ThrowsWithoutSerializer() = runTest(testDispatcher) {
        assertFailsWith<IllegalArgumentException> {
            dataStore.getValue("fail", CustomData(0, ""))
        }
    }

    @Test
    fun getValue_ReturnsDefaultIfKeyMissing() = runTest(testDispatcher) {
        assertEquals(99, dataStore.getValue("missing", 99))
    }

    @Test
    fun hasKey_WorksCorrectly() = runTest(testDispatcher) {
        assertTrue(!dataStore.hasKey("nope"))
        dataStore.putValue("exists", 1)
        assertTrue(dataStore.hasKey("exists"))
    }

    @Test
    fun removeValue_RemovesKey() = runTest(testDispatcher) {
        dataStore.putValue("toremove", 5)
        dataStore.removeValue("toremove")
        assertEquals(0, dataStore.getValue("toremove", 0))
    }

    @Test
    fun clearAll_RemovesAllKeys() = runTest(testDispatcher) {
        dataStore.putValue("a", 1)
        dataStore.putValue("b", 2)
        dataStore.clearAll()
        assertEquals(0, dataStore.getValue("a", 0))
        assertEquals(0, dataStore.getValue("b", 0))
    }

    @Test
    fun getAllKeys_ReturnsAllKeys() = runTest(testDispatcher) {
        dataStore.putValue("k1", 1)
        dataStore.putValue("k2", 2)
        assertEquals(setOf("k1", "k2"), dataStore.getAllKeys())
    }

    @Test
    fun getSize_ReturnsCorrectCount() = runTest(testDispatcher) {
        dataStore.putValue("k1", 1)
        dataStore.putValue("k2", 2)
        assertEquals(2, dataStore.getSize())
    }
} 