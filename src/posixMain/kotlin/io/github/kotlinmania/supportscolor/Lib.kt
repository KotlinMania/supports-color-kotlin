// port-lint: source src/lib.rs
@file:OptIn(ExperimentalForeignApi::class)

package io.github.kotlinmania.supportscolor

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.STDERR_FILENO
import platform.posix.STDOUT_FILENO
import platform.posix.getenv
import platform.posix.isatty
import platform.posix.setenv
import platform.posix.unsetenv

internal actual fun envVar(name: String): String? = getenv(name)?.toKString()

internal actual fun envSetVar(name: String, value: String) {
    setenv(name, value, 1)
}

internal actual fun envRemoveVar(name: String) {
    unsetenv(name)
}

// On POSIX, the upstream environment-variable iterator walks the C library's
// environment-block pointer. Kotlin Native cinterop does not portably surface that
// pointer; for the test block, callers iterate the recognised supports-color and
// continuous-integration env-var names instead. See the loop in the test source set.
internal actual fun envVars(): Iterable<Pair<String, String>> = emptyList()

internal actual fun isATty(stream: Stream): Boolean = when (stream) {
    Stream.Stdout -> isatty(STDOUT_FILENO) == 1
    Stream.Stderr -> isatty(STDERR_FILENO) == 1
}

// Non-Windows variant of the upstream cfg-gated `checkAnsiColor`.
internal actual fun checkAnsiColor(term: String?): Boolean {
    return if (term != null) {
        // dumb terminals don't support ANSI escape sequences.
        term != "dumb"
    } else {
        // TERM is not set, which is really weird on Unix systems.
        false
    }
}
