package com.thevinesh.wackamoji

import platform.Foundation.NSUserDefaults

private const val LEADERBOARD_SCORES_PREFERENCE_KEY = "leaderboard_scores"

internal class IosLeaderboardStorage(
    private val userDefaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : LeaderboardStorage {
    override fun load(): List<Int> {
        return userDefaults.stringForKey(LEADERBOARD_SCORES_PREFERENCE_KEY).decodeLeaderboardScores()
    }

    override fun save(scores: List<Int>) {
        userDefaults.setObject(scores.encodeLeaderboardScores(), forKey = LEADERBOARD_SCORES_PREFERENCE_KEY)
    }
}

private fun String?.decodeLeaderboardScores(): List<Int> {
    if (isNullOrBlank()) {
        return emptyList()
    }
    return split(",").mapNotNull(String::toIntOrNull)
}

private fun List<Int>.encodeLeaderboardScores(): String = joinToString(",")

