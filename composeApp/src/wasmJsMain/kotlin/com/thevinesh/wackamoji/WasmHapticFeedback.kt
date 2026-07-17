package com.thevinesh.wackamoji

internal class WasmHapticFeedback : HapticFeedback {
    override fun performLightImpact() {
        vibrateLight()
    }
}

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@JsFun("(ms) => { if (navigator.vibrate) navigator.vibrate(ms); }")
private external fun vibrateLightJs(ms: Int)

private fun vibrateLight() {
    vibrateLightJs(15)
}
