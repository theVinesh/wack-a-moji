package com.thevinesh.wackamoji

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
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

    // ─── randomMoleEmoji ─────────────────────────────────────────────────────

    @Test
    fun randomMoleEmoji_returnsEmojiFromDefinedPool() {
        val pool = listOf("😡", "😂", "🙄", "😅", "🤪", "😤", "🥴", "😎")
        for (i in 0..100) {
            val emoji = randomMoleEmoji()
            assertTrue(pool.contains(emoji), "Emoji $emoji is not in the allowed pool")
        }
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
        assertTrue(state.cells.all { !it.isUp })
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

    @Test
    fun screenshotScenarioFromLaunchValue_returnsMatchingScenario() {
        assertEquals(ScreenshotScenario.Gameplay, screenshotScenarioFromLaunchValue("gameplay"))
        assertEquals(ScreenshotScenario.GameOver, screenshotScenarioFromLaunchValue("game-over"))
        assertEquals(ScreenshotScenario.Settings, screenshotScenarioFromLaunchValue("settings"))
        assertEquals(null, screenshotScenarioFromLaunchValue("unknown"))
    }

    @Test
    fun screenshotStateForScenario_gameplayMatchesExpectedShot() {
        val state = screenshotStateForScenario(ScreenshotScenario.Gameplay)

        assertEquals(12, state.score)
        assertEquals(9, state.timeLeft)
        assertTrue(state.running)
        assertFalse(state.gameOver)
        assertEquals(2, state.level)
        assertEquals(1, state.cells.count { it.isUp })
    }

    @Test
    fun screenshotStateForScenario_gameOverMatchesExpectedShot() {
        val state = screenshotStateForScenario(ScreenshotScenario.GameOver)

        assertEquals(32, state.score)
        assertEquals(0, state.timeLeft)
        assertFalse(state.running)
        assertTrue(state.gameOver)
        assertEquals(4, state.level)
        assertTrue(state.cells.all { !it.isUp })
    }

    @Test
    fun screenshotStateForScenario_settingsStartsFromIdleBoard() {
        val state = screenshotStateForScenario(ScreenshotScenario.Settings)

        assertEquals(0, state.score)
        assertEquals(GAME_DURATION_SECONDS, state.timeLeft)
        assertFalse(state.running)
        assertFalse(state.gameOver)
        assertTrue(state.cells.all { !it.isUp })
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
        assertEquals(BackgroundMusicPlayback.Playing, vm.backgroundMusicState.value.playback)
        assertEquals(BackgroundMusicTrack.GameplayLoop, vm.backgroundMusicState.value.track)
        assertTrue(vm.backgroundMusicState.value.loop)
    }

    @Test
    fun viewModel_screenshotScenarioStartsInDeterministicState() {
        val vm = GameViewModel(ScreenshotScenario.GameOver)

        assertEquals(screenshotStateForScenario(ScreenshotScenario.GameOver), vm.uiState.value)
        assertEquals(BackgroundMusicPlayback.Stopped, vm.backgroundMusicState.value.playback)
    }

    @Test
    fun viewModel_backgroundMusicCanBeDisabledForPreviewLikeScenarios() {
        val vm = GameViewModel(backgroundMusicAutoplayEnabled = false)

        assertEquals(BackgroundMusicPlayback.Stopped, vm.backgroundMusicState.value.playback)

        vm.onAppForegrounded()

        assertEquals(BackgroundMusicPlayback.Stopped, vm.backgroundMusicState.value.playback)
    }

    // ─── Game modes ──────────────────────────────────────────────────────────

    @Test
    fun gameOverTitle_matchesMode() {
        assertEquals("TIME'S UP!", gameOverTitle(GameMode.Classic))
        assertEquals("OUT OF LIVES!", gameOverTitle(GameMode.Endless))
    }

    @Test
    fun freshGameUiState_endlessStartsWithFullLives() {
        val state = freshGameUiState(GameMode.Endless)
        assertEquals(GameMode.Endless, state.mode)
        assertEquals(ENDLESS_START_LIVES, state.lives)
        assertTrue(state.running)
        assertFalse(state.gameOver)
    }

    @Test
    fun applyMoleTimeouts_reducesLivesWithoutEnding() {
        val state = GameUiState(mode = GameMode.Endless, lives = 3, running = true, combo = 4)
        val cells = List(9) { CellState() }

        val next = applyMoleTimeouts(state, missedCount = 1, cells = cells)

        assertEquals(2, next.lives)
        assertEquals(0, next.combo)
        assertFalse(next.gameOver)
        assertTrue(next.running)
    }

    @Test
    fun applyMoleTimeouts_endsGameWhenLivesReachZero() {
        val state = GameUiState(
            mode = GameMode.Endless,
            lives = 1,
            running = true,
            cells = List(9) { if (it == 0) CellState(isUp = true, contentType = CellContentType.Emoji, content = "😎") else CellState() },
        )
        val cells = List(9) { CellState() }

        val next = applyMoleTimeouts(state, missedCount = 1, cells = cells)

        assertEquals(0, next.lives)
        assertTrue(next.gameOver)
        assertFalse(next.running)
        assertTrue(next.cells.all { !it.isUp })
    }

    @Test
    fun applyMoleTimeouts_resetsComboInClassicModeWithoutLosingLives() {
        val state = GameUiState(mode = GameMode.Classic, lives = 3, running = true, combo = 6)
        val cells = List(9) { if (it == 1) CellState(isUp = true, contentType = CellContentType.Emoji, content = "😂") else CellState() }

        val next = applyMoleTimeouts(state, missedCount = 1, cells = cells)

        assertEquals(3, next.lives)
        assertEquals(0, next.combo)
    }

    @Test
    fun comboBonusPoints_rampsWithStreak() {
        assertEquals(0, comboBonusPoints(1))
        assertEquals(0, comboBonusPoints(2))
        assertEquals(1, comboBonusPoints(3))
        assertEquals(2, comboBonusPoints(5))
        assertEquals(3, comboBonusPoints(10))
    }

    @Test
    fun pointsForHit_includesComboBonus() {
        assertEquals(1, pointsForHit(currentCombo = 0))
        assertEquals(2, pointsForHit(currentCombo = 2)) // next streak 3
        assertEquals(3, pointsForHit(currentCombo = 4)) // next streak 5
        assertEquals(4, pointsForHit(currentCombo = 9)) // next streak 10
    }

    @Test
    fun onMoleHit_incrementsComboAndBonusScore() {
        val cells = List(9) { if (it == 2) CellState(isUp = true, contentType = CellContentType.Emoji, content = "😎") else CellState() }
        val vm = GameViewModel.createForTest(
            initialState = GameUiState(
                score = 10,
                combo = 2,
                running = true,
                cells = cells,
            ),
        )

        vm.onMoleHit(2)

        assertEquals(3, vm.uiState.value.combo)
        assertEquals(12, vm.uiState.value.score) // +2 for streak 3
        assertFalse(vm.uiState.value.cells[2].isUp)
    }

    @Test
    fun onRestart_preservesEndlessMode() {
        val vm = GameViewModel(mode = GameMode.Endless)
        assertEquals(GameMode.Endless, vm.uiState.value.mode)

        vm.onPauseResume()
        vm.onRestart()

        assertEquals(GameMode.Endless, vm.uiState.value.mode)
        assertEquals(ENDLESS_START_LIVES, vm.uiState.value.lives)
        assertTrue(vm.uiState.value.running)
        assertFalse(vm.uiState.value.gameOver)
    }

    @Test
    fun endlessMode_doesNotTickClassicTimer() = runTest(testDispatcher.scheduler) {
        val vm = GameViewModel(mode = GameMode.Endless)
        vm.onPauseResume() // pause so spawn misses cannot end the run

        advanceTimeBy(GAME_DURATION_SECONDS * 1000L)
        runCurrent()

        assertEquals(GAME_DURATION_SECONDS, vm.uiState.value.timeLeft)
        assertFalse(vm.uiState.value.gameOver)
        assertEquals(GameMode.Endless, vm.uiState.value.mode)
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
        assertEquals(BackgroundMusicPlayback.Playing, vm.backgroundMusicState.value.playback)
    }

    @Test
    fun appBackgrounding_pausesAndResumesBackgroundMusicWithoutChangingGamePauseState() {
        val vm = GameViewModel()

        vm.onAppBackgrounded()
        assertEquals(BackgroundMusicPlayback.Paused, vm.backgroundMusicState.value.playback)
        assertTrue(vm.uiState.value.running)

        vm.onAppForegrounded()
        assertEquals(BackgroundMusicPlayback.Playing, vm.backgroundMusicState.value.playback)
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
        assertEquals(BackgroundMusicPlayback.Playing, vm.backgroundMusicState.value.playback)
    }

    // ─── GameViewModel.onMoleHit ─────────────────────────────────────────────

    @Test
    fun onMoleHit_doesNothingWhenNoCellIsUp() {
        val player = RecordingSoundEffectPlayer()
        val vm = GameViewModel(soundEffectPlayer = player)
        // At initial state, all cells are down
        vm.onMoleHit(0)
        assertEquals(0, vm.uiState.value.score)
        assertTrue(player.playedEffects.isEmpty())
    }

    @Test
    fun onMoleHit_visibleMoleIncrementsScore_andPlaysWackOnce() {
        val player = RecordingSoundEffectPlayer()
        val haptics = RecordingHapticFeedback()
        val vm = GameViewModel(
            screenshotScenario = ScreenshotScenario.Gameplay,
            soundEffectPlayer = player,
            hapticFeedback = haptics,
        )
        val initialBackgroundMusicPlayback = vm.backgroundMusicState.value.playback

        vm.onMoleHit(2)

        assertEquals(13, vm.uiState.value.score)
        assertFalse(vm.uiState.value.cells[2].isUp)
        assertEquals(2, vm.uiState.value.lastHitCellIndex)
        assertEquals(listOf(SoundEffect.Wack), player.playedEffects)
        assertEquals(1, haptics.impactCount)
        assertEquals(initialBackgroundMusicPlayback, vm.backgroundMusicState.value.playback)
    }

    @Test
    fun onMoleHit_skipsHapticsWhenDisabled() {
        val haptics = RecordingHapticFeedback()
        val vm = GameViewModel(
            screenshotScenario = ScreenshotScenario.Gameplay,
            hapticFeedback = haptics,
            hapticsEnabled = { false },
        )

        vm.onMoleHit(2)

        assertEquals(13, vm.uiState.value.score)
        assertEquals(0, haptics.impactCount)
    }

    @Test
    fun onMoleHit_hiddenCellDoesNotPlayWack() {
        val player = RecordingSoundEffectPlayer()
        val vm = GameViewModel(
            screenshotScenario = ScreenshotScenario.Gameplay,
            soundEffectPlayer = player,
        )

        vm.onMoleHit(0)

        assertEquals(screenshotStateForScenario(ScreenshotScenario.Gameplay), vm.uiState.value)
        assertTrue(player.playedEffects.isEmpty())
    }

    @Test
    fun onMoleHit_afterGameOverDoesNotPlayWack() {
        val player = RecordingSoundEffectPlayer()
        val vm = GameViewModel(
            screenshotScenario = ScreenshotScenario.GameOver,
            soundEffectPlayer = player,
        )

        vm.onMoleHit(0)

        assertEquals(32, vm.uiState.value.score)
        assertTrue(vm.uiState.value.gameOver)
        assertTrue(player.playedEffects.isEmpty())
    }

    @Test
    fun onMoleHit_whilePausedDoesNotScoreOrPlayWack() {
        val player = RecordingSoundEffectPlayer()
        val cells = List(9) { if (it == 2) CellState(isUp = true, contentType = CellContentType.Emoji, content = "😎") else CellState() }
        val vm = GameViewModel.createForTest(
            initialState = GameUiState(
                score = 4,
                running = false,
                cells = cells,
            ),
            soundEffectPlayer = player,
        )

        vm.onMoleHit(2)

        assertEquals(4, vm.uiState.value.score)
        assertTrue(vm.uiState.value.cells[2].isUp)
        assertTrue(player.playedEffects.isEmpty())
    }

    @Test
    fun gameOver_doesNotInterruptBackgroundMusic() = runTest(testDispatcher.scheduler) {
        val vm = GameViewModel()

        advanceTimeBy(GAME_DURATION_SECONDS * 1000L)
        runCurrent()

        assertTrue(vm.uiState.value.gameOver)
        assertEquals(BackgroundMusicPlayback.Playing, vm.backgroundMusicState.value.playback)
    }

    @Test
    fun resolveBackgroundMusicAction_mapsPlaybackTransitions() {
        val playingState = BackgroundMusicState(playback = BackgroundMusicPlayback.Playing)

        assertEquals(
            BackgroundMusicAction.Start(BackgroundMusicTrack.GameplayLoop, loop = true),
            resolveBackgroundMusicAction(previousState = null, desiredState = playingState),
        )
        assertEquals(
            BackgroundMusicAction.Pause,
            resolveBackgroundMusicAction(
                previousState = playingState,
                desiredState = playingState.copy(playback = BackgroundMusicPlayback.Paused),
            ),
        )
        assertEquals(
            BackgroundMusicAction.Resume,
            resolveBackgroundMusicAction(
                previousState = playingState.copy(playback = BackgroundMusicPlayback.Paused),
                desiredState = playingState,
            ),
        )
        assertEquals(
            BackgroundMusicAction.Stop,
            resolveBackgroundMusicAction(
                previousState = playingState,
                desiredState = playingState.copy(playback = BackgroundMusicPlayback.Stopped),
            ),
        )
    }

    private class RecordingSoundEffectPlayer : SoundEffectPlayer {
        val playedEffects = mutableListOf<SoundEffect>()

        override fun play(effect: SoundEffect) {
            playedEffects += effect
        }

        override fun setVolume(volume: Float) = Unit

        override fun dispose() = Unit
    }

    private class RecordingHapticFeedback : HapticFeedback {
        var impactCount = 0
            private set

        override fun performLightImpact() {
            impactCount++
        }
    }
}
