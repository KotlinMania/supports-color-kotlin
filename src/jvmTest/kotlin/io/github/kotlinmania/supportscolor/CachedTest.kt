// port-lint: source tests/cached.rs
package io.github.kotlinmania.supportscolor

import kotlin.concurrent.thread
import kotlin.test.Test

class CachedTest {
    @Test
    fun cachedMultithreaded() {
        (0 until 12)
            .map {
                thread {
                    repeat(1000) {
                        onCached(Stream.Stdout)
                    }
                }
            }.forEach { thread -> thread.join() }
    }
}
