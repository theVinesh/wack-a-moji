# Repository Guidelines: WackAMoji

Use this file as the quick repo-specific guide for contributors and agents. Keep it concise and defer release details to the canonical release doc.

## Project Overview

WackAMoji is a Kotlin Multiplatform game for Android and iOS.

- Shared app/game code: `composeApp/src/commonMain/kotlin/com/thevinesh/wackamoji`
- Shared tests: `composeApp/src/commonTest/kotlin/com/thevinesh/wackamoji`
- Android entrypoint: `composeApp/src/androidMain`
- iOS host app: `iosApp/iosApp`

## Code Expectations

- Keep pure game logic separate from UI.
- Keep extracted UI pieces in their own files (`CloudsBackground`, `ScoreDisplay`, `TimerSection`, `LevelIndicator`, `GameButtons`, `GameOverOverlay`, etc.).
- Put cross-platform code in `commonMain`; keep platform-specific bridges in `androidMain` / `iosMain`.
- Follow Kotlin conventions: 4-space indent, `PascalCase` types, `camelCase` members, `UPPER_SNAKE_CASE` constants.
- Use package namespace `com.thevinesh.wackamoji.*`.
- Avoid new dependencies unless they are clearly necessary.

## Compose & Testing

- Add a deterministic `@Preview` for every new Composable.
- Prefer hard-coded preview data; avoid timers, network calls, and nondeterministic state.
- Add or update `kotlin.test` coverage in `composeApp/src/commonTest/kotlin` when changing game logic.
- Useful checks:
  - `./gradlew test`
  - `./gradlew build`
  - `./gradlew :composeApp:assembleDebug`

## Release Process

- The canonical release guide lives at `docs/release_process.md`.
- Do **not** duplicate release steps, signing setup, metadata-sync instructions, or screenshot workflow details here.
- When release work changes, update the canonical release doc and keep this file limited to the pointer above.

## Contribution Notes

- Preserve the app's playful, child-friendly visual style when adjusting UI.
- Prefer Conventional Commit style for human-authored commits, e.g. `feat: ...` or `fix: ...`.
