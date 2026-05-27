package com.ironcore.metrics.data.health

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ironcore.metrics.MainActivity
import com.ironcore.metrics.R
import com.ironcore.metrics.car.EmergencyNotificationHelper
import com.ironcore.metrics.data.local.IronCoreDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Foreground service that monitors vitals in real-time during active sessions.
 * Provides more frequent updates than the background WorkManager task.
 */
@AndroidEntryPoint
class IronCoreVitalsService : Service() {

    @Inject
    lateinit var healthConnectManager: HealthConnectManager

    @Inject
    lateinit var database: IronCoreDatabase

    @Inject
    lateinit var hrThresholdCalculator: HeartRateThresholdCalculator

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var monitoringJob: Job? = null

    companion object {
        private const val TAG = "IronCoreVitalsService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "vitals_monitoring_channel"
        
        const val ACTION_START_MONITORING = "com.ironcore.metrics.START_MONITORING"
        const val ACTION_STOP_MONITORING = "com.ironcore.metrics.STOP_MONITORING"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_MONITORING -> startMonitoring()
            ACTION_STOP_MONITORING -> stopMonitoring()
        }
        return START_STICKY
    }

    private fun startMonitoring() {
        if (monitoringJob?.isActive == true) return

        val notification = createNotification("Monitoring Vitals...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, 
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        monitoringJob = serviceScope.launch {
            while (isActive) {
                checkVitals()
                delay(30000) // Check every 30 seconds during active session
            }
        }
    }

    private fun stopMonitoring() {
        monitoringJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private suspend fun checkVitals() {
        try {
            if (!healthConnectManager.isAvailable()) return

            val endTime = Instant.now()
            val startTime = endTime.minus(5, ChronoUnit.MINUTES)
            
            val latestHR = healthConnectManager.readLatestHeartRate(startTime, endTime)
            if (latestHR == 0L) return

            val userProfile = database.userProfileDao().getUserProfileOnce()
            val activityType = healthConnectManager.readCurrentActivityType(startTime, endTime)
            
            val isCritical = hrThresholdCalculator.isHeartRateCritical(latestHR, userProfile?.age, activityType)
            
            if (isCritical) {
                Log.w(TAG, "CRITICAL HEART RATE: $latestHR BPM")
                EmergencyNotificationHelper.sendEmergencyAlert(applicationContext)
                updateNotification("CRITICAL: High Heart Rate ($latestHR BPM)")
            } else {
                updateNotification("Heart Rate: $latestHR BPM")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking vitals in service", e)
        }
    }

    private fun createNotification(content: String): Notification {
        val pendingIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("IronCore Vitals Monitor")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_ironcore_logo)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private fun updateNotification(content: String) {
        val notification = createNotification(content)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Vitals Monitoring",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Used for real-time heart rate monitoring during workouts"
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
