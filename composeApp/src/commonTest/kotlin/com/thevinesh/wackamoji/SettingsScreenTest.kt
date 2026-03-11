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

        val bindings = settingsScreenBindings(store)

        assertEquals(0.2f, bindings.musicVolume)
        assertEquals(0.8f, bindings.soundEffectVolume)
    }

    @Test
    fun settingsScreenBindings_callbacksUpdatePersistedAudioSettings() {
        val store = AudioSettingsStore(InMemoryAudioSettingsStorage())
        val bindings = settingsScreenBindings(store)

        bindings.onMusicVolumeChange(0.15f)
        bindings.onSoundEffectVolumeChange(0.65f)

        assertEquals(0.15f, store.settings.musicVolume)
        assertEquals(0.65f, store.settings.soundEffectVolume)
    }

    @Test
    fun formatVolumePercentage_normalizesAndRoundsForDisplay() {
        assertEquals("0%", formatVolumePercentage(-0.2f))
        assertEquals("43%", formatVolumePercentage(0.426f))
        assertEquals("100%", formatVolumePercentage(1.4f))
    }
}