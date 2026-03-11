package com.thevinesh.wackamoji

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

private val SHARED_SKY_CONTENT_MAX_WIDTH = 430.dp
private const val STATIC_CLOUD_ALPHA = 0.7f
private const val STATIC_CLOUD_CORNER_RADIUS_SCALE = 0.15f
private val STATIC_CLOUD_LAYOUTS = listOf(
    StaticCloudLayout(x = 0.08f, y = 0.04f, width = 0.25f, height = 0.03f),
    StaticCloudLayout(x = 0.62f, y = 0.07f, width = 0.30f, height = 0.035f),
    StaticCloudLayout(x = 0.15f, y = 0.14f, width = 0.20f, height = 0.025f),
)

private data class StaticCloudLayout(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
)

@Composable
internal fun SharedSkyScreen(
    modifier: Modifier = Modifier,
    animateClouds: Boolean = true,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(WackAMojiColors.SkyTop, WackAMojiColors.SkyBottom)
                )
            )
        }

        if (animateClouds) {
            CloudsBackground()
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cloudColor = WackAMojiColors.Cloud.copy(alpha = STATIC_CLOUD_ALPHA)

                fun drawCloud(x: Float, y: Float, width: Float, height: Float) {
                    drawRoundRect(
                        color = cloudColor,
                        topLeft = Offset(size.width * x, size.height * y),
                        size = Size(size.width * width, size.height * height),
                        cornerRadius = CornerRadius(size.width * STATIC_CLOUD_CORNER_RADIUS_SCALE)
                    )
                }

                STATIC_CLOUD_LAYOUTS.forEach { cloud ->
                    drawCloud(x = cloud.x, y = cloud.y, width = cloud.width, height = cloud.height)
                }
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = SHARED_SKY_CONTENT_MAX_WIDTH)
                .fillMaxHeight(),
            content = content,
        )

        overlay()
    }
}

@Preview
@Composable
private fun SharedSkyScreenPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SharedSkyScreen(animateClouds = false) {
                Text(
                    text = "Preview",
                    color = WackAMojiColors.SkyDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}