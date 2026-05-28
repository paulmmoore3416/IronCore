package com.ironcore.metrics.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ironcore.metrics.ui.theme.GoCyan
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HeaderSection(
    isFocusMode: Boolean,
    isOnline: Boolean,
    isWearableConnected: Boolean,
    onRefresh: () -> Unit,
    onToggleFocus: () -> Unit,
    onQuickAddHydration: () -> Unit
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM dd")),
                    style = MaterialTheme.typography.labelLarge,
                    color = GoCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Connectivity Pills
                StatusIndicator(
                    text = if (isOnline) "ONLINE" else "OFFLINE",
                    color = if (isOnline) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                Spacer(modifier = Modifier.width(4.dp))
                StatusIndicator(
                    text = if (isWearableConnected) "WATCH" else "NO WATCH",
                    color = if (isWearableConnected) Color(0xFF2196F3) else Color(0xFF9E9E9E)
                )
            }
        }
        
        Row {
            IconButton(onClick = onQuickAddHydration) {
                Icon(Icons.Default.LocalDrink, contentDescription = "Quick Add Water", tint = GoCyan)
            }
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
fun StatusIndicator(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}
