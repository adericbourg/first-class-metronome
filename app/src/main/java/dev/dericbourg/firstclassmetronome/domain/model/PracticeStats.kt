package dev.dericbourg.firstclassmetronome.domain.model

data class PracticeStats(
    val last7Days: PeriodStats = PeriodStats(),
    val last30Days: PeriodStats = PeriodStats(),
    val allTime: PeriodStats = PeriodStats()
) {
    val totalSessionCount: Int get() = allTime.sessionCount

    companion object {
        val EMPTY = PracticeStats()
    }
}

data class PeriodStats(
    val totalDurationMs: Long = 0,
    val daysWithPractice: Int = 0,
    val sessionCount: Int = 0
) {
    val averagePerSession: Long
        get() = if (sessionCount > 0) totalDurationMs / sessionCount else 0

    val averagePerDay: Long
        get() = if (daysWithPractice > 0) totalDurationMs / daysWithPractice else 0

    val durationFormatted: String get() = formatDuration(totalDurationMs)
    val avgPerSessionFormatted: String get() = formatDuration(averagePerSession)
    val avgPerDayFormatted: String get() = formatDuration(averagePerDay)

    private fun formatDuration(durationMs: Long): String {
        if (durationMs == 0L) return "0m"

        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }
}
