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
}