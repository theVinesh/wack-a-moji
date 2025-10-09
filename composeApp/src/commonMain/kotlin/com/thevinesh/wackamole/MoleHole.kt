package com.thevinesh.wackamole

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MoleHole(
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

@Preview
@Composable
private fun MoleHolePreview() {
    MaterialTheme {
        MoleHole(isUp = true, emoji = "🐹", onTap = {})
    }
}

