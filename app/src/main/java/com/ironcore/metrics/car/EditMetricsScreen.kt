package com.ironcore.metrics.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*

/**
 * Screen to manually edit and enter workout metrics.
 */
class EditMetricsScreen(
    carContext: CarContext,
    private var energyBurned: Int,
    private var durationMins: Int,
    private var avgHeartRate: Int,
    private val onSaved: (Int, Int, Int) -> Unit
) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val itemListBuilder = ItemList.Builder()

        // Energy Edit
        itemListBuilder.addItem(
            Row.Builder()
                .setTitle("Energy: $energyBurned kcal")
                .addAction(Action.Builder().setTitle("-50").setOnClickListener {
                    energyBurned = (energyBurned - 50).coerceAtLeast(0)
                    invalidate()
                }.build())
                .addAction(Action.Builder().setTitle("+50").setOnClickListener {
                    energyBurned += 50
                    invalidate()
                }.build())
                .build()
        )

        // Duration Edit
        itemListBuilder.addItem(
            Row.Builder()
                .setTitle("Duration: $durationMins mins")
                .addAction(Action.Builder().setTitle("-5").setOnClickListener {
                    durationMins = (durationMins - 5).coerceAtLeast(0)
                    invalidate()
                }.build())
                .addAction(Action.Builder().setTitle("+5").setOnClickListener {
                    durationMins += 5
                    invalidate()
                }.build())
                .build()
        )
        
        // HR Edit
        itemListBuilder.addItem(
            Row.Builder()
                .setTitle("Avg HR: $avgHeartRate BPM")
                .addAction(Action.Builder().setTitle("-5").setOnClickListener {
                    avgHeartRate = (avgHeartRate - 5).coerceAtLeast(40)
                    invalidate()
                }.build())
                .addAction(Action.Builder().setTitle("+5").setOnClickListener {
                    avgHeartRate = (avgHeartRate + 5).coerceAtMost(220)
                    invalidate()
                }.build())
                .build()
        )

        return ListTemplate.Builder()
            .setTitle("Manual Metric Entry")
            .setHeaderAction(Action.BACK)
            .setSingleList(itemListBuilder.build())
            .addAction(
                Action.Builder()
                    .setTitle("Save Metrics")
                    .setOnClickListener {
                        onSaved(energyBurned, durationMins, avgHeartRate)
                        finish()
                    }
                    .build()
            )
            .build()
    }
}
