package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

import io.github.theriverelder.minigames.tablebottomsimulator.builtin.behavior.PlaceholderBehavior
import io.github.theriverelder.minigames.tablebottomsimulator.extensions.BirminghamExtension
import io.github.theriverelder.minigames.tablebottomsimulator.simulator.gameobject.GameObject


fun getHoldingObjects(extension: BirminghamExtension, placeholderObjectUid: Int): List<GameObject> {
    return extension.simulator.gameObjects[placeholderObjectUid]?.getBehaviorByType(PlaceholderBehavior.TYPE)?.holdingGameObjects ?: emptyList()
}

fun getHoldingFactory(extension: BirminghamExtension, placeholderObjectUid: Int): Factory? {
    for (holdingObject in getHoldingObjects(extension, placeholderObjectUid)) {
        val factory = holdingObject.factory
        if (factory != null) return factory
    }
    return null
}

fun getHoldingWay(extension: BirminghamExtension, placeholderObjectUid: Int): Way? {
    for (holdingObject in getHoldingObjects(extension, placeholderObjectUid)) {
        val way = holdingObject.way
        if (way != null) return way
    }
    return null
}