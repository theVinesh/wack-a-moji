# Repository Guidelines: WackAMoji

This document contains specific guidelines and context for AI agents and human contributors working on the **WackAMoji** project.

## Project Context

WackAMoji is a Kotlin Multiplatform (KMP) game targeting Android and iOS.
**Key Technical & Design Constraints:**

- **UI Architecture**: Game UI components (e.g., `CloudsBackground`, `ScoreDisplay`, `TimerSection`, `LevelIndicator`, `GameButtons`, `GameOverOverlay`) are extracted into separate files.
- **Game Logic**: Pure game logic (state, scoring, timing) is separated from UI components.

## Project Structure & Module Organization

- **Shared Code**: `composeApp/src/commonMain/kotlin/com/thevinesh/wackamole`
- **Android Entrypoint**: `composeApp/src/androidMain`
- **iOS Entrypoint**: `iosApp/iosApp` (SwiftUI host app). Open `iosApp/iosApp.xcodeproj` in Xcode.
- **Tests**: `composeApp/src/commonTest/kotlin`

## Build, Test & Run

- Compile all targets: `./gradlew build`
- Unit tests: `./gradlew test`
- Android debug build: `./gradlew :composeApp:assembleDebug`
- iOS app: open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator/device.

## Coding Style & Naming Conventions

- Follow official Kotlin style (4‑space indent, max 100–120 cols).
- Packages: `com.thevinesh.wackamoji.*`.
- Types: `PascalCase`; functions/vars: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Keep shared UI/state in `commonMain`; platform-specific bridges in `androidMain`/`iosMain`.

### Compose Previews

- Provide a `@Preview` for every new Composable added.
- Previews should encompass all isolated UI components.
- Keep previews deterministic (no timers/network). Use hard-coded sample data.

### Commit Messages

- Prefer Conventional Commits (e.g., `feat: update game loop logic`, `fix: isometric shadow clipping`).
- Provide a single, one-line summary.

## Testing Guidelines

- Framework: `kotlin.test` in `composeApp/src/commonTest`.
- Ensure newly extracted game logic is accompanied by robust unit tests.

## Agent-Specific Notes

- **UI Changes**: When altering the game UI, strictly adhere to the established beautiful, childish design language.
- **Dependencies**: Avoid adding new dependencies unless absolutely necessary.
- **Platform Bridging**: Place cross‑platform code in `commonMain`; use `expect/actual` if needed for platform bridges.

## Cursor Cloud specific instructions

### Environment

- **JDK 21** is pre-installed at `/usr/lib/jvm/java-21-openjdk-amd64`. `JAVA_HOME` and `ANDROID_HOME` are configured in `~/.bashrc`.
- **Android SDK** (platform 36, build-tools 36) is installed at `~/android-sdk`. The file `local.properties` (gitignored) points `sdk.dir` to this path.
- **Gradle 8.14.3** is auto-downloaded by the wrapper (`./gradlew`). No manual Gradle install needed.
- **No Docker or external services** are required; the project is fully self-contained.

### Running the app (WasmJS browser target)

On this Linux VM there is no Android emulator or Xcode. To test the app interactively, use the **WasmJS browser target**:

1. Build: `./gradlew :composeApp:wasmJsBrowserDistribution`
2. Serve: `python3 -m http.server 8080` from `composeApp/build/dist/wasmJs/productionExecutable/`
3. Open `http://localhost:8080` in Chrome.

Alternatively, `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` starts a Webpack dev server with hot-reload (runs on port 8080).

### Gotchas

- The first `./gradlew build` downloads Gradle, Kotlin compiler, and all dependencies — allow ~5-7 minutes.
- iOS targets (`linkDebugFrameworkIos*`) are always SKIPPED on Linux (no Xcode toolchain). This is expected.
- Lint reports are written to `composeApp/build/reports/lint-results-debug.html`.
