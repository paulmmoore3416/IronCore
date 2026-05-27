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
        FoodEntry::class,
        HydrationLog::class,
        WellnessLog::class
    ],
    version = 2,
    exportSchema = false
)
abstract class IronCoreDatabase : RoomDatabase() {
    abstract fun workoutDao(): com.ironcore.metrics.data.local.dao.WorkoutDao
    abstract fun nutritionDao(): com.ironcore.metrics.data.local.dao.NutritionDao
    abstract fun userProfileDao(): com.ironcore.metrics.data.local.dao.UserProfileDao
    abstract fun wellnessDao(): com.ironcore.metrics.data.local.dao.WellnessDao
}
