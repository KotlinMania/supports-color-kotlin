// port-lint: source lib.rs
@file:OptIn(ExperimentalForeignApi::class)

package io.github.kotlinmania.supportscolor

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.setenv
import platform.posix.unsetenv
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private fun setUp() {
    // clears process env variable
    for (name in KNOWN_ENV) unsetenv(name)
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

        setenv("IGNORE_IS_TERMINAL", "1", 1)
        setenv("CLICOLOR", "1", 1)
        val expected = ColorLevel(
            level = 1,
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
        val expected = ColorLevel(
            level = 1,
            hasBasic = true,
            has256 = false,
            has16m = false,
        )
        assertEquals(expected, on(Stream.Stdout))
    }
}

private val KNOWN_ENV: Array<String> = arrayOf(
    "FORCE_COLOR",
    "CLICOLOR_FORCE",
    "NO_COLOR",
    "TERM",
    "TERM_PROGRAM",
    "COLORTERM",
    "CLICOLOR",
    "IGNORE_IS_TERMINAL",
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
