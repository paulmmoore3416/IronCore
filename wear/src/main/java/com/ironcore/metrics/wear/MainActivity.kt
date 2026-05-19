package com.ironcore.metrics.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
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
fun WearApp() {
    var heartRate by remember { mutableStateOf(0) }
    var isLogging by remember { mutableStateOf(false) }

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator() }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Text(
                    text = if (isLogging) "Workout Active" else "Ready to Lift",
                    style = MaterialTheme.typography.caption1
                )
            }
            item {
                Text(
                    text = "${heartRate} BPM",
                    style = MaterialTheme.typography.display1,
                    color = MaterialTheme.colors.primary
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Button(
                    onClick = { isLogging = !isLogging },
                    modifier = Modifier.size(ButtonDefaults.DefaultButtonSize)
                ) {
                    Text(if (isLogging) "STOP" else "START")
                }
            }
        }
    }
}
