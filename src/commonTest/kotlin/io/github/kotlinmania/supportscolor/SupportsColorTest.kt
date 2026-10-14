// port-lint: tests lib.rs
package io.github.kotlinmania.supportscolor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// needed to prevent race conditions when mutating the environment
private val TEST_LOCK: TestMutex = TestMutex()

private fun assertEq(actual: ColorLevel?, expected: ColorLevel?) {
    assertEquals(expected, actual)
}

private fun assert(condition: Boolean) {
    assertTrue(condition)
}

private fun setUp() {
    // clears process env variable
    env.vars().forEach { (k, _) -> env.removeVar(k) }
}

internal fun testEmptyEnv() {
    val ignored = TEST_LOCK.lock().unwrap()
    setUp()

    assertEq(on(Stream.Stdout), None)
}

internal fun testClicolorAnsi() {
    val ignored = TEST_LOCK.lock().unwrap()
    setUp()

    env.setVar("IGNORE_IS_TERMINAL", "1")
    env.setVar("CLICOLOR", "1")
    val expected =
        Some(
            ColorLevel(
                1,
                hasBasic = true,
                has256 = false,
                has16m = false,
            ),
        )
    assertEq(on(Stream.Stdout), expected)

    env.setVar("CLICOLOR", "0")
    assertEq(on(Stream.Stdout), None)
}

internal fun testOnCached() {
    val ignored = TEST_LOCK.lock().unwrap()
    setUp()
    env.setVar("IGNORE_IS_TERMINAL", "1")

    env.setVar("CLICOLOR", "1")
    assert(on(Stream.Stdout).isSome())
    assert(onCached(Stream.Stdout).isSome())

    env.setVar("CLICOLOR", "0")
    assert(on(Stream.Stdout).isNone())
    assert(onCached(Stream.Stdout).isSome())
}

internal fun testClicolorForceAnsi() {
    val ignored = TEST_LOCK.lock().unwrap()
    setUp()

    env.setVar("CLICOLOR", "0")
    env.setVar("CLICOLOR_FORCE", "1")
    val expected =
        Some(
            ColorLevel(
                1,
                hasBasic = true,
                has256 = false,
                has16m = false,
            ),
        )
    assertEq(on(Stream.Stdout), expected)
}

class SupportsColorTest {
    @Test
    fun setUp() {
        // clears process env variable
        env.vars().forEach { (k, _) -> env.removeVar(k) }
    }

    @Test
    fun testEmptyEnv() {
        val ignored = TEST_LOCK.lock().unwrap()
        setUp()

        assertEq(on(Stream.Stdout), None)
    }

    @Test
    fun testClicolorAnsi() {
        val ignored = TEST_LOCK.lock().unwrap()
        setUp()

        env.setVar("IGNORE_IS_TERMINAL", "1")
        env.setVar("CLICOLOR", "1")
        val expected =
            Some(
                ColorLevel(
                    1,
                    hasBasic = true,
                    has256 = false,
                    has16m = false,
                ),
            )
        assertEq(on(Stream.Stdout), expected)

        env.setVar("CLICOLOR", "0")
        assertEq(on(Stream.Stdout), None)
    }

    @Test
    fun testOnCached() {
        val ignored = TEST_LOCK.lock().unwrap()
        setUp()
        env.setVar("IGNORE_IS_TERMINAL", "1")

        env.setVar("CLICOLOR", "1")
        assert(on(Stream.Stdout).isSome())
        assert(onCached(Stream.Stdout).isSome())

        env.setVar("CLICOLOR", "0")
        assert(on(Stream.Stdout).isNone())
        assert(onCached(Stream.Stdout).isSome())
    }

    @Test
    fun testClicolorForceAnsi() {
        val ignored = TEST_LOCK.lock().unwrap()
        setUp()

        env.setVar("CLICOLOR", "0")
        env.setVar("CLICOLOR_FORCE", "1")
        val expected =
            Some(
                ColorLevel(
                    1,
                    hasBasic = true,
                    has256 = false,
                    has16m = false,
                ),
            )
        assertEq(on(Stream.Stdout), expected)
    }
}
