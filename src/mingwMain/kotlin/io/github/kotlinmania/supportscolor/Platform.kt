// port-lint: ignore (Windows MinGW implementation of env / isatty / cfg(windows) shims for src/lib.rs)
@file:OptIn(ExperimentalForeignApi::class)

package io.github.kotlinmania.supportscolor

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import platform.posix._putenv_s
import platform.posix.getenv
import platform.windows.DWORDVar
import platform.windows.GetConsoleMode
import platform.windows.GetStdHandle
import platform.windows.INVALID_HANDLE_VALUE
import platform.windows.SetEnvironmentVariableA
import platform.windows.STD_ERROR_HANDLE
import platform.windows.STD_OUTPUT_HANDLE

internal actual fun envVar(name: String): String? = getenv(name)?.toKString()

internal actual fun isATty(stream: Stream): Boolean {
    val stdHandle = when (stream) {
        Stream.STDOUT -> STD_OUTPUT_HANDLE
        Stream.STDERR -> STD_ERROR_HANDLE
    }
    val handle = GetStdHandle(stdHandle)
    if (handle == INVALID_HANDLE_VALUE || handle == null) return false
    return memScoped {
        val mode = alloc<DWORDVar>()
        GetConsoleMode(handle, mode.ptr) != 0
    }
}

internal actual val isWindows: Boolean = true

internal actual fun setEnvVar(name: String, value: String) {
    _putenv_s(name, value)
}

internal actual fun clearAllEnvVars() {
    for (name in SUPPORTS_COLOR_ENV_NAMES) SetEnvironmentVariableA(name, null)
    for (name in CI_ENV_NAMES) SetEnvironmentVariableA(name, null)
}
