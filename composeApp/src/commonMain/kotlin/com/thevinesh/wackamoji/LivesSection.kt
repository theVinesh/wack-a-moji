package com.thevinesh.wackamoji

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal fun formatLivesDisplay(lives: Int): String =
    List(lives.coerceAtLeast(0)) { "❤️" }.joinToString(separator = "").ifEmpty { "🖤" }

@Composable
internal fun LivesSection(lives: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = "LIVES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = WackAMojiColors.SkyDark,
                letterSpacing = (-0.5).sp,
            )
            Text(
                text = formatLivesDisplay(lives),
                fontSize = 22.sp,
                fontFamily = LocalEmojiFont.current,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Miss a mole and lose a life",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = WackAMojiColors.SkyMedium,
        )
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun LivesSectionPreview() {
    MaterialTheme { Surface { LivesSection(lives = 2) } }
}
