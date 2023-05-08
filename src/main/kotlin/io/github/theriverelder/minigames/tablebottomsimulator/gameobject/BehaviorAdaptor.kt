package io.github.theriverelder.minigames.tablebottomsimulator.gameobject

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder

open class BehaviorAdaptor<T : Behavior<T>>(type: BehaviorType<T>, host: GameObject, uid: Int) : Behavior<T>(type, host, uid) {
    override fun JsonObjectBuilder.saveData() { }

    override fun restoreData(data: JsonObject) { }

    override fun onInitialize() { }

    override fun onDestroy() { }
}