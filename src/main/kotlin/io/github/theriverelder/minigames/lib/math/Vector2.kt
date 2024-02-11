package io.github.theriverelder.minigames.lib.math

import kotlin.math.cos
import kotlin.math.sin

class Vector2(
    val x: Double,
    val y: Double,
) {

    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

    operator fun unaryPlus() = Vector2(x, y)
    operator fun unaryMinus() = Vector2(-x, -y)
    operator fun plus(other: Vector2) = Vector2(x + other.x, y + other.y)
    operator fun minus(other: Vector2) = Vector2(x - other.x, y - other.y)
    operator fun times(number: Number) = Vector2(x * number.toDouble(), y * number.toDouble())
    operator fun div(number: Number) = Vector2(x / number.toDouble(), y / number.toDouble())
    operator fun rem(number: Number) = Vector2(x % number.toDouble(), y % number.toDouble())

    companion object {
        fun fromPolar(theta: Double, rho: Double) = Vector2(rho * cos(theta), rho * sin(theta))

        fun zero() = Vector2(0.0, 0.0)
    }
}