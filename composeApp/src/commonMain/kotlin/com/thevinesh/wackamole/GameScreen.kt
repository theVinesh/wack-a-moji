package com.thevinesh.wackamole

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

const val MOLE_UP_TIME_MIN_MS = 600L
const val MOLE_UP_TIME_MAX_MS = 1200L
const val DELAY_BETWEEN_MOLES_MS = 80L
const val GAME_DURATION_SECONDS = 30

// Level thresholds
private val LEVEL_THRESHOLDS = listOf(0, 5, 15, 30, 50, 75)

internal fun calculateLevel(score: Int): Int {
    return LEVEL_THRESHOLDS.indexOfLast { score >= it }.coerceAtLeast(0) + 1
}

private fun maxMolesForLevel(level: Int): Int {
    return when {
        level >= 5 -> 3
        level >= 3 -> 2
        else -> 1
    }
}

private fun moleUpTimeRange(level: Int): Pair<Long, Long> {
    return when {
        level >= 5 -> 300L to 600L
        level >= 4 -> 400L to 800L
        level >= 3 -> 500L to 900L
        level >= 2 -> 500L to 1000L
        else -> MOLE_UP_TIME_MIN_MS to MOLE_UP_TIME_MAX_MS
    }
}

@Composable
fun GameScreen() {
    var score by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(true) }
    var timeLeft by remember { mutableIntStateOf(GAME_DURATION_SECONDS) }
    var gameOver by remember { mutableStateOf(false) }

    val cells = remember { mutableStateListOf<Boolean>().also { it.addAll(List(9) { false }) } }
    val emojis =
        remember { mutableStateListOf<String>().also { it.addAll(List(9) { randomMoleEmoji() }) } }

    val level = calculateLevel(score)
    val timerFraction = timeLeft.toFloat() / GAME_DURATION_SECONDS.toFloat()

    // Countdown timer
    LaunchedEffect(running, gameOver) {
        if (!running || gameOver) return@LaunchedEffect
        while (timeLeft > 0 && running && !gameOver) {
            delay(1000L)
            if (running && !gameOver) {
                timeLeft--
            }
        }
        if (timeLeft <= 0) {
            gameOver = true
            running = false
            for (i in 0 until 9) cells[i] = false
        }
    }

    // Mole spawning logic
    LaunchedEffect(running, gameOver) {
        if (!running || gameOver) return@LaunchedEffect
        val remaining = LongArray(9)
        while (running && !gameOver) {
            val currentLevel = calculateLevel(score)
            val maxMoles = maxMolesForLevel(currentLevel)
            val (minTime, maxTime) = moleUpTimeRange(currentLevel)

            for (i in 0 until 9) {
                if (cells[i]) {
                    remaining[i] = (remaining[i] - 80L).coerceAtLeast(0L)
                    if (remaining[i] == 0L) cells[i] = false
                }
            }
            val upCount = cells.count { it }
            if (upCount < maxMoles) {
                val idx = Random.nextInt(0, 9)
                if (!cells[idx]) {
                    cells[idx] = true
                    emojis[idx] = randomMoleEmoji()
                    remaining[idx] = Random.nextLong(minTime, maxTime)
                }
            }
            delay(DELAY_BETWEEN_MOLES_MS)
        }
    }

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
            ScoreDisplay(score = score)

            Spacer(modifier = Modifier.height(20.dp))

            // Timer section
            TimerSection(timeLeft = timeLeft, timerFraction = timerFraction)

            // Level indicator
            LevelIndicator(level = level)

            Spacer(modifier = Modifier.height(12.dp))

            // Game grid
            GameGrid(
                cells = cells,
                emojis = emojis,
                onHit = { index ->
                    if (cells[index] && !gameOver) {
                        score += 1
                        cells[index] = false
                    }
                },
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Action buttons
            ButtonsRow(
                running = running,
                onRestart = {
                    score = 0
                    timeLeft = GAME_DURATION_SECONDS
                    gameOver = false
                    for (i in 0 until 9) cells[i] = false
                    running = true
                },
                onPauseResume = {
                    if (!gameOver) {
                        running = !running
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Game Over overlay
        if (gameOver) {
            GameOverOverlay(
                score = score,
                level = level,
                onRestart = {
                    score = 0
                    timeLeft = GAME_DURATION_SECONDS
                    gameOver = false
                    for (i in 0 until 9) cells[i] = false
                    running = true
                }
            )
        }
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

private fun randomMoleEmoji(): String {
    val pool = listOf("😡", "😂", "🙄", "😅", "🤪", "😤", "🥴", "😎")
    return pool[Random.nextInt(pool.size)]
}

@Preview
@Composable
private fun GameScreenPreview() {
    MaterialTheme { Surface { GameScreen() } }
}
