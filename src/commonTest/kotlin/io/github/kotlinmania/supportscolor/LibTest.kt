// port-lint: source src/lib.rs
package io.github.kotlinmania.supportscolor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private fun setUp() {
    // clears process env variables
    clearAllEnvVars()
}

class LibTest {
    @Test
    fun testEmptyEnv() {
        setUp()
        assertNull(on(Stream.STDOUT))
    }

    @Test
    fun testClicolorAnsi() {
        setUp()

        setEnvVar("IGNORE_IS_TERMINAL", "1")
        setEnvVar("CLICOLOR", "1")
        val expected = ColorLevel(
            level = 1,
            hasBasic = true,
            has256 = false,
            has16m = false,
        )
        assertEquals(expected, on(Stream.STDOUT))

        setEnvVar("CLICOLOR", "0")
        assertNull(on(Stream.STDOUT))
    }

    @Test
    fun testOnCached() {
        setUp()
        setEnvVar("IGNORE_IS_TERMINAL", "1")

        setEnvVar("CLICOLOR", "1")
        assertNotNull(on(Stream.STDOUT))
        assertNotNull(onCached(Stream.STDOUT))

        setEnvVar("CLICOLOR", "0")
        assertNull(on(Stream.STDOUT))
        assertNotNull(onCached(Stream.STDOUT))
    }

    @Test
    fun testClicolorForceAnsi() {
        setUp()

        setEnvVar("CLICOLOR", "0")
        setEnvVar("CLICOLOR_FORCE", "1")
        val expected = ColorLevel(
            level = 1,
            hasBasic = true,
            has256 = false,
            has16m = false,
        )
        assertEquals(expected, on(Stream.STDOUT))
    }
}
