package io.github.theriverelder.minigames.tablebottomsimulator.channel

import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

class FullUpdateChannel(name: String, simulator: TableBottomSimulatorServer) : Channel(name, simulator) {
    override fun receive(data: JsonObject, sender: User) { }

    suspend fun sendFullUpdate(receiver: User) {
        send(buildJsonObject {
//            put("gamers", buildJsonArray { simulator.gamers.values.forEach { add(it.save()) } })
            put("users", buildJsonArray { simulator.users.values.forEach { add(it.save()) } })
            put("gameObjects", buildJsonArray { simulator.gameObjects.values.forEach { add(it.save()) } })
        }, receiver)
    }
}