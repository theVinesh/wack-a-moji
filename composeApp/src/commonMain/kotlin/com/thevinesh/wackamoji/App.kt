package com.thevinesh.wackamoji

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Provides the emoji FontFamily throughout the app. On mobile this is null (system handles emojis natively). */
val LocalEmojiFont = compositionLocalOf<FontFamily?> { null }
internal val LocalAudioSettingsStore = compositionLocalOf<AudioSettingsStore> {
    AudioSettingsStore(InMemoryAudioSettingsStorage())
}
val LocalSoundEffectPlayer = compositionLocalOf<SoundEffectPlayer> { NoOpSoundEffectPlayer }

internal enum class AppScreen {
    Menu,
    Settings,
    Gameplay,
}

internal fun initialAppScreen(screenshotScenario: ScreenshotScenario?): AppScreen =
    if (screenshotScenario == null) AppScreen.Menu else AppScreen.Gameplay

internal fun appScreenAfterOpenSettings(): AppScreen = AppScreen.Settings

internal fun appScreenAfterStartGame(): AppScreen = AppScreen.Gameplay

internal fun appScreenAfterBackToMenu(): AppScreen = AppScreen.Menu

private val APP_PREVIEW_AUDIO_SETTINGS = AudioSettings(
    musicVolume = DEFAULT_MUSIC_VOLUME,
    soundEffectVolume = DEFAULT_SOUND_EFFECT_VOLUME,
)

@Composable
fun App() {
    val audioSettingsStore = remember {
        AudioSettingsStore(InMemoryAudioSettingsStorage(APP_PREVIEW_AUDIO_SETTINGS))
    }

    App(
        screenshotScenario = null,
        backgroundMusicController = NoOpBackgroundMusicController,
        soundEffectPlayer = NoOpSoundEffectPlayer,
        audioSettingsStore = audioSettingsStore,
    )
}

@Preview
@Composable
private fun AppPreview() {
    App()
}

@Composable
fun App(
    backgroundMusicController: BackgroundMusicController,
    soundEffectPlayer: SoundEffectPlayer = NoOpSoundEffectPlayer,
) {
    val audioSettingsStore = remember { AudioSettingsStore(InMemoryAudioSettingsStorage()) }

    App(
        screenshotScenario = null,
        backgroundMusicController = backgroundMusicController,
        soundEffectPlayer = soundEffectPlayer,
        audioSettingsStore = audioSettingsStore,
    )
}

@Composable
internal fun App(
    screenshotScenario: ScreenshotScenario?,
    backgroundMusicController: BackgroundMusicController = NoOpBackgroundMusicController,
    soundEffectPlayer: SoundEffectPlayer = NoOpSoundEffectPlayer,
    audioSettingsStore: AudioSettingsStore,
) {
    var appScreen by remember { mutableStateOf(initialAppScreen(screenshotScenario)) }
    val audioSettings = audioSettingsStore.settings

    DisposableEffect(soundEffectPlayer) {
        onDispose { soundEffectPlayer.dispose() }
    }

    SideEffect {
        audioSettings.applyTo(
            backgroundMusicController = backgroundMusicController,
            soundEffectPlayer = soundEffectPlayer,
        )
    }

    CompositionLocalProvider(
        LocalAudioSettingsStore provides audioSettingsStore,
        LocalSoundEffectPlayer provides soundEffectPlayer,
    ) {
        MaterialTheme {
            when (appScreen) {
                AppScreen.Menu -> GameMenuScreen(
                    onStartGame = { appScreen = appScreenAfterStartGame() },
                    onLeaderboard = {},
                    onSettings = { appScreen = appScreenAfterOpenSettings() },
                )

                AppScreen.Settings -> SettingsScreen(
                    onBackToMenu = { appScreen = appScreenAfterBackToMenu() },
                )

                AppScreen.Gameplay -> GameplayAppScreen(
                    screenshotScenario = screenshotScenario,
                    backgroundMusicController = backgroundMusicController,
                    soundEffectPlayer = soundEffectPlayer,
                    onBack = { appScreen = appScreenAfterBackToMenu() },
                )
            }
        }
    }
}

@Composable
private fun GameplayAppScreen(
    screenshotScenario: ScreenshotScenario?,
    backgroundMusicController: BackgroundMusicController,
    soundEffectPlayer: SoundEffectPlayer,
    onBack: () -> Unit,
) {
    val gameplayViewModelStoreOwner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }

    DisposableEffect(gameplayViewModelStoreOwner) {
        onDispose { gameplayViewModelStoreOwner.viewModelStore.clear() }
    }

    val backgroundMusicAutoplayEnabled = !LocalInspectionMode.current && screenshotScenario == null
    val gameViewModel = viewModel(viewModelStoreOwner = gameplayViewModelStoreOwner) {
        GameViewModel(
            screenshotScenario = screenshotScenario,
            backgroundMusicAutoplayEnabled = backgroundMusicAutoplayEnabled,
            soundEffectPlayer = soundEffectPlayer,
        )
    }
    val state by gameViewModel.uiState.collectAsState()
    val backgroundMusicState by gameViewModel.backgroundMusicState.collectAsState()

    BindBackgroundMusic(
        controller = backgroundMusicController,
        desiredState = backgroundMusicState,
        onAppForegrounded = gameViewModel::onAppForegrounded,
        onAppBackgrounded = gameViewModel::onAppBackgrounded,
    )

    SharedSkyScreen(
        overlay = {
            if (state.gameOver) {
                GameOverOverlay(
                    score = state.score,
                    level = state.level,
                    onRestart = { gameViewModel.onRestart() },
                )
            }
        }
    ) {
        GameScreen(
            viewModel = gameViewModel,
            onBack = onBack,
        )
    }
}
