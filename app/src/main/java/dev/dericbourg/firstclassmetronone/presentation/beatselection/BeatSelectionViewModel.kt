package dev.dericbourg.firstclassmetronone.presentation.beatselection

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dericbourg.firstclassmetronone.audio.MetronomePlayer
import dev.dericbourg.firstclassmetronone.presentation.taptempo.TapTempoState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BeatSelectionViewModel @Inject constructor(
    private val metronomePlayer: MetronomePlayer
) : ViewModel() {

    private val _state = MutableStateFlow(BeatSelectionState())
    val state: StateFlow<BeatSelectionState> = _state.asStateFlow()

    private val _tapTempoState = MutableStateFlow(TapTempoState())
    val tapTempoState: StateFlow<TapTempoState> = _tapTempoState.asStateFlow()

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
        val newBpm = (_state.value.selectedBpm - BeatSelectionState.BPM_SHIFT_AMOUNT)
            .coerceAtLeast(BeatSelectionState.MIN_BPM)
        setBpm(newBpm)
    }

    fun increaseBpm() {
        val newBpm = (_state.value.selectedBpm + BeatSelectionState.BPM_SHIFT_AMOUNT)
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
    }

    fun stop() {
        metronomePlayer.stop()
        _state.update { it.copy(isPlaying = false) }
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
