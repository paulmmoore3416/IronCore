package com.ironcore.metrics.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ironcore.metrics.data.remote.HomelabApiService
import com.ironcore.metrics.data.remote.dto.OllamaGenerateRequest
import com.ironcore.metrics.data.remote.dto.RemoteMeal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val apiService: HomelabApiService
) : ViewModel() {

    private val _meals = MutableStateFlow<List<RemoteMeal>>(emptyList())
    val meals = _meals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val gson = Gson()

    fun generatePlan() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Prompting the Granite model to strictly return JSON matching our RemoteMeal structure.
                val prompt = """
                    Generate a daily meal plan with a target of 2500 calories and 200g protein.
                    Return ONLY a JSON array of objects. Each object must have these exact keys:
                    "name" (string), "calories" (int), "protein" (float), "carbs" (float), "fat" (float), "ingredients" (array of strings).
                """.trimIndent()

                val request = OllamaGenerateRequest(
                    model = "granite-code:3b", // Set to your local granite model
                    prompt = prompt,
                    format = "json"
                )

                val response = apiService.generateWithGranite(request)
                
                // Parse the JSON string returned by Ollama into our Kotlin list
                val listType = object : TypeToken<List<RemoteMeal>>() {}.type
                val generatedMeals: List<RemoteMeal> = gson.fromJson(response.response, listType)
                
                _meals.value = generatedMeals
            } catch (e: Exception) {
                // In a production app, we would emit an error state here.
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
