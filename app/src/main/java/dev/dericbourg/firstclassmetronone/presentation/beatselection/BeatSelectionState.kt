package dev.dericbourg.firstclassmetronone.presentation.beatselection

data class BeatSelectionState(
    val selectedBpm: Int = DEFAULT_BPM,
    val availableBpmValues: List<Int> = BPM_VALUES
) {
    companion object {
        const val DEFAULT_BPM = 60
        const val GRID_COLUMNS = 4

        val BPM_VALUES = listOf(
            30, 35, 40, 45,
            50, 55, 60, 65,
            70, 75, 80, 85,
            90, 95, 100, 110,
            120, 130, 140, 150
        )
    }
}
