package com.ironcore.metrics.ui.nutrition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironcore.metrics.ui.components.IronCoreButton
import com.ironcore.metrics.ui.components.IronCoreCard
import androidx.fragment.app.FragmentActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = hiltViewModel(),
    onNavigateToMealDetail: (String) -> Unit = {}
) {
    val meals by viewModel.meals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedCuisine by viewModel.selectedCuisine.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    // Snackbar setup
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    // Biometric Lock State
    var isLocked by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
Icon(Icons.Default.Lock, contentDescription = "AI Meal Planner locked", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Nutrition Plan Locked", style = MaterialTheme.typography.headlineSmall)
            Text("Secure biometric authentication required", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(24.dp))
            IronCoreButton(onClick = {
                activity?.let {
                    val manager = com.ironcore.metrics.ui.security.BiometricAuthManager(context)
                    manager.authenticateHealthData(it, onSuccess = { isLocked = false })
                }
            }) {
                Text("Unlock with Biometrics")
            }
        }
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Meal Plans",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Row {
                        IconButton(onClick = { /* Handle Export */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Export")
                        }
                        IconButton(onClick = { /* Handle Import */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Import")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Cuisine Selector
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCuisine,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ethnic Cuisine") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        viewModel.cuisines.forEach { cuisine ->
                            DropdownMenuItem(
                                text = { Text(cuisine) },
                                onClick = {
                                    viewModel.setCuisine(cuisine)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                IronCoreButton(
                    onClick = { viewModel.generatePlan() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onSecondary)
                    } else {
                        Text("Generate $selectedCuisine Plan")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val morningMeals = meals.filterIndexed { index, _ -> index == 0 || index == 4 }
                val afternoonMeals = meals.filterIndexed { index, _ -> index == 1 || index == 3 }
                val eveningMeals = meals.filterIndexed { index, _ -> index == 2 || index == 5 || index == 6 }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (morningMeals.isNotEmpty()) {
                        item {
                            Text("Morning Fuel", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        items(morningMeals) { meal ->
                            MealCard(
                                meal = meal, 
                                onClick = { 
                                    val mealJson = com.google.gson.Gson().toJson(meal)
                                    onNavigateToMealDetail(mealJson) 
                                },
                                onDelete = { viewModel.deleteMeal(meal) }
                            )
                        }
                    }

                    if (afternoonMeals.isNotEmpty()) {
                        item {
                            Text("Afternoon Sustenance", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
                        }
                        items(afternoonMeals) { meal ->
                            MealCard(
                                meal = meal, 
                                onClick = { 
                                    val mealJson = com.google.gson.Gson().toJson(meal)
                                    onNavigateToMealDetail(mealJson) 
                                },
                                onDelete = { viewModel.deleteMeal(meal) }
                            )
                        }
                    }

                    if (eveningMeals.isNotEmpty()) {
                        item {
                            Text("Evening Recovery", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
                        }
                        items(eveningMeals) { meal ->
                            MealCard(
                                meal = meal, 
                                onClick = { 
                                    val mealJson = com.google.gson.Gson().toJson(meal)
                                    onNavigateToMealDetail(mealJson) 
                                },
                                onDelete = { viewModel.deleteMeal(meal) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealCard(meal: com.ironcore.metrics.data.remote.dto.RemoteMeal, onClick: () -> Unit, onDelete: () -> Unit) {
    IronCoreCard(modifier = Modifier.clickable { onClick() }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(text = meal.name, style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Detailed Macros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroItem("CALORIES", "${meal.calories}")
                MacroItem("PROTEIN", "${meal.protein}g")
                MacroItem("CARBS", "${meal.carbs}g")
                MacroItem("FAT", "${meal.fat}g")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = "Key Ingredients", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Display ingredients as small chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                val displayCount = 3
                meal.ingredients.take(displayCount).forEach { ingredient ->
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = ingredient, 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                if (meal.ingredients.size > displayCount) {
                     Text(text = "+${meal.ingredients.size - displayCount}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { /* Edit Dialog */ }) {
                    Text("Edit Meal")
                }
            }
        }
    }
}

@Composable
fun MacroItem(label: String, value: String) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    }
}
