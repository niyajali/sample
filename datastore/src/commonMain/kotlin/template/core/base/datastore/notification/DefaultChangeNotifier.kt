package template.core.base.datastore.notification

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import template.core.base.datastore.contracts.DataStoreChange

class DefaultChangeNotifier(
    private val capacity: Int = Channel.UNLIMITED,
) : ChangeNotifier {

    private val changeChannel = Channel<DataStoreChange>(capacity)

    override fun notifyChange(change: DataStoreChange) {
        println("[ChangeNotifier] notifyChange: $change")
        val result = changeChannel.trySend(change)
        println("[ChangeNotifier] trySend result: $result")
    }

    override fun observeChanges(): Flow<DataStoreChange> {
        return changeChannel.receiveAsFlow()
    }

    override fun observeKeyChanges(key: String): Flow<DataStoreChange> {
        return observeChanges().filter { it.key == key || it.key == "*" }
    }

    override fun clear() {
        changeChannel.close()
    }
}