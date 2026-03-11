package com.thevinesh.wackamoji

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer

private const val IOS_WACK_SOUND_EFFECT_RESOURCE_NAME = "wack"
private const val IOS_WACK_SOUND_EFFECT_RESOURCE_EXTENSION = "mp3"
private const val IOS_CLICK_SOUND_EFFECT_RESOURCE_NAME = "click"
private const val IOS_CLICK_SOUND_EFFECT_RESOURCE_EXTENSION = "mp3"

@OptIn(ExperimentalForeignApi::class)
internal class IosSoundEffectPlayer : SoundEffectPlayer {
    private val activePlayers = mutableListOf<AVAudioPlayer>()
    private var volume = DEFAULT_SOUND_EFFECT_VOLUME

    override fun play(effect: SoundEffect) {
        activePlayers.removeAll { !it.playing }
        createSoundEffectPlayer(effect)?.also { player ->
            activePlayers += player
            player.play()
        }
    }

    override fun setVolume(volume: Float) {
        val normalizedVolume = volume.normalizedAudioVolume()
        this.volume = normalizedVolume
        activePlayers.forEach { it.volume = normalizedVolume }
    }

    override fun dispose() {
        activePlayers.forEach { it.stop() }
        activePlayers.clear()
    }

    private fun createSoundEffectPlayer(effect: SoundEffect): AVAudioPlayer? {
        val resourceUrl = bundledResourceUrl(effect) ?: return null
        return createAudioPlayer(resourceUrl, loop = false, volume = volume)
    }
}

private fun bundledResourceUrl(effect: SoundEffect) = when (effect) {
    SoundEffect.Wack -> bundledResourceUrl(
        resourceName = IOS_WACK_SOUND_EFFECT_RESOURCE_NAME,
        resourceExtension = IOS_WACK_SOUND_EFFECT_RESOURCE_EXTENSION,
    )
    SoundEffect.Click -> bundledResourceUrl(
        resourceName = IOS_CLICK_SOUND_EFFECT_RESOURCE_NAME,
        resourceExtension = IOS_CLICK_SOUND_EFFECT_RESOURCE_EXTENSION,
    )
}