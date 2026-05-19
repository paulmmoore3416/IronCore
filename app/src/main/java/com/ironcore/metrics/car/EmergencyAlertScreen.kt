package com.ironcore.metrics.car

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.core.graphics.drawable.IconCompat
import com.ironcore.metrics.R

/**
 * A screen that displays a critical health alert on the car display.
 * Provides options to call emergency services or dismiss the alert.
 */
class EmergencyAlertScreen(carContext: CarContext) : Screen(carContext) {
    
    companion object {
        private const val TAG = "EmergencyAlertScreen"
        private const val EMERGENCY_NUMBER = "911" // US emergency number
    }
    
    override fun onGetTemplate(): Template {
        return MessageTemplate.Builder("CRITICAL HEART RATE DETECTED")
            .setTitle("Health Emergency")
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, android.R.drawable.ic_dialog_alert)).build())
            .addAction(
                Action.Builder()
                    .setTitle("CALL EMERGENCY")
                    .setBackgroundColor(CarColor.RED)
                    .setOnClickListener { 
                        initiateEmergencyCall()
                    }
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setTitle("Dismiss")
                    .setOnClickListener { finish() }
                    .build()
            )
            .build()
    }
    
    /**
     * Initiates an emergency call using ACTION_DIAL intent.
     * Uses ACTION_DIAL instead of ACTION_CALL to give user final confirmation.
     */
    private fun initiateEmergencyCall() {
        try {
            Log.w(TAG, "Initiating emergency call to $EMERGENCY_NUMBER")
            
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$EMERGENCY_NUMBER")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            carContext.startActivity(dialIntent)
            
            Log.i(TAG, "Emergency dialer launched successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error launching emergency dialer", e)
            // Screen will remain visible so user can try again or dismiss
        }
    }
}
