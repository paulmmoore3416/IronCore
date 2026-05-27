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

import android.app.Application
import android.content.Intent
import com.ironcore.metrics.data.health.IronCoreVitalsService

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val application: Application,
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

    private val _sleepMinutes = MutableStateFlow(0L)
    val sleepMinutes = _sleepMinutes.asStateFlow()

    private val _spO2 = MutableStateFlow(0.0)
    val spO2 = _spO2.asStateFlow()

    private val _respiratoryRate = MutableStateFlow(0.0)
    val respiratoryRate = _respiratoryRate.asStateFlow()

    // Real-time Heart Rate Flow
    private val _realtimeHeartRate = MutableStateFlow(72)
    val realtimeHeartRate = _realtimeHeartRate.asStateFlow()

    // Vitals Warning State
    private val _vitalsWarning = MutableStateFlow<String?>(null)
    val vitalsWarning = _vitalsWarning.asStateFlow()

    // Focus Mode Toggle
    private val _isFocusMode = MutableStateFlow(false)
    val isFocusMode = _isFocusMode.asStateFlow()

    // Biometric Auth State
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    // Hydration Target
    val hydrationTarget = 2000

    init {
        // Simulation of real-time data updates
        viewModelScope.launch {
            while (true) {
                val delayTime = if (_isFocusMode.value) 2000L else 10000L
                kotlinx.coroutines.delay(delayTime)
                if (_permissionsGranted.value) {
                    // Simulate slight fluctuations in heart rate
                    val currentHr = _realtimeHeartRate.value
                    val delta = if (_isFocusMode.value) (2..10).random() else (-2..2).random()
                    val newHr = (currentHr + delta).coerceIn(60, 180)
                    _realtimeHeartRate.value = newHr
                    
                    updateVitalsWarning(newHr.toLong(), _spO2.value)
                    
                    // Periodically increment steps to feel "alive"
                    if ((0..10).random() > 7) {
                        _steps.value += (1..5).random()
                    }
                }
            }
        }
    }

    private fun updateVitalsWarning(hr: Long, spo2: Double) {
        _vitalsWarning.value = when {
            hr > 160 -> "CRITICAL: High Heart Rate ($hr BPM)"
            spo2 > 0.0 && spo2 < 90.0 -> "CRITICAL: Low Oxygen ($spo2%)"
            else -> null
        }
    }

    fun toggleFocusMode() {
        _isFocusMode.value = !_isFocusMode.value
        
        val intent = Intent(application, IronCoreVitalsService::class.java).apply {
            action = if (_isFocusMode.value) {
                IronCoreVitalsService.ACTION_START_MONITORING
            } else {
                IronCoreVitalsService.ACTION_STOP_MONITORING
            }
        }
        
        if (_isFocusMode.value) {
            application.startForegroundService(intent)
        } else {
            application.startService(intent)
        }
    }

    fun setAuthenticated(authenticated: Boolean) {
        _isAuthenticated.value = authenticated
    }

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
                    wearDataSyncService.syncHydration(_hydrationMl.value + validatedAmount, hydrationTarget)
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
                
                val endTime = Instant.now()
                val startTime = endTime.minus(24, ChronoUnit.HOURS)
                val sleepStages = healthConnectManager.readSleepStages(startTime, endTime)
                
                val advice = recoveryAdvisorService.getRecoveryAdvice(
                    steps = _steps.value,
                    heartRate = _heartRate.value,
                    hydrationMl = _hydrationMl.value,
                    activeCalories = _activeCaloriesBurned.value,
                    consumedCalories = _consumedCalories.value,
                    recoveryScore = recoveryScore.value,
                    sleepStages = sleepStages
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
            val rawSleep = healthConnectManager.readSleepDuration(startTime, endTime)
            val rawSpO2 = healthConnectManager.readLatestOxygenSaturation(startTime, endTime)
            val rawRespRate = healthConnectManager.readLatestRespiratoryRate(startTime, endTime)

            // Apply validation
            _steps.value = healthDataValidator.validateSteps(rawSteps)
            _heartRate.value = healthDataValidator.validateHeartRate(rawHeartRate)
            _weight.value = healthDataValidator.validateWeight(rawWeight)
            _activeCaloriesBurned.value = healthDataValidator.validateCalories(rawCalories)
            _sleepMinutes.value = healthDataValidator.validateSleep(rawSleep)
            _spO2.value = healthDataValidator.validateSpO2(rawSpO2)
            _respiratoryRate.value = healthDataValidator.validateRespiratoryRate(rawRespRate)
            
            updateVitalsWarning(_heartRate.value, _spO2.value)

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
            }
        }
        viewModelScope.launch {
            nutritionDao.getHydrationSince(todayStart).collect { total ->
                _hydrationMl.value = total ?: 0
            }
        }
    }

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
