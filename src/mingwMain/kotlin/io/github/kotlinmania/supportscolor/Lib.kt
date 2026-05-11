// port-lint: source lib.rs
@file:OptIn(ExperimentalForeignApi::class)

package io.github.kotlinmania.supportscolor

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import platform.posix.getenv
import platform.windows.DWORDVar
import platform.windows.GetConsoleMode
import platform.windows.GetStdHandle
import platform.windows.INVALID_HANDLE_VALUE
import platform.windows.STD_ERROR_HANDLE
import platform.windows.STD_OUTPUT_HANDLE

internal actual fun envVar(name: String): String? = getenv(name)?.toKString()

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
