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
internal val LocalPlayerPreferencesStore = compositionLocalOf<PlayerPreferencesStore> {
    PlayerPreferencesStore(InMemoryPlayerPreferencesStorage())
}
val LocalHapticFeedback = compositionLocalOf<HapticFeedback> { NoOpHapticFeedback }

internal enum class AppScreen {
    Menu,
    Settings,
    Leaderboard,
    Gameplay,
}

internal fun initialAppScreen(screenshotScenario: ScreenshotScenario?): AppScreen =
    when (screenshotScenario) {
        null -> AppScreen.Menu
        ScreenshotScenario.Settings -> AppScreen.Settings
        ScreenshotScenario.Gameplay, ScreenshotScenario.GameOver -> AppScreen.Gameplay
    }

internal fun appScreenAfterOpenSettings(): AppScreen = AppScreen.Settings

internal fun appScreenAfterOpenLeaderboard(): AppScreen = AppScreen.Leaderboard

internal fun appScreenAfterStartGame(): AppScreen = AppScreen.Gameplay

internal fun appScreenAfterStartClassic(): AppScreen = AppScreen.Gameplay

internal fun appScreenAfterStartEndless(): AppScreen = AppScreen.Gameplay

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
    val leaderboardStore = remember {
        LeaderboardStore(InMemoryLeaderboardStorage())
    }
    val playerPreferencesStore = remember {
        PlayerPreferencesStore(InMemoryPlayerPreferencesStorage())
    }

    App(
        screenshotScenario = null,
        backgroundMusicController = NoOpBackgroundMusicController,
        soundEffectPlayer = NoOpSoundEffectPlayer,
        hapticFeedback = NoOpHapticFeedback,
        audioSettingsStore = audioSettingsStore,
        leaderboardStore = leaderboardStore,
        playerPreferencesStore = playerPreferencesStore,
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
    val leaderboardStore = remember { LeaderboardStore(InMemoryLeaderboardStorage()) }
    val playerPreferencesStore = remember { PlayerPreferencesStore(InMemoryPlayerPreferencesStorage()) }

    App(
        screenshotScenario = null,
        backgroundMusicController = backgroundMusicController,
        soundEffectPlayer = soundEffectPlayer,
        hapticFeedback = NoOpHapticFeedback,
        audioSettingsStore = audioSettingsStore,
        leaderboardStore = leaderboardStore,
        playerPreferencesStore = playerPreferencesStore,
    )
}

@Composable
internal fun App(
    screenshotScenario: ScreenshotScenario?,
    backgroundMusicController: BackgroundMusicController = NoOpBackgroundMusicController,
    soundEffectPlayer: SoundEffectPlayer = NoOpSoundEffectPlayer,
    hapticFeedback: HapticFeedback = NoOpHapticFeedback,
    audioSettingsStore: AudioSettingsStore,
    leaderboardStore: LeaderboardStore,
    playerPreferencesStore: PlayerPreferencesStore,
) {
    var appScreen by remember { mutableStateOf(initialAppScreen(screenshotScenario)) }
    var selectedGameMode by remember { mutableStateOf(GameMode.Classic) }
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
        LocalLeaderboardStore provides leaderboardStore,
        LocalSoundEffectPlayer provides soundEffectPlayer,
        LocalPlayerPreferencesStore provides playerPreferencesStore,
        LocalHapticFeedback provides hapticFeedback,
    ) {
        MaterialTheme {
            when (appScreen) {
                AppScreen.Menu -> GameMenuScreen(
                    onStartClassic = {
                        selectedGameMode = GameMode.Classic
                        appScreen = appScreenAfterStartClassic()
                    },
                    onStartEndless = {
                        selectedGameMode = GameMode.Endless
                        appScreen = appScreenAfterStartEndless()
                    },
                    onLeaderboard = { appScreen = appScreenAfterOpenLeaderboard() },
                    onSettings = { appScreen = appScreenAfterOpenSettings() },
                )

                AppScreen.Leaderboard -> LeaderboardScreen(
                    onBackToMenu = { appScreen = appScreenAfterBackToMenu() },
                )

                AppScreen.Settings -> SettingsScreen(
                    onBackToMenu = { appScreen = appScreenAfterBackToMenu() },
                )

                AppScreen.Gameplay -> GameplayAppScreen(
                    screenshotScenario = screenshotScenario,
                    gameMode = selectedGameMode,
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
    gameMode: GameMode,
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
    val playerPreferencesStore = LocalPlayerPreferencesStore.current
    val hapticFeedback = LocalHapticFeedback.current
    val gameViewModel = viewModel(viewModelStoreOwner = gameplayViewModelStoreOwner) {
        GameViewModel(
            screenshotScenario = screenshotScenario,
            mode = gameMode,
            backgroundMusicAutoplayEnabled = backgroundMusicAutoplayEnabled,
            soundEffectPlayer = soundEffectPlayer,
            hapticFeedback = hapticFeedback,
            hapticsEnabled = { playerPreferencesStore.preferences.hapticsEnabled },
        )
    }
    val state by gameViewModel.uiState.collectAsState()
    val backgroundMusicState by gameViewModel.backgroundMusicState.collectAsState()
    val leaderboardStore = LocalLeaderboardStore.current

    DisposableEffect(state.gameOver) {
        if (state.gameOver && state.score > 0) {
            leaderboardStore.addScore(state.score)
        }
        onDispose { }
    }

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
                    mode = state.mode,
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
