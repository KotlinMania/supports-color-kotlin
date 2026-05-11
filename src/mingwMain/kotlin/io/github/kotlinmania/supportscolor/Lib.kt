// port-lint: source lib.rs
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
import platform.windows.STD_ERROR_HANDLE
import platform.windows.STD_OUTPUT_HANDLE

internal actual fun envVar(name: String): String? = getenv(name)?.toKString()

internal actual fun envSetVar(name: String, value: String) {
    _putenv_s(name, value)
}

internal actual fun envRemoveVar(name: String) {
    // MSVCRT: calling [_putenv_s] with an empty value is the documented "remove" form, and
    // clears the variable from the CRT env table that [getenv] reads.
    _putenv_s(name, "")
}

// MSVCRT cinterop does not portably surface the environment-block pointer used by the
// upstream environment-variable iterator; the test block relies on explicit re-zeroing of
// the named env vars supports-color and IsCi consult.
internal actual fun envVars(): Iterable<Pair<String, String>> = emptyList()

internal actual fun isATty(stream: Stream): Boolean {
    val stdHandle = when (stream) {
        Stream.Stdout -> STD_OUTPUT_HANDLE
        Stream.Stderr -> STD_ERROR_HANDLE
    }
    val handle = GetStdHandle(stdHandle)
    if (handle == INVALID_HANDLE_VALUE || handle == null) return false
    return memScoped {
        val mode = alloc<DWORDVar>()
        GetConsoleMode(handle, mode.ptr) != 0
    }
}

// Windows variant of the upstream cfg-gated `checkAnsiColor`.
internal actual fun checkAnsiColor(term: String?): Boolean {
    return if (term != null) {
        // cygwin doesn't seem to support ANSI escape sequences and instead has its own variety.
        term != "dumb" && term != "cygwin"
    } else {
        // TERM is generally not set on Windows. It's reasonable to assume that all Windows
        // terminals support ANSI escape sequences (since Windows 10 version 1511).
        true
    }
}
