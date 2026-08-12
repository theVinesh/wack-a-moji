package com.thevinesh.wackamoji

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

/** Combo label text shown on floating combo pills. */
internal fun formatComboLabel(combo: Int): String =
    if (combo <= 0) "NO COMBO" else "x$combo COMBO"

/**
 * A short-lived floating label (combo milestones, score deltas, ...).
 * Deliberately subtle: small text, gentle pop, quick fade.
 */
data class FloatingLabel(
    val id: Long,
    val text: String,
    val startOffset: Offset,
    val endOffset: Offset,
)

@Composable
fun FloatingLabelAnimations(
    labels: List<FloatingLabel>,
    onFinished: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        labels.forEach { label ->
            FloatingLabelAnimation(
                label = label,
                key = label.id,
                onFinished = { onFinished(label.id) }
            )
        }
    }
}

@Composable
private fun FloatingLabelAnimation(
    label: FloatingLabel,
    key: Long,
    onFinished: () -> Unit,
) {
    val position = remember {
        Animatable(initialValue = label.startOffset, typeConverter = Offset.VectorConverter)
    }
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(key) {
        // Gentle pop in while the label drifts upward
        launch {
            scale.animateTo(
                targetValue = 1.15f,
                animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
            )
        }
        position.animateTo(
            targetValue = label.endOffset,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing)
        )

        // Settle and fade out
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 160)
        )
        onFinished()
    }

    Text(
        text = label.text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = WackAMojiColors.Primary,
        modifier = Modifier
            .offset {
                IntOffset(
                    position.value.x.roundToInt(),
                    position.value.y.roundToInt()
                )
            }
            .scale(scale.value)
            .graphicsLayer { this.alpha = alpha.value }
            .background(Color.White.copy(alpha = 0.75f), RoundedCornerShape(50))
            .border(1.dp, WackAMojiColors.ScoreBadgeBorder, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp),
    )
}

class FloatingLabelManager {
    private val _labels = mutableStateListOf<FloatingLabel>()
    val labels: List<FloatingLabel> get() = _labels

    private var nextId = 0L

    fun trigger(text: String, startOffset: Offset, endOffset: Offset) {
        val label = FloatingLabel(
            id = nextId++,
            text = text,
            startOffset = startOffset,
            endOffset = endOffset
        )
        _labels.add(label)
    }

    fun removeLabel(id: Long) {
        _labels.removeAll { it.id == id }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun FloatingLabelAnimationsPreview() {
    MaterialTheme {
        Surface {
            FloatingLabelAnimations(
                labels = listOf(
                    FloatingLabel(
                        id = 1,
                        text = "x2 COMBO",
                        startOffset = Offset(120f, 320f),
                        endOffset = Offset(120f, 264f)
                    ),
                    FloatingLabel(
                        id = 2,
                        text = "+3",
                        startOffset = Offset(280f, 120f),
                        endOffset = Offset(280f, 64f)
                    )
                ),
                onFinished = {}
            )
        }
    }
}
