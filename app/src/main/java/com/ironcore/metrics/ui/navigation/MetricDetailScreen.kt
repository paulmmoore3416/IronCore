package com.ironcore.metrics.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ironcore.metrics.ui.components.IronCoreCard
import com.ironcore.metrics.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricDetailScreen(
    metricType: String,
    onBack: () -> Unit
) {
    var inputValue by remember { mutableStateOf("") }
    
    val themeColor = when(metricType.lowercase()) {
        "steps" -> GoCyan
        "bpm" -> JavaOrange
        "energy" -> Html5Orange
        "weight" -> JavaBlue
        "hydration" -> GoCyan
        "nutrition" -> Html5DeepRed
        "sleep" -> JavaBlue
        "spo2" -> GoCyan
        "respiratory_rate" -> JavaOrange
        else -> CobaltBlue
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                listOf(DarkBackground, themeColor.copy(alpha = 0.1f), DarkBackground)
            )
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(metricType.replaceFirstChar { it.uppercase() }, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IronCoreCard(
                    gradientColors = listOf(themeColor.copy(alpha = 0.2f), Color.Transparent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, themeColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "Log New Data",
                        style = MaterialTheme.typography.titleLarge,
                        color = themeColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        label = { Text("Enter $metricType value") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColor,
                            unfocusedBorderColor = themeColor.copy(alpha = 0.5f)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { onBack() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor)
                    ) {
                        Text("SAVE ENTRY", color = Color.Black)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Enhancement 5: Predictive Recovery Graph (Simulated)
                Text("Predictive Recovery Trend", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.Black.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
                        .padding(16.dp)
                ) {
                    // Simulating a graph with rows
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        repeat(7) { index ->
                            val heightFactor = (30..100).random() / 100f
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .fillMaxHeight(heightFactor)

                                    .background(
                                        Brush.verticalGradient(listOf(themeColor, themeColor.copy(alpha = 0.3f))),
                                        shape = MaterialTheme.shapes.extraSmall
                                    )
                            )
                        }
                    }
                }
                Text("Simulation: Based on previous $metricType logs", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}
