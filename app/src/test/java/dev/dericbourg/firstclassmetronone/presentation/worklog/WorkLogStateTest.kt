package dev.dericbourg.firstclassmetronone.presentation.worklog

import dev.dericbourg.firstclassmetronone.domain.model.PracticeSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class WorkLogStateTest {

    private fun localDateToMillis(date: LocalDate): Long {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @Test
    fun isEmpty_withNoSessions_returnsTrue() {
        val state = WorkLogState()

        assertTrue(state.isEmpty)
    }

    @Test
    fun isEmpty_withSessions_returnsFalse() {
        val state = WorkLogState(
            sessions = listOf(
                PracticeSession(startTime = 1000L, endTime = 2000L, durationMs = 1000L)
            )
        )

        assertFalse(state.isEmpty)
    }

    @Test
    fun sessionsByDate_groupsSessionsByDate() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val todayMillis = localDateToMillis(today)
        val yesterdayMillis = localDateToMillis(yesterday)

        val state = WorkLogState(
            sessions = listOf(
                PracticeSession(startTime = todayMillis, endTime = todayMillis + 1000L, durationMs = 1000L),
                PracticeSession(startTime = todayMillis + 10_000L, endTime = todayMillis + 11_000L, durationMs = 1000L),
                PracticeSession(startTime = yesterdayMillis, endTime = yesterdayMillis + 1000L, durationMs = 1000L)
            )
        )

        assertEquals(2, state.sessionsByDate.size)
        assertEquals(2, state.sessionsByDate[today]?.size)
        assertEquals(1, state.sessionsByDate[yesterday]?.size)
    }

    @Test
    fun sessionsByDate_withEmptySessions_returnsEmptyMap() {
        val state = WorkLogState()

        assertTrue(state.sessionsByDate.isEmpty())
    }
}
