package com.ironcore.metrics.ui.workout

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ironcore.metrics.data.remote.dto.RemoteExercise
import com.ironcore.metrics.ui.components.IronCoreButton
import com.ironcore.metrics.ui.components.IronCoreCard
import com.ironcore.metrics.ui.theme.GoCyan
import com.ironcore.metrics.ui.theme.Granite
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecializedWorkoutScreen(
    modality: String,
    onBack: () -> Unit,
    viewModel: SpecializedWorkoutViewModel = hiltViewModel()
) {
    val routine by viewModel.routine.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var selectedDayIndex by remember { mutableStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var selectedExerciseIndex by remember { mutableStateOf<Int?>(null) }
    
    // Voice command integration
    val voiceCommandHelper = remember {
        VoiceCommandHelper(context) { command ->
            selectedExerciseIndex?.let { exIndex ->
                routine?.days?.get(selectedDayIndex)?.exercises?.get(exIndex)?.let { exercise ->
                    val updatedExercise = exercise.copy(
                        reps = command.reps.toString(),
                        weight = "${command.weight.toInt()} kg"
                    )
                    viewModel.updateExercise(
                        routine!!.days[selectedDayIndex].dayNumber,
                        exIndex,
                        updatedExercise
                    )
                    scope.launch {
                        snackbarHostState.showSnackbar("Logged: ${command.reps} reps at ${command.weight.toInt()} kg")
                    }
                }
            }
        }
    }
    
    val isListening by voiceCommandHelper.isListening.collectAsState()
    val lastRecognizedText by voiceCommandHelper.lastRecognizedText.collectAsState()
    
    // Request audio permission
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceCommandHelper.startListening()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Microphone permission required for voice commands")
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            voiceCommandHelper.cleanup()
        }
    }

    LaunchedEffect(modality) {
        viewModel.loadRoutine(modality)
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("$modality Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isListening) {
                        voiceCommandHelper.stopListening()
                    } else {
                        if (selectedExerciseIndex == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Select an exercise first to log with voice")
                            }
                        } else {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                containerColor = if (isListening) MaterialTheme.colorScheme.error else GoCyan
            ) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = if (isListening) "Stop listening" else "Start voice command",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // High-Level Modality Metrics
            IronCoreCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Performance Metrics", style = MaterialTheme.typography.titleMedium, color = GoCyan)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        MetricItem("Intensity", "High")
                        MetricItem("Frequency", "4x/wk")
                        MetricItem("Fatigue", "Low")
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                routine?.let { plan ->
                    // Day Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = "Day ${plan.days[selectedDayIndex].dayNumber}: ${plan.days[selectedDayIndex].focus}",
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Select training day") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            plan.days.forEachIndexed { index, day ->
                                DropdownMenuItem(
                                    text = { Text("Day ${day.dayNumber}: ${day.focus}") },
                                    onClick = {
                                        selectedDayIndex = index
                                        expanded = false
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Editable Routine", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                    // Editable Exercises List
                    plan.days[selectedDayIndex].exercises.forEachIndexed { exIndex, exercise ->
                        EditableExerciseCard(
                            exercise = exercise,
                            isSelected = selectedExerciseIndex == exIndex,
                            onExerciseChanged = { updatedEx ->
                                viewModel.updateExercise(plan.days[selectedDayIndex].dayNumber, exIndex, updatedEx)
                            },
                            onExerciseSelected = {
                                selectedExerciseIndex = if (selectedExerciseIndex == exIndex) null else exIndex
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    IronCoreButton(
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            scope.launch { snackbarHostState.showSnackbar("Routine modifications saved!") }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save routine")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Routine to Calendar")
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun EditableExerciseCard(
    exercise: RemoteExercise,
    isSelected: Boolean = false,
    onExerciseChanged: (RemoteExercise) -> Unit,
    onExerciseSelected: () -> Unit = {}
) {
    var name by remember { mutableStateOf(exercise.name) }
    var sets by remember { mutableStateOf(exercise.sets.toString()) }
    var reps by remember { mutableStateOf(exercise.reps) }
    var weight by remember { mutableStateOf(exercise.weight) }
    
    var setsError by remember { mutableStateOf(false) }
    var repsError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }

    IronCoreCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExerciseSelected() },
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, GoCyan) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    if (it.length <= 100) {
                        name = it
                        onExerciseChanged(exercise.copy(name = it))
                    }
                },
                label = { Text("Exercise Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = name.isBlank(),
                supportingText = if (name.isBlank()) {
                    { Text("Exercise name cannot be empty", color = MaterialTheme.colorScheme.error) }
                } else null
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = sets,
                    onValueChange = { input ->
                        // Only allow digits
                        if (input.isEmpty() || input.all { it.isDigit() }) {
                            sets = input
                            val setsValue = input.toIntOrNull()
                            setsError = setsValue == null || setsValue !in 1..20
                            if (!setsError && setsValue != null) {
                                onExerciseChanged(exercise.copy(sets = setsValue))
                            }
                        }
                    },
                    label = { Text("Sets") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = setsError,
                    supportingText = if (setsError) {
                        { Text("1-20", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }
                    } else null
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { input ->
                        // Allow digits, hyphens for ranges (e.g., "8-12"), and "AMRAP"
                        if (input.isEmpty() || input.matches(Regex("^[0-9\\-AMRAP]*$"))) {
                            reps = input
                            // Validate: either a number (1-100), a range (e.g., "8-12"), or "AMRAP"
                            repsError = when {
                                input.isEmpty() -> true
                                input == "AMRAP" -> false
                                input.contains("-") -> {
                                    val parts = input.split("-")
                                    parts.size != 2 || parts.any { it.toIntOrNull() == null || it.toInt() !in 1..100 }
                                }
                                else -> input.toIntOrNull()?.let { it !in 1..100 } ?: true
                            }
                            if (!repsError) {
                                onExerciseChanged(exercise.copy(reps = input))
                            }
                        }
                    },
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = repsError,
                    supportingText = if (repsError) {
                        { Text("1-100 or range", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }
                    } else null
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { input ->
                        // Allow digits, decimal point, and common weight units (lbs, kg)
                        if (input.isEmpty() || input.matches(Regex("^[0-9.lbskgLBSKG ]*$"))) {
                            weight = input
                            // Extract numeric value for validation
                            val numericPart = input.filter { it.isDigit() || it == '.' }
                            weightError = if (numericPart.isEmpty()) {
                                input.isNotEmpty() // Error if non-empty but no numbers
                            } else {
                                numericPart.toDoubleOrNull()?.let { it !in 0.0..2000.0 } ?: true
                            }
                            if (!weightError) {
                                onExerciseChanged(exercise.copy(weight = input))
                            }
                        }
                    },
                    label = { Text("Weight") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = weightError,
                    supportingText = if (weightError) {
                        { Text("0-2000", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }
                    } else null
                )
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Granite)
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
