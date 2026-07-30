// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal fun translateLevel(level: Int): ColorLevel? =
    if (level == 0) {
        null
    } else {
        ColorLevel(
            level = level,
            hasBasic = true,
            has256 = level >= 2,
            has16m = level >= 3,
        )
    }
