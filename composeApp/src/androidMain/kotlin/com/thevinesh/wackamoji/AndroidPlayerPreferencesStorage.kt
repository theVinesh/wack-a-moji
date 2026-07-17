package com.thevinesh.wackamoji

import android.content.Context
import androidx.core.content.edit

private const val PLAYER_PREFERENCES_NAME = "player_preferences"
private const val HAPTICS_ENABLED_KEY = "haptics_enabled"
private const val REDUCE_MOTION_KEY = "reduce_motion"
private const val LARGE_TARGETS_KEY = "large_targets"

internal class AndroidPlayerPreferencesStorage(
    context: Context,
) : PlayerPreferencesStorage {
    private val sharedPreferences = context.applicationContext.getSharedPreferences(
        PLAYER_PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override fun load(): PlayerPreferences = PlayerPreferences(
        hapticsEnabled = sharedPreferences.getBoolean(HAPTICS_ENABLED_KEY, true),
        reduceMotion = sharedPreferences.getBoolean(REDUCE_MOTION_KEY, false),
        largeTargets = sharedPreferences.getBoolean(LARGE_TARGETS_KEY, false),
    )

    override fun save(preferences: PlayerPreferences) {
        sharedPreferences.edit {
            putBoolean(HAPTICS_ENABLED_KEY, preferences.hapticsEnabled)
                .putBoolean(REDUCE_MOTION_KEY, preferences.reduceMotion)
                .putBoolean(LARGE_TARGETS_KEY, preferences.largeTargets)
        }
    }
}
