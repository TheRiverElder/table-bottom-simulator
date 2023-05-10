package io.github.theriverelder.minigames.tablebottomsimulator

import io.github.theriverelder.minigames.lib.management.ObservableRegistry
import io.github.theriverelder.minigames.lib.management.Registry
import io.github.theriverelder.minigames.tablebottomsimulator.channel.Channel
import io.github.theriverelder.minigames.tablebottomsimulator.channel.FullUpdateChannel
import io.github.theriverelder.minigames.tablebottomsimulator.channel.IncrementalUpdateChannel
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.Behavior
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorType
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.user.Gamer
import io.github.theriverelder.minigames.tablebottomsimulator.user.User
import java.util.concurrent.atomic.AtomicInteger

class TableBottomSimulatorServer {

    val uidGenerator = AtomicInteger(1)

    val behaviorTypes = Registry(BehaviorType<*>::name)

//    val gamers = Registry(Gamer::name)
    val users = Registry(User::uid)
    val gameObjects = ObservableRegistry(GameObject::uid)
    val channels = Registry(Channel::name)
    var communication: Communication? = null

    val channelFullUpdateChannel = FullUpdateChannel("full_update", this)
    val channelIncrementalUpdate = IncrementalUpdateChannel("incremental_update", this)

    init {
        channels.add(channelFullUpdateChannel)
        channels.add(channelIncrementalUpdate)
    }

    fun createGameObject(uid: Int = genUid()) = GameObject(this, uid)

    fun createAndAddGameObject(uid: Int = genUid()): GameObject {
        val gameObject = GameObject(this, uid)
        gameObjects += gameObject
        return gameObject
    }

}

fun TableBottomSimulatorServer.genUid() = uidGenerator.getAndIncrement()