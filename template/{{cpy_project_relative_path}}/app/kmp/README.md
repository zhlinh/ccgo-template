# CCGO Sample App - Multiplatform Test Application

A Kotlin Multiplatform Compose application for testing CCGO native library integration across Android, iOS, and Desktop platforms.

## 📱 Supported Platforms

1. **Android** - Native Android application
2. **iOS** - Native iOS application
3. **Desktop** - JVM Desktop application (Windows, macOS, Linux)

## 🎨 UI Design

The application features a split-screen design optimized for testing native functionality:

### Top Section (40% of screen) - Button Grid
- **3-column grid layout** with square buttons
- **Easy to extend** - simply add more `TestButton` entries in `App.kt`
- Pre-configured test buttons:
  - `Test 1` - Platform detection test
  - `Test 2` - Native bridge test
  - `Test 3` - Integration test
  - `Enable Debug Log` - Calls native `SetDebugLog(true)`
  - `Disable Debug Log` - Calls native `SetDebugLog(false)`
  - `Clear Log` - Clears the log output

### Bottom Section (60% of screen) - Log Output
- **Scrollable text view** with automatic scrolling
- **Timestamped logs** for each button click
- **Platform-specific timestamp formatting**
- Placeholder text when empty: "Tap buttons above to see output..."

## 🏗️ Architecture

### Kotlin Multiplatform Structure

```
app/
├── android/           # Android application module
├── ios/              # iOS application (Xcode project)
├── desktop/          # Desktop JVM application
└── common/           # Shared multiplatform code
    ├── commonMain/   # Shared UI and business logic
    │   └── App.kt    # Main Compose UI with button grid and log view
    ├── androidMain/  # Android-specific implementations
    ├── iosMain/      # iOS-specific implementations
    └── desktopMain/  # Desktop-specific implementations
```

### expect/actual Pattern

The app uses Kotlin's `expect/actual` pattern for platform-specific code:

```kotlin
// commonMain/App.kt (expect declarations)
expect fun getCurrentTimestamp(): String
expect fun callNativeSetDebugLog(enable: Boolean): String

// androidMain/platform.kt (actual implementations)
actual fun getCurrentTimestamp(): String { /* Android impl */ }
actual fun callNativeSetDebugLog(enable: Boolean): String { /* JNI call */ }

// iosMain/Platform.kt (actual implementations)
actual fun getCurrentTimestamp(): String { /* iOS impl */ }
actual fun callNativeSetDebugLog(enable: Boolean): String { /* Obj-C call */ }

// desktopMain/platform.kt (actual implementations)
actual fun getCurrentTimestamp(): String { /* Desktop impl */ }
actual fun callNativeSetDebugLog(enable: Boolean): String { /* JNI call */ }
```

## 🚀 Quick Start

### Prerequisites

- **Android**: Android Studio Arctic Fox or later, Android SDK
- **iOS**: Xcode 14+, macOS
- **Desktop**: JDK 11 or later

### Build & Run

#### Android
```bash
cd android
./gradlew installDebug
# Or open android/ in Android Studio and run
```

#### iOS
```bash
cd ios
pod install
open iosApp.xcworkspace
# Run in Xcode
```

#### Desktop
```bash
./gradlew :desktop:run
```

## 🔌 Adding Native Integration

### Step 1: Enable Native Module

Edit `settings.gradle.kts`:

```kotlin
// Uncomment these lines:
include(":native_lib")
project(":native_lib").projectDir = file("../../android/android_native_stub")
```

### Step 2: Update Android Module

Edit `android/build.gradle.kts` to add dependency:

```kotlin
dependencies {
    implementation(project(":native_lib"))
}
```

### Step 3: Implement Native Calls

Edit platform-specific files:

**Android** (`androidMain/platform.kt`):
```kotlin
actual fun callNativeSetDebugLog(enable: Boolean): String {
    return try {
        {{cpy_project_name|capitalize|replace('comm', 'Comm')}}Jni.setDebugLog(enable)
        "Debug logging ${if (enable) "enabled" else "disabled"}"
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}
```

**iOS** (`iosMain/Platform.kt`):
```kotlin
actual fun callNativeSetDebugLog(enable: Boolean): String {
    return try {
        {{cpy_project_name|capitalize|replace('comm', 'Comm')}}.setDebugLog(enable)
        "Debug logging ${if (enable) "enabled" else "disabled"}"
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}
```

## ➕ Adding Custom Test Buttons

To add new test buttons, edit `common/src/commonMain/kotlin/com/mojet/app/sample/common/App.kt`:

```kotlin
val testButtons = remember {
    listOf(
        // ... existing buttons ...

        // Add your custom button here:
        TestButton(
            id = "my_custom_test",
            label = "My Test",
            onClick = {
                // Your test logic here
                "Custom test executed successfully!"
            }
        )
    )
}
```

The button will automatically appear in the grid. No layout changes needed!

## 📖 Key Features

### 1. **Easy Button Management**
- Add buttons by adding entries to the `testButtons` list
- No XML layouts - pure Compose
- Automatic grid layout (3 columns)

### 2. **Automatic Logging**
- All button clicks automatically log to the bottom section
- Timestamps included
- Auto-scroll to latest log entry

### 3. **Cross-Platform**
- Single codebase for UI logic
- Platform-specific implementations only where needed
- Seamless native integration

### 4. **Native Ready**
- Pre-configured for CCGO native library integration
- Example `SetDebugLog` integration included
- TODO comments guide you to implement actual native calls

## 🔧 Configuration

### Gradle Properties

Edit `gradle.properties` to configure:
- Kotlin version
- Compose version
- AGP (Android Gradle Plugin) version
- Target SDK versions

### App Name

Edit `android/src/main/AndroidManifest.xml`:
```xml
<application android:label="Your App Name">
```

### Package Name

Edit `android/build.gradle.kts`:
```kotlin
namespace = "com.your.package"
```

## 📚 Learn More

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [CCGO Documentation](https://github.com/your-org/ccgo)

## 🐛 Troubleshooting

### "Unresolved reference: LazyVerticalGrid"
Ensure you're using Compose version 1.2.0 or later.

### Native library not loading
1. Check that `android_native_stub` module is included
2. Verify native library is built (`ccgo build android`)
3. Check dependency in `android/build.gradle.kts`

### iOS build fails
1. Run `pod install` in `ios/` directory
2. Clean build folder in Xcode
3. Check CocoaPods version (1.11+)

## 📄 License

This sample application is part of the CCGO project and follows the same license.

---

**Generated by CCGO** - Cross-platform C++ build system
