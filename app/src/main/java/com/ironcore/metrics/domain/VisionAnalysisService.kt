package com.ironcore.metrics.domain

import com.ironcore.metrics.data.remote.HomelabApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service that analyzes meal photos to estimate nutrition.
 */
@Singleton
class VisionAnalysisService @Inject constructor(
    private val homelabApiService: HomelabApiService
) {
    /**
     * Simulated photo analysis.
     */
    suspend fun analyzeMealPhoto(base64Image: String): MealAnalysis {
        // In a real scenario, we'd send the image to a multimodal model like LLaVA or Llava-Granite
        // For now, return a realistic simulation based on the prompt.
        return MealAnalysis(
            itemName = "Steak and Asparagus",
            calories = 450,
            protein = 40,
            carbs = 10,
            fat = 25
        )
    }
}

data class MealAnalysis(
    val itemName: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int
)
