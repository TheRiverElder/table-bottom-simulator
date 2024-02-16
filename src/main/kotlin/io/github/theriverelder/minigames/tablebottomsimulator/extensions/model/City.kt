package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObjectTag

class City(
    val name: String,
    val index: Int,
    val factoryTypeNames: List<String>,
    val placeholderObjectUid: Int,
)

var GameObject.city: City
    get() {
        val tag = tags["birmingham:city"]!!
        return City(tag.getString(0), tag.getInt(1), tag.values.drop(2).mapNotNull { it as? String }, uid)
    }
    set(value) {
        tags.add(GameObjectTag("birmingham:city", (listOf(value.name, value.index) + value.factoryTypeNames).toMutableList()))
    }