package com.ironcore.metrics.data.auth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized permission management for IronCore Metrics
 * Handles all runtime permissions required for full device integration
 */
@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * All runtime permissions required by the app
     * Grouped by category for better organization
     */
    val allPermissions: List<String> = buildList {
        // Audio & Voice
        add(Manifest.permission.RECORD_AUDIO)
        
        // Location (for outdoor workout tracking)
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        
        // Activity Recognition (for automatic workout detection)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        
        // Body Sensors (for heart rate monitors, etc.)
        add(Manifest.permission.BODY_SENSORS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.BODY_SENSORS_BACKGROUND)
        }
        
        // Notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        // Bluetooth (for wearable connectivity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            add(Manifest.permission.BLUETOOTH)
            add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        
        // Camera (for AR body scanning & form analysis)
        add(Manifest.permission.CAMERA)
        
        // Storage (for workout photos/videos)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_IMAGES)
            add(Manifest.permission.READ_MEDIA_VIDEO)
        }
        
        // Phone (for emergency calls)
        add(Manifest.permission.CALL_PHONE)
    }
    
    /**
     * Critical permissions required for core functionality
     */
    val criticalPermissions: List<String> = listOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.ACTIVITY_RECOGNITION.takeIf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q }
    ).filterNotNull()
    
    /**
     * Check if a specific permission is granted
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Check if all permissions are granted
     */
    fun areAllPermissionsGranted(): Boolean {
        return allPermissions.all { isPermissionGranted(it) }
    }
    
    /**
     * Check if critical permissions are granted
     */
    fun areCriticalPermissionsGranted(): Boolean {
        return criticalPermissions.all { isPermissionGranted(it) }
    }
    
    /**
     * Get list of permissions that are not yet granted
     */
    fun getMissingPermissions(): List<String> {
        return allPermissions.filter { !isPermissionGranted(it) }
    }
    
    /**
     * Get list of critical permissions that are not yet granted
     */
    fun getMissingCriticalPermissions(): List<String> {
        return criticalPermissions.filter { !isPermissionGranted(it) }
    }
    
    /**
     * Get permission status summary
     */
    fun getPermissionStatus(): PermissionStatus {
        val total = allPermissions.size
        val granted = allPermissions.count { isPermissionGranted(it) }
        val criticalGranted = criticalPermissions.all { isPermissionGranted(it) }
        
        return PermissionStatus(
            totalPermissions = total,
            grantedPermissions = granted,
            missingPermissions = getMissingPermissions(),
            criticalPermissionsGranted = criticalGranted,
            allPermissionsGranted = granted == total
        )
    }
    
    /**
     * Get user-friendly permission names for display
     */
    fun getPermissionDisplayName(permission: String): String {
        return when (permission) {
            Manifest.permission.RECORD_AUDIO -> "Microphone (Voice Commands)"
            Manifest.permission.ACCESS_FINE_LOCATION -> "Precise Location (Outdoor Tracking)"
            Manifest.permission.ACCESS_COARSE_LOCATION -> "Approximate Location"
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> "Background Location"
            Manifest.permission.ACTIVITY_RECOGNITION -> "Activity Recognition (Auto Workout Detection)"
            Manifest.permission.BODY_SENSORS -> "Body Sensors (Heart Rate)"
            Manifest.permission.BODY_SENSORS_BACKGROUND -> "Background Body Sensors"
            Manifest.permission.POST_NOTIFICATIONS -> "Notifications (Workout Reminders)"
            Manifest.permission.BLUETOOTH_CONNECT -> "Bluetooth (Wearable Devices)"
            Manifest.permission.BLUETOOTH_SCAN -> "Bluetooth Scanning"
            Manifest.permission.BLUETOOTH -> "Bluetooth"
            Manifest.permission.BLUETOOTH_ADMIN -> "Bluetooth Admin"
            Manifest.permission.CAMERA -> "Camera (AR Body Scanning)"
            Manifest.permission.READ_MEDIA_IMAGES -> "Photos (Progress Pictures)"
            Manifest.permission.READ_MEDIA_VIDEO -> "Videos (Form Analysis)"
            Manifest.permission.CALL_PHONE -> "Phone (Emergency Calls)"
            else -> permission.substringAfterLast('.')
        }
    }
}

/**
 * Data class representing permission status
 */
data class PermissionStatus(
    val totalPermissions: Int,
    val grantedPermissions: Int,
    val missingPermissions: List<String>,
    val criticalPermissionsGranted: Boolean,
    val allPermissionsGranted: Boolean
) {
    val percentageGranted: Int
        get() = if (totalPermissions > 0) (grantedPermissions * 100) / totalPermissions else 0
}
