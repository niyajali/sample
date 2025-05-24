package template.core.base.datastore.operators

import app.cash.turbine.test
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import template.core.base.datastore.ReactiveUserPreferencesDataStore
import template.core.base.datastore.cache.LRUCacheManager
import template.core.base.datastore.flow.DefaultValueObserver
import template.core.base.datastore.notification.DefaultChangeNotifier
import template.core.base.datastore.repository.DefaultReactiveUserPreferencesRepository
import template.core.base.datastore.serialization.JsonSerializationStrategy
import template.core.base.datastore.strategy.BooleanTypeHandler
import template.core.base.datastore.strategy.IntTypeHandler
import template.core.base.datastore.strategy.StringTypeHandler
import template.core.base.datastore.strategy.TypeHandler
import template.core.base.datastore.validation.DefaultDataStoreValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.yield

/**
 * Test suite for [PreferenceFlowOperators].
 * Verifies combining, mapping, and observing preference flows, including edge cases.
 */
@ExperimentalCoroutinesApi
class PreferenceFlowOperatorsTest {

    private val testDispatcher = StandardTestDispatcher()
    private val changeNotifier = DefaultChangeNotifier()

    private val reactiveDataStore = ReactiveUserPreferencesDataStore(
        settings = MapSettings(),
        dispatcher = testDispatcher,
        typeHandlers = listOf(
            IntTypeHandler(),
            StringTypeHandler(),
            BooleanTypeHandler(),
        ) as List<TypeHandler<Any>>,
        serializationStrategy = JsonSerializationStrategy(),
        validator = DefaultDataStoreValidator(),
        cacheManager = LRUCacheManager(),
        changeNotifier = changeNotifier,
        valueObserver = DefaultValueObserver(changeNotifier),
    )

    private val repository = DefaultReactiveUserPreferencesRepository(reactiveDataStore)
    private val operators = PreferenceFlowOperators(repository)

    /**
     * Tests combining two preference flows and verifies correct emission order.
     */
    @Test
    fun combinePreferences_TwoValues_CombinesCorrectly() = runTest(testDispatcher) {
        operators.combinePreferences(
            "key1", "default1",
            "key2", "default2",
        ) { value1, value2 ->
            "$value1-$value2"
        }.test {
            // Initial combined value
            assertEquals("default1-default2", awaitItem())

            // Update first preference
            repository.savePreference("key1", "new1")
            assertEquals("new1-default2", awaitItem())

            // Update second preference
            repository.savePreference("key2", "new2")
            assertEquals("new1-new2", awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Tests combining three preference flows and verifies correct emission order.
     */
    @Test
    fun combinePreferences_ThreeValues_CombinesCorrectly() = runTest(testDispatcher) {
        operators.combinePreferences(
            "theme", "light",
            "language", "en",
            "notifications", true,
        ) { theme, language, notifications ->
            Triple(theme, language, notifications)
        }.test {
            // Initial combined value
            assertEquals(Triple("light", "en", true), awaitItem())

            // Give time for initial emission
            kotlinx.coroutines.delay(10)

            // Update theme
            repository.savePreference("theme", "dark")
            yield()
            advanceUntilIdle()
            assertEquals(Triple("dark", "en", true), awaitItem())

            // Update notifications
            repository.savePreference("notifications", false)
            yield()
            advanceUntilIdle()
            assertEquals(Triple("dark", "en", false), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Tests observing changes to any of the specified keys.
     */
    @Test
    fun observeAnyKeyChange_EmitsOnSpecifiedKeys() = runTest(testDispatcher) {
        operators.observeAnyKeyChange("key1", "key2").test {
            // Change to key1 - should emit
            repository.savePreference("key1", "value1")
            assertEquals("key1", awaitItem())

            // Change to key3 - should not emit (not in watched keys)
            repository.savePreference("key3", "value3")

            // Change to key2 - should emit
            repository.savePreference("key2", "value2")
            assertEquals("key2", awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Tests mapping a preference value using a transform function.
     */
    @Test
    fun observeMappedPreference_TransformsValues() = runTest(testDispatcher) {
        operators.observeMappedPreference("count", 0) { count ->
            "Count is: $count"
        }.test {
            // Initial mapped value
            assertEquals("Count is: 0", awaitItem())

            // Update preference
            repository.savePreference("count", 5)
            assertEquals("Count is: 5", awaitItem())

            // Update again
            repository.savePreference("count", 10)
            assertEquals("Count is: 10", awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Tests rapid updates to preferences and ensures all changes are emitted.
     */
    @Test
    fun rapidUpdates_AreHandledCorrectly() = runTest(testDispatcher) {
        operators.observeMappedPreference("rapid", 0) { it }.test {
            for (i in 1..5) {
                repository.savePreference("rapid", i)
                assertEquals(i, awaitItem())
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Tests combining preferences with default/null values.
     */
    @Test
    fun combinePreferences_DefaultValues() = runTest(testDispatcher) {
        operators.combinePreferences(
            "missing1", "",
            "missing2", ""
        ) { v1, v2 ->
            v1 to v2
        }.test {
            assertEquals("" to "", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}