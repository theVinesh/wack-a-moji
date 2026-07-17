package com.thevinesh.wackamoji

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel { GameViewModel() },
    onBack: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
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

            when (state.mode) {
                GameMode.Classic -> TimerSection(
                    timeLeft = state.timeLeft,
                    timerFraction = state.timerFraction,
                )
                GameMode.Endless -> LivesSection(lives = state.lives)
            }

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
                onPauseResume = { viewModel.onPauseResume() },
                onBack = onBack,
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (!state.running && !state.gameOver) {
            PausedBanner(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 48.dp),
            )
        }
    }
}

@Composable
internal fun PausedBanner(modifier: Modifier = Modifier) {
    Text(
        text = "PAUSED",
        fontSize = 28.sp,
        fontWeight = FontWeight.Black,
        color = Color.White,
        modifier = modifier
            .background(
                color = WackAMojiColors.SkyDark.copy(alpha = 0.75f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
    )
}

@Preview
@Composable
private fun GameScreenPreview() {
    MaterialTheme { Surface { GameScreen() } }
}
