package com.ironcore.metrics.ui.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.ironcore.metrics.data.remote.dto.RemoteMeal
import com.ironcore.metrics.ui.components.IronCoreButton
import com.ironcore.metrics.ui.components.IronCoreCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    mealJson: String,
    onBack: () -> Unit
) {
    val meal = remember { Gson().fromJson(mealJson, RemoteMeal::class.java) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Enhancement: AI Chef Bottom Sheet State
    var showChefSheet by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(meal.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showChefSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Chat, contentDescription = "Ask AI Chef")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Image
            item {
                AsyncImage(
                    model = "https://loremflickr.com/600/400/food,${meal.name.split(" ").firstOrNull() ?: "dish"}",
                    contentDescription = meal.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            // Enhancement: Badges & Summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge(text = meal.prepTime ?: "15 mins", color = MaterialTheme.colorScheme.tertiaryContainer)
                    Badge(text = meal.difficulty ?: "Medium", color = MaterialTheme.colorScheme.secondaryContainer)
                    Badge(text = "${meal.calories} kcal", color = MaterialTheme.colorScheme.primaryContainer)
                }
            }

            // Enhancement: Macro Distribution Visualizer
            item {
                IronCoreCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Macro Distribution", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val totalMacros = meal.protein + meal.carbs + meal.fat
                        val pPct = if (totalMacros > 0) (meal.protein / totalMacros) else 0f
                        val cPct = if (totalMacros > 0) (meal.carbs / totalMacros) else 0f
                        val fPct = if (totalMacros > 0) (meal.fat / totalMacros) else 0f

                        Row(
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp))
                        ) {
                            Box(modifier = Modifier.weight(if (pPct > 0) pPct else 0.1f).fillMaxHeight().background(Color(0xFFE57373)))
                            Box(modifier = Modifier.weight(if (cPct > 0) cPct else 0.1f).fillMaxHeight().background(Color(0xFF64B5F6)))
                            Box(modifier = Modifier.weight(if (fPct > 0) fPct else 0.1f).fillMaxHeight().background(Color(0xFFFFD54F)))
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Protein: ${meal.protein}g", color = Color(0xFFE57373), style = MaterialTheme.typography.bodySmall)
                            Text("Carbs: ${meal.carbs}g", color = Color(0xFF64B5F6), style = MaterialTheme.typography.bodySmall)
                            Text("Fat: ${meal.fat}g", color = Color(0xFFFFD54F), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Ingredients & Grocery Export
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Ingredients", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Exported ${meal.ingredients.size} items to Grocery List")
                        }
                    }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Export to Grocery", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            items(meal.ingredients) { ingredient ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(MaterialTheme.colorScheme.primary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = ingredient, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Cooking Instructions
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Instructions", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                IronCoreCard {
                    Text(
                        text = meal.cookingInstructions ?: "No instructions provided. Ask the AI Chef!",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(80.dp)) // padding for FAB
            }
        }

        // Enhancement: AI Chef Bottom Sheet
        if (showChefSheet) {
            ModalBottomSheet(onDismissRequest = { showChefSheet = false }) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("👨‍🍳 Ask AI Chef", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Need a substitution for an allergy? Want to make it vegan? The AI Chef can regenerate this specific meal to fit your precise needs.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    IronCoreButton(onClick = { 
                        showChefSheet = false
                        scope.launch { snackbarHostState.showSnackbar("Chef suggests replacing primary protein with Tofu/Seitan for a vegan twist.") }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Suggest Vegan Substitution")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = { showChefSheet = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Close")
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}