package io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior

import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorAdaptor
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorType
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.Side
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import kotlinx.serialization.json.*

class ControllerBehavior(type: BehaviorType<ControllerBehavior>, host: GameObject, uid: Int) : BehaviorAdaptor<ControllerBehavior>(type, host, uid) {

    var draggable: Boolean = true
    var controller: User? = null

    override fun JsonObjectBuilder.saveData() {
        put("draggable", JsonPrimitive(draggable))
        put("controller", JsonPrimitive(controller?.uid))
    }

    override fun restoreData(data: JsonObject) {
        this.draggable = data["draggable"]?.jsonPrimitive?.booleanOrNull ?: false
        val controllerUid = data["controller"]?.jsonPrimitive?.intOrNull
        if (controllerUid != null) {
            val controller = simulator.users[controllerUid]
            if (controller != null) {
                this.controller = controller
            }
        }
    }

    companion object {
        val TYPE = BehaviorType<ControllerBehavior>("controller", Side.BOTH) { type, host, uid -> ControllerBehavior(type, host, uid) }
    }
}