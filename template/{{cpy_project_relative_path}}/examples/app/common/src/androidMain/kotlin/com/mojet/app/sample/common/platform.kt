package com.mojet.app.sample.common

import java.text.SimpleDateFormat
import java.util.*

actual fun getPlatformName(): String {
    return "Android"
}

actual fun getCurrentTimestamp(): String {
    val formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    return formatter.format(Date())
}

actual fun callNativeSetDebugLog(enable: Boolean): String {
    return try {
        // TODO: Implement actual JNI call to native SetDebugLog
        // Example:
        // {{cpy_project_name|capitalize|replace('comm', 'Comm')}}Jni.setDebugLog(enable)

        // For now, return a placeholder
        "Native SetDebugLog($enable) - JNI integration pending"
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}