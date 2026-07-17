package dev.dericbourg.firstclassmetronome.presentation.beatselection

import dev.dericbourg.firstclassmetronome.data.settings.AppSettings
import dev.dericbourg.firstclassmetronome.domain.model.BeatOutput
import dev.dericbourg.firstclassmetronome.domain.model.BeatPattern

data class BeatSelectionState(
    val selectedBpm: Int = DEFAULT_BPM,
    val availableBpmValues: List<Int> = BPM_VALUES,
    val isPlaying: Boolean = false,
    val bpmIncrement: Int = AppSettings.DEFAULT_BPM_INCREMENT,
    val isHapticEnabled: Boolean = false,
    val beatPattern: List<BeatOutput> = BeatPattern.DEFAULT,
    val currentBeat: Int = NO_BEAT
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
        const val MIN_BEATS = 1
        const val MAX_BEATS = 12
        const val NO_BEAT = -1

        val BPM_VALUES = listOf(
            30, 35, 40, 45,
            50, 55, 60, 65,
            70, 75, 80, 85,
            90, 95, 100, 110,
            120, 130, 140, 150
        )
    }
}
