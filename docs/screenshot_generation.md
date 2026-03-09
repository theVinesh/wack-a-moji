# Manual Screenshot Generation

Screenshots are generated locally, staged for review, and only the curated final set is copied into the shared store metadata tree that the release tooling uploads.

## Shared screenshot layout

- Android source screenshots: `store_metadata/assets/screenshots/android/en-US/phoneScreenshots/`
- iOS final curated screenshots for upload: `store_metadata/assets/screenshots/ios/en-US/`
- iOS generated Snapshot intake: `store_metadata/assets/screenshots/ios/intake/generated/en-US/`
- iOS user-provided iPhone intake: `store_metadata/assets/screenshots/ios/intake/user-provided-iphone/en-US/`

`capture_screenshots.sh` fills Android’s final source folder directly, but stages iOS captures in the intake folders so the final App Store set can be curated separately.

## Prerequisites

1. **Android Studio and Emulator**: Have a running Android emulator available.
2. **Xcode and Simulator**: Have Xcode plus the repo’s configured iPhone and iPad simulators available (`iPhone 17 Pro Max` and `iPad Pro 13-inch (M4)` in the current `Snapfile`).
3. **Fastlane**: Use the repo Gemfile (`bundle exec fastlane ...`) or install Fastlane locally.

## In-repo iOS screenshot plumbing

The repo now includes the minimum iOS screenshot automation plumbing needed for a universal App Store package:

1. Shared scheme: `iosApp/iosApp.xcodeproj/xcshareddata/xcschemes/iosAppUITests.xcscheme`
2. Snapshot helper compiled into the UI-test target: `iosApp/iosAppUITests/SnapshotHelper.swift`
3. Deterministic capture tests: `iosApp/iosAppUITests/iosAppUITests.swift`
4. Launch-argument driven screenshot states: `gameplay` and `game-over`
5. Snapshot device matrix: one iPhone + one iPad simulator, which produces a minimal 2 iPhone + 2 iPad package because each simulator captures the same two scenes

More information: [Fastlane Snapshot Documentation](https://docs.fastlane.tools/getting-started/ios/screenshots/)

## How to generate screenshots

1. Optional but recommended: stage the two user-provided iPhone references while you capture automated shots:
   - `./capture_screenshots.sh --ios-user-shot /path/to/gameplay-reference.png --ios-user-shot /path/to/game-over-reference.png`
2. The script runs Android Screengrab and, on macOS, iOS Snapshot.
3. Android output is copied into `store_metadata/assets/screenshots/android/en-US/phoneScreenshots/`.
4. In this repo, `bundle exec fastlane snapshot` writes generated iOS captures to `iosApp/screenshots/en-US/`.
5. `capture_screenshots.sh` copies those generated Snapshot files into `store_metadata/assets/screenshots/ios/intake/generated/en-US/`.
6. The two user-provided iPhone screenshots are copied into `store_metadata/assets/screenshots/ios/intake/user-provided-iphone/en-US/` when provided via `--ios-user-shot`.
7. Curate the final uploadable 2 iPhone + 2 iPad set later by copying only the approved images into `store_metadata/assets/screenshots/ios/en-US/`.

## How the release tooling uses them

- `bundle exec fastlane android sync_metadata` copies the Android screenshots into `composeApp/fastlane/metadata/android/en-US/images/phoneScreenshots/`, and the manual `Sync Store Metadata` workflow runs `bundle exec fastlane android sync_listings` to upload Android listing assets without uploading a new AAB.
- `bundle exec fastlane ios sync_screenshots` mirrors only the curated final screenshots from `store_metadata/assets/screenshots/ios/en-US/` into `iosApp/fastlane/screenshots/en-US/` for `deliver`. This is a later release-sync step and is separate from the raw Snapshot capture output in `iosApp/screenshots/en-US/`.
- The normal GitHub Actions Android/iOS deploy paths stay binary-only (`deploy-android`) and TestFlight-only (`deploy-ios`); listing screenshots sync only through the manual store-metadata workflow.
