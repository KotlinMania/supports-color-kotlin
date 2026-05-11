// port-lint: source lib.rs
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? = jsGetEnv(name)

internal actual fun envSetVar(name: String, value: String) {
    jsSetEnv(name, value)
}

internal actual fun envRemoveVar(name: String) {
    jsDeleteEnv(name)
}

internal actual fun envVars(): Iterable<Pair<String, String>> {
    val n = jsEnvCount()
    val result = ArrayList<Pair<String, String>>(n)
    for (i in 0 until n) {
        val key = jsEnvKeyAt(i) ?: continue
        val value = jsGetEnv(key) ?: continue
        result.add(key to value)
    }
    return result
}

internal actual fun isATty(stream: Stream): Boolean = when (stream) {
    Stream.Stdout -> jsIsTty("stdout")
    Stream.Stderr -> jsIsTty("stderr")
}

// JS hosts (Node, browsers) are not Windows. Use the non-Windows variant of the upstream
// cfg-gated `checkAnsiColor`.
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
