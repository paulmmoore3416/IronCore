package com.ironcore.metrics.ui.device

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ironcore.metrics.R
import com.ironcore.metrics.ui.components.IronCoreButton
import com.ironcore.metrics.ui.components.IronCoreCard

@Composable
fun DeviceManagerScreen(
    onBack: () -> Unit
) {
    var isSyncing by remember { mutableStateOf(false) }
    var lastSync by remember { mutableStateOf("Just now") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Device Manager",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Fossil Gen 6 Status Card
        IronCoreCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Placeholder for Fossil Gen 6 Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Settings, 
                        contentDescription = null, 
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "Fossil Gen 6 - 44mm",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Venture Edition",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle, 
                        contentDescription = null, 
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Connected", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                SyncOption(
                    title = "Nutrition Sync",
                    description = "Sync daily meal plans & macros",
                    checked = true
                )
                SyncOption(
                    title = "Vitals Tracking",
                    description = "Sync Heart Rate & SpO2",
                    checked = true
                )
                SyncOption(
                    title = "Always-On Display",
                    description = "Optimize for Gen 6 AOD",
                    checked = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                IronCoreButton(
                    onClick = { 
                        isSyncing = true
                        // Simulate sync
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onSecondary)
                    } else {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh devices")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Force Sync Now")
                    }
                }
                
                Text(
                    text = "Last synced: $lastSync",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SyncOption(title: String, description: String, checked: Boolean) {
    var isChecked by remember { mutableStateOf(checked) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
        Switch(checked = isChecked, onCheckedChange = { isChecked = it })
    }
}
