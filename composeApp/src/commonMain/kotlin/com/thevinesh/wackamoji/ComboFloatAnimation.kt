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

data class ComboFloat(
    val id: Long,
    val combo: Int,
    val startOffset: Offset,
    val endOffset: Offset,
)

@Composable
fun ComboFloatAnimations(
    floats: List<ComboFloat>,
    onFinished: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        floats.forEach { float ->
            ComboFloatAnimation(
                float = float,
                key = float.id,
                onFinished = { onFinished(float.id) }
            )
        }
    }
}

@Composable
private fun ComboFloatAnimation(
    float: ComboFloat,
    key: Long,
    onFinished: () -> Unit,
) {
    val position = remember {
        Animatable(initialValue = float.startOffset, typeConverter = Offset.VectorConverter)
    }
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(key) {
        // Pop in while the pill drifts upward
        launch {
            scale.animateTo(
                targetValue = 1.35f,
                animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
            )
        }
        position.animateTo(
            targetValue = float.endOffset,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing)
        )

        // Settle and fade out
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 180)
        )
        onFinished()
    }

    Text(
        text = formatComboLabel(float.combo),
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
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
            .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(50))
            .border(2.dp, WackAMojiColors.ScoreBadgeBorder, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}

class ComboFloatManager {
    private val _floats = mutableStateListOf<ComboFloat>()
    val floats: List<ComboFloat> get() = _floats

    private var nextId = 0L

    fun triggerCombo(combo: Int, startOffset: Offset, endOffset: Offset) {
        val float = ComboFloat(
            id = nextId++,
            combo = combo,
            startOffset = startOffset,
            endOffset = endOffset
        )
        _floats.add(float)
    }

    fun removeFloat(id: Long) {
        _floats.removeAll { it.id == id }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun ComboFloatAnimationsPreview() {
    MaterialTheme {
        Surface {
            ComboFloatAnimations(
                floats = listOf(
                    ComboFloat(
                        id = 1,
                        combo = 2,
                        startOffset = Offset(140f, 320f),
                        endOffset = Offset(140f, 240f)
                    ),
                    ComboFloat(
                        id = 2,
                        combo = 3,
                        startOffset = Offset(320f, 340f),
                        endOffset = Offset(320f, 260f)
                    )
                ),
                onFinished = {}
            )
        }
    }
}
