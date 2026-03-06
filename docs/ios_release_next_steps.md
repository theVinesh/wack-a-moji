# iOS Release Automation Next Steps

Because the Apple Developer Program fee has not yet been paid, we cannot fully automate the iOS store submission (Fastlane requires access to App Store Connect to generate distribution certificates, provisioning profiles, and API access).

Once you pay the fee, follow these steps to finish setting up the automated pipeline for the iOS build:

### 1. Register App ID in App Store Connect

Log in to your Apple Developer account and create an App ID for your application. Make sure the Bundle Identifier matches what is in your Xcode project.

### 2. Set up App Store Connect API Key

Fastlane needs an API key to communicate with Apple's servers securely without 2FA prompts:

1. Go to App Store Connect -> Users and Access -> Keys.
2. Generate a new API Key with "App Manager" permissions.
3. Download the `.p8` key file.
4. Record the **Issuer ID** and **Key ID**.

### 3. Initialize Code Signing (Fastlane Match)

We will use `fastlane match` to automate code signing (certificates and provisioning profiles). You'll usually store these in a private GitHub repository or Google Cloud Storage.

1. Run `fastlane match init` and set up the storage backend.
2. Run `fastlane match appstore` to generate and save your distribution certificates and profiles securely.

### 4. Provide Secrets to GitHub Actions

Add the following as repository secrets in GitHub:

* `MATCH_PASSWORD`: The encryption password you created when running `fastlane match init`.
* `APP_STORE_CONNECT_API_KEY_KEY_ID`: Your Key ID.
* `APP_STORE_CONNECT_API_KEY_ISSUER_ID`: Your Issuer ID.
* `APP_STORE_CONNECT_API_KEY_KEY`: The base64-encoded string of the `.p8` API Key file content.
* `GIT_PAT_MATCH` (If using a private repo for Match): A GitHub Personal Access Token to clone the certificates repo.

### 5. Update GitHub Actions Workflow and Fastfile

1. **Update `ios/fastlane/Fastfile`**: Add the `match` action to pull certificates, and `app_store_connect_api_key` to authenticate. Finally, add the `upload_to_testflight` or `upload_to_app_store` action.
2. **Update `.github/workflows/build-and-test.yml`**: Change the current `ios-build-release` job to an `ios-deploy` job that runs `bundle exec fastlane ios deploy`.

By completing these steps, pushing to `main` will automatically build and distribute the iOS app exactly as it does for Android.
