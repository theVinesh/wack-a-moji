package com.thevinesh.wackamoji

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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

/** Peak alpha of the red miss flash on the mat (kept low so it reads as a subtle tint). */
private const val MISS_FLASH_PEAK_ALPHA = 0.35f
private const val MISS_FLASH_RISE_MS = 110
private const val MISS_FLASH_FALL_MS = 220

@Composable
fun GameGrid(
    cells: List<CellState>,
    missedTapCount: Int = 0,
    onHit: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val largeTargets = LocalPlayerPreferencesStore.current.preferences.largeTargets
    val reduceMotion = LocalPlayerPreferencesStore.current.preferences.reduceMotion
    val holePadding = moleHoleCellPadding(largeTargets)

    // Momentary red pulse on every empty-tap miss, keyed on the monotonic counter
    val flashAlpha = remember { Animatable(0f) }
    LaunchedEffect(missedTapCount, reduceMotion) {
        if (missedTapCount > 0 && !reduceMotion) {
            flashAlpha.animateTo(MISS_FLASH_PEAK_ALPHA, tween(MISS_FLASH_RISE_MS))
            flashAlpha.animateTo(0f, tween(MISS_FLASH_FALL_MS))
        }
    }

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
                // Red miss flash overlay on top of the green mat
                if (flashAlpha.value > 0f) {
                    drawRoundRect(
                        color = WackAMojiColors.MissFlashRed.copy(alpha = flashAlpha.value),
                        cornerRadius = CornerRadius(24.dp.toPx()),
                        size = size
                    )
                }
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
                        val cell = cells[index]
                        MoleHole(
                            isUp = cell.isUp,
                            emoji = cell.content,
                            onTap = { onHit(index) },
                            modifier = Modifier.weight(1f).padding(holePadding)
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
            mutableStateListOf<CellState>().also {
                it.addAll(List(9) { i -> 
                    CellState(
                        isUp = i % 2 == 0,
                        contentType = CellContentType.Emoji,
                        content = listOf("🐹", "🐰", "🐵")[i % 3]
                    )
                })
            }
        }
        GameGrid(cells = cells, onHit = {})
    }
}
