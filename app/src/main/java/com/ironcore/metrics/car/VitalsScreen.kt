package com.ironcore.metrics.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.car.app.CarToast

/**
 * A screen to view current vitals in Android Auto.
 */
/**
 * Addition 2: Vitals Safety HUD
 * High-visibility real-time metrics for driving safety.
 */
class VitalsScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val itemListBuilder = ItemList.Builder()
        
        // Heart Rate Grid Item
        itemListBuilder.addItem(
            GridItem.Builder()
                .setTitle("Heart Rate")
                .setText("82 BPM") 
                .setImage(CarIcon.APP_ICON)
                .build()
        )

        // SpO2 Grid Item
        itemListBuilder.addItem(
            GridItem.Builder()
                .setTitle("SpO2")
                .setText("98%")
                .setImage(CarIcon.APP_ICON)
                .build()
        )

        // Safety Alert
        itemListBuilder.addItem(
            GridItem.Builder()
                .setTitle("Driver Status")
                .setText("Optimal")
                .setImage(CarIcon.APP_ICON)
                .build()
        )

        return GridTemplate.Builder()
            .setTitle("Vitals Safety HUD")
            .setHeaderAction(Action.BACK)
            .setSingleList(itemListBuilder.build())
            .build()
    }
}
