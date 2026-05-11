// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? {
    val raw: dynamic = jsGetEnv(name)
    return if (raw == null || raw == undefined()) null else raw.unsafeCast<String>()
}

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

private fun jsGetEnv(name: String): dynamic = js(
    "(typeof process !== 'undefined' && process && process.env) ? process.env[name] : undefined",
)

private fun jsIsTty(name: String): Boolean = js(
    "(typeof process !== 'undefined' && process && process[name] && process[name].isTTY === true)",
)

private fun undefined(): dynamic = js("undefined")
