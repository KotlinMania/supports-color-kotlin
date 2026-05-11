// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? {
    val raw: dynamic = jsGetEnv(name)
    return if (raw == null || raw == undefined()) null else raw.unsafeCast<String>()
}

private fun jsGetEnv(name: String): dynamic = js(
    "(typeof process !== 'undefined' && process && process.env) ? process.env[name] : undefined",
)

private fun undefined(): dynamic = js("undefined")
