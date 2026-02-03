package dev.dericbourg.firstclassmetronone.presentation.beatselection

import dev.dericbourg.firstclassmetronone.data.settings.AppSettings

data class BeatSelectionState(
    val selectedBpm: Int = DEFAULT_BPM,
    val availableBpmValues: List<Int> = BPM_VALUES,
    val isPlaying: Boolean = false,
    val bpmIncrement: Int = AppSettings.DEFAULT_BPM_INCREMENT
) {
    val isOnGrid: Boolean
        get() = selectedBpm in availableBpmValues

    val canDecreaseBpm: Boolean
        get() = selectedBpm - bpmIncrement >= MIN_BPM

    val canIncreaseBpm: Boolean
        get() = selectedBpm + bpmIncrement <= MAX_BPM

    companion object {
        const val DEFAULT_BPM = 60
        const val GRID_COLUMNS = 4
        const val MIN_BPM = 20
        const val MAX_BPM = 300

        val BPM_VALUES = listOf(
            30, 35, 40, 45,
            50, 55, 60, 65,
            70, 75, 80, 85,
            90, 95, 100, 110,
            120, 130, 140, 150
        )
    }
}
