package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.channel.Channel
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
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
                println("Action options updated: ${gamer.ordinal} ${gamer.userUid}")
                gamer.userOrNull?.run { sendDisplayActionOptions(this) }
            }

            simulator.users.values.forEach(this::sendUpdateGameState)
        }
    }

    val game: BirminghamGame get() = extension.birminghamGame ?: throw Exception("BirminghamGame not created yet")

    override fun receive(data: JsonObject, sender: User) {
        val commandType = data.forceGet("type").jsonPrimitive.content
        val commandData = data.forceGet("data").run { this as? JsonObject }


        when (commandType) {
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

            "occupy_gamer" -> {
                if (commandData == null) throw Exception("Command data must be not null")
                val gamerOrdinal = commandData.forceGet("gamerOrdinal").jsonPrimitive.int
                game.gamerList[gamerOrdinal].user = sender
                sendDisplayActionOptions(sender)
            }
        }
    }

    fun sendDisplayActionOptions(user: User) {
        val birminghamGamer = extension.birminghamGame?.getGamerByUserUid(user.uid) ?: return
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

        val data = buildJsonObject {
            put("type", "display_action_options")
            put("data", commandData)
        }

        send(data, birminghamGamer.user)
    }

    fun sendUpdateGameState(user: User) {
        val birminghamGame = extension.birminghamGame
//        if (birminghamGamer == null) throw Exception("Gamer not found")
        val birminghamGamer = birminghamGame?.getGamerByUserUid(user.uid)

        val commandData =
            if (birminghamGame == null) JsonNull
            else buildJsonObject {
                put("period", birminghamGame.period)
                put("gamerList", buildJsonArray {
                    birminghamGame.gamerList.forEach {
                        add(buildJsonObject {
                            put("ordinal", it.ordinal)
                            put("userUid", it.userUid)
                            put("money", it.money)
                            put("cardAmount", it.cardObjectUidList.size)
                            if (birminghamGamer != null && birminghamGamer.userUid == it.userUid)
                                put("cardObjectUidList", buildJsonArray { it.cardObjectUidList.forEach { add(it) } })
                        })
                    }
                })
            }

        val data = buildJsonObject {
            put("type", "update_game_state")
            put("data", commandData)
        }

        send(data, user)
    }

}