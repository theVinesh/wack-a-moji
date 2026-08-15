package com.thevinesh.wackamoji

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecordInfoTest {

    @Test
    fun recordInfoForRun_firstEverScore_isNewRecord() {
        assertEquals(
            RecordInfo(isNewRecord = true, bestScore = 25),
            recordInfoForRun(score = 25, previousBest = null),
        )
    }

    @Test
    fun recordInfoForRun_beatsPreviousBest_isNewRecord() {
        assertEquals(
            RecordInfo(isNewRecord = true, bestScore = 41),
            recordInfoForRun(score = 41, previousBest = 40),
        )
    }

    @Test
    fun recordInfoForRun_belowPreviousBest_isCloseCallWithOldBest() {
        assertEquals(
            RecordInfo(isNewRecord = false, bestScore = 44),
            recordInfoForRun(score = 40, previousBest = 44),
        )
    }

    @Test
    fun recordInfoForRun_tie_isNotNewRecord() {
        assertEquals(
            RecordInfo(isNewRecord = false, bestScore = 40),
            recordInfoForRun(score = 40, previousBest = 40),
        )
    }

    @Test
    fun recordInfoForRun_zeroScoreNoHistory_showsNothing() {
        assertNull(recordInfoForRun(score = 0, previousBest = null))
    }

    @Test
    fun recordInfoForRun_zeroScoreWithHistory_showsGapToBest() {
        assertEquals(
            RecordInfo(isNewRecord = false, bestScore = 18),
            recordInfoForRun(score = 0, previousBest = 18),
        )
    }

    @Test
    fun recordGapText_singularPoint() {
        assertEquals(
            "So close! You were 1 point short of beating your own record",
            recordGapText(score = 24, bestScore = 25),
        )
    }

    @Test
    fun recordGapText_pluralPoints() {
        assertEquals(
            "So close! You were 4 points short of beating your own record",
            recordGapText(score = 21, bestScore = 25),
        )
    }

    @Test
    fun recordLineText_newRecord_showsTrophy() {
        assertEquals(
            "🏆 NEW RECORD!",
            recordLineText(score = 25, recordInfo = RecordInfo(isNewRecord = true, bestScore = 25)),
        )
    }

    @Test
    fun recordLineText_tie_showsMatchedCopy() {
        assertEquals(
            "You matched your record!",
            recordLineText(score = 40, recordInfo = RecordInfo(isNewRecord = false, bestScore = 40)),
        )
    }

    @Test
    fun recordLineText_closeCall_showsGapCopy() {
        assertEquals(
            "So close! You were 4 points short of beating your own record",
            recordLineText(score = 21, recordInfo = RecordInfo(isNewRecord = false, bestScore = 25)),
        )
    }

    @Test
    fun recordGapText_neverNegative() {
        assertEquals(
            "So close! You were 0 points short of beating your own record",
            recordGapText(score = 30, bestScore = 25),
        )
    }
}
