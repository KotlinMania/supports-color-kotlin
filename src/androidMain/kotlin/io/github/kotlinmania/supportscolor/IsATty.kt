// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal actual fun isATty(stream: Stream): Boolean = System.console() != null
