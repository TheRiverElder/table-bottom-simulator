package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.channel.Channel
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class BirminghamInstructionChannel(name: String, simulator: TableBottomSimulatorServer) : Channel(name, simulator) {

    override fun receive(data: JsonObject, sender: User) {
        val commandType = data.forceGet("type").jsonPrimitive.toString()
        val commandData = data.forceGet("data").jsonObject

        TODO()
    }

}