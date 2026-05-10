// port-lint: ignore (Node.js implementation of env / isatty / cfg(windows) shims for src/lib.rs)
package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? {
    val raw: dynamic = jsGetEnv(name)
    return if (raw == null || raw == undefined()) null else raw.unsafeCast<String>()
}

internal actual fun isATty(stream: Stream): Boolean = when (stream) {
    Stream.STDOUT -> jsIsTty("stdout")
    Stream.STDERR -> jsIsTty("stderr")
}

internal actual val isWindows: Boolean = jsIsWindows()

internal actual fun setEnvVar(name: String, value: String) {
    jsSetEnv(name, value)
}

internal actual fun clearAllEnvVars() {
    val names = jsEnvKeys()
    val length = names.length
    for (i in 0 until length) {
        val key = names[i].unsafeCast<String>()
        jsDeleteEnv(key)
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

private fun jsIsWindows(): Boolean = js(
    "(typeof process !== 'undefined' && process && process.platform === 'win32')",
)

private fun undefined(): dynamic = js("undefined")
