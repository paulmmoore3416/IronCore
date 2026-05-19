package com.ironcore.metrics.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ironcore.metrics.ui.theme.GoCyan

@Composable
fun FirstRunScreen(onFinish: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to IronCore", style = MaterialTheme.typography.headlineLarge, color = GoCyan)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "To give you the best insights, IronCore needs access to Google Health Connect. " +
                "Your data never leaves your device unless you enable Homelab Sync.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onFinish, colors = ButtonDefaults.buttonColors(containerColor = GoCyan)) {
                Text("Get Started", color = MaterialTheme.colorScheme.background)
            }
        }
    }
}
