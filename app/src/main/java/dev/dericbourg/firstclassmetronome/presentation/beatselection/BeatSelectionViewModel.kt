package dev.dericbourg.firstclassmetronome.presentation.beatselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dericbourg.firstclassmetronome.audio.MetronomePlayer
import dev.dericbourg.firstclassmetronome.data.repository.PracticeRepository
import dev.dericbourg.firstclassmetronome.data.settings.SettingsRepository
import dev.dericbourg.firstclassmetronome.domain.model.BeatOutput
import dev.dericbourg.firstclassmetronome.domain.model.ClickSound
import dev.dericbourg.firstclassmetronome.presentation.taptempo.TapTempoState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BeatSelectionViewModel @Inject constructor(
    private val metronomePlayer: MetronomePlayer,
    private val practiceRepository: PracticeRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BeatSelectionState())
    val state: StateFlow<BeatSelectionState> = _state.asStateFlow()

    private val _tapTempoState = MutableStateFlow(TapTempoState())
    val tapTempoState: StateFlow<TapTempoState> = _tapTempoState.asStateFlow()

    private val _beatConfigState = MutableStateFlow(BeatConfigState())
    val beatConfigState: StateFlow<BeatConfigState> = _beatConfigState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                metronomePlayer.updatePattern(settings.beatPattern)
                _state.update {
                    it.copy(
                        bpmIncrement = settings.bpmIncrement,
                        isHapticEnabled = settings.hapticFeedbackEnabled,
                        beatPattern = settings.beatPattern
                    )
                }
            }
        }
        viewModelScope.launch {
            metronomePlayer.currentBeat.collect { beat ->
                _state.update { it.copy(currentBeat = beat) }
            }
        }
    }

    fun selectBpm(bpm: Int) {
        if (bpm in _state.value.availableBpmValues) {
            _state.update { it.copy(selectedBpm = bpm) }
            if (_state.value.isPlaying) {
                metronomePlayer.updateBpm(bpm)
            } else {
                play()
            }
        }
    }

    fun decreaseBpm() {
        val currentState = _state.value
        val newBpm = (currentState.selectedBpm - currentState.bpmIncrement)
            .coerceAtLeast(BeatSelectionState.MIN_BPM)
        setBpm(newBpm)
    }

    fun increaseBpm() {
        val currentState = _state.value
        val newBpm = (currentState.selectedBpm + currentState.bpmIncrement)
            .coerceAtMost(BeatSelectionState.MAX_BPM)
        setBpm(newBpm)
    }

    private fun setBpm(bpm: Int) {
        _state.update { it.copy(selectedBpm = bpm) }
        if (_state.value.isPlaying) {
            metronomePlayer.updateBpm(bpm)
        }
    }

    fun togglePlayback() {
        val currentState = _state.value
        if (currentState.isPlaying) {
            stop()
        } else {
            play()
        }
    }

    fun play() {
        metronomePlayer.start(_state.value.selectedBpm)
        _state.update { it.copy(isPlaying = true) }
        viewModelScope.launch {
            practiceRepository.recordStart()
        }
    }

    fun stop() {
        metronomePlayer.stop()
        _state.update { it.copy(isPlaying = false) }
        viewModelScope.launch {
            practiceRepository.recordStop()
        }
    }

    fun openTapTempo() {
        val wasPlaying = _state.value.isPlaying
        if (wasPlaying) {
            metronomePlayer.stop()
            _state.update { it.copy(isPlaying = false) }
        }
        _tapTempoState.update {
            TapTempoState(
                isVisible = true,
                wasPlayingBeforeOpen = wasPlaying
            )
        }
    }

    fun closeTapTempo() {
        val wasPlaying = _tapTempoState.value.wasPlayingBeforeOpen
        _tapTempoState.update { TapTempoState() }
        if (wasPlaying) {
            play()
        }
    }

    fun recordTap() {
        val now = System.currentTimeMillis()
        val currentState = _tapTempoState.value
        val timeSinceLastTap = now - currentState.lastTapTime

        val newTimestamps = if (timeSinceLastTap > TapTempoState.TIMEOUT_MS && currentState.tapTimestamps.isNotEmpty()) {
            listOf(now)
        } else {
            currentState.tapTimestamps + now
        }

        val newBpm = TapTempoState.calculateBpm(newTimestamps)

        _tapTempoState.update {
            it.copy(
                tapTimestamps = newTimestamps,
                calculatedBpm = newBpm ?: it.calculatedBpm,
                lastTapTime = now
            )
        }
    }

    fun applyTappedBpm() {
        val tappedBpm = _tapTempoState.value.calculatedBpm ?: return
        val wasPlaying = _tapTempoState.value.wasPlayingBeforeOpen

        _state.update { currentState ->
            val isOnGrid = tappedBpm in currentState.availableBpmValues
            currentState.copy(
                selectedBpm = tappedBpm,
                isPlaying = wasPlaying
            ).let {
                if (!isOnGrid) it else it
            }
        }

        if (wasPlaying) {
            metronomePlayer.start(tappedBpm)
        }

        _tapTempoState.update { TapTempoState() }
    }

    fun openBeatConfig() {
        _beatConfigState.update { it.copy(isVisible = true) }
    }

    fun closeBeatConfig() {
        _beatConfigState.update { it.copy(isVisible = false) }
    }

    fun setBeatCount(count: Int) {
        val clamped = count.coerceIn(BeatSelectionState.MIN_BEATS, BeatSelectionState.MAX_BEATS)
        val current = _state.value.beatPattern
        if (clamped == current.size) return

        val resized = if (clamped > current.size) {
            current + List(clamped - current.size) { DEFAULT_ADDED_BEAT }
        } else {
            current.take(clamped)
        }
        persistPattern(resized)
    }

    fun setBeatOutput(index: Int, output: BeatOutput) {
        val current = _state.value.beatPattern
        if (index !in current.indices) return
        persistPattern(current.toMutableList().apply { this[index] = output })
    }

    private fun persistPattern(pattern: List<BeatOutput>) {
        viewModelScope.launch {
            settingsRepository.setBeatPattern(pattern)
        }
    }

    override fun onCleared() {
        super.onCleared()
        metronomePlayer.stop()
    }

    companion object {
        private val DEFAULT_ADDED_BEAT = BeatOutput.Sound(ClickSound.CLICK)
    }
}
