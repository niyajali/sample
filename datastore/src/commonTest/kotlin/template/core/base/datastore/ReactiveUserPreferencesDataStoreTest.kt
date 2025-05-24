package template.core.base.datastore

import app.cash.turbine.test
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import template.core.base.datastore.cache.LRUCacheManager
import template.core.base.datastore.contracts.DataStoreChange
import template.core.base.datastore.flow.DefaultValueObserver
import template.core.base.datastore.notification.DefaultChangeNotifier
import template.core.base.datastore.serialization.JsonSerializationStrategy
import template.core.base.datastore.strategy.BooleanTypeHandler
import template.core.base.datastore.strategy.DoubleTypeHandler
import template.core.base.datastore.strategy.FloatTypeHandler
import template.core.base.datastore.strategy.IntTypeHandler
import template.core.base.datastore.strategy.LongTypeHandler
import template.core.base.datastore.strategy.StringTypeHandler
import template.core.base.datastore.strategy.TypeHandler
import template.core.base.datastore.validation.DefaultDataStoreValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.advanceUntilIdle

@ExperimentalCoroutinesApi
class ReactiveUserPreferencesDataStoreTest {

    private val testDispatcher = StandardTestDispatcher()
    private val settings = MapSettings()
    private val changeNotifier = DefaultChangeNotifier()

    private val reactiveDataStore = ReactiveUserPreferencesDataStore(
        settings = settings,
        dispatcher = testDispatcher,
        typeHandlers = listOf(
            IntTypeHandler(),
            StringTypeHandler(),
            BooleanTypeHandler(),
            LongTypeHandler(),
            FloatTypeHandler(),
            DoubleTypeHandler()
        ) as List<TypeHandler<Any>>,
        serializationStrategy = JsonSerializationStrategy(),
        validator = DefaultDataStoreValidator(),
        cacheManager = LRUCacheManager(maxSize = 10),
        changeNotifier = changeNotifier,
        valueObserver = DefaultValueObserver(changeNotifier)
    )

    @Serializable
    data class TestUser(
        val id: Long,
        val name: String,
        val age: Int
    )

    @Test
    fun observeValue_EmitsInitialValueAndUpdates() = runTest(testDispatcher) {
        // Initially store a value
        reactiveDataStore.putValue("test_key", "initial")

        reactiveDataStore.observeValue("test_key", "default").test {
            // Should emit initial value
            assertEquals("initial", awaitItem())

            // Update the value
            reactiveDataStore.putValue("test_key", "updated")
            assertEquals("updated", awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeValue_EmitsDefaultWhenKeyNotExists() = runTest(testDispatcher) {
        reactiveDataStore.observeValue("non_existent", "default").test {
            assertEquals("default", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeSerializableValue_WorksWithCustomObjects() = runTest(testDispatcher) {
        val defaultUser = TestUser(0, "", 0)
        val testUser = TestUser(1, "John", 25)

        reactiveDataStore.observeSerializableValue(
            "user",
            defaultUser,
            TestUser.serializer()
        ).test {
            // Should emit default initially
            assertEquals(defaultUser, awaitItem())

            // Update with new user
            reactiveDataStore.putSerializableValue("user", testUser, TestUser.serializer())
            assertEquals(testUser, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeChanges_EmitsCorrectChangeTypes() = runTest(testDispatcher) {
        reactiveDataStore.observeChanges().test {
            // Add a value
            reactiveDataStore.putValue("key1", "value1")
            val addChange = awaitItem()
            assertTrue(addChange is DataStoreChange.ValueAdded)
            assertEquals("key1", addChange.key)
            assertEquals("value1", addChange.value)

            // Update the value
            reactiveDataStore.putValue("key1", "value2")
            val updateChange = awaitItem()
            assertTrue(updateChange is DataStoreChange.ValueUpdated)
            assertEquals("key1", updateChange.key)
            assertEquals("value1", updateChange.oldValue)
            assertEquals("value2", updateChange.newValue)

            // Remove the value
            reactiveDataStore.removeValue("key1")
            val removeChange = awaitItem()
            assertTrue(removeChange is DataStoreChange.ValueRemoved)
            assertEquals("key1", removeChange.key)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeKeys_EmitsUpdatedKeySets() = runTest(testDispatcher) {
        reactiveDataStore.observeKeys().test {
            // Initial empty set
            assertEquals(emptySet(), awaitItem())

            // Give time for initial emission
            kotlinx.coroutines.delay(10)

            // Add first key
            reactiveDataStore.putValue("key1", "value1")
            advanceUntilIdle()
            assertEquals(setOf("key1"), awaitItem())

            // Add second key
            reactiveDataStore.putValue("key2", "value2")
            advanceUntilIdle()
            assertEquals(setOf("key1", "key2"), awaitItem())

            // Remove first key
            reactiveDataStore.removeValue("key1")
            advanceUntilIdle()
            assertEquals(setOf("key2"), awaitItem())

            // Remove second key
            reactiveDataStore.removeValue("key2")
            advanceUntilIdle()
            assertEquals(emptySet(), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeSize_EmitsCorrectCounts() = runTest(testDispatcher) {
        reactiveDataStore.observeSize().test {
            // Initial size should be 0
            assertEquals(0, awaitItem())

            // Add items
            reactiveDataStore.putValue("key1", "value1")
            assertEquals(1, awaitItem())

            reactiveDataStore.putValue("key2", "value2")
            assertEquals(2, awaitItem())

            // Clear all
            reactiveDataStore.clearAll()
            assertEquals(0, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeValue_DistinctUntilChanged() = runTest(testDispatcher) {
        reactiveDataStore.observeValue("key", "default").test {
            // Initial emission
            assertEquals("default", awaitItem())

            // Set same value - should not emit
            reactiveDataStore.putValue("key", "default")

            // Set different value - should emit
            reactiveDataStore.putValue("key", "new_value")
            assertEquals("new_value", awaitItem())

            // Set same value again - should not emit
            reactiveDataStore.putValue("key", "new_value")

            // Verify no more emissions
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun clearAll_EmitsClearChange() = runTest(testDispatcher) {
        // Add some data first
        reactiveDataStore.putValue("key1", "value1")
        reactiveDataStore.putValue("key2", "value2")

        reactiveDataStore.observeChanges().test {
            // Skip the initial additions
            skipItems(2)

            // Clear all
            reactiveDataStore.clearAll()

            val clearChange = awaitItem()
            assertTrue(clearChange is DataStoreChange.StoreCleared)

            cancelAndIgnoreRemainingEvents()
        }
    }
}