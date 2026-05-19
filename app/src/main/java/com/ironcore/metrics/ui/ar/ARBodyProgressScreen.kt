package com.ironcore.metrics.ui.ar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ironcore.metrics.ui.theme.Html5Orange

@Composable
fun ARBodyProgressScreen(onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = onBack) { Text("Back") }
            Spacer(modifier = Modifier.height(16.dp))
            Text("AR Body Progress", style = MaterialTheme.typography.headlineLarge, color = Html5Orange)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Camera integration for AR muscle definition overlays goes here.")
        }
    }
}
