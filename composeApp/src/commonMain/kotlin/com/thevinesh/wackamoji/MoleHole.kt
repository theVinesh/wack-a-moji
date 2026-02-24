package com.thevinesh.wackamoji

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
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
            .aspectRatio(1f)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        // Brown circle background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Main brown fill
            drawCircle(
                color = WackAMojiColors.EarthBrown,
                radius = w / 2f,
                center = Offset(w / 2f, h / 2f)
            )

            // Inset shadow effect — dark gradient from top
            val insetPath = Path().apply {
                addOval(Rect(0f, 0f, w, h))
            }
            drawPath(
                path = insetPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x99000000), // 60% black at top
                        Color(0x33000000), // 20% black at middle
                        Color(0x00000000), // transparent at bottom
                    ),
                    startY = 0f,
                    endY = h * 0.7f
                )
            )

            // Top border accent — darker arc at the top
            drawArc(
                color = WackAMojiColors.HoleTopBorder,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(2f, 2f),
                size = androidx.compose.ui.geometry.Size(w - 4f, h - 4f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
            )
        }

        // Emoji appearing in the hole
        AnimatedVisibility(
            visible = isUp,
            enter = scaleIn(
                animationSpec = tween(150),
                initialScale = 0.3f
            ) + fadeIn(animationSpec = tween(150)),
            exit = scaleOut(
                animationSpec = tween(150),
                targetScale = 0.3f
            ) + fadeOut(animationSpec = tween(150))
        ) {
            Text(
                text = emoji,
                fontSize = 36.sp,
                fontFamily = LocalEmojiFont.current,
                modifier = Modifier.padding(4.dp)
            )
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
