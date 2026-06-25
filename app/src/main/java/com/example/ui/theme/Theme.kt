package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val GlassColorScheme = lightColorScheme(
    primary = GlassPrimary,
    background = GlassBackground,
    surface = GlassSurface,
    onPrimary = Color.White,
    onBackground = GlassText,
    onSurface = GlassText,
    secondary = Color(0xFFBBD1EE),
    surfaceVariant = Color(0x66FFFFFF)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to maintain the specific brand theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = GlassPrimary,
            background = Color(0xFF0F172A),
            surface = Color(0x331E293B),
            onPrimary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
            secondary = Color(0xFF38BDF8)
        )
    } else {
        GlassColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
