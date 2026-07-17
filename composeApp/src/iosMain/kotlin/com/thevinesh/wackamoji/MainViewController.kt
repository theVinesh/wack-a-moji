package com.thevinesh.wackamoji

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.runtime.remember
import platform.UIKit.UIViewController

fun MainViewController() = MainViewController(screenshotScenarioName = null)

fun MainViewController(screenshotScenarioName: String?): UIViewController {
    val screenshotScenario = screenshotScenarioFromLaunchValue(screenshotScenarioName)
    val backgroundMusicController = IosBackgroundMusicController()
    val soundEffectPlayer = IosSoundEffectPlayer()
    val hapticFeedback = IosHapticFeedback()

    return ComposeUIViewController {
        val audioSettingsStore = remember { AudioSettingsStore(IosAudioSettingsStorage()) }
        val leaderboardStore = remember { LeaderboardStore(IosLeaderboardStorage()) }
        val playerPreferencesStore = remember { PlayerPreferencesStore(IosPlayerPreferencesStorage()) }

        App(
            screenshotScenario = screenshotScenario,
            backgroundMusicController = backgroundMusicController,
            soundEffectPlayer = soundEffectPlayer,
            hapticFeedback = hapticFeedback,
            audioSettingsStore = audioSettingsStore,
            leaderboardStore = leaderboardStore,
            playerPreferencesStore = playerPreferencesStore,
        )
    }
}
