package com.ironcore.metrics.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironcore.metrics.ui.components.IronCoreCard
import com.ironcore.metrics.ui.theme.*

@Composable
fun WorkoutLoggerScreen(
    viewModel: WorkoutViewModel = hiltViewModel(),
    onNavigateToModality: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Training Command Center",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your centralized dashboard for all physical modalities.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Metric Graphs (Simulated)
            item {
                IronCoreCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Today's Volume Load", style = MaterialTheme.typography.titleMedium, color = CobaltBlue)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val days = listOf("M", "T", "W", "T", "F", "S", "S")
                            val heights = listOf(0.4f, 0.7f, 0.2f, 0.9f, 0.5f, 0.8f, 0.3f) // Simulated volume
                            
                            days.forEachIndexed { index, day ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .width(24.dp)
                                            .fillMaxHeight(heights[index])
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(if (index == 3) CobaltBlue else Granite.copy(alpha = 0.5f))
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(day, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // 7-Day Routine Preview
            item {
                IronCoreCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Current 7-Day Routine", style = MaterialTheme.typography.titleMedium, color = JavaOrange)
                            TextButton(onClick = { /* View past routines */ }) {
                                Text("History")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val activeDays = listOf("Push", "Pull", "Rest", "Legs", "Cardio", "Full Body", "Rest")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            activeDays.forEachIndexed { index, focus ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (index == 3) JavaOrange.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                        border = if (index == 3) androidx.compose.foundation.BorderStroke(1.dp, JavaOrange) else null
                                    ) {
                                        Text(
                                            text = focus.take(4),
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = if (index == 3) JavaOrange else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Navigation Tree
            item {
                Text(
                    text = "Specialized Modalities", 
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
            
            item {
                val modalities = listOf(
                    Triple("Weight Training", Icons.Default.FitnessCenter, JavaBlue),
                    Triple("Calisthenics", Icons.Default.SportsGymnastics, Html5Orange),
                    Triple("Running", Icons.Default.DirectionsRun, GoCyan),
                    Triple("Cycling", Icons.Default.DirectionsBike, JavaOrange),
                    Triple("Swimming", Icons.Default.Pool, CobaltBlue),
                    Triple("Hiking", Icons.Default.Landscape, Html5DeepRed),
                    Triple("Chest Focus", Icons.Default.FitnessCenter, JavaBlue),
                    Triple("Back Focus", Icons.Default.FitnessCenter, CobaltBlue),
                    Triple("Arms Focus", Icons.Default.FitnessCenter, GoCyan),
                    Triple("Legs Focus", Icons.Default.FitnessCenter, JavaOrange),
                    Triple("Abs Focus", Icons.Default.FitnessCenter, Html5Orange),
                    Triple("Shoulders Focus", Icons.Default.FitnessCenter, Html5DeepRed),
                    Triple("Neck & Traps", Icons.Default.FitnessCenter, Granite)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(800.dp), // Increased height for more items
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(modalities) { (name, icon, color) ->
                        ModalityCard(name = name, icon = icon, color = color) {
                            onNavigateToModality(name)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModalityCard(name: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    IronCoreCard(modifier = Modifier.clickable { onClick() }) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = name, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
