#!/bin/bash

# Script to capture screenshots for the app stores using Fastlane Screengrab (Android) and Snapshot (iOS)

echo "📸 Starting manual screenshot capture process..."

# Define the shared screenshot paths used by the store pipelines
SHARED_SCREENSHOTS_DIR="store_metadata/assets/screenshots"
ANDROID_SHARED_ASSETS_DIR="$SHARED_SCREENSHOTS_DIR/android/en-US/phoneScreenshots"
IOS_SHARED_ASSETS_DIR="$SHARED_SCREENSHOTS_DIR/ios/en-US"
ANDROID_DIR="composeApp"
IOS_DIR="iosApp"

# 1. Capture Android Screenshots
echo "🤖 Starting Android screenshot capture..."
echo "Note: Ensure you have a running Android Emulator before proceeding."
cd $ANDROID_DIR || exit 1

# Check if Fastlane is initialized in composeApp
if [ ! -d "fastlane" ]; then
    echo "⚠️ Warning: Fastlane not fully configured in $ANDROID_DIR. Setting up basic screengrab file..."
    mkdir -p fastlane
    cat <<EOF > fastlane/Screengrabfile
android_home(ENV['ANDROID_HOME'])
app_package_name("com.thevinesh.wackamoji")
test_package_name("com.thevinesh.wackamoji.test")
locales(["en-US"])
clear_previous_screenshots(true)
EOF
fi

# Run the Android UI tests to generate screenshots
echo "Building and running tests via gradlew..."
./../gradlew :composeApp:assembleDebug :composeApp:assembleDebugAndroidTest

# Check fastlane availability
if command -v bundle &> /dev/null; then
    bundle install || true
    bundle exec fastlane screengrab || FASTLANE_ANDROID_FAILED=1
else
    fastlane screengrab || FASTLANE_ANDROID_FAILED=1
fi

if [ "$FASTLANE_ANDROID_FAILED" = "1" ]; then
    echo "❌ Android screenshot capture failed."
else
    echo "✅ Android screenshots captured."
fi
cd ..

# 2. Capture iOS Screenshots (If Xcode and Simulator are available)
# Check if running on macOS
if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "🍎 Detected macOS. Starting iOS screenshot capture..."
    cd $IOS_DIR || exit 1
    
    # Check if Snapshot is configured
    if [ ! -d "fastlane" ]; then
        echo "⚠️ Warning: Fastlane not fully configured in $IOS_DIR. Setting up basic Snapfile..."
        mkdir -p fastlane
        cat <<EOF > fastlane/Snapfile
devices([
  "iPhone 15 Pro Max" # 6.7-inch device
])
languages([
  "en-US"
])
scheme("iosAppUITests")
output_directory("./screenshots")
clear_previous_screenshots(true)
EOF
    fi

    # Run the iOS UI tests to generate screenshots
    if command -v bundle &> /dev/null; then
        bundle install || true
        bundle exec fastlane snapshot || FASTLANE_IOS_FAILED=1
    else
        fastlane snapshot || FASTLANE_IOS_FAILED=1
    fi
    
    if [ "$FASTLANE_IOS_FAILED" = "1" ]; then
        echo "❌ iOS screenshot capture failed. Ensure you have added a UI Testing Target in Xcode and included the SnapshotHelper.swift file. See docs/screenshot_generation.md for details."
    else
        echo "✅ iOS screenshots captured."
    fi
    cd ..
else
    echo "⚠️ Not running on macOS. Skipping iOS screenshot capture."
fi

# 3. Synchronize Screenshots to the Shared Directory
echo "🔄 Copying generated screenshots into shared store metadata ($SHARED_SCREENSHOTS_DIR)..."
mkdir -p "$ANDROID_SHARED_ASSETS_DIR" "$IOS_SHARED_ASSETS_DIR"

# Copy Android screenshots
if [ -d "$ANDROID_DIR/fastlane/metadata/android/en-US/images/phoneScreenshots" ]; then
    rm -f "$ANDROID_SHARED_ASSETS_DIR"/*.png
    cp "$ANDROID_DIR"/fastlane/metadata/android/en-US/images/phoneScreenshots/*.png "$ANDROID_SHARED_ASSETS_DIR/" 2>/dev/null
    echo "Copied Android screenshots to $ANDROID_SHARED_ASSETS_DIR."
fi

# Copy iOS screenshots
if [ -d "$IOS_DIR/fastlane/screenshots/en-US" ]; then
    rm -f "$IOS_SHARED_ASSETS_DIR"/*.png
    cp "$IOS_DIR"/fastlane/screenshots/en-US/*.png "$IOS_SHARED_ASSETS_DIR/" 2>/dev/null
    echo "Copied iOS screenshots to $IOS_SHARED_ASSETS_DIR."
fi

echo "🎉 Screenshot capture process complete! Please verify the platform-specific images under $SHARED_SCREENSHOTS_DIR."
echo "You can now commit these images to the repository to be used by the next Android publish and staged for the iOS App Store listing flow."
