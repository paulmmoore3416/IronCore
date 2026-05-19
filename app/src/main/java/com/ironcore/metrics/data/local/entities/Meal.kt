package com.ironcore.metrics.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g., Breakfast, Post-Workout
    val timestamp: Long = System.currentTimeMillis(),
    val totalCalories: Int = 0,
    val proteinGrams: Float = 0f,
    val carbGrams: Float = 0f,
    val fatGrams: Float = 0f
)

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mealId: Long,
    val foodName: String,
    val quantity: Float,
    val unit: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)
