package com.thevinesh.wackamoji

import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeAppCommonTest {

    @Test
    fun initialAppScreen_withoutScreenshotScenario_startsAtMenu() {
        assertEquals(AppScreen.Menu, initialAppScreen(screenshotScenario = null))
    }

    @Test
    fun initialAppScreen_settingsScreenshotScenario_startsAtSettings() {
        assertEquals(AppScreen.Settings, initialAppScreen(screenshotScenario = ScreenshotScenario.Settings))
    }

    @Test
    fun initialAppScreen_gameplayScreenshotScenario_startsAtGameplay() {
        assertEquals(AppScreen.Gameplay, initialAppScreen(screenshotScenario = ScreenshotScenario.Gameplay))
    }

    @Test
    fun appScreenAfterOpenSettings_transitionsToSettings() {
        assertEquals(AppScreen.Settings, appScreenAfterOpenSettings())
    }

    @Test
    fun appScreenAfterStartGame_transitionsToGameplay() {
        assertEquals(AppScreen.Gameplay, appScreenAfterStartGame())
    }

    @Test
    fun appScreenAfterBackToMenu_returnsToMenu() {
        assertEquals(AppScreen.Menu, appScreenAfterBackToMenu())
    }

    @Test
    fun pauseResumeButtonText_showsPauseWhileRunning() {
        assertEquals("Pause", pauseResumeButtonText(running = true))
    }

    @Test
    fun pauseResumeButtonText_showsResumeWhilePaused() {
        assertEquals("Resume", pauseResumeButtonText(running = false))
    }

    @Test
    fun buttonClickHandler_playsClickAndRunsActionOnce() {
        val player = RecordingSoundEffectPlayer()
        var actionCount = 0

        val handler = buttonClickHandler(player) { actionCount++ }

        handler()

        assertEquals(listOf(SoundEffect.Click), player.playedEffects)
        assertEquals(1, actionCount)
    }

    @Test
    fun buttonClickHandler_replaysClickForEachTap() {
        val player = RecordingSoundEffectPlayer()
        var actionCount = 0

        val handler = buttonClickHandler(player) { actionCount++ }

        handler()
        handler()

        assertEquals(listOf(SoundEffect.Click, SoundEffect.Click), player.playedEffects)
        assertEquals(2, actionCount)
    }

    @Test
    fun buttonClickHandler_playsClickBeforeBackNavigationAction() {
        val events = mutableListOf<String>()
        val player = object : SoundEffectPlayer {
            override fun play(effect: SoundEffect) {
                events += effect.name
            }

            override fun setVolume(volume: Float) = Unit

            override fun dispose() = Unit
        }

        val handler = buttonClickHandler(player) {
            events += "NavigateBack"
        }

        handler()

        assertEquals(listOf(SoundEffect.Click.name, "NavigateBack"), events)
    }

    private class RecordingSoundEffectPlayer : SoundEffectPlayer {
        val playedEffects = mutableListOf<SoundEffect>()

        override fun play(effect: SoundEffect) {
            playedEffects += effect
        }

        override fun setVolume(volume: Float) = Unit

        override fun dispose() = Unit
    }
}