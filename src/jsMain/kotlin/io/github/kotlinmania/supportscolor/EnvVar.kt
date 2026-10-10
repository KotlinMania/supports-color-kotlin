// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? {
    val raw: dynamic = jsGetEnv(name)
    return if (raw == null || raw == undefined()) null else raw.unsafeCast<String>()
}

private fun jsGetEnv(name: String): dynamic =
    js(
        "(() => { const g = typeof globalThis !== 'undefined' ? globalThis : (typeof window !== 'undefined' ? window : this); if (g.process && g.process.env && g.process.env[name] !== undefined) return g.process.env[name]; if (typeof process !== 'undefined' && process && process.env && process.env[name] !== undefined) return process.env[name]; return undefined; })()",
    )

private fun undefined(): dynamic = js("undefined")
