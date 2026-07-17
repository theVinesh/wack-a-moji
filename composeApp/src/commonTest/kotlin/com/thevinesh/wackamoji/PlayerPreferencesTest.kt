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

    @Test
    fun store_updatesAccessibilityPreferences() {
        val store = PlayerPreferencesStore(InMemoryPlayerPreferencesStorage())

        store.updateReduceMotion(true)
        store.updateLargeTargets(true)

        assertTrue(store.preferences.reduceMotion)
        assertTrue(store.preferences.largeTargets)
    }

    @Test
    fun accessibilityHelpers_scaleTargetsAndMotion() {
        assertEquals(64, gameButtonHeight(largeTargets = false).value.toInt())
        assertEquals(76, gameButtonHeight(largeTargets = true).value.toInt())
        assertEquals(6, moleHoleCellPadding(largeTargets = false).value.toInt())
        assertEquals(2, moleHoleCellPadding(largeTargets = true).value.toInt())
        assertTrue(shouldAnimateClouds(animateClouds = true, reduceMotion = false))
        assertFalse(shouldAnimateClouds(animateClouds = true, reduceMotion = true))
    }
}
