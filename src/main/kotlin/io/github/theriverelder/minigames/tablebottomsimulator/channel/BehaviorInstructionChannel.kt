package io.github.theriverelder.minigames.tablebottomsimulator.channel

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.Behavior
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import kotlinx.serialization.json.*

class BehaviorInstructionChannel(name: String, simulator: TableBottomSimulatorServer) : Channel(name, simulator) {

    override fun receive(data: JsonObject, sender: User) {
        val hostUid = data.forceGet("hostUid").jsonPrimitive.int
        val behaviorUid = data.forceGet("behaviorUid").jsonPrimitive.int
        val instruction = data.forceGet("instruction").jsonObject

        val behavior = simulator.gameObjects[hostUid]?.behaviors?.get(behaviorUid) ?: return

        behavior.receiveInstruction(instruction, sender)
        simulator.channelIncrementalUpdate.sendUpdateBehavior(behavior)
    }

    fun sendInstruction(behavior: Behavior<*>, instruction: JsonObject, receivers: Collection<User>? = null) {
        val pack = buildJsonObject {
            put("hostUid", JsonPrimitive(behavior.host.uid))
            put("behaviorUid", JsonPrimitive(behavior.uid))
            put("instruction", instruction)
        }

        (receivers ?: simulator.users.values).forEach { send(pack, it) }
    }
}