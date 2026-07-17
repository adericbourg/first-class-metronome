package dev.dericbourg.firstclassmetronome.presentation.beatselection

import dev.dericbourg.firstclassmetronome.audio.MetronomePlayer
import dev.dericbourg.firstclassmetronome.data.repository.PracticeRepository
import dev.dericbourg.firstclassmetronome.data.settings.AppSettings
import dev.dericbourg.firstclassmetronome.data.settings.SettingsRepository
import dev.dericbourg.firstclassmetronome.domain.model.BeatOutput
import dev.dericbourg.firstclassmetronome.domain.model.ClickSound
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BeatSelectionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var metronomePlayer: MetronomePlayer
    private lateinit var practiceRepository: PracticeRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var settingsFlow: MutableStateFlow<AppSettings>
    private lateinit var currentBeatFlow: MutableStateFlow<Int>
    private lateinit var viewModel: BeatSelectionViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        currentBeatFlow = MutableStateFlow(MetronomePlayer.NO_BEAT)
        metronomePlayer = mockk(relaxed = true) {
            every { currentBeat } returns currentBeatFlow
        }
        practiceRepository = mockk(relaxed = true)
        settingsFlow = MutableStateFlow(AppSettings())
        settingsRepository = mockk(relaxed = true) {
            every { settings } returns settingsFlow
        }
        viewModel = BeatSelectionViewModel(metronomePlayer, practiceRepository, settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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
    fun decreaseBpm_decreasesByBpmIncrement() {
        val bpmIncrement = viewModel.state.value.bpmIncrement

        viewModel.decreaseBpm()

        assertEquals(BeatSelectionState.DEFAULT_BPM - bpmIncrement, viewModel.state.value.selectedBpm)
    }

    @Test
    fun increaseBpm_increasesByBpmIncrement() {
        val bpmIncrement = viewModel.state.value.bpmIncrement

        viewModel.increaseBpm()

        assertEquals(BeatSelectionState.DEFAULT_BPM + bpmIncrement, viewModel.state.value.selectedBpm)
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
        val bpmIncrement = viewModel.state.value.bpmIncrement
        viewModel.play()

        viewModel.decreaseBpm()

        verify { metronomePlayer.updateBpm(BeatSelectionState.DEFAULT_BPM - bpmIncrement) }
    }

    @Test
    fun increaseBpm_whenPlaying_updatesMetronome() {
        val bpmIncrement = viewModel.state.value.bpmIncrement
        viewModel.play()

        viewModel.increaseBpm()

        verify { metronomePlayer.updateBpm(BeatSelectionState.DEFAULT_BPM + bpmIncrement) }
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

    // Settings integration tests

    @Test
    fun bpmIncrement_updatesFromSettings() {
        settingsFlow.value = AppSettings(bpmIncrement = 10)

        assertEquals(10, viewModel.state.value.bpmIncrement)
    }

    @Test
    fun increaseBpm_usesSettingsBpmIncrement() {
        settingsFlow.value = AppSettings(bpmIncrement = 10)

        viewModel.increaseBpm()

        assertEquals(BeatSelectionState.DEFAULT_BPM + 10, viewModel.state.value.selectedBpm)
    }

    @Test
    fun decreaseBpm_usesSettingsBpmIncrement() {
        settingsFlow.value = AppSettings(bpmIncrement = 10)

        viewModel.decreaseBpm()

        assertEquals(BeatSelectionState.DEFAULT_BPM - 10, viewModel.state.value.selectedBpm)
    }

    @Test
    fun isHapticEnabled_initiallyFalse() {
        assertFalse(viewModel.state.value.isHapticEnabled)
    }

    @Test
    fun isHapticEnabled_updatesFromSettings() {
        settingsFlow.value = AppSettings(hapticFeedbackEnabled = true)

        assertTrue(viewModel.state.value.isHapticEnabled)
    }

    @Test
    fun isHapticEnabled_canBeDisabled() {
        settingsFlow.value = AppSettings(hapticFeedbackEnabled = true)
        settingsFlow.value = AppSettings(hapticFeedbackEnabled = false)

        assertFalse(viewModel.state.value.isHapticEnabled)
    }

    // Beat pattern tests

    private val click = BeatOutput.Sound(ClickSound.CLICK)

    @Test
    fun beatConfigInitialState_isNotVisible() {
        assertFalse(viewModel.beatConfigState.value.isVisible)
    }

    @Test
    fun openBeatConfig_makesDialogVisible() {
        viewModel.openBeatConfig()

        assertTrue(viewModel.beatConfigState.value.isVisible)
    }

    @Test
    fun closeBeatConfig_hidesDialog() {
        viewModel.openBeatConfig()

        viewModel.closeBeatConfig()

        assertFalse(viewModel.beatConfigState.value.isVisible)
    }

    @Test
    fun beatPattern_updatesFromSettings() {
        val pattern = listOf(click, BeatOutput.NoSound, BeatOutput.HapticOnly)

        settingsFlow.value = AppSettings(beatPattern = pattern)

        assertEquals(pattern, viewModel.state.value.beatPattern)
    }

    @Test
    fun beatPattern_pushedToPlayerOnSettingsChange() {
        val pattern = listOf(click, BeatOutput.NoSound)

        settingsFlow.value = AppSettings(beatPattern = pattern)

        verify { metronomePlayer.updatePattern(pattern) }
    }

    @Test
    fun setBeatCount_whenLarger_appendsClickBeats() {
        settingsFlow.value = AppSettings(beatPattern = listOf(click, BeatOutput.NoSound))

        viewModel.setBeatCount(4)

        coVerify {
            settingsRepository.setBeatPattern(
                listOf(click, BeatOutput.NoSound, click, click)
            )
        }
    }

    @Test
    fun setBeatCount_whenSmaller_dropsTail() {
        settingsFlow.value = AppSettings(
            beatPattern = listOf(click, BeatOutput.NoSound, BeatOutput.HapticOnly)
        )

        viewModel.setBeatCount(1)

        coVerify { settingsRepository.setBeatPattern(listOf(click)) }
    }

    @Test
    fun setBeatCount_clampsToMaximum() {
        viewModel.setBeatCount(99)

        coVerify {
            settingsRepository.setBeatPattern(
                match { it.size == BeatSelectionState.MAX_BEATS }
            )
        }
    }

    @Test
    fun setBeatCount_clampsToMinimum() {
        settingsFlow.value = AppSettings(beatPattern = listOf(click, click, click))

        viewModel.setBeatCount(0)

        coVerify {
            settingsRepository.setBeatPattern(
                match { it.size == BeatSelectionState.MIN_BEATS }
            )
        }
    }

    @Test
    fun setBeatOutput_replacesBeatAtIndex() {
        settingsFlow.value = AppSettings(beatPattern = listOf(click, click, click))

        viewModel.setBeatOutput(1, BeatOutput.NoSound)

        coVerify {
            settingsRepository.setBeatPattern(listOf(click, BeatOutput.NoSound, click))
        }
    }

    @Test
    fun setBeatOutput_whenIndexOutOfBounds_doesNothing() {
        settingsFlow.value = AppSettings(beatPattern = listOf(click))

        viewModel.setBeatOutput(5, BeatOutput.NoSound)

        coVerify(exactly = 0) { settingsRepository.setBeatPattern(any()) }
    }

    @Test
    fun currentBeat_reflectsPlayerState() {
        currentBeatFlow.value = 2

        assertEquals(2, viewModel.state.value.currentBeat)
    }
}
