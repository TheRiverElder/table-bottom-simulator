package io.github.theriverelder.minigames.tablebottomsimulator.extensions.model

import kotlin.math.ceil

class IncomeTrack(
    val strategy: List<Pair<Int, Int>> = DEFAULT_INCOME_STRATEGY, // 小于等于几点的时候，升一级需要的点数
    var points: Int = 10,
) {

    var level: Int
        get() = levelOfPoints(points)
        set(value) { // 会设置到该等级的最大值
            points = maxPointsOfLevel(value.coerceAtLeast(-10))
        }

    fun levelOfPoints(points: Int): Int {
        var previousStageMaxPoints = -1
        var previousLevel = -11
        for (pair in strategy) {
            val valve = pair.first
            val pointsPerLevel = pair.second

            val restPoints = when {
                points <= previousStageMaxPoints -> break
                points <= valve -> points - previousStageMaxPoints
                else -> valve - previousStageMaxPoints
            }
            previousLevel += ceil(restPoints.toDouble() / pointsPerLevel).toInt()
            previousStageMaxPoints = valve
        }
        return previousLevel
    }

    fun maxPointsOfLevel(level: Int): Int {
        var previousPoints = -1
        var previousLevel = -11
        for (pair in strategy) {
            val valve = pair.first
            val pointsPerLevel = pair.second

            val maxLevelOfThisStage = previousLevel + (valve - previousPoints) / pointsPerLevel

            if (level <= previousLevel) break
            else if (level <= maxLevelOfThisStage) {
                previousPoints += (level - previousLevel) * pointsPerLevel
                previousLevel = level
            } else {
                previousPoints = valve
                previousLevel = maxLevelOfThisStage
            }
        }
        return previousPoints
    }

    fun increase(deltaPoints: Int = 1) {
        points += deltaPoints
    }

    fun decrease(deltaLevels: Int = 1) {
        level -= deltaLevels
    }

    operator fun plusAssign(deltaPoints: Int) = increase(deltaPoints)
    operator fun minusAssign(deltaLevels: Int) = decrease(deltaLevels)
}

val DEFAULT_INCOME_STRATEGY = listOf(
    10 to 1,
    30 to 2,
    60 to 3,
    100 to 4,
)