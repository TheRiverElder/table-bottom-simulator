package io.github.theriverelder.minigames.lib.management

open class ObservableRegistry<K, V>(getKey: (V) -> K) : Registry<K, V>(getKey) {

    val onAdd = ListenerManager<V>()
    val onRemove = ListenerManager<V>()

    override fun add(value: V): Boolean {
        val result = super.add(value)
        if (result) {
            onAdd.emit(value)
        }
        return result
    }

    override fun remove(value: V): Boolean {
        val result = super.remove(value)
        if (result) {
            onRemove.emit(value)
        }
        return result
    }

    override fun removeByKey(key: K): V? {
        val result = super.removeByKey(key)
        if (result != null) {
            onRemove.emit(result)
        }
        return result
    }
}