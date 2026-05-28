package com.ironcore.metrics.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import com.ironcore.metrics.R
import com.ironcore.metrics.data.auth.AuthState
import com.ironcore.metrics.data.model.UnitSystem
import com.ironcore.metrics.ui.components.IronCoreButton
import com.ironcore.metrics.ui.components.IronCoreCard

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val unitSystem by viewModel.unitSystem.collectAsState()

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
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(stringResource(R.string.user_profile), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                IronCoreCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(stringResource(R.string.name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = age,
                                onValueChange = { age = it },
                                label = { Text(stringResource(R.string.age)) },
                                modifier = Modifier.weight(1f)
                            )
                            val weightLabel = if (unitSystem == UnitSystem.METRIC) "Weight (kg)" else "Weight (lb)"
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                label = { Text(weightLabel) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        val heightLabel = if (unitSystem == UnitSystem.METRIC) "Height (cm)" else "Height (in)"
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text(heightLabel) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                val validatedAge = age.toIntOrNull()
                                val validatedWeight = weight.toFloatOrNull()
                                val validatedHeight = height.toFloatOrNull()
                                
                                when {
                                    name.isBlank() -> {
                                        Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show()
                                    }
                                    validatedAge == null || validatedAge < 13 || validatedAge > 120 -> {
                                        Toast.makeText(context, "Age must be between 13-120", Toast.LENGTH_SHORT).show()
                                    }
                                    validatedWeight == null || validatedWeight < 30 || validatedWeight > 300 -> {
                                        Toast.makeText(context, "Weight must be between 30-300 kg", Toast.LENGTH_SHORT).show()
                                    }
                                    validatedHeight == null || validatedHeight < 100 || validatedHeight > 250 -> {
                                        Toast.makeText(context, "Height must be between 100-250 cm", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        viewModel.updateProfile(
                                            name,
                                            validatedAge,
                                            validatedWeight,
                                            validatedHeight,
                                            "Maintenance"
                                        )
                                        Toast.makeText(context, "Profile saved successfully", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(stringResource(R.string.save_profile))
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
                Icon(Icons.Default.DarkMode, contentDescription = "Dark mode toggle")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.dark_mode), style = MaterialTheme.typography.titleMedium)
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
                Icon(Icons.Default.CloudOff, contentDescription = "Offline mode toggle")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.offline_mode), style = MaterialTheme.typography.titleMedium)
                        }
                        Switch(
                            checked = isOfflineModeEnabled,
                            onCheckedChange = { isOfflineModeEnabled = it }
                        )
                    }
                }
            }

            item {
                Text(stringResource(R.string.unit_system), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                IronCoreCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = "Advanced settings")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (unitSystem == UnitSystem.METRIC) stringResource(R.string.metric) else stringResource(R.string.imperial),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Switch(
                            checked = unitSystem == UnitSystem.IMPERIAL,
                            onCheckedChange = { viewModel.toggleUnitSystem() }
                        )
                    }
                }
            }

            item {
                Text(stringResource(R.string.ai_integrations), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                IronCoreCard {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Memory, contentDescription = "AI model settings")
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(stringResource(R.string.on_device_ai), style = MaterialTheme.typography.titleMedium)
                                    Text(stringResource(R.string.on_device_ai_desc), style = MaterialTheme.typography.bodySmall)
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
                Icon(Icons.Default.Memory, contentDescription = "Memory and performance settings")
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(stringResource(R.string.remote_ai), style = MaterialTheme.typography.titleMedium)
                                    Text(stringResource(R.string.remote_ai_desc), style = MaterialTheme.typography.bodySmall)
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
                Text(stringResource(R.string.account), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                IronCoreCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            val nameText = when (val state = authState) {
                                is AuthState.LoggedIn -> state.name
                                AuthState.LoggedOut -> stringResource(R.string.guest_user)
                            }
                            val subtext = when (val state = authState) {
                                is AuthState.LoggedIn -> state.email
                                AuthState.LoggedOut -> stringResource(R.string.sign_in_to_sync)
                            }
                            Text(nameText, style = MaterialTheme.typography.titleMedium)
                            Text(subtext, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (authState is AuthState.LoggedOut) {
                        IronCoreButton(
                            onClick = { viewModel.signIn() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.sign_in))
                        }
                    } else {
                        IronCoreButton(
                            onClick = { viewModel.signOut() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.sign_out))
                        }
                    }
                }
            }
        }
    }
}
