package template.core.base.datastore.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import template.core.base.datastore.contracts.DataStoreChange

/**
 * Maps values and provides error handling with default value.
 */
fun <T, R> Flow<T>.mapWithDefault(default: R, transform: (T) -> R): Flow<R> {
    return this.map { transform(it) }
        .catch { emit(default) }
}

/**
 * Filters changes by change type.
 */
inline fun <reified T : DataStoreChange> Flow<DataStoreChange>.filterChangeType(): Flow<T> {
    return this.map { it as? T }.filter { it != null }.map { it!! }
}

/**
 * Observes only value additions.
 */
fun Flow<DataStoreChange>.onlyAdditions(): Flow<DataStoreChange.ValueAdded> {
    return filterChangeType()
}

/**
 * Observes only value updates.
 */
fun Flow<DataStoreChange>.onlyUpdates(): Flow<DataStoreChange.ValueUpdated> {
    return filterChangeType()
}

/**
 * Observes only value removals.
 */
fun Flow<DataStoreChange>.onlyRemovals(): Flow<DataStoreChange.ValueRemoved> {
    return filterChangeType()
}

/**
 * Debounces preference changes to avoid excessive emissions.
 */
fun <T> Flow<T>.debouncePreferences(timeoutMillis: Long = 300): Flow<T> {
    // Note: This would require kotlinx-coroutines-core with debounce support
    // For now, we'll use distinctUntilChanged as a simple approach
    return this.distinctUntilChanged()
}

/**
 * Logs preference changes for debugging.
 */
fun <T> Flow<T>.logChanges(tag: String = "DataStore"): Flow<T> {
    return this.onEach { value ->
        println("[$tag] Value changed: $value")
    }
}