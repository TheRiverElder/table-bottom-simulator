package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.channel.Channel
import io.github.theriverelder.minigames.tablebottomsimulator.save
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import kotlinx.serialization.json.*

class BirminghamInstructionChannel(name: String, simulator: TableBottomSimulatorServer, val game: BirminghamGame) :
    Channel(name, simulator) {

    init {
        game.listenerGameStateUpdated.add { it.users.values.forEach(this::sendDisplayActionOptions) }
    }

    override fun receive(data: JsonObject, sender: User) {
        val commandType = data.forceGet("type").jsonPrimitive.content
        val commandData = data.forceGet("data").run { this as? JsonObject }

        val gameUser = game.users[sender.uid]!!

        when (commandType) {
            "request_action_options" -> sendDisplayActionOptions(gameUser)
            "choose_action_options" -> {
                val index = commandData?.get("index")?.jsonPrimitive?.int ?: -1
                gameUser.actionGuide?.choose(index)
            }
        }
    }

    fun sendDisplayActionOptions(birminghamUser: BirminghamUser) {
        val options = birminghamUser.actionGuide?.options

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

        send(data, birminghamUser.simulatorUser)
    }

}