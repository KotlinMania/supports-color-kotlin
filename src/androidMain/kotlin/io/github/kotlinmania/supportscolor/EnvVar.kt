// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal actual fun envVar(name: String): String? {
    return try {
        System.getenv(name)
    } catch (e: SecurityException) {
        null
    }
}
