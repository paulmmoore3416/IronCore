package com.ironcore.metrics.ui.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import com.ironcore.metrics.ui.components.IronCoreButton
import com.ironcore.metrics.ui.components.IronCoreCard
import com.ironcore.metrics.ui.theme.CobaltBlue
import com.ironcore.metrics.ui.theme.Granite

@Composable
fun WorkoutLoggerScreen(
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentWorkout by viewModel.currentWorkout.collectAsState()
    val sets by viewModel.workoutSets.collectAsState()

    var reps by remember { mutableStateOf("10") }
    var weight by remember { mutableStateOf("100") }
    var rpe by remember { mutableStateOf("8") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (currentWorkout == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                IronCoreButton(onClick = { viewModel.startWorkout("Hypertrophy Session") }) {
                    Text("Initialize Training Session")
                }
            }
        } else {
            Text(
                text = "Active Session: ${currentWorkout?.name}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Entry Section
            IronCoreCard {
                Text(text = "Quick Log Set", style = MaterialTheme.typography.titleMedium, color = CobaltBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CobaltBlue)
                    )
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CobaltBlue)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rpe,
                        onValueChange = { rpe = it },
                        label = { Text("RPE (1-10)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CobaltBlue)
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.weight(2f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CobaltBlue)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                IronCoreButton(
                    onClick = { 
                        // Validate inputs before saving
                        val validatedWeight = weight.toFloatOrNull()
                        val validatedReps = reps.toIntOrNull()
                        val validatedRpe = rpe.toIntOrNull()
                        
                        when {
                            validatedWeight == null || validatedWeight <= 0 -> {
                                Toast.makeText(context, "Please enter a valid weight (> 0)", Toast.LENGTH_SHORT).show()
                            }
                            validatedReps == null || validatedReps <= 0 || validatedReps > 100 -> {
                                Toast.makeText(context, "Please enter valid reps (1-100)", Toast.LENGTH_SHORT).show()
                            }
                            validatedRpe != null && (validatedRpe < 1 || validatedRpe > 10) -> {
                                Toast.makeText(context, "RPE must be between 1-10", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                viewModel.addSet(
                                    exerciseId = 1, 
                                    reps = validatedReps, 
                                    weight = validatedWeight,
                                    rpe = validatedRpe,
                                    notes = if (notes.isEmpty()) null else notes
                                )
                                Toast.makeText(context, "Set logged successfully", Toast.LENGTH_SHORT).show()
                                // Clear inputs after successful save
                                reps = "10"
                                weight = "100"
                                rpe = "8"
                                notes = ""
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Commit Set to Neural Core")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Session History", style = MaterialTheme.typography.titleMedium, color = Granite)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sets.reversed()) { set ->
                    IronCoreCard {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${set.weight} kg x ${set.reps}", style = MaterialTheme.typography.bodyLarge)
                            set.rpe?.let { Text("RPE: $it", color = CobaltBlue) }
                        }
                        set.notes?.let { 
                            Text(it, style = MaterialTheme.typography.bodySmall, color = Granite)
                        }
                    }
                }
            }
        }
    }
}

