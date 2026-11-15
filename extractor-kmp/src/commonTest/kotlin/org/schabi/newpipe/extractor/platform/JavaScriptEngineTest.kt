/*
 * SPDX-FileCopyrightText: 2026 StreamPort contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe.extractor.platform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

// Runs on every target: Rhino on JVM, JavaScriptCore on the iOS simulator (macOS CI).
class JavaScriptEngineTest {

    private val engine = createJavaScriptEngine()

    @Test
    fun runsNamedFunctionWithStringArguments() {
        // Same shape as YouTube's throttling-parameter function: string in, string out
        val js = "function decode(a){var b=a.split('');b.reverse();return b.join('')+'-ok';}"
        assertEquals("cba-ok", engine.run(js, "decode", "abc"))
    }

    @Test
    fun passesMultipleParameters() {
        val js = "function join(a, b){return a + '|' + b;}"
        assertEquals("x|y", engine.run(js, "join", "x", "y"))
    }

    @Test
    fun compileOrThrowAcceptsValidCode() {
        engine.compileOrThrow("function ok(a){return a;}")
    }

    @Test
    fun compileOrThrowRejectsSyntaxErrors() {
        assertFailsWith<JavaScriptException> { engine.compileOrThrow("function broken( {") }
    }

    @Test
    fun runThrowsForMissingFunction() {
        assertFailsWith<JavaScriptException> { engine.run("var x = 1;", "nope") }
    }
}
