package com.thevinesh.wackamoji

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class PlayerPreferences(
    val hapticsEnabled: Boolean = true,
    val reduceMotion: Boolean = false,
    val largeTargets: Boolean = false,
)

internal interface PlayerPreferencesStorage {
    fun load(): PlayerPreferences
    fun save(preferences: PlayerPreferences)
}

internal class PlayerPreferencesStore(
    private val storage: PlayerPreferencesStorage,
) {
    var preferences by mutableStateOf(storage.load())
        private set

    fun updateHapticsEnabled(enabled: Boolean) {
        update(preferences.copy(hapticsEnabled = enabled))
    }

    fun updateReduceMotion(enabled: Boolean) {
        update(preferences.copy(reduceMotion = enabled))
    }

    fun updateLargeTargets(enabled: Boolean) {
        update(preferences.copy(largeTargets = enabled))
    }

    fun update(newPreferences: PlayerPreferences) {
        if (preferences == newPreferences) {
            return
        }
        preferences = newPreferences
        storage.save(newPreferences)
    }
}

internal class InMemoryPlayerPreferencesStorage(
    private var storedPreferences: PlayerPreferences = PlayerPreferences(),
) : PlayerPreferencesStorage {
    override fun load(): PlayerPreferences = storedPreferences

    override fun save(preferences: PlayerPreferences) {
        storedPreferences = preferences
    }
}
