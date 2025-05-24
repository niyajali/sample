package template.core.base.datastore.flow

import kotlinx.coroutines.flow.Flow

interface ValueObserver {
    fun <T> createValueFlow(
        key: String,
        default: T,
        getter: suspend () -> Result<T>
    ): Flow<T>

    fun <T> createDistinctValueFlow(
        key: String,
        default: T,
        getter: suspend () -> Result<T>
    ): Flow<T>
}