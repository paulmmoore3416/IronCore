package com.ironcore.metrics.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meals",
    indices = [Index(value = ["timestamp"], name = "idx_meal_timestamp")]
)
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g., Breakfast, Post-Workout
    val timestamp: Long = System.currentTimeMillis(),
    val totalCalories: Int = 0,
    val proteinGrams: Float = 0f,
    val carbGrams: Float = 0f,
    val fatGrams: Float = 0f,
    val cuisine: String? = null,
    val ingredients: String? = null, // JSON string of ingredients
    val cookingInstructions: String? = null,
    val prepTime: String? = null,
    val difficulty: String? = null
)

@Entity(
    tableName = "food_entries",
    indices = [Index(value = ["mealId"], name = "idx_food_entry_meal_id")]
)
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
