package com.thevinesh.wackamoji

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun ButtonsRow(
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
internal fun GameButton(
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

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun ButtonsRowPreview() {
    MaterialTheme { Surface { ButtonsRow(running = true, onRestart = {}, onPauseResume = {}) } }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun GameButtonPreview() {
    MaterialTheme {
        Surface {
            GameButton(
                text = "PLAY",
                backgroundColor = WackAMojiColors.RestartOrange,
                shadowColor = WackAMojiColors.RestartShadow,
                onClick = {}
            )
        }
    }
}
