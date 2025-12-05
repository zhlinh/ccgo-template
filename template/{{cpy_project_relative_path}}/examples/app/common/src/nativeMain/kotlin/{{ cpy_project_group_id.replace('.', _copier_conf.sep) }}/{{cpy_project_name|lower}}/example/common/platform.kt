package {{ cpy_project_group_id }}.{{cpy_project_name|lower}}.example.common

import kotlinx.cinterop.*
import platform.posix.*

actual fun getPlatformName(): String {
    return "Native Desktop"
}

actual fun getCurrentTimestamp(): String {
    memScoped {
        val timeVal = alloc<timeval>()
        gettimeofday(timeVal.ptr, null)
        val sec = timeVal.tv_sec
        val usec = timeVal.tv_usec
        val ms = usec / 1000

        val time = alloc<time_tVar>()
        time.value = sec
        val tm = localtime(time.ptr)?.pointed

        return if (tm != null) {
            "%02d:%02d:%02d.%03d".format(
                tm.tm_hour,
                tm.tm_min,
                tm.tm_sec,
                ms.toInt()
            )
        } else {
            "00:00:00.000"
        }
    }
}

actual fun callNativeSetDebugLog(enable: Boolean): String {
    return try {
        // TODO: Implement actual C interop call to native SetDebugLog
        // Example using cinterop:
        // {{cpy_project_name|lower}}.SetDebugLog(enable)

        // For now, return a placeholder
        "Native SetDebugLog($enable) - Native interop integration pending"
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}
