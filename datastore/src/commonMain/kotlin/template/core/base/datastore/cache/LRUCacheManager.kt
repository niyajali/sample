package template.core.base.datastore.cache


/**
 * LRU Cache implementation with configurable size.
 */
class LRUCacheManager<K, V>(
    private val maxSize: Int = 100,
) : CacheManager<K, V> {

    private val cache = LinkedHashMap<K, V>(maxSize, 0.75f)

    override fun put(key: K, value: V) {
        cache[key] = value
        if (cache.size > maxSize) {
            val eldest = cache.keys.first()
            cache.remove(eldest)
        }
    }

    override fun get(key: K): V? = cache[key]

    override fun remove(key: K): V? = cache.remove(key)

    override fun clear() = cache.clear()

    override fun size(): Int = cache.size

    override fun containsKey(key: K): Boolean = cache.containsKey(key)
}