package io.github.theriverelder.minigames.tablebottomsimulator.channel

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.Channel
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.sendCommand
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

class GamePlayerChannel(simulator: TableBottomSimulatorServer) : Channel("game_player", simulator) {

    override fun receive(data: JsonObject, sender: User) {
        val commandAction = data.forceGet("action").jsonPrimitive.content
        val commandData = data.forceGet("data").run { this as? JsonObject }


        when (commandAction) {
            "request_users" -> sendUsers(sender)
            "request_gamers" -> sendGamers(sender)
            "occupy_gamer" -> {
                if (commandData == null) throw Exception("Command data must be not null")
                val gamerUid = commandData.forceGet("gamerUid").jsonPrimitive.int
                if (gamerUid < 0) {
                    sender.gamer?.user = null
                } else {
                    simulator.gamers[gamerUid]?.user = sender
                }
                simulator.users.values.forEach(this::sendUsersAndGamers)
            }
        }
    }

    fun sendGamers(receiver: User? = null) {
        sendCommand(
            receiver,
            "update_gamers",
            buildJsonArray { simulator.gamers.values.forEach { add(it.extractData(receiver)) } })
    }

    fun sendUsers(receiver: User? = null) {
        sendCommand(
            receiver,
            "update_users",
            buildJsonArray { simulator.users.values.forEach { add(it.save()) } })
    }

    fun sendUsersAndGamers(receiver: User? = null) {
        sendUsers(receiver)
        sendGamers(receiver)
    }

}