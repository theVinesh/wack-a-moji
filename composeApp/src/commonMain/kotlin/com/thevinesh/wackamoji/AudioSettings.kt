package com.thevinesh.wackamoji

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

internal const val DEFAULT_MUSIC_VOLUME = 0.6f
internal const val DEFAULT_SOUND_EFFECT_VOLUME = 0.9f

data class AudioSettings(
    val musicVolume: Float = DEFAULT_MUSIC_VOLUME,
    val soundEffectVolume: Float = DEFAULT_SOUND_EFFECT_VOLUME,
)

internal fun Float.normalizedAudioVolume(): Float = coerceIn(0f, 1f)

internal interface AudioSettingsStorage {
    fun load(): AudioSettings
    fun save(settings: AudioSettings)
}

internal class AudioSettingsStore(
    private val storage: AudioSettingsStorage,
) {
    var settings by mutableStateOf(storage.load().normalized())
        private set

    fun updateMusicVolume(volume: Float) {
        update(settings.copy(musicVolume = volume))
    }

    fun updateSoundEffectVolume(volume: Float) {
        update(settings.copy(soundEffectVolume = volume))
    }

    fun update(newSettings: AudioSettings) {
        val normalized = newSettings.normalized()
        if (settings == normalized) {
            return
        }

        settings = normalized
        storage.save(normalized)
    }
}

internal class InMemoryAudioSettingsStorage(
    private var storedSettings: AudioSettings = AudioSettings(),
) : AudioSettingsStorage {
    override fun load(): AudioSettings = storedSettings

    override fun save(settings: AudioSettings) {
        storedSettings = settings
    }
}

internal fun AudioSettings.applyTo(
    backgroundMusicController: BackgroundMusicController,
    soundEffectPlayer: SoundEffectPlayer,
) {
    val normalizedSettings = normalized()
    backgroundMusicController.setVolume(normalizedSettings.musicVolume)
    soundEffectPlayer.setVolume(normalizedSettings.soundEffectVolume)
}

internal fun AudioSettings.normalized(): AudioSettings = copy(
    musicVolume = musicVolume.normalizedAudioVolume(),
    soundEffectVolume = soundEffectVolume.normalizedAudioVolume(),
)