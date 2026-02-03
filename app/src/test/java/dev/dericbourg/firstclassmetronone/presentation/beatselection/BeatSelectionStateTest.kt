package dev.dericbourg.firstclassmetronone.presentation.beatselection

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BeatSelectionStateTest {

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
    fun canDecreaseBpm_atMinPlusShift_returnsTrue() {
        val state = BeatSelectionState(selectedBpm = BeatSelectionState.MIN_BPM + BeatSelectionState.BPM_SHIFT_AMOUNT)

        assertTrue(state.canDecreaseBpm)
    }

    @Test
    fun canDecreaseBpm_belowMinPlusShift_returnsFalse() {
        val state = BeatSelectionState(selectedBpm = BeatSelectionState.MIN_BPM + BeatSelectionState.BPM_SHIFT_AMOUNT - 1)

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
    fun canIncreaseBpm_atMaxMinusShift_returnsTrue() {
        val state = BeatSelectionState(selectedBpm = BeatSelectionState.MAX_BPM - BeatSelectionState.BPM_SHIFT_AMOUNT)

        assertTrue(state.canIncreaseBpm)
    }

    @Test
    fun canIncreaseBpm_aboveMaxMinusShift_returnsFalse() {
        val state = BeatSelectionState(selectedBpm = BeatSelectionState.MAX_BPM - BeatSelectionState.BPM_SHIFT_AMOUNT + 1)

        assertFalse(state.canIncreaseBpm)
    }

    @Test
    fun canIncreaseBpm_atMax_returnsFalse() {
        val state = BeatSelectionState(selectedBpm = BeatSelectionState.MAX_BPM)

        assertFalse(state.canIncreaseBpm)
    }

    @Test
    fun constants_haveExpectedValues() {
        assertTrue(BeatSelectionState.MIN_BPM == 20)
        assertTrue(BeatSelectionState.MAX_BPM == 300)
        assertTrue(BeatSelectionState.BPM_SHIFT_AMOUNT == 5)
    }
}
