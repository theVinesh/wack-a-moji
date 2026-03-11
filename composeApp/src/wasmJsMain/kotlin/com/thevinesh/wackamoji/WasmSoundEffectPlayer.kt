package com.thevinesh.wackamoji

import kotlin.js.ExperimentalWasmJsInterop

private const val WEB_WACK_SOUND_EFFECT_RESOURCE_PATH = "wack.mp3"
private const val WEB_CLICK_SOUND_EFFECT_RESOURCE_PATH = "click.mp3"

internal class WasmSoundEffectPlayer : SoundEffectPlayer {
    override fun play(effect: SoundEffect) {
        playWebSoundEffect(effect.resourcePath())
    }

    override fun setVolume(volume: Float) {
        setWebSoundEffectVolume(volume.normalizedAudioVolume())
    }

    override fun dispose() {
        disposeWebSoundEffects()
    }
}

private fun SoundEffect.resourcePath(): String = when (this) {
    SoundEffect.Wack -> WEB_WACK_SOUND_EFFECT_RESOURCE_PATH
    SoundEffect.Click -> WEB_CLICK_SOUND_EFFECT_RESOURCE_PATH
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    (resourcePath) => {
      const player = window.wackAMojiSoundEffects;
      if (player) {
        player.play(resourcePath);
      }
    }
    """
)
private external fun playWebSoundEffect(resourcePath: String)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    (volume) => {
      const player = window.wackAMojiSoundEffects;
      if (player) {
        player.setVolume(volume);
      }
    }
    """
)
private external fun setWebSoundEffectVolume(volume: Float)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    () => {
      const player = window.wackAMojiSoundEffects;
      if (player) {
        player.dispose();
      }
    }
    """
)
private external fun disposeWebSoundEffects()