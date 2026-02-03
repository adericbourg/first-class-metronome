package dev.dericbourg.firstclassmetronone.presentation.beatselection

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dericbourg.firstclassmetronone.audio.MetronomePlayer
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

    override fun onCleared() {
        super.onCleared()
        metronomePlayer.stop()
    }
}
