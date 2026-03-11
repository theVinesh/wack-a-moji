package com.thevinesh.wackamoji

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

enum class BackgroundMusicTrack {
    GameplayLoop,
}

enum class BackgroundMusicPlayback {
    Stopped,
    Paused,
    Playing,
}

data class BackgroundMusicState(
    val track: BackgroundMusicTrack = BackgroundMusicTrack.GameplayLoop,
    val playback: BackgroundMusicPlayback = BackgroundMusicPlayback.Stopped,
    val loop: Boolean = true,
)

interface BackgroundMusicController {
    fun start(track: BackgroundMusicTrack, loop: Boolean = true)
    fun pause()
    fun resume()
    fun stop()
}

object NoOpBackgroundMusicController : BackgroundMusicController {
    override fun start(track: BackgroundMusicTrack, loop: Boolean) = Unit

    override fun pause() = Unit

    override fun resume() = Unit

    override fun stop() = Unit
}

internal sealed interface BackgroundMusicAction {
    data class Start(val track: BackgroundMusicTrack, val loop: Boolean) : BackgroundMusicAction
    data object Pause : BackgroundMusicAction
    data object Resume : BackgroundMusicAction
    data object Stop : BackgroundMusicAction
}

internal fun resolveBackgroundMusicAction(
    previousState: BackgroundMusicState?,
    desiredState: BackgroundMusicState,
): BackgroundMusicAction? = when (desiredState.playback) {
    BackgroundMusicPlayback.Playing -> when {
        previousState == null || previousState.playback == BackgroundMusicPlayback.Stopped -> {
            BackgroundMusicAction.Start(track = desiredState.track, loop = desiredState.loop)
        }

        previousState.track != desiredState.track || previousState.loop != desiredState.loop -> {
            BackgroundMusicAction.Start(track = desiredState.track, loop = desiredState.loop)
        }

        previousState.playback == BackgroundMusicPlayback.Paused -> BackgroundMusicAction.Resume
        else -> null
    }

    BackgroundMusicPlayback.Paused -> {
        if (previousState?.playback == BackgroundMusicPlayback.Playing) {
            BackgroundMusicAction.Pause
        } else {
            null
        }
    }

    BackgroundMusicPlayback.Stopped -> {
        if (previousState?.playback != null && previousState.playback != BackgroundMusicPlayback.Stopped) {
            BackgroundMusicAction.Stop
        } else {
            null
        }
    }
}

internal fun BackgroundMusicController.apply(action: BackgroundMusicAction) {
    when (action) {
        is BackgroundMusicAction.Start -> start(track = action.track, loop = action.loop)
        BackgroundMusicAction.Pause -> pause()
        BackgroundMusicAction.Resume -> resume()
        BackgroundMusicAction.Stop -> stop()
    }
}

@Composable
internal fun BindBackgroundMusic(
    controller: BackgroundMusicController,
    desiredState: BackgroundMusicState,
    onAppForegrounded: () -> Unit,
    onAppBackgrounded: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnAppForegrounded by rememberUpdatedState(onAppForegrounded)
    val currentOnAppBackgrounded by rememberUpdatedState(onAppBackgrounded)
    var appliedState: BackgroundMusicState? by remember(controller) { mutableStateOf(null) }

    DisposableEffect(lifecycleOwner, controller) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> currentOnAppForegrounded()
                Lifecycle.Event.ON_STOP -> currentOnAppBackgrounded()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            controller.stop()
        }
    }

    LaunchedEffect(controller, desiredState) {
        resolveBackgroundMusicAction(
            previousState = appliedState,
            desiredState = desiredState,
        )?.let(controller::apply)

        appliedState = desiredState
    }
}