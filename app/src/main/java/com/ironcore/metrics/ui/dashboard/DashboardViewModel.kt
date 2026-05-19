package com.ironcore.metrics.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ironcore.metrics.data.health.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    private val _steps = MutableStateFlow(0L)
    val steps = _steps.asStateFlow()

    private val _heartRate = MutableStateFlow(0L)
    val heartRate = _heartRate.asStateFlow()

    private val _weight = MutableStateFlow(0.0)
    val weight = _weight.asStateFlow()

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted = _permissionsGranted.asStateFlow()

    fun checkPermissionsAndFetchData() {
        viewModelScope.launch {
            if (healthConnectManager.isAvailable() && healthConnectManager.hasAllPermissions()) {
                _permissionsGranted.value = true
                fetchHealthData()
            } else {
                _permissionsGranted.value = false
            }
        }
    }

    private suspend fun fetchHealthData() {
        val endTime = Instant.now()
        val startTime = endTime.minus(7, ChronoUnit.DAYS)

        _steps.value = healthConnectManager.readStepsByTimeRange(startTime, endTime)
        _heartRate.value = healthConnectManager.readLatestHeartRate(startTime, endTime)
        _weight.value = healthConnectManager.readLatestWeight(startTime, endTime)
    }
}
