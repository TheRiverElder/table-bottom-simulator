package io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior

import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorAdaptor
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorType
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.Side
import kotlinx.serialization.json.*

class ControllerBehavior(type: BehaviorType<ControllerBehavior>, host: GameObject, uid: Int) : BehaviorAdaptor<ControllerBehavior>(type, host, uid) {

    var draggable: Boolean = true

    override fun JsonObjectBuilder.saveData() {
        put("draggable", JsonPrimitive(draggable))
    }

    override fun restoreData(data: JsonObject) {
        this.draggable = data["draggable"]?.jsonPrimitive?.booleanOrNull ?: false
    }

    companion object {
        val TYPE = BehaviorType<ControllerBehavior>("controller", Side.BOTH) { type, host, uid -> ControllerBehavior(type, host, uid) }
    }
}