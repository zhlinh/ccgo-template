# Android Native Stub Module

## 📋 Overview

This module is a **bridge module** that enables Android Studio to recognize and work with C++ code in this project. It contains no actual source code - instead, it acts as an adapter between Gradle's Android build system and the project's CMake-based C++ build system.

## 🎯 Purpose

### Why does this module exist?

Android Studio requires a Gradle module with `externalNativeBuild` configuration to:
- **Recognize C++ files** in the project explorer
- **Enable code navigation** (Go to Definition, Find Usages)
- **Provide IntelliSense** (auto-completion, syntax highlighting)
- **Support debugging** (breakpoints, variable inspection)
- **Index native code** for search and refactoring

Without this module, Android Studio would treat the project as Java/Kotlin-only and ignore all C++ code.

### What does it do?

```
┌─────────────────────┐
│  Android Studio     │
│  (Gradle-based)     │
└──────────┬──────────┘
           │
           │ Needs a Gradle module to recognize native code
           ▼
┌─────────────────────┐
│ android_native_stub │ ◄── This module acts as a bridge
│   (This Module)     │
└──────────┬──────────┘
           │
           │ Points to CMakeLists.txt via externalNativeBuild
           ▼
┌─────────────────────┐
│  CMakeLists.txt     │
│  (Root project)     │
│  + src/, include/   │
└─────────────────────┘
```

## 🏗️ Architecture

### Module Structure

```
android_native_stub/
├── build.gradle.kts        # Gradle configuration with CMake integration
├── proguard-rules.pro      # ProGuard rules (standard template)
├── .gitignore              # Ignores build artifacts
├── README.md               # This file
└── src/main/
    └── AndroidManifest.xml # Minimal Android manifest
```

### Key Configuration

The `build.gradle.kts` file:

```kotlin
plugins {
    // Uses CCGO's convention plugin for native stub modules
    alias(libs.plugins.ccgo.android.library.native.empty)
}

android {
    defaultConfig {
        externalNativeBuild {
            cmake {
                // Points to the project root's CMakeLists.txt
                // Enables GoogleTest and Benchmark support
                arguments("-DGOOGLETEST_SUPPORT=ON", "-DBENCHMARK_SUPPORT=ON")
            }
        }
    }
}
```

## 📂 File Descriptions

| File | Purpose |
|------|---------|
| `build.gradle.kts` | Configures Gradle to recognize CMake-based native builds |
| `AndroidManifest.xml` | Declares minimal Android permissions (network, WiFi) |
| `proguard-rules.pro` | Standard ProGuard configuration template |
| `.gitignore` | Prevents build artifacts from being committed |
| `README.md` | Documentation (this file) |

## ⚠️ Important Notes

### Can I delete this module?

**No.** Removing this module will cause:
- ❌ Android Studio to stop recognizing C++ files
- ❌ Loss of code navigation and IntelliSense
- ❌ Inability to debug native code from Android Studio
- ❌ Build errors when opening the project in Android Studio

### Can I add source code here?

**Not recommended.** This module is intentionally kept minimal. All C++ source code should be in:
- `../../src/` - Implementation files
- `../../include/` - Header files
- `../../tests/` - Unit tests

These are managed by the root `CMakeLists.txt`, not by this module.

### Why is it called "stub"?

In software engineering, a **stub** is a minimal implementation that acts as a placeholder or bridge. This module:
- Contains no actual implementation (it's "empty")
- Acts as a bridge between two build systems (Gradle ↔ CMake)
- Provides the minimum configuration needed for IDE recognition

## 🔧 Customization

### Adding CMake Arguments

To pass additional CMake flags, edit `build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        externalNativeBuild {
            cmake {
                arguments(
                    "-DGOOGLETEST_SUPPORT=ON",
                    "-DBENCHMARK_SUPPORT=ON",
                    "-DCUSTOM_FLAG=VALUE"  // Add your flag here
                )
            }
        }
    }
}
```

### Changing Target ABIs

The supported ABIs are configured in the CCGO Gradle plugin. By default:
- `armeabi-v7a` (32-bit ARM)
- `arm64-v8a` (64-bit ARM)
- `x86_64` (64-bit x86 for emulators)

To override, use `build_config.py` in the project root.

### Adding Permissions

To add Android permissions, edit `src/main/AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- Add more permissions here -->
</manifest>
```

## 🛠️ How CCGO Uses This Module

When you run:
```bash
ccgo build android
```

The build process:
1. Invokes `build_android.py` from the project root
2. `build_android.py` uses CMake to compile C++ code
3. Gradle uses this module's configuration to integrate the CMake build
4. Android Studio reads this module to enable IDE features

## 📚 Related Files

- **Root CMakeLists.txt**: `../../CMakeLists.txt`
- **Build script**: `../../build_android.py`
- **Build configuration**: `../../build_config.py`
- **Main Android module**: `../main_android_sdk/`
- **CCGO Gradle plugin**: Defined in `ccgo-gradle-plugins` repository

## 🤔 FAQ

### Q: Why not put CMake configuration in `main_android_sdk`?
**A:** Separation of concerns:
- `main_android_sdk` - Handles Android-specific packaging (AAR, Maven publishing)
- `android_native_stub` - Handles C++ recognition and CMake integration

This allows `main_android_sdk` to focus on Java/Kotlin code and packaging, while this module handles native build configuration.

### Q: Can I rename this module?
**A:** Yes, but you must update:
1. Directory name: `android/android_native_stub/`
2. Module name in `android/settings.gradle.kts`
3. Any references in example projects

### Q: Does this module affect the final APK/AAR size?
**A:** No. This module produces no output artifacts. The actual native libraries are built by CMake and included in `main_android_sdk`.

### Q: Why use a convention plugin instead of configuring directly?
**A:** The `ccgo.android.library.native.empty` plugin:
- Standardizes configuration across CCGO projects
- Automatically applies necessary plugins (Android, Kotlin, Lint)
- Configures CMake paths and build settings
- Simplifies updates when CCGO evolves

## 📖 Learn More

- [CCGO Documentation](https://github.com/your-org/ccgo)
- [Android CMake Guide](https://developer.android.com/ndk/guides/cmake)
- [Gradle Native Build](https://developer.android.com/studio/projects/gradle-external-native-builds)

---

**Generated by CCGO** - Cross-platform C++ build system
