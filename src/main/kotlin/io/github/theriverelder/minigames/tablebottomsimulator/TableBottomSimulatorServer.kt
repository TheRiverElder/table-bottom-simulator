package io.github.theriverelder.minigames.tablebottomsimulator

import io.github.theriverelder.minigames.lib.management.AutoIncrementObservableRegistry
import io.github.theriverelder.minigames.lib.management.ObservableRegistry
import io.github.theriverelder.minigames.lib.management.Registry
import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.Card
import io.github.theriverelder.minigames.tablebottomsimulator.channel.*
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorType
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.user.Gamer
import io.github.theriverelder.minigames.tablebottomsimulator.user.User

class TableBottomSimulatorServer {

    val behaviorTypes = Registry(BehaviorType<*>::name)

    val users = ObservableRegistry(User::uid)
    val gamers = AutoIncrementObservableRegistry(Gamer::uid)
    val gameObjects = AutoIncrementObservableRegistry(GameObject::uid)
    val channels = Registry(Channel::name)
    var communication: Communication? = null
    var extensions = Registry(Extension::name)

    val channelFullUpdateChannel = FullUpdateChannel("full_update", this)
    val channelGameObject = GameObjectChannel(this)
    val channelGamePlayer = GamePlayerChannel(this)
    val channelCard = CardChannel(this)
    val channelBehaviorInstruction = BehaviorInstructionChannel(this)

    init {
        channels.add(channelFullUpdateChannel)
        channels.add(channelGameObject)
        channels.add(channelGamePlayer)
        channels.add(channelCard)
        channels.add(channelBehaviorInstruction)

        gameObjects.onAdd.add { channelGameObject.sendUpdateGameObjectFull(it) }
        gameObjects.onRemove.add { channelGameObject.sendRemoveGameObject(it) }

        users.onAdd.add { users.values.forEach { channelGamePlayer.sendUsers(it) } }
        users.onRemove.add { user ->
            gamers.values.forEach { if (it.userUid == user.uid) it.userUid = null }
            users.values.forEach { channelGamePlayer.sendUsersAndGamers(it) }
        }

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


}