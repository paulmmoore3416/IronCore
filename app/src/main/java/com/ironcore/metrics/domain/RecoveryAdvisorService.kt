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
        recoveryScore: Int,
        sleepStages: com.ironcore.metrics.data.health.SleepStages? = null
    ): RecoveryAdvice? = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPrompt(
                steps, heartRate, hydrationMl, 
                activeCalories, consumedCalories, recoveryScore,
                sleepStages
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
        recoveryScore: Int,
        sleepStages: com.ironcore.metrics.data.health.SleepStages? = null
    ): String {
        return """
[System Instructions]
You are a high-performance fitness recovery advisor for the IronCore Metrics ecosystem. 
Your goal is to provide precise, actionable, and data-driven recovery advice.
Respond ONLY with a valid JSON object. Do not include any preamble or postamble.

[User Metrics]
- Steps: ${if (steps > 0) steps else "No data"}
- Heart Rate: ${if (heartRate > 0) "$heartRate BPM" else "No data"}
- Hydration: $hydrationMl ml (target: 2000 ml)
- Active Calories Burned: ${if (activeCalories > 0) "${activeCalories.toInt()} kcal" else "No data"}
- Calories Consumed: ${if (consumedCalories > 0) "$consumedCalories kcal" else "No data"}
- Recovery Score: $recoveryScore/100
- Sleep Quality: ${if (sleepStages != null) "Deep: ${sleepStages.deepMinutes}m, REM: ${sleepStages.remMinutes}m, Light: ${sleepStages.lightMinutes}m" else "No data"}

[Constraints]
1. If steps are 0, suggest light movement to stimulate recovery.
2. If HR is missing, focus advice on hydration and perceived exertion.
3. If hydration is < 1500ml, prioritize water intake.
4. If calorie balance is negative, suggest nutrient-dense snacks.
5. If deep sleep is < 60m, emphasize rest and possible overtraining.
6. Keep advice professional, direct, and under 50 words.

[Response Format]
{
  "advice": "Main advice text",
  "priority": "high|medium|low",
  "recommendations": ["Rec 1", "Rec 2", "Rec 3"]
}
        """.trimIndent()
    }

    /**
     * Parses the AI response into a RecoveryAdvice object.
     * Extracts JSON from potential markdown blocks.
     */
    private fun parseRecoveryAdvice(response: String): RecoveryAdvice? {
        return try {
            // Extract JSON if AI wraps it in markdown blocks
            val jsonString = if (response.contains("```json")) {
                response.substringAfter("```json").substringBefore("```")
            } else if (response.contains("```")) {
                response.substringAfter("```").substringBefore("```")
            } else {
                response
            }.trim()

            val json = JSONObject(jsonString)
            RecoveryAdvice(
                advice = json.optString("advice", "Recovery data processed."),
                priority = json.optString("priority", "medium"),
                recommendations = buildList {
                    val recsArray = json.optJSONArray("recommendations")
                    if (recsArray != null) {
                        for (i in 0 until recsArray.length()) {
                            add(recsArray.getString(i))
                        }
                    } else {
                        add("Continue monitoring your vitals.")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing AI response: $response", e)
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
