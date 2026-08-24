// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal fun supportsColor(
    stream: Stream,
    env: (String) -> String? = { envVar(it) },
    isTty: (Stream) -> Boolean = { isATty(it) },
): Int {
    val forceColor = envForceColor(env)
    return if (forceColor > 0) {
        forceColor
    } else if (envNoColor(env) ||
        asStr(env("TERM")) == "dumb" ||
        !(isTty(stream) || env("IGNORE_IS_TERMINAL")?.let { it != "0" } == true)
    ) {
        0
    } else if (env("COLORTERM")?.let { checkColorterm16m(it) } == true ||
        env("TERM")?.let { checkTerm16m(it) } == true ||
        asStr(env("TERM_PROGRAM")) == "iTerm.app"
    ) {
        3
    } else if (asStr(env("TERM_PROGRAM")) == "Apple_Terminal" ||
        env("TERM")?.let { check256Color(it) } == true
    ) {
        2
    } else if (env("COLORTERM") != null ||
        checkAnsiColor(env("TERM")) ||
        env("CLICOLOR")?.let { it != "0" } == true ||
        IsCi.uncached()
    ) {
        1
    } else {
        0
    }
}
