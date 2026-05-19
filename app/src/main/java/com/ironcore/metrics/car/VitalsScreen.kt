package com.ironcore.metrics.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.car.app.CarToast

/**
 * A screen to view current vitals in Android Auto.
 */
class VitalsScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val row1 = Row.Builder()
            .setTitle("Heart Rate")
            .addText("82 BPM - Normal")
            .build()
            
        val row2 = Row.Builder()
            .setTitle("Recovery Score")
            .addText("85% - Optimal readiness")
            .build()

        val pane = Pane.Builder()
            .addRow(row1)
            .addRow(row2)
            .addAction(
                Action.Builder()
                    .setTitle("Refresh")
                    .setOnClickListener { 
                        CarToast.makeText(carContext, "Vitals refreshed", CarToast.LENGTH_SHORT).show()
                        invalidate()
                    }
                    .build()
            )
            .build()

        return PaneTemplate.Builder(pane)
            .setHeaderAction(Action.BACK)
            .setTitle("Live Vitals")
            .build()
    }
}
