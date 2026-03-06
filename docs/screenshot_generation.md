# Manual Screenshot Generation

To save CI time and resources, screenshots are generated locally on your machine and committed to the repository.

## Prerequisites

1. **Xcode and Simulator**: Ensure you have Xcode installed along with an iOS Simulator (e.g., iPhone 15 Pro Max).
2. **Android Studio and Emulator**: Ensure you have Android Studio installed and a running Android Emulator.
3. **Fastlane**: Ensure you have Fastlane installed (`gem install fastlane`) or via Homebrew (`brew install fastlane`).

## Initial iOS Xcode Setup (One-Time)

Because the `iosApp` project does not have a UI Testing Target by default, you must set one up to take iOS screenshots automatically:

1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. Go to **File > New > Target...**, choose **UI Testing Bundle** (under iOS / Test), and name it `iosAppUITests`. Make sure the "Target to be Tested" is `iosApp`.
3. In your terminal, run `cd iosApp && fastlane snapshot init`.
4. Drag the newly generated `iosApp/fastlane/SnapshotHelper.swift` file into your `iosAppUITests` target in Xcode.
5. In Xcode, click your new `iosAppUITests` class, and inside the `setUp()` method, ensure you add:

   ```swift
   let app = XCUIApplication()
   setupSnapshot(app)
   app.launch()
   ```

6. Inside `testExample()`, add `snapshot("0Launch")` (or `"01_GameScreen"`) to take the picture!
7. **Add a new Xcode scheme** explicitly for the newly created `iosAppUITests` target.
8. Edit the newly created scheme and ensure the **Shared** checkbox is enabled.

More information: [Fastlane Snapshot Documentation](https://docs.fastlane.tools/getting-started/ios/screenshots/)

## How to Generate

1. Open your terminal and navigate to the root of the project.
2. Run the manual screenshot script:

    ```bash
    ./capture_screenshots.sh
    ```

3. The script will automatically detect the android emulator and iOS simulator, compile the apps, run the screenshot tests, and copy the final `png` images into `store_metadata/assets/screenshots/`.
4. Commit these images to your repository. Fastlane will upload them automatically during the next GitHub Actions deployment.
