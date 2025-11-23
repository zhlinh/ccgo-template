import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("native.cocoapods")
}

group = "com.mojet.app.sample"
version = "1.0-SNAPSHOT"

kotlin {
    android()

    // iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // Desktop Native targets for macOS and Linux
    macosX64()
    macosArm64()

    linuxX64()
    linuxArm64()

    // Desktop JVM target for Windows
    // Windows uses JVM because CCGO builds with MSVC, while Kotlin/Native uses MinGW (incompatible)
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../ios/Podfile")
        framework {
            baseName = "common"
            isStatic = true
        }
        framework {
            baseName = "main_project"
            isStatic = true
        }
    }



    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }

        // Desktop JVM source set (for Windows)
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
        val desktopTest by getting

        // iOS source set
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }

        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }

        // Native desktop targets (macOS, Linux)
        val macosX64Main by getting
        val macosArm64Main by getting
        val linuxX64Main by getting
        val linuxArm64Main by getting

        // Shared native source set for macOS and Linux
        val nativeMain by creating {
            dependsOn(commonMain)
        }

        // macOS source set
        val macosMain by creating {
            dependsOn(nativeMain)
            macosX64Main.dependsOn(this)
            macosArm64Main.dependsOn(this)
        }

        // Linux source set
        val linuxMain by creating {
            dependsOn(nativeMain)
            linuxX64Main.dependsOn(this)
            linuxArm64Main.dependsOn(this)
        }
    }
}


android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    namespace = "com.mojet.app.sample"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}