#!/bin/bash

# Script to capture screenshots for the app stores using Fastlane Screengrab (Android) and Snapshot (iOS)

set -euo pipefail

IOS_USER_SHOT_INPUTS=()
FASTLANE_ANDROID_FAILED=0
FASTLANE_IOS_FAILED=0

while [[ $# -gt 0 ]]; do
    case "$1" in
        --ios-user-shot)
            if [[ $# -lt 2 ]]; then
                echo "❌ Missing file path after --ios-user-shot"
                exit 1
            fi
            IOS_USER_SHOT_INPUTS+=("$2")
            shift 2
            ;;
        *)
            echo "❌ Unknown argument: $1"
            echo "Usage: ./capture_screenshots.sh [--ios-user-shot /path/to/gameplay.png] [--ios-user-shot /path/to/game-over.png]"
            exit 1
            ;;
    esac
done

echo "📸 Starting manual screenshot capture process..."

# Define the shared screenshot paths used by the store pipelines
SHARED_SCREENSHOTS_DIR="store_metadata/assets/screenshots"
ANDROID_SHARED_ASSETS_DIR="$SHARED_SCREENSHOTS_DIR/android/en-US/phoneScreenshots"
IOS_SHARED_ASSETS_DIR="$SHARED_SCREENSHOTS_DIR/ios/en-US"
IOS_GENERATED_INTAKE_DIR="$SHARED_SCREENSHOTS_DIR/ios/intake/generated/en-US"
IOS_USER_PROVIDED_INTAKE_DIR="$SHARED_SCREENSHOTS_DIR/ios/intake/user-provided-iphone/en-US"
ANDROID_DIR="composeApp"
IOS_DIR="iosApp"
IOS_SNAPSHOT_OUTPUT_DIR="$IOS_DIR/screenshots/en-US"

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
mkdir -p "$ANDROID_SHARED_ASSETS_DIR" "$IOS_SHARED_ASSETS_DIR" "$IOS_GENERATED_INTAKE_DIR" "$IOS_USER_PROVIDED_INTAKE_DIR"

# Copy Android screenshots
if [ -d "$ANDROID_DIR/fastlane/metadata/android/en-US/images/phoneScreenshots" ]; then
    rm -f "$ANDROID_SHARED_ASSETS_DIR"/*.png
    cp "$ANDROID_DIR"/fastlane/metadata/android/en-US/images/phoneScreenshots/*.png "$ANDROID_SHARED_ASSETS_DIR/" 2>/dev/null
    echo "Copied Android screenshots to $ANDROID_SHARED_ASSETS_DIR."
fi

# Copy iOS screenshots into intake staging from Snapshot output; keep final curated en-US/ folder untouched
if [ -d "$IOS_SNAPSHOT_OUTPUT_DIR" ]; then
    rm -f "$IOS_GENERATED_INTAKE_DIR"/*.png
    cp "$IOS_SNAPSHOT_OUTPUT_DIR"/*.png "$IOS_GENERATED_INTAKE_DIR/" 2>/dev/null
    echo "Copied generated iOS screenshots to $IOS_GENERATED_INTAKE_DIR."
else
    echo "⚠️ No generated iOS Snapshot output found at $IOS_SNAPSHOT_OUTPUT_DIR."
fi

if [ ${#IOS_USER_SHOT_INPUTS[@]} -gt 0 ]; then
    rm -f "$IOS_USER_PROVIDED_INTAKE_DIR"/*

    for index in "${!IOS_USER_SHOT_INPUTS[@]}"; do
        screenshot_path="${IOS_USER_SHOT_INPUTS[$index]}"
        if [ ! -f "$screenshot_path" ]; then
            echo "⚠️ Skipping missing user-provided iPhone screenshot: $screenshot_path"
            continue
        fi

        target_name=$(printf "%02d_user_provided_iphone_reference.%s" "$((index + 1))" "${screenshot_path##*.}")
        cp "$screenshot_path" "$IOS_USER_PROVIDED_INTAKE_DIR/$target_name"
    done

    echo "Staged user-provided iPhone screenshots in $IOS_USER_PROVIDED_INTAKE_DIR."
else
    echo "ℹ️ To stage the two user-provided iPhone references, rerun with:"
    echo "   ./capture_screenshots.sh --ios-user-shot /path/to/gameplay.png --ios-user-shot /path/to/gameover.png"
fi

echo "🎉 Screenshot capture process complete! Please verify the platform-specific images under $SHARED_SCREENSHOTS_DIR."
echo "Curate only the chosen final 2 iPhone + 2 iPad App Store set into $IOS_SHARED_ASSETS_DIR during the follow-on curation wave."
