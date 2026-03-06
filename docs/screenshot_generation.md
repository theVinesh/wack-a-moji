# Manual Screenshot Generation

Screenshots are generated locally and committed to the shared store metadata tree so both mobile release pipelines read from one repo-owned source of truth.

## Shared screenshot layout

- Android source screenshots: `store_metadata/assets/screenshots/android/en-US/phoneScreenshots/`
- iOS source screenshots: `store_metadata/assets/screenshots/ios/en-US/`

`capture_screenshots.sh` fills those platform-specific folders from Fastlane Screengrab/Snapshot output.

## Prerequisites

1. **Android Studio and Emulator**: Have a running Android emulator available.
2. **Xcode and Simulator**: Have Xcode plus an iOS simulator available (the repo is configured for `iPhone 15 Pro Max`).
3. **Fastlane**: Use the repo Gemfile (`bundle exec fastlane ...`) or install Fastlane locally.

## One-time iOS screenshot setup

The repo includes `iosApp/fastlane/Snapfile` and `iosApp/fastlane/SnapshotHelper.swift`, but Xcode still needs a UI-test target/scheme before `fastlane snapshot` can capture anything:

1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. Create a **UI Testing Bundle** target named `iosAppUITests` for app target `iosApp`.
3. Add `iosApp/fastlane/SnapshotHelper.swift` to that UI-test target.
4. In the UI test `setUp()`, launch the app through `setupSnapshot(app)` before calling `app.launch()`.
5. Add at least one `snapshot("01_GameScreen")` call in the UI test.
6. Create/share the `iosAppUITests` scheme so Fastlane Snapshot can run it.

More information: [Fastlane Snapshot Documentation](https://docs.fastlane.tools/getting-started/ios/screenshots/)

## How to generate screenshots

1. From the repo root, run `./capture_screenshots.sh`.
2. The script runs Android Screengrab and, on macOS, iOS Snapshot.
3. Android output is copied into `store_metadata/assets/screenshots/android/en-US/phoneScreenshots/`.
4. iOS output is copied into `store_metadata/assets/screenshots/ios/en-US/`.
5. Commit the generated images you want to keep.

## How the release tooling uses them

- `bundle exec fastlane android sync_metadata` copies the Android screenshots into `composeApp/fastlane/metadata/android/en-US/images/phoneScreenshots/`, and the deploy lane uploads them to Google Play.
- `bundle exec fastlane ios sync_screenshots` mirrors the committed iOS screenshots into `iosApp/fastlane/screenshots/en-US/` so the Fastlane/App Store layout stays aligned.
- The current GitHub Actions iOS deploy path uploads the TestFlight build only; App Store listing screenshots remain staged repo-side until an App Store listing upload step is run later.
