package com.thevinesh.wackamoji

import android.content.Context
import androidx.core.content.edit

private const val LEADERBOARD_PREFERENCES_NAME = "leaderboard"
private const val LEADERBOARD_SCORES_PREFERENCE_KEY = "scores"

internal class AndroidLeaderboardStorage(
    context: Context,
) : LeaderboardStorage {
    private val sharedPreferences = context.applicationContext.getSharedPreferences(
        LEADERBOARD_PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override fun load(): List<Int> {
        return sharedPreferences.getString(LEADERBOARD_SCORES_PREFERENCE_KEY, null).decodeLeaderboardScores()
    }

    override fun save(scores: List<Int>) {
        sharedPreferences.edit {
            putString(LEADERBOARD_SCORES_PREFERENCE_KEY, scores.encodeLeaderboardScores())
        }
    }
}

private fun String?.decodeLeaderboardScores(): List<Int> {
    if (isNullOrBlank()) {
        return emptyList()
    }
    return split(",").mapNotNull(String::toIntOrNull)
}

private fun List<Int>.encodeLeaderboardScores(): String = joinToString(",")

