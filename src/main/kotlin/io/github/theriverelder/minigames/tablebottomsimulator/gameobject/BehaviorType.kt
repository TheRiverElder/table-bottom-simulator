package io.github.theriverelder.minigames.tablebottomsimulator.gameobject

class BehaviorType<T : Behavior<T>>(
    val name: String,
    val side: Side,
    val creator: (BehaviorType<T>, GameObject, Int) -> T
) {

    fun create(host: GameObject, uid: Int) = creator(this, host, uid)
}