package dev.dericbourg.firstclassmetronome.data.converter

import androidx.room.TypeConverter
import dev.dericbourg.firstclassmetronome.data.model.EventType

class EventTypeConverter {
    @TypeConverter
    fun fromEventType(eventType: EventType): String = eventType.name

    @TypeConverter
    fun toEventType(value: String): EventType = EventType.valueOf(value)
}
