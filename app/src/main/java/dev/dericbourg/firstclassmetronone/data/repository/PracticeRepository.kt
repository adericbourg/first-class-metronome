package dev.dericbourg.firstclassmetronone.data.repository

import dev.dericbourg.firstclassmetronone.domain.model.PracticeSession
import dev.dericbourg.firstclassmetronone.domain.model.PracticeStats
import kotlinx.coroutines.flow.Flow

interface PracticeRepository {
    suspend fun recordStart()
    suspend fun recordStop()
    fun getAllSessions(): Flow<List<PracticeSession>>
    fun getStats(): Flow<PracticeStats>
    suspend fun clearAllData()
    suspend fun compactOldEvents()
    suspend fun fixUnterminatedSession()
}
