package io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject

import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.TableBottomSimulatorServer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

abstract class  Behavior<T : Behavior<T>>(
    val type: BehaviorType<T>,
    val host: GameObject,
    val uid: Int,
) : Persistable {

    val simulator: TableBottomSimulatorServer
        get() = host.simulator


    fun remove() {
        host.behaviors.remove(this)
        this.onDestroy()
    }

    override fun save(): JsonObject = buildJsonObject {
        put("type", JsonPrimitive(type.name))
        put("uid", JsonPrimitive(uid))
    }

    // 对客户端发送指令
    fun sendInstruction(data: JsonObject, receivers: Collection<User>? = null) =
        simulator.channelBehaviorInstruction.sendInstruction(this, data, receivers)

    // 从客户端接收指令
    abstract fun receiveInstruction(data: JsonObject, sender: User)

    fun sendUpdate() = simulator.channelGameObject.sendUpdateBehavior(this)

    override fun restore(data: JsonObject) { }

    // 当第一次被加入到GameObject后调用
    abstract fun onInitialize()
    // 当被移除时调用
    abstract fun onDestroy()

}