package com.thevinesh.wackamoji

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController() = MainViewController(screenshotScenarioName = null)

fun MainViewController(screenshotScenarioName: String?): UIViewController {
    val screenshotScenario = screenshotScenarioFromLaunchValue(screenshotScenarioName)
    val backgroundMusicController = IosBackgroundMusicController()

    return ComposeUIViewController {
        App(
            screenshotScenario = screenshotScenario,
            backgroundMusicController = backgroundMusicController,
        )
    }
}