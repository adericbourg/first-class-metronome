package dev.dericbourg.firstclassmetronone.presentation.taptempo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TapTempoStateTest {

    @Test
    fun calculateBpm_withLessThanTwoTaps_returnsNull() {
        assertNull(TapTempoState.calculateBpm(emptyList()))
        assertNull(TapTempoState.calculateBpm(listOf(1000L)))
    }

    @Test
    fun calculateBpm_withTwoTaps_calculatesFromSingleInterval() {
        val timestamps = listOf(0L, 500L)

        val bpm = TapTempoState.calculateBpm(timestamps)

        assertEquals(120, bpm)
    }

    @Test
    fun calculateBpm_withMultipleTaps_averagesIntervals() {
        val timestamps = listOf(0L, 500L, 1000L, 1500L)

        val bpm = TapTempoState.calculateBpm(timestamps)

        assertEquals(120, bpm)
    }

    @Test
    fun calculateBpm_withMoreThanMaxIntervals_usesOnlyLastFive() {
        val timestamps = listOf(0L, 10000L, 10500L, 11000L, 11500L, 12000L, 12500L)

        val bpm = TapTempoState.calculateBpm(timestamps)

        assertEquals(120, bpm)
    }

    @Test
    fun calculateBpm_at60Bpm_returns60() {
        val timestamps = listOf(0L, 1000L, 2000L)

        val bpm = TapTempoState.calculateBpm(timestamps)

        assertEquals(60, bpm)
    }

    @Test
    fun calculateBpm_at180Bpm_returns180() {
        val timestamps = listOf(0L, 333L, 667L, 1000L)

        val bpm = TapTempoState.calculateBpm(timestamps)

        assertEquals(180, bpm)
    }

    @Test
    fun canApply_withLessThanMinTaps_returnsFalse() {
        val state = TapTempoState(tapTimestamps = listOf(1000L))

        assertFalse(state.canApply)
    }

    @Test
    fun canApply_withMinTaps_returnsTrue() {
        val state = TapTempoState(tapTimestamps = listOf(0L, 500L))

        assertTrue(state.canApply)
    }

    @Test
    fun canApply_withMoreThanMinTaps_returnsTrue() {
        val state = TapTempoState(tapTimestamps = listOf(0L, 500L, 1000L, 1500L))

        assertTrue(state.canApply)
    }

    @Test
    fun defaultState_hasCorrectValues() {
        val state = TapTempoState()

        assertFalse(state.isVisible)
        assertTrue(state.tapTimestamps.isEmpty())
        assertNull(state.calculatedBpm)
        assertFalse(state.wasPlayingBeforeOpen)
        assertEquals(0L, state.lastTapTime)
    }
}
