package com.thevinesh.wackamoji

import androidx.compose.ui.graphics.Color

/**
 * Design tokens from the Stitch "Wack-A-Moji Night Variant" design.
 */
object WackAMojiColors {
    // Brand
    val Primary = Color(0xFFF4257B)        // Hot pink
    val Accent = Color(0xFFFACC15)         // Yellow

    // Sky gradient
    val SkyTop = Color(0xFF38BDF8)         // sky-400
    val SkyBottom = Color(0xFFBAE6FD)      // sky-200

    // Cloud
    val Cloud = Color(0xFFFFFFFF)

    // Game board
    val GrassGreen = Color(0xFF4ADE80)     // grass-green
    val GrassBorder = Color(0xFF16A34A)    // green-600

    // Holes
    val EarthBrown = Color(0xFF5D4037)     // earth-brown
    val HoleTopBorder = Color(0x33000000)  // black/20%

    // Buttons
    val RestartOrange = Color(0xFFFB8500)
    val RestartShadow = Color(0xFFD35400)
    val PauseGreen = Color(0xFF8BC34A)
    val PauseShadow = Color(0xFF689F38)
    val ButtonHighlight = Color(0x4DFFFFFF) // white/30%

    // Text
    val SkyDark = Color(0xFF0C4A6E)        // sky-900
    val SkyMedium = Color(0xFF075985)      // sky-800
    val ScoreBadgeBorder = Color(0x1AF4257B) // primary/10%

    // Timer bar
    val TimerTrack = Color(0x80FFFFFF)     // white/50%
    val TimerTrackBorder = Color(0x66FFFFFF) // white/40%
}
