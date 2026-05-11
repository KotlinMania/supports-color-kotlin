// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal fun checkColorterm16m(colorterm: String): Boolean =
    colorterm == "truecolor" || colorterm == "24bit"
