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

internal fun formatComboLabel(combo: Int): String = "x$combo COMBO"

internal fun shouldShowCombo(combo: Int): Boolean = combo >= 2

@Composable
internal fun ComboDisplay(
    combo: Int,
    modifier: Modifier = Modifier,
) {
    if (!shouldShowCombo(combo)) return

    Text(
        text = formatComboLabel(combo),
        fontSize = 16.sp,
        fontWeight = FontWeight.Black,
        color = WackAMojiColors.Primary,
        modifier = modifier
            .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(50))
            .border(2.dp, WackAMojiColors.ScoreBadgeBorder, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun ComboDisplayPreview() {
    MaterialTheme { Surface { ComboDisplay(combo = 5) } }
}
