package com.thevinesh.wackamoji

import kotlinx.browser.localStorage

private const val HAPTICS_ENABLED_KEY = "wackamoji.haptics_enabled"
private const val REDUCE_MOTION_KEY = "wackamoji.reduce_motion"
private const val LARGE_TARGETS_KEY = "wackamoji.large_targets"

internal class WasmPlayerPreferencesStorage : PlayerPreferencesStorage {
    override fun load(): PlayerPreferences = PlayerPreferences(
        hapticsEnabled = localStorage.getItem(HAPTICS_ENABLED_KEY).toBooleanOrDefault(true),
        reduceMotion = localStorage.getItem(REDUCE_MOTION_KEY).toBooleanOrDefault(false),
        largeTargets = localStorage.getItem(LARGE_TARGETS_KEY).toBooleanOrDefault(false),
    )

    override fun save(preferences: PlayerPreferences) {
        localStorage.setItem(HAPTICS_ENABLED_KEY, preferences.hapticsEnabled.toString())
        localStorage.setItem(REDUCE_MOTION_KEY, preferences.reduceMotion.toString())
        localStorage.setItem(LARGE_TARGETS_KEY, preferences.largeTargets.toString())
    }
}

private fun String?.toBooleanOrDefault(defaultValue: Boolean): Boolean =
    when (this) {
        null -> defaultValue
        "true" -> true
        "false" -> false
        else -> defaultValue
    }
