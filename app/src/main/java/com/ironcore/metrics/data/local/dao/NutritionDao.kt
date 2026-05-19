package com.ironcore.metrics.data.local.dao

import androidx.room.*
import com.ironcore.metrics.data.local.entities.Meal
import com.ironcore.metrics.data.local.entities.FoodEntry
import com.ironcore.metrics.data.local.entities.HydrationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {
    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<Meal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(foodEntry: FoodEntry)

    @Query("SELECT SUM(totalCalories) FROM meals WHERE timestamp >= :startTime")
    fun getConsumedCaloriesSince(startTime: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHydration(hydrationLog: HydrationLog)

    @Query("SELECT SUM(amountMl) FROM hydration_logs WHERE timestamp >= :startTime")
    fun getHydrationSince(startTime: Long): Flow<Int?>
}
