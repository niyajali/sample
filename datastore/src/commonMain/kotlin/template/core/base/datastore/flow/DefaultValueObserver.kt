package template.core.base.datastore.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import template.core.base.datastore.notification.ChangeNotifier

class DefaultValueObserver(
    private val changeNotifier: ChangeNotifier,
) : ValueObserver {

    override fun <T> createValueFlow(
        key: String,
        default: T,
        getter: suspend () -> Result<T>,
    ): Flow<T> {
        return flow {
            // Initial emission of the value
            emit(getter().getOrElse { default })
            emitAll(
                changeNotifier.observeKeyChanges(key)
                    .map { getter().getOrElse { default } }
            )
        }
    }

    override fun <T> createDistinctValueFlow(
        key: String,
        default: T,
        getter: suspend () -> Result<T>,
    ): Flow<T> {
        return createValueFlow(key, default, getter).distinctUntilChanged()
    }
}