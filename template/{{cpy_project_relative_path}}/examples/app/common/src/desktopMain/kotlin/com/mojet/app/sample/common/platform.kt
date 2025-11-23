package com.mojet.app.sample.common

import java.text.SimpleDateFormat
import java.util.*

actual fun getPlatformName(): String {
    return "Desktop (${System.getProperty("os.name")})"
}

actual fun getCurrentTimestamp(): String {
    val formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    return formatter.format(Date())
}

actual fun callNativeSetDebugLog(enable: Boolean): String {
    return try {
        // TODO: Implement actual JNI call to native SetDebugLog on Desktop
        // Example:
        // System.loadLibrary("{{cpy_project_name|lower}}")
        // {{cpy_project_name|capitalize|replace('comm', 'Comm')}}Jni.setDebugLog(enable)

        // For now, return a placeholder
        "Native SetDebugLog($enable) - Desktop JNI integration pending"
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}