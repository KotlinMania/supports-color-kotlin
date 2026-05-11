// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal expect fun checkAnsiColor(term: String?): Boolean
