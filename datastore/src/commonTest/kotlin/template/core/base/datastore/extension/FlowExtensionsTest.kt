package template.core.base.datastore.extension

import app.cash.turbine.test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import template.core.base.datastore.contracts.DataStoreChange
import template.core.base.datastore.extensions.mapWithDefault
import template.core.base.datastore.extensions.onlyAdditions
import template.core.base.datastore.extensions.onlyRemovals
import template.core.base.datastore.extensions.onlyUpdates
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowExtensionsTest {

    @Test
    fun mapWithDefault_HandlesErrors() = runTest {
        val flow = flowOf(1, 2, 3)
            .mapWithDefault("default") {
                if (it == 2) throw RuntimeException("Error")
                "value_$it"
            }

        flow.test {
            assertEquals("value_1", awaitItem())
            assertEquals("default", awaitItem()) // Error case
            awaitComplete()
        }
    }

    @Test
    fun filterChangeType_FiltersCorrectTypes() = runTest {
        val changes = flowOf(
            DataStoreChange.ValueAdded("key1", "value1"),
            DataStoreChange.ValueUpdated("key2", "old", "new"),
            DataStoreChange.ValueAdded("key3", "value3"),
            DataStoreChange.ValueRemoved("key4", "value4")
        )

        changes.onlyAdditions().test {
            val first = awaitItem()
            assertTrue(first is DataStoreChange.ValueAdded)
            assertEquals("key1", first.key)

            val second = awaitItem()
            assertTrue(second is DataStoreChange.ValueAdded)
            assertEquals("key3", second.key)

            awaitComplete()
        }
    }

    @Test
    fun onlyUpdates_FiltersUpdateChanges() = runTest {
        val changes = flowOf(
            DataStoreChange.ValueAdded("key1", "value1"),
            DataStoreChange.ValueUpdated("key2", "old", "new"),
            DataStoreChange.ValueRemoved("key3", "value3")
        )

        changes.onlyUpdates().test {
            val update = awaitItem()
            assertTrue(update is DataStoreChange.ValueUpdated)
            assertEquals("key2", update.key)
            assertEquals("old", update.oldValue)
            assertEquals("new", update.newValue)

            awaitComplete()
        }
    }

    @Test
    fun onlyRemovals_FiltersRemovalChanges() = runTest {
        val changes = flowOf(
            DataStoreChange.ValueAdded("key1", "value1"),
            DataStoreChange.ValueRemoved("key2", "value2"),
            DataStoreChange.ValueUpdated("key3", "old", "new")
        )

        changes.onlyRemovals().test {
            val removal = awaitItem()
            assertTrue(removal is DataStoreChange.ValueRemoved)
            assertEquals("key2", removal.key)
            assertEquals("value2", removal.oldValue)

            awaitComplete()
        }
    }
}