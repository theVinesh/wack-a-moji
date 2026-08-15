package com.thevinesh.wackamoji

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun GameOverOverlay(
    score: Int,
    level: Int,
    mode: GameMode = GameMode.Classic,
    recordInfo: RecordInfo? = null,
    onRestart: () -> Unit,
    onMenu: () -> Unit,
) {
    // Pop the record line when it appears (new record or close call)
    val recordScale = remember { Animatable(1f) }
    LaunchedEffect(recordInfo) {
        if (recordInfo != null) {
            recordScale.animateTo(
                targetValue = 1.15f,
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
            )
            recordScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
            )
        }
    }
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
                text = gameOverEmoji(mode),
                fontSize = 48.sp,
                fontFamily = LocalEmojiFont.current,
            )
            Text(
                text = gameOverTitle(mode),
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
                text = "Level $level",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WackAMojiColors.SkyMedium,
            )
            recordInfo?.let { info ->
                Spacer(modifier = Modifier.height(4.dp))
                if (info.isNewRecord) {
                    Text(
                        text = "🏆 NEW RECORD!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = WackAMojiColors.RestartOrange,
                        modifier = Modifier.scale(recordScale.value),
                    )
                } else {
                    Text(
                        text = recordLineText(score = score, recordInfo = info),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = WackAMojiColors.Primary,
                        modifier = Modifier.scale(recordScale.value),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            GameButton(
                text = "PLAY AGAIN",
                backgroundColor = WackAMojiColors.RestartOrange,
                shadowColor = WackAMojiColors.RestartShadow,
                onClick = onRestart,
                modifier = Modifier.width(200.dp)
            )
            GameButton(
                text = "MAIN MENU",
                backgroundColor = WackAMojiColors.MainMenuBlue,
                shadowColor = WackAMojiColors.MainMenuShadow,
                onClick = onMenu,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun GameOverOverlayPreview() {
    MaterialTheme {
        GameOverOverlay(score = 25, level = 3, recordInfo = RecordInfo(isNewRecord = true, bestScore = 25), onRestart = {}, onMenu = {})
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun GameOverOverlayCloseCallPreview() {
    MaterialTheme {
        GameOverOverlay(score = 21, level = 3, recordInfo = RecordInfo(isNewRecord = false, bestScore = 25), onRestart = {}, onMenu = {})
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun GameOverOverlayBaselinePreview() {
    MaterialTheme {
        GameOverOverlay(score = 25, level = 3, onRestart = {}, onMenu = {})
    }
}

/** Copy for the record line on the game-over screen: trophy, tie, or the close-call gap. */
internal fun recordLineText(score: Int, recordInfo: RecordInfo): String = when {
    recordInfo.isNewRecord -> "🏆 NEW RECORD!"
    score == recordInfo.bestScore -> "You matched your record!"
    else -> recordGapText(score = score, bestScore = recordInfo.bestScore)
}

/** "You were 4 points short of your record!" copy for near-record runs. */
internal fun recordGapText(score: Int, bestScore: Int): String {
    val gap = (bestScore - score).coerceAtLeast(0)
    return "So close! You were $gap ${if (gap == 1) "point" else "points"} short of your record"
}
