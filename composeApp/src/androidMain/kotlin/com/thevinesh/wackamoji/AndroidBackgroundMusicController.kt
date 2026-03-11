package com.thevinesh.wackamoji

import android.content.Context
import android.media.MediaPlayer

/**
 * Android still loads `R.raw.loop`, but the packaged raw resource now comes from the
 * generated canonical-copy directory at build time.
 */
internal class AndroidBackgroundMusicController(
    context: Context,
) : BackgroundMusicController {
    private val appContext = context.applicationContext
    private var mediaPlayer: MediaPlayer? = null
    private var volume = DEFAULT_MUSIC_VOLUME

    override fun start(track: BackgroundMusicTrack, loop: Boolean) {
        stop()

        mediaPlayer = MediaPlayer.create(appContext, track.resourceId()).apply {
            isLooping = loop
            setVolume(volume, volume)
            start()
        }
    }

    override fun setVolume(volume: Float) {
        val normalizedVolume = volume.normalizedAudioVolume()
        this.volume = normalizedVolume
        mediaPlayer?.setVolume(normalizedVolume, normalizedVolume)
    }

    override fun pause() {
        mediaPlayer?.takeIf(MediaPlayer::isPlaying)?.pause()
    }

    override fun resume() {
        mediaPlayer?.takeIf { !it.isPlaying }?.start()
    }

    override fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

private fun BackgroundMusicTrack.resourceId(): Int = when (this) {
    BackgroundMusicTrack.GameplayLoop -> R.raw.loop
}