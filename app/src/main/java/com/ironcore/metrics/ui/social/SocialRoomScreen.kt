package com.ironcore.metrics.ui.social

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ironcore.metrics.ui.theme.JavaBlue

@Composable
fun SocialRoomScreen(onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = onBack) { Text("Back") }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Live Workout Room", style = MaterialTheme.typography.headlineLarge, color = JavaBlue)
            Spacer(modifier = Modifier.height(16.dp))
            Text("You are in a virtual gym with 3 friends. (Simulated)")
        }
    }
}
