package com.ironcore.metrics.data.sync

import android.util.Log
import com.google.android.gms.wearable.*
import com.ironcore.metrics.data.local.dao.NutritionDao
import com.ironcore.metrics.data.local.entities.HydrationLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Service that listens for data changes from Wear OS devices.
 * Updates local database when data (like hydration) is logged on the watch.
 */
@AndroidEntryPoint
class IronCoreWearListenerService : WearableListenerService() {

    @Inject
    lateinit var nutritionDao: NutritionDao

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "IronCoreWearListener"
        private const val PATH_HYDRATION = "/ironcore/hydration"
        private const val KEY_HYDRATION_ML = "hydration_ml"
        private const val KEY_TIMESTAMP = "timestamp"
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == PATH_HYDRATION) {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                    val amountMl = dataMap.getInt(KEY_HYDRATION_ML)
                    val timestamp = dataMap.getLong(KEY_TIMESTAMP)
                    
                    Log.d(TAG, "Received hydration sync from watch: $amountMl ml")
                    
                    // We only want to add the INCREMENT, but the DataMap usually stores the TOTAL.
                    // For a simple production-ready sync, we might use MessageClient for increments
                    // or compare with local state. 
                    // To keep it simple for now, we'll assume the watch sends the total daily amount
                    // and we reconcile. However, a better way for "Logging" is to use MessageClient.
                    
                    // Let's assume the watch sends a message to "LOG_HYDRATION" instead of just changing data.
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        when (messageEvent.path) {
            "/ironcore/log_hydration" -> {
                val amountMl = messageEvent.data.decodeToString().toIntOrNull() ?: 0
                if (amountMl > 0) {
                    serviceScope.launch {
                        nutritionDao.insertHydration(HydrationLog(amountMl = amountMl))
                        Log.d(TAG, "Logged $amountMl ml hydration from Wear OS message")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
