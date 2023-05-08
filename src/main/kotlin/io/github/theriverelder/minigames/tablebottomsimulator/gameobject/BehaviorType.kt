package io.github.theriverelder.minigames.tablebottomsimulator.gameobject

import io.github.theriverelder.minigames.tablebottomsimulator.genUid

class BehaviorType<T : Behavior<T>>(
    val name: String,
    val side: Side,
    val creator: (BehaviorType<T>, GameObject, Int) -> T
) {

    fun create(host: GameObject, uid: Int = host.simulator.genUid()) = creator(this, host, uid)
}