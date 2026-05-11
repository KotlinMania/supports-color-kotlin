// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? {
    val raw: dynamic = jsGetEnv(name)
    return if (raw == null || raw == undefined()) null else raw.unsafeCast<String>()
}

internal actual fun envSetVar(name: String, value: String) {
    jsSetEnv(name, value)
}

internal actual fun envRemoveVar(name: String) {
    jsDeleteEnv(name)
}

internal actual fun envVars(): Iterable<Pair<String, String>> {
    val result = mutableListOf<Pair<String, String>>()
    val names = jsEnvKeys()
    val length = names.length
    for (i in 0 until length) {
        val key = names[i].unsafeCast<String>()
        val value = jsGetEnv(key)
        if (value != null && value != undefined()) {
            result.add(key to value.unsafeCast<String>())
        }
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

private fun jsGetEnv(name: String): dynamic = js(
    "(typeof process !== 'undefined' && process && process.env) ? process.env[name] : undefined",
)

private fun jsSetEnv(name: String, value: String): Unit = js(
    "if (typeof process !== 'undefined' && process && process.env) { process.env[name] = value; }",
)

private fun jsDeleteEnv(name: String): Unit = js(
    "if (typeof process !== 'undefined' && process && process.env) { delete process.env[name]; }",
)

private fun jsEnvKeys(): dynamic = js(
    "(typeof process !== 'undefined' && process && process.env) ? Object.keys(process.env) : []",
)

private fun jsIsTty(name: String): Boolean = js(
    "(typeof process !== 'undefined' && process && process[name] && process[name].isTTY === true)",
)

private fun undefined(): dynamic = js("undefined")
