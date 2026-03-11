package com.thevinesh.wackamoji

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

internal class AndroidSoundEffectPlayer(
    context: Context,
) : SoundEffectPlayer {
    private val appContext = context.applicationContext
    private val loadedSoundIds = mutableSetOf<Int>()
    private var volume = DEFAULT_SOUND_EFFECT_VOLUME
    private val soundPool = SoundPool.Builder()
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .setMaxStreams(4)
        .build()
    private val soundIds: Map<SoundEffect, Int>

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSoundIds += sampleId
            }
        }
        soundIds = SoundEffect.entries.associateWith { effect ->
            soundPool.load(appContext, effect.resourceId(), 1)
        }
    }

    override fun play(effect: SoundEffect) {
        soundIds[effect]
            ?.takeIf(loadedSoundIds::contains)
            ?.let { soundPool.play(it, volume, volume, 1, 0, 1f) }
    }

    override fun setVolume(volume: Float) {
        this.volume = volume.normalizedAudioVolume()
    }

    override fun dispose() {
        loadedSoundIds.clear()
        soundPool.release()
    }
}

private fun SoundEffect.resourceId(): Int = when (this) {
    SoundEffect.Wack -> R.raw.wack
    SoundEffect.Click -> R.raw.click
}