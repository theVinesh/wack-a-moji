package com.thevinesh.wackamoji

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun ScoreDisplay(score: Int) {
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

internal fun formatScore(score: Int): String {
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

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun ScoreDisplayPreview() {
    MaterialTheme { Surface { ScoreDisplay(score = 42) } }
}
