// port-lint: source src/lib.rs
package io.github.kotlinmania.supportscolor

/**
 * Detects whether a terminal supports color, and gives details about that
 * support. It takes into account the `NO_COLOR` environment variable.
 *
 * This module is a Kotlin Multiplatform port of [@sindresorhus](https://github.com/sindresorhus)'
 * [NPM package by the same name](https://npm.im/supports-color), via the Rust
 * `supports-color` crate.
 *
 * ## Example
 *
 * ```kotlin
 * import io.github.kotlinmania.supportscolor.Stream
 * import io.github.kotlinmania.supportscolor.on
 *
 * val support = on(Stream.Stdout)
 * if (support != null) {
 *     when {
 *         support.has16m -> println("16 million (RGB) colors are supported")
 *         support.has256 -> println("256-bit colors are supported.")
 *         support.hasBasic -> println("Only basic ANSI colors are supported.")
 *     }
 * } else {
 *     println("No color support.")
 * }
 * ```
 */

// Kotlin equivalents of the upstream environment-variable and once-lock standard library
// imports. [envVar] is the success arm of the upstream environment-variable read; null is
// both the not-present and the invalid-unicode arms folded together, matching how the
// upstream consumes them. [envSetVar], [envRemoveVar], and [envVars] mirror the upstream
// environment-variable mutators and the iterator used by the test block at the bottom of
// this file. The upstream once-lock becomes [kotlin.Lazy] at the usage site; see [cache]
// below.
internal expect fun envVar(name: String): String?
internal expect fun envSetVar(name: String, value: String)
internal expect fun envRemoveVar(name: String)
internal expect fun envVars(): Iterable<Pair<String, String>>

/** possible stream sources */
enum class Stream {
    Stdout,
    Stderr,
}

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

internal fun envNoColor(): Boolean {
    return when (asStr(envVar("NO_COLOR"))) {
        "0", null -> false
        else -> true
    }
}

// same as a nullable-string identity passthrough — see the upstream deref-string helper.
// Kept as a function so the call sites read the same as the upstream originals.
internal fun asStr(option: String?): String? = option

internal fun translateLevel(level: Int): ColorLevel? {
    return if (level == 0) {
        null
    } else {
        ColorLevel(
            level = level,
            hasBasic = true,
            has256 = level >= 2,
            has16m = level >= 3,
        )
    }
}

internal expect fun isATty(stream: Stream): Boolean

internal fun supportsColor(stream: Stream): Int {
    val forceColor = envForceColor()
    return if (forceColor > 0) {
        forceColor
    } else if (envNoColor() ||
        asStr(envVar("TERM")) == "dumb" ||
        !(isATty(stream) || envVar("IGNORE_IS_TERMINAL")?.let { it != "0" } == true)
    ) {
        0
    } else if (envVar("COLORTERM")?.let { checkColorterm16m(it) } == true ||
        envVar("TERM")?.let { checkTerm16m(it) } == true ||
        asStr(envVar("TERM_PROGRAM")) == "iTerm.app"
    ) {
        3
    } else if (asStr(envVar("TERM_PROGRAM")) == "Apple_Terminal" ||
        envVar("TERM")?.let { check256Color(it) } == true
    ) {
        2
    } else if (envVar("COLORTERM") != null ||
        checkAnsiColor(envVar("TERM")) ||
        envVar("CLICOLOR")?.let { it != "0" } == true ||
        IsCi.uncached()
    ) {
        1
    } else {
        0
    }
}

internal expect fun checkAnsiColor(term: String?): Boolean

internal fun checkColorterm16m(colorterm: String): Boolean {
    return colorterm == "truecolor" || colorterm == "24bit"
}

internal fun checkTerm16m(term: String): Boolean {
    return term.endsWith("direct") || term.endsWith("truecolor")
}

internal fun check256Color(term: String): Boolean {
    return term.endsWith("256") || term.endsWith("256color")
}

/**
 * Returns a [ColorLevel] if a [Stream] supports terminal colors.
 */
fun on(stream: Stream): ColorLevel? {
    return translateLevel(supportsColor(stream))
}

// Compile-time assertion that the below indexing will never panic:
// the `Stream` enum has exactly two ordinals (0, 1), and `cache` is sized to match,
// so `cache[stream.ordinal]` is in-bounds by construction.
private val cache: Array<Lazy<ColorLevel?>> = arrayOf(
    lazy { translateLevel(supportsColor(Stream.Stdout)) },
    lazy { translateLevel(supportsColor(Stream.Stderr)) },
)

/**
 * Returns a [ColorLevel] if a [Stream] supports terminal colors, caching the result to
 * be returned from then on.
 *
 * If you expect your environment to change between calls, use [on]
 */
fun onCached(stream: Stream): ColorLevel? {
    val streamIndex = stream.ordinal
    return cache[streamIndex].value
}

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
