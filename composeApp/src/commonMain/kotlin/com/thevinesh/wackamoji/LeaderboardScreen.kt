package com.thevinesh.wackamoji

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun LeaderboardScreen(
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier,
    animateClouds: Boolean = true,
) {
    val store = LocalLeaderboardStore.current
    val scores by store.scores.collectAsState()

    SharedSkyScreen(
        modifier = modifier,
        animateClouds = animateClouds,
    ) {
        LeaderboardScreenContent(
            scores = scores,
            onBackToMenu = onBackToMenu,
        )
    }
}

@Composable
internal fun LeaderboardScreenContent(
    scores: List<Int>,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .safeContentPadding()
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "LEADERBOARD",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = WackAMojiColors.LeaderboardPurple,
            letterSpacing = (-1).sp,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (scores.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No scores yet!\nPlay a game to set a high score.",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WackAMojiColors.SkyDark,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(scores) { index, score ->
                    val isHighScore = index == 0
                    ScoreItem(
                        rank = index + 1,
                        score = score,
                        isHighScore = isHighScore
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GameButton(
            text = "Back to Menu",
            backgroundColor = WackAMojiColors.RestartOrange,
            shadowColor = WackAMojiColors.RestartShadow,
            onClick = onBackToMenu,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ScoreItem(
    rank: Int,
    score: Int,
    isHighScore: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isHighScore) WackAMojiColors.Accent else Color.White.copy(alpha = 0.9f)
    val borderColor = if (isHighScore) WackAMojiColors.RestartOrange else WackAMojiColors.ButtonHighlight
    val textColor = if (isHighScore) WackAMojiColors.SkyDark else WackAMojiColors.SkyMedium

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "#$rank",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = textColor,
            )
            if (isHighScore) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "👑",
                    fontSize = 20.sp,
                    fontFamily = LocalEmojiFont.current,
                )
            }
        }

        Text(
            text = formatScore(score),
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = WackAMojiColors.Primary,
        )
    }
}

@Preview
@Composable
private fun ScoreItemPreview() {
    MaterialTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ScoreItem(rank = 1, score = 1500, isHighScore = true)
                ScoreItem(rank = 2, score = 850, isHighScore = false)
            }
        }
    }
}

@Preview
@Composable
private fun LeaderboardScreenContentPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LeaderboardScreenContent(
                scores = listOf(1500, 850, 420, 100),
                onBackToMenu = {},
            )
        }
    }
}

@Preview
@Composable
private fun LeaderboardScreenContentEmptyPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LeaderboardScreenContent(
                scores = emptyList(),
                onBackToMenu = {},
            )
        }
    }
}
