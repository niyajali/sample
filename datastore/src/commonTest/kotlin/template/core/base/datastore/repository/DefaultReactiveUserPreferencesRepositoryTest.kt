package template.core.base.datastore.repository

import app.cash.turbine.test
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import template.core.base.datastore.ReactiveUserPreferencesDataStore
import template.core.base.datastore.cache.LRUCacheManager
import template.core.base.datastore.contracts.DataStoreChange
import template.core.base.datastore.flow.DefaultValueObserver
import template.core.base.datastore.notification.DefaultChangeNotifier
import template.core.base.datastore.serialization.JsonSerializationStrategy
import template.core.base.datastore.strategy.BooleanTypeHandler
import template.core.base.datastore.strategy.IntTypeHandler
import template.core.base.datastore.strategy.StringTypeHandler
import template.core.base.datastore.strategy.TypeHandler
import template.core.base.datastore.validation.DefaultDataStoreValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.yield
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineScheduler

@ExperimentalCoroutinesApi
class DefaultReactiveUserPreferencesRepositoryTest {

    private val scheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(scheduler)
    private val changeNotifier = DefaultChangeNotifier()

    private val reactiveDataStore = ReactiveUserPreferencesDataStore(
        settings = MapSettings(),
        dispatcher = testDispatcher,
        typeHandlers = listOf(
            IntTypeHandler(),
            StringTypeHandler(),
            BooleanTypeHandler()
        ) as List<TypeHandler<Any>>,
        serializationStrategy = JsonSerializationStrategy(),
        validator = DefaultDataStoreValidator(),
        cacheManager = LRUCacheManager(),
        changeNotifier = changeNotifier,
        valueObserver = DefaultValueObserver(changeNotifier)
    )

    private val repository = DefaultReactiveUserPreferencesRepository(reactiveDataStore)

    @Serializable
    data class AppSettings(
        val theme: String,
        val language: String,
        val notifications: Boolean
    )

    @Test
    fun observePreference_ReactsToChanges() = runTest(testDispatcher) {
        repository.observePreference("theme", "light").test {
            // Initial value
            assertEquals("light", awaitItem())

            // Save new preference
            repository.savePreference("theme", "dark")
            advanceUntilIdle()
            assertEquals("dark", awaitItem())

            // Save another value
            repository.savePreference("theme", "auto")
            advanceUntilIdle()
            assertEquals("auto", awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeSerializablePreference_ReactsToComplexObjects() = runTest(testDispatcher) {
        val defaultSettings = AppSettings("light", "en", true)

        repository.observeSerializablePreference(
            "app_settings",
            defaultSettings,
            AppSettings.serializer()
        ).test {
            // Initial default
            assertEquals(defaultSettings, awaitItem())

            // Update settings
            val newSettings = AppSettings("dark", "es", false)
            repository.saveSerializablePreference(
                "app_settings",
                newSettings,
                AppSettings.serializer()
            )
            advanceUntilIdle()
            assertEquals(newSettings, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observePreferenceChanges_FiltersCorrectly() = runTest(testDispatcher) {
        repository.observePreferenceChanges("specific_key").test {
            // No initial emission expected for this flow
            // Save to different key - should not emit
            repository.savePreference("other_key", "value")
            advanceUntilIdle()

            // Save to specific key - should emit
            repository.savePreference("specific_key", "value")
            advanceUntilIdle()
            val change = awaitItem()
            assertTrue(change is DataStoreChange.ValueAdded)
            assertEquals("specific_key", change.key)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeAllKeys_ReactsToKeyChanges() = runTest(testDispatcher) {
        repository.observeAllKeys().test {
            // Initial empty set
            assertEquals(emptySet(), awaitItem())

            // Add preferences
            repository.savePreference("key1", "value1")
            advanceUntilIdle()
            assertEquals(setOf("key1"), awaitItem())

            repository.savePreference("key2", "value2")
            advanceUntilIdle()
            assertEquals(setOf("key1", "key2"), awaitItem())

            // Remove preference
            repository.removePreference("key1")
            advanceUntilIdle()
            assertEquals(setOf("key2"), awaitItem())

            // Clear all
            repository.clearAllPreferences()
            advanceUntilIdle()
            assertEquals(emptySet(), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observePreferenceCount_ReactsToSizeChanges() = runTest(testDispatcher) {
        repository.observePreferenceCount().test {
            // Initial count
            assertEquals(0, awaitItem())

            // Add preferences
            repository.savePreference("key1", "value1")
            advanceUntilIdle()
            assertEquals(1, awaitItem())

            repository.savePreference("key2", "value2")
            advanceUntilIdle()
            assertEquals(2, awaitItem())

            // Clear all
            repository.clearAllPreferences()
            advanceUntilIdle()
            assertEquals(0, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}