// port-lint: source lib.rs
@file:OptIn(ExperimentalForeignApi::class)

package io.github.kotlinmania.supportscolor

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.STDERR_FILENO
import platform.posix.STDOUT_FILENO
import platform.posix.getenv
import platform.posix.isatty

internal actual fun envVar(name: String): String? = getenv(name)?.toKString()

internal actual fun isATty(stream: Stream): Boolean = when (stream) {
    Stream.Stdout -> isatty(STDOUT_FILENO) == 1
    Stream.Stderr -> isatty(STDERR_FILENO) == 1
}

internal actual fun checkAnsiColor(term: String?): Boolean {
    return if (term != null) {
        // dumb terminals don't support ANSI escape sequences.
        term != "dumb"
    } else {
        // TERM is not set, which is really weird on Unix systems.
        false
    }
}
