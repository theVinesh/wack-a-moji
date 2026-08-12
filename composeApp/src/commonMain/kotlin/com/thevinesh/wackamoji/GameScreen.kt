package com.thevinesh.wackamoji

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
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
    val density = LocalDensity.current
    val pickupManager = remember { PowerUpPickupManager() }
    val comboFloatManager = remember { ComboFloatManager() }
    
    var dockPosition by remember { mutableStateOf(Offset.Zero) }
    
    // Trigger pickup animation when a power-up is collected
    LaunchedEffect(state.lastPickedUpPowerUp) {
        state.lastPickedUpPowerUp?.let { (type, cellIndex) ->
            pickupManager.triggerPickup(
                emoji = powerUpEmoji(type),
                startOffset = estimateCellCenter(cellIndex, density),
                endOffset = dockPosition
            )
            
            viewModel.clearLastPickupTrigger()
        }
    }

    // Trigger a floating combo pill on every scored hit once a streak is live
    LaunchedEffect(state.combo) {
        val combo = state.combo
        if (combo >= 2) {
            val hitIndex = state.lastHitCellIndex ?: return@LaunchedEffect
            val start = estimateCellCenter(hitIndex, density)
            val end = Offset(start.x, start.y - with(density) { 90.dp.toPx() })
            comboFloatManager.triggerCombo(combo = combo, startOffset = start, endOffset = end)
        }
    }

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

            // Top row: centered Score + power-up dock on the right
            Box(modifier = Modifier.fillMaxWidth()) {
                ScoreDisplay(
                    score = state.score,
                    modifier = Modifier.align(Alignment.Center)
                )

                if (state.mode == GameMode.Endless) {
                    PowerUpDock(
                        activePowerUps = state.activePowerUps,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .onGloballyPositioned { coords ->
                                dockPosition = coords.positionInRoot()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
        
        // Power-up pickup animations overlay
        PowerUpPickupAnimations(
            pickups = pickupManager.pickups,
            modifier = Modifier.fillMaxSize()
        )

        // Floating combo pill animations overlay
        ComboFloatAnimations(
            floats = comboFloatManager.floats,
            onFinished = comboFloatManager::removeFloat,
            modifier = Modifier.fillMaxSize()
        )

        if (!state.running && !state.gameOver) {
            PausedBanner(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 48.dp),
            )
        }
    }
}

/** Approximate the center of a grid cell in root coordinates (shared by power-up and combo spawn animations). */
private fun estimateCellCenter(index: Int, density: Density): Offset {
    val gridStartX = with(density) { 100.dp.toPx() }
    val gridStartY = with(density) { 300.dp.toPx() }
    val cellSize = with(density) { 80.dp.toPx() }
    val row = index / 3
    val col = index % 3
    return Offset(
        gridStartX + col * cellSize + cellSize / 2,
        gridStartY + row * cellSize + cellSize / 2
    )
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
