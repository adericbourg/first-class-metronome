package dev.dericbourg.firstclassmetronone.presentation.beatselection

import dev.dericbourg.firstclassmetronone.data.settings.AppSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BeatSelectionStateTest {

    private val defaultBpmIncrement = AppSettings.DEFAULT_BPM_INCREMENT

    @Test
    fun isOnGrid_whenBpmInGridValues_returnsTrue() {
        val state = BeatSelectionState(selectedBpm = 60)

        assertTrue(state.isOnGrid)
    }

    @Test
    fun isOnGrid_whenBpmNotInGridValues_returnsFalse() {
        val state = BeatSelectionState(selectedBpm = 62)

        assertFalse(state.isOnGrid)
    }

    @Test
    fun isOnGrid_atMinGridValue_returnsTrue() {
        val state = BeatSelectionState(selectedBpm = 30)

        assertTrue(state.isOnGrid)
    }

    @Test
    fun isOnGrid_atMaxGridValue_returnsTrue() {
        val state = BeatSelectionState(selectedBpm = 150)

        assertTrue(state.isOnGrid)
    }

    @Test
    fun isOnGrid_belowMinGridValue_returnsFalse() {
        val state = BeatSelectionState(selectedBpm = 25)

        assertFalse(state.isOnGrid)
    }

    @Test
    fun isOnGrid_aboveMaxGridValue_returnsFalse() {
        val state = BeatSelectionState(selectedBpm = 200)

        assertFalse(state.isOnGrid)
    }

    @Test
    fun canDecreaseBpm_whenAboveMinPlusShift_returnsTrue() {
        val state = BeatSelectionState(selectedBpm = 60)

        assertTrue(state.canDecreaseBpm)
    }

    @Test
    fun canDecreaseBpm_atMinPlusIncrement_returnsTrue() {
        val state = BeatSelectionState(selectedBpm = BeatSelectionState.MIN_BPM + defaultBpmIncrement)

        assertTrue(state.canDecreaseBpm)
    }

    @Test
    fun canDecreaseBpm_belowMinPlusIncrement_returnsFalse() {
        val state = BeatSelectionState(selectedBpm = BeatSelectionState.MIN_BPM + defaultBpmIncrement - 1)

        assertFalse(state.canDecreaseBpm)
    }

    @Test
    fun canDecreaseBpm_atMin_returnsFalse() {
        val state = BeatSelectionState(selectedBpm = BeatSelectionState.MIN_BPM)

        assertFalse(state.canDecreaseBpm)
    }

    @Test
    fun canIncreaseBpm_whenBelowMaxMinusShift_returnsTrue() {
        val state = BeatSelectionState(selectedBpm = 60)

        assertTrue(state.canIncreaseBpm)
    }

    @Test
    fun canIncreaseBpm_atMaxMinusIncrement_returnsTrue() {
        val state = BeatSelectionState(selectedBpm = BeatSelectionState.MAX_BPM - defaultBpmIncrement)

        assertTrue(state.canIncreaseBpm)
    }

    @Test
    fun canIncreaseBpm_aboveMaxMinusIncrement_returnsFalse() {
        val state = BeatSelectionState(selectedBpm = BeatSelectionState.MAX_BPM - defaultBpmIncrement + 1)

        assertFalse(state.canIncreaseBpm)
    }

    @Test
    fun canIncreaseBpm_atMax_returnsFalse() {
        val state = BeatSelectionState(selectedBpm = BeatSelectionState.MAX_BPM)

        assertFalse(state.canIncreaseBpm)
    }

    @Test
    fun constants_haveExpectedValues() {
        assertEquals(20, BeatSelectionState.MIN_BPM)
        assertEquals(300, BeatSelectionState.MAX_BPM)
    }

    @Test
    fun bpmIncrement_defaultsToSettingsDefault() {
        val state = BeatSelectionState()

        assertEquals(AppSettings.DEFAULT_BPM_INCREMENT, state.bpmIncrement)
    }

    @Test
    fun canDecreaseBpm_usesCustomIncrement() {
        val state = BeatSelectionState(selectedBpm = 30, bpmIncrement = 10)

        assertTrue(state.canDecreaseBpm)

        val stateAtMinWithCustomIncrement = BeatSelectionState(selectedBpm = 29, bpmIncrement = 10)
        assertFalse(stateAtMinWithCustomIncrement.canDecreaseBpm)
    }

    @Test
    fun canIncreaseBpm_usesCustomIncrement() {
        val state = BeatSelectionState(selectedBpm = 290, bpmIncrement = 10)

        assertTrue(state.canIncreaseBpm)

        val stateNearMaxWithCustomIncrement = BeatSelectionState(selectedBpm = 291, bpmIncrement = 10)
        assertFalse(stateNearMaxWithCustomIncrement.canIncreaseBpm)
    }
}
