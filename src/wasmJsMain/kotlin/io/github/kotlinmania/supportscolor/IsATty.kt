// port-lint: source lib.rs
@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package io.github.kotlinmania.supportscolor

internal actual fun isATty(stream: Stream): Boolean =
    when (stream) {
        Stream.Stdout -> jsIsTty("stdout")
        Stream.Stderr -> jsIsTty("stderr")
    }

private fun jsIsTty(name: String): Boolean =
    js(
        "((typeof process !== 'undefined' && process && process[name] && process[name].isTTY === true))",
    )
