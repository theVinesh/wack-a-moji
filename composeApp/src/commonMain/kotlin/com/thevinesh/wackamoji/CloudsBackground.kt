package com.thevinesh.wackamoji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

/**
 * Decorative cloud shapes drawn on the sky background.
 *
 * All positions and sizes are expressed as **fractions of the canvas
 * dimensions** so the clouds scale proportionally on any screen size.
 */
@Composable
fun CloudsBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cloudColor = WackAMojiColors.Cloud.copy(alpha = 0.7f)

        // Cloud 1 — top left
        drawRoundRect(
            color = cloudColor,
            topLeft = Offset(x = w * 0.08f, y = h * 0.04f),
            size = Size(width = w * 0.25f, height = h * 0.03f),
            cornerRadius = CornerRadius(w * 0.15f)
        )

        // Cloud 2 — top right
        drawRoundRect(
            color = cloudColor,
            topLeft = Offset(x = w * 0.62f, y = h * 0.07f),
            size = Size(width = w * 0.30f, height = h * 0.035f),
            cornerRadius = CornerRadius(w * 0.15f)
        )

        // Cloud 3 — mid left
        drawRoundRect(
            color = cloudColor,
            topLeft = Offset(x = w * 0.15f, y = h * 0.14f),
            size = Size(width = w * 0.20f, height = h * 0.025f),
            cornerRadius = CornerRadius(w * 0.15f)
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
