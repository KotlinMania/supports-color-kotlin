// port-lint: source lib.rs
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? = jsGetEnv(name)

private fun jsGetEnv(name: String): String? =
    js(
        "(() => { const g = typeof globalThis !== 'undefined' ? globalThis : (typeof window !== 'undefined' ? window : this); if (g.process && g.process.env && typeof g.process.env[name] === 'string') return g.process.env[name]; if (typeof process !== 'undefined' && process && process.env && typeof process.env[name] === 'string') return process.env[name]; return null; })()",
    )
