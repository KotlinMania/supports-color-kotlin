// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal fun checkTerm16m(term: String): Boolean =
    term.endsWith("direct") || term.endsWith("truecolor")
