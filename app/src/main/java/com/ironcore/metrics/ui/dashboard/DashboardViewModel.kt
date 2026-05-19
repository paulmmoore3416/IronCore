package com.ironcore.metrics.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.ironcore.metrics.data.health.HealthConnectManager
import com.ironcore.metrics.data.health.HealthDataValidator
import com.ironcore.metrics.data.local.dao.NutritionDao
import com.ironcore.metrics.data.local.entities.HydrationLog
import com.ironcore.metrics.data.sync.WearDataSyncService
import com.ironcore.metrics.domain.RecoveryAdvice
import com.ironcore.metrics.domain.RecoveryAdvisorService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val nutritionDao: NutritionDao,
    private val recoveryAdvisorService: RecoveryAdvisorService,
    private val wearDataSyncService: WearDataSyncService,
    private val healthDataValidator: HealthDataValidator
) : ViewModel() {

    companion object {
        private const val TAG = "DashboardViewModel"
    }

    private val _steps = MutableStateFlow(0L)
    val steps = _steps.asStateFlow()

    private val _heartRate = MutableStateFlow(0L)
    val heartRate = _heartRate.asStateFlow()

    private val _weight = MutableStateFlow(0.0)
    val weight = _weight.asStateFlow()

    private val _activeCaloriesBurned = MutableStateFlow(0.0)
    val activeCaloriesBurned = _activeCaloriesBurned.asStateFlow()

    private val _consumedCalories = MutableStateFlow(0)
    val consumedCalories = _consumedCalories.asStateFlow()

    private val _hydrationMl = MutableStateFlow(0)
    val hydrationMl = _hydrationMl.asStateFlow()

    val energyBalance = combine(_consumedCalories, _activeCaloriesBurned) { consumed, burned ->
        consumed - burned.toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recoveryScore = combine(_steps, _heartRate, _hydrationMl) { steps, hr, hydration ->
        // Heuristic: Steps (low load), HR (normal range), Hydration (target 2000ml)
        val stepScore = (steps / 10000.0 * 30).coerceAtMost(30.0)
        val hrScore = if (hr in 50..80) 40.0 else 20.0
        val hydrationScore = (hydration / 2000.0 * 30).coerceAtMost(30.0)
        (stepScore + hrScore + hydrationScore).toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted = _permissionsGranted.asStateFlow()

    private val _recoveryAdvice = MutableStateFlow<RecoveryAdvice?>(null)
    val recoveryAdvice = _recoveryAdvice.asStateFlow()

    private val _isLoadingAdvice = MutableStateFlow(false)
    val isLoadingAdvice = _isLoadingAdvice.asStateFlow()

    fun checkPermissionsAndFetchData() {
        viewModelScope.launch {
            if (healthConnectManager.isAvailable() && healthConnectManager.hasAllPermissions()) {
                _permissionsGranted.value = true
                fetchHealthData()
                fetchNutritionData()
            } else {
                _permissionsGranted.value = false
            }
        }
    }

    fun addHydration(amount: Int) {
        viewModelScope.launch {
            try {
                val validatedAmount = healthDataValidator.validateHydration(amount)
                if (validatedAmount > 0) {
                    nutritionDao.insertHydration(HydrationLog(amountMl = validatedAmount))
                    // Sync updated hydration to Wear OS
                    wearDataSyncService.syncHydration(_hydrationMl.value + validatedAmount, 2000)
                    Log.d(TAG, "Added $validatedAmount ml hydration")
                } else {
                    Log.w(TAG, "Invalid hydration amount: $amount ml")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding hydration", e)
            }
        }
    }

    /**
     * Fetches AI-powered recovery advice based on current metrics.
     */
    fun fetchRecoveryAdvice() {
        viewModelScope.launch {
            try {
                _isLoadingAdvice.value = true
                Log.d(TAG, "Fetching AI recovery advice...")
                
                val advice = recoveryAdvisorService.getRecoveryAdvice(
                    steps = _steps.value,
                    heartRate = _heartRate.value,
                    hydrationMl = _hydrationMl.value,
                    activeCalories = _activeCaloriesBurned.value,
                    consumedCalories = _consumedCalories.value,
                    recoveryScore = recoveryScore.value
                )
                
                _recoveryAdvice.value = advice

                
                // Sync recovery data to Wear OS
                wearDataSyncService.syncRecovery(
                    score = recoveryScore.value,
                    advice = advice?.advice
                )

                Log.d(TAG, "Recovery advice received: ${advice?.advice}")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching recovery advice", e)
                _recoveryAdvice.value = null
            } finally {
                _isLoadingAdvice.value = false
            }
        }
    }

    private suspend fun fetchHealthData() {
        try {
            val endTime = Instant.now()
            val startTime = endTime.minus(7, ChronoUnit.DAYS)

            // Fetch and validate health data
            val rawSteps = healthConnectManager.readStepsByTimeRange(startTime, endTime)
            val rawHeartRate = healthConnectManager.readLatestHeartRate(startTime, endTime)
            val rawWeight = healthConnectManager.readLatestWeight(startTime, endTime)
            val rawCalories = healthConnectManager.readActiveCalories(startTime, endTime)

            // Apply validation
            _steps.value = healthDataValidator.validateSteps(rawSteps)
            _heartRate.value = healthDataValidator.validateHeartRate(rawHeartRate)
            _weight.value = healthDataValidator.validateWeight(rawWeight)
            _activeCaloriesBurned.value = healthDataValidator.validateCalories(rawCalories)
            
            Log.d(TAG, "Health data fetched and validated successfully")
            
            // Sync health metrics to Wear OS
            syncToWearDevice()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching health data", e)
            // Keep existing values on error
        }
    }

    private fun fetchNutritionData() {
        val todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        viewModelScope.launch {
            nutritionDao.getConsumedCaloriesSince(todayStart).collect { total ->
                _consumedCalories.value = total ?: 0


    /**
     * Syncs all current health metrics to Wear OS device.
     */
    private suspend fun syncToWearDevice() {
        try {
            // Sync comprehensive health metrics
            wearDataSyncService.syncHealthMetrics(
                heartRate = _heartRate.value,
                steps = _steps.value,
                activeCalories = _activeCaloriesBurned.value,
                consumedCalories = _consumedCalories.value
            )
            
            // Sync hydration
            wearDataSyncService.syncHydration(_hydrationMl.value, 2000)
            
            // Sync recovery if available
            wearDataSyncService.syncRecovery(
                score = recoveryScore.value,
                advice = _recoveryAdvice.value?.advice
            )
            
            Log.d(TAG, "Successfully synced all metrics to Wear OS")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing to Wear OS", e)
        }
    }

            }
        }
        viewModelScope.launch {
            nutritionDao.getHydrationSince(todayStart).collect { total ->
                _hydrationMl.value = total ?: 0
            }
        }
    }
}
