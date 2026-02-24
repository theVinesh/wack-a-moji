package com.thevinesh.wackamole

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameGrid(
    cells: List<Boolean>,
    emojis: List<String>,
    onHit: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .drawBehind {
                // Green board background
                drawRoundRect(
                    color = WackAMojiColors.GrassGreen,
                    cornerRadius = CornerRadius(24.dp.toPx()),
                    size = size
                )
                // Bottom border (thick green-600 shadow)
                drawRoundRect(
                    color = WackAMojiColors.GrassBorder,
                    topLeft = Offset(0f, size.height - 8.dp.toPx()),
                    size = Size(size.width, 8.dp.toPx()),
                    cornerRadius = CornerRadius(24.dp.toPx())
                )
            }
    ) {
        // 3×3 Grid of holes
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        MoleHole(
                            isUp = cells[index],
                            emoji = emojis[index],
                            onTap = { onHit(index) },
                            modifier = Modifier.weight(1f).padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun GameGridPreview() {
    MaterialTheme {
        val cells = remember {
            mutableStateListOf<Boolean>().also {
                it.addAll(List(9) { it % 2 == 0 })
            }
        }
        val emojis = remember { List(9) { listOf("🐹", "🐰", "🐵")[it % 3] } }
        GameGrid(cells = cells, emojis = emojis, onHit = {})
    }
}
