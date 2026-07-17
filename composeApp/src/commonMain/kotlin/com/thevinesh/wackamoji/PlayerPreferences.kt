package com.thevinesh.wackamoji

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PlayerPreferences(
    val hapticsEnabled: Boolean = true,
    val reduceMotion: Boolean = false,
    val largeTargets: Boolean = false,
)

internal fun gameButtonHeight(largeTargets: Boolean): Dp =
    if (largeTargets) 76.dp else 64.dp

internal fun gameButtonFaceHeight(largeTargets: Boolean): Dp =
    if (largeTargets) 70.dp else 58.dp

internal fun moleHoleCellPadding(largeTargets: Boolean): Dp =
    if (largeTargets) 2.dp else 6.dp

internal fun moleEmojiFontSize(largeTargets: Boolean): TextUnit =
    if (largeTargets) 42.sp else 36.sp

internal fun shouldAnimateClouds(animateClouds: Boolean, reduceMotion: Boolean): Boolean =
    animateClouds && !reduceMotion

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
