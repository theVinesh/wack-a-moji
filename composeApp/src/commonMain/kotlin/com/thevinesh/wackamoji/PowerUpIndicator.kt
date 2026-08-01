package com.thevinesh.wackamoji

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ActivePowerUp(
    val type: PowerUpType,
    val ticksRemaining: Int,
)

@Composable
fun PowerUpDock(
    activePowerUps: List<ActivePowerUp>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        activePowerUps.forEach { powerUp ->
            PowerUpIndicatorIcon(
                powerUp = powerUp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun PowerUpIndicatorIcon(
    powerUp: ActivePowerUp,
    modifier: Modifier = Modifier,
) {
    val emoji = powerUpEmoji(powerUp.type)
    val progress = powerUp.ticksRemaining.toFloat() / POWER_UP_DURATION_TICKS.toFloat()
    
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }
    
    // Pulse animation while active
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = tween(durationMillis = 600, easing = LinearEasing)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = LinearEasing)
        )
    }
    
    // Fade out when expiring
    LaunchedEffect(powerUp.ticksRemaining) {
        if (powerUp.ticksRemaining <= TICKS_PER_SECOND) {
            alpha.animateTo(0f, animationSpec = tween(durationMillis = 500))
        }
    }

    Box(
        modifier = modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        // Circular progress ring
        Canvas(modifier = Modifier.size(48.dp)) {
            val strokeWidth = 4.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            
            // Background circle
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth)
            )
            
            // Progress arc
            val sweepAngle = 360f * progress
            drawArc(
                color = powerUpColor(powerUp.type).copy(alpha = alpha.value),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        // Emoji in the center
        Text(
            text = emoji,
            fontSize = 24.sp,
            fontFamily = LocalEmojiFont.current,
            modifier = Modifier
        )
    }
}

private fun powerUpColor(type: PowerUpType): Color = when (type) {
    PowerUpType.ExtraLife -> Color(0xFFFF6B6B)  // Red
    PowerUpType.Slowdown -> Color(0xFFAB47BC)   // Purple
    PowerUpType.Freeze -> Color(0xFF42A5F5)     // Blue
}
