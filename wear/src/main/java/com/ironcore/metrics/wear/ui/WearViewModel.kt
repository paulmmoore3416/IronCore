package com.ironcore.metrics.wear.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ironcore.metrics.wear.data.HydrationData
import com.ironcore.metrics.wear.data.RecoveryData
import com.ironcore.metrics.wear.data.WearDataSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WearViewModel @Inject constructor(
    private val wearDataSyncService: WearDataSyncService
) : ViewModel() {

    private val _heartRate = MutableStateFlow(0L)
    val heartRate = _heartRate.asStateFlow()

    private val _steps = MutableStateFlow(0L)
    val steps = _steps.asStateFlow()

    private val _hydration = MutableStateFlow<HydrationData?>(null)
    val hydration = _hydration.asStateFlow()

    private val _recovery = MutableStateFlow<RecoveryData?>(null)
    val recovery = _recovery.asStateFlow()

    private val _activeCalories = MutableStateFlow(0.0)
    val activeCalories = _activeCalories.asStateFlow()

    private val _consumedCalories = MutableStateFlow(0)
    val consumedCalories = _consumedCalories.asStateFlow()

    private val _nutritionPlan = MutableStateFlow<List<WearMeal>>(emptyList())
    val nutritionPlan = _nutritionPlan.asStateFlow()

    private val gson = Gson()

    init {
        loadMetrics()
    }

    fun loadMetrics() {
        viewModelScope.launch {
            _hydration.value = wearDataSyncService.readHydration()
            _recovery.value = wearDataSyncService.readRecovery()
            
            wearDataSyncService.readNutritionPlan()?.let { json ->
                try {
                    val listType = object : TypeToken<List<WearMeal>>() {}.type
                    _nutritionPlan.value = gson.fromJson(json, listType)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun refreshData() {
        loadMetrics()
    }
}
