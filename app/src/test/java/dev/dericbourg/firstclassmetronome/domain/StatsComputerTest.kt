package dev.dericbourg.firstclassmetronome.domain

import dev.dericbourg.firstclassmetronome.domain.model.PracticeSession
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class StatsComputerTest {

    private lateinit var statsComputer: StatsComputer

    @Before
    fun setup() {
        statsComputer = StatsComputer()
    }

    private fun localDateToMillis(date: LocalDate): Long {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @Test
    fun computeStats_ofEmptyList_returnsZeros() {
        val result = statsComputer.computeStats(emptyList())

        assertEquals(0L, result.last7Days.totalDurationMs)
        assertEquals(0L, result.last30Days.totalDurationMs)
        assertEquals(0L, result.allTime.totalDurationMs)
    }

    @Test
    fun computeStats_ofTodaySession_includesInAllPeriods() {
        val today = LocalDate.now()
        val startTime = localDateToMillis(today)
        val sessions = listOf(
            PracticeSession(
                startTime = startTime,
                endTime = startTime + 60_000L,
                durationMs = 60_000L
            )
        )

        val result = statsComputer.computeStats(sessions)

        assertEquals(60_000L, result.last7Days.totalDurationMs)
        assertEquals(60_000L, result.last30Days.totalDurationMs)
        assertEquals(60_000L, result.allTime.totalDurationMs)
        assertEquals(1, result.last7Days.sessionCount)
        assertEquals(1, result.last7Days.daysWithPractice)
    }

    @Test
    fun computeStats_of6DaysAgoSession_includesInAllPeriods() {
        val today = LocalDate.now()
        val sixDaysAgo = today.minusDays(6)
        val startTime = localDateToMillis(sixDaysAgo)
        val sessions = listOf(
            PracticeSession(
                startTime = startTime,
                endTime = startTime + 60_000L,
                durationMs = 60_000L
            )
        )

        val result = statsComputer.computeStats(sessions)

        assertEquals(60_000L, result.last7Days.totalDurationMs)
        assertEquals(60_000L, result.last30Days.totalDurationMs)
        assertEquals(60_000L, result.allTime.totalDurationMs)
    }

    @Test
    fun computeStats_of8DaysAgoSession_excludesFrom7Days() {
        val today = LocalDate.now()
        val eightDaysAgo = today.minusDays(8)
        val startTime = localDateToMillis(eightDaysAgo)
        val sessions = listOf(
            PracticeSession(
                startTime = startTime,
                endTime = startTime + 60_000L,
                durationMs = 60_000L
            )
        )

        val result = statsComputer.computeStats(sessions)

        assertEquals(0L, result.last7Days.totalDurationMs)
        assertEquals(60_000L, result.last30Days.totalDurationMs)
        assertEquals(60_000L, result.allTime.totalDurationMs)
    }

    @Test
    fun computeStats_of31DaysAgoSession_excludesFrom30Days() {
        val today = LocalDate.now()
        val thirtyOneDaysAgo = today.minusDays(31)
        val startTime = localDateToMillis(thirtyOneDaysAgo)
        val sessions = listOf(
            PracticeSession(
                startTime = startTime,
                endTime = startTime + 60_000L,
                durationMs = 60_000L
            )
        )

        val result = statsComputer.computeStats(sessions)

        assertEquals(0L, result.last7Days.totalDurationMs)
        assertEquals(0L, result.last30Days.totalDurationMs)
        assertEquals(60_000L, result.allTime.totalDurationMs)
    }

    @Test
    fun computeStats_ofMultipleSessions_sumsCorrectly() {
        val today = LocalDate.now()
        val sessions = listOf(
            PracticeSession(
                startTime = localDateToMillis(today),
                endTime = localDateToMillis(today) + 60_000L,
                durationMs = 60_000L
            ),
            PracticeSession(
                startTime = localDateToMillis(today.minusDays(5)),
                endTime = localDateToMillis(today.minusDays(5)) + 120_000L,
                durationMs = 120_000L
            ),
            PracticeSession(
                startTime = localDateToMillis(today.minusDays(15)),
                endTime = localDateToMillis(today.minusDays(15)) + 180_000L,
                durationMs = 180_000L
            ),
            PracticeSession(
                startTime = localDateToMillis(today.minusDays(60)),
                endTime = localDateToMillis(today.minusDays(60)) + 240_000L,
                durationMs = 240_000L
            )
        )

        val result = statsComputer.computeStats(sessions)

        assertEquals(180_000L, result.last7Days.totalDurationMs)
        assertEquals(360_000L, result.last30Days.totalDurationMs)
        assertEquals(600_000L, result.allTime.totalDurationMs)
        assertEquals(2, result.last7Days.sessionCount)
        assertEquals(3, result.last30Days.sessionCount)
        assertEquals(4, result.allTime.sessionCount)
    }

    @Test
    fun computeStats_countsUniqueDays() {
        val today = LocalDate.now()
        val sessions = listOf(
            PracticeSession(
                startTime = localDateToMillis(today),
                endTime = localDateToMillis(today) + 60_000L,
                durationMs = 60_000L
            ),
            PracticeSession(
                startTime = localDateToMillis(today) + 3600_000L,
                endTime = localDateToMillis(today) + 3660_000L,
                durationMs = 60_000L
            )
        )

        val result = statsComputer.computeStats(sessions)

        assertEquals(1, result.last7Days.daysWithPractice)
        assertEquals(2, result.last7Days.sessionCount)
    }

    @Test
    fun durationFormatted_ofMinutes_formatsCorrectly() {
        val today = LocalDate.now()
        val sessions = listOf(
            PracticeSession(
                startTime = localDateToMillis(today),
                endTime = localDateToMillis(today) + 5 * 60_000L,
                durationMs = 5 * 60_000L
            )
        )

        val result = statsComputer.computeStats(sessions)

        assertEquals("05:00", result.last7Days.durationFormatted)
    }

    @Test
    fun durationFormatted_ofHoursAndMinutes_formatsCorrectly() {
        val today = LocalDate.now()
        val sessions = listOf(
            PracticeSession(
                startTime = localDateToMillis(today),
                endTime = localDateToMillis(today) + 65 * 60_000L,
                durationMs = 65 * 60_000L
            )
        )

        val result = statsComputer.computeStats(sessions)

        assertEquals("01:05:00", result.last7Days.durationFormatted)
    }

    @Test
    fun averagePerSession_computesCorrectly() {
        val today = LocalDate.now()
        val sessions = listOf(
            PracticeSession(
                startTime = localDateToMillis(today),
                endTime = localDateToMillis(today) + 60_000L,
                durationMs = 60_000L
            ),
            PracticeSession(
                startTime = localDateToMillis(today) + 3600_000L,
                endTime = localDateToMillis(today) + 3720_000L,
                durationMs = 120_000L
            )
        )

        val result = statsComputer.computeStats(sessions)

        assertEquals(90_000L, result.last7Days.averagePerSession)
    }

    @Test
    fun averagePerDay_computesCorrectly() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val sessions = listOf(
            PracticeSession(
                startTime = localDateToMillis(today),
                endTime = localDateToMillis(today) + 60_000L,
                durationMs = 60_000L
            ),
            PracticeSession(
                startTime = localDateToMillis(yesterday),
                endTime = localDateToMillis(yesterday) + 120_000L,
                durationMs = 120_000L
            )
        )

        val result = statsComputer.computeStats(sessions)

        assertEquals(90_000L, result.last7Days.averagePerDay)
    }
}
