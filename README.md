📱 Bild Android App Automation
🚀 Overview

This repository contains automated test scripts for the Bild Android App, built using Appium, Java, Maven, TestNG, and Extent Reports.
The framework follows the Page Object Model (POM) design pattern for clean, maintainable, and reusable test code.

🧪 Test Coverage

This project automates critical user journeys for the Bild Android app, including:

App Launch:
✅ Verifies first-time app launch — checks the Bild logo, onboarding flow, and footer icons.

Registered User Login:
✅ Verifies that registered users can log in successfully.

New User Registration:
✅ Automates account creation flow for new users.

Invalid Login:
✅ Ensures proper error messages for non-registered users.

🧰 Tech Stack & Concepts

Framework Features:

✅ Test execution via TestNG.xml

✅ Page Object Model (POM) structure

✅ Maven build management

✅ Configurable setup via external config.properties

✅ Common utility & interaction classes

✅ Extent HTML Reports with screenshot capture for failures

✅ Modular and reusable test methods

✅ Integrated GitHub Actions CI/CD workflow (via .github/workflows/maven.yml)

Tools & Libraries Used:

☕ Java 21+

🧩 Appium (UIAutomator2 driver)

🧱 Maven

🧾 TestNG

📄 Extent Reports

🧍‍♂️ Page Object Design Pattern

⚙️ Shell scripts for installing base + split APKs

⚙️ Project Setup
🧠 Prerequisites

💻 IntelliJ IDEA (recommended) or Android Studio

☕ Java JDK 21+

🧱 Apache Maven 3.9+

📱 Android Studio SDKs

🤖 Appium Server v3.0.2

🧩 UIAutomator2 driver

📲 Android Emulator

Recommended device: Medium Phone (API level 36)

To create it:

Android Studio → Tools → AVD Manager → Create Virtual Device → Phone → Medium Phone → Android 36


Start emulator:

emulator -avd "Medium Phone"

⚙️ Configurations

Project configuration file: config/config.properties

appium.server.url=http://127.0.0.1:4723
device.name=Medium_Phone
platform.name=Android
platform.version=16
app.package=com.netbiscuits.bild.android
app.activity=de.bild.android.app.MainActivity
automation.name=UiAutomator2
no.reset=true

🧭 How to Set Up & Run Tests
🔹 Method 1: Manual Clone and Run

Clone the repository

git clone https://github.com/sorabh-vasudeva-git/bildAndroidAppAutomation.git
cd bildAndroidAppAutomation


Open in IntelliJ IDEA

Start Appium Server

appium


Install the Bild App

./scripts/install-split-apks.sh


(Assumes emulator is fresh and Bild app is not installed.)

Run the tests

mvn clean test

🔹 Method 2: Import via GitHub in IntelliJ/Android Studio

Import project via:
File → New → Project from Version Control → GitHub

Start Appium:

appium


Install APKs and run:

./scripts/install-split-apks.sh
mvn compile
mvn clean test

🧩 Alternate Execution Methods

🧾 From TestNG Runner class

🧱 Via Maven commands

mvn test -DsuiteXmlFile=testng.xml


🧰 Custom shell scripts:

scripts/run-tests.sh

scripts/run-local.sh

🏗️ Project Structure
├── .github/
│   └── workflows/
│       └── maven.yml
├── apps/
│   ├── base.apk
│   ├── split_config.x86_64.apk
│   └── split_config.xxhdpi.apk
├── config/
│   └── config.properties
├── pom.xml
├── reports/
│   ├── report-20251006-150519.html
│   └── report-20251006-175751.html
├── screenshots/
│   ├── appIsRunning-07-10-2025-02-18-26.jpeg
│   └── loginWithNotRegisteredUser-07-10-2025-07-18-09.jpeg
├── scripts/
│   ├── install-split-apks.sh
│   ├── run-local.sh
│   └── run-tests.sh
├── src/
│   ├── main/java/
│   │   └── base/
│   │       ├── AndroidActions.java
│   │       └── ConfigReader.java
│   └── test/java/
│       ├── pages/
│       │   ├── LoginPage.java
│       │   └── HomePage.java
│       └── tests/
│           ├── BaseTest.java
│           └── LoginTest.java
└── README.md

🧾 Notes

✅ Make sure the emulator is running before starting Appium.

✅ Tests execution order is defined in testng.xml.

✅ If no device.name is given, the framework picks the first available ADB device.

✅ Failed test screenshots are stored in the /screenshots directory.

✅ HTML reports generated via Extent Reports are available in /reports.

✅ scripts/install-split-apks.sh automatically installs the Bild app from base + split APKs.

✅ These tests are designed to be idempotent — you can run them multiple times without changing test data or resetting the app manually.

✅ During the tear down process (@AfterTest), a cleanup script automatically uninstalls and reinstalls the Bild app.
This ensures every execution starts with a fresh, clean instance of the app, avoiding stale state or cached data issues.

✅ The project includes a GitHub Actions YAML file under .github/workflows/maven.yml for CI/CD integration.
Currently, this workflow uses a GitHub-hosted emulator instance, which can be flaky and inconsistent for Appium tests.
The recommended approach for stability and scalability is to:

🐳 Use a Docker image with a pre-installed emulator in self-hosted GitHub runners, or

☁️ Integrate with BrowserStack or Sauce Labs, or

🔌 Connect to physical Android devices via ADB or device farms.

🏁 Summary

🧪 Fully automated Android test framework built on top of:

Appium + Java + Maven + TestNG + Extent Reports

💡 Designed with:

Scalability, Maintainability, and Reusability in mind.