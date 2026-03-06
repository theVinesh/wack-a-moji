# iOS Release Automation Status

The repo-side iOS release wiring is complete. Pushes to `main` already run `bundle exec fastlane ios deploy` from GitHub Actions.

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
- `iosApp/fastlane/Fastfile` syncs shared metadata from `store_metadata/en-US` into `iosApp/fastlane/metadata/en-US` and mirrors committed iOS screenshots from `store_metadata/assets/screenshots/ios/en-US` into `iosApp/fastlane/screenshots/en-US`.
- The same lane creates an App Store Connect API session from GitHub secrets, pulls signing assets with `match`, increments the build number from `GITHUB_RUN_NUMBER`, builds an App Store archive, and uploads it to TestFlight.
- If the required secrets are missing, the lane reports the missing prerequisites and exits cleanly so current pushes stay green while Apple access is still blocked.
- The current automated iOS path does **not** upload App Store listing screenshots yet; it stages the shared screenshots in Fastlane's expected folder so the repo layout is already aligned when listing upload is added later.

## One-time setup after Apple access is available

1. Create the App Store Connect app for `com.thevinesh.wackamoji` if it does not already exist.
2. Create the App Store Connect API key and save the key ID, issuer ID, and base64-encoded `.p8` contents as the GitHub secrets above.
3. Set up `fastlane match` storage (usually a private Git repository), generate the App Store distribution assets, and save `MATCH_GIT_URL` plus `MATCH_PASSWORD` in GitHub secrets.
4. If the signing repository is private, also save `MATCH_GIT_BASIC_AUTHORIZATION`.

After those external steps are done, no further repo changes are needed: the next push to `main` will take the iOS path all the way through TestFlight upload.
