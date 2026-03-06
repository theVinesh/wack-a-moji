package com.thevinesh.wackamoji

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule

@RunWith(AndroidJUnit4::class)
class ScreenshotTest {
    @Rule
    @JvmField
    val localeTestRule = LocaleTestRule()

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun captureScreenshots() {
        // Wait for the app to settle
        composeTestRule.waitForIdle()
        
        // Take a screenshot of the main game screen
        Screengrab.screenshot("01_GameScreen")
        
        // You can add more interactions here:
        // composeTestRule.onNodeWithText("START").performClick()
        // Screengrab.screenshot("02_GameProgress")
    }
}
