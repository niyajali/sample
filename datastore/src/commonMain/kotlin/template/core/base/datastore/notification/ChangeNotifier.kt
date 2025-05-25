package template.core.base.datastore.notification

import kotlinx.coroutines.flow.Flow
import template.core.base.datastore.contracts.DataStoreChange

interface ChangeNotifier {
    fun notifyChange(change: DataStoreChange)
    fun observeChanges(): Flow<DataStoreChange>
    fun observeKeyChanges(key: String): Flow<DataStoreChange>
    fun clear()
}