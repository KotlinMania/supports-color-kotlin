// port-lint: tests tests/cached.rs
package io.github.kotlinmania.supportscolor

import kotlin.concurrent.thread
import kotlin.test.Test

class CachedTest {
    @Test
    fun cachedMultithreaded() {
        (0..<12)
            .map {
                thread {
                    for (ignored in 0..<1000) {
                        onCached(Stream.Stdout)
                    }
                }
            }.forEach { thread -> thread.join() }
    }
}
