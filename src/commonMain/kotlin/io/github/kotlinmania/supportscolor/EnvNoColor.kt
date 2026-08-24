// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal fun envNoColor(env: (String) -> String? = { envVar(it) }): Boolean =
    when (asStr(env("NO_COLOR"))) {
        "0", null -> false
        else -> true
    }
