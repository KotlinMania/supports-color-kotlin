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
