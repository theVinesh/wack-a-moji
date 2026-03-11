package com.thevinesh.wackamoji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun GameMenuScreen(
    onStartGame: () -> Unit,
    onLeaderboard: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    animateClouds: Boolean = true,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(WackAMojiColors.SkyTop, WackAMojiColors.SkyBottom)
                )
            )
        }

        if (animateClouds) {
            CloudsBackground()
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cloudColor = WackAMojiColors.Cloud.copy(alpha = 0.7f)
                fun drawCloud(x: Float, y: Float, width: Float, height: Float) {
                    drawRoundRect(
                        color = cloudColor,
                        topLeft = Offset(size.width * x, size.height * y),
                        size = Size(size.width * width, size.height * height),
                        cornerRadius = CornerRadius(size.width * 0.15f)
                    )
                }

                drawCloud(x = 0.08f, y = 0.04f, width = 0.25f, height = 0.03f)
                drawCloud(x = 0.62f, y = 0.07f, width = 0.30f, height = 0.035f)
                drawCloud(x = 0.15f, y = 0.14f, width = 0.20f, height = 0.025f)
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = 430.dp)
                .fillMaxHeight()
        ) {
            GameMenuContent(
                onStartGame = onStartGame,
                onLeaderboard = onLeaderboard,
                onSettings = onSettings,
            )
        }
    }
}

@Composable
internal fun GameMenuContent(
    onStartGame: () -> Unit,
    onLeaderboard: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .safeContentPadding()
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "WACK-A-MOJI",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = WackAMojiColors.Primary,
            letterSpacing = (-1).sp,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GameButton(
                text = "Start Game",
                backgroundColor = WackAMojiColors.RestartOrange,
                shadowColor = WackAMojiColors.RestartShadow,
                onClick = onStartGame,
                modifier = Modifier.fillMaxWidth()
            )
            GameButton(
                text = "Leaderboard",
                backgroundColor = WackAMojiColors.LeaderboardPurple,
                shadowColor = WackAMojiColors.LeaderboardShadow,
                onClick = onLeaderboard,
                modifier = Modifier.fillMaxWidth()
            )
            GameButton(
                text = "Settings",
                backgroundColor = WackAMojiColors.PauseGreen,
                shadowColor = WackAMojiColors.PauseShadow,
                onClick = onSettings,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1.2f))
    }
}

@Preview
@Composable
private fun GameMenuContentPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            GameMenuContent(
                onStartGame = {},
                onLeaderboard = {},
                onSettings = {},
            )
        }
    }
}

@Preview
@Composable
private fun GameMenuScreenPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            GameMenuScreen(
                onStartGame = {},
                onLeaderboard = {},
                onSettings = {},
                animateClouds = false,
            )
        }
    }
}