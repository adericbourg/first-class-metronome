package dev.dericbourg.firstclassmetronome.presentation.beatselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dericbourg.firstclassmetronome.audio.MetronomePlayer
import dev.dericbourg.firstclassmetronome.data.repository.PracticeRepository
import dev.dericbourg.firstclassmetronome.data.settings.SettingsRepository
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

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update {
                    it.copy(
                        bpmIncrement = settings.bpmIncrement,
                        isHapticEnabled = settings.hapticFeedbackEnabled
                    )
                }
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

    override fun onCleared() {
        super.onCleared()
        metronomePlayer.stop()
    }
}
