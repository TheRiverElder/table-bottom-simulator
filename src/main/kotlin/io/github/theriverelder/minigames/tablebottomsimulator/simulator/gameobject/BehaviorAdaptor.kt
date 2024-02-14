package io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject

import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import kotlinx.serialization.json.JsonObject

open class BehaviorAdaptor<T : Behavior<T>>(type: BehaviorType<T>, host: GameObject, uid: Int) : Behavior<T>(type, host, uid) {
    override fun receiveInstruction(data: JsonObject, sender: User) { }

    override fun onInitialize() { }

    override fun onDestroy() { }
}