package com.thevinesh.wackamoji

enum class SoundEffect {
    Wack,
    Click,
}

interface SoundEffectPlayer {
    fun play(effect: SoundEffect)
    fun dispose()
}

object NoOpSoundEffectPlayer : SoundEffectPlayer {
    override fun play(effect: SoundEffect) = Unit

    override fun dispose() = Unit
}