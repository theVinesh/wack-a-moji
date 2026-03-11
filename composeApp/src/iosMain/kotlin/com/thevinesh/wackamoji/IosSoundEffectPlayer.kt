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

    override fun play(effect: SoundEffect) {
        activePlayers.removeAll { !it.playing }
        createSoundEffectPlayer(effect)?.also { player ->
            activePlayers += player
            player.play()
        }
    }

    override fun dispose() {
        activePlayers.forEach { it.stop() }
        activePlayers.clear()
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun createSoundEffectPlayer(effect: SoundEffect): AVAudioPlayer? {
    val resourceUrl = bundledResourceUrl(effect) ?: return null
    return createAudioPlayer(resourceUrl, loop = false)
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