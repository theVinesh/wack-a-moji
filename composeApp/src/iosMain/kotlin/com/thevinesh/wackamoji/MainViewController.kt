package com.thevinesh.wackamoji

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = MainViewController(screenshotScenarioName = null)

fun MainViewController(screenshotScenarioName: String?) = ComposeUIViewController {
    App(screenshotScenario = screenshotScenarioFromLaunchValue(screenshotScenarioName))
}