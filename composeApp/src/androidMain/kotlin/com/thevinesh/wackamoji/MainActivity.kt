package com.thevinesh.wackamoji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    companion object {
        const val EXTRA_SCREENSHOT_SCENARIO = "screenshot-scenario"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val screenshotScenario = screenshotScenarioFromLaunchValue(
            intent?.getStringExtra(EXTRA_SCREENSHOT_SCENARIO),
        )
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = Unit
            },
        )

        setContent {
            val audioSettingsStore = remember(applicationContext) {
                AudioSettingsStore(AndroidAudioSettingsStorage(applicationContext))
            }
            val backgroundMusicController = remember(applicationContext) {
                AndroidBackgroundMusicController(applicationContext)
            }
            val soundEffectPlayer = remember(applicationContext) {
                AndroidSoundEffectPlayer(applicationContext)
            }
            val leaderboardStore = remember { LeaderboardStore() }

            App(
                screenshotScenario = screenshotScenario,
                backgroundMusicController = backgroundMusicController,
                soundEffectPlayer = soundEffectPlayer,
                audioSettingsStore = audioSettingsStore,
                leaderboardStore = leaderboardStore,
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}