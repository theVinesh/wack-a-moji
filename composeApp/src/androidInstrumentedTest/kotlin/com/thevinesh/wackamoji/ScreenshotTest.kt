package com.thevinesh.wackamoji

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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

    @Test
    fun captureScreenshots() {
        captureScreenshot(name = "01_GameScreen", scenario = null)
        captureScreenshot(name = "02_Gameplay", scenario = "gameplay")
        captureScreenshot(name = "03_GameOver", scenario = "game-over")
        captureScreenshot(name = "04_Settings", scenario = "settings")
    }

    private fun captureScreenshot(name: String, scenario: String?) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            scenario?.let { putExtra(MainActivity.EXTRA_SCREENSHOT_SCENARIO, it) }
        }

        ActivityScenario.launch<MainActivity>(intent).use {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            Screengrab.screenshot(name)
        }
    }
}
