package com.thevinesh.wackamoji

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.jetbrains.compose.resources.Font
import wackamole.composeapp.generated.resources.NotoColorEmoji
import wackamole.composeapp.generated.resources.Res

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        val emojiFontFamily = FontFamily(Font(Res.font.NotoColorEmoji))
        CompositionLocalProvider(LocalEmojiFont provides emojiFontFamily) {
            App()
            // Signal JS that fonts + UI are ready
            androidx.compose.runtime.LaunchedEffect(Unit) { dismissLoader() }
        }
    }
}

// JS interop: call window.onAppReady() to dismiss the HTML loader
@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@JsFun("() => { if (window.onAppReady) window.onAppReady(); }")
external fun dismissLoader()
