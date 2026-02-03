package dev.dericbourg.firstclassmetronone.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class PracticeSession(
    val startTime: Long,
    val endTime: Long,
    val durationMs: Long
) {
    val date: LocalDate
        get() = Instant.ofEpochMilli(startTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

    val durationFormatted: String
        get() {
            val totalSeconds = durationMs / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            return when {
                hours > 0 -> "${hours}h ${minutes}m"
                minutes > 0 -> "${minutes}m ${seconds}s"
                else -> "${seconds}s"
            }
        }

    val startTimeFormatted: String
        get() {
            val instant = Instant.ofEpochMilli(startTime)
            val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime()
            return "%02d:%02d".format(localTime.hour, localTime.minute)
        }
}
