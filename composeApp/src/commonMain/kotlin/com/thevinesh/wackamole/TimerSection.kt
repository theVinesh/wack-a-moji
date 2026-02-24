package com.thevinesh.wackamole

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun TimerSection(timeLeft: Int, timerFraction: Float) {
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

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun TimerSectionPreview() {
    MaterialTheme { Surface { TimerSection(timeLeft = 15, timerFraction = 0.5f) } }
}
