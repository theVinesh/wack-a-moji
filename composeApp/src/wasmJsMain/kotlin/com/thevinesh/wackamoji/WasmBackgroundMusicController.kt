package com.thevinesh.wackamoji

import kotlin.js.ExperimentalWasmJsInterop

private const val WEB_GAMEPLAY_LOOP_RESOURCE_PATH = "background-music-loop.mp3"

internal class WasmBackgroundMusicController : BackgroundMusicController {
    override fun start(track: BackgroundMusicTrack, loop: Boolean) {
        startWebBackgroundMusic(track.resourcePath(), loop)
    }

    override fun pause() {
        pauseWebBackgroundMusic()
    }

    override fun resume() {
        resumeWebBackgroundMusic()
    }

    override fun stop() {
        stopWebBackgroundMusic()
    }
}

private fun BackgroundMusicTrack.resourcePath(): String = when (this) {
    BackgroundMusicTrack.GameplayLoop -> WEB_GAMEPLAY_LOOP_RESOURCE_PATH
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    (resourcePath, loop) => {
      const controller = window.wackAMojiBackgroundMusic;
      if (controller) {
        controller.start(resourcePath, loop);
      }
    }
    """
)
private external fun startWebBackgroundMusic(resourcePath: String, loop: Boolean)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    () => {
      const controller = window.wackAMojiBackgroundMusic;
      if (controller) {
        controller.pause();
      }
    }
    """
)
private external fun pauseWebBackgroundMusic()

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    () => {
      const controller = window.wackAMojiBackgroundMusic;
      if (controller) {
        controller.resume();
      }
    }
    """
)
private external fun resumeWebBackgroundMusic()

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    () => {
      const controller = window.wackAMojiBackgroundMusic;
      if (controller) {
        controller.stop();
      }
    }
    """
)
private external fun stopWebBackgroundMusic()