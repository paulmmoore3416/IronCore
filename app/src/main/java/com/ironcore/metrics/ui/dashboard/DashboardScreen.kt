package com.ironcore.metrics.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironcore.metrics.ui.components.IronCoreCard
import com.ironcore.metrics.ui.theme.CobaltBlue
import com.ironcore.metrics.ui.theme.Granite
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val steps by viewModel.steps.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val activeCaloriesBurned by viewModel.activeCaloriesBurned.collectAsState()
    val consumedCalories by viewModel.consumedCalories.collectAsState()
    val energyBalance by viewModel.energyBalance.collectAsState()
    val hydrationMl by viewModel.hydrationMl.collectAsState()
    val recoveryScore by viewModel.recoveryScore.collectAsState()
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkPermissionsAndFetchData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "IronCore Overview",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM dd")),
            style = MaterialTheme.typography.labelLarge,
            color = CobaltBlue
        )

        if (!permissionsGranted) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Health Connect permissions required for live data.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Metric Highlights (Granite & Blue Style)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            item {
                MetricCard(
                    title = "Steps (7 Days)",
                    value = "$steps",
                    icon = Icons.Default.ElectricBolt,
                    trend = "Active"
                )
            }
            item {
                MetricCard(
                    title = "Latest HR",
                    value = if (heartRate > 0) "$heartRate BPM" else "--",
                    icon = Icons.Default.Favorite,
                    trend = "Recent Reading"
                )
            }
            item {
                MetricCard(
                    title = "Active Energy",
                    value = if (activeCaloriesBurned > 0.0) String.format("%.0f kcal", activeCaloriesBurned) else "--",
                    icon = Icons.Default.ElectricBolt,
                    trend = "Total Burned (7d)"
                )
            }
            item {
                MetricCard(
                    title = "Body Weight",
                    value = if (weight > 0.0) String.format("%.1f kg", weight) else "--",
                    icon = Icons.Default.Scale,
                    trend = "Latest logged"
                )
            }
            item {
                MetricCard(
                    title = "Training Load",
                    value = "Optimal",
                    icon = Icons.Default.Timeline,
                    trend = "3 sessions this week"
                )
            }
            item {
                MetricCard(
                    title = "Hydration",
                    value = "$hydrationMl ml",
                    icon = Icons.Default.ElectricBolt,
                    trend = "Target: 2000ml"
                )
            }

            item {
                DetailedSectionCard(
                    title = "Recovery Advisor",
                    subtitle = "Readiness: $recoveryScore%"
                )
            }
            item {
                DetailedSectionCard(
                    title = "Energy Balance",
                    subtitle = "Consumed: $consumedCalories | Net: $energyBalance kcal"
                )
            }
            item {
                DetailedSectionCard(
                    title = "Sleep Score",
                    subtitle = "84/100 - Good"
                )
            }
        }
    }
}


@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    trend: String
) {
    IronCoreCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(icon, contentDescription = null, tint = CobaltBlue, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, style = MaterialTheme.typography.labelMedium, color = Granite)
        Text(text = value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
        Text(text = trend, style = MaterialTheme.typography.bodySmall, color = CobaltBlue)
    }
}

@Composable
fun DetailedSectionCard(
    title: String,
    subtitle: String
) {
    IronCoreCard {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = CobaltBlue)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        // Placeholder for small graph/bar
        LinearProgressIndicator(
            progress = 0.7f,
            modifier = Modifier.fillMaxWidth(),
            color = CobaltBlue,
            trackColor = Granite
        )
    }
}
