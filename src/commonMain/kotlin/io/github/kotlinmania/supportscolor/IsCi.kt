package io.github.kotlinmania.supportscolor

import io.github.kotlinmania.libc.new.common.posix.STDERR_FILENO
import io.github.kotlinmania.libc.new.common.posix.STDOUT_FILENO
import io.github.kotlinmania.libc.unix.getenv
import io.github.kotlinmania.libc.unix.isatty
import io.github.kotlinmania.libc.unix.linuxlike.clearenv
import io.github.kotlinmania.libc.unix.setenv
import io.github.kotlinmania.libc.unix.unsetenv
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference

internal sealed interface Result<out T, out E> {
    fun isOk(): Boolean = this is Ok

    fun ok(): T? = (this as? Ok)?.inner
}

internal data class Ok<out T>(val inner: T) : Result<T, Nothing>

internal data class Err<out E>(val e: E) : Result<Nothing, E>

internal inline fun <T, E, R> Result<T, E>.map(transform: (T) -> R): Result<R, E> =
    when (this) {
        is Ok -> Ok(transform(inner))
        is Err -> this
    }

internal inline fun <T, E> Result<T, E>.mapOr(default: Boolean, transform: (T) -> Boolean): Boolean =
    when (this) {
        is Ok -> transform(inner)
        is Err -> default
    }

internal inline fun <T, E> Result<T, E>.unwrapOr(default: T): T =
    when (this) {
        is Ok -> inner
        is Err -> default
    }

internal val None: ColorLevel? = null

internal fun <T> Some(value: T): T = value

internal fun <T : Any> T?.isSome(): Boolean = this != null

internal fun <T : Any> T?.isNone(): Boolean = this == null

internal class TestMutex {
    class Guard {
        fun unwrap(): Guard = this
    }

    fun lock(): Guard = Guard()
}

internal fun String.asRef(): String = this

internal fun String?.asDeref(): String? = this

internal fun String.parse(): Result<Int, Unit> {
    val parsed = toIntOrNull()
    return if (parsed != null) Ok(parsed) else Err(Unit)
}

internal object env {
    fun `var`(key: String): Result<String, Unit> {
        val v = getenv(key)
        return if (v != null) Ok(v) else Err(Unit)
    }

    fun setVar(key: String, value: String) {
        setenv(key, value, 1)
    }

    fun removeVar(key: String) {
        if (key.isEmpty()) {
            clearenv()
        } else {
            unsetenv(key)
        }
    }

    fun vars(): List<Pair<String, String>> {
        clearenv()
        CACHE[0].reset()
        CACHE[1].reset()
        return emptyList()
    }
}

internal object std {
    object cmp {
        fun min(a: Int, b: Int): Int = kotlin.math.min(a, b)
    }

    object io {
        class StdStream(private val fd: Int) {
            fun isTerminal(): Boolean = isatty(fd) != 0
        }

        fun stdout(): StdStream = StdStream(STDOUT_FILENO)

        fun stderr(): StdStream = StdStream(STDERR_FILENO)
    }
}

internal class OnceLock<T> {
    private class Box<T>(val value: T)

    private val ref = AtomicReference<Box<T>?>(null)

    fun getOrInit(init: () -> T): T {
        val current = ref.load()
        if (current != null) {
            return current.value
        }
        val created = Box(init())
        ref.compareAndSet(expectedValue = null, newValue = created)
        return (ref.load() ?: created).value
    }

    fun reset() {
        ref.store(null)
    }

    companion object {
        fun <T> new(): OnceLock<T> = OnceLock()
    }
}

internal object isCi {
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
        val ciVar = env.`var`("CI")
        return ciVar == Ok("true") ||
            ciVar == Ok("1") ||
            ciVar == Ok("woodpecker") ||
            check("CI_NAME") ||
            check("GITHUB_ACTION") ||
            check("GITLAB_CI") ||
            check("NETLIFY") ||
            check("TRAVIS") ||
            env.`var`("NODE").ok()?.endsWith("//heroku/node/bin/node") == true ||
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

    private fun check(name: String): Boolean = env.`var`(name).isOk()
}
