package com.ironcore.metrics.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ironcore.metrics.data.local.entities.*

@Database(
    entities = [
        UserProfile::class,
        Exercise::class,
        Workout::class,
        WorkoutSet::class,
        Meal::class,
        FoodEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class IronCoreDatabase : RoomDatabase() {
    abstract fun workoutDao(): com.ironcore.metrics.data.local.dao.WorkoutDao
}
