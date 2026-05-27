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
 * Service that generates personalized workout plans using AI.
 */
@Singleton
class WorkoutPlanService @Inject constructor(
    private val homelabApiService: HomelabApiService
) {
    companion object {
        private const val TAG = "WorkoutPlanService"
        private const val MODEL_NAME = "granite-code:3b"
    }

    /**
     * Generates a workout plan based on recovery score and fitness goal.
     */
    suspend fun generateWorkoutPlan(
        recoveryScore: Int,
        fitnessGoal: String,
        lastWorkoutName: String? = null
    ): WorkoutPlan? = withContext(Dispatchers.IO) {
        try {
            val prompt = """
[System Instructions]
You are a professional strength and conditioning coach.
Generate a high-intensity workout plan optimized for the user's current recovery state.
Respond ONLY with a valid JSON object.

[User Context]
- Recovery Readiness: $recoveryScore/100
- Primary Goal: $fitnessGoal
- Previous Session: ${lastWorkoutName ?: "None"}

[Constraints]
1. If recovery < 50, focus on mobility and active recovery.
2. If recovery > 80, suggest a "Personal Record" (PR) attempt or high volume.
3. Include exactly 4 exercises with sets, reps, and target intensity.
4. Keep exercise names standard (e.g., "Deadlift", "Bench Press").

[Response Format]
{
  "planName": "Workout Name",
  "exercises": [
    { "name": "Ex 1", "sets": 3, "reps": "8-10", "intensity": "70% 1RM" },
    ...
  ],
  "coachingTip": "One sentence motivation"
}
            """.trimIndent()

            val request = OllamaGenerateRequest(
                model = MODEL_NAME,
                prompt = prompt,
                stream = false,
                format = "json"
            )

            val response = homelabApiService.generateWithGranite(request)
            parseWorkoutPlan(response.response)
        } catch (e: Exception) {
            Log.e(TAG, "Error generating workout plan", e)
            null
        }
    }

    private fun parseWorkoutPlan(response: String): WorkoutPlan? {
        return try {
            val jsonString = if (response.contains("```json")) {
                response.substringAfter("```json").substringBefore("```")
            } else response.trim()

            val json = JSONObject(jsonString)
            WorkoutPlan(
                planName = json.getString("planName"),
                exercises = buildList {
                    val array = json.getJSONArray("exercises")
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        add(WorkoutExercise(
                            name = obj.getString("name"),
                            sets = obj.getInt("sets"),
                            reps = obj.getString("reps"),
                            intensity = obj.getString("intensity")
                        ))
                    }
                },
                coachingTip = json.getString("coachingTip")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing workout plan", e)
            null
        }
    }
}

data class WorkoutPlan(
    val planName: String,
    val exercises: List<WorkoutExercise>,
    val coachingTip: String
)

data class WorkoutExercise(
    val name: String,
    val sets: Int,
    val reps: String,
    val intensity: String
)
