package com.ironcore.metrics.ui.nutrition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironcore.metrics.ui.components.IronCoreButton
import com.ironcore.metrics.ui.components.IronCoreCard

@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val meals by viewModel.meals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Nutrition & AI Plans",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        IronCoreButton(
            onClick = { viewModel.generatePlan() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onSecondary)
            } else {
                Text("Generate AI Meal Plan")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(meals) { meal ->
                IronCoreCard {
                    Text(text = meal.name, style = MaterialTheme.typography.titleMedium)
                    Text(text = "${meal.calories} kcal | P: ${meal.protein}g | C: ${meal.carbs}g | F: ${meal.fat}g", 
                         style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
