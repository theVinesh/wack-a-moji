package com.thevinesh.wackamoji

import kotlin.test.Test
import kotlin.test.assertEquals

class LeaderboardStoreTest {

    @Test
    fun leaderboardStore_loadsSortedAndTrimmedTop20Scores() {
        val storage = RecordingLeaderboardStorage(
            storedScores = listOf(5, 42, 7, 100, 1) + (200 downTo 170).toList(),
        )

        val store = LeaderboardStore(storage)

        assertEquals((200 downTo 181).toList(), store.scores.value)
    }

    @Test
    fun leaderboardStore_addScore_savesUpdatedTop20Only() {
        val storage = RecordingLeaderboardStorage(
            storedScores = (20 downTo 1).toList(),
        )
        val store = LeaderboardStore(storage)

        store.addScore(25)

        assertEquals(listOf(25) + (20 downTo 2).toList(), store.scores.value)
        assertEquals(store.scores.value, storage.savedScores.single())
        assertEquals(20, store.scores.value.size)
    }

    private class RecordingLeaderboardStorage(
        private var storedScores: List<Int> = emptyList(),
    ) : LeaderboardStorage {
        val savedScores = mutableListOf<List<Int>>()

        override fun load(): List<Int> = storedScores

        override fun save(scores: List<Int>) {
            storedScores = scores
            savedScores += scores
        }
    }
}

