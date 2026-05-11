// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal fun envForceColor(): Int {
    val force = envVar("FORCE_COLOR")
    return if (force != null) {
        when (force) {
            "true", "" -> 1
            "false" -> 0
            else -> minOf(force.toIntOrNull() ?: 1, 3)
        }
    } else {
        val cliClrForce = envVar("CLICOLOR_FORCE")
        if (cliClrForce != null) {
            if (cliClrForce != "0") {
                1
            } else {
                0
            }
        } else {
            0
        }
    }
}
