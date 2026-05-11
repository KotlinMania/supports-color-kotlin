// port-lint: source lib.rs
@file:OptIn(ExperimentalForeignApi::class)

package io.github.kotlinmania.supportscolor

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.STDERR_FILENO
import platform.posix.STDOUT_FILENO
import platform.posix.isatty

internal actual fun isATty(stream: Stream): Boolean = when (stream) {
    Stream.Stdout -> isatty(STDOUT_FILENO) == 1
    Stream.Stderr -> isatty(STDERR_FILENO) == 1
}
