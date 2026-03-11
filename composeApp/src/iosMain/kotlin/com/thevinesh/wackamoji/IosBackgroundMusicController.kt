package com.thevinesh.wackamoji

import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.Foundation.NSURL

private const val IOS_GAMEPLAY_LOOP_RESOURCE_NAME = "background-music-loop"
private const val IOS_GAMEPLAY_LOOP_RESOURCE_EXTENSION = "mp3"

@OptIn(ExperimentalForeignApi::class)
internal class IosBackgroundMusicController : BackgroundMusicController {
    private var player: AVAudioPlayer? = null
    private var volume = DEFAULT_MUSIC_VOLUME

    override fun start(track: BackgroundMusicTrack, loop: Boolean) {
        player?.stop()
        player = createPlayer(track = track, loop = loop)?.also { it.play() }
    }

    override fun setVolume(volume: Float) {
        val normalizedVolume = volume.normalizedAudioVolume()
        this.volume = normalizedVolume
        player?.volume = normalizedVolume
    }

    override fun pause() {
        player?.pause()
    }

    override fun resume() {
        player?.play()
    }

    override fun stop() {
        player?.stop()
        player = null
    }

    private fun createPlayer(track: BackgroundMusicTrack, loop: Boolean): AVAudioPlayer? {
        val resourceUrl = bundledResourceUrl(track) ?: return null

        return createAudioPlayer(resourceUrl, loop, volume)
    }
}

private fun bundledResourceUrl(track: BackgroundMusicTrack): NSURL? {
    val (resourceName, resourceExtension) = when (track) {
        BackgroundMusicTrack.GameplayLoop -> IOS_GAMEPLAY_LOOP_RESOURCE_NAME to IOS_GAMEPLAY_LOOP_RESOURCE_EXTENSION
    }

    return bundledResourceUrl(resourceName, resourceExtension)
}

@OptIn(ExperimentalForeignApi::class)
internal fun createAudioPlayer(resourceUrl: NSURL, loop: Boolean, volume: Float = 1f): AVAudioPlayer? = memScoped {
    val error = alloc<ObjCObjectVar<NSError?>>()

    AVAudioPlayer(contentsOfURL = resourceUrl, error = error.ptr).apply {
        numberOfLoops = if (loop) -1 else 0
        this.volume = volume.normalizedAudioVolume()
        prepareToPlay()
    }
}

internal fun bundledResourceUrl(resourceName: String, resourceExtension: String): NSURL? {
    return NSBundle.mainBundle.URLForResource(
        name = resourceName,
        withExtension = resourceExtension,
    )
}