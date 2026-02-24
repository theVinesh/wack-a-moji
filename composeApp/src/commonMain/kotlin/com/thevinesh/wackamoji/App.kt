package com.thevinesh.wackamoji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Provides the emoji FontFamily throughout the app. On mobile this is null (system handles emojis natively). */
val LocalEmojiFont = compositionLocalOf<FontFamily?> { null }

@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            // Sky gradient — fills entire screen
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(WackAMojiColors.SkyTop, WackAMojiColors.SkyBottom)
                    )
                )
            }

            // Clouds — fill entire screen
            CloudsBackground()

            // Game content — centered, phone-width
            Box(
                modifier = Modifier
                    .widthIn(max = 430.dp)
                    .fillMaxHeight(),
            ) {
                GameScreen()
            }
        }
    }
}
