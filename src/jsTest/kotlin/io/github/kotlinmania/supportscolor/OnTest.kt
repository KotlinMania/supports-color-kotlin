// port-lint: tests lib.rs
package io.github.kotlinmania.supportscolor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private fun setUp() {
    resetCacheForTesting()
    val keys = jsEnvKeys()
    val length = keys.length
    for (i in 0 until length) {
        jsDeleteEnv(keys[i].unsafeCast<String>())
    }
}

class OnTest {
    @Test
    fun testEmptyEnv() {
        setUp()

        assertNull(on(Stream.Stdout))
    }

    @Test
    fun testClicolorAnsi() {
        setUp()

        jsSetEnv("IGNORE_IS_TERMINAL", "1")
        jsSetEnv("CLICOLOR", "1")
        val expected =
            ColorLevel(
                level = 1,
                hasBasic = true,
                has256 = false,
                has16m = false,
            )
        assertEquals(expected, on(Stream.Stdout))

        jsSetEnv("CLICOLOR", "0")
        assertNull(on(Stream.Stdout))
    }

    @Test
    fun testOnCached() {
        setUp()
        jsSetEnv("IGNORE_IS_TERMINAL", "1")

        jsSetEnv("CLICOLOR", "1")
        assertNotNull(on(Stream.Stdout))
        assertNotNull(onCached(Stream.Stdout))

        jsSetEnv("CLICOLOR", "0")
        assertNull(on(Stream.Stdout))
        assertNotNull(onCached(Stream.Stdout))
        resetCacheForTesting()
    }

    @Test
    fun testClicolorForceAnsi() {
        setUp()

        jsSetEnv("CLICOLOR", "0")
        jsSetEnv("CLICOLOR_FORCE", "1")
        val expected =
            ColorLevel(
                level = 1,
                hasBasic = true,
                has256 = false,
                has16m = false,
            )
        assertEquals(expected, on(Stream.Stdout))
    }
}

private fun jsSetEnv(name: String, value: String): Unit =
    js(
        "(() => { const g = typeof globalThis !== 'undefined' ? globalThis : (typeof window !== 'undefined' ? window : this); if (!g.process) g.process = {}; if (!g.process.env) g.process.env = {}; g.process.env[name] = value; if (typeof process !== 'undefined' && process && process.env) process.env[name] = value; })()",
    )

private fun jsDeleteEnv(name: String): Unit =
    js(
        "(() => { const g = typeof globalThis !== 'undefined' ? globalThis : (typeof window !== 'undefined' ? window : this); if (g.process && g.process.env) delete g.process.env[name]; if (typeof process !== 'undefined' && process && process.env) delete process.env[name]; })()",
    )

private fun jsEnvKeys(): dynamic =
    js(
        "(() => { const g = typeof globalThis !== 'undefined' ? globalThis : (typeof window !== 'undefined' ? window : this); if (g.process && g.process.env) return Object.keys(g.process.env); if (typeof process !== 'undefined' && process && process.env) return Object.keys(process.env); return []; })()",
    )
