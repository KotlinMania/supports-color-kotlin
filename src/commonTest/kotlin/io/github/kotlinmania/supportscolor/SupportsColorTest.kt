// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupportsColorTest {
    private fun setUp() {
        resetCacheForTesting()
    }

    @Test
    fun testEmptyEnv() {
        setUp()
        val env = emptyMap<String, String>()
        val result = on(Stream.Stdout, env = { env[it] }, isTty = { false })
        assertNull(result)
    }

    @Test
    fun testClicolorAnsi() {
        val env1 = mapOf("IGNORE_IS_TERMINAL" to "1", "CLICOLOR" to "1")
        val expected = ColorLevel(
            level = 1,
            hasBasic = true,
            has256 = false,
            has16m = false,
        )
        val result1 = on(Stream.Stdout, env = { env1[it] }, isTty = { false })
        assertEquals(expected, result1)

        val env2 = mapOf("IGNORE_IS_TERMINAL" to "1", "CLICOLOR" to "0")
        val result2 = on(Stream.Stdout, env = { env2[it] }, isTty = { false })
        assertNull(result2)
    }

    @Test
    fun testOnCached() {
        resetCacheForTesting()
        val liveResult = on(Stream.Stdout)
        val cachedResult = onCached(Stream.Stdout)
        assertEquals(liveResult, cachedResult)
        resetCacheForTesting()
    }

    @Test
    fun testClicolorForceAnsi() {
        val env = mapOf("CLICOLOR" to "0", "CLICOLOR_FORCE" to "1")
        val expected = ColorLevel(
            level = 1,
            hasBasic = true,
            has256 = false,
            has16m = false,
        )
        val result = on(Stream.Stdout, env = { env[it] }, isTty = { false })
        assertEquals(expected, result)
    }

    @Test
    fun testColorterm16m() {
        val env = mapOf("IGNORE_IS_TERMINAL" to "1", "COLORTERM" to "truecolor")
        val result = on(Stream.Stdout, env = { env[it] }, isTty = { false })
        assertNotNull(result)
        assertEquals(3, result.level)
        assertTrue(result.hasBasic)
        assertTrue(result.has256)
        assertTrue(result.has16m)
    }

    @Test
    fun testTerm256() {
        val env = mapOf("IGNORE_IS_TERMINAL" to "1", "TERM" to "xterm-256color")
        val result = on(Stream.Stdout, env = { env[it] }, isTty = { false })
        assertNotNull(result)
        assertEquals(2, result.level)
        assertTrue(result.hasBasic)
        assertTrue(result.has256)
        assertEquals(false, result.has16m)
    }

    @Test
    fun testItermApp() {
        val env = mapOf("IGNORE_IS_TERMINAL" to "1", "TERM_PROGRAM" to "iTerm.app")
        val result = on(Stream.Stdout, env = { env[it] }, isTty = { false })
        assertNotNull(result)
        assertEquals(3, result.level)
        assertTrue(result.has16m)
    }

    @Test
    fun testAppleTerminal() {
        val env = mapOf("IGNORE_IS_TERMINAL" to "1", "TERM_PROGRAM" to "Apple_Terminal")
        val result = on(Stream.Stdout, env = { env[it] }, isTty = { false })
        assertNotNull(result)
        assertEquals(2, result.level)
        assertTrue(result.has256)
    }

    @Test
    fun testNoColor() {
        val env = mapOf("IGNORE_IS_TERMINAL" to "1", "CLICOLOR" to "1", "NO_COLOR" to "1")
        val result = on(Stream.Stdout, env = { env[it] }, isTty = { false })
        assertNull(result)
    }

    @Test
    fun testForceColor() {
        val env = mapOf("FORCE_COLOR" to "3")
        val result = on(Stream.Stdout, env = { env[it] }, isTty = { false })
        assertNotNull(result)
        assertEquals(3, result.level)
        assertTrue(result.has16m)
    }

    @Test
    fun testDumbTerminal() {
        val env = mapOf("IGNORE_IS_TERMINAL" to "1", "TERM" to "dumb", "CLICOLOR" to "1")
        val result = on(Stream.Stdout, env = { env[it] }, isTty = { false })
        assertNull(result)
    }
}
