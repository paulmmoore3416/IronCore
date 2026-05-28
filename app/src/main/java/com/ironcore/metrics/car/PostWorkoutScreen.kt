package com.ironcore.metrics.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.car.app.CarToast
import androidx.core.graphics.drawable.IconCompat
import com.ironcore.metrics.R

/**
 * A screen that displays a summary of the latest workout on the car display,
 * allowing the user to review, edit, or use voice commands.
 */
class PostWorkoutScreen(carContext: CarContext) : Screen(carContext) {

    // Mocked metrics for the prototype
    private var energyBurned = 450
    private var durationMins = 45
    private var avgHeartRate = 135

    override fun onGetTemplate(): Template {
        val itemListBuilder = ItemList.Builder()

        itemListBuilder.addItem(
            Row.Builder()
                .setTitle("Energy Burned")
                .addText("$energyBurned kcal")
                .build()
        )

        itemListBuilder.addItem(
            Row.Builder()
                .setTitle("Duration")
                .addText("$durationMins minutes")
                .build()
        )

        itemListBuilder.addItem(
            Row.Builder()
                .setTitle("Avg Heart Rate")
                .addText("$avgHeartRate BPM")
                .build()
        )

        // Action strip with Touch to Talk
        val actionStrip = ActionStrip.Builder()
            .addAction(
                Action.Builder()
                    .setTitle("🎙️ Touch to Talk")
                    .setOnClickListener {
                        screenManager.push(VoiceCommandScreen(carContext))
                    }
                    .build()
            )
            .build()

        return ListTemplate.Builder()
            .setTitle("Workout Metrics Review")
            .setHeaderAction(Action.BACK)
            .setSingleList(itemListBuilder.build())
            .setActionStrip(actionStrip)
            .addAction(
                Action.Builder()
                    .setTitle("Edit Metrics")
                    .setOnClickListener {
                        screenManager.push(EditMetricsScreen(carContext, energyBurned, durationMins, avgHeartRate) { newKcal, newMins, newHr ->
                            energyBurned = newKcal
                            durationMins = newMins
                            avgHeartRate = newHr
                            invalidate() // Refresh this screen with new data
                            CarToast.makeText(carContext, "Metrics Saved", CarToast.LENGTH_SHORT).show()
                        })
                    }
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setTitle("Save & Close")
                    .setOnClickListener {
                        CarToast.makeText(carContext, "Workout Logged", CarToast.LENGTH_SHORT).show()
                        finish()
                    }
                    .build()
            )
            .build()
    }
}
