// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal fun envNoColor(): Boolean {
    return when (asStr(envVar("NO_COLOR"))) {
        "0", null -> false
        else -> true
    }
}
