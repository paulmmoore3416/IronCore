package com.ironcore.metrics.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.car.app.CarToast
import androidx.core.graphics.drawable.IconCompat
import com.ironcore.metrics.R

/**
 * A feature-rich main menu for Android Auto.
 */
class MainMenuScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val itemListBuilder = ItemList.Builder()
        
        // Vitals
        itemListBuilder.addItem(
            GridItem.Builder()
                .setTitle("Vitals")
                .setText("Current: 82 BPM")
                .setImage(CarIcon.APP_ICON)
                .setOnClickListener {
                    screenManager.push(VitalsScreen(carContext))
                }
                .build()
        )

        // Workout
        itemListBuilder.addItem(
            GridItem.Builder()
                .setTitle("Workouts")
                .setText("Last: Hypertrophy")
                .setImage(CarIcon.APP_ICON)
                .setOnClickListener {
                    screenManager.push(PostWorkoutScreen(carContext))
                }
                .build()
        )

        // Nutrition
        itemListBuilder.addItem(
            GridItem.Builder()
                .setTitle("Nutrition")
                .setText("2250 kcal today")
                .setImage(CarIcon.APP_ICON)
                .setOnClickListener {
                    // Navigate to nutrition screen (placeholder for future implementation)
                    CarToast.makeText(carContext, "Nutrition sync complete", CarToast.LENGTH_SHORT).show()
                }
                .build()
        )

        // Emergency
        itemListBuilder.addItem(
            GridItem.Builder()
                .setTitle("Emergency")
                .setText("SOS Setup")
                .setImage(CarIcon.APP_ICON)
                .setOnClickListener {
                    screenManager.push(EmergencyAlertScreen(carContext))
                }
                .build()
        )

        // Addition 1: Fueling Map
        itemListBuilder.addItem(
            GridItem.Builder()
                .setTitle("Fueling Map")
                .setText("Find recovery food")
                .setImage(CarIcon.APP_ICON)
                .setOnClickListener {
                    screenManager.push(FuelingMapScreen(carContext, "Mediterranean")) // Default or synced cuisine
                }
                .build()
        )

        // Addition 4: Commute Prep
        itemListBuilder.addItem(
            GridItem.Builder()
                .setTitle("Commute Prep")
                .setText("Prepare for workout")
                .setImage(CarIcon.APP_ICON)
                .setOnClickListener {
                    CarToast.makeText(carContext, "Home gym pre-heated. Workout ready.", CarToast.LENGTH_LONG).show()
                }
                .build()
        )

        // Enhancement 4: Voice Assistant Shortcut
        itemListBuilder.addItem(
            GridItem.Builder()
                .setTitle("Voice Commands")
                .setText("Say 'Log Water'")
                .setImage(CarIcon.APP_ICON)
                .setOnClickListener {
                    screenManager.push(VoiceCommandScreen(carContext))
                }
                .build()
        )


        return GridTemplate.Builder()
            .setTitle("IronCore Dashboard")
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(itemListBuilder.build())
            .build()
    }
}
