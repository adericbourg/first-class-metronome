package dev.dericbourg.firstclassmetronone.presentation.beatselection

data class BeatSelectionState(
    val selectedBpm: Int = DEFAULT_BPM,
    val availableBpmValues: List<Int> = BPM_VALUES,
    val isPlaying: Boolean = false
) {
    val isOnGrid: Boolean
        get() = selectedBpm in availableBpmValues

    val canDecreaseBpm: Boolean
        get() = selectedBpm - BPM_SHIFT_AMOUNT >= MIN_BPM

    val canIncreaseBpm: Boolean
        get() = selectedBpm + BPM_SHIFT_AMOUNT <= MAX_BPM

    companion object {
        const val DEFAULT_BPM = 60
        const val GRID_COLUMNS = 4
        const val MIN_BPM = 20
        const val MAX_BPM = 300
        const val BPM_SHIFT_AMOUNT = 5

        val BPM_VALUES = listOf(
            30, 35, 40, 45,
            50, 55, 60, 65,
            70, 75, 80, 85,
            90, 95, 100, 110,
            120, 130, 140, 150
        )
    }
}
