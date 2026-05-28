package com.ironcore.metrics.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.car.app.CarToast

/**
 * Screen demonstrating Voice to Text functionality using SearchTemplate 
 * as a proxy for "Touch to Talk" capability.
 */
class VoiceCommandScreen(carContext: CarContext) : Screen(carContext) {

    private var lastCommand: String = ""

    override fun onGetTemplate(): Template {
        return SearchTemplate.Builder(
            object : SearchTemplate.SearchCallback {
                override fun onSearchTextChanged(searchText: String) {
                    lastCommand = searchText
                }

                override fun onSearchSubmitted(searchTerm: String) {
                    processVoiceCommand(searchTerm)
                }
            }
        )
            .setHeaderAction(Action.BACK)
            .setShowKeyboardByDefault(false)
            .setItemList(
                ItemList.Builder()
                    .addItem(
                        Row.Builder()
                            .setTitle("Say commands like:")
                            .addText("'Start Hypertrophy Workout'")
                            .addText("'Log 500ml of water'")
                            .addText("'Check my heart rate'")
                            .addText("'Find Mediterranean food'")
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun processVoiceCommand(command: String) {
        val lowerCommand = command.lowercase()
        when {
            lowerCommand.contains("water") || lowerCommand.contains("hydration") -> {
                CarToast.makeText(carContext, "Hydration logged via Voice", CarToast.LENGTH_SHORT).show()
                finish()
            }
            lowerCommand.contains("workout") || lowerCommand.contains("start") -> {
                CarToast.makeText(carContext, "Workout session started", CarToast.LENGTH_SHORT).show()
                screenManager.push(PostWorkoutScreen(carContext))
            }
            lowerCommand.contains("vitals") || lowerCommand.contains("heart") -> {
                screenManager.push(VitalsScreen(carContext))
            }
            lowerCommand.contains("food") || lowerCommand.contains("fueling") -> {
                screenManager.push(FuelingMapScreen(carContext, "Mediterranean"))
            }
            else -> {
                CarToast.makeText(carContext, "Command interpreted: $command", CarToast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
