package dev.dericbourg.firstclassmetronome.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.dericbourg.firstclassmetronome.data.AppDatabase
import dev.dericbourg.firstclassmetronome.data.dao.PracticeEventDao
import dev.dericbourg.firstclassmetronome.data.dao.PracticeSessionDao
import dev.dericbourg.firstclassmetronome.data.repository.PracticeRepository
import dev.dericbourg.firstclassmetronome.data.repository.PracticeRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun providePracticeEventDao(database: AppDatabase): PracticeEventDao {
        return database.practiceEventDao()
    }

    @Provides
    fun providePracticeSessionDao(database: AppDatabase): PracticeSessionDao {
        return database.practiceSessionDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPracticeRepository(impl: PracticeRepositoryImpl): PracticeRepository
}
