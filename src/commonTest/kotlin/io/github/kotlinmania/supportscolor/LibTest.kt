// port-lint: source src/lib.rs (the test block at the bottom)
package io.github.kotlinmania.supportscolor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

// The upstream uses a process-wide mutex to serialize the env-mutating tests below. Kotlin
// test runners execute the tests inside a single class sequentially per worker on every
// target this repo ships for, which is the strongest portable guarantee available; no
// extra KMP-portable mutex is wired up.

private fun setUp() {
    // clears process env variables
    envVars().forEach { (k, _) -> envRemoveVar(k) }
    // Kotlin Native cinterop does not surface the underlying environment-block pointer, so
    // the iterator-driven line above only fires on the JS Node target. To honor the
    // upstream clean-env precondition on every target, additionally remove every name
    // [supportsColor] and [IsCi] consult.
    for (name in TEST_ENV_NAMES) envRemoveVar(name)
}

class LibTest {
    @Test
    fun testEmptyEnv() {
        setUp()

        assertNull(on(Stream.Stdout))
    }

    @Test
    fun testClicolorAnsi() {
        setUp()

        envSetVar("IGNORE_IS_TERMINAL", "1")
        envSetVar("CLICOLOR", "1")
        val expected = ColorLevel(
            level = 1,
            hasBasic = true,
            has256 = false,
            has16m = false,
        )
        assertEquals(expected, on(Stream.Stdout))

        envSetVar("CLICOLOR", "0")
        assertNull(on(Stream.Stdout))
    }

    @Test
    fun testOnCached() {
        setUp()
        envSetVar("IGNORE_IS_TERMINAL", "1")

        envSetVar("CLICOLOR", "1")
        assertNotNull(on(Stream.Stdout))
        assertNotNull(onCached(Stream.Stdout))

        envSetVar("CLICOLOR", "0")
        assertNull(on(Stream.Stdout))
        assertNotNull(onCached(Stream.Stdout))
    }

    @Test
    fun testClicolorForceAnsi() {
        setUp()

        envSetVar("CLICOLOR", "0")
        envSetVar("CLICOLOR_FORCE", "1")
        val expected = ColorLevel(
            level = 1,
            hasBasic = true,
            has256 = false,
            has16m = false,
        )
        assertEquals(expected, on(Stream.Stdout))
    }
}

// Names of every environment variable [supportsColor] and the in-repo [IsCi] stand-in
// consult. Used only by [setUp] to guarantee a clean precondition; see the note there.
private val TEST_ENV_NAMES: Array<String> = arrayOf(
    // supportsColor
    "FORCE_COLOR",
    "CLICOLOR_FORCE",
    "NO_COLOR",
    "TERM",
    "TERM_PROGRAM",
    "COLORTERM",
    "CLICOLOR",
    "IGNORE_IS_TERMINAL",
    // IsCi
    "CI",
    "CONTINUOUS_INTEGRATION",
    "BUILD_NUMBER",
    "RUN_ID",
    "AGOLA_GIT_REF",
    "AC_APPCIRCLE",
    "APPVEYOR",
    "CODEBUILD_BUILD_ARN",
    "BAMBOO_BUILDKEY",
    "BITBUCKET_COMMIT",
    "BITRISE_IO",
    "BUDDY_WORKSPACE_ID",
    "BUILDKITE",
    "CIRCLECI",
    "CIRRUS_CI",
    "CF_BUILD_ID",
    "CM_BUILD_ID",
    "CI_NAME",
    "DRONE",
    "DSARI",
    "EARTHLY_CI",
    "EAS_BUILD",
    "GERRIT_PROJECT",
    "GITEA_ACTIONS",
    "GITHUB_ACTIONS",
    "GITLAB_CI",
    "GOCD",
    "BUILDER_OUTPUT",
    "HARNESS_BUILD_ID",
    "HUDSON_URL",
    "JENKINS_URL",
    "LAYERCI",
    "MAGNUM",
    "NETLIFY",
    "NEVERCODE",
    "PROW_JOB_ID",
    "RELEASE_BUILD_ID",
    "RENDER",
    "SAILCI",
    "HOSTNAME",
    "SCREWDRIVER",
    "SEMAPHORE",
    "SOURCEHUT",
    "STRIDER",
    "TASK_ID",
    "TEAMCITY_VERSION",
    "TF_BUILD",
    "TRAVIS",
    "VELA",
    "VERCEL",
    "APPCENTER_BUILD_ID",
    "WERCKER_ROOT",
    "WOODPECKER",
    "XCS",
)
