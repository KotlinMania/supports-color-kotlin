package io.github.kotlinmania.supportscolor

/**
 * Color level support details.
 *
 * This type is returned from [on]. See documentation for its fields for more details.
 */
@ConsistentCopyVisibility
public data class ColorLevel internal constructor(
    /** Numeric color level supported by the stream. */
    internal val level: Int,
    /** Basic ANSI colors are supported. */
    public val hasBasic: Boolean,
    /** 256-bit colors are supported. */
    public val has256: Boolean,
    /** 16 million (RGB) colors are supported. */
    public val has16m: Boolean,
)
