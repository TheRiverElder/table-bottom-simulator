package io.github.theriverelder.minigames.lib.math

interface Area {
    operator fun contains(point: Vector2): Boolean
}