package com.thevinesh.wackamoji

import kotlin.test.Test
import kotlin.test.assertEquals

class AudioSettingsTest {

    @Test
    fun audioSettings_defaultsMatchSpecAssumptions() {
        val settings = AudioSettings()

        assertEquals(DEFAULT_MUSIC_VOLUME, settings.musicVolume)
        assertEquals(DEFAULT_SOUND_EFFECT_VOLUME, settings.soundEffectVolume)
    }

    @Test
    fun audioSettingsStore_loadsInitialSettingsFromStorage() {
        val store = AudioSettingsStore(
            RecordingAudioSettingsStorage(
                AudioSettings(musicVolume = 0.25f, soundEffectVolume = 0.75f)
            )
        )

        assertEquals(AudioSettings(musicVolume = 0.25f, soundEffectVolume = 0.75f), store.settings)
    }

    @Test
    fun audioSettingsStore_normalizesOutOfRangeStoredValues() {
        val store = AudioSettingsStore(
            RecordingAudioSettingsStorage(
                AudioSettings(musicVolume = -0.5f, soundEffectVolume = 1.5f)
            )
        )

        assertEquals(AudioSettings(musicVolume = 0f, soundEffectVolume = 1f), store.settings)
    }

    @Test
    fun audioSettingsStore_updateMusicVolume_savesNormalizedValue() {
        val storage = RecordingAudioSettingsStorage()
        val store = AudioSettingsStore(storage)

        store.updateMusicVolume(1.2f)

        assertEquals(AudioSettings(musicVolume = 1f, soundEffectVolume = DEFAULT_SOUND_EFFECT_VOLUME), store.settings)
        assertEquals(store.settings, storage.savedSettings.single())
    }

    @Test
    fun audioSettingsStore_updateSoundEffectVolume_savesValue() {
        val storage = RecordingAudioSettingsStorage()
        val store = AudioSettingsStore(storage)

        store.updateSoundEffectVolume(0.42f)

        assertEquals(AudioSettings(musicVolume = DEFAULT_MUSIC_VOLUME, soundEffectVolume = 0.42f), store.settings)
        assertEquals(store.settings, storage.savedSettings.single())
    }

    @Test
    fun audioSettings_applyTo_clampsAndRoutesMusicAndSfxVolumes() {
        val backgroundMusicController = RecordingBackgroundMusicController()
        val soundEffectPlayer = RecordingSoundEffectPlayer()

        AudioSettings(musicVolume = -0.2f, soundEffectVolume = 1.3f).applyTo(
            backgroundMusicController = backgroundMusicController,
            soundEffectPlayer = soundEffectPlayer,
        )

        assertEquals(0f, backgroundMusicController.volume)
        assertEquals(1f, soundEffectPlayer.volume)
    }

    private class RecordingAudioSettingsStorage(
        private var storedSettings: AudioSettings = AudioSettings(),
    ) : AudioSettingsStorage {
        val savedSettings = mutableListOf<AudioSettings>()

        override fun load(): AudioSettings = storedSettings

        override fun save(settings: AudioSettings) {
            storedSettings = settings
            savedSettings += settings
        }
    }

    private class RecordingBackgroundMusicController : BackgroundMusicController {
        var volume: Float? = null

        override fun start(track: BackgroundMusicTrack, loop: Boolean) = Unit

        override fun setVolume(volume: Float) {
            this.volume = volume
        }

        override fun pause() = Unit

        override fun resume() = Unit

        override fun stop() = Unit
    }

    private class RecordingSoundEffectPlayer : SoundEffectPlayer {
        var volume: Float? = null

        override fun play(effect: SoundEffect) = Unit

        override fun setVolume(volume: Float) {
            this.volume = volume
        }

        override fun dispose() = Unit
    }
}