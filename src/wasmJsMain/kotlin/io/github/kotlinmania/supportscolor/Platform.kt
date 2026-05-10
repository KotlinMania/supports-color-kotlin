// port-lint: ignore (Wasm-JS / Node implementation of env / isatty / cfg(windows) shims for src/lib.rs)
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? = jsGetEnv(name)

internal actual fun isATty(stream: Stream): Boolean = when (stream) {
    Stream.STDOUT -> jsIsTty("stdout")
    Stream.STDERR -> jsIsTty("stderr")
}

internal actual val isWindows: Boolean = jsIsWindows()

internal actual fun setEnvVar(name: String, value: String) {
    jsSetEnv(name, value)
}

internal actual fun clearAllEnvVars() {
    val n = jsEnvCount()
    repeat(n) {
        val key = jsEnvKeyAt(0) ?: return
        jsDeleteEnv(key)
    }
}

private fun jsGetEnv(name: String): String? = js(
    "(typeof process !== 'undefined' && process && process.env && typeof process.env[name] === 'string') ? process.env[name] : null",
)

private fun jsSetEnv(name: String, value: String) {
    js("if (typeof process !== 'undefined' && process && process.env) { process.env[name] = value; }")
}

private fun jsDeleteEnv(name: String) {
    js("if (typeof process !== 'undefined' && process && process.env) { delete process.env[name]; }")
}

private fun jsEnvCount(): Int = js(
    "(typeof process !== 'undefined' && process && process.env) ? Object.keys(process.env).length : 0",
)

private fun jsEnvKeyAt(index: Int): String? = js(
    "(typeof process !== 'undefined' && process && process.env) ? Object.keys(process.env)[index] : null",
)

private fun jsIsTty(name: String): Boolean = js(
    "((typeof process !== 'undefined' && process && process[name] && process[name].isTTY === true))",
)

private fun jsIsWindows(): Boolean = js(
    "((typeof process !== 'undefined' && process && process.platform === 'win32'))",
)
