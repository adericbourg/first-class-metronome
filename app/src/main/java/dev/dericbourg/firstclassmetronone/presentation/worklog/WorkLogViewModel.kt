package dev.dericbourg.firstclassmetronone.presentation.worklog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dericbourg.firstclassmetronone.data.repository.PracticeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkLogViewModel @Inject constructor(
    private val repository: PracticeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WorkLogState())
    val state: StateFlow<WorkLogState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllSessions().collect { sessions ->
                _state.update { it.copy(sessions = sessions) }
            }
        }
        viewModelScope.launch {
            repository.getStats().collect { stats ->
                _state.update { it.copy(stats = stats) }
            }
        }
    }

    fun showClearDialog() {
        _state.update { it.copy(clearDialogState = ClearDataDialogState.FirstConfirmation) }
    }

    fun confirmFirstClear() {
        val currentStats = _state.value.stats
        _state.update {
            it.copy(
                clearDialogState = ClearDataDialogState.SecondConfirmation(
                    sessionCount = currentStats.totalSessionCount,
                    totalDurationMs = currentStats.allTime.totalDurationMs
                )
            )
        }
    }

    fun confirmSecondClear() {
        viewModelScope.launch {
            repository.clearAllData()
            _state.update { it.copy(clearDialogState = ClearDataDialogState.Hidden) }
        }
    }

    fun dismissClearDialog() {
        _state.update { it.copy(clearDialogState = ClearDataDialogState.Hidden) }
    }
}
