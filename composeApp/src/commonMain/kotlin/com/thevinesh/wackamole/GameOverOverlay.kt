package com.thevinesh.wackamole

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun GameOverOverlay(
    score: Int,
    level: Int,
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
                text = "Level $level",
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

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun GameOverOverlayPreview() {
    MaterialTheme { GameOverOverlay(score = 25, level = 3, onRestart = {}) }
}
