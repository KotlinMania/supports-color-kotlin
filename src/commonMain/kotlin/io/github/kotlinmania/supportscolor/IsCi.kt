// port-lint: ignore (vendored Kotlin equivalent of the `is_ci 1.2.0` crate dependency until a sibling is-ci-kotlin port exists)
package io.github.kotlinmania.supportscolor

/**
 * Mirrors `is_ci::uncached()` from the upstream `is_ci` crate (version 1.2.0). Returns true when
 * the current process appears to be running inside a continuous-integration environment by looking
 * for any of the well-known CI provider environment variables.
 *
 * The list mirrors the upstream crate's recognised provider set and is consulted by
 * [supportsColor] as a fallback signal: if the stream is not a tty and no other color signal is
 * present, presence of a CI environment is treated as evidence that ANSI color is acceptable.
 */
internal fun isCiUncached(): Boolean {
    for (name in CI_ENV_NAMES) {
        if (envVar(name) != null) return true
    }
    return false
}

internal val CI_ENV_NAMES: Array<String> = arrayOf(
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
