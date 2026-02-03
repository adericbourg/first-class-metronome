package dev.dericbourg.firstclassmetronone.presentation.beatselection

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BeatSelectionViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(BeatSelectionState())
    val state: StateFlow<BeatSelectionState> = _state.asStateFlow()

    fun selectBpm(bpm: Int) {
        if (bpm in _state.value.availableBpmValues) {
            _state.update { it.copy(selectedBpm = bpm) }
        }
    }
}
