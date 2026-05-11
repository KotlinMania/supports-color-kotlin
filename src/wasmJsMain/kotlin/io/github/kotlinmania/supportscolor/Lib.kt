// port-lint: source lib.rs
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? = jsGetEnv(name)

internal actual fun isATty(stream: Stream): Boolean = when (stream) {
    Stream.Stdout -> jsIsTty("stdout")
    Stream.Stderr -> jsIsTty("stderr")
}

internal actual fun checkAnsiColor(term: String?): Boolean {
    return if (term != null) {
        // dumb terminals don't support ANSI escape sequences.
        term != "dumb"
    } else {
        // TERM is not set, which is really weird on Unix systems.
        false
    }
}

private fun jsGetEnv(name: String): String? = js(
    "(typeof process !== 'undefined' && process && process.env && typeof process.env[name] === 'string') ? process.env[name] : null",
)

private fun jsIsTty(name: String): Boolean = js(
    "((typeof process !== 'undefined' && process && process[name] && process[name].isTTY === true))",
)
