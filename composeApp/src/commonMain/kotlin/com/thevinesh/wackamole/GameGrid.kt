package com.thevinesh.wackamole

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameGrid(
    cells: List<Boolean>,
    emojis: List<String>,
    onHit: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        for (row in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    MoleHole(
                        isUp = cells[index],
                        emoji = emojis[index],
                        onTap = { onHit(index) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
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
