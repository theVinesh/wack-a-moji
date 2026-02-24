package com.thevinesh.wackamoji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel { GameViewModel() }) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Sky gradient background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(WackAMojiColors.SkyTop, WackAMojiColors.SkyBottom)
                )
            )
        }

        // Clouds
        CloudsBackground()

        // Main content
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Score display
            ScoreDisplay(score = state.score)

            Spacer(modifier = Modifier.height(20.dp))

            // Timer section
            TimerSection(timeLeft = state.timeLeft, timerFraction = state.timerFraction)

            // Level indicator
            LevelIndicator(level = state.level)

            Spacer(modifier = Modifier.height(12.dp))

            // Game grid
            GameGrid(
                cells = state.cells,
                emojis = state.emojis,
                onHit = { index -> viewModel.onMoleHit(index) },
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Action buttons
            ButtonsRow(
                running = state.running,
                onRestart = { viewModel.onRestart() },
                onPauseResume = { viewModel.onPauseResume() },
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Game Over overlay
        if (state.gameOver) {
            GameOverOverlay(
                score = state.score,
                level = state.level,
                onRestart = { viewModel.onRestart() },
            )
        }
    }
}

@Preview
@Composable
private fun GameScreenPreview() {
    MaterialTheme { Surface { GameScreen() } }
}
