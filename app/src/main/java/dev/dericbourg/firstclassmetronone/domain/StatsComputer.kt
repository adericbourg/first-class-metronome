package dev.dericbourg.firstclassmetronone.domain

import dev.dericbourg.firstclassmetronone.domain.model.PeriodStats
import dev.dericbourg.firstclassmetronone.domain.model.PracticeSession
import dev.dericbourg.firstclassmetronone.domain.model.PracticeStats
import java.time.ZoneId
import javax.inject.Inject

class StatsComputer @Inject constructor() {

    fun computeStats(
        sessions: List<PracticeSession>,
        now: Long = System.currentTimeMillis()
    ): PracticeStats {
        if (sessions.isEmpty()) return PracticeStats.EMPTY

        val today = java.time.Instant.ofEpochMilli(now)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val sevenDaysAgo = today.minusDays(7)
        val thirtyDaysAgo = today.minusDays(30)

        var last7DaysMs = 0L
        var last30DaysMs = 0L
        var allTimeMs = 0L

        val last7DaysDates = mutableSetOf<java.time.LocalDate>()
        val last30DaysDates = mutableSetOf<java.time.LocalDate>()
        val allTimeDates = mutableSetOf<java.time.LocalDate>()

        var last7DaysCount = 0
        var last30DaysCount = 0
        var allTimeCount = 0

        for (session in sessions) {
            val sessionDate = session.date
            allTimeMs += session.durationMs
            allTimeDates.add(sessionDate)
            allTimeCount++

            if (!sessionDate.isBefore(thirtyDaysAgo)) {
                last30DaysMs += session.durationMs
                last30DaysDates.add(sessionDate)
                last30DaysCount++
            }
            if (!sessionDate.isBefore(sevenDaysAgo)) {
                last7DaysMs += session.durationMs
                last7DaysDates.add(sessionDate)
                last7DaysCount++
            }
        }

        return PracticeStats(
            last7Days = PeriodStats(
                totalDurationMs = last7DaysMs,
                daysWithPractice = last7DaysDates.size,
                sessionCount = last7DaysCount
            ),
            last30Days = PeriodStats(
                totalDurationMs = last30DaysMs,
                daysWithPractice = last30DaysDates.size,
                sessionCount = last30DaysCount
            ),
            allTime = PeriodStats(
                totalDurationMs = allTimeMs,
                daysWithPractice = allTimeDates.size,
                sessionCount = allTimeCount
            )
        )
    }
}
