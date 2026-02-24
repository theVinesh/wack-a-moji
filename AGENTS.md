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
- Packages: `com.thevinesh.wackamole.*`.
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
