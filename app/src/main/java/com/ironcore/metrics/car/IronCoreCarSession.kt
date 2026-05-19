package com.ironcore.metrics.car

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session

/**
 * Manages the life cycle of the Android Auto session.
 */
class IronCoreCarSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        // Simulation: In a real scenario, we'd check the latest HR from Health Connect
        // or a local database. For this prototype, we show the EmergencyAlertScreen
        // if a specific extra is present or based on a simulated high value.
        
        val isEmergency = intent.getBooleanExtra("is_emergency", false)
        
        return if (isEmergency) {
            EmergencyAlertScreen(carContext)
        } else {
            PostWorkoutScreen(carContext)
        }
    }
}
