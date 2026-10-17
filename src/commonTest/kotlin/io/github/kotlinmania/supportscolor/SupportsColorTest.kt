// port-lint: tests lib.rs
package io.github.kotlinmania.supportscolor

import io.github.kotlinmania.libc.unix.linuxlike.clearenv
import io.github.kotlinmania.libc.unix.setenv
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SupportsColorTest {
    private fun setUp() {
        clearenv()
        CACHE[0].reset()
        CACHE[1].reset()
    }

    @Test
    fun testEmptyEnv() {
        setUp()
        assertNull(on(Stream.Stdout))
    }

    @Test
    fun testClicolorAnsi() {
        setUp()

        setenv("IGNORE_IS_TERMINAL", "1", 1)
        setenv("CLICOLOR", "1", 1)
        val expected =
            ColorLevel(
                1,
                hasBasic = true,
                has256 = false,
                has16m = false,
            )
        assertEquals(expected, on(Stream.Stdout))

        setenv("CLICOLOR", "0", 1)
        assertNull(on(Stream.Stdout))
    }

    @Test
    fun testOnCached() {
        setUp()
        setenv("IGNORE_IS_TERMINAL", "1", 1)

        setenv("CLICOLOR", "1", 1)
        assertNotNull(on(Stream.Stdout))
        assertNotNull(onCached(Stream.Stdout))

        setenv("CLICOLOR", "0", 1)
        assertNull(on(Stream.Stdout))
        assertNotNull(onCached(Stream.Stdout))
    }

    @Test
    fun testClicolorForceAnsi() {
        setUp()

        setenv("CLICOLOR", "0", 1)
        setenv("CLICOLOR_FORCE", "1", 1)
        val expected =
            ColorLevel(
                1,
                hasBasic = true,
                has256 = false,
                has16m = false,
            )
        assertEquals(expected, on(Stream.Stdout))
    }
}
