package dev.dericbourg.firstclassmetronome.presentation.beatselection

/**
 * Visibility of the beat-configuration dialog. The pattern itself is edited live against
 * [BeatSelectionState.beatPattern], so this only tracks whether the dialog is open.
 */
data class BeatConfigState(
    val isVisible: Boolean = false
)
