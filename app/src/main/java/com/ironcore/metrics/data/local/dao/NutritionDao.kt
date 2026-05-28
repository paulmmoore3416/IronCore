package com.ironcore.metrics.data.local.dao

import androidx.room.*
import com.ironcore.metrics.data.local.entities.Meal
import com.ironcore.metrics.data.local.entities.FoodEntry
import com.ironcore.metrics.data.local.entities.HydrationLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface NutritionDao {
    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<Meal>>

    @Transaction
    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMealsDistinct(): Flow<List<Meal>> = getAllMeals().distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long

    @Update
    suspend fun updateMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Query("DELETE FROM meals")
    suspend fun deleteAllMeals()

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: Long): Meal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(foodEntry: FoodEntry)

    @Query("SELECT SUM(totalCalories) FROM meals WHERE timestamp >= :startTime")
    fun getConsumedCaloriesSince(startTime: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHydration(hydrationLog: HydrationLog)

    @Query("SELECT SUM(amountMl) FROM hydration_logs WHERE timestamp >= :startTime")
    fun getHydrationSince(startTime: Long): Flow<Int?>
}
