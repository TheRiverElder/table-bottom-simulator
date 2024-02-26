package io.github.theriverelder.minigames.lib.math

// 轴心（position）为几何中心
class RectangleArea(
    val position: Vector2,
    val size: Vector2,
    val rotation: Double = 0.0,
) : Area {

    override operator fun contains(point: Vector2): Boolean {
        val relativePoint = point - position
        val modulo = relativePoint.modulo
        val angle = relativePoint.angle
        val tp = Vector2.fromPolar(angle - rotation, modulo)
        val halfSize = size / 2
        return (tp.x >= -halfSize.x && tp.x < halfSize.x && tp.y >= -halfSize.y && tp.y < halfSize.y)
    }
}