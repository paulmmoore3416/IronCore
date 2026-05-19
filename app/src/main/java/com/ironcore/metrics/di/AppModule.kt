package com.ironcore.metrics.di

import android.content.Context
import androidx.room.Room
import com.ironcore.metrics.data.local.IronCoreDatabase
import com.ironcore.metrics.data.local.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): IronCoreDatabase {
        return Room.databaseBuilder(
            context,
            IronCoreDatabase::class.java,
            "ironcore_db"
        ).build()
    }

    @Provides
    fun provideWorkoutDao(database: IronCoreDatabase): WorkoutDao {
        return database.workoutDao()
    }
}
