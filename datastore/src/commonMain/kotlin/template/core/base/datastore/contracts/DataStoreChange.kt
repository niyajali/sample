package template.core.base.datastore.contracts

import kotlinx.datetime.Clock.System

sealed class DataStoreChange {
    abstract val key: String
    abstract val timestamp: Long

    data class ValueAdded(
        override val key: String,
        val value: Any?,
        override val timestamp: Long = System.now().toEpochMilliseconds(),
    ) : DataStoreChange()

    data class ValueUpdated(
        override val key: String,
        val oldValue: Any?,
        val newValue: Any?,
        override val timestamp: Long = System.now().toEpochMilliseconds(),
    ) : DataStoreChange()

    data class ValueRemoved(
        override val key: String,
        val oldValue: Any?,
        override val timestamp: Long = System.now().toEpochMilliseconds(),
    ) : DataStoreChange()

    data class StoreCleared(
        override val key: String = "*",
        override val timestamp: Long = System.now().toEpochMilliseconds(),
    ) : DataStoreChange()
}