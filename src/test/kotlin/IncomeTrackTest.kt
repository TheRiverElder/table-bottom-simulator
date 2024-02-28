import io.github.theriverelder.minigames.tablebottomsimulator.extensions.model.IncomeTrack
import org.junit.jupiter.api.Test
import kotlin.test.expect

class IncomeTrackTest {

    @Test
    fun testIncomeTrackPointsToLevels() {
        val track = IncomeTrack()
        println("-".repeat(16) + "Points to Levels" + "-".repeat(16))
        for (points in 0..100) {
            println("Point $points, Level ${track.levelOfPoints(points)}")
        }

        TEST_POINTS_LEVELS_DATA.forEach {
            expect(it.second) { track.levelOfPoints(it.first) }
        }
    }

    @Test
    fun testIncomeTrackLevelsToPoints() {
        val track = IncomeTrack()
        println("-".repeat(16) + "Levels to Points" + "-".repeat(16))
        for (levels in -10..30) {
            println("Level $levels, Point ${track.maxPointsOfLevel(levels)}")
        }

        TEST_LEVELS_POINTS_DATA.forEach {
            expect(it.first) { track.maxPointsOfLevel(it.second) }
        }
    }
}

val TEST_POINTS_LEVELS_DATA = listOf(
    0 to -10,
    10 to 0,
    11 to 1,
    12 to 1,
    29 to 10,
    30 to 10,
    31 to 11,
    33 to 11,
    60 to 20,
    61 to 21,
    64 to 21,
    97 to 30,
    100 to 30,
)

val TEST_LEVELS_POINTS_DATA = listOf(
    0 to -10,
    10 to 0,
    12 to 1,
    30 to 10,
    33 to 11,
    60 to 20,
    64 to 21,
    100 to 30,
)