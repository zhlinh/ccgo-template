package {{ cpy_project_group_id }}.{{cpy_project_name|lower}}.example.common

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual fun getPlatformName(): String {
    return "iOS"
}

actual fun getCurrentTimestamp(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "HH:mm:ss.SSS"
    return formatter.stringFromDate(NSDate())
}

actual fun callNativeSetDebugLog(enable: Boolean): String {
    return try {
        // TODO: Implement actual Objective-C/Swift call to native SetDebugLog
        // Example:
        // {{cpy_project_name|capitalize|replace('comm', 'Comm')}}.setDebugLog(enable)

        // For now, return a placeholder
        "Native SetDebugLog($enable) - iOS integration pending"
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}