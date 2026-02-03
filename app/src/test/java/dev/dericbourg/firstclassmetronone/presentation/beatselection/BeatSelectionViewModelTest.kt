package dev.dericbourg.firstclassmetronone.presentation.beatselection

import org.junit.Assert.assertEquals
import org.junit.Test

class BeatSelectionViewModelTest {

    @Test
    fun initialState_hasDefaultBpm() {
        val viewModel = BeatSelectionViewModel()

        assertEquals(BeatSelectionState.DEFAULT_BPM, viewModel.state.value.selectedBpm)
    }

    @Test
    fun initialState_hasAllBpmValues() {
        val viewModel = BeatSelectionViewModel()

        assertEquals(BeatSelectionState.BPM_VALUES, viewModel.state.value.availableBpmValues)
    }

    @Test
    fun selectBpm_whenValidValue_updatesBpm() {
        val viewModel = BeatSelectionViewModel()

        viewModel.selectBpm(120)

        assertEquals(120, viewModel.state.value.selectedBpm)
    }

    @Test
    fun selectBpm_whenInvalidValue_doesNotUpdateBpm() {
        val viewModel = BeatSelectionViewModel()

        viewModel.selectBpm(999)

        assertEquals(BeatSelectionState.DEFAULT_BPM, viewModel.state.value.selectedBpm)
    }

    @Test
    fun selectBpm_whenSameValue_maintainsBpm() {
        val viewModel = BeatSelectionViewModel()

        viewModel.selectBpm(BeatSelectionState.DEFAULT_BPM)

        assertEquals(BeatSelectionState.DEFAULT_BPM, viewModel.state.value.selectedBpm)
    }

    @Test
    fun selectBpm_multipleTimes_updatesToLastValue() {
        val viewModel = BeatSelectionViewModel()

        viewModel.selectBpm(30)
        viewModel.selectBpm(150)
        viewModel.selectBpm(85)

        assertEquals(85, viewModel.state.value.selectedBpm)
    }
}
