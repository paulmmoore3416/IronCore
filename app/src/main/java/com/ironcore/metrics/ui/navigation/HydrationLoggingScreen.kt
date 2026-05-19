package com.ironcore.metrics.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironcore.metrics.ui.components.IronCoreCard
import com.ironcore.metrics.ui.dashboard.DashboardViewModel
import com.ironcore.metrics.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationLoggingScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val presets = listOf(
        HydrationPreset("Glass", "8oz", 236),
        HydrationPreset("Bottle", "12oz", 355),
        HydrationPreset("Large Bottle", "32oz", 946),
        HydrationPreset("Growler", "64oz", 1892)
    )

    var selectedPreset by remember { mutableStateOf(presets[0]) }
    var quantity by remember { mutableIntStateOf(1) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                listOf(DarkBackground, GoCyan.copy(alpha = 0.1f), DarkBackground)
            )
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Log Hydration", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        "Select Size",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                items(presets) { preset ->
                    IronCoreCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPreset = preset },
                        gradientColors = if (selectedPreset == preset) {
                            listOf(GoCyan.copy(alpha = 0.3f), GoCyan.copy(alpha = 0.1f))
                        } else {
                            listOf(GlassBackground, Color.Transparent)
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedPreset == preset) GoCyan else GlassBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(preset.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
                                Text(preset.label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Text("${preset.ml} ml", style = MaterialTheme.typography.headlineSmall, color = GoCyan)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Quantity", style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = GoCyan)
                        }
                        Text(
                            "$quantity",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        IconButton(onClick = { quantity++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = GoCyan)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            viewModel.addHydration(selectedPreset.ml * quantity)
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoCyan)
                    ) {
                        Text("LOG ${(selectedPreset.ml * quantity)} ML", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                
                // Enhancement 9: Zen Breathing Coach (Mini)
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                    ZenBreathingCoach()
                }
            }
        }
    }
}

@Composable
fun ZenBreathingCoach() {
    var phase by remember { mutableStateOf("Inhale") }
    // Simulation: In a real app, this would be an animation
    IronCoreCard(
        gradientColors = listOf(JavaBlue.copy(alpha = 0.1f), Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, JavaBlue.copy(alpha = 0.3f))
    ) {
        Text("Zen Breathing Coach", style = MaterialTheme.typography.titleMedium, color = JavaBlue)
        Text("Hydration is better with focus.", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { 0.5f },
                modifier = Modifier.size(80.dp),
                color = JavaBlue,
                strokeWidth = 8.dp
            )
            Text(phase, style = MaterialTheme.typography.labelLarge, color = Color.White)
        }
    }
}

data class HydrationPreset(val name: String, val label: String, val ml: Int)
