package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObjectTag

class Network(
    val cityNames: List<String>,
    val periods: List<Int>,
    val placeholderObjectUid: Int,
)

var GameObject.network: Network
    get() {
        val tag = tags["birmingham:network"]
        val periodsString = tag?.getString(0) ?: ""
        val periods = buildList(2) {
            if (periodsString.contains('c')) add(1)
            if (periodsString.contains('r')) add(2)
        }
        val cityNames = tag?.values?.drop(1)?.mapNotNull { it as? String }?.filter { it.isNotBlank() } ?: emptyList()
        return Network(cityNames, periods, uid)
    }
    set(value) {
        tags.add(GameObjectTag("birmingham:network", (value.periods + value.cityNames).toMutableList()))
    }