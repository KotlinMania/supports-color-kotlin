// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal actual fun checkAnsiColor(term: String?): Boolean =
    if (term != null) {
        // cygwin doesn't seem to support ANSI escape sequences and instead has its own variety.
        term != "dumb" && term != "cygwin"
    } else {
        // TERM is generally not set on Windows. It's reasonable to assume that all Windows
        // terminals support ANSI escape sequences (since Windows 10 version 1511).
        true
    }
