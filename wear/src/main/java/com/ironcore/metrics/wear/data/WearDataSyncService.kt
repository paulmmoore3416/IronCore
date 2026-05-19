package com.ironcore.metrics.wear.data

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service that syncs health metrics between phone and Wear OS device.
 * Uses Wear DataClient API for bidirectional data synchronization.
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

    /**
     * Reads hydration data from DataClient.
     */
    suspend fun readHydration(): HydrationData? {
        return try {
            val dataItems = dataClient.getDataItems(
                WearableUris.createUriWithHost(PATH_HYDRATION, "*")
            ).await()
            
            dataItems.firstOrNull()?.let { item ->
                val dataMap = DataMapItem.fromDataItem(item).dataMap
                HydrationData(
                    currentMl = dataMap.getInt(KEY_HYDRATION_ML),
                    targetMl = dataMap.getInt(KEY_HYDRATION_TARGET),
                    timestamp = dataMap.getLong(KEY_TIMESTAMP)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading hydration data", e)
            null
        } finally {
            // Always release resources
        }
    }

    /**
     * Reads recovery data from DataClient.
     */
    suspend fun readRecovery(): RecoveryData? {
        return try {
            val dataItems = dataClient.getDataItems(
                WearableUris.createUriWithHost(PATH_RECOVERY, "*")
            ).await()
            
            dataItems.firstOrNull()?.let { item ->
                val dataMap = DataMapItem.fromDataItem(item).dataMap
                RecoveryData(
                    score = dataMap.getInt(KEY_RECOVERY_SCORE),
                    advice = dataMap.getString(KEY_RECOVERY_ADVICE),
                    timestamp = dataMap.getLong(KEY_TIMESTAMP)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading recovery data", e)
            null
        }
    }
}

/**
 * Data class for hydration metrics.
 */
data class HydrationData(
    val currentMl: Int,
    val targetMl: Int,
    val timestamp: Long
)

/**
 * Data class for recovery metrics.
 */
data class RecoveryData(
    val score: Int,
    val advice: String?,
    val timestamp: Long
)
