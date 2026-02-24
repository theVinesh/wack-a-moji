package com.thevinesh.wackamole

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

const val MOLE_UP_TIME_MIN_MS = 600L
const val MOLE_UP_TIME_MAX_MS = 1200L
const val DELAY_BETWEEN_MOLES_MS = 80L
const val GAME_DURATION_SECONDS = 30

// Level thresholds
private val LEVEL_THRESHOLDS = listOf(0, 5, 15, 30, 50, 75)

private fun calculateLevel(score: Int): Int {
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

// ─── Clouds ──────────────────────────────────────────────────────────────────

@Composable
private fun CloudsBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cloudColor = WackAMojiColors.Cloud.copy(alpha = 0.7f)
        // Cloud 1 — top left
        drawRoundRect(
            color = cloudColor,
            topLeft = Offset(40.dp.toPx(), 48.dp.toPx()),
            size = Size(96.dp.toPx(), 32.dp.toPx()),
            cornerRadius = CornerRadius(100.dp.toPx())
        )
        // Cloud 2 — top right
        drawRoundRect(
            color = cloudColor,
            topLeft = Offset(size.width - 140.dp.toPx(), 96.dp.toPx()),
            size = Size(128.dp.toPx(), 40.dp.toPx()),
            cornerRadius = CornerRadius(100.dp.toPx())
        )
        // Cloud 3 — mid left
        drawRoundRect(
            color = cloudColor,
            topLeft = Offset(80.dp.toPx(), 180.dp.toPx()),
            size = Size(80.dp.toPx(), 24.dp.toPx()),
            cornerRadius = CornerRadius(100.dp.toPx())
        )
    }
}

// ─── Score Display ───────────────────────────────────────────────────────────

@Composable
private fun ScoreDisplay(score: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        // Large score number
        Text(
            text = formatScore(score),
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold,
            color = WackAMojiColors.Primary,
            textAlign = TextAlign.Center,
            letterSpacing = (-2).sp,
        )

        // "SCORE" badge — rotated, offset to top-left
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-20).dp, y = (-8).dp)
                .rotate(-12f)
                .background(Color.White, RoundedCornerShape(50))
                .border(2.dp, WackAMojiColors.ScoreBadgeBorder, RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = "SCORE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = WackAMojiColors.Primary,
            )
        }
    }
}

private fun formatScore(score: Int): String {
    return when {
        score >= 1000 -> {
            val thousands = score / 1000
            val hundreds = (score % 1000) / 100
            if (hundreds > 0) "$thousands,${(score % 1000).toString().padStart(3, '0')}"
            else "$thousands,000"
        }

        else -> score.toString()
    }
}

// ─── Timer Section ───────────────────────────────────────────────────────────

@Composable
private fun TimerSection(timeLeft: Int, timerFraction: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Labels row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "TIME LEFT",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = WackAMojiColors.SkyDark,
                letterSpacing = (-0.5).sp,
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$timeLeft",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = WackAMojiColors.Primary,
                )
                Text(
                    text = "s",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = WackAMojiColors.Primary,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(50))
                .background(WackAMojiColors.TimerTrack)
                .border(1.dp, WackAMojiColors.TimerTrackBorder, RoundedCornerShape(50))
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = timerFraction.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(WackAMojiColors.Primary, WackAMojiColors.Accent)
                        )
                    )
            )
        }
    }
}

// ─── Level Indicator ─────────────────────────────────────────────────────────

@Composable
private fun LevelIndicator(level: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "LEVEL $level",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = WackAMojiColors.SkyMedium,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { dotIndex ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (dotIndex % 2 == 0) WackAMojiColors.Primary
                            else Color.White.copy(alpha = 0.6f)
                        )
                )
            }
        }
    }
}

// ─── Buttons Row ─────────────────────────────────────────────────────────────

@Composable
private fun ButtonsRow(
    running: Boolean,
    onRestart: () -> Unit,
    onPauseResume: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        GameButton(
            text = "RESTART",
            backgroundColor = WackAMojiColors.RestartOrange,
            shadowColor = WackAMojiColors.RestartShadow,
            onClick = onRestart,
            modifier = Modifier.weight(1f)
        )
        GameButton(
            text = if (running) "PAUSE" else "RESUME",
            backgroundColor = WackAMojiColors.PauseGreen,
            shadowColor = WackAMojiColors.PauseShadow,
            onClick = onPauseResume,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GameButton(
    text: String,
    backgroundColor: Color,
    shadowColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 3D push-style button
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        // Shadow layer (offset below)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 6.dp)
                .background(shadowColor, RoundedCornerShape(16.dp))
        )

        // Main button surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .background(backgroundColor, RoundedCornerShape(16.dp))
                .border(4.dp, WackAMojiColors.ButtonHighlight, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp,
            )
        }
    }
}

// ─── Game Over Overlay ───────────────────────────────────────────────────────

@Composable
private fun GameOverOverlay(
    score: Int,
    onRestart: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* consume clicks */ }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(32.dp)
                .background(Color.White, RoundedCornerShape(32.dp))
                .padding(32.dp)
        ) {
            Text(
                text = "⏰",
                fontSize = 48.sp,
            )
            Text(
                text = "TIME'S UP!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = WackAMojiColors.Primary,
            )
            Text(
                text = "Final Score",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
            )
            Text(
                text = formatScore(score),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = WackAMojiColors.Primary,
                letterSpacing = (-2).sp,
            )
            Text(
                text = "Level ${calculateLevel(score)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WackAMojiColors.SkyMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            GameButton(
                text = "PLAY AGAIN",
                backgroundColor = WackAMojiColors.RestartOrange,
                shadowColor = WackAMojiColors.RestartShadow,
                onClick = onRestart,
                modifier = Modifier.width(200.dp)
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
