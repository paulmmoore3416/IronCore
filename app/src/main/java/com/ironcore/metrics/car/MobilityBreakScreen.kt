package com.ironcore.metrics.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.core.graphics.drawable.IconCompat
import com.ironcore.metrics.R

/**
 * A screen that suggests a mobility break after a long drive.
 */
class MobilityBreakScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        return MessageTemplate.Builder("Time for a mobility break!")
            .setTitle("Stitch: Drive Monitor")
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, android.R.drawable.ic_dialog_info)).build())
            .addAction(
                Action.Builder()
                    .setTitle("Dismiss")
                    .setOnClickListener { finish() }
                    .build()
            )
            .build()
    }
}
