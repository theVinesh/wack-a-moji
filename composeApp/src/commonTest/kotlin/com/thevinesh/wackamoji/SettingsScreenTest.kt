package com.thevinesh.wackamoji

import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsScreenTest {

    @Test
    fun settingsScreenBindings_reflectCurrentStoreValues() {
        val store = AudioSettingsStore(
            InMemoryAudioSettingsStorage(
                AudioSettings(musicVolume = 0.2f, soundEffectVolume = 0.8f)
            )
        )
        val preferencesStore = PlayerPreferencesStore(
            InMemoryPlayerPreferencesStorage(PlayerPreferences(hapticsEnabled = false)),
        )

        val bindings = settingsScreenBindings(store, preferencesStore)

        assertEquals(0.2f, bindings.musicVolume)
        assertEquals(0.8f, bindings.soundEffectVolume)
        assertEquals(false, bindings.hapticsEnabled)
        assertEquals(false, bindings.reduceMotion)
        assertEquals(false, bindings.largeTargets)
    }

    @Test
    fun settingsScreenBindings_callbacksUpdatePersistedAudioSettings() {
        val store = AudioSettingsStore(InMemoryAudioSettingsStorage())
        val preferencesStore = PlayerPreferencesStore(InMemoryPlayerPreferencesStorage())
        val bindings = settingsScreenBindings(store, preferencesStore)

        bindings.onMusicVolumeChange(0.15f)
        bindings.onSoundEffectVolumeChange(0.65f)

        assertEquals(0.15f, store.settings.musicVolume)
        assertEquals(0.65f, store.settings.soundEffectVolume)
    }

    @Test
    fun formatVolumePercentage_normalizesAndRoundsForDisplay() {
        // Out of bounds values
        assertEquals("0%", formatVolumePercentage(-0.2f))
        assertEquals("100%", formatVolumePercentage(1.4f))

        // Exact boundaries
        assertEquals("0%", formatVolumePercentage(0.0f))
        assertEquals("50%", formatVolumePercentage(0.5f))
        assertEquals("100%", formatVolumePercentage(1.0f))

        // Rounding behavior
        assertEquals("43%", formatVolumePercentage(0.426f))
        assertEquals("0%", formatVolumePercentage(0.004f))
        assertEquals("1%", formatVolumePercentage(0.005f))
        assertEquals("99%", formatVolumePercentage(0.994f))
        assertEquals("100%", formatVolumePercentage(0.995f))
    }
}