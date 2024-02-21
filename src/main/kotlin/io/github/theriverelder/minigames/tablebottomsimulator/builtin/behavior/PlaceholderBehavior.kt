package io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior

import io.github.theriverelder.minigames.lib.math.AABBArea
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.BehaviorAdaptor
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.BehaviorType
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.Side

class PlaceholderBehavior(type: BehaviorType<PlaceholderBehavior>, host: GameObject, uid: Int) :
    BehaviorAdaptor<PlaceholderBehavior>(type, host, uid) {

    companion object {
        val TYPE = BehaviorType("placeholder", Side.BOTH, ::PlaceholderBehavior)
    }

    val area: AABBArea
        get() = AABBArea(host.position - (host.size / 2), host.size)

    val holdingGameObjects: List<GameObject>
        get() = simulator.gameObjects.values.filter(this::checkHolding)

    val holdingGameObject: GameObject?
        get() = simulator.gameObjects.values.firstOrNull(this::checkHolding)

    private fun checkHolding(gameObject: GameObject): Boolean = gameObject.uid != uid &&
            gameObject.position in area &&
            gameObject.getBehaviorByType(TYPE) == null &&
            gameObject.tags.containsKey("tbs:ignored_by_placeholder")
}