package com.thevinesh.wackamoji

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

internal class IosHapticFeedback : HapticFeedback {
    private val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)

    init {
        generator.prepare()
    }

    override fun performLightImpact() {
        generator.impactOccurred()
        generator.prepare()
    }
}
