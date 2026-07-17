package com.thevinesh.wackamoji

interface HapticFeedback {
    fun performLightImpact()
}

object NoOpHapticFeedback : HapticFeedback {
    override fun performLightImpact() = Unit
}
