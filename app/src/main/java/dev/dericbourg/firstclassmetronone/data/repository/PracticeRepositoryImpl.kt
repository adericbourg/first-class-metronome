package dev.dericbourg.firstclassmetronone.data.repository

import androidx.room.withTransaction
import dev.dericbourg.firstclassmetronone.data.AppDatabase
import dev.dericbourg.firstclassmetronone.data.dao.PracticeEventDao
import dev.dericbourg.firstclassmetronone.data.dao.PracticeSessionDao
import dev.dericbourg.firstclassmetronone.data.entity.PracticeEventEntity
import dev.dericbourg.firstclassmetronone.data.entity.PracticeSessionEntity
import dev.dericbourg.firstclassmetronone.data.model.EventType
import dev.dericbourg.firstclassmetronone.domain.SessionComputer
import dev.dericbourg.firstclassmetronone.domain.StatsComputer
import dev.dericbourg.firstclassmetronone.domain.model.PracticeSession
import dev.dericbourg.firstclassmetronone.domain.model.PracticeStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val eventDao: PracticeEventDao,
    private val sessionDao: PracticeSessionDao,
    private val sessionComputer: SessionComputer,
    private val statsComputer: StatsComputer
) : PracticeRepository {

    override suspend fun recordStart() {
        eventDao.insert(
            PracticeEventEntity(
                timestamp = System.currentTimeMillis(),
                eventType = EventType.START
            )
        )
    }

    override suspend fun recordStop() {
        eventDao.insert(
            PracticeEventEntity(
                timestamp = System.currentTimeMillis(),
                eventType = EventType.STOP
            )
        )
    }

    override fun getAllSessions(): Flow<List<PracticeSession>> {
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

    override fun getStats(): Flow<PracticeStats> {
        return getAllSessions().map { sessions ->
            statsComputer.computeStats(sessions)
        }
    }

    override suspend fun clearAllData() {
        database.withTransaction {
            eventDao.deleteAll()
            sessionDao.deleteAll()
        }
    }

    override suspend fun compactOldEvents() {
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

    override suspend fun fixUnterminatedSession() {
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
