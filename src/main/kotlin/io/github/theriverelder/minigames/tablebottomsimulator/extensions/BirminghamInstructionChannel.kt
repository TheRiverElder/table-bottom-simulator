package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.Channel
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import kotlinx.serialization.json.*

class BirminghamInstructionChannel(val extension: BirminghamExtension) :
    Channel("birmingham_instruction", extension.simulator) {

    init {
        extension.listenerGameCreated.add { game ->
            println("Game created")
            game.listenerGameStateUpdated.add {
                println("Game state updated")
                simulator.users.values.forEach(this::sendUpdateGameState)
            }
            game.listenerActionOptionsUpdated.add { gamer ->
                println("Action options updated: ${gamer.ordinal} ${gamer.gamer?.user?.name ?: gamer.gamer?.uid}")
                gamer.user?.run { sendDisplayActionOptions(this) }
            }

            simulator.users.values.forEach(this::sendUpdateGameState)
        }
    }

    val game: BirminghamGame get() = extension.birminghamGame ?: throw Exception("BirminghamGame not created yet")

    override fun receive(data: JsonObject, sender: User) {
        val commandAction = data.forceGet("action").jsonPrimitive.content
        val commandData = data.forceGet("data").run { this as? JsonObject }


        when (commandAction) {
            "request_action_options" -> sendDisplayActionOptions(sender)
            "request_game_state" -> sendUpdateGameState(sender)
            "choose_action_options" -> {
                val optionIndex = commandData?.get("optionIndex")?.jsonPrimitive?.int ?: -1
                extension.birminghamGame?.getGamerByUserUid(sender.uid)?.actionGuide?.choose(optionIndex)
            }

            "create_game" -> {
                val gamerAmount = commandData?.get("gamerAmount")?.jsonPrimitive?.int ?: 2
                extension.createGame(gamerAmount)
            }
        }
    }

    fun sendCommand(receiver: User, action: String, data: JsonElement = JsonNull) = send(buildJsonObject {
        put("action", action)
        put("data", data)
    }, receiver)

    fun sendDisplayActionOptions(receiver: User) {
        val birminghamGamer = extension.birminghamGame?.getGamerByUserUid(receiver.uid) ?: return
//        if (birminghamGamer == null) throw Exception("Gamer not found")
        val options = birminghamGamer.actionGuide?.options

        val commandData =
            if (options == null) JsonNull
            else buildJsonObject {
                put("text", options.text)
                put("options", buildJsonArray {
                    options.options.forEach { option ->
                        add(buildJsonObject {
                            put("text", option.text)
                            put("value", JsonNull)
                        })
                    }
                })
            }

        sendCommand(receiver, "display_action_options", commandData)
    }

    fun sendUpdateGameState(receiver: User) {
        val birminghamGame = extension.birminghamGame
//        if (birminghamGamer == null) throw Exception("Gamer not found")
//        val birminghamGamer = birminghamGame?.getGamerByUserUid(user.uid)

        val commandData = birminghamGame?.save() ?: JsonNull

        sendCommand(receiver, "update_game_state", commandData)
    }

}