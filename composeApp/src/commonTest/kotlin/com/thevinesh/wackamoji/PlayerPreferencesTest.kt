package com.thevinesh.wackamoji

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlayerPreferencesTest {

    @Test
    fun store_updatesAndPersistsHapticsPreference() {
        val storage = InMemoryPlayerPreferencesStorage()
        val store = PlayerPreferencesStore(storage)

        store.updateHapticsEnabled(false)

        assertFalse(store.preferences.hapticsEnabled)
        assertFalse(storage.load().hapticsEnabled)
    }

    @Test
    fun settingsScreenBindings_includeHapticsToggle() {
        val audioStore = AudioSettingsStore(InMemoryAudioSettingsStorage())
        val preferencesStore = PlayerPreferencesStore(
            InMemoryPlayerPreferencesStorage(PlayerPreferences(hapticsEnabled = true)),
        )

        val bindings = settingsScreenBindings(audioStore, preferencesStore)
        assertTrue(bindings.hapticsEnabled)

        bindings.onHapticsEnabledChange(false)

        assertFalse(preferencesStore.preferences.hapticsEnabled)
        assertEquals(false, bindings.hapticsEnabled.let {
            // re-bind to observe store
            settingsScreenBindings(audioStore, preferencesStore).hapticsEnabled
        })
    }
}
