# WackAMoji 👾🔨

A fun, childish, "Whack-A-Mole" style game built entirely with Kotlin Multiplatform (KMP) and Compose Multiplatform. No external image assets are used—everything is drawn natively using Compose UI!

This project targets **Android** and **iOS**.

## Features

- **Cross-Platform**: Playable on both Android and iOS with a single shared codebase.
- **Modern Architecture**: Clean separation of game logic from UI for high testability.
- **Theming**: Includes dynamic styling and design tokens.

## Project Structure

- [`/composeApp`](./composeApp/src) - Shared code across Compose Multiplatform applications.
  - [`commonMain`](./composeApp/src/commonMain/kotlin) - The core game logic, UI components, and state management.
  - [`androidMain`](./composeApp/src/androidMain/kotlin) - Android-specific entry point and actual implementations.
  - [`iosMain`](./composeApp/src/iosMain/kotlin) - iOS-specific expected/actual implementations.
- [`/iosApp`](./iosApp/iosApp) - The iOS Xcode project and SwiftUI host application.

## Getting Started

### Prerequisites

- Android Studio (latest stable or Ladybug+) / IntelliJ IDEA
- Xcode (for iOS development)
- JDK 17+

### Build and Run Android

To build and run the development version of the Android app, use the run widget in Android Studio or build from the terminal:

```shell
./gradlew :composeApp:assembleDebug
```

To install on a connected device/emulator:

```shell
./gradlew :composeApp:installDebug
```

### Build and Run iOS

Open the [`/iosApp`](./iosApp) directory in Xcode and run it from there on a simulator or physical device.

## Contributing

When contributing to this project, please adhere to the guidelines outlined in [`AGENTS.md`](./AGENTS.md).

## License

This project is licensed under the [MIT License](LICENSE).
