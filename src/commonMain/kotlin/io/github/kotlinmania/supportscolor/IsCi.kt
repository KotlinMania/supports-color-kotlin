// port-lint: ignore (in-repo stand-in for the upstream is-ci 1.2.0 crate until a sibling
// is-ci-kotlin port exists; mirrors the upstream uncached surface so the call site in
// [Lib.kt] reads identically to the upstream original.)
package io.github.kotlinmania.supportscolor

/**
 * Mirrors the upstream is-ci 1.2.0 crate. That crate exposes a single public function,
 * [uncached], which returns true when any of a known set of continuous-integration provider
 * environment variables is set in the current process.
 *
 * This Kotlin object preserves that call shape ([IsCi.uncached]) so the consumer line in
 * [supportsColor] reads the same as the upstream original.
 */
internal object IsCi {
    fun uncached(): Boolean {
        for (name in CI_ENV_NAMES) {
            if (envVar(name) != null) return true
        }
        return false
    }
}

private val CI_ENV_NAMES: Array<String> = arrayOf(
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
