package com.ironcore.metrics.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironcore.metrics.data.remote.HomelabApiService
import com.ironcore.metrics.data.remote.dto.MealPlanRequest
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

    fun generatePlan() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.generateMealPlan(
                    MealPlanRequest(
                        userId = 0,
                        targetCalories = 2500,
                        proteinGrams = 200f,
                        preferences = listOf("High protein", "Low carb")
                    )
                )
                _meals.value = response.meals
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
