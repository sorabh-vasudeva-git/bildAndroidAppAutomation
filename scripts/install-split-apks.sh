#!/usr/bin/env bash
set -euo pipefail

# Usage:
#   ./scripts/install-split-apks.sh [apks-folder] [avd-name] [app-package]
# Defaults:
#   apks-folder = mobile-test-challenge/apps
#   avd-name = Medium_Phone
#   app-package = com.netbiscuits.bild.android
#
# Env vars supported (Option B):
#   DEVICE_NAME  -> adb device id to use (e.g. emulator-5554 or <device-udid>)
#   AVD_NAME     -> avd name to start if no device is connected
#
# Example:
#   DEVICE_NAME=emulator-5554 AVD_NAME=Medium_Phone ./scripts/install-split-apks.sh

APKS_DIR="${1:-mobile-test-challenge/apps}"
AVD_NAME_ARG="${2:-Medium_Phone}"
APP_PKG="${3:-com.netbiscuits.bild.android}"

if [ ! -d "$APKS_DIR" ]; then
  echo "ERROR: APK directory not found: $APKS_DIR"
  exit 1
fi

if ! command -v adb >/dev/null 2>&1; then
  echo "ERROR: adb not found in PATH. Ensure Android platform-tools are installed and on PATH."
  exit 2
fi

SDK="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}"

# Prefer environment variables if provided (Option B)
DEVICE_NAME_ENV="${DEVICE_NAME:-}"
AVD_NAME_ENV="${AVD_NAME:-}"

# Determine which device to use:
# 1) If DEVICE_NAME env provided and connected -> use it
# 2) Else if a connected device exists -> use first connected device
# 3) Else attempt to start AVD (AVD_NAME env preferred, then arg)
FIRST_UDID=""
if [ -n "$DEVICE_NAME_ENV" ]; then
  # Check if the provided DEVICE_NAME is connected
  if adb devices | awk 'NR>1 && $2=="device" {print $1}' | grep -q "^${DEVICE_NAME_ENV}$" ; then
    FIRST_UDID="$DEVICE_NAME_ENV"
    echo "Using DEVICE_NAME from env: $FIRST_UDID"
  else
    echo "DEVICE_NAME env provided ('$DEVICE_NAME_ENV') but not currently connected."
    # We'll try to start AVD if specified; else continue to find other devices
  fi
fi

if [ -z "$FIRST_UDID" ]; then
  # find first connected device (device state == device)
  FIRST_UDID=$(adb devices | awk 'NR>1 && $2=="device" {print $1}' | head -n1 || true)
fi

# If no device is connected, attempt to start the AVD
if [ -z "$FIRST_UDID" ]; then
  AVD_TO_START="${AVD_NAME_ENV:-$AVD_NAME_ARG}"
  echo "No connected device found. Will try to start AVD: $AVD_TO_START"

  if [ -z "$SDK" ]; then
    echo "ERROR: ANDROID_SDK_ROOT / ANDROID_HOME unset; cannot start emulator."
    exit 3
  fi

  # start emulator in background (use -no-window if CI/headless desired)
  nohup "$SDK/emulator/emulator" -avd "$AVD_TO_START" -no-audio >/dev/null 2>&1 &
  echo "Waiting for emulator to appear..."
  adb wait-for-device

  # wait for boot completion (120s timeout)
  timeout=120
  while [ $timeout -gt 0 ]; do
    boot=$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || echo "")
    if [ "$boot" = "1" ]; then break; fi
    sleep 1
    timeout=$((timeout-1))
  done
  if [ $timeout -le 0 ]; then
    echo "ERROR: emulator failed to boot"
    adb devices
    exit 4
  fi

  # after boot, refresh FIRST_UDID
  FIRST_UDID=$(adb devices | awk 'NR>1 && $2=="device" {print $1}' | head -n1)
fi

if [ -z "$FIRST_UDID" ]; then
  echo "ERROR: No device available after attempting to start emulator."
  adb devices
  exit 5
fi

echo "Using device: $FIRST_UDID (AVD requested: ${AVD_NAME_ENV:-$AVD_NAME_ARG})"
ADB_CMD=(adb -s "$FIRST_UDID")

# Helper: is package installed?
is_installed() {
  "${ADB_CMD[@]}" shell pm list packages | tr -d '\r' | grep -q "^package:$1$" || return $?
}

# If app package provided, attempt to uninstall completely
if [ -n "$APP_PKG" ]; then
  echo "Checking if $APP_PKG is installed..."
  if is_installed "$APP_PKG"; then
    echo "$APP_PKG is installed. Attempting uninstall sequence..."
    # 1) normal uninstall
    if "${ADB_CMD[@]}" uninstall "$APP_PKG" >/dev/null 2>&1; then
      echo "adb uninstall succeeded."
    else
      echo "adb uninstall failed. Trying pm uninstall --user 0 ..."
      if "${ADB_CMD[@]}" shell pm uninstall --user 0 "$APP_PKG" >/dev/null 2>&1; then
        echo "pm uninstall --user 0 succeeded."
      else
        echo "pm uninstall --user 0 failed. Trying to clear data and uninstall again..."
        "${ADB_CMD[@]}" shell pm clear "$APP_PKG" >/dev/null 2>&1 || true
        if "${ADB_CMD[@]}" uninstall "$APP_PKG" >/dev/null 2>&1; then
          echo "adb uninstall succeeded after clearing data."
        else
          echo "Uninstall attempts failed. Checking if package still present..."
          if is_installed "$APP_PKG"; then
            echo "ERROR: Could not remove $APP_PKG. This may be a system app or protected by signature."
            echo "To avoid signature mismatch issues, aborting install. Manually remove app or use a fresh emulator."
            exit 10
          else
            echo "Package no longer present after retries."
          fi
        fi
      fi
    fi
  else
    echo "$APP_PKG not installed — good."
  fi
fi

# Collect APK files portably (works on macOS bash 3.x)
APKS=()
while IFS= read -r -d '' file; do
  APKS+=( "$file" )
done < <(find "$APKS_DIR" -maxdepth 1 -type f -iname "*.apk" -print0 | sort -z || true)

if [ ${#APKS[@]} -eq 0 ]; then
  echo "ERROR: No APKs found in $APKS_DIR"
  exit 5
fi

echo "Installing ${#APKS[@]} apk(s) via: ${ADB_CMD[*]} install-multiple ..."
if "${ADB_CMD[@]}" install-multiple "${APKS[@]}"; then
  echo "install-multiple succeeded."
else
  echo "install-multiple failed; attempting sequential install -r for each apk..."
  for apk in "${APKS[@]}"; do
    echo "Installing $apk ..."
    if "${ADB_CMD[@]}" install -r "$apk"; then
      echo "Installed $apk"
    else
      echo "WARNING: install failed for $apk"
    fi
  done
fi

echo "Installed APKs:"
for a in "${APKS[@]}"; do echo "  $a"; done

# final verification (if APP_PKG provided)
if [ -n "$APP_PKG" ]; then
  echo "Verifying installed package on device:"
  if is_installed "$APP_PKG"; then
    echo "Package $APP_PKG installed successfully."
  else
    echo "WARNING: Package $APP_PKG not found after install. Check install logs above."
  fi
fi

echo "Done."
