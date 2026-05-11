// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

/**
 * Color level support details.
 *
 * This type is returned from [on]. See documentation for its fields for more details.
 */
@ConsistentCopyVisibility
data class ColorLevel internal constructor(
    internal val level: Int,
    /** Basic ANSI colors are supported. */
    val hasBasic: Boolean,
    /** 256-bit colors are supported. */
    val has256: Boolean,
    /** 16 million (RGB) colors are supported. */
    val has16m: Boolean,
)
