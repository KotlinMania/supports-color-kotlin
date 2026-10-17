package io.github.kotlinmania.supportscolor

import io.github.kotlinmania.libc.unix.getenv
import kotlin.concurrent.atomics.AtomicBoolean

internal object IsCi {
    private val INIT = AtomicBoolean(false)
    private val IS_CI = AtomicBoolean(false)

    /**
     * Returns true if the current environment is found to probably be a CI
     * environment or service. That's it, that's all it does.
     */
    @Deprecated(message = "Use `cached` or `uncached` instead")
    fun isCi(): Boolean = uncached()

    /**
     * Returns true if the current environment is found to probably be a CI
     * environment or service, and caches the result for future calls. If you
     * expect the environment to change, use [uncached].
     */
    fun cached(): Boolean {
        if (INIT.compareAndSet(expectedValue = false, newValue = true)) {
            IS_CI.store(uncached())
        }
        return IS_CI.load()
    }

    /**
     * Returns true if the current environment is found to probably be a CI
     * environment or service. If you expect to call this multiple times without
     * the environment changing, use [cached].
     */
    fun uncached(): Boolean {
        val ciVar = getenv("CI")
        return ciVar == "true" ||
            ciVar == "1" ||
            ciVar == "woodpecker" ||
            check("CI_NAME") ||
            check("GITHUB_ACTION") ||
            check("GITLAB_CI") ||
            check("NETLIFY") ||
            check("TRAVIS") ||
            getenv("NODE")?.endsWith("//heroku/node/bin/node") == true ||
            check("CODEBUILD_SRC_DIR") ||
            check("BUILDER_OUTPUT") ||
            check("GITLAB_DEPLOYMENT") ||
            check("NOW_GITHUB_DEPLOYMENT") ||
            check("NOW_BUILDER") ||
            check("BITBUCKET_DEPLOYMENT") ||
            check("GERRIT_PROJECT") ||
            check("SYSTEM_TEAMFOUNDATIONCOLLECTIONURI") ||
            check("BITRISE_IO") ||
            check("BUDDY_WORKSPACE_ID") ||
            check("BUILDKITE") ||
            check("CIRRUS_CI") ||
            check("APPVEYOR") ||
            check("CIRCLECI") ||
            check("SEMAPHORE") ||
            check("DRONE") ||
            check("DSARI") ||
            check("TDDIUM") ||
            check("STRIDER") ||
            check("TASKCLUSTER_ROOT_URL") ||
            check("JENKINS_URL") ||
            check("bamboo.buildKey") ||
            check("GO_PIPELINE_NAME") ||
            check("HUDSON_URL") ||
            check("WERCKER") ||
            check("MAGNUM") ||
            check("NEVERCODE") ||
            check("RENDER") ||
            check("SAIL_CI") ||
            check("SHIPPABLE")
    }

    private fun check(name: String): Boolean = getenv(name) != null
}

internal val isCi: IsCi = IsCi
