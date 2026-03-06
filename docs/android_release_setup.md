# Android Release Setup and Publish Flow

Android publishing is wired for a zero-effort normal flow: once the Play Console setup and GitHub secrets below are in place, a push to `main` builds the signed release bundle and uploads it to the Google Play **internal** track automatically.

## Required GitHub repository secrets

- `KEYSTORE_FILE_BASE64`: Base64-encoded upload keystore file (`release.jks`).
- `KEYSTORE_PASSWORD`: Keystore password.
- `KEY_ALIAS`: Alias inside the keystore.
- `KEY_PASSWORD`: Key password for that alias.
- `PLAY_STORE_CONFIG_JSON`: Google Play service-account JSON with Play Console access.

## One-time setup

1. Create the Google Play app entry for `com.thevinesh.wackamoji`.
2. Create or choose the upload keystore you will use for Play uploads.
3. Grant a Google service account Play Console access, then store its JSON in `PLAY_STORE_CONFIG_JSON`.
4. Base64-encode the keystore and save it as `KEYSTORE_FILE_BASE64`, then save the keystore passwords/alias in the matching GitHub secrets.
5. Commit shared store assets under `store_metadata/`, including any Android phone screenshots in `store_metadata/assets/screenshots/android/en-US/phoneScreenshots/`.

## Normal publish flow

1. Update app code and store metadata as needed.
2. Commit any new Android screenshots to `store_metadata/assets/screenshots/android/en-US/phoneScreenshots/`.
3. Merge or push to `main`.
4. GitHub Actions runs `deploy-android`, decodes the keystore/service-account key, builds the release AAB, syncs metadata from `store_metadata/`, and uploads to the Play internal track.

## Remaining manual steps

Only external Play Console actions remain manual, such as first-time app/account setup, completing Play Console forms/content declarations, or promoting releases beyond the internal track.