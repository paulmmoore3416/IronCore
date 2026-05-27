package com.ironcore.metrics.car

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*

/**
 * Manages the life cycle of the Android Auto session.
 */
class IronCoreCarSession : Session(), DefaultLifecycleObserver {
    
    private var sessionJob: Job? = null
    private val sessionScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    companion object {
        // Production: 2 hours (120 minutes)
        // Testing: 2 minutes
        private const val MOBILITY_BREAK_THRESHOLD_MINUTES = 2L 
    }

    override fun onCreateScreen(intent: Intent): Screen {
        lifecycle.addObserver(this)
        
        val isEmergency = intent.getBooleanExtra("is_emergency", false)
        val isWorkoutDeepLink = intent.data?.host == "workout"
        
        return when {
            isEmergency -> EmergencyAlertScreen(carContext)
            isWorkoutDeepLink -> {
                // In a real app, this would trigger a "Workout Started" state
                // For the HUD, we'll show the Vitals screen
                VitalsScreen(carContext)
            }
            else -> MainMenuScreen(carContext)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        startDriveMonitor()
    }

    override fun onPause(owner: LifecycleOwner) {
        stopDriveMonitor()
    }

    private fun startDriveMonitor() {
        if (sessionJob?.isActive == true) return
        
        sessionJob = sessionScope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive) {
                delay(60000) // Check every minute
                val elapsedMinutes = (System.currentTimeMillis() - startTime) / 60000
                if (elapsedMinutes >= MOBILITY_BREAK_THRESHOLD_MINUTES) {
                    carContext.getCarService(androidx.car.app.ScreenManager::class.java)
                        .push(MobilityBreakScreen(carContext))
                    break // Stop monitoring after triggering once
                }
            }
        }
    }

    private fun stopDriveMonitor() {
        sessionJob?.cancel()
    }
}
