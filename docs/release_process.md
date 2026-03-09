# Mobile Release Process

This is the canonical repo guide for shipping WackAMoji on both Android and iOS.

## What happens automatically vs manually

- Pushes to `main` run the binary release pipeline only:
  - Android: build a signed release AAB and upload it to the Play **internal** track as a **draft** release.
  - iOS: build an IPA and upload it to **TestFlight**.
- Store metadata, listing images, and screenshots do **not** ship on the normal push path.
- Listing sync is a separate **manual GitHub Actions workflow**: `Sync Store Metadata`.
- Final submission/review/promotion steps in Play Console and App Store Connect remain manual.

## Source of truth: what to edit

| Concern | Canonical repo path | Android usage | iOS usage |
| --- | --- | --- | --- |
| Shared app name | `store_metadata/en-US/name.txt` | Synced to Play title | Synced to App Store name |
| Shared long description | `store_metadata/en-US/description.txt` | Synced to Play full description | Synced to App Store description |
| Android-only short description | `store_metadata/en-US/short_description.txt` | Synced to Play short description | Not used |
| iOS-only listing fields | `store_metadata/ios/metadata/en-US/` | Not used | Synced to App Store metadata |
| Android listing images | `store_metadata/assets/feature_graphic.png`, `store_metadata/assets/icon_512.png` | Synced to Play | Not used |
| Android screenshots | `store_metadata/assets/screenshots/android/en-US/phoneScreenshots/` | Synced to Play | Not used |
| iOS screenshot intake | `store_metadata/assets/screenshots/ios/intake/**` | Not used | Staging only, never uploaded directly |
| iOS final screenshots | `store_metadata/assets/screenshots/ios/en-US/` | Not used | Synced to App Store screenshots |

Current iOS-only metadata includes `subtitle`, `promotional_text`, `keywords`, `support_url`, `privacy_url`, and `copyright`.

### Important ownership notes

- The overlapping cross-store copy is currently `name.txt` and `description.txt`.
- Android keeps its own `short_description.txt`.
- iOS App Store-only fields live under `store_metadata/ios/metadata/en-US/`.
- The checked-in `store_metadata/en-US/keywords.txt` is **not** part of the current automated release path. Treat `store_metadata/ios/metadata/en-US/keywords.txt` as the active iOS keywords source unless the automation is changed later.

## Generated folders: do not edit by hand

These are staging outputs, not sources of truth:

- `composeApp/fastlane/metadata/android/`
- `iosApp/fastlane/metadata/`
- `iosApp/fastlane/screenshots/`
- `iosApp/screenshots/en-US/`

## Screenshot flow

1. Run `./capture_screenshots.sh` locally.
2. Android screenshots are copied directly into `store_metadata/assets/screenshots/android/en-US/phoneScreenshots/`.
3. On macOS, `bundle exec fastlane snapshot` writes raw iOS output to `iosApp/screenshots/en-US/`.
4. The script copies those generated iOS files into `store_metadata/assets/screenshots/ios/intake/generated/en-US/`.
5. Optional user-provided iPhone reference shots go to `store_metadata/assets/screenshots/ios/intake/user-provided-iphone/en-US/` via `--ios-user-shot`.
6. Curate the final uploadable App Store set by copying only the approved images into `store_metadata/assets/screenshots/ios/en-US/`.
7. The manual `Sync Store Metadata` workflow is what later uploads the curated final iOS screenshots.

Current iOS Snapshot capture is configured for:

- `iPhone 17 Pro Max`
- `iPad Pro 13-inch (M4)`

That matrix captures `gameplay` and `game-over`, which produces the current minimal universal App Store package of **2 iPhone + 2 iPad** screenshots.

## Binary release flow

### Android

- Workflow: `.github/workflows/build-and-test.yml`
- Jobs: `build-android` -> `deploy-android`
- Build step: `./gradlew :composeApp:bundleRelease`
- Upload step: `bundle exec fastlane android deploy`
- Result: Play Console **internal** track upload with `ANDROID_PLAY_RELEASE_STATUS=draft`
- The normal Android deploy path is **binary-only**: it does not upload metadata, images, or screenshots.

### iOS

- Workflow: `.github/workflows/build-and-test.yml`
- Jobs: `build-ios` -> `deploy-ios`
- Build step: `bundle exec fastlane ios build_release_artifact`
- Upload step: `bundle exec fastlane ios deploy`
- Result: IPA uploaded to **TestFlight**
- The normal iOS deploy path is **binary-only**: it does not upload listing metadata or screenshots.

## Versioning

### Android

- `composeApp/build.gradle.kts` derives release versioning from `GITHUB_RUN_NUMBER`.
- `versionCode = GITHUB_RUN_NUMBER`
- `versionName = 1.0.$GITHUB_RUN_NUMBER`

### iOS

- `ios build_release_artifact` sets the build number from `GITHUB_RUN_NUMBER`.
- `iosApp/Configuration/Config.xcconfig` defines:
  - `CURRENT_PROJECT_VERSION=1` as the base build-number setting
  - `MARKETING_VERSION=1.0.$(CURRENT_PROJECT_VERSION)`
- Listing sync is version-neutral: `ios sync_listing` runs `deliver` with `skip_app_version_update: true`.

Practical implication: routine release version bumps should come from CI/build-number flow, not from manually rewriting store-listing text.

## Signing and secrets

### Android release prerequisites

Required GitHub secrets for binary release:

- `KEYSTORE_FILE_BASE64`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`
- `PLAY_STORE_CONFIG_JSON`

How they are used:

- The workflow decodes `KEYSTORE_FILE_BASE64` into `composeApp/release.jks`.
- Gradle uses `ANDROID_RELEASE_KEYSTORE_PATH`, `ANDROID_RELEASE_KEYSTORE_PASSWORD`, `ANDROID_RELEASE_KEY_ALIAS`, and `ANDROID_RELEASE_KEY_PASSWORD` for signing.
- Fastlane uses `PLAY_STORE_CONFIG_JSON` through `ANDROID_PLAY_CONFIG_JSON_PATH` for Play upload.

### iOS release prerequisites

Required GitHub secrets for binary release:

- `MATCH_GIT_URL`
- `MATCH_PASSWORD`
- `APP_STORE_CONNECT_API_KEY_KEY_ID`
- `APP_STORE_CONNECT_API_KEY_ISSUER_ID`
- `APP_STORE_CONNECT_API_KEY_KEY`

Optional if the signing repo needs authenticated Git access:

- `MATCH_GIT_BASIC_AUTHORIZATION`

How they are used:

- `fastlane match` pulls signing assets for the App Store build.
- The App Store Connect API key is used for both TestFlight upload and manual listing sync.

Manual setup still required outside the repo:

- Apple Developer / App Store Connect app setup
- Match repository initialization and signing asset creation
- Play Console app setup, service-account access, and any track promotion/publishing decisions

## Manual workflows

### Sync store metadata without uploading new binaries

Workflow: `.github/workflows/store-metadata-sync.yml`

- Trigger it manually from GitHub Actions.
- It compares the selected ref against `HEAD^`.
- If no store-relevant paths changed for a platform, that platform exits as a no-op.

Platform behavior:

- Android runs `bundle exec fastlane android sync_listings`
  - uploads metadata, graphics, and screenshots
  - does **not** upload a new AAB
- iOS runs `bundle exec fastlane ios sync_listing`
  - stages metadata/screenshots from `store_metadata/`
  - runs `deliver` with `skip_binary_upload: true`
  - does **not** submit for review

If you only changed listing content, you can run this manual workflow on the commit with those changes without doing a new binary release first.

## Recommended order of operations

### One-time setup

1. Finish Apple and Google account/app setup.
2. Configure all required GitHub secrets.
3. Confirm signing assets and API credentials work locally if you are bootstrapping the pipeline.

### For a normal release

1. Update app code if needed.
2. Update repo-managed listing content in `store_metadata/`.
3. Generate Android/iOS screenshots with `./capture_screenshots.sh`.
4. Curate the final App Store screenshots into `store_metadata/assets/screenshots/ios/en-US/`.
5. Merge or push the release changes to `main`.
6. Let `Build and Test` finish:
   - Android AAB uploaded to Play internal draft
   - iOS IPA uploaded to TestFlight
7. Verify the uploaded binaries in Play Console / TestFlight.
8. Run `Sync Store Metadata` on the ref containing the listing changes.
9. Perform store-console-only steps manually:
   - review listing changes
   - promote Android beyond internal/draft when ready
   - submit the iOS release when ready

### Practical rule of thumb

- Use push-to-`main` for **binaries**.
- Use `Sync Store Metadata` for **listing content**.
- Do final review/promotion/submission in the store consoles.