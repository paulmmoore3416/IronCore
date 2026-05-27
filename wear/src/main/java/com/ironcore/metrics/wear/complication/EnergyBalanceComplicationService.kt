package com.ironcore.metrics.wear.complication

import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import com.ironcore.metrics.wear.data.WearDataSyncService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Complication provider that displays the Net Energy Balance (Consumed - Active Burned).
 */
@AndroidEntryPoint
class EnergyBalanceComplicationService : ComplicationDataSourceService() {

    @Inject
    lateinit var wearDataSyncService: WearDataSyncService

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener
    ) {
        serviceScope.launch {
            // Ideally we'd have a specific method for Energy Balance, 
            // but we can derive it from general health metrics.
            // For now, we'll return a placeholder or use existing data if available.
            
            val complicationData = when (request.complicationType) {
                ComplicationType.SHORT_TEXT -> {
                    ShortTextComplicationData.Builder(
                        text = PlainComplicationText.Builder("-120").build(),
                        contentDescription = PlainComplicationText.Builder("Energy Balance").build()
                    )
                    .setTitle(PlainComplicationText.Builder("kcal").build())
                    .build()
                }
                ComplicationType.RANGED_VALUE -> {
                    RangedValueComplicationData.Builder(
                        value = 750f,
                        min = 0f,
                        max = 2000f,
                        contentDescription = PlainComplicationText.Builder("Energy Balance").build()
                    )
                    .setText(PlainComplicationText.Builder("750").build())
                    .setTitle(PlainComplicationText.Builder("Net").build())
                    .build()
                }
                else -> null
            }
            
            listener.onComplicationData(complicationData)
        }
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder("500").build(),
                    contentDescription = PlainComplicationText.Builder("Energy Balance Preview").build()
                )
                .setTitle(PlainComplicationText.Builder("kcal").build())
                .build()
            }
            else -> null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.launch {
            // Cleanup
        }
    }
}
