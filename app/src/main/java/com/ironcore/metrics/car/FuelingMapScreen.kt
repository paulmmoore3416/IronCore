package com.ironcore.metrics.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.car.app.model.Action
import androidx.core.graphics.drawable.IconCompat
import com.ironcore.metrics.R

/**
 * Addition 1: Post-Workout Fueling Map
 * Helps users find restaurants matching their AI meal plan cuisine.
 */
class FuelingMapScreen(carContext: CarContext, private val cuisine: String) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val itemListBuilder = ItemList.Builder()

        // Mock locations based on cuisine
        val restaurants = listOf(
            "The $cuisine Spot" to "1.2 miles",
            "$cuisine Kitchen" to "2.5 miles",
            "Authentic $cuisine" to "3.1 miles"
        )

        restaurants.forEach { (name, distance) ->
            itemListBuilder.addItem(
                Row.Builder()
                    .setTitle(name)
                    .addText(distance)
                    .setOnClickListener {
                        carContext.startCarApp(
                            android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                data = android.net.Uri.parse("geo:0,0?q=$name")
                            }
                        )
                    }
                    .build()
            )
        }

        return PlaceListMapTemplate.Builder()
            .setTitle("Fueling: $cuisine")
            .setHeaderAction(Action.BACK)
            .setItemList(itemListBuilder.build())
            .build()
    }
}
