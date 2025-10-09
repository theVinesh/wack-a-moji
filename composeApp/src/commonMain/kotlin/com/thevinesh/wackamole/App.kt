package com.thevinesh.wackamole

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            GameScreen()
        }
    }
}

@Composable
private fun GameScreen() {
    var score by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(true) }

    // 9 cells, each indicates if the mole is currently up
    val cells = remember { mutableStateListOf<Boolean>().also { it.addAll(List(9) { false }) } }
    // Random emoji per cell for fun variety
    val emojis = remember { List(9) { randomMoleEmoji() } }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        // Track remaining up-time for each cell in milliseconds
        val remaining = LongArray(9) { 0L }
        // Main loop ticks at a steady cadence, updating timers and spawning new moles
        while (running) {
            // decrement timers and drop finished moles
            for (i in 0 until 9) {
                if (cells[i]) {
                    remaining[i] = (remaining[i] - 80L).coerceAtLeast(0L)
                    if (remaining[i] == 0L) cells[i] = false
                }
            }

            // Possibly spawn a new mole if under cap
            val upCount = cells.count { it }
            val canSpawnMore = upCount < 3
            if (canSpawnMore) {
                val idx = Random.nextInt(0, 9)
                if (!cells[idx]) {
                    cells[idx] = true
                    remaining[idx] = Random.nextLong(600, 1200)
                }
            }

            // Wait until next tick
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

        Grid3x3(
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

@Composable
private fun Grid3x3(
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

@Composable
private fun MoleHole(
    isUp: Boolean,
    emoji: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isUp,
            enter = fadeIn() + slideInVertically(animationSpec = tween(150)) { fullHeight -> fullHeight / 2 },
            exit = fadeOut() + slideOutVertically(animationSpec = tween(150)) { fullHeight -> fullHeight / 2 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onTap),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 36.sp)
            }
        }
    }
}

private fun randomMoleEmoji(): String {
    val pool = listOf("🐹", "🐰", "🐵", "🐶", "🐱", "🐸", "🐻", "🦊", "🐼")
    return pool[Random.nextInt(pool.size)]
}
