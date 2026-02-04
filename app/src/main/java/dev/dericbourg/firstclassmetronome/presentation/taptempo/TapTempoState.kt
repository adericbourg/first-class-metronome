package dev.dericbourg.firstclassmetronome.presentation.taptempo

import kotlin.math.roundToInt

data class TapTempoState(
    val isVisible: Boolean = false,
    val tapTimestamps: List<Long> = emptyList(),
    val calculatedBpm: Int? = null,
    val wasPlayingBeforeOpen: Boolean = false,
    val lastTapTime: Long = 0L
) {
    val canApply: Boolean
        get() = tapTimestamps.size >= MIN_TAPS_TO_APPLY

    companion object {
        const val MIN_TAPS_TO_APPLY = 2
        const val MAX_INTERVALS_FOR_AVERAGE = 5
        const val TIMEOUT_MS = 5000L

        fun calculateBpm(timestamps: List<Long>): Int? {
            if (timestamps.size < 2) return null

            val intervals = timestamps.zipWithNext { a, b -> b - a }
            val recentIntervals = intervals.takeLast(MAX_INTERVALS_FOR_AVERAGE)
            val averageMs = recentIntervals.average()

            return (60_000 / averageMs).roundToInt()
        }
    }
}
