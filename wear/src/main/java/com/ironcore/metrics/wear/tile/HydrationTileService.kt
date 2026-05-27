package com.ironcore.metrics.wear.tile

import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.ironcore.metrics.wear.data.WearDataSyncService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.guava.future
import javax.inject.Inject

private const val RESOURCES_VERSION = "1"

/**
 * Wear OS Tile for quick hydration logging and status.
 */
@AndroidEntryPoint
class HydrationTileService : TileService() {

    @Inject
    lateinit var wearDataSyncService: WearDataSyncService

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        return serviceScope.future {
            val hydrationData = wearDataSyncService.readHydration()
            val currentMl = hydrationData?.currentMl ?: 0
            
            TileBuilders.Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(
                    TimelineBuilders.Timeline.Builder()
                        .addTimelineEntry(
                            TimelineBuilders.TimelineEntry.Builder()
                                .setLayout(
                                    LayoutElementBuilders.Layout.Builder()
                                        .setRoot(layout(currentMl, requestParams.deviceConfiguration))
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build()
        }
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(
            ResourceBuilders.Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build()
        )
    }

    private fun layout(current: Int, deviceParameters: DeviceParameters): LayoutElementBuilders.LayoutElement {
        return PrimaryLayout.Builder(deviceParameters)
            .setContent(
                LayoutElementBuilders.Column.Builder()
                    .addContent(
                        Text.Builder(this, "$current")
                            .setTypography(Typography.TYPOGRAPHY_DISPLAY2)
                            .setColor(argb(0xFFFFFFFF.toInt()))
                            .build()
                    )
                    .addContent(
                        Text.Builder(this, "ml")
                            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                            .setColor(argb(0xFF00E5FF.toInt()))
                            .build()
                    )
                    .build()
            )
            .setPrimaryLabelTextContent(
                Text.Builder(this, "Hydration")
                    .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                    .setColor(argb(0xFF00E5FF.toInt()))
                    .build()
            )
            .build()
    }
}
