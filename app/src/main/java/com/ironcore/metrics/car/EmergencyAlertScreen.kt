package com.ironcore.metrics.car

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.core.content.ContextCompat
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
     * Initiates an emergency call.
     * Uses ACTION_CALL for instant dialing if permission is granted,
     * otherwise falls back to ACTION_DIAL.
     */
    private fun initiateEmergencyCall() {
        try {
            Log.w(TAG, "Initiating emergency call to $EMERGENCY_NUMBER")
            
            val hasCallPermission = ContextCompat.checkSelfPermission(
                carContext,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED

            val action = if (hasCallPermission) Intent.ACTION_CALL else Intent.ACTION_DIAL
            
            val callIntent = Intent(action).apply {
                data = Uri.parse("tel:$EMERGENCY_NUMBER")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            
            carContext.startActivity(callIntent)
            
            Log.i(TAG, "Emergency call initiated with action: $action")
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating emergency call", e)
        }
    }
}
