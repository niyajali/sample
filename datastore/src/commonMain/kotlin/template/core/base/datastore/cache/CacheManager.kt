package template.core.base.datastore.cache

/**
 * Interface for managing data caching.
 * @param K The type of the cache key
 * @param V The type of the cached value
 */
interface CacheManager<K, V> {
    fun put(key: K, value: V)
    fun get(key: K): V?
    fun remove(key: K): V?
    fun clear()
    fun size(): Int
    fun containsKey(key: K): Boolean
}