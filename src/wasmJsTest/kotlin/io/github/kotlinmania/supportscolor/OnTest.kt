// port-lint: tests lib.rs
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.supportscolor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private fun setUp() {
    resetCacheForTesting()
    val n = jsEnvCount()
    repeat(n) {
        val key = jsEnvKeyAt(0) ?: return
        jsDeleteEnv(key)
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

private fun jsSetEnv(name: String, value: String) {
    js(
        "(() => { const g = typeof globalThis !== 'undefined' ? globalThis : (typeof window !== 'undefined' ? window : this); if (!g.process) g.process = {}; if (!g.process.env) g.process.env = {}; g.process.env[name] = value; if (typeof process !== 'undefined' && process && process.env) process.env[name] = value; })()",
    )
}

private fun jsDeleteEnv(name: String) {
    js(
        "(() => { const g = typeof globalThis !== 'undefined' ? globalThis : (typeof window !== 'undefined' ? window : this); if (g.process && g.process.env) delete g.process.env[name]; if (typeof process !== 'undefined' && process && process.env) delete process.env[name]; })()",
    )
}

private fun jsEnvCount(): Int =
    js(
        "(() => { const g = typeof globalThis !== 'undefined' ? globalThis : (typeof window !== 'undefined' ? window : this); if (g.process && g.process.env) return Object.keys(g.process.env).length; if (typeof process !== 'undefined' && process && process.env) return Object.keys(process.env).length; return 0; })()",
    )

private fun jsEnvKeyAt(index: Int): String? =
    js(
        "(() => { const g = typeof globalThis !== 'undefined' ? globalThis : (typeof window !== 'undefined' ? window : this); const keys = (g.process && g.process.env) ? Object.keys(g.process.env) : ((typeof process !== 'undefined' && process && process.env) ? Object.keys(process.env) : []); return keys[index] || null; })()",
    )
