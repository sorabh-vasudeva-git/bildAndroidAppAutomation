#!/usr/bin/env bash
set -e

if [ -z "$ANDROID_SDK_ROOT" ] && [ -z "$ANDROID_HOME" ]; then
  echo "ERROR: Set ANDROID_SDK_ROOT or ANDROID_HOME first."
  exit 1
fi

if ! command -v adb >/dev/null 2>&1; then
  echo "ERROR: adb not found in PATH."
  exit 1
fi

echo "Using SDK: ${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
echo "Make sure emulator is running and APKs installed (./scripts/install-split-apks.sh apps/)."
chmod +x mvnw
./mvnw test
