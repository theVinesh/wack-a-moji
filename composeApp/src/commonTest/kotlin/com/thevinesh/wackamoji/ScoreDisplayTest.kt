package com.thevinesh.wackamoji

import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreDisplayTest {

    @Test
    fun formatScore_lessThan1000() {
        assertEquals("0", formatScore(0))
        assertEquals("5", formatScore(5))
        assertEquals("42", formatScore(42))
        assertEquals("999", formatScore(999))
    }

    @Test
    fun formatScore_exactly1000() {
        assertEquals("1,000", formatScore(1000))
    }

    @Test
    fun formatScore_greaterThan1000() {
        assertEquals("1,001", formatScore(1001))
        assertEquals("1,050", formatScore(1050))
        assertEquals("1,100", formatScore(1100))
        assertEquals("1,234", formatScore(1234))
        assertEquals("10,000", formatScore(10000))
        assertEquals("10,005", formatScore(10005))
        assertEquals("10,500", formatScore(10500))
    }
}
