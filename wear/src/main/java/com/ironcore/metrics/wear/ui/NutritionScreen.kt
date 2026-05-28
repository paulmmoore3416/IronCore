package com.ironcore.metrics.wear.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.*
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items

@Composable
fun NutritionScreen(
    viewModel: WearViewModel = hiltViewModel()
) {
    val nutritionPlan by viewModel.nutritionPlan.collectAsState()

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = rememberScalingLazyListState()) }
    ) {
        if (nutritionPlan.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No meal plan synced", style = MaterialTheme.typography.body2)
            }
        } else {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 32.dp, start = 8.dp, end = 8.dp, bottom = 32.dp)
            ) {
                item {
                    Text(
                        text = "Today's Plan",
                        style = MaterialTheme.typography.title2,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(nutritionPlan) { meal ->
                    Card(
                        onClick = { /* Show ingredients dialog */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(text = meal.name, style = MaterialTheme.typography.title3)
                            Text(
                                text = "${meal.calories} kcal | P:${meal.protein.toInt()}g",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

data class WearMeal(
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val ingredients: List<String>
)
