package io.github.theriverelder.minigames.tablebottomsimulator.channel

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.Channel
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.Behavior
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import kotlinx.serialization.json.*

class BehaviorInstructionChannel(simulator: TableBottomSimulatorServer) : Channel("behavior_instruction", simulator) {

    override fun receive(data: JsonObject, sender: User) {
        val hostUid = data.forceGet("hostUid").jsonPrimitive.int
        val behaviorUid = data.forceGet("behaviorUid").jsonPrimitive.int
        val instruction = data.forceGet("instruction").jsonObject

        val behavior = simulator.gameObjects[hostUid]?.behaviors?.get(behaviorUid) ?: return

        behavior.receiveInstruction(instruction, sender)
        simulator.channelGameObject.sendUpdateBehavior(behavior)
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