// port-lint: source lib.rs
package io.github.kotlinmania.supportscolor

internal fun supportsColor(stream: Stream): Int {
    val forceColor = envForceColor()
    return if (forceColor > 0) {
        forceColor
    } else if (envNoColor() ||
        asStr(envVar("TERM")) == "dumb" ||
        !(isATty(stream) || envVar("IGNORE_IS_TERMINAL")?.let { it != "0" } == true)
    ) {
        0
    } else if (envVar("COLORTERM")?.let { checkColorterm16m(it) } == true ||
        envVar("TERM")?.let { checkTerm16m(it) } == true ||
        asStr(envVar("TERM_PROGRAM")) == "iTerm.app"
    ) {
        3
    } else if (asStr(envVar("TERM_PROGRAM")) == "Apple_Terminal" ||
        envVar("TERM")?.let { check256Color(it) } == true
    ) {
        2
    } else if (envVar("COLORTERM") != null ||
        checkAnsiColor(envVar("TERM")) ||
        envVar("CLICOLOR")?.let { it != "0" } == true ||
        IsCi.uncached()
    ) {
        1
    } else {
        0
    }
}
