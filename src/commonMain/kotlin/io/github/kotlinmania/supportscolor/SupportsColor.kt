package io.github.kotlinmania.supportscolor

import io.github.kotlinmania.libc.new.common.posix.STDERR_FILENO
import io.github.kotlinmania.libc.new.common.posix.STDOUT_FILENO
import io.github.kotlinmania.libc.unix.getenv
import io.github.kotlinmania.libc.unix.isatty
import kotlin.concurrent.atomics.AtomicReference

private fun envForceColor(): Int {
    val force = getenv("FORCE_COLOR")
    return if (force != null) {
        when (force) {
            "true", "" -> 1
            "false" -> 0
            else -> minOf(force.toIntOrNull() ?: 1, 3)
        }
    } else {
        val cliClrForce = getenv("CLICOLOR_FORCE")
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

private fun envNoColor(): Boolean {
    val noColor = getenv("NO_COLOR") ?: return false
    return noColor != "0"
}

private fun translateLevel(level: Int): ColorLevel? =
    if (level == 0) {
        null
    } else {
        ColorLevel(
            level,
            hasBasic = true,
            has256 = level >= 2,
            has16m = level >= 3,
        )
    }

private fun isATty(stream: Stream): Boolean {
    val fd =
        when (stream) {
            Stream.Stdout -> STDOUT_FILENO
            Stream.Stderr -> STDERR_FILENO
        }
    return isatty(fd) != 0
}

private fun supportsColor(stream: Stream): Int {
    val forceColor = envForceColor()
    return if (forceColor > 0) {
        forceColor
    } else if (
        envNoColor() ||
        getenv("TERM") == "dumb" ||
        !(isATty(stream) || getenv("IGNORE_IS_TERMINAL")?.let { it != "0" } == true)
    ) {
        0
    } else if (
        getenv("COLORTERM")?.let { checkColorterm16m(it) } == true ||
        getenv("TERM")?.let { checkTerm16m(it) } == true ||
        getenv("TERM_PROGRAM") == "iTerm.app"
    ) {
        3
    } else if (
        getenv("TERM_PROGRAM") == "Apple_Terminal" ||
        getenv("TERM")?.let { check256Color(it) } == true
    ) {
        2
    } else if (
        getenv("COLORTERM") != null ||
        checkAnsiColor(getenv("TERM")) ||
        getenv("CLICOLOR")?.let { it != "0" } == true ||
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

internal class OnceLock<T> {
    private class Box<T>(
        val value: T,
    )

    private val ref = AtomicReference<Box<T>?>(null)

    fun getOrInit(init: () -> T): T {
        val current = ref.load()
        if (current != null) {
            return current.value
        }
        val created = Box(init())
        ref.compareAndSet(expectedValue = null, newValue = created)
        return (ref.load() ?: created).value
    }

    fun reset() {
        ref.store(null)
    }

    companion object {
        fun <T> new(): OnceLock<T> = OnceLock()
    }
}

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
