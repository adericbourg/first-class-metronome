package dev.dericbourg.firstclassmetronone.data.converter

import androidx.room.TypeConverter
import dev.dericbourg.firstclassmetronone.data.model.EventType

class EventTypeConverter {
    @TypeConverter
    fun fromEventType(eventType: EventType): String = eventType.name

    @TypeConverter
    fun toEventType(value: String): EventType = EventType.valueOf(value)
}
