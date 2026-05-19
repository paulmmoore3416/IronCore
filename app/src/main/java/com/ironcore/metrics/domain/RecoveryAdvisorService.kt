package com.ironcore.metrics.domain

import android.util.Log
import com.ironcore.metrics.data.remote.HomelabApiService
import com.ironcore.metrics.data.remote.dto.OllamaGenerateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service that provides AI-powered recovery advice using Ollama/Granite model.
 */
@Singleton
class RecoveryAdvisorService @Inject constructor(
    private val homelabApiService: HomelabApiService
) {
    companion object {
        private const val TAG = "RecoveryAdvisorService"
        private const val MODEL_NAME = "granite-code:3b"
    }

    /**
     * Generates personalized recovery advice based on user metrics.
     * 
     * @param steps Daily step count
     * @param heartRate Current/latest heart rate
     * @param hydrationMl Hydration intake in ml
     * @param activeCalories Active calories burned
     * @param consumedCalories Calories consumed
     * @param recoveryScore Current recovery score (0-100)
     * @return AI-generated recovery advice or null if request fails
     */
    suspend fun getRecoveryAdvice(
        steps: Long,
        heartRate: Long,
        hydrationMl: Int,
        activeCalories: Double,
        consumedCalories: Int,
        recoveryScore: Int
    ): RecoveryAdvice? = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPrompt(
                steps, heartRate, hydrationMl, 
                activeCalories, consumedCalories, recoveryScore
            )
            
            Log.d(TAG, "Requesting recovery advice from AI model")
            
            val request = OllamaGenerateRequest(
                model = MODEL_NAME,
                prompt = prompt,
                stream = false,
                format = "json"
            )
            
            val response = homelabApiService.generateWithGranite(request)
            
            Log.d(TAG, "Received AI response: ${response.response}")
            
            parseRecoveryAdvice(response.response)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recovery advice from AI", e)
            // Return fallback advice based on recovery score
            getFallbackAdvice(recoveryScore)
        }
    }

    /**
     * Builds a structured prompt for the AI model.
     */
    private fun buildPrompt(
        steps: Long,
        heartRate: Long,
        hydrationMl: Int,
        activeCalories: Double,
        consumedCalories: Int,
        recoveryScore: Int
    ): String {
        return """
You are a fitness recovery advisor. Analyze the following user metrics and provide personalized recovery advice.

User Metrics:
- Steps: $steps
- Heart Rate: $heartRate BPM
- Hydration: $hydrationMl ml (target: 2000 ml)
- Active Calories Burned: ${activeCalories.toInt()} kcal
- Calories Consumed: $consumedCalories kcal
- Recovery Score: $recoveryScore/100

Provide your response in the following JSON format:
{
  "advice": "Brief, actionable recovery advice (2-3 sentences)",
  "priority": "high|medium|low",
  "recommendations": [
    "Specific recommendation 1",
    "Specific recommendation 2",
    "Specific recommendation 3"
  ]
}

Focus on:
1. Hydration status and recommendations
2. Energy balance (calories in vs out)
3. Activity level and rest needs
4. Heart rate recovery

Keep advice practical, encouraging, and specific to the metrics provided.
        """.trimIndent()
    }

    /**
     * Parses the AI response into a RecoveryAdvice object.
     */
    private fun parseRecoveryAdvice(jsonResponse: String): RecoveryAdvice? {
        return try {
            val json = JSONObject(jsonResponse)
            RecoveryAdvice(
                advice = json.getString("advice"),
                priority = json.getString("priority"),
                recommendations = buildList {
                    val recsArray = json.getJSONArray("recommendations")
                    for (i in 0 until recsArray.length()) {
                        add(recsArray.getString(i))
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing AI response", e)
            null
        }
    }

    /**
     * Provides fallback advice when AI is unavailable.
     */
    private fun getFallbackAdvice(recoveryScore: Int): RecoveryAdvice {
        return when {
            recoveryScore >= 80 -> RecoveryAdvice(
                advice = "Excellent recovery! Your body is well-rested and ready for intense training.",
                priority = "low",
                recommendations = listOf(
                    "Consider a challenging workout today",
                    "Maintain your current hydration and nutrition habits",
                    "Focus on progressive overload in your training"
                )
            )
            recoveryScore >= 60 -> RecoveryAdvice(
                advice = "Good recovery status. You're ready for moderate activity with proper warm-up.",
                priority = "medium",
                recommendations = listOf(
                    "Stick to moderate intensity workouts",
                    "Ensure adequate hydration throughout the day",
                    "Get 7-9 hours of sleep tonight"
                )
            )
            recoveryScore >= 40 -> RecoveryAdvice(
                advice = "Moderate recovery. Consider light activity and focus on rest and nutrition.",
                priority = "medium",
                recommendations = listOf(
                    "Opt for light cardio or stretching today",
                    "Increase water intake to 2+ liters",
                    "Prioritize sleep and stress management"
                )
            )
            else -> RecoveryAdvice(
                advice = "Low recovery detected. Prioritize rest, hydration, and recovery activities today.",
                priority = "high",
                recommendations = listOf(
                    "Take a rest day or do very light activity only",
                    "Focus on hydration - aim for 2.5+ liters of water",
                    "Consider a recovery meal with adequate protein and carbs",
                    "Ensure 8+ hours of quality sleep"
                )
            )
        }
    }
}

/**
 * Data class representing recovery advice from the AI.
 */
data class RecoveryAdvice(
    val advice: String,
    val priority: String,
    val recommendations: List<String>
)
