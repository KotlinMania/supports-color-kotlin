// port-lint: ignore (Android implementation of env / isatty / cfg(windows) shims for src/lib.rs)
package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? = System.getenv(name)

internal actual fun isATty(stream: Stream): Boolean {
    // Android applications are not attached to a controlling terminal in the conventional sense.
    // Treating stdout/stderr as not-a-tty matches what callers reasonably expect, and aligns with
    // the upstream Rust behavior on platforms where IsTerminal returns false for the process's
    // descriptors.
    return false
}

internal actual val isWindows: Boolean = false

internal actual fun setEnvVar(name: String, value: String) {
    // The JVM does not expose a portable way to mutate the running process's environment block.
    // This is a no-op; tests that exercise mutating env vars are skipped on this target.
}

internal actual fun clearAllEnvVars() {
    // See [setEnvVar]: the running JVM cannot mutate its own environment block.
}
