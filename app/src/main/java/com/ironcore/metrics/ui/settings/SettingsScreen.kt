package com.ironcore.metrics.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironcore.metrics.ui.components.IronCoreButton
import com.ironcore.metrics.ui.components.IronCoreCard

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()

    var isDarkModeEnabled by remember { mutableStateOf(true) }
    var isOfflineModeEnabled by remember { mutableStateOf(false) }
    var useAiCore by remember { mutableStateOf(true) }

    var name by remember(userProfile) { mutableStateOf(userProfile?.name ?: "") }
    var age by remember(userProfile) { mutableStateOf(userProfile?.age?.toString() ?: "") }
    var weight by remember(userProfile) { mutableStateOf(userProfile?.weightKg?.toString() ?: "") }
    var height by remember(userProfile) { mutableStateOf(userProfile?.heightCm?.toString() ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("User Profile", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                IronCoreCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = age,
                                onValueChange = { age = it },
                                label = { Text("Age") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                label = { Text("Weight (kg)") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text("Height (cm)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                viewModel.updateProfile(
                                    name,
                                    age.toIntOrNull() ?: 30,
                                    weight.toFloatOrNull() ?: 70f,
                                    height.toFloatOrNull() ?: 175f,
                                    "Maintenance"
                                )
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Save Profile")
                        }
                    }
                }
            }

            item {
                IronCoreCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dark Mode", style = MaterialTheme.typography.titleMedium)
                        }
                        Switch(
                            checked = isDarkModeEnabled,
                            onCheckedChange = { isDarkModeEnabled = it }
                        )
                    }
                }
            }
            
            item {
                IronCoreCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudOff, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Offline Mode", style = MaterialTheme.typography.titleMedium)
                        }
                        Switch(
                            checked = isOfflineModeEnabled,
                            onCheckedChange = { isOfflineModeEnabled = it }
                        )
                    }
                }
            }

            item {
                Text("AI Integrations", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                IronCoreCard {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Memory, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("On-Device AI (AICore)", style = MaterialTheme.typography.titleMedium)
                                    Text("Use AICore for fast, local responses", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Switch(
                                checked = useAiCore,
                                onCheckedChange = { useAiCore = it }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Memory, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Remote AI (Ollama/Homelab)", style = MaterialTheme.typography.titleMedium)
                                    Text("Use Granite Code 3b model server", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Switch(
                                checked = !useAiCore,
                                onCheckedChange = { useAiCore = !it }
                            )
                        }
                    }
                }
            }

            item {
                Text("Media Integrations", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                IronCoreCard {
                    Column {
                        IronCoreButton(
                            onClick = { 
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://spotify.com/login"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.LibraryMusic, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Connect Spotify")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        IronCoreButton(
                            onClick = { 
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://music.youtube.com/login"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.LibraryMusic, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Connect YouTube Music")
                        }
                    }
                }
            }

            item {
                Text("Account", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                IronCoreCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Guest User", style = MaterialTheme.typography.titleMedium)
                            Text("Sign in to sync data", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    IronCoreButton(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Sign In")
                    }
                }
            }
        }
    }
}
