// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal fun check256Color(term: String): Boolean =
    term.endsWith("256") || term.endsWith("256color")
