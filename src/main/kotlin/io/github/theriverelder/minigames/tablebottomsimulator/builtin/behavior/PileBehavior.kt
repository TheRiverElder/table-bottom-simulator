package io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior

import io.github.theriverelder.minigames.lib.management.ListenerManager
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorAdaptor
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.BehaviorType
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.gameobject.Side


class PileBehavior(type: BehaviorType<PileBehavior>, host: GameObject, uid: Int) :
    BehaviorAdaptor<PileBehavior>(type, host, uid) {

    val onPileListeners = ListenerManager<PileEvent>()

    companion object {
        val TYPE = BehaviorType("pile", Side.BOTH, ::PileBehavior)
    }

    // pile不动，把member拖到pile上松手，触发这个事件
    class PileEvent(
        val pile: GameObject,
        val member: GameObject,
    )
}