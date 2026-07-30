// port-lint: source lib.rs
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.supportscolor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private fun setUp() {
    // clears process env variable
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
    js("if (typeof process !== 'undefined' && process && process.env) { process.env[name] = value; }")
}

private fun jsDeleteEnv(name: String) {
    js("if (typeof process !== 'undefined' && process && process.env) { delete process.env[name]; }")
}

private fun jsEnvCount(): Int =
    js(
        "(typeof process !== 'undefined' && process && process.env) ? Object.keys(process.env).length : 0",
    )

private fun jsEnvKeyAt(index: Int): String? =
    js(
        "(typeof process !== 'undefined' && process && process.env) ? Object.keys(process.env)[index] : null",
    )
