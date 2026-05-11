// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

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
 *
 * Returns a [ColorLevel] if a [Stream] supports terminal colors.
 */
fun on(stream: Stream): ColorLevel? = translateLevel(supportsColor(stream))
