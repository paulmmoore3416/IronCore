package com.ironcore.metrics.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val IronCoreDarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = CobaltBlue,
    tertiary = Granite,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = SpaceGray,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary
)

@Composable
fun IronCoreTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IronCoreDarkColorScheme,
        typography = Typography, // We'll define this next
        content = content
    )
}
