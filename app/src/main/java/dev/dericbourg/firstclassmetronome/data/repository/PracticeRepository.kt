package dev.dericbourg.firstclassmetronome.data.repository

import androidx.room.withTransaction
import dev.dericbourg.firstclassmetronome.data.AppDatabase
import dev.dericbourg.firstclassmetronome.data.dao.PracticeEventDao
import dev.dericbourg.firstclassmetronome.data.dao.PracticeSessionDao
import dev.dericbourg.firstclassmetronome.data.entity.PracticeEventEntity
import dev.dericbourg.firstclassmetronome.data.entity.PracticeSessionEntity
import dev.dericbourg.firstclassmetronome.data.model.EventType
import dev.dericbourg.firstclassmetronome.domain.SessionComputer
import dev.dericbourg.firstclassmetronome.domain.model.PracticeSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeRepository @Inject constructor(
    private val database: AppDatabase,
    private val eventDao: PracticeEventDao,
    private val sessionDao: PracticeSessionDao,
    private val sessionComputer: SessionComputer
) {

    suspend fun recordStart() = record(EventType.START)

    suspend fun recordStop() = record(EventType.STOP)

    private suspend fun record(eventType: EventType) {
        eventDao.insert(PracticeEventEntity(timestamp = System.currentTimeMillis(), eventType = eventType))
    }

    fun getAllSessions(): Flow<List<PracticeSession>> {
        return combine(
            eventDao.getAllEvents(),
            sessionDao.getAllSessions()
        ) { events, compactedSessions ->
            val computedSessions = sessionComputer.computeSessions(events)
            val fromCompacted = compactedSessions.map { entity ->
                PracticeSession(
                    startTime = entity.startTime,
                    endTime = entity.endTime,
                    durationMs = entity.durationMs
                )
            }
            (computedSessions + fromCompacted).sortedByDescending { it.startTime }
        }
    }

    suspend fun clearAllData() {
        database.withTransaction {
            eventDao.deleteAll()
            sessionDao.deleteAll()
        }
    }

    suspend fun compactOldEvents() {
        val cutoffTime = System.currentTimeMillis() - SessionComputer.COMPACTION_THRESHOLD_MS
        val oldEvents = eventDao.getEventsBefore(cutoffTime)

        if (oldEvents.isEmpty()) return

        val sessions = sessionComputer.computeSessions(oldEvents)
        if (sessions.isEmpty()) return

        database.withTransaction {
            sessionDao.insertAll(
                sessions.map { session ->
                    PracticeSessionEntity(
                        startTime = session.startTime,
                        endTime = session.endTime,
                        durationMs = session.durationMs
                    )
                }
            )
            eventDao.deleteByIds(oldEvents.map { it.id })
        }
    }

    suspend fun fixUnterminatedSession() {
        val lastEvent = eventDao.getLastEvent() ?: return

        if (lastEvent.eventType == EventType.START) {
            eventDao.insert(
                PracticeEventEntity(
                    timestamp = lastEvent.timestamp + ESTIMATED_SESSION_DURATION_MS,
                    eventType = EventType.STOP
                )
            )
        }
    }

    companion object {
        private const val ESTIMATED_SESSION_DURATION_MS = 5 * 60 * 1000L
    }
}
