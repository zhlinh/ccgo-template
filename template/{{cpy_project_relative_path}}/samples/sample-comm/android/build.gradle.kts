plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

group "com.mojet.app.sample"
version "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
}

android {
    compileSdk = 34
    buildToolsVersion = "34.0.0"
    namespace = "com.mojet.app.sample"
    defaultConfig {
        applicationId = "com.mojet.app.sample.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}