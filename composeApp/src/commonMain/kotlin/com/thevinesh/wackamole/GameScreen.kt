package com.thevinesh.wackamole

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GameScreen() {
    var score by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(true) }

    val cells = remember { mutableStateListOf<Boolean>().also { it.addAll(List(9) { false }) } }
    val emojis = remember { List(9) { randomMoleEmoji() } }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        val remaining = LongArray(9) { 0L }
        while (running) {
            for (i in 0 until 9) {
                if (cells[i]) {
                    remaining[i] = (remaining[i] - 80L).coerceAtLeast(0L)
                    if (remaining[i] == 0L) cells[i] = false
                }
            }
            val upCount = cells.count { it }
            if (upCount < 3) {
                val idx = Random.nextInt(0, 9)
                if (!cells[idx]) {
                    cells[idx] = true
                    remaining[idx] = Random.nextLong(600, 1200)
                }
            }
            delay(80L)
        }
    }

    Column(
        modifier = Modifier
            .safeContentPadding()
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Wack-A-Mole",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Score: $score",
            style = MaterialTheme.typography.headlineSmall
        )

        GameGrid(
            cells = cells,
            emojis = emojis,
            onHit = { index ->
                if (cells[index]) {
                    score += 1
                    cells[index] = false
                }
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                score = 0
                for (i in 0 until 9) cells[i] = false
                running = true
            }) { Text("Restart") }
            Button(onClick = { running = !running }) {
                Text(if (running) "Pause" else "Resume")
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "Tap the emoji when it pops up!",
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun randomMoleEmoji(): String {
    val pool = listOf("🐹", "🐰", "🐵", "🐶", "🐱", "🐸", "🐻", "🦊", "🐼")
    return pool[Random.nextInt(pool.size)]
}

@Preview
@Composable
private fun GameScreenPreview() {
    MaterialTheme { GameScreen() }
}

