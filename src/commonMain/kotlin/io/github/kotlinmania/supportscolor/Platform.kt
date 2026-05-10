// port-lint: ignore (platform abstractions over std::env, std::io::IsTerminal, and #[cfg(windows)] for src/lib.rs)
package io.github.kotlinmania.supportscolor

/**
 * Returns the value of the named process environment variable, or null if the variable is
 * unset or the platform cannot decode it as Unicode. Mirrors the success arm of `env::var`.
 */
internal expect fun envVar(name: String): String?

/**
 * Returns true when the descriptor for [stream] refers to a terminal/tty. Mirrors
 * `std::io::IsTerminal::is_terminal` invoked on `std::io::stdout()` / `std::io::stderr()`.
 */
internal expect fun isATty(stream: Stream): Boolean

/**
 * True when running on Windows. Mirrors `cfg!(windows)` and is consulted by [checkAnsiColor]
 * to decide whether to allow non-`dumb`/non-`cygwin` `TERM` values to imply ANSI support.
 */
internal expect val isWindows: Boolean

/**
 * Sets the named process environment variable to [value]. Mirrors `env::set_var`. Used by the
 * port's test suite to drive the same scenarios the upstream suite drives. On platforms where
 * the running process cannot mutate its own environment (Android), this is a no-op.
 */
internal expect fun setEnvVar(name: String, value: String)

/**
 * Removes every entry from the process environment. Mirrors the upstream `set_up()` test helper,
 * which does `env::vars().for_each(|(k, _v)| env::remove_var(k))` to give each test case a clean
 * environment. No-op on platforms that cannot mutate their own environment.
 */
internal expect fun clearAllEnvVars()

/**
 * Color- and tty-related environment variable names consulted by [supportsColor]. Combined with
 * [CI_ENV_NAMES] this is the working set the test suite needs to be able to clear in order to
 * reproduce the upstream `set_up()` precondition of an empty environment.
 */
internal val SUPPORTS_COLOR_ENV_NAMES: Array<String> = arrayOf(
    "FORCE_COLOR",
    "CLICOLOR_FORCE",
    "NO_COLOR",
    "TERM",
    "TERM_PROGRAM",
    "COLORTERM",
    "CLICOLOR",
    "IGNORE_IS_TERMINAL",
)
