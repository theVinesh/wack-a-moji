package com.thevinesh.wackamoji

import kotlin.js.ExperimentalWasmJsInterop

private const val LEADERBOARD_SCORES_STORAGE_KEY = "wackamoji.leaderboard.scores"

internal class WasmLeaderboardStorage : LeaderboardStorage {
    override fun load(): List<Int> {
        return getLocalStorageItem(LEADERBOARD_SCORES_STORAGE_KEY).decodeLeaderboardScores()
    }

    override fun save(scores: List<Int>) {
        setLocalStorageItem(LEADERBOARD_SCORES_STORAGE_KEY, scores.encodeLeaderboardScores())
    }
}

private fun String?.decodeLeaderboardScores(): List<Int> {
    if (isNullOrBlank()) {
        return emptyList()
    }
    return split(",").mapNotNull(String::toIntOrNull)
}

private fun List<Int>.encodeLeaderboardScores(): String = joinToString(",")

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    (key) => {
      try {
        return typeof window !== 'undefined' && window.localStorage
          ? window.localStorage.getItem(key)
          : null;
      } catch (_) {
        return null;
      }
    }
    """
)
private external fun getLocalStorageItem(key: String): String?

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun(
    """
    (key, value) => {
      try {
        if (typeof window !== 'undefined' && window.localStorage) {
          window.localStorage.setItem(key, value);
        }
      } catch (_) {
      }
    }
    """
)
private external fun setLocalStorageItem(key: String, value: String)

