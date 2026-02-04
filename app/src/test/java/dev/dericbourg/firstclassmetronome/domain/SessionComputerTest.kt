package dev.dericbourg.firstclassmetronome.domain

import dev.dericbourg.firstclassmetronome.data.entity.PracticeEventEntity
import dev.dericbourg.firstclassmetronome.data.model.EventType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SessionComputerTest {

    private lateinit var sessionComputer: SessionComputer

    @Before
    fun setup() {
        sessionComputer = SessionComputer()
    }

    @Test
    fun computeSessions_ofEmptyList_returnsEmptyList() {
        val result = sessionComputer.computeSessions(emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun computeSessions_ofSingleStartEvent_returnsEmptyList() {
        val events = listOf(
            PracticeEventEntity(id = 1, timestamp = 1000L, eventType = EventType.START)
        )

        val result = sessionComputer.computeSessions(events)

        assertTrue(result.isEmpty())
    }

    @Test
    fun computeSessions_ofStartAndStop_returnsOneSession() {
        val events = listOf(
            PracticeEventEntity(id = 1, timestamp = 1000L, eventType = EventType.START),
            PracticeEventEntity(id = 2, timestamp = 2000L, eventType = EventType.STOP)
        )

        val result = sessionComputer.computeSessions(events)

        assertEquals(1, result.size)
        assertEquals(1000L, result[0].startTime)
        assertEquals(2000L, result[0].endTime)
        assertEquals(1000L, result[0].durationMs)
    }

    @Test
    fun computeSessions_ofMultipleStartStop_withinShortPause_returnsOneSession() {
        val events = listOf(
            PracticeEventEntity(id = 1, timestamp = 0L, eventType = EventType.START),
            PracticeEventEntity(id = 2, timestamp = 60_000L, eventType = EventType.STOP),
            PracticeEventEntity(id = 3, timestamp = 120_000L, eventType = EventType.START),
            PracticeEventEntity(id = 4, timestamp = 180_000L, eventType = EventType.STOP)
        )

        val result = sessionComputer.computeSessions(events)

        assertEquals(1, result.size)
        assertEquals(0L, result[0].startTime)
        assertEquals(180_000L, result[0].endTime)
        assertEquals(180_000L, result[0].durationMs) // 60s play + 60s short pause + 60s play
    }

    @Test
    fun computeSessions_ofMultipleStartStop_withLongGap_returnsTwoSessions() {
        val oneHourGap = 61 * 60 * 1000L
        val events = listOf(
            PracticeEventEntity(id = 1, timestamp = 0L, eventType = EventType.START),
            PracticeEventEntity(id = 2, timestamp = 60_000L, eventType = EventType.STOP),
            PracticeEventEntity(id = 3, timestamp = 60_000L + oneHourGap, eventType = EventType.START),
            PracticeEventEntity(id = 4, timestamp = 60_000L + oneHourGap + 60_000L, eventType = EventType.STOP)
        )

        val result = sessionComputer.computeSessions(events)

        assertEquals(2, result.size)
        assertEquals(0L, result[0].startTime)
        assertEquals(60_000L, result[0].durationMs)
        assertEquals(60_000L + oneHourGap, result[1].startTime)
        assertEquals(60_000L, result[1].durationMs)
    }

    @Test
    fun computeSessions_ofUnorderedEvents_sortsAndComputes() {
        val events = listOf(
            PracticeEventEntity(id = 2, timestamp = 2000L, eventType = EventType.STOP),
            PracticeEventEntity(id = 1, timestamp = 1000L, eventType = EventType.START)
        )

        val result = sessionComputer.computeSessions(events)

        assertEquals(1, result.size)
        assertEquals(1000L, result[0].durationMs)
    }

    @Test
    fun computeSessions_ofConsecutiveStarts_ignoresExtraStarts() {
        val events = listOf(
            PracticeEventEntity(id = 1, timestamp = 1000L, eventType = EventType.START),
            PracticeEventEntity(id = 2, timestamp = 1500L, eventType = EventType.START),
            PracticeEventEntity(id = 3, timestamp = 2000L, eventType = EventType.STOP)
        )

        val result = sessionComputer.computeSessions(events)

        assertEquals(1, result.size)
        assertEquals(1000L, result[0].startTime)
    }

    @Test
    fun computeSessions_ofConsecutiveStops_ignoresExtraStops() {
        val events = listOf(
            PracticeEventEntity(id = 1, timestamp = 1000L, eventType = EventType.START),
            PracticeEventEntity(id = 2, timestamp = 2000L, eventType = EventType.STOP),
            PracticeEventEntity(id = 3, timestamp = 2500L, eventType = EventType.STOP)
        )

        val result = sessionComputer.computeSessions(events)

        assertEquals(1, result.size)
        assertEquals(1000L, result[0].durationMs)
    }

    @Test
    fun computeSessions_ofStopWithoutStart_returnsEmptyList() {
        val events = listOf(
            PracticeEventEntity(id = 1, timestamp = 1000L, eventType = EventType.STOP)
        )

        val result = sessionComputer.computeSessions(events)

        assertTrue(result.isEmpty())
    }
}
