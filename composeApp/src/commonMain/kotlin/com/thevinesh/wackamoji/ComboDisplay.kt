package com.thevinesh.wackamoji

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Always show a combo label so the HUD never jumps when a streak starts or breaks. */
internal fun formatComboLabel(combo: Int): String =
    if (combo <= 0) "NO COMBO" else "x$combo COMBO"

/** Streaks of 2+ are "live" — brighter chrome; 0–1 stay muted but still occupy space. */
internal fun isComboActive(combo: Int): Boolean = combo >= 2

@Composable
internal fun ComboDisplay(
    combo: Int,
    modifier: Modifier = Modifier,
) {
    val active = isComboActive(combo)
    val textColor = if (active) {
        WackAMojiColors.Primary
    } else {
        WackAMojiColors.SkyMedium.copy(alpha = 0.55f)
    }
    val background = if (active) {
        Color.White.copy(alpha = 0.92f)
    } else {
        Color.White.copy(alpha = 0.55f)
    }
    val borderColor = if (active) {
        WackAMojiColors.ScoreBadgeBorder
    } else {
        WackAMojiColors.SkyMedium.copy(alpha = 0.18f)
    }

    Text(
        text = formatComboLabel(combo),
        fontSize = 16.sp,
        fontWeight = FontWeight.Black,
        color = textColor,
        modifier = modifier
            .background(background, RoundedCornerShape(50))
            .border(2.dp, borderColor, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun ComboDisplayPreview() {
    MaterialTheme { Surface { ComboDisplay(combo = 5) } }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun ComboDisplayIdlePreview() {
    MaterialTheme { Surface { ComboDisplay(combo = 0) } }
}
