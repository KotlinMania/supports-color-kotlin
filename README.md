# supports-color-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fsupports--color--kotlin-blue.svg)](https://github.com/KotlinMania/supports-color-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/supports-color-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/supports-color-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/supports-color-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/supports-color-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`zkat/supports-color`](https://github.com/zkat/supports-color).

**Original Project:** This port is based on [`zkat/supports-color`](https://github.com/zkat/supports-color). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `zkat/supports-color`

> The text below is reproduced and lightly edited from [`https://github.com/zkat/supports-color`](https://github.com/zkat/supports-color). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

Detects whether a terminal supports color, and gives details about that
support. It takes into account the `NO_COLOR` environment variable.

This crate is a Rust port of [@sindresorhus](https://github.com/sindresorhus)'
[NPM package by the same name](https://npm.im/supports-color).

## Example

```rust
use supports_color::Stream;

if let Some(support) = supports_color::on(Stream::Stdout) {
    if support.has_16m {
        println!("16 million (RGB) colors are supported");
    } else if support.has_256 {
        println!("256 colors are supported.");
    } else if support.has_basic {
        println!("Only basic ANSI colors are supported.");
    }
} else {
    println!("No color support.");
}
```

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:supports-color-kotlin:0.1.0-SNAPSHOT")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same Apache-2.0 license as the upstream [`zkat/supports-color`](https://github.com/zkat/supports-color). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the supports-color authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`zkat/supports-color`](https://github.com/zkat/supports-color) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
