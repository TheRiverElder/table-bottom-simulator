package io.github.theriverelder.minigames.lib.math

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    val modSquared: Double get() = x * x + y * y
    val modulo: Double get() = sqrt(modSquared)
    val angle: Double get() = atan2(y, x)

    override fun toString(): String = "(${x}, ${y})"
    companion object {
        fun fromPolar(angle: Double, modulo: Double) = Vector2(modulo * cos(angle), modulo * sin(angle))

        fun zero() = Vector2(0.0, 0.0)
    }
}