/*
 * SPDX-FileCopyrightText: 2026 StreamPort contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe.extractor.platform

import platform.JavaScriptCore.JSContext
import platform.JavaScriptCore.JSValue

actual fun createJavaScriptEngine(): JavaScriptEngine = JavaScriptCoreEngine()

/**
 * JavaScriptCore-backed engine (built into iOS). Third-party iOS apps run JSC without JIT,
 * which is fast enough for YouTube's deobfuscation functions.
 */
internal class JavaScriptCoreEngine : JavaScriptEngine {

    override fun run(function: String, functionName: String, vararg parameters: String): String {
        val context = JSContext()
        context.evaluateScript(function)
        context.throwIfFailed("evaluating '$functionName'")
        // JSContext's subscript is bridged as Any?, so cast to JSValue explicitly.
        // Calling a non-function (e.g. undefined) sets context.exception, handled below.
        val jsFunction = context.objectForKeyedSubscript(functionName) as? JSValue
            ?: throw JavaScriptException("'$functionName' is not defined")
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
