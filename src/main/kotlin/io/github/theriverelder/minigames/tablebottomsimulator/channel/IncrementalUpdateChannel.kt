package io.github.theriverelder.minigames.tablebottomsimulator.channel

import io.github.theriverelder.minigames.tablebottomsimulator.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

class IncrementalUpdateChannel(name: String, simulator: TableBottomSimulatorServer) : Channel(name, simulator) {
    override fun onReceive(data: JsonObject, sender: User) { }

    suspend fun sendUpdate(obj: Persistable, receiver: User) {
        send(obj.save(), receiver)
    }

    fun broadcastGameObjectUpdate(obj: GameObject) {
        runBlocking {
            broadcast(buildJsonObject {
                put("gameObjects", buildJsonArray {
                    add(obj.save())
                })
            })
        }
    }

    fun broadcastGameObjectUpdateByUid(uidSet: Set<Int>) {
        runBlocking {
            broadcast(buildJsonObject {
                put("gameObjects", buildJsonArray {
                    for (it in uidSet) {
                        val data = simulator.gameObjects[it]?.save() ?: continue
                        add(data)
                    }
                })
            })
        }
    }
}