//
//  Copyright 2024 zhlinh and ccgo Project Authors. All rights reserved.
//  Use of this source code is governed by a MIT-style
//  license that can be found at
//
//  https://opensource.org/license/MIT
//
//  The above copyright notice and this permission
//  notice shall be included in all copies or
//  substantial portions of the Software.

// ============================================================================
// Android Native Stub Module
// ============================================================================
// This module enables Android Studio to recognize C++ code in the project.
// It contains no source code itself - it's a bridge between Gradle and CMake.
//
// Purpose:
// - Configures Gradle's externalNativeBuild to point to the root CMakeLists.txt
// - Enables C++ code navigation, IntelliSense, and debugging in Android Studio
// - Provides CMake configuration for GoogleTest and Benchmark support
//
// ⚠️ Do not remove this module - it's required for IDE C++ recognition.
// ============================================================================

plugins {
    // CCGO convention plugin for native stub modules
    // This plugin automatically:
    // - Applies Android library plugin
    // - Applies Kotlin Android plugin
    // - Configures CMake to use the root project's CMakeLists.txt
    // - Sets up NDK, ABI filters, and compiler flags
    alias(libs.plugins.ccgo.android.library.native.empty)
}

android {
    defaultConfig {
        externalNativeBuild {
            cmake {
                // Pass CMake arguments to enable test and benchmark support
                // These flags are read by the root CMakeLists.txt to include:
                // - GoogleTest framework (for unit tests in tests/ directory)
                // - Google Benchmark framework (for performance tests in benches/ directory)
                arguments("-DGOOGLETEST_SUPPORT=ON", "-DBENCHMARK_SUPPORT=ON")

                // Additional CMake arguments can be added here, for example:
                // arguments("-DCUSTOM_FLAG=VALUE", "-DENABLE_FEATURE=ON")
            }
        }
    }
}
