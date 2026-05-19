package com.ironcore.metrics.ui.media

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ironcore.metrics.ui.theme.CobaltBlue
import com.ironcore.metrics.ui.theme.SpaceGray

@Composable
fun MediaControlOverlay(
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        color = SpaceGray,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, CobaltBlue)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Now Playing", style = MaterialTheme.typography.labelSmall, color = CobaltBlue)
                Text(text = "IronCore Workout Mix", style = MaterialTheme.typography.bodyMedium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* Spotify Prev */ }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = CobaltBlue)
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.PlayArrow else Icons.Default.PlayArrow, // Simplified for now
                        contentDescription = "Play/Pause", 
                        tint = SpaceGray
                    )
                }
                IconButton(onClick = { /* Spotify Next */ }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
