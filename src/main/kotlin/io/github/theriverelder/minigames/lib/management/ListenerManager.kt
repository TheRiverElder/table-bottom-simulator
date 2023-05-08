package io.github.theriverelder.minigames.lib.management

class ListenerManager<TEvent> {
    private val listeners = HashSet<(TEvent) -> Unit>()

    fun add(listener: (TEvent) -> Unit) {
        listeners.add(listener)
    }

    fun remove(listener: (TEvent) -> Unit) {
        listeners.remove(listener)
    }

    fun emit(event: TEvent) {
        listeners.forEach { it(event) }
    }
}