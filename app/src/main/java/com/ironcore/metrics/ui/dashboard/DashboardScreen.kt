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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ironcore.metrics.ui.components.IronCoreCard
import com.ironcore.metrics.ui.theme.CobaltBlue
import com.ironcore.metrics.ui.theme.Granite

@Composable
fun DashboardScreen() {
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
            text = "Tuesday, May 19",
            style = MaterialTheme.typography.labelLarge,
            color = CobaltBlue
        )

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
                    title = "Active Energy",
                    value = "850 kcal",
                    icon = Icons.Default.ElectricBolt,
                    trend = "+12% from yesterday"
                )
            }
            item {
                MetricCard(
                    title = "Heart Rate",
                    value = "72 BPM",
                    icon = Icons.Default.Favorite,
                    trend = "Resting: 64"
                )
            }
            item {
                MetricCard(
                    title = "Body Weight",
                    value = "88.5 kg",
                    icon = Icons.Default.Scale,
                    trend = "-0.2 kg"
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
                DetailedSectionCard(
                    title = "Nutrition Balance",
                    subtitle = "Protein focus reached"
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
