package io.github.theriverelder.minigames.lib.management

open class Registry<K, V>(
    private val getKey: (V) -> K,
) {

    private val map = HashMap<K, V>()

    operator fun get(key: K): V? = map[key]
    fun getOrThrow(key: K): V = map[key] ?: throw Exception("No such key: $key")
//    operator fun set(key: K, value: V) { map[key] = value }

    open fun add(value: V): Boolean {
        val key: K = getKey(value)
        if (key in map) return false
        map[key] = value
        return true
    }

    open fun remove(value: V): Boolean = map.remove(getKey(value), value)

    open fun removeByKey(key: K): V? = map.remove(key)

    fun clear() = map.clear()

    val keys: Set<K> = map.keys
    val values: Collection<V> = map.values

    operator fun plusAssign(value: V) { add(value) }
    operator fun minusAssign(value: V) { remove(value) }
    operator fun contains(value: V) = map.containsKey(getKey(value))
    fun containsKey(key: K) = map.containsKey(key)


}