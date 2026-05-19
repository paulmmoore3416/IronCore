package com.ironcore.metrics.data.health

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ironcore.metrics.car.EmergencyNotificationHelper
import com.ironcore.metrics.data.local.IronCoreDatabase
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Background worker that periodically checks heart rate for critical values.
 * Uses dynamic thresholds based on user age and current activity type.
 */
class VitalsMonitorWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "VitalsMonitorWorker"
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface VitalsWorkerEntryPoint {
        fun healthConnectManager(): HealthConnectManager
        fun database(): IronCoreDatabase
        fun hrThresholdCalculator(): HeartRateThresholdCalculator
    }

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                VitalsWorkerEntryPoint::class.java
            )
            val healthConnectManager = entryPoint.healthConnectManager()
            val database = entryPoint.database()
            val hrCalculator = entryPoint.hrThresholdCalculator()

            if (!healthConnectManager.isAvailable()) {
                Log.d(TAG, "Health Connect not available")
                return Result.success()
            }

            val endTime = Instant.now()
            val startTime = endTime.minus(15, ChronoUnit.MINUTES)
            
            // Get latest heart rate
            val latestHR = healthConnectManager.readLatestHeartRate(startTime, endTime)
            
            if (latestHR == 0L) {
                Log.d(TAG, "No heart rate data available")
                return Result.success()
            }

            // Get user age from profile
            val userProfile = database.userProfileDao().getUserProfileOnce()
            val userAge = userProfile?.age
            
            // Get current activity type
            val activityType = healthConnectManager.readCurrentActivityType(startTime, endTime)
            
            // Calculate dynamic threshold
            val isCritical = hrCalculator.isHeartRateCritical(latestHR, userAge, activityType)
            
            if (isCritical) {
                Log.w(TAG, "Critical heart rate detected: $latestHR BPM - Sending emergency alert")
                EmergencyNotificationHelper.sendEmergencyAlert(applicationContext)
            } else {
                Log.d(TAG, "Heart rate normal: $latestHR BPM")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error monitoring vitals", e)
            // Retry on failure to ensure continuous monitoring
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "VitalsMonitorWork"

        fun enqueue(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<VitalsMonitorWorker>(
                15, TimeUnit.MINUTES // Minimum periodic interval allowed by WorkManager
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
