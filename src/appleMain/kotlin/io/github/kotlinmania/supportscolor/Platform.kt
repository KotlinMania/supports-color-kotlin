// port-lint: ignore (Apple POSIX implementation of env / isatty / cfg(windows) shims for src/lib.rs)
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

internal actual fun isATty(stream: Stream): Boolean = when (stream) {
    Stream.STDOUT -> isatty(STDOUT_FILENO) == 1
    Stream.STDERR -> isatty(STDERR_FILENO) == 1
}

internal actual val isWindows: Boolean = false

internal actual fun setEnvVar(name: String, value: String) {
    setenv(name, value, 1)
}

internal actual fun clearAllEnvVars() {
    for (name in SUPPORTS_COLOR_ENV_NAMES) unsetenv(name)
    for (name in CI_ENV_NAMES) unsetenv(name)
}
