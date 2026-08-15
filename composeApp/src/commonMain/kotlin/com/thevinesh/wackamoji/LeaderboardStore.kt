package com.thevinesh.wackamoji

import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val LEADERBOARD_LIMIT = 20

internal val LocalLeaderboardStore = compositionLocalOf<LeaderboardStore> {
    error("No LeaderboardStore provided")
}

internal interface LeaderboardStorage {
    fun load(): List<Int>
    fun save(scores: List<Int>)
}

internal class InMemoryLeaderboardStorage(
    private var storedScores: List<Int> = emptyList(),
) : LeaderboardStorage {
    override fun load(): List<Int> = storedScores

    override fun save(scores: List<Int>) {
        storedScores = scores
    }
}

internal class LeaderboardStore(
    private val storage: LeaderboardStorage,
) {
    private val _scores = MutableStateFlow(storage.load().normalizedLeaderboardScores())
    val scores: StateFlow<List<Int>> = _scores.asStateFlow()

    fun addScore(score: Int) {
        _scores.update { currentScores ->
            val newScores = currentScores.toMutableList()
            newScores.add(score)
            newScores.normalizedLeaderboardScores().also(storage::save)
        }
    }
}

/** How the just-finished run relates to the stored best score (used by the game-over screen). */
internal data class RecordInfo(
    val isNewRecord: Boolean,
    val bestScore: Int,
)

/**
 * Classifies a finished run against the previous best score (null when no score exists yet).
 * First-ever scores count as records; ties are not new records.
 * Returns null when there is nothing meaningful to show (zero-score run with no history).
 */
internal fun recordInfoForRun(score: Int, previousBest: Int?): RecordInfo? =
    when {
        score > (previousBest ?: 0) -> RecordInfo(isNewRecord = true, bestScore = score)
        score <= 0 && previousBest == null -> null
        else -> RecordInfo(isNewRecord = false, bestScore = previousBest ?: 0)
    }

private fun List<Int>.normalizedLeaderboardScores(): List<Int> {
    return sortedDescending().take(LEADERBOARD_LIMIT)
}
