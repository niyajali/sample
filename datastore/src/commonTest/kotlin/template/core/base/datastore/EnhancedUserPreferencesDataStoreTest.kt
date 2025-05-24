package template.core.base.datastore

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import template.core.base.datastore.cache.LRUCacheManager
import template.core.base.datastore.serialization.JsonSerializationStrategy
import template.core.base.datastore.strategy.BooleanTypeHandler
import template.core.base.datastore.strategy.IntTypeHandler
import template.core.base.datastore.strategy.StringTypeHandler
import template.core.base.datastore.strategy.TypeHandler
import template.core.base.datastore.validation.DefaultDataStoreValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class EnhancedUserPreferencesDataStoreTest {
    private val testDispatcher = StandardTestDispatcher()
    private val settings = MapSettings()
    private val cacheManager = LRUCacheManager<String, Any>(maxSize = 2)
    private val dataStore = EnhancedUserPreferencesDataStore(
        settings = settings,
        dispatcher = testDispatcher,
        typeHandlers = listOf(IntTypeHandler(), StringTypeHandler(), BooleanTypeHandler()) as List<TypeHandler<Any>>,
        serializationStrategy = JsonSerializationStrategy(),
        validator = DefaultDataStoreValidator(),
        cacheManager = cacheManager
    )

    @Serializable
    data class Custom(val id: Int, val name: String)

    @Test
    fun putAndGet_PrimitiveTypes() = runTest(testDispatcher) {
        assertTrue(dataStore.putValue("int", 1).isSuccess)
        assertEquals(1, dataStore.getValue("int", 0).getOrNull())
        assertTrue(dataStore.putValue("bool", true).isSuccess)
        assertEquals(true, dataStore.getValue("bool", false).getOrNull())
    }

    @Test
    fun putAndGet_SerializableType() = runTest(testDispatcher) {
        val custom = Custom(1, "abc")
        assertTrue(dataStore.putSerializableValue("custom", custom, Custom.serializer()).isSuccess)
        assertEquals(custom, dataStore.getSerializableValue("custom", Custom(0, ""), Custom.serializer()).getOrNull())
    }

    @Test
    fun getValue_ReturnsDefaultIfMissingOrCorrupt() = runTest(testDispatcher) {
        assertEquals(99, dataStore.getValue("missing", 99).getOrNull())
    }

    @Test
    fun hasKey_WorksWithCache() = runTest(testDispatcher) {
        assertTrue(!dataStore.hasKey("nope"))
        dataStore.putValue("exists", 1)
        assertTrue(dataStore.hasKey("exists"))
    }

    @Test
    fun removeValue_RemovesFromCacheAndSettings() = runTest(testDispatcher) {
        dataStore.putValue("toremove", 5)
        dataStore.removeValue("toremove")
        assertEquals(0, dataStore.getValue("toremove", 0).getOrNull())
    }

    @Test
    fun clearAll_RemovesEverything() = runTest(testDispatcher) {
        dataStore.putValue("a", 1)
        dataStore.putValue("b", 2)
        dataStore.clearAll()
        assertEquals(0, dataStore.getValue("a", 0).getOrNull())
        assertEquals(0, dataStore.getValue("b", 0).getOrNull())
    }

    @Test
    fun getAllKeysAndSize() = runTest(testDispatcher) {
        dataStore.putValue("k1", 1)
        dataStore.putValue("k2", 2)
        assertEquals(setOf("k1", "k2"), dataStore.getAllKeys())
        assertEquals(2, dataStore.getSize())
    }

    @Test
    fun cacheEviction_WorksAsExpected() = runTest(testDispatcher) {
        dataStore.putValue("k1", 1)
        dataStore.putValue("k2", 2)
        dataStore.putValue("k3", 3) // Should evict k1 if maxSize=2
        assertEquals(2, dataStore.getCacheSize())
        assertTrue(!cacheManager.containsKey("k1"))
    }

    @Test
    fun invalidateCache_RemovesSpecificKey() = runTest(testDispatcher) {
        dataStore.putValue("k1", 1)
        dataStore.invalidateCache("k1")
        assertTrue(!cacheManager.containsKey("k1"))
    }

    @Test
    fun invalidateAllCache_RemovesAll() = runTest(testDispatcher) {
        dataStore.putValue("k1", 1)
        dataStore.putValue("k2", 2)
        dataStore.invalidateAllCache()
        assertEquals(0, dataStore.getCacheSize())
    }

    @Test
    fun putValue_FailsForUnsupportedType() = runTest(testDispatcher) {
        class Unsupported
        val result = dataStore.putValue("bad", Unsupported())
        assertTrue(result.isFailure)
    }
} 