package dev.dericbourg.firstclassmetronone.presentation.worklog

import dev.dericbourg.firstclassmetronone.domain.model.PracticeSession
import dev.dericbourg.firstclassmetronone.domain.model.PracticeStats
import java.time.LocalDate

data class WorkLogState(
    val sessions: List<PracticeSession> = emptyList(),
    val stats: PracticeStats = PracticeStats.EMPTY,
    val clearDialogState: ClearDataDialogState = ClearDataDialogState.Hidden
) {
    val isEmpty: Boolean
        get() = sessions.isEmpty()

    val sessionsByDate: Map<LocalDate, List<PracticeSession>>
        get() = sessions.groupBy { it.date }
}

sealed class ClearDataDialogState {
    data object Hidden : ClearDataDialogState()
    data object FirstConfirmation : ClearDataDialogState()
    data class SecondConfirmation(
        val sessionCount: Int,
        val totalDurationMs: Long
    ) : ClearDataDialogState() {
        val totalDurationFormatted: String
            get() {
                val totalSeconds = totalDurationMs / 1000
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
            }
    }
}
