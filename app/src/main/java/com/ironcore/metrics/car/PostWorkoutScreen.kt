package com.ironcore.metrics.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*

/**
 * A screen that displays a summary of the latest workout on the car display.
 */
class PostWorkoutScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val row = Row.Builder()
            .setTitle("Latest Session: Hypertrophy")
            .addText("Energy Burned: 450 kcal")
            .addText("Duration: 45 minutes")
            .build()

        val pane = Pane.Builder()
            .addRow(row)
            .addAction(
                Action.Builder()
                    .setTitle("Dismiss")
                    .setOnClickListener { finish() }
                    .build()
            )
            .build()

        return PaneTemplate.Builder(pane)
            .setHeaderAction(Action.BACK)
            .setTitle("Post-Workout Summary")
            .build()
    }
}
