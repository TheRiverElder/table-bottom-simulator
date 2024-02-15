package io.github.theriverelder.minigames.tablebottomsimulator.builtin.channel

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.*
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

class FullUpdateChannel(simulator: TableBottomSimulatorServer) : Channel("full_update", simulator) {
    override fun receive(data: JsonObject, sender: User) {
        val commandAction = data.forceGet("action").jsonPrimitive.content
        val commandData = data["data"]?.run { this as? JsonObject }


        when (commandAction) {
            "read_from_file" -> {
                val fileName = getRawFilePath(commandData)
                if (fileName != null) simulator.readFromFile(File("simulator_data/${fileName}.json"))
                else simulator.readFromAutosave()

                sendFullUpdate()
                simulator.channelGamePlayer.sendUsersAndGamers()
            }

            "write_to_file" -> {
                val fileName = getRawFilePath(commandData)
                if (fileName != null) simulator.writeToFile(File("simulator_data/${fileName}.json"))
                else simulator.writeToAutosave()
            }

            "read_from_autosave" -> {
                simulator.readFromAutosave()

                sendFullUpdate()
                simulator.channelGamePlayer.sendUsersAndGamers()
            }

            "write_to_autosave" -> {
                simulator.writeToAutosave()
            }
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

fun getRawFilePath(commandData: JsonObject?): String? {
    val raw = commandData?.get("name")?.jsonPrimitive?.content?.replace(Regex("[<>:\"/\\\\|?*]"), "_")
    return if (raw.isNullOrBlank()) null else raw
}