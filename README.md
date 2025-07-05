# MobileController

## Overview

This is a Kotlin-based Android app providing an HTTP API to control device functions such as:
- getting properties
- opening the camera
- taking a photo

A Python CLI script (`controller.py`) is also included for testing these APIs.

---

## Project Requirements

- **Minimum SDK Version:** 24 (Android 7.0 Nougat)  
- **Target & Compile SDK Version:** 35 (Android 13)  
- **Java Version:** 11  
- **Kotlin Version:** 1.9.0  
- **Build Tools:** Android Gradle Plugin compatible with SDK 35 and Kotlin 1.9.0  

---

## Plugins Used

- **Android Application Plugin:** For building and packaging the APK.  
- **Kotlin Android Plugin:** Enables Kotlin support for Android development.  
- **Kotlin Compose Plugin:** Adds Jetpack Compose compiler extensions and tooling.  
- **Kotlin Serialization Plugin:** Supports Kotlin Serialization for JSON and other formats.  

---

## Key Dependencies

### Core Libraries

- **Jetpack Compose:** UI toolkit for building native Android interfaces. Managed with Compose BOM for version alignment.  
- **CameraX:** Jetpack libraries (`camera-core`, `camera-camera2`, `camera-view`) for easy camera integration.  
- **Kotlinx Serialization:** JSON serialization/deserialization library for Kotlin.  
- **NanoHTTPD:** Lightweight HTTP server embedded in the app.  

### AndroidX and Kotlin Extensions

- `androidx.core:core-ktx` - Kotlin extensions for Android core APIs.  
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle-aware components with Kotlin support.  
- `androidx.activity:activity-compose` - Compose integration with Android activity lifecycle.  
- `androidx.material3:material3` - Material Design 3 components for Compose UI.  

### Testing

- **JUnit 4.13.2:** Unit testing framework.  
- **AndroidX Test:** Includes JUnit extensions and Espresso for UI testing.  
- **Compose UI Test Libraries:** Testing support for Compose UI elements.  

---

## Build Configuration Highlights

- **Java Compatibility:** Source and target set to Java 11.  
- **Compose Enabled:** Build feature toggled for Compose.  
- **Proguard:** Enabled for release builds with default Android optimizations.  

---

## Additional Notes

- The project uses a **version catalog** (`libs.versions.toml`) for managing library versions centrally, referenced in the Gradle build scripts as `libs.` aliases.  
- Explicit version pinning is used for some CameraX and Kotlinx Serialization libraries to ensure stability.  

---

## Setup Instructions

1. **Android Studio:** Use a recent version compatible with Android SDK 35 and Kotlin 1.9.0.  
2. **Java:** Ensure Java 11 JDK is installed and configured.  
3. **Gradle:** Use the Gradle wrapper included or install Gradle compatible with the Android Gradle Plugin version.  
4. **Sync & Build:** Open the project in Android Studio and sync Gradle to download dependencies.

- Python 3.x (if using the CLI)
- `pip install -r app/scripts/requirements.txt` for Python dependencies

---

## Permissions & Features

This app requires the following permissions to function correctly:

- `CAMERA` — To access the device camera for photo capture and preview.  
- `WRITE_EXTERNAL_STORAGE` — To save captured photos to device storage.  
- `INTERNET` — To enable networking features (e.g., HTTP server).  
- `ACCESS_NETWORK_STATE` — To monitor network connectivity status.

The app declares that the camera hardware feature is **not required** (`android:required="false"`), allowing installation on devices without a camera, though camera features will be limited.

---

## How to build

```bash
git clone https://github.com/samshimoni/MobileController.git
cd MobileController
./gradlew assembleDebug
