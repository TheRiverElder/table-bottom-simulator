package io.github.theriverelder.minigames.tablebottomsimulator.builtin.channel

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.Channel
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.readFromAutosave
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.writeToAutosave
import kotlinx.serialization.json.*

class FullUpdateChannel(simulator: TableBottomSimulatorServer) : Channel("full_update", simulator) {
    override fun receive(data: JsonObject, sender: User) {
        val commandAction = data.forceGet("action").jsonPrimitive.content
        val commandData = data["data"]?.run { this as? JsonObject }


        when (commandAction) {
            "read_from_autosave" -> {
                simulator.readFromAutosave()
                sendFullUpdate()
                simulator.channelGamePlayer.sendUsersAndGamers()
            }
            "write_to_autosave" -> simulator.writeToAutosave()
        }
    }

    fun sendFullUpdate(receiver: User? = null) {
        send(buildJsonObject {
//            put("gamers", buildJsonArray { simulator.gamers.values.forEach { add(it.save()) } })
            put("users", buildJsonArray { simulator.users.values.forEach { add(it.save()) } })
            put("gameObjects", buildJsonArray { simulator.gameObjects.values.forEach { add(it.save()) } })
        }, if (receiver == null) emptyList() else listOf(receiver))
    }
}