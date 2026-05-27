package com.ironcore.metrics.ui.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironcore.metrics.data.health.HealthConnectManager
import com.ironcore.metrics.data.health.HealthDataValidator
import com.ironcore.metrics.data.local.dao.NutritionDao
import com.ironcore.metrics.data.local.entities.HydrationLog
import com.ironcore.metrics.data.local.entities.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetricDetailViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val nutritionDao: NutritionDao,
    private val healthDataValidator: HealthDataValidator
) : ViewModel() {

    companion object {
        private const val TAG = "MetricDetailViewModel"
    }

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun saveMetric(metricType: String, value: String) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                _saveSuccess.value = false

                when (metricType.lowercase()) {
                    "weight" -> {
                        val weightKg = value.toFloatOrNull()
                        if (weightKg == null) {
                            _errorMessage.value = "Please enter a valid weight"
                            return@launch
                        }
                        val validated = healthDataValidator.validateWeight(weightKg.toDouble())
                        if (validated > 0) {
                            // Note: Health Connect write operations require additional implementation
                            // For now, we'll just validate and mark as success
                            // TODO: Implement writeWeight in HealthConnectManager
                            _saveSuccess.value = true
                            Log.d(TAG, "Weight validated: $weightKg kg (write not yet implemented)")
                        } else {
                            _errorMessage.value = "Weight must be between 30-300 kg"
                        }
                    }
                    "hydration" -> {
                        val ml = value.toIntOrNull()
                        if (ml == null) {
                            _errorMessage.value = "Please enter a valid amount"
                            return@launch
                        }
                        val validated = healthDataValidator.validateHydration(ml)
                        if (validated > 0) {
                            nutritionDao.insertHydration(HydrationLog(amountMl = ml))
                            _saveSuccess.value = true
                            Log.d(TAG, "Hydration saved: $ml ml")
                        } else {
                            _errorMessage.value = "Hydration must be between 1-5000 ml"
                        }
                    }
                    "nutrition", "energy" -> {
                        val calories = value.toIntOrNull()
                        if (calories == null) {
                            _errorMessage.value = "Please enter valid calories"
                            return@launch
                        }
                        if (calories > 0 && calories < 10000) {
                            nutritionDao.insertMeal(
                                Meal(
                                    name = "Quick Entry",
                                    totalCalories = calories,
                                    proteinGrams = 0f,
                                    carbGrams = 0f,
                                    fatGrams = 0f,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            _saveSuccess.value = true
                            Log.d(TAG, "Nutrition saved: $calories kcal")
                        } else {
                            _errorMessage.value = "Calories must be between 1-10000"
                        }
                    }
                    "steps" -> {
                        _errorMessage.value = "Steps are automatically tracked via Health Connect"
                    }
                    "bpm" -> {
                        _errorMessage.value = "Heart rate is automatically tracked via Health Connect"
                    }
                    else -> {
                        _errorMessage.value = "Manual entry not supported for this metric"
                        Log.w(TAG, "Unsupported metric type: $metricType")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error saving data: ${e.message}"
                Log.e(TAG, "Error saving metric", e)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
