package com.ironcore.metrics.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ironcore.metrics.data.remote.HomelabApiService
import com.ironcore.metrics.data.remote.dto.OllamaGenerateRequest
import com.ironcore.metrics.data.remote.dto.RemoteMeal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val apiService: HomelabApiService,
    private val nutritionDao: com.ironcore.metrics.data.local.dao.NutritionDao
) : ViewModel() {

    private val _meals = MutableStateFlow<List<RemoteMeal>>(emptyList())
    val meals = _meals.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val gson = Gson()

    // Mock setting for AI Core vs Remote. In a real app, this comes from DataStore
    var useAiCoreOnDevice = true

    init {
        // Load existing meals from database on startup
        viewModelScope.launch {
            nutritionDao.getAllMeals().collect { dbMeals ->
                if (_meals.value.isEmpty() && dbMeals.isNotEmpty()) {
                    _meals.value = dbMeals.map { meal ->
                        RemoteMeal(
                            name = meal.name,
                            calories = meal.totalCalories,
                            protein = meal.proteinGrams,
                            carbs = meal.carbGrams,
                            fat = meal.fatGrams,
                            ingredients = emptyList()
                        )
                    }
                }
            }
        }
    }

    fun generatePlan() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (useAiCoreOnDevice) {
                    // Simulate fast on-device AICore generation
                    delay(800) 
                    _meals.value = listOf(
                        RemoteMeal("AICore Oatmeal", 450, 20.0f, 60.0f, 10.0f, listOf("Oats", "Milk", "Protein Powder")),
                        RemoteMeal("AICore Chicken Salad", 600, 50.0f, 20.0f, 25.0f, listOf("Chicken", "Greens", "Olive Oil")),
                        RemoteMeal("AICore Steak & Rice", 800, 60.0f, 80.0f, 30.0f, listOf("Steak", "Rice", "Broccoli"))
                    )
                    
                    // Persist generated meals to database
                    saveMealsToDatabase(_meals.value)
                } else {
                    // Remote Ollama/Homelab Generation
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
                    
                    val listType = object : TypeToken<List<RemoteMeal>>() {}.type
                    val generatedMeals: List<RemoteMeal> = gson.fromJson(response.response, listType)
                    
                    _meals.value = generatedMeals
                    
                    // Persist generated meals to database
                    saveMealsToDatabase(generatedMeals)
                }
            } catch (e: Exception) {
                // Fallback to local mock if remote fails
                _meals.value = listOf(
                    RemoteMeal("Local Fallback Chicken", 500, 40f, 30f, 15f, listOf("Chicken", "Rice"))
                )
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun saveMealsToDatabase(meals: List<RemoteMeal>) {
        meals.forEach { remoteMeal ->
            val meal = com.ironcore.metrics.data.local.entities.Meal(
                name = remoteMeal.name,
                totalCalories = remoteMeal.calories,
                proteinGrams = remoteMeal.protein,
                carbGrams = remoteMeal.carbs,
                fatGrams = remoteMeal.fat,
                timestamp = System.currentTimeMillis()
            )
            nutritionDao.insertMeal(meal)
        }
    }
}
