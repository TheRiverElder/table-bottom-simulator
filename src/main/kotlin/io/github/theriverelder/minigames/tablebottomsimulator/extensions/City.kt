package io.github.theriverelder.minigames.tablebottomsimulator.extensions

import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObjectTag

class City(
    val name: String,
    val factoryTypes: List<String>,
) {
}

var GameObject.city: City
    get() {
        val name = tags["birmingham:city_name"]!!.getString()
        val factoryTypes = tags["birmingham:city_factory_types"]!!.values.map { it.toString() }
        return City(name, factoryTypes)
    }
    set(value) {
        tags.add(GameObjectTag("birmingham:city_name", arrayListOf(value.name)))
        tags.add(GameObjectTag("birmingham:city_factory_types", value.factoryTypes.toMutableList()))
    }