package dev.dericbourg.firstclassmetronone.presentation.beatselection

import dev.dericbourg.firstclassmetronone.audio.MetronomePlayer
import dev.dericbourg.firstclassmetronone.data.repository.PracticeRepository
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BeatSelectionViewModelTest {

    private lateinit var metronomePlayer: MetronomePlayer
    private lateinit var practiceRepository: PracticeRepository
    private lateinit var viewModel: BeatSelectionViewModel

    @Before
    fun setup() {
        metronomePlayer = mockk(relaxed = true)
        practiceRepository = mockk(relaxed = true)
        viewModel = BeatSelectionViewModel(metronomePlayer, practiceRepository)
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
    fun play_recordsStartEvent() {
        viewModel.play()

        coVerify { practiceRepository.recordStart() }
    }

    @Test
    fun stop_stopsMetronome() {
        viewModel.play()
        viewModel.stop()

        assertFalse(viewModel.state.value.isPlaying)
        verify { metronomePlayer.stop() }
    }

    @Test
    fun stop_recordsStopEvent() {
        viewModel.play()
        viewModel.stop()

        coVerify { practiceRepository.recordStop() }
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

    // Tap Tempo Tests

    @Test
    fun tapTempoInitialState_isNotVisible() {
        assertFalse(viewModel.tapTempoState.value.isVisible)
    }

    @Test
    fun openTapTempo_makesOverlayVisible() {
        viewModel.openTapTempo()

        assertTrue(viewModel.tapTempoState.value.isVisible)
    }

    @Test
    fun openTapTempo_whenPlaying_stopsMetronome() {
        viewModel.play()

        viewModel.openTapTempo()

        assertFalse(viewModel.state.value.isPlaying)
        verify { metronomePlayer.stop() }
    }

    @Test
    fun openTapTempo_whenPlaying_remembersWasPlaying() {
        viewModel.play()

        viewModel.openTapTempo()

        assertTrue(viewModel.tapTempoState.value.wasPlayingBeforeOpen)
    }

    @Test
    fun openTapTempo_whenNotPlaying_remembersWasNotPlaying() {
        viewModel.openTapTempo()

        assertFalse(viewModel.tapTempoState.value.wasPlayingBeforeOpen)
    }

    @Test
    fun closeTapTempo_hidesOverlay() {
        viewModel.openTapTempo()

        viewModel.closeTapTempo()

        assertFalse(viewModel.tapTempoState.value.isVisible)
    }

    @Test
    fun closeTapTempo_whenWasPlaying_resumesPlayback() {
        viewModel.play()
        viewModel.openTapTempo()

        viewModel.closeTapTempo()

        assertTrue(viewModel.state.value.isPlaying)
    }

    @Test
    fun closeTapTempo_whenWasNotPlaying_doesNotResumePlayback() {
        viewModel.openTapTempo()

        viewModel.closeTapTempo()

        assertFalse(viewModel.state.value.isPlaying)
    }

    @Test
    fun recordTap_firstTap_storesTimestamp() {
        viewModel.openTapTempo()

        viewModel.recordTap()

        assertEquals(1, viewModel.tapTempoState.value.tapTimestamps.size)
    }

    @Test
    fun recordTap_multipleTaps_storesAllTimestamps() {
        viewModel.openTapTempo()

        viewModel.recordTap()
        viewModel.recordTap()
        viewModel.recordTap()

        assertEquals(3, viewModel.tapTempoState.value.tapTimestamps.size)
    }

    @Test
    fun recordTap_withTwoTaps_calculatesBpm() {
        viewModel.openTapTempo()

        viewModel.recordTap()
        viewModel.recordTap()

        assertNotNull(viewModel.tapTempoState.value.calculatedBpm)
    }

    @Test
    fun recordTap_withOneTap_doesNotCalculateBpm() {
        viewModel.openTapTempo()

        viewModel.recordTap()

        assertNull(viewModel.tapTempoState.value.calculatedBpm)
    }

    @Test
    fun applyTappedBpm_updatesBeatSelectionBpm() {
        viewModel.openTapTempo()
        viewModel.recordTap()
        viewModel.recordTap()
        val calculatedBpm = viewModel.tapTempoState.value.calculatedBpm!!

        viewModel.applyTappedBpm()

        assertEquals(calculatedBpm, viewModel.state.value.selectedBpm)
    }

    @Test
    fun applyTappedBpm_closesOverlay() {
        viewModel.openTapTempo()
        viewModel.recordTap()
        viewModel.recordTap()

        viewModel.applyTappedBpm()

        assertFalse(viewModel.tapTempoState.value.isVisible)
    }

    @Test
    fun applyTappedBpm_whenWasPlaying_resumesAtNewBpm() {
        viewModel.play()
        viewModel.openTapTempo()
        viewModel.recordTap()
        viewModel.recordTap()
        val calculatedBpm = viewModel.tapTempoState.value.calculatedBpm!!

        viewModel.applyTappedBpm()

        assertTrue(viewModel.state.value.isPlaying)
        verify { metronomePlayer.start(calculatedBpm) }
    }

    @Test
    fun applyTappedBpm_whenWasNotPlaying_doesNotResumePlayback() {
        viewModel.openTapTempo()
        viewModel.recordTap()
        viewModel.recordTap()

        viewModel.applyTappedBpm()

        assertFalse(viewModel.state.value.isPlaying)
    }

    @Test
    fun applyTappedBpm_withNoBpm_doesNothing() {
        viewModel.openTapTempo()

        viewModel.applyTappedBpm()

        assertEquals(BeatSelectionState.DEFAULT_BPM, viewModel.state.value.selectedBpm)
        assertTrue(viewModel.tapTempoState.value.isVisible)
    }

    // Tempo Shift Tests

    @Test
    fun decreaseBpm_decreasesByShiftAmount() {
        viewModel.decreaseBpm()

        assertEquals(BeatSelectionState.DEFAULT_BPM - BeatSelectionState.BPM_SHIFT_AMOUNT, viewModel.state.value.selectedBpm)
    }

    @Test
    fun increaseBpm_increasesByShiftAmount() {
        viewModel.increaseBpm()

        assertEquals(BeatSelectionState.DEFAULT_BPM + BeatSelectionState.BPM_SHIFT_AMOUNT, viewModel.state.value.selectedBpm)
    }

    @Test
    fun decreaseBpm_atMinimum_clampsToMinimum() {
        repeat(20) { viewModel.decreaseBpm() }

        assertEquals(BeatSelectionState.MIN_BPM, viewModel.state.value.selectedBpm)
    }

    @Test
    fun increaseBpm_atMaximum_clampsToMaximum() {
        repeat(60) { viewModel.increaseBpm() }

        assertEquals(BeatSelectionState.MAX_BPM, viewModel.state.value.selectedBpm)
    }

    @Test
    fun decreaseBpm_whenPlaying_updatesMetronome() {
        viewModel.play()

        viewModel.decreaseBpm()

        verify { metronomePlayer.updateBpm(BeatSelectionState.DEFAULT_BPM - BeatSelectionState.BPM_SHIFT_AMOUNT) }
    }

    @Test
    fun increaseBpm_whenPlaying_updatesMetronome() {
        viewModel.play()

        viewModel.increaseBpm()

        verify { metronomePlayer.updateBpm(BeatSelectionState.DEFAULT_BPM + BeatSelectionState.BPM_SHIFT_AMOUNT) }
    }

    @Test
    fun decreaseBpm_whenNotPlaying_doesNotStartMetronome() {
        viewModel.decreaseBpm()

        assertFalse(viewModel.state.value.isPlaying)
    }

    @Test
    fun increaseBpm_whenNotPlaying_doesNotStartMetronome() {
        viewModel.increaseBpm()

        assertFalse(viewModel.state.value.isPlaying)
    }

    @Test
    fun isOnGrid_whenBpmInGrid_returnsTrue() {
        assertTrue(viewModel.state.value.isOnGrid)
    }

    @Test
    fun isOnGrid_whenBpmOffGrid_returnsFalse() {
        viewModel.selectBpm(100)
        viewModel.increaseBpm()

        assertFalse(viewModel.state.value.isOnGrid)
    }

    @Test
    fun canDecreaseBpm_atDefault_returnsTrue() {
        assertTrue(viewModel.state.value.canDecreaseBpm)
    }

    @Test
    fun canDecreaseBpm_nearMinimum_returnsFalse() {
        repeat(10) { viewModel.decreaseBpm() }

        assertFalse(viewModel.state.value.canDecreaseBpm)
    }

    @Test
    fun canIncreaseBpm_atDefault_returnsTrue() {
        assertTrue(viewModel.state.value.canIncreaseBpm)
    }

    @Test
    fun canIncreaseBpm_nearMaximum_returnsFalse() {
        repeat(50) { viewModel.increaseBpm() }

        assertFalse(viewModel.state.value.canIncreaseBpm)
    }

    @Test
    fun selectBpm_afterShift_snapsToGrid() {
        viewModel.selectBpm(100)
        viewModel.increaseBpm()
        assertFalse(viewModel.state.value.isOnGrid)
        assertEquals(105, viewModel.state.value.selectedBpm)

        viewModel.selectBpm(110)

        assertTrue(viewModel.state.value.isOnGrid)
        assertEquals(110, viewModel.state.value.selectedBpm)
    }
}
