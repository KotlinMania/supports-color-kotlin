// port-lint: source lib.rs

/**
 * Detects whether a terminal supports color, and gives details about that
 * support. It takes into account the `NO_COLOR` environment variable.
 *
 * This crate is a Kotlin port of [@sindresorhus](https://github.com/sindresorhus)'
 * [NPM package by the same name](https://npm.im/supports-color).
 *
 * ## Example
 *
 * ```kotlin
 * import io.github.kotlinmania.supportscolor.Stream
 * import io.github.kotlinmania.supportscolor.on
 *
 * val support = on(Stream.Stdout)
 * if (support != null) {
 *     if (support.has16m) {
 *         println("16 million (RGB) colors are supported")
 *     } else if (support.has256) {
 *         println("256-bit colors are supported.")
 *     } else if (support.hasBasic) {
 *         println("Only basic ANSI colors are supported.")
 *     }
 * } else {
 *     println("No color support.")
 * }
 * ```
 */
package io.github.kotlinmania.supportscolor

/**
 * possible stream sources
 *
 * Identifies whether terminal color support is being queried for standard
 * output or standard error.
 */
public enum class Stream {
    /** Standard output stream. */
    Stdout,

    /** Standard error stream. */
    Stderr,
}

private fun envForceColor(): Int {
    val force = env.`var`("FORCE_COLOR")
    return if (force is Ok) {
        when (val f = force.inner.asRef()) {
            "true", "" -> 1
            "false" -> 0
            else -> std.cmp.min(f.parse().unwrapOr(1), 3)
        }
    } else {
        val cliClrForce = env.`var`("CLICOLOR_FORCE")
        if (cliClrForce is Ok) {
            if (cliClrForce.inner != "0") {
                1
            } else {
                0
            }
        } else {
            0
        }
    }
}

private fun envNoColor(): Boolean =
    when (val res = asStr(env.`var`("NO_COLOR"))) {
        Ok("0"), is Err -> false
        is Ok -> res.inner.isNotEmpty() || true
    }

// same as Option.asDeref
private fun <E> asStr(option: Result<String, E>): Result<String, E> =
    when (option) {
        is Ok -> {
            val inner = option.inner
            Ok(inner)
        }

        is Err -> {
            val e = option.e
            Err(e)
        }
    }

private fun translateLevel(level: Int): ColorLevel? =
    if (level == 0) {
        None
    } else {
        Some(
            ColorLevel(
                level,
                hasBasic = true,
                has256 = level >= 2,
                has16m = level >= 3,
            ),
        )
    }

private fun isATty(stream: Stream): Boolean =
    when (stream) {
        Stream.Stdout -> std.io.stdout().isTerminal()
        Stream.Stderr -> std.io.stderr().isTerminal()
    }

private fun supportsColor(stream: Stream): Int {
    val forceColor = envForceColor()
    return if (forceColor > 0) {
        forceColor
    } else if (
        envNoColor() ||
        asStr(env.`var`("TERM")) == Ok("dumb") ||
        !(isATty(stream) || env.`var`("IGNORE_IS_TERMINAL").mapOr(false) { v -> v != "0" })
    ) {
        0
    } else if (
        env.`var`("COLORTERM").map { colorterm -> checkColorterm16m(colorterm) } == Ok(true) ||
        env.`var`("TERM").map { term -> checkTerm16m(term) } == Ok(true) ||
        asStr(env.`var`("TERM_PROGRAM")) == Ok("iTerm.app")
    ) {
        3
    } else if (
        asStr(env.`var`("TERM_PROGRAM")) == Ok("Apple_Terminal") ||
        env.`var`("TERM").map { term -> check256Color(term) } == Ok(true)
    ) {
        2
    } else if (
        env.`var`("COLORTERM").isOk() ||
        checkAnsiColor(env.`var`("TERM").ok().asDeref()) ||
        env.`var`("CLICOLOR").mapOr(false) { v -> v != "0" } ||
        isCi.uncached()
    ) {
        1
    } else {
        0
    }
}

internal fun checkAnsiColor(term: CharSequence?): Boolean =
    if (term != null) {
        // cygwin doesn't seem to support ANSI escape sequences and instead has its own variety.
        term != "dumb" && term != "cygwin"
    } else {
        // TERM is generally not set on Windows. It's reasonable to assume that all Windows
        // terminals support ANSI escape sequences (since Windows 10 version 1511).
        true
    }

private fun checkAnsiColor(term: String?): Boolean =
    if (term != null) {
        // dumb terminals don't support ANSI escape sequences.
        term != "dumb"
    } else {
        // TERM is not set, which is really weird on Unix systems.
        false
    }

private fun checkColorterm16m(colorterm: String): Boolean =
    colorterm == "truecolor" || colorterm == "24bit"

private fun checkTerm16m(term: String): Boolean =
    term.endsWith("direct") || term.endsWith("truecolor")

private fun check256Color(term: String): Boolean =
    term.endsWith("256") || term.endsWith("256color")

/**
 * Returns a [ColorLevel] if a [Stream] supports terminal colors.
 */
public fun on(stream: Stream): ColorLevel? = translateLevel(supportsColor(stream))

internal val CACHE: Array<OnceLock<ColorLevel?>> = arrayOf(OnceLock.new(), OnceLock.new())

/**
 * Returns a [ColorLevel] if a [Stream] supports terminal colors, caching the result to
 * be returned from then on.
 *
 * If you expect your environment to change between calls, use [on]
 */
public fun onCached(stream: Stream): ColorLevel? {
    val cache: Array<OnceLock<ColorLevel?>> =
        CACHE.ifEmpty { arrayOf(OnceLock.new(), OnceLock.new()) }
    val streamIndex = stream.ordinal
    return cache[streamIndex].getOrInit { translateLevel(supportsColor(stream)) }
}

/**
 * Color level support details.
 *
 * This type is returned from [on]. See documentation for its fields for more details.
 */
@ConsistentCopyVisibility
public data class ColorLevel internal constructor(
    /** Numeric color level supported by the stream. */
    internal val level: Int,
    /**
     * Basic ANSI colors are supported.
     */
    public val hasBasic: Boolean,
    /**
     * 256-bit colors are supported.
     */
    public val has256: Boolean,
    /**
     * 16 million (RGB) colors are supported.
     */
    public val has16m: Boolean,
)

internal fun setUp() {
    // clears process env variable
    env.vars().forEach { (k, _) -> env.removeVar(k) }
}
