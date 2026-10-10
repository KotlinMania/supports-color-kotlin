// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal actual fun checkAnsiColor(term: String?): Boolean =
    if (term != null) {
        // dumb terminals don't support ANSI escape sequences.
        term != "dumb"
    } else {
        // TERM is not set, which is really weird on Unix systems.
        false
    }
