package template.core.base.datastore.performance

import app.cash.turbine.test
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import template.core.base.datastore.ReactiveUserPreferencesDataStore
import template.core.base.datastore.cache.LRUCacheManager
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
import kotlin.time.measureTime
import kotlinx.coroutines.yield

@ExperimentalCoroutinesApi
class ReactivePerformanceTest {

    private val testDispatcher = StandardTestDispatcher()
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
        cacheManager = LRUCacheManager(maxSize = 1000),
        changeNotifier = changeNotifier,
        valueObserver = DefaultValueObserver(changeNotifier)
    )

    @Test
    fun rapidUpdates_HandledEfficiently() = runTest(testDispatcher) {
        val updateCount = 100

        reactiveDataStore.observeValue("counter", 0).test {
            // Initial value
            assertEquals(0, awaitItem())

            // Give time for initial emission
            kotlinx.coroutines.delay(10)

            val duration = measureTime {
                repeat(updateCount) { i ->
                    reactiveDataStore.putValue("counter", i + 1)
                    advanceUntilIdle()
                }
                yield()
                advanceUntilIdle()
            }

            // Should receive all updates in order
            val received = mutableListOf<Int>()
            repeat(updateCount) {
                received.add(awaitItem())
            }
            assertEquals((1..updateCount).toList(), received)

            // Verify performance is reasonable (this is a rough check)
            assertTrue(duration.inWholeMilliseconds < 5000, "Updates took too long: \\${duration.inWholeMilliseconds}ms")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun multipleObservers_ShareNotifications() = runTest(testDispatcher) {
        val observer1 = reactiveDataStore.observeValue("shared_key", "default")
        val observer2 = reactiveDataStore.observeValue("shared_key", "default")

        observer1.test {
            observer2.test {
                // Both should get initial value
                assertEquals("default", awaitItem())
                assertEquals("default", awaitItem())

                // Give time for initial emission
                kotlinx.coroutines.delay(10)

                // Update the value
                reactiveDataStore.putValue("shared_key", "updated")
                advanceUntilIdle()

                // Both should get updated value
                assertEquals("updated", awaitItem())
                assertEquals("updated", awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}