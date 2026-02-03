package dev.dericbourg.firstclassmetronone.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.dericbourg.firstclassmetronone.data.converter.EventTypeConverter
import dev.dericbourg.firstclassmetronone.data.dao.PracticeEventDao
import dev.dericbourg.firstclassmetronone.data.dao.PracticeSessionDao
import dev.dericbourg.firstclassmetronone.data.entity.PracticeEventEntity
import dev.dericbourg.firstclassmetronone.data.entity.PracticeSessionEntity

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
