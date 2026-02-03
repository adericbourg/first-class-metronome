package dev.dericbourg.firstclassmetronone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.dericbourg.firstclassmetronone.data.entity.PracticeSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeSessionDao {
    @Insert
    suspend fun insert(session: PracticeSessionEntity): Long

    @Insert
    suspend fun insertAll(sessions: List<PracticeSessionEntity>)

    @Query("SELECT * FROM practice_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<PracticeSessionEntity>>

    @Query("SELECT * FROM practice_sessions ORDER BY startTime DESC")
    suspend fun getAllSessionsOnce(): List<PracticeSessionEntity>

    @Query("DELETE FROM practice_sessions")
    suspend fun deleteAll()
}
