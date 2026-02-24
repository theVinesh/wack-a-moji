package com.thevinesh.wackamoji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Provides the emoji FontFamily throughout the app. On mobile this is null (system handles emojis natively). */
val LocalEmojiFont = compositionLocalOf<FontFamily?> { null }

@Composable
@Preview
fun App(gameViewModel: GameViewModel = viewModel { GameViewModel() }) {
    val state by gameViewModel.uiState.collectAsState()

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
                GameScreen(viewModel = gameViewModel)
            }

            // Game Over overlay — fills entire screen
            if (state.gameOver) {
                GameOverOverlay(
                    score = state.score,
                    level = state.level,
                    onRestart = { gameViewModel.onRestart() },
                )
            }
        }
    }
}
