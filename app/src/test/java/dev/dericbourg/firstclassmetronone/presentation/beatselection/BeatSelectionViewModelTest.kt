package dev.dericbourg.firstclassmetronone.presentation.beatselection

import dev.dericbourg.firstclassmetronone.audio.MetronomePlayer
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BeatSelectionViewModelTest {

    private lateinit var metronomePlayer: MetronomePlayer
    private lateinit var viewModel: BeatSelectionViewModel

    @Before
    fun setup() {
        metronomePlayer = mockk(relaxed = true)
        viewModel = BeatSelectionViewModel(metronomePlayer)
    }

    @Test
    fun initialState_hasDefaultBpm() {
        assertEquals(BeatSelectionState.DEFAULT_BPM, viewModel.state.value.selectedBpm)
    }

    @Test
    fun initialState_hasAllBpmValues() {
        assertEquals(BeatSelectionState.BPM_VALUES, viewModel.state.value.availableBpmValues)
    }

    @Test
    fun initialState_isNotPlaying() {
        assertFalse(viewModel.state.value.isPlaying)
    }

    @Test
    fun selectBpm_whenValidValue_updatesBpm() {
        viewModel.selectBpm(120)

        assertEquals(120, viewModel.state.value.selectedBpm)
    }

    @Test
    fun selectBpm_whenInvalidValue_doesNotUpdateBpm() {
        viewModel.selectBpm(999)

        assertEquals(BeatSelectionState.DEFAULT_BPM, viewModel.state.value.selectedBpm)
    }

    @Test
    fun selectBpm_whenSameValue_maintainsBpm() {
        viewModel.selectBpm(BeatSelectionState.DEFAULT_BPM)

        assertEquals(BeatSelectionState.DEFAULT_BPM, viewModel.state.value.selectedBpm)
    }

    @Test
    fun selectBpm_multipleTimes_updatesToLastValue() {
        viewModel.selectBpm(30)
        viewModel.selectBpm(150)
        viewModel.selectBpm(85)

        assertEquals(85, viewModel.state.value.selectedBpm)
    }

    @Test
    fun play_startsMetronome() {
        viewModel.play()

        assertTrue(viewModel.state.value.isPlaying)
        verify { metronomePlayer.start(BeatSelectionState.DEFAULT_BPM) }
    }

    @Test
    fun stop_stopsMetronome() {
        viewModel.play()
        viewModel.stop()

        assertFalse(viewModel.state.value.isPlaying)
        verify { metronomePlayer.stop() }
    }

    @Test
    fun togglePlayback_whenNotPlaying_startsPlayback() {
        viewModel.togglePlayback()

        assertTrue(viewModel.state.value.isPlaying)
        verify { metronomePlayer.start(any()) }
    }

    @Test
    fun togglePlayback_whenPlaying_stopsPlayback() {
        viewModel.play()
        viewModel.togglePlayback()

        assertFalse(viewModel.state.value.isPlaying)
        verify { metronomePlayer.stop() }
    }

    @Test
    fun selectBpm_whenPlaying_updatesBpmInPlayer() {
        viewModel.play()

        viewModel.selectBpm(120)

        verify { metronomePlayer.updateBpm(120) }
    }

    @Test
    fun selectBpm_whenNotPlaying_startsPlayback() {
        viewModel.selectBpm(120)

        assertTrue(viewModel.state.value.isPlaying)
        verify { metronomePlayer.start(120) }
    }

    @Test
    fun play_usesSelectedBpm() {
        viewModel.selectBpm(120)

        viewModel.play()

        verify { metronomePlayer.start(120) }
    }
}
