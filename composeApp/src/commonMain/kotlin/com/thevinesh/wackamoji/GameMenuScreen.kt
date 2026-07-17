package com.thevinesh.wackamoji

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val SHOW_LEADERBOARD_BUTTON = true

@Composable
internal fun GameMenuScreen(
    onStartClassic: () -> Unit,
    onStartEndless: () -> Unit,
    onLeaderboard: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    animateClouds: Boolean = true,
) {
    SharedSkyScreen(modifier = modifier, animateClouds = animateClouds) {
        GameMenuContent(
            onStartClassic = onStartClassic,
            onStartEndless = onStartEndless,
            onLeaderboard = onLeaderboard,
            onSettings = onSettings,
        )
    }
}

@Composable
internal fun GameMenuContent(
    onStartClassic: () -> Unit,
    onStartEndless: () -> Unit,
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
                text = "Classic",
                backgroundColor = WackAMojiColors.RestartOrange,
                shadowColor = WackAMojiColors.RestartShadow,
                onClick = onStartClassic,
                modifier = Modifier.fillMaxWidth()
            )
            GameButton(
                text = "Endless",
                backgroundColor = WackAMojiColors.Accent,
                shadowColor = Color(0xFFCA8A04),
                onClick = onStartEndless,
                modifier = Modifier.fillMaxWidth()
            )
            if (SHOW_LEADERBOARD_BUTTON) {
                GameButton(
                    text = "Leaderboard",
                    backgroundColor = WackAMojiColors.LeaderboardPurple,
                    shadowColor = WackAMojiColors.LeaderboardShadow,
                    onClick = onLeaderboard,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
                onStartClassic = {},
                onStartEndless = {},
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
                onStartClassic = {},
                onStartEndless = {},
                onLeaderboard = {},
                onSettings = {},
                animateClouds = false,
            )
        }
    }
}