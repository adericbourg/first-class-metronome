package dev.dericbourg.firstclassmetronome.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.dericbourg.firstclassmetronome.data.model.EventType

@Entity(tableName = "practice_events")
data class PracticeEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val eventType: EventType
)
