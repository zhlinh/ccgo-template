//
// Copyright 2024 zhlinh and ccgo Project Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found at
//
// https://opensource.org/license/MIT
//
// The above copyright notice and this permission
// notice shall be included in all copies or
// substantial portions of the Software.

val rootProjectName = rootDir.parentFile.absolutePath.split("/").last().lowercase()
println("rootProjectName: $rootProjectName")
rootProject.name = rootProjectName

pluginManagement {
    repositories {
        // Use mavenLocal for testing CCGO Gradle plugins
        mavenLocal()

        // Use remote Maven repository for production
        // maven {
        //     url = uri("https://your-maven-repo.com/releases")
        // }

        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// Main Android SDK module - contains the primary library code
include(":main_android_sdk")

// Native stub module - enables Android Studio to recognize C++ code and provides CMake integration
// This module contains no source code itself; it acts as a bridge between Gradle and the project's
// CMakeLists.txt, allowing IDE features like code navigation, IntelliSense, and debugging to work.
// WARNING: Do not remove this module - without it, Android Studio cannot recognize C++ files.
include(":android_native_stub")

