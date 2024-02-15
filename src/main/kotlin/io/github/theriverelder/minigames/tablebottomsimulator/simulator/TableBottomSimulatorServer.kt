package io.github.theriverelder.minigames.tablebottomsimulator.simulator

import io.github.theriverelder.minigames.lib.management.AutoIncrementObservableRegistry
import io.github.theriverelder.minigames.lib.management.ObservableRegistry
import io.github.theriverelder.minigames.lib.management.Registry
import io.github.theriverelder.minigames.lib.util.forceGet
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.*
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.channel.*
import io.github.theriverelder.minigames.tablebottomsimulator.channel.*
import io.github.theriverelder.minigames.tablebottomsimulator.communication.Communication
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.BehaviorType
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.restoreGameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.Gamer
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.User
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.user.restoreGamer
import io.github.theriverelder.minigames.tablebottomsimulator.util.Persistable
import io.github.theriverelder.minigames.tablebottomsimulator.util.save
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import java.io.*

class TableBottomSimulatorServer : Persistable {

    val behaviorTypes = Registry(BehaviorType<*>::name)

    val users = ObservableRegistry(User::uid)
    val gamers = AutoIncrementObservableRegistry(Gamer::uid)
    val gameObjects = AutoIncrementObservableRegistry(GameObject::uid)
    val channels = Registry(Channel::name)
    var communication: Communication? = null
    var extensions = Registry(Extension::name)

    val channelFullUpdateChannel = FullUpdateChannel(this)
    val channelGameObject = GameObjectChannel(this)
    val channelGamePlayer = GamePlayerChannel(this)
    val channelCard = CardChannel(this)
    val channelBehaviorInstruction = BehaviorInstructionChannel(this)
    val channelEdit = EditChannel(this)

    init {

        behaviorTypes.add(ControllerBehavior.TYPE)
        behaviorTypes.add(PileBehavior.TYPE)
        behaviorTypes.add(CardBehavior.TYPE)
        behaviorTypes.add(PlaceholderBehavior.TYPE)

        channels.add(channelFullUpdateChannel)
        channels.add(channelGameObject)
        channels.add(channelGamePlayer)
        channels.add(channelCard)
        channels.add(channelBehaviorInstruction)
        channels.add(channelEdit)

        gameObjects.onAdd.add { channelGameObject.sendUpdateGameObjectFull(it) }
        gameObjects.onRemove.add { channelGameObject.sendRemoveGameObject(it) }

        users.onAdd.add { user ->
            val gamer = gamers.values.find { it.userUid == user.uid }
            if (gamer != null) {
                user.gamerUid = gamer.uid
            }
            channelGamePlayer.sendUsers()
        }
        users.onRemove.add { user ->
            gamers.values.forEach { if (it.userUid == user.uid) it.userUid = null }
            channelGamePlayer.sendUsersAndGamers()
        }

    }

    // 作为新的Simulator时调用
    fun initialize() {
        extensions.values.forEach { it.initialize() }
    }


    fun createAndAddGameObject(uid: Int? = null): GameObject {
        if (uid != null) {
            val gameObject = GameObject(this, uid)
            gameObjects += gameObject
            return gameObject
        }
        return gameObjects.addRaw { GameObject(this, it) }
    }

    fun addExtension(extension: Extension) {
        this.extensions.add(extension)
    }

    override fun save(): JsonObject = buildJsonObject {
        put("gameObjects", gameObjects.values.save())
        put("gamers", gamers.values.save())
        put("extensions", buildJsonArray {
            extensions.values.forEach { ext ->
                add(buildJsonObject {
                    put("name", ext.name)
                    put("data", ext.save())
                })
            }
        })
    }

    override fun restore(data: JsonObject) {
        gameObjects.clear()
        gamers.clear()

        data.forceGet("gameObjects").jsonArray
            .map { restoreGameObject(it.jsonObject, this) }
            .forEach { gameObjects.add(it) }

        data.forceGet("gamers").jsonArray
            .map { restoreGamer(it.jsonObject, this) }
            .forEach { gamers.add(it) }

        data.forceGet("extensions").jsonArray.forEach {
            val extensionName = it.jsonObject.forceGet("name").jsonPrimitive.content
            val extensionData = it.jsonObject.forceGet("data").jsonObject
            val extension = extensions[extensionName] ?: return@forEach
            extension.restore(extensionData)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun write(stream: OutputStream) {
        val data: JsonObject = this.save()
        Json.encodeToStream(data, stream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun read(stream: InputStream) {
        val data = Json.decodeFromStream<JsonObject>(stream)
        restore(data)
    }
}

fun TableBottomSimulatorServer.readFromFile(file: File): Boolean {
    println("载入存档：开始 $file")
    if (!file.exists() || !file.isFile) return false
    return try {
        FileInputStream(file).use { read(it) }
        println("载入存档：失败")
        true
    } catch (ex: Exception) {
        ex.printStackTrace()
        println("载入存档：失败")
        false
    }
}

fun TableBottomSimulatorServer.writeToFile(file: File): Boolean {
    println("写入存档：开始 $file")
    val parent = file.parentFile
    if (if (parent.exists()) !parent.isDirectory else !parent.mkdirs()) return false
    return try {
        FileOutputStream(file).use { write(it) }
        println("写入存档：成功")
        true
    } catch (ex: Exception) {
        ex.printStackTrace()
        println("写入存档：失败")
        false
    }
}

val AUTOSAVE_FILE = File("./simulator_data/autosave.json")

fun TableBottomSimulatorServer.readFromAutosave(): Boolean = readFromFile(AUTOSAVE_FILE)

fun TableBottomSimulatorServer.writeToAutosave(): Boolean = writeToFile(AUTOSAVE_FILE)