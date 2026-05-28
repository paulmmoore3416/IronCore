package com.ironcore.metrics.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ironcore.metrics.data.remote.HomelabApiService
import com.ironcore.metrics.data.remote.dto.OllamaGenerateRequest
import com.ironcore.metrics.data.remote.dto.RemoteExercise
import com.ironcore.metrics.data.remote.dto.RemoteWorkoutDay
import com.ironcore.metrics.data.remote.dto.RemoteWorkoutRoutine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpecializedWorkoutViewModel @Inject constructor(
    private val apiService: HomelabApiService
) : ViewModel() {

    private val _routine = MutableStateFlow<RemoteWorkoutRoutine?>(null)
    val routine = _routine.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val gson = Gson()

    fun loadRoutine(modality: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val prompt = """
                    Generate a highly advanced, professional 7-day training routine for $modality.
                    Format the output ONLY as a valid JSON object matching this structure exactly:
                    {
                        "modality": "$modality",
                        "days": [
                            {
                                "dayNumber": 1,
                                "focus": "string",
                                "exercises": [
                                    { "name": "string", "sets": int, "reps": "string", "rest": "string", "weight": "string (optional)" }
                                ]
                            }
                        ]
                    }
                    Provide exactly 7 days.
                """.trimIndent()

                val request = OllamaGenerateRequest(
                    model = "granite-code:3b",
                    prompt = prompt,
                    format = "json"
                )

                val response = try {
                    apiService.generateWithGranite(request)
                } catch (e: Exception) {
                    _routine.value = getSmartFallbackRoutine(modality)
                    _errorMessage.value = "Homelab unreachable. Loaded locally cached elite routine."
                    return@launch
                }

                try {
                    val jsonContent = response.response.trim()
                    val startIndex = jsonContent.indexOf("{")
                    val endIndex = jsonContent.lastIndexOf("}")
                    
                    val cleanJson = if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                        jsonContent.substring(startIndex, endIndex + 1)
                    } else {
                        jsonContent
                    }
                    
                    val generatedRoutine = gson.fromJson(cleanJson, RemoteWorkoutRoutine::class.java)
                    if (generatedRoutine.days.isNotEmpty()) {
                        _routine.value = generatedRoutine
                    } else {
                        throw Exception("Empty routine generated")
                    }
                } catch (e: Exception) {
                    _routine.value = getSmartFallbackRoutine(modality)
                    _errorMessage.value = "AI formatting issue. Using optimized local template."
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "Generation error: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getSmartFallbackRoutine(modality: String): RemoteWorkoutRoutine {
        val days = mutableListOf<RemoteWorkoutDay>()
        for (i in 1..7) {
            val focus = if (i % 2 == 0) "Active Recovery" else "High Intensity"
            val exercises = mutableListOf<RemoteExercise>()
            if (focus == "High Intensity") {
                exercises.add(RemoteExercise("Main Compound for $modality", 4, "8-10", "120s", "Depends on RM"))
                exercises.add(RemoteExercise("Secondary Movement", 3, "10-12", "90s"))
                exercises.add(RemoteExercise("Accessory Work", 3, "15", "60s"))
            } else {
                exercises.add(RemoteExercise("Light Mobility", 1, "15 mins", "N/A"))
                exercises.add(RemoteExercise("Stretching", 1, "10 mins", "N/A"))
            }
            days.add(RemoteWorkoutDay(i, focus, exercises))
        }
        return RemoteWorkoutRoutine(modality, days)
    }

    fun updateExercise(dayNumber: Int, exerciseIndex: Int, updatedExercise: RemoteExercise) {
        val currentRoutine = _routine.value ?: return
        val newDays = currentRoutine.days.map { day ->
            if (day.dayNumber == dayNumber) {
                val newExercises = day.exercises.toMutableList()
                newExercises[exerciseIndex] = updatedExercise
                day.copy(exercises = newExercises)
            } else {
                day
            }
        }
        _routine.value = currentRoutine.copy(days = newDays)
    }
}
