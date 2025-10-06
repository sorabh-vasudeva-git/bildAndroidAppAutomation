#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   ./scripts/run-local.sh [avd-name]
# Default avd-name: Pixel9aapi36

AVD_NAME="${1:-Pixel9aapi36}"
APKS_DIR="apps/apk"
APP_PKG="com.netbiscuits.bild.android"
APPIUM_PORT=4723

if [ -z "${ANDROID_SDK_ROOT:-}" ] && [ -z "${ANDROID_HOME:-}" ]; then
  echo "ERROR: ANDROID_SDK_ROOT or ANDROID_HOME must be set."
  exit 1
fi

SDK="${ANDROID_SDK_ROOT:-${ANDROID_HOME}}"

# Start emulator if none running
FIRST_UDID=$(adb devices | awk 'NR>1 && $2=="device" {print $1}' | head -n1 || true)
if [ -z "$FIRST_UDID" ]; then
  echo "Starting AVD: $AVD_NAME"
  nohup "$SDK/emulator/emulator" -avd "$AVD_NAME" -no-window -no-audio >/dev/null 2>&1 &
  adb wait-for-device
  timeout=120
  while [ $timeout -gt 0 ]; do
    boot=$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
    if [ "$boot" = "1" ]; then break; fi
    sleep 1
    timeout=$((timeout-1))
  done
  if [ $timeout -le 0 ]; then
    echo "ERROR: emulator failed to boot"
    exit 2
  fi
  FIRST_UDID=$(adb devices | awk 'NR>1 && $2=="device" {print $1}' | head -n1)
fi

echo "Device ready: $FIRST_UDID (AVD: $AVD_NAME)"

chmod +x ./scripts/install-split-apks.sh
./scripts/install-split-apks.sh "$APKS_DIR" "$AVD_NAME" "$APP_PKG"

# Ensure Appium is installed
if ! command -v appium >/dev/null 2>&1; then
  echo "Appium not found. Please install Appium 3 globally first: npm install -g appium"
  exit 3
fi

echo "Installing uiautomator2 driver (idempotent)..."
appium driver install uiautomator2 || true

echo "Starting Appium server..."
nohup appium --port $APPIUM_PORT > appium.log 2>&1 &
sleep 4
tail -n 40 appium.log || true

# Run tests and pass device.name (AVD name)
chmod +x mvnw
./mvnw -Ddevice.name="$AVD_NAME" -Dappium.server.url="http://127.0.0.1:$APPIUM_PORT" test
