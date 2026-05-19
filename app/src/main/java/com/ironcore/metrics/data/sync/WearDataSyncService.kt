package com.ironcore.metrics.data.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service that syncs health metrics from phone to Wear OS device.
 * Uses Wear DataClient API for data synchronization.
 */
@Singleton
class WearDataSyncService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "WearDataSyncService"
        
        // Data paths for different metric types
        private const val PATH_HYDRATION = "/ironcore/hydration"
        private const val PATH_RECOVERY = "/ironcore/recovery"
        private const val PATH_HEART_RATE = "/ironcore/heartrate"
        private const val PATH_STEPS = "/ironcore/steps"
        private const val PATH_CALORIES = "/ironcore/calories"
        
        // Data keys
        private const val KEY_HYDRATION_ML = "hydration_ml"
        private const val KEY_HYDRATION_TARGET = "hydration_target"
        private const val KEY_RECOVERY_SCORE = "recovery_score"
        private const val KEY_RECOVERY_ADVICE = "recovery_advice"
        private const val KEY_HEART_RATE = "heart_rate"
        private const val KEY_STEPS = "steps"
        private const val KEY_ACTIVE_CALORIES = "active_calories"
        private const val KEY_CONSUMED_CALORIES = "consumed_calories"
        private const val KEY_TIMESTAMP = "timestamp"
    }

    private val dataClient: DataClient by lazy {
        Wearable.getDataClient(context)
    }

    /**
     * Syncs hydration data to Wear OS device.
     */
    suspend fun syncHydration(currentMl: Int, targetMl: Int = 2000): Boolean {
        return try {
            val putDataReq = PutDataMapRequest.create(PATH_HYDRATION).apply {
                dataMap.putInt(KEY_HYDRATION_ML, currentMl)
                dataMap.putInt(KEY_HYDRATION_TARGET, targetMl)
                dataMap.putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            }.asPutDataRequest()
            
            dataClient.putDataItem(putDataReq).await()
            Log.d(TAG, "Hydration synced: $currentMl/$targetMl ml")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing hydration data", e)
            false
        }
    }

    /**
     * Syncs recovery metrics to Wear OS device.
     */
    suspend fun syncRecovery(score: Int, advice: String? = null): Boolean {
        return try {
            val putDataReq = PutDataMapRequest.create(PATH_RECOVERY).apply {
                dataMap.putInt(KEY_RECOVERY_SCORE, score)
                dataMap.putString(KEY_RECOVERY_ADVICE, advice ?: "")
                dataMap.putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            }.asPutDataRequest()
            
            dataClient.putDataItem(putDataReq).await()
            Log.d(TAG, "Recovery synced: score=$score")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing recovery data", e)
            false
        }
    }

    /**
     * Syncs comprehensive health metrics to Wear OS device.
     */
    suspend fun syncHealthMetrics(
        heartRate: Long,
        steps: Long,
        activeCalories: Double,
        consumedCalories: Int
    ): Boolean {
        return try {
            // Sync heart rate
            val hrReq = PutDataMapRequest.create(PATH_HEART_RATE).apply {
                dataMap.putLong(KEY_HEART_RATE, heartRate)
                dataMap.putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            }.asPutDataRequest()
            dataClient.putDataItem(hrReq).await()

            // Sync steps
            val stepsReq = PutDataMapRequest.create(PATH_STEPS).apply {
                dataMap.putLong(KEY_STEPS, steps)
                dataMap.putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            }.asPutDataRequest()
            dataClient.putDataItem(stepsReq).await()

            // Sync calories
            val caloriesReq = PutDataMapRequest.create(PATH_CALORIES).apply {
                dataMap.putDouble(KEY_ACTIVE_CALORIES, activeCalories)
                dataMap.putInt(KEY_CONSUMED_CALORIES, consumedCalories)
                dataMap.putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            }.asPutDataRequest()
            dataClient.putDataItem(caloriesReq).await()

            Log.d(TAG, "Health metrics synced: HR=$heartRate, Steps=$steps, Calories=$activeCalories/$consumedCalories")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing health metrics", e)
            false
        }
    }
}
