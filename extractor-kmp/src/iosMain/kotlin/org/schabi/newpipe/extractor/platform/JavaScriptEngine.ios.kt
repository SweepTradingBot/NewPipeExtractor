/*
 * SPDX-FileCopyrightText: 2026 StreamPort contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe.extractor.platform

import platform.JavaScriptCore.JSContext

actual fun createJavaScriptEngine(): JavaScriptEngine = JavaScriptCoreEngine()

/**
 * JavaScriptCore-backed engine (built into iOS). Third-party iOS apps run JSC without JIT,
 * which is fast enough for YouTube's deobfuscation functions.
 */
internal class JavaScriptCoreEngine : JavaScriptEngine {

    override fun run(function: String, functionName: String, vararg parameters: String): String {
        val context = JSContext()
        // Evaluating the source followed by the bare name yields the function value itself.
        // This avoids JSContext's subscript API, which Kotlin/Native does not bridge cleanly.
        // A missing name throws a ReferenceError, reported by throwIfFailed.
        val jsFunction = context.evaluateScript("$function\n;$functionName")
        context.throwIfFailed("evaluating '$functionName'")
        if (jsFunction == null) throw JavaScriptException("'$functionName' is not defined")
        val result = jsFunction.callWithArguments(parameters.toList())
        context.throwIfFailed("calling '$functionName'")
        return result?.toObject()?.toString()
            ?: throw JavaScriptException("'$functionName' returned no value")
    }

    override fun compileOrThrow(function: String) {
        // JSC has no compile-only API: wrapping the source in an uncalled function
        // makes it parse without running any of it.
        val context = JSContext()
        context.evaluateScript("(function(){\n$function\n})")
        context.throwIfFailed("compiling")
    }

    private fun JSContext.throwIfFailed(action: String) {
        val error = exception ?: return
        exception = null
        throw JavaScriptException("JavaScript error while $action: ${error.toObject()}")
    }
}
