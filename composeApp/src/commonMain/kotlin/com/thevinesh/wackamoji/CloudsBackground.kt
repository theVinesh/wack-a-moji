package com.thevinesh.wackamoji

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush

/**
 * Decorative cloud shapes drawn on the sky background.
 *
 * All positions and sizes are expressed as **fractions of the canvas
 * dimensions** so the clouds scale proportionally on any screen size.
 */
@Composable
fun CloudsBackground() {
    val infiniteTransition = rememberInfiniteTransition()

    // Cloud 1 animation (baseline speed)
    val cloud1Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Cloud 2 animation (slower)
    val cloud2Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 55000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Cloud 3 animation (faster)
    val cloud3Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cloudColor = WackAMojiColors.Cloud.copy(alpha = 0.7f)

        // Helper function for seamlessly wrapping animated clouds
        fun drawCloud(
            initialXFraction: Float,
            initialYFraction: Float,
            widthFraction: Float,
            heightFraction: Float,
            progress: Float
        ) {
            val cloudW = w * widthFraction
            val cloudH = h * heightFraction
            val startX = w * initialXFraction
            
            // The full track distance before snapping back to the exact same visual state
            val totalLoopDistance = w + cloudW
            val movedDistance = progress * totalLoopDistance
            
            // Move right, wrap around left when off-screen seamlessly
            val currentX = (startX + movedDistance + cloudW) % totalLoopDistance - cloudW

            drawRoundRect(
                color = cloudColor,
                topLeft = Offset(x = currentX, y = h * initialYFraction),
                size = Size(width = cloudW, height = cloudH),
                cornerRadius = CornerRadius(w * 0.15f)
            )
        }

        // Cloud 1 — top left (started)
        drawCloud(
            initialXFraction = 0.08f,
            initialYFraction = 0.04f,
            widthFraction = 0.25f,
            heightFraction = 0.03f,
            progress = cloud1Progress
        )

        // Cloud 2 — top right
        drawCloud(
            initialXFraction = 0.62f,
            initialYFraction = 0.07f,
            widthFraction = 0.30f,
            heightFraction = 0.035f,
            progress = cloud2Progress
        )

        // Cloud 3 — mid left
        drawCloud(
            initialXFraction = 0.15f,
            initialYFraction = 0.14f,
            widthFraction = 0.20f,
            heightFraction = 0.025f,
            progress = cloud3Progress
        )
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun CloudsBackgroundPreview() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(WackAMojiColors.SkyTop, WackAMojiColors.SkyBottom)
                    )
                )
            }
            CloudsBackground()
        }
    }
}
