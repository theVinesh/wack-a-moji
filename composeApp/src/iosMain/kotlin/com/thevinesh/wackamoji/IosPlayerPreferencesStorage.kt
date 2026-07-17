package com.thevinesh.wackamoji

import platform.Foundation.NSUserDefaults

private const val HAPTICS_ENABLED_KEY = "haptics_enabled"
private const val REDUCE_MOTION_KEY = "reduce_motion"
private const val LARGE_TARGETS_KEY = "large_targets"

internal class IosPlayerPreferencesStorage : PlayerPreferencesStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun load(): PlayerPreferences = PlayerPreferences(
        hapticsEnabled = defaults.boolForKeyOrDefault(HAPTICS_ENABLED_KEY, defaultValue = true),
        reduceMotion = defaults.boolForKeyOrDefault(REDUCE_MOTION_KEY, defaultValue = false),
        largeTargets = defaults.boolForKeyOrDefault(LARGE_TARGETS_KEY, defaultValue = false),
    )

    override fun save(preferences: PlayerPreferences) {
        defaults.setBool(preferences.hapticsEnabled, forKey = HAPTICS_ENABLED_KEY)
        defaults.setBool(preferences.reduceMotion, forKey = REDUCE_MOTION_KEY)
        defaults.setBool(preferences.largeTargets, forKey = LARGE_TARGETS_KEY)
    }
}

private fun NSUserDefaults.boolForKeyOrDefault(key: String, defaultValue: Boolean): Boolean {
    if (objectForKey(key) == null) return defaultValue
    return boolForKey(key)
}
