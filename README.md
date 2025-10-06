# 🧪 Appium Android Test Automation Setup

This project uses **Appium + Java + UiAutomator2** for Android automation.  
To make it flexible for every teammate, emulator/device info is passed via **environment variables** (no hardcoded names).

---

## ⚙️ 1. Local Setup

### Prerequisites
- Android Studio + SDK installed
- Emulator configured (see `emulator -list-avds`)
- Appium 2.x installed
- Node.js ≥ 16
- Maven ≥ 3.8
- Java ≥ 17

---

## 🚀 2. Running Tests Locally

You don’t need a `.env` file — just provide environment variables either inline or once per session.

### Option A — Inline (quickest)
```bash
DEVICE_NAME=emulator-5554 AVD_NAME=Medium_Phone mvn test

## Option B — Export once per session
export DEVICE_NAME=emulator-5554
export AVD_NAME=Medium_Phone
mvn test

## Installing apk before test

DEVICE_NAME=emulator-5554 AVD_NAME=Medium_Phone ./scripts/install-split-apks.sh
