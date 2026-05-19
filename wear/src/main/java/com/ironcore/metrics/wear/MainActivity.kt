package com.ironcore.metrics.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.*
import com.ironcore.metrics.wear.ui.WearViewModel
import com.ironcore.metrics.wear.ui.theme.IronCoreWearTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IronCoreWearTheme {
                WearApp()
            }
        }
    }
}

@Composable
fun WearApp(viewModel: WearViewModel = hiltViewModel()) {
    val heartRate by viewModel.heartRate.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val hydration by viewModel.hydration.collectAsState()
    val recovery by viewModel.recovery.collectAsState()
    val activeCalories by viewModel.activeCalories.collectAsState()
    val consumedCalories by viewModel.consumedCalories.collectAsState()

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = rememberScalingLazyListState()) }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Text(
                    text = "IronCore Metrics",
                    style = MaterialTheme.typography.title3,
                    color = MaterialTheme.colors.primary
                )
            }
            
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            // Heart Rate Card
            item {
                Card(
                    onClick = { viewModel.refreshData() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Heart Rate",
                            style = MaterialTheme.typography.caption2
                        )
                        Text(
                            text = "$heartRate BPM",
                            style = MaterialTheme.typography.display3,
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            }
            
            // Steps Card
            item {
                Card(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Steps",
                            style = MaterialTheme.typography.caption2
                        )
                        Text(
                            text = steps.toString(),
                            style = MaterialTheme.typography.display3
                        )
                    }
                }
            }
            
            // Hydration Card
            hydration?.let { hydrationData ->
                item {
                    Card(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Hydration",
                                style = MaterialTheme.typography.caption2
                            )
                            Text(
                                text = "${hydrationData.currentMl}/${hydrationData.targetMl} ml",
                                style = MaterialTheme.typography.title3
                            )
                            val percentage = (hydrationData.currentMl.toFloat() / hydrationData.targetMl * 100).toInt()
                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.caption1,
                                color = if (percentage >= 100) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                            )
                        }
                    }
                }
            }
            
            // Recovery Card
            recovery?.let { recoveryData ->
                item {
                    Card(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Recovery",
                                style = MaterialTheme.typography.caption2
                            )
                            Text(
                                text = "${recoveryData.score}%",
                                style = MaterialTheme.typography.display3,
                                color = when {
                                    recoveryData.score >= 80 -> MaterialTheme.colors.primary
                                    recoveryData.score >= 60 -> MaterialTheme.colors.secondary
                                    else -> MaterialTheme.colors.error
                                }
                            )
                            recoveryData.advice?.takeIf { it.isNotBlank() }?.let { advice ->
                                Text(
                                    text = advice.take(50) + if (advice.length > 50) "..." else "",
                                    style = MaterialTheme.typography.caption3,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Calories Card
            item {
                Card(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Energy Balance",
                            style = MaterialTheme.typography.caption2
                        )
                        Text(
                            text = "${consumedCalories - activeCalories.toInt()} kcal",
                            style = MaterialTheme.typography.title3
                        )
                        Text(
                            text = "In: $consumedCalories | Out: ${activeCalories.toInt()}",
                            style = MaterialTheme.typography.caption3
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            // Refresh Button
            item {
                Button(
                    onClick = { viewModel.refreshData() },
                    modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
                ) {
                    Text("REFRESH")
                }
            }
        }
    }
}
