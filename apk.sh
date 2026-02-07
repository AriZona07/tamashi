#!/bin/bash

# Build the debug APK
./gradlew app:assembleDebug

# Find the APK file
APK_PATH=$(find app/build/outputs/apk/debug -name "*.apk" | head -n 1)

# Check if APK was found
if [ -z "$APK_PATH" ]; then
  echo "Error: APK file not found after build."
  exit 1
fi

# Print the absolute path
echo "APK generated at: $(readlink -f "$APK_PATH")"
