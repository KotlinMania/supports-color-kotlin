// port-lint: source src/lib.rs
package io.github.kotlinmania.supportscolor

/**
 * Detects whether a terminal supports color, and gives details about that
 * support. It takes into account the `NO_COLOR` environment variable.
 *
 * This module is a Rust port of [@sindresorhus](https://github.com/sindresorhus)'
 * [NPM package by the same name](https://npm.im/supports-color).
 *
 * ## Example
 *
 * ```kotlin
 * import io.github.kotlinmania.supportscolor.Stream
 * import io.github.kotlinmania.supportscolor.on
 *
 * val support = on(Stream.STDOUT)
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

/** possible stream sources */
enum class Stream {
    STDOUT,
    STDERR,
}

internal fun envForceColor(): Int {
    val force = envVar("FORCE_COLOR")
    if (force != null) {
        return when (force) {
            "true", "" -> 1
            "false" -> 0
            else -> minOf(force.toIntOrNull() ?: 1, 3)
        }
    }
    val cliClrForce = envVar("CLICOLOR_FORCE")
    if (cliClrForce != null) {
        return if (cliClrForce != "0") 1 else 0
    }
    return 0
}

internal fun envNoColor(): Boolean = when (envVar("NO_COLOR")) {
    null, "0" -> false
    else -> true
}

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

internal fun supportsColor(stream: Stream): Int {
    val forceColor = envForceColor()
    return if (forceColor > 0) {
        forceColor
    } else if (envNoColor() ||
        envVar("TERM") == "dumb" ||
        !(isATty(stream) || envVar("IGNORE_IS_TERMINAL")?.let { it != "0" } == true)
    ) {
        0
    } else if (envVar("COLORTERM")?.let { checkColorterm16m(it) } == true ||
        envVar("TERM")?.let { checkTerm16m(it) } == true ||
        envVar("TERM_PROGRAM") == "iTerm.app"
    ) {
        3
    } else if (envVar("TERM_PROGRAM") == "Apple_Terminal" ||
        envVar("TERM")?.let { check256Color(it) } == true
    ) {
        2
    } else if (envVar("COLORTERM") != null ||
        checkAnsiColor(envVar("TERM")) ||
        envVar("CLICOLOR")?.let { it != "0" } == true ||
        isCiUncached()
    ) {
        1
    } else {
        0
    }
}

internal fun checkAnsiColor(term: String?): Boolean {
    return if (isWindows) {
        if (term != null) {
            // cygwin doesn't seem to support ANSI escape sequences and instead has its own variety.
            term != "dumb" && term != "cygwin"
        } else {
            // TERM is generally not set on Windows. It's reasonable to assume that all Windows
            // terminals support ANSI escape sequences (since Windows 10 version 1511).
            true
        }
    } else {
        if (term != null) {
            // dumb terminals don't support ANSI escape sequences.
            term != "dumb"
        } else {
            // TERM is not set, which is really weird on Unix systems.
            false
        }
    }
}

internal fun checkColorterm16m(colorterm: String): Boolean =
    colorterm == "truecolor" || colorterm == "24bit"

internal fun checkTerm16m(term: String): Boolean =
    term.endsWith("direct") || term.endsWith("truecolor")

internal fun check256Color(term: String): Boolean =
    term.endsWith("256") || term.endsWith("256color")

/**
 * Returns a [ColorLevel] if a [Stream] supports terminal colors.
 */
fun on(stream: Stream): ColorLevel? = translateLevel(supportsColor(stream))

private val cachedStdout: Lazy<ColorLevel?> = lazy { translateLevel(supportsColor(Stream.STDOUT)) }
private val cachedStderr: Lazy<ColorLevel?> = lazy { translateLevel(supportsColor(Stream.STDERR)) }

/**
 * Returns a [ColorLevel] if a [Stream] supports terminal colors, caching the result to
 * be returned from then on.
 *
 * If you expect your environment to change between calls, use [on]
 */
fun onCached(stream: Stream): ColorLevel? = when (stream) {
    Stream.STDOUT -> cachedStdout.value
    Stream.STDERR -> cachedStderr.value
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
