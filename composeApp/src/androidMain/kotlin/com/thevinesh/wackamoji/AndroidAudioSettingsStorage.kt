package com.thevinesh.wackamoji

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

private const val AUDIO_SETTINGS_PREFERENCES_NAME = "audio_settings"
private const val MUSIC_VOLUME_PREFERENCE_KEY = "music_volume"
private const val SOUND_EFFECT_VOLUME_PREFERENCE_KEY = "sound_effect_volume"

internal class AndroidAudioSettingsStorage(
    context: Context,
) : AudioSettingsStorage {
    private val sharedPreferences = context.applicationContext.getSharedPreferences(
        AUDIO_SETTINGS_PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override fun load(): AudioSettings = AudioSettings(
        musicVolume = sharedPreferences.getStoredFloat(
            key = MUSIC_VOLUME_PREFERENCE_KEY,
            defaultValue = DEFAULT_MUSIC_VOLUME,
        ),
        soundEffectVolume = sharedPreferences.getStoredFloat(
            key = SOUND_EFFECT_VOLUME_PREFERENCE_KEY,
            defaultValue = DEFAULT_SOUND_EFFECT_VOLUME,
        ),
    )

    override fun save(settings: AudioSettings) {
        sharedPreferences.edit {
            putFloat(MUSIC_VOLUME_PREFERENCE_KEY, settings.musicVolume)
                .putFloat(SOUND_EFFECT_VOLUME_PREFERENCE_KEY, settings.soundEffectVolume)
        }
    }
}

private fun SharedPreferences.getStoredFloat(key: String, defaultValue: Float): Float {
    return if (contains(key)) getFloat(key, defaultValue) else defaultValue
}