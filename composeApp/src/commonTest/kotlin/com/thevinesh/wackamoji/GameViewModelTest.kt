package com.thevinesh.wackamoji

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── calculateLevel ──────────────────────────────────────────────────────

    @Test
    fun calculateLevel_returnsLevel1ForScoreZero() {
        assertEquals(1, calculateLevel(0))
    }

    @Test
    fun calculateLevel_returnsLevel2AtThreshold() {
        assertEquals(2, calculateLevel(5))
    }

    @Test
    fun calculateLevel_returnsLevel3AtThreshold() {
        assertEquals(3, calculateLevel(15))
    }

    @Test
    fun calculateLevel_returnsLevel4AtThreshold() {
        assertEquals(4, calculateLevel(30))
    }

    @Test
    fun calculateLevel_returnsLevel5AtThreshold() {
        assertEquals(5, calculateLevel(50))
    }

    @Test
    fun calculateLevel_returnsLevel6AtThreshold() {
        assertEquals(6, calculateLevel(75))
    }

    @Test
    fun calculateLevel_scoresAboveHighestThresholdStayAtMaxLevel() {
        assertEquals(6, calculateLevel(100))
    }

    @Test
    fun calculateLevel_scoreBetweenThresholdsReturnsLowerLevel() {
        assertEquals(2, calculateLevel(10)) // between 5 (L2) and 15 (L3)
    }

    // ─── maxMolesForLevel ────────────────────────────────────────────────────

    @Test
    fun maxMolesForLevel_level1Returns1() {
        assertEquals(1, maxMolesForLevel(1))
    }

    @Test
    fun maxMolesForLevel_level2Returns1() {
        assertEquals(1, maxMolesForLevel(2))
    }

    @Test
    fun maxMolesForLevel_level3Returns2() {
        assertEquals(2, maxMolesForLevel(3))
    }

    @Test
    fun maxMolesForLevel_level4Returns2() {
        assertEquals(2, maxMolesForLevel(4))
    }

    @Test
    fun maxMolesForLevel_level5Returns3() {
        assertEquals(3, maxMolesForLevel(5))
    }

    @Test
    fun maxMolesForLevel_level6Returns3() {
        assertEquals(3, maxMolesForLevel(6))
    }

    // ─── moleUpTimeRange ─────────────────────────────────────────────────────

    @Test
    fun moleUpTimeRange_level1ReturnsBaseRange() {
        assertEquals(MOLE_UP_TIME_MIN_MS to MOLE_UP_TIME_MAX_MS, moleUpTimeRange(1))
    }

    @Test
    fun moleUpTimeRange_level2Returns500to1000() {
        assertEquals(500L to 1000L, moleUpTimeRange(2))
    }

    @Test
    fun moleUpTimeRange_level5ReturnsFastestRange() {
        assertEquals(300L to 600L, moleUpTimeRange(5))
    }

    // ─── GameUiState derived properties ──────────────────────────────────────

    @Test
    fun gameUiState_defaultsAreCorrect() {
        val state = GameUiState()
        assertEquals(0, state.score)
        assertTrue(state.running)
        assertEquals(GAME_DURATION_SECONDS, state.timeLeft)
        assertFalse(state.gameOver)
        assertEquals(9, state.cells.size)
        assertTrue(state.cells.all { !it })
        assertEquals(1, state.level)
        assertEquals(1.0f, state.timerFraction)
    }

    @Test
    fun gameUiState_levelDerivedFromScore() {
        val state = GameUiState(score = 16)
        assertEquals(3, state.level)
    }

    @Test
    fun gameUiState_timerFractionIsCorrect() {
        val state = GameUiState(timeLeft = 15)
        assertEquals(0.5f, state.timerFraction)
    }

    @Test
    fun gameUiState_timerFractionIsZeroAtEnd() {
        val state = GameUiState(timeLeft = 0)
        assertEquals(0.0f, state.timerFraction)
    }

    // ─── GameViewModel initial state ─────────────────────────────────────────

    @Test
    fun viewModel_initialStateIsDefault() {
        val vm = GameViewModel()
        val state = vm.uiState.value
        assertEquals(0, state.score)
        assertTrue(state.running)
        assertEquals(GAME_DURATION_SECONDS, state.timeLeft)
        assertFalse(state.gameOver)
    }

    // ─── GameViewModel.onPauseResume ─────────────────────────────────────────

    @Test
    fun onPauseResume_togglesRunning() {
        val vm = GameViewModel()
        assertTrue(vm.uiState.value.running)

        vm.onPauseResume()
        assertFalse(vm.uiState.value.running)

        vm.onPauseResume()
        assertTrue(vm.uiState.value.running)
    }

    // ─── GameViewModel.onRestart ─────────────────────────────────────────────

    @Test
    fun onRestart_afterPause_resetsState() {
        val vm = GameViewModel()
        vm.onPauseResume()
        assertFalse(vm.uiState.value.running)

        vm.onRestart()
        val state = vm.uiState.value
        assertEquals(0, state.score)
        assertEquals(GAME_DURATION_SECONDS, state.timeLeft)
        assertFalse(state.gameOver)
        assertTrue(state.running)
    }

    // ─── GameViewModel.onMoleHit ─────────────────────────────────────────────

    @Test
    fun onMoleHit_doesNothingWhenNoCellIsUp() {
        val vm = GameViewModel()
        // At initial state, all cells are down
        vm.onMoleHit(0)
        assertEquals(0, vm.uiState.value.score)
    }

    @Test
    fun onMoleHit_doesNotScoreWhenGameIsPaused() {
        // Create a paused game state with a mole up at index 0
        val pausedState = GameUiState(
            running = false,
            cells = listOf(true, false, false, false, false, false, false, false, false)
        )
        
        // Use the internal test constructor to inject the paused state
        val vm = GameViewModel(initialState = pausedState, startGame = false)
        
        // Try to hit the mole while game is paused
        val initialScore = pausedState.score
        vm.onMoleHit(0)
        
        // Score should not change when game is paused
        assertEquals(initialScore, vm.uiState.value.score)
        // The mole should still be up (since scoring didn't happen)
        assertTrue(vm.uiState.value.cells[0])
    }

    @Test
    fun onMoleHit_scoresWhenGameIsRunning() {
        // Create a running game state with a mole up at index 0
        val runningState = GameUiState(
            running = true,
            cells = listOf(true, false, false, false, false, false, false, false, false)
        )
        
        // Use the internal test constructor to inject the running state
        val vm = GameViewModel(initialState = runningState, startGame = false)
        
        // Hit the mole while game is running
        val initialScore = runningState.score
        vm.onMoleHit(0)
        
        // Score should increase when game is running
        assertEquals(initialScore + 1, vm.uiState.value.score)
        // The mole should be down after being hit
        assertFalse(vm.uiState.value.cells[0])
    }
}
