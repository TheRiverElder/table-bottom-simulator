package io.github.theriverelder.minigames.lib.math

class AABBArea(
    val position: Vector2,
    val size: Vector2,
) {

    operator fun contains(point: Vector2): Boolean =
        (point.x >= position.x && point.x < position.x + size.x && point.y >= position.y && point.y < position.y + size.y)
}