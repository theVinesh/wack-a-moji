# Audio Architecture

This document explains how looping background music is implemented once in shared code and reused on Android, iOS, and wasm/web.

## Shared control flow (`commonMain`)

Background music behavior is driven by shared state and a shared interface:

- `GameViewModel` publishes `backgroundMusicState` as `BackgroundMusicState(track, playback, loop)`.
- `App.kt` binds that state through `BindBackgroundMusic(...)`.
- `BindBackgroundMusic` computes the minimum action needed (`Start`, `Pause`, `Resume`, `Stop`) using `resolveBackgroundMusicAction(...)` and applies it through `BackgroundMusicController`.

The lifecycle hook in `BindBackgroundMusic` also maps app foreground/background events to `onAppForegrounded` / `onAppBackgrounded`, which update playback state in `GameViewModel`.

## Platform implementations

All platforms implement the same `BackgroundMusicController` contract:

- **Android** (`AndroidBackgroundMusicController`): uses `MediaPlayer`, sets `isLooping = loop`, and plays `R.raw.loop`.
- **iOS** (`IosBackgroundMusicController`): uses `AVAudioPlayer`, sets `numberOfLoops = -1` when looping, and loads `background-music-loop.mp3` from the app bundle.
- **wasm/web** (`WasmBackgroundMusicController`): calls JS interop methods that delegate to `window.wackAMojiBackgroundMusic` in `index.html`, which controls one `Audio` element with `audio.loop`.

## Cross-platform asset reuse

The loop file has a single canonical source:

- `composeApp/src/androidMain/res/raw/loop.mp3`

Gradle copy tasks in `composeApp/build.gradle.kts` stage this same source for every target:

- Android generated `res/raw` packaging directory
- iOS generated bundle staging directory (renamed to `background-music-loop.mp3`)
- wasm/web generated resources directory (renamed to `background-music-loop.mp3`)

This keeps loop playback behavior shared in Kotlin while ensuring each platform receives the same audio content in its native packaging format.
