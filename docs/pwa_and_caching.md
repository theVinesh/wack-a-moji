# Wack-A-Moji PWA & Caching Strategy

This document outlines the architecture used to provide a premium PWA experience for the Wack-A-Moji web app, focusing on fast boot times and offline reliability.

## 1. PWA Architecture

The web app is configured as a standalone Progressive Web App with:

- **Manifest**: Located at `manifest.json`, defining brand colors, standalone display mode, and high-res icons.
- **Favicons**: Custom-generated icons from the high-res app logo to ensure sharp visuals across all devices (Desktop, Android, iOS).

## 2. Dynamic Loading Screen

To avoid the standard "white screen" during WebAssembly initialization:

- A lightweight, inline HTML/CSS loader is embedded in `index.html`.
- **Timing**: The loader is NOT dismissed just because the canvas appears. It waits for a signal from the Kotlin code.
- **Kotlin Integration**: In `main.kt`, a `LaunchedEffect` triggers after the initial composition (once fonts are ready). This calls the JavaScript function `window.onAppReady()` to smoothly fade out the loader.

## 3. Caching Strategy

The service worker (`sw.js`) implements a "Cache-First" strategy to ensure the game works offline and loads instantly on repeat visits.

### Isolated Font Caching

The `NotoColorEmoji.ttf` font is essential for the game's visuals but is large (~10MB). To optimize this:

- **Dedicated Cache**: Fonts are stored in a separate cache bucket named `wackamoji-font-v1`.
- **SW Updates**: When the app is updated (new `v2`, `v3`, etc.), the main `wackamoji-cache` is cleared to ensure users get the latest game logic. However, `wackamoji-font-v1` is **intentionally preserved**.
- **Result**: Users only ever download the 10MB font once. Future game updates only require downloading the small JS/WASM changes.
