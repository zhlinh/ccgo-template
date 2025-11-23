import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group "com.mojet.app.sample"
version = "1.0-SNAPSHOT"


kotlin {
    // JVM target for Windows (MSVC compatibility)
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }

    // Native targets for macOS and Linux
    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting

        // Native desktop targets (macOS, Linux)
        val macosX64Main by getting
        val macosArm64Main by getting
        val linuxX64Main by getting
        val linuxArm64Main by getting

        // Shared native source set
        val nativeMain by creating {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
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

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KMPTemplate"
            packageVersion = "1.0.0"
        }
    }
}
