package com.thevinesh.wackamoji

import kotlin.js.ExperimentalWasmJsInterop

private const val MUSIC_VOLUME_STORAGE_KEY = "wackamoji.musicVolume"
private const val SOUND_EFFECT_VOLUME_STORAGE_KEY = "wackamoji.soundEffectVolume"

internal class WasmAudioSettingsStorage : AudioSettingsStorage {
    override fun load(): AudioSettings = AudioSettings(
        musicVolume = getStoredFloat(MUSIC_VOLUME_STORAGE_KEY, DEFAULT_MUSIC_VOLUME),
        soundEffectVolume = getStoredFloat(SOUND_EFFECT_VOLUME_STORAGE_KEY, DEFAULT_SOUND_EFFECT_VOLUME),
    )

    override fun save(settings: AudioSettings) {
        setLocalStorageItem(MUSIC_VOLUME_STORAGE_KEY, settings.musicVolume.toString())
        setLocalStorageItem(SOUND_EFFECT_VOLUME_STORAGE_KEY, settings.soundEffectVolume.toString())
    }
}

private fun getStoredFloat(key: String, defaultValue: Float): Float {
    return getLocalStorageItem(key)?.toFloatOrNull() ?: defaultValue
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    (key) => {
      try {
        return typeof window !== 'undefined' && window.localStorage
          ? window.localStorage.getItem(key)
          : null;
      } catch (_) {
        return null;
      }
    }
    """
)
private external fun getLocalStorageItem(key: String): String?

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    (key, value) => {
      try {
        if (typeof window !== 'undefined' && window.localStorage) {
          window.localStorage.setItem(key, value);
        }
      } catch (_) {
      }
    }
    """
)
private external fun setLocalStorageItem(key: String, value: String)