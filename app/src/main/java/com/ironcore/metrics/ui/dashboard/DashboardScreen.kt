package com.ironcore.metrics.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironcore.metrics.ui.components.IronCoreCard
import com.ironcore.metrics.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.widget.Toast

import androidx.compose.ui.platform.LocalContext
import com.ironcore.metrics.MainActivity

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onMetricClick: (String) -> Unit = {}
) {
    val steps by viewModel.steps.collectAsState()
    val realtimeHr by viewModel.realtimeHeartRate.collectAsState()
    val isFocusMode by viewModel.isFocusMode.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val activeCaloriesBurned by viewModel.activeCaloriesBurned.collectAsState()
    val hydrationMl by viewModel.hydrationMl.collectAsState()
    val sleepMinutes by viewModel.sleepMinutes.collectAsState()
    val spO2 by viewModel.spO2.collectAsState()
    val respiratoryRate by viewModel.respiratoryRate.collectAsState()
    val recoveryScore by viewModel.recoveryScore.collectAsState()
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()
    
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        viewModel.checkPermissionsAndFetchData()
    }

    // Enhancement 8: Dynamic Glass Background (Time-based variation)
    val hour = java.time.LocalTime.now().hour
    val backgroundColors = if (isFocusMode) {
        listOf(Color(0xFF1A0000), Color(0xFF330000))
    } else if (hour in 6..18) {
        listOf(DarkBackground, Color(0xFF162030)) // Day-time blue tint
    } else {
        listOf(DarkBackground, Color(0xFF0D1117)) // Night-time deep black
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Brush.verticalGradient(backgroundColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            HeaderSection(
                isFocusMode = isFocusMode,
                onRefresh = { viewModel.checkPermissionsAndFetchData() },
                onToggleFocus = { viewModel.toggleFocusMode() }
            )

            if (!permissionsGranted) {
                PermissionWarning(context = context, viewModel = viewModel)
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    MetricCard(
                        title = "Steps",
                        value = "$steps",
                        icon = Icons.Default.ElectricBolt,
                        trend = "Real-time sync",
                        themeColor = GoCyan,
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onMetricClick("steps") 
                        }
                    )
                }
                item {
                    MetricCard(
                        title = "BPM",
                        value = "$realtimeHr",
                        icon = Icons.Default.Favorite,
                        trend = if (isFocusMode) "HIGH INTENSITY" else "Live Reading",
                        themeColor = JavaOrange,
                        isPulsing = true,
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onMetricClick("bpm") 
                        }
                    )
                }
                item {
                    MetricCard(
                        title = "Active Energy",
                        value = if (activeCaloriesBurned > 0.0) String.format("%.0f kcal", activeCaloriesBurned) else "--",
                        icon = Icons.Default.ElectricBolt,
                        trend = "Total Burned",
                        themeColor = Html5Orange,
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onMetricClick("energy") 
                        }
                    )
                }
                item {
                    MetricCard(
                        title = "Body Weight",
                        value = if (weight > 0.0) String.format("%.1f kg", weight) else "--",
                        icon = Icons.Default.Scale,
                        trend = "Latest logged",
                        themeColor = JavaBlue,
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onMetricClick("weight") 
                        }
                    )
                }

                item {
                    MetricCard(
                        title = "Sleep",
                        value = "${sleepMinutes / 60}h ${sleepMinutes % 60}m",
                        icon = Icons.Default.Favorite, // Placeholder for Sleep icon
                        trend = "Last Night",
                        themeColor = JavaBlue,
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onMetricClick("sleep") 
                        }
                    )
                }

                item {
                    MetricCard(
                        title = "SpO2",
                        value = if (spO2 > 0.0) "$spO2%" else "--",
                        icon = Icons.Default.Timeline, // Placeholder for SpO2 icon
                        trend = "Oxygen Level",
                        themeColor = GoCyan,
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onMetricClick("spo2") 
                        }
                    )
                }

                item {
                    MetricCard(
                        title = "Resp Rate",
                        value = if (respiratoryRate > 0.0) "$respiratoryRate/m" else "--",
                        icon = Icons.Default.ElectricBolt, // Placeholder
                        trend = "Breaths/min",
                        themeColor = JavaOrange,
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onMetricClick("respiratory_rate") 
                        }
                    )
                }
                
                // Enhancement 2 & 14: Hydration Visualizer with Detail Navigation
                item(span = { GridItemSpan(2) }) {
                    HydrationVisualizer(
                        current = hydrationMl,
                        target = viewModel.hydrationTarget,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onMetricClick("hydration")
                        },
                        onAdd = { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.addHydration(250) 
                        }
                    )
                }

                // Enhancement 10: Muscle Map Visualizer
                item {
                    MuscleMapCard(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onMetricClick("ar")
                    })
                }

                // Enhancement 11: Workout Streak Calendar
                item {
                    StreakCalendarCard()
                }

                // Enhancement 6: Social Leaderboard Simulation
                item(span = { GridItemSpan(2) }) {
                    SocialLeaderboardCard(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onMetricClick("social")
                    })
                }


                // Enhancement 12: Pro Performance Summary
                item(span = { GridItemSpan(2) }) {
                    ProPerformanceSummary()
                }

                item {
                    DetailedSectionCard(
                        title = "Recovery Advisor",
                        subtitle = "Readiness: $recoveryScore%",
                        themeColor = GoCyan
                    )
                }
                
                item {
                    IronCoreCard(
                        gradientColors = listOf(Color(0x44FF0000), Color.Transparent),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                    ) {
                        Text("SOS Emergency", color = Color.Red, style = MaterialTheme.typography.titleMedium)
                        Text("One-tap alert", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast.makeText(context, "Emergency SOS triggered", Toast.LENGTH_LONG).show() 
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("TRIGGER SOS", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MuscleMapCard(onClick: () -> Unit = {}) {
    IronCoreCard(
        modifier = Modifier.clickable { onClick() },
        gradientColors = listOf(JavaOrange.copy(alpha = 0.1f), Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, JavaOrange.copy(alpha = 0.3f))
    ) {
        Text("Muscle Activation", style = MaterialTheme.typography.titleMedium, color = JavaOrange)
        Text("Upper Body Focus", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(5) {
                Box(Modifier.size(20.dp).background(JavaOrange.copy(alpha = (30..100).random() / 100f), shape = androidx.compose.foundation.shape.CircleShape))
            }
        }
    }
}

@Composable
fun StreakCalendarCard() {
    IronCoreCard(
        gradientColors = listOf(GoCyan.copy(alpha = 0.1f), Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoCyan.copy(alpha = 0.3f))
    ) {
        Text("Workout Streak", style = MaterialTheme.typography.titleMedium, color = GoCyan)
        Text("12 Days Active", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(7) { i ->
                Box(Modifier.size(10.dp).background(if (i < 5) GoCyan else Color.Gray, shape = androidx.compose.foundation.shape.CircleShape))
            }
        }
    }
}

@Composable
fun ProPerformanceSummary() {
    IronCoreCard(
        gradientColors = listOf(Html5Orange.copy(alpha = 0.1f), Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, Html5Orange.copy(alpha = 0.3f))
    ) {
        Text("Pro Performance", style = MaterialTheme.typography.titleMedium, color = Html5Orange)
        Spacer(modifier = Modifier.height(8.dp))
        Text("You are 15% more active than last week. Your recovery is optimal for tomorrow.", style = MaterialTheme.typography.bodyMedium, color = Color.White)
    }
}

@Composable
fun SocialLeaderboardCard(onClick: () -> Unit = {}) {
    IronCoreCard(
        modifier = Modifier.clickable { onClick() },
        gradientColors = listOf(JavaBlue.copy(alpha = 0.1f), Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, JavaBlue.copy(alpha = 0.3f))
    ) {
        Text("IronCore Community", style = MaterialTheme.typography.titleMedium, color = JavaBlue)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("1. Paul (You)", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Text("12,450 pts", style = MaterialTheme.typography.bodyMedium, color = GoCyan)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("2. Sarah", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text("11,200 pts", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@Composable
fun HeaderSection(
    isFocusMode: Boolean,
    onRefresh: () -> Unit,
    onToggleFocus: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = if (isFocusMode) "FOCUS ACTIVE" else "IronCore Overview",
                style = MaterialTheme.typography.headlineLarge,
                color = if (isFocusMode) Color.Red else MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM dd")),
                style = MaterialTheme.typography.labelLarge,
                color = GoCyan
            )
        }
        
        Row {
            IconButton(onClick = onToggleFocus) {
                Icon(
                    if (isFocusMode) Icons.Default.FitnessCenter else Icons.Default.Timeline,
                    contentDescription = "Focus",
                    tint = if (isFocusMode) Color.Red else GoCyan
                )
            }
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Timeline, contentDescription = "Refresh", tint = GoCyan)
            }
        }
    }
}

@Composable
fun PermissionWarning(context: android.content.Context, viewModel: DashboardViewModel) {
    Spacer(modifier = Modifier.height(16.dp))
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Health Connect Access Required",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = "To provide live fitness and health tracking, IronCore needs permission to access your Health Connect data.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { 
                    var ctx = context
                    while (ctx is android.content.ContextWrapper) {
                        if (ctx is MainActivity) {
                            break
                        }
                        ctx = ctx.baseContext
                    }
                    (ctx as? MainActivity)?.requestPermissions {
                        // Callback: refresh data after permissions are granted
                        viewModel.checkPermissionsAndFetchData()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Grant Permissions")
            }
        }
    }
}

@Composable
fun HydrationVisualizer(current: Int, target: Int, onClick: () -> Unit, onAdd: () -> Unit) {
    val progress = (current.toFloat() / target).coerceIn(0f, 1f)
    IronCoreCard(
        modifier = Modifier.clickable { onClick() },
        gradientColors = listOf(GoCyan.copy(alpha = 0.2f), Color.Transparent)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Daily Hydration", style = MaterialTheme.typography.titleMedium, color = GoCyan)
                Text("$current / $target ml", style = MaterialTheme.typography.headlineSmall)
            }
            IconButton(onClick = onAdd) {
                Icon(Icons.Default.LocalDrink, contentDescription = "Add Water", tint = GoCyan)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(12.dp),
            color = GoCyan,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}


@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    trend: String,
    themeColor: Color = CobaltBlue,
    isPulsing: Boolean = false,
    onClick: () -> Unit = {}
) {
    IronCoreCard(
        modifier = Modifier.clickable { onClick() },
        gradientColors = listOf(
            themeColor.copy(alpha = 0.15f),
            Color.Transparent
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, themeColor.copy(alpha = 0.3f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(icon, contentDescription = null, tint = themeColor, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
        Text(text = trend, style = MaterialTheme.typography.bodySmall, color = themeColor.copy(alpha = 0.8f))
    }
}


@Composable
fun DetailedSectionCard(
    title: String,
    subtitle: String,
    themeColor: Color = CobaltBlue
) {
    IronCoreCard(
        gradientColors = listOf(
            themeColor.copy(alpha = 0.1f),
            Color.Transparent
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, themeColor.copy(alpha = 0.3f))
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = themeColor)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { 0.7f },
            modifier = Modifier.fillMaxWidth(),
            color = themeColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
