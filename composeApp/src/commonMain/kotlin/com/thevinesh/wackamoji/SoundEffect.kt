package com.thevinesh.wackamoji

enum class SoundEffect {
    Wack,
    Click,
}

interface SoundEffectPlayer {
    fun play(effect: SoundEffect)
    fun setVolume(volume: Float)
    fun dispose()
}

object NoOpSoundEffectPlayer : SoundEffectPlayer {
    override fun play(effect: SoundEffect) = Unit

    override fun setVolume(volume: Float) = Unit

    override fun dispose() = Unit
}