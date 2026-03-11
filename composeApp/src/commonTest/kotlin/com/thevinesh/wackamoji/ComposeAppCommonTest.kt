package com.thevinesh.wackamoji

import kotlin.test.Test
import kotlin.test.assertEquals

class ComposeAppCommonTest {

    @Test
    fun initialAppScreen_withoutScreenshotScenario_startsAtMenu() {
        assertEquals(AppScreen.Menu, initialAppScreen(screenshotScenario = null))
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

    private class RecordingSoundEffectPlayer : SoundEffectPlayer {
        val playedEffects = mutableListOf<SoundEffect>()

        override fun play(effect: SoundEffect) {
            playedEffects += effect
        }

        override fun dispose() = Unit
    }
}