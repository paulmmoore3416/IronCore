package com.ironcore.metrics.data.remote.dto

data class MealPlanRequest(
    val userId: Int,
    val targetCalories: Int,
    val proteinGrams: Float,
    val preferences: List<String>
)

data class MealPlanResponse(
    val dayPlan: String, // Markdown or structured JSON
    val meals: List<RemoteMeal>
)

data class RemoteMeal(
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val ingredients: List<String>,
    val cookingInstructions: String? = null,
    val prepTime: String? = null,
    val difficulty: String? = null
)
