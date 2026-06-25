package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AmbientBackground() {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    Box(modifier = Modifier.fillMaxSize()) {
        // Top Left Blob
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = (-80).dp)
                .size(500.dp)
                .blur(120.dp)
                .clip(CircleShape)
                .background(
                    if (isDark) Color(0xFF1E1B4B).copy(alpha = 0.5f) 
                    else Color(0xFF6366F1).copy(alpha = 0.2f)
                )
        )
        // Bottom Right Blob
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .size(600.dp)
                .blur(140.dp)
                .clip(CircleShape)
                .background(
                    if (isDark) Color(0xFF312E81).copy(alpha = 0.4f)
                    else Color(0xFFA855F7).copy(alpha = 0.2f)
                )
        )
        // Center Accent
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(400.dp)
                .blur(180.dp)
                .clip(CircleShape)
                .background(
                    if (isDark) Color(0xFF0F172A).copy(alpha = 0.6f)
                    else Color(0xFF38BDF8).copy(alpha = 0.15f)
                )
        )
    }
}

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val backgroundColor = if (isDark) {
        Color.White.copy(alpha = 0.05f)
    } else {
        Color.White.copy(alpha = 0.4f)
    }
    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.1f)
    } else {
        Color.White.copy(alpha = 0.2f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        content()
    }
}
