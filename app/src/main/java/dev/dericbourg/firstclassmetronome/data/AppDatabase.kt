package dev.dericbourg.firstclassmetronome.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.dericbourg.firstclassmetronome.data.converter.EventTypeConverter
import dev.dericbourg.firstclassmetronome.data.dao.PracticeEventDao
import dev.dericbourg.firstclassmetronome.data.dao.PracticeSessionDao
import dev.dericbourg.firstclassmetronome.data.entity.PracticeEventEntity
import dev.dericbourg.firstclassmetronome.data.entity.PracticeSessionEntity

@Database(
    entities = [PracticeEventEntity::class, PracticeSessionEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(EventTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun practiceEventDao(): PracticeEventDao
    abstract fun practiceSessionDao(): PracticeSessionDao

    companion object {
        const val DATABASE_NAME = "metronome_db"
    }
}
