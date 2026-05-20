// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

private val isWindowsRuntime: Boolean =
    System.getProperty("os.name").startsWith("Windows", ignoreCase = true)

internal actual fun checkAnsiColor(term: String?): Boolean {
    return if (term != null) {
        if (isWindowsRuntime) {
            // cygwin doesn't seem to support ANSI escape sequences and instead has its own variety.
            term != "dumb" && term != "cygwin"
        } else {
            // dumb terminals don't support ANSI escape sequences.
            term != "dumb"
        }
    } else {
        if (isWindowsRuntime) {
            // TERM is generally not set on Windows. It's reasonable to assume that all Windows
            // terminals support ANSI escape sequences (since Windows 10 version 1511).
            true
        } else {
            // TERM is not set, which is really weird on Unix systems.
            false
        }
    }
}
