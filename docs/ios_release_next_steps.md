# iOS Release Automation Status

The repo-side iOS release wiring is complete. Pushes to `main` already run `bundle exec fastlane ios deploy` from GitHub Actions, and that normal path remains TestFlight-only.

Until the Apple Developer Program membership is active and the required Apple credentials exist, the lane exits early with an explicit message instead of failing or requiring more repo edits.

## Remaining external Apple-only blockers

1. Pay/activate the Apple Developer Program membership.
2. Create the App Store Connect app record for bundle identifier `com.thevinesh.wackamoji`.
3. Generate an App Store Connect API key with permission to manage builds.
4. Initialize `fastlane match` storage and create the App Store distribution certificate + provisioning profile.

## Required GitHub repository secrets

Add these secrets once the Apple account prerequisites above are complete:

- `MATCH_GIT_URL`: Private Git URL (or other supported `match` storage URL) that stores signing assets.
- `MATCH_PASSWORD`: Encryption password used by `fastlane match`.
- `MATCH_GIT_BASIC_AUTHORIZATION` *(optional)*: Base64-encoded `username:token` for private Git access if `MATCH_GIT_URL` needs authentication.
- `APP_STORE_CONNECT_API_KEY_KEY_ID`: App Store Connect API key ID.
- `APP_STORE_CONNECT_API_KEY_ISSUER_ID`: App Store Connect API issuer ID.
- `APP_STORE_CONNECT_API_KEY_KEY`: Base64-encoded contents of the downloaded `.p8` key file.

## What the repo now does automatically

- `.github/workflows/build-and-test.yml` runs the `deploy-ios` job on pushes to `main`.
- `iosApp/fastlane/Fastfile` includes helper lanes that sync shared metadata from `store_metadata/en-US` into `iosApp/fastlane/metadata/en-US` and mirror committed iOS screenshots from `store_metadata/assets/screenshots/ios/en-US` into `iosApp/fastlane/screenshots/en-US` for the manual listing-sync path.
- The push-based iOS release automation creates an App Store Connect API session from GitHub secrets, pulls signing assets with `match`, increments the build number from `GITHUB_RUN_NUMBER`, builds an App Store archive, and uploads it to TestFlight.
- If the required secrets are missing, the lane reports the missing prerequisites and exits cleanly so current pushes stay green while Apple access is still blocked.
- The normal push-based iOS path does **not** upload App Store listing metadata or screenshots.
- `.github/workflows/store-metadata-sync.yml` provides a separate manual `Sync Store Metadata` workflow that compares the selected ref against `HEAD^` and runs the listing-only Fastlane lane only when iOS-relevant shared metadata or iOS screenshots changed.
- The manual iOS listing sync path uses `deliver` with `skip_binary_upload: true`, `submit_for_review: false`, and screenshot syncing enabled, so it updates listing content without uploading an IPA or submitting anything for review.

## One-time setup after Apple access is available

1. Create the App Store Connect app for `com.thevinesh.wackamoji` if it does not already exist.
2. Create the App Store Connect API key and save the key ID, issuer ID, and base64-encoded `.p8` contents as the GitHub secrets above.
3. Set up `fastlane match` storage (usually a private Git repository), generate the App Store distribution assets, and save `MATCH_GIT_URL` plus `MATCH_PASSWORD` in GitHub secrets.
4. If the signing repository is private, also save `MATCH_GIT_BASIC_AUTHORIZATION`.

After those external steps are done, no further repo changes are needed: the next push to `main` will take the iOS path all the way through TestFlight upload.

## Manual App Store listing sync

1. Commit shared App Store text changes in `store_metadata/en-US/` and any iOS screenshots in `store_metadata/assets/screenshots/ios/en-US/`.
2. In GitHub Actions, run `Sync Store Metadata` on the branch/commit that contains those listing changes.
3. The workflow checks the selected ref against `HEAD^`. If no iOS-relevant listing paths changed, the iOS listing job exits cleanly without calling App Store Connect.
4. If iOS-relevant listing paths changed, Fastlane stages the shared metadata/screenshots and runs `deliver` in listing-only mode so metadata and screenshots sync without uploading an IPA or submitting for review.
