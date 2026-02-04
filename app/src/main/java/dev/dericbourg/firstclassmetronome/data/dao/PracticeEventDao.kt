package dev.dericbourg.firstclassmetronome.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.dericbourg.firstclassmetronome.data.entity.PracticeEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeEventDao {
    @Insert
    suspend fun insert(event: PracticeEventEntity): Long

    @Query("SELECT * FROM practice_events ORDER BY timestamp ASC")
    fun getAllEvents(): Flow<List<PracticeEventEntity>>

    @Query("SELECT * FROM practice_events ORDER BY timestamp ASC")
    suspend fun getAllEventsOnce(): List<PracticeEventEntity>

    @Query("SELECT * FROM practice_events WHERE timestamp < :beforeTimestamp ORDER BY timestamp ASC")
    suspend fun getEventsBefore(beforeTimestamp: Long): List<PracticeEventEntity>

    @Query("SELECT * FROM practice_events ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastEvent(): PracticeEventEntity?

    @Query("DELETE FROM practice_events WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("DELETE FROM practice_events")
    suspend fun deleteAll()
}
