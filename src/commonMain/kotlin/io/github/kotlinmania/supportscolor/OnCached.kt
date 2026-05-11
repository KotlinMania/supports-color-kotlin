// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

// Compile-time assertion that the below indexing will never panic
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
