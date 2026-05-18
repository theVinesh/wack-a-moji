package com.thevinesh.wackamoji

import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val LocalLeaderboardStore = compositionLocalOf<LeaderboardStore> {
    error("No LeaderboardStore provided")
}

class LeaderboardStore {
    private val _scores = MutableStateFlow<List<Int>>(emptyList())
    val scores: StateFlow<List<Int>> = _scores.asStateFlow()

    fun addScore(score: Int) {
        _scores.update { currentScores ->
            val newScores = currentScores.toMutableList()
            newScores.add(score)
            newScores.sortedDescending()
        }
    }
}
