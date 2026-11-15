/*
 * SPDX-FileCopyrightText: 2026 StreamPort contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

// Multiplatform foundation for porting NewPipeExtractor to iOS.
// Phase 0: platform abstractions (JavaScript engine, HTTP, PoToken) with JVM + iOS actuals.
// The Java extractor in :extractor stays the source of truth until code moves here.

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvmToolchain(17)

    jvm()

    // iOS targets only compile on macOS (e.g. the GitHub Actions macOS runner).
    // On Windows/Linux Gradle skips them with a warning.
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "StreamPortExtractor"
            isStatic = true
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(libs.mozilla.rhino.core)
        }
    }
}
