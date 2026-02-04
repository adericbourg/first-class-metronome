package dev.dericbourg.firstclassmetronome.domain

import dev.dericbourg.firstclassmetronome.data.entity.PracticeEventEntity
import dev.dericbourg.firstclassmetronome.data.model.EventType
import dev.dericbourg.firstclassmetronome.domain.model.PracticeSession
import javax.inject.Inject

class SessionComputer @Inject constructor() {

    fun computeSessions(events: List<PracticeEventEntity>): List<PracticeSession> {
        if (events.isEmpty()) return emptyList()

        val sortedEvents = events.sortedBy { it.timestamp }
        val sessions = mutableListOf<PracticeSession>()

        var sessionStart: Long? = null
        var lastStartTime: Long? = null
        var lastStopTime: Long? = null
        var totalDuration = 0L

        for (event in sortedEvents) {
            when (event.eventType) {
                EventType.START -> {
                    val gapFromLastStop = lastStopTime?.let { event.timestamp - it }

                    if (gapFromLastStop != null && gapFromLastStop >= SESSION_GAP_THRESHOLD_MS) {
                        if (sessionStart != null && lastStopTime != null && totalDuration > 0) {
                            sessions.add(
                                PracticeSession(
                                    startTime = sessionStart,
                                    endTime = lastStopTime,
                                    durationMs = totalDuration
                                )
                            )
                        }
                        sessionStart = event.timestamp
                        totalDuration = 0
                    } else if (gapFromLastStop != null && gapFromLastStop < SHORT_PAUSE_THRESHOLD_MS) {
                        totalDuration += gapFromLastStop
                    }

                    if (sessionStart == null) {
                        sessionStart = event.timestamp
                    }
                    lastStartTime = event.timestamp
                }
                EventType.STOP -> {
                    lastStartTime?.let { start ->
                        totalDuration += event.timestamp - start
                    }
                    lastStopTime = event.timestamp
                    lastStartTime = null
                }
            }
        }

        if (sessionStart != null && lastStopTime != null && totalDuration > 0) {
            sessions.add(
                PracticeSession(
                    startTime = sessionStart,
                    endTime = lastStopTime,
                    durationMs = totalDuration
                )
            )
        }

        return sessions
    }

    companion object {
        const val SHORT_PAUSE_THRESHOLD_MS = 10 * 60 * 1000L
        const val SESSION_GAP_THRESHOLD_MS = 60 * 60 * 1000L
        const val COMPACTION_THRESHOLD_MS = 30 * 24 * 60 * 60 * 1000L
    }
}
