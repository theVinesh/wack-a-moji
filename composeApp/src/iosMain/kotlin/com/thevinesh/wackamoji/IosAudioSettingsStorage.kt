package com.thevinesh.wackamoji

import platform.Foundation.NSUserDefaults

private const val MUSIC_VOLUME_PREFERENCE_KEY = "music_volume"
private const val SOUND_EFFECT_VOLUME_PREFERENCE_KEY = "sound_effect_volume"

internal class IosAudioSettingsStorage(
    private val userDefaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : AudioSettingsStorage {
    override fun load(): AudioSettings = AudioSettings(
        musicVolume = userDefaults.storedFloat(
            key = MUSIC_VOLUME_PREFERENCE_KEY,
            defaultValue = DEFAULT_MUSIC_VOLUME,
        ),
        soundEffectVolume = userDefaults.storedFloat(
            key = SOUND_EFFECT_VOLUME_PREFERENCE_KEY,
            defaultValue = DEFAULT_SOUND_EFFECT_VOLUME,
        ),
    )

    override fun save(settings: AudioSettings) {
        userDefaults.setDouble(settings.musicVolume.toDouble(), forKey = MUSIC_VOLUME_PREFERENCE_KEY)
        userDefaults.setDouble(settings.soundEffectVolume.toDouble(), forKey = SOUND_EFFECT_VOLUME_PREFERENCE_KEY)
    }
}

private fun NSUserDefaults.storedFloat(key: String, defaultValue: Float): Float {
    return if (objectForKey(key) == null) defaultValue else doubleForKey(key).toFloat()
}