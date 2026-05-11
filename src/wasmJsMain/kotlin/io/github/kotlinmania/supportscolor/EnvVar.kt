// port-lint: source lib.rs
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? = jsGetEnv(name)

private fun jsGetEnv(name: String): String? = js(
    "(typeof process !== 'undefined' && process && process.env && typeof process.env[name] === 'string') ? process.env[name] : null",
)
