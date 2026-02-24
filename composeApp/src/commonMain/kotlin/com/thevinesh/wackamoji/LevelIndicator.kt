package com.thevinesh.wackamoji

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun LevelIndicator(level: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "LEVEL $level",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = WackAMojiColors.SkyMedium,
        )
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun LevelIndicatorPreview() {
    MaterialTheme { Surface { LevelIndicator(level = 3) } }
}
