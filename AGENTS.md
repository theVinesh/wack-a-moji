# Repository Guidelines

## Project Structure & Module Organization
- Kotlin Multiplatform with Compose: shared code in `composeApp/src/commonMain/kotlin`.
- Android entrypoint and resources in `composeApp/src/androidMain` (e.g., `MainActivity.kt`, `AndroidManifest.xml`).
- iOS entrypoint in `iosApp/iosApp` (SwiftUI host app). Open `iosApp/iosApp.xcodeproj` in Xcode.
- Tests live in `composeApp/src/commonTest/kotlin`.
- Build scripts: `build.gradle.kts`, `settings.gradle.kts`, module `composeApp/build.gradle.kts`.

## Build, Test & Run
- Build all: `./gradlew build` — compiles all targets and runs checks.
- Unit tests: `./gradlew test` or `./gradlew :composeApp:check` — runs common tests and verifications.
- Android debug APK: `./gradlew :composeApp:assembleDebug`.
- Install to device/emulator: `./gradlew :composeApp:installDebug` (requires an Android device/emulator).
- iOS app: open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator/device.

## Coding Style & Naming Conventions
- Follow official Kotlin style (4‑space indent, max 100–120 cols, trailing commas allowed).
- Packages: `com.thevinesh.wackamole.*`. Types: `PascalCase`; functions/vars: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Android resources use lowercase underscore (e.g., `ic_launcher_background`).
- Keep shared UI/state in `commonMain`; platform APIs in `androidMain`/`iosMain`.

## Testing Guidelines
- Framework: `kotlin.test` in `composeApp/src/commonTest`.
- Name test classes with `...Test` and annotate methods with `@Test`.
- Add tests for new logic in `commonMain` when feasible. Run via `./gradlew test`.

## Commit & Pull Request Guidelines
- Prefer Conventional Commits (e.g., `feat: add score tracking`, `fix: null state crash`).
- Keep commits focused and descriptive; reference issues (`Closes #123`).
- PRs should include: summary, rationale, screenshots (UI changes), test notes, and affected platforms (Android/iOS).

## Security & Configuration Tips
- Do not commit secrets/keys. Android signing configs and iOS provisioning should remain local.
- iOS configuration lives under `iosApp/Configuration/*.xcconfig` — keep sensitive overrides out of VCS.

## Agent-Specific Notes
- Do not move modules or restructure Gradle without discussion.
- Place cross‑platform code in `commonMain`; use expect/actual if needed for platform bridges.
- Avoid new dependencies unless justified in the PR description.
