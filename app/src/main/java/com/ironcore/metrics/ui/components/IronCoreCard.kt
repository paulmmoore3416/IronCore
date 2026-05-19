package com.ironcore.metrics.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ironcore.metrics.ui.theme.CobaltBlue
import com.ironcore.metrics.ui.theme.GlassBackground
import com.ironcore.metrics.ui.theme.GlassBorder
import com.ironcore.metrics.ui.theme.GoCyan
import com.ironcore.metrics.ui.theme.JavaOrange
import com.ironcore.metrics.ui.theme.Html5Orange
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset

@Composable
fun IronCoreCard(
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(GlassBackground, GlassBackground.copy(alpha = 0.1f)),
    border: BorderStroke? = BorderStroke(1.dp, GlassBorder),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(gradientColors)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent, // Let the background brush show through
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = border,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

