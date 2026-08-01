package com.thevinesh.wackamoji

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

data class PowerUpPickup(
    val id: Long,
    val emoji: String,
    val startOffset: Offset,
    val endOffset: Offset,
)

@Composable
fun PowerUpPickupAnimations(
    pickups: List<PowerUpPickup>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        pickups.forEach { pickup ->
            PowerUpPickupAnimation(
                pickup = pickup,
                key = pickup.id
            )
        }
    }
}

@Composable
private fun PowerUpPickupAnimation(
    pickup: PowerUpPickup,
    key: Long,
) {
    val density = LocalDensity.current
    
    val position = remember {
        Animatable(
            initialValue = pickup.startOffset,
            typeConverter = Offset.VectorConverter
        )
    }
    
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }
    
    LaunchedEffect(key) {
        // Bounce scale up
        scale.animateTo(
            targetValue = 1.3f,
            animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
        )
        
        // Arc to target position and scale down simultaneously
        position.animateTo(
            targetValue = pickup.endOffset,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        
        scale.animateTo(
            targetValue = 0.8f,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
        )
        
        // Fade out
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 200)
        )
    }
    
    if (alpha.value > 0f) {
        Text(
            text = pickup.emoji,
            fontSize = 32.sp,
            fontFamily = LocalEmojiFont.current,
            modifier = Modifier
                .offset {
                    IntOffset(
                        position.value.x.roundToInt(),
                        position.value.y.roundToInt()
                    )
                }
                .scale(scale.value)
        )
    }
}

class PowerUpPickupManager {
    private val _pickups = mutableStateListOf<PowerUpPickup>()
    val pickups: List<PowerUpPickup> get() = _pickups
    
    private var nextId = 0L
    
    fun triggerPickup(emoji: String, startOffset: Offset, endOffset: Offset) {
        val pickup = PowerUpPickup(
            id = nextId++,
            emoji = emoji,
            startOffset = startOffset,
            endOffset = endOffset
        )
        _pickups.add(pickup)
        
        // Remove after animation completes
        // In production, use a coroutine scope tied to composition
    }
    
    fun removePickup(id: Long) {
        _pickups.removeAll { it.id == id }
    }
}
