package com.thevinesh.wackamoji

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import org.jetbrains.compose.ui.tooling.preview.Preview

internal data class SettingsScreenBindings(
    val musicVolume: Float,
    val soundEffectVolume: Float,
    val hapticsEnabled: Boolean,
    val reduceMotion: Boolean,
    val largeTargets: Boolean,
    val onMusicVolumeChange: (Float) -> Unit,
    val onSoundEffectVolumeChange: (Float) -> Unit,
    val onHapticsEnabledChange: (Boolean) -> Unit,
    val onReduceMotionChange: (Boolean) -> Unit,
    val onLargeTargetsChange: (Boolean) -> Unit,
)

internal fun settingsScreenBindings(
    audioSettingsStore: AudioSettingsStore,
    playerPreferencesStore: PlayerPreferencesStore,
): SettingsScreenBindings {
    val settings = audioSettingsStore.settings
    val preferences = playerPreferencesStore.preferences

    return SettingsScreenBindings(
        musicVolume = settings.musicVolume,
        soundEffectVolume = settings.soundEffectVolume,
        hapticsEnabled = preferences.hapticsEnabled,
        reduceMotion = preferences.reduceMotion,
        largeTargets = preferences.largeTargets,
        onMusicVolumeChange = audioSettingsStore::updateMusicVolume,
        onSoundEffectVolumeChange = audioSettingsStore::updateSoundEffectVolume,
        onHapticsEnabledChange = playerPreferencesStore::updateHapticsEnabled,
        onReduceMotionChange = playerPreferencesStore::updateReduceMotion,
        onLargeTargetsChange = playerPreferencesStore::updateLargeTargets,
    )
}

internal fun formatVolumePercentage(volume: Float): String =
    "${(volume.normalizedAudioVolume() * 100).roundToInt()}%"

private val SETTINGS_SCREEN_PREVIEW_AUDIO_SETTINGS = AudioSettings(
    musicVolume = 0.4f,
    soundEffectVolume = 0.7f,
)

@Composable
internal fun SettingsScreen(
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier,
    animateClouds: Boolean = true,
) {
    val bindings = settingsScreenBindings(
        audioSettingsStore = LocalAudioSettingsStore.current,
        playerPreferencesStore = LocalPlayerPreferencesStore.current,
    )

    SharedSkyScreen(
        modifier = modifier,
        animateClouds = animateClouds,
    ) {
        SettingsScreenContent(
            musicVolume = bindings.musicVolume,
            soundEffectVolume = bindings.soundEffectVolume,
            hapticsEnabled = bindings.hapticsEnabled,
            reduceMotion = bindings.reduceMotion,
            largeTargets = bindings.largeTargets,
            onMusicVolumeChange = bindings.onMusicVolumeChange,
            onSoundEffectVolumeChange = bindings.onSoundEffectVolumeChange,
            onHapticsEnabledChange = bindings.onHapticsEnabledChange,
            onReduceMotionChange = bindings.onReduceMotionChange,
            onLargeTargetsChange = bindings.onLargeTargetsChange,
            onBackToMenu = onBackToMenu,
        )
    }
}

@Composable
internal fun SettingsScreenContent(
    musicVolume: Float,
    soundEffectVolume: Float,
    hapticsEnabled: Boolean,
    reduceMotion: Boolean,
    largeTargets: Boolean,
    onMusicVolumeChange: (Float) -> Unit,
    onSoundEffectVolumeChange: (Float) -> Unit,
    onHapticsEnabledChange: (Boolean) -> Unit,
    onReduceMotionChange: (Boolean) -> Unit,
    onLargeTargetsChange: (Boolean) -> Unit,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .safeContentPadding()
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "SETTINGS",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = WackAMojiColors.Primary,
            letterSpacing = (-1).sp,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Tune audio, feedback, and accessibility",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = WackAMojiColors.SkyDark,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VolumeSettingCard(
                title = "Music",
                description = "Background loop volume",
                volume = musicVolume,
                accentColor = WackAMojiColors.Primary,
                onVolumeChange = onMusicVolumeChange,
            )
            VolumeSettingCard(
                title = "Sound Effects",
                description = "Button taps and mole whacks",
                volume = soundEffectVolume,
                accentColor = WackAMojiColors.LeaderboardPurple,
                onVolumeChange = onSoundEffectVolumeChange,
            )
            ToggleSettingCard(
                title = "Haptics",
                description = "Vibrate on each mole whack",
                checked = hapticsEnabled,
                accentColor = WackAMojiColors.PauseGreen,
                onCheckedChange = onHapticsEnabledChange,
            )
            ToggleSettingCard(
                title = "Reduce Motion",
                description = "Still clouds and instant mole pops",
                checked = reduceMotion,
                accentColor = WackAMojiColors.SkyMedium,
                onCheckedChange = onReduceMotionChange,
            )
            ToggleSettingCard(
                title = "Larger Targets",
                description = "Bigger buttons and mole holes",
                checked = largeTargets,
                accentColor = WackAMojiColors.RestartOrange,
                onCheckedChange = onLargeTargetsChange,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        GameButton(
            text = "Back to Menu",
            backgroundColor = WackAMojiColors.RestartOrange,
            shadowColor = WackAMojiColors.RestartShadow,
            onClick = onBackToMenu,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun VolumeSettingCard(
    title: String,
    description: String,
    volume: Float,
    accentColor: Color,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(24.dp),
            )
            .border(
                width = 2.dp,
                color = WackAMojiColors.ButtonHighlight,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = WackAMojiColors.SkyDark,
            )
            Text(
                text = formatVolumePercentage(volume),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = accentColor,
            )
        }

        Text(
            text = description,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = WackAMojiColors.SkyMedium,
        )

        Slider(
            value = volume.normalizedAudioVolume(),
            onValueChange = onVolumeChange,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = accentColor.copy(alpha = 0.25f),
            ),
        )
    }
}

@Composable
internal fun ToggleSettingCard(
    title: String,
    description: String,
    checked: Boolean,
    accentColor: Color,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(24.dp),
            )
            .border(
                width = 2.dp,
                color = WackAMojiColors.ButtonHighlight,
                shape = RoundedCornerShape(24.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) },
            )
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = WackAMojiColors.SkyDark,
            )
            Text(
                text = description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = WackAMojiColors.SkyMedium,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = accentColor.copy(alpha = 0.35f),
            ),
        )
    }
}

@Preview
@Composable
private fun VolumeSettingCardPreview() {
    MaterialTheme {
        Surface {
            VolumeSettingCard(
                title = "Music",
                description = "Background loop volume",
                volume = 0.6f,
                accentColor = WackAMojiColors.Primary,
                onVolumeChange = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenContentPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SettingsScreenContent(
                musicVolume = 0.35f,
                soundEffectVolume = 0.8f,
                hapticsEnabled = true,
                reduceMotion = false,
                largeTargets = true,
                onMusicVolumeChange = {},
                onSoundEffectVolumeChange = {},
                onHapticsEnabledChange = {},
                onReduceMotionChange = {},
                onLargeTargetsChange = {},
                onBackToMenu = {},
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    val audioSettingsStore = remember {
        AudioSettingsStore(InMemoryAudioSettingsStorage(SETTINGS_SCREEN_PREVIEW_AUDIO_SETTINGS))
    }
    val playerPreferencesStore = remember {
        PlayerPreferencesStore(InMemoryPlayerPreferencesStorage())
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CompositionLocalProvider(
                LocalAudioSettingsStore provides audioSettingsStore,
                LocalPlayerPreferencesStore provides playerPreferencesStore,
            ) {
                SettingsScreen(
                    onBackToMenu = {},
                    animateClouds = false,
                )
            }
        }
    }
}