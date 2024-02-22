package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObjectTag

class Way(
    val period: Int,
    val ownerGameUid: Int,
) {
}

var GameObject.way: Way
    get() {
        val tag = tags["birmingham:way"]!!
        return Way(
            tag.getInt(0),
            tag.getInt(1),
        )
    }
    set(value) {
        tags.add(GameObjectTag("birmingham:way", mutableListOf(value.period, value.ownerGameUid)))
    }