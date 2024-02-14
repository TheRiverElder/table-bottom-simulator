package io.github.theriverelder.minigames.lib.management

class AutoIncrementObservableRegistry<V>(getKey: (V) -> Int) : ObservableRegistry<Int, V>(getKey) {

    var idCounter = 1

    fun nextId() = idCounter++

    fun addRaw(create: (Int) -> V): V {
        var id = nextId()
        while (containsKey(id)) {
            id = nextId()
        }
        val value = create(id)
        add(value)
        return value
    }
}