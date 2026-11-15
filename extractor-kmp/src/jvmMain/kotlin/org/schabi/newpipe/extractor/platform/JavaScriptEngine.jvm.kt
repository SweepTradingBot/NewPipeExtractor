/*
 * SPDX-FileCopyrightText: 2026 StreamPort contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe.extractor.platform

import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.RhinoException

actual fun createJavaScriptEngine(): JavaScriptEngine = RhinoJavaScriptEngine

/** Rhino-backed engine; behaves like the Java extractor's `JavaScript` utility. */
internal object RhinoJavaScriptEngine : JavaScriptEngine {

    override fun run(function: String, functionName: String, vararg parameters: String): String =
        Context.enter().use { context ->
            context.setInterpretedMode(true)
            try {
                val scope = context.initSafeStandardObjects()
                context.evaluateString(scope, function, functionName, 1, null)
                val jsFunction = scope.get(functionName, scope) as? Function
                    ?: throw JavaScriptException("'$functionName' is not a function")
                val args: Array<Any> = arrayOf(*parameters)
                jsFunction.call(context, scope, scope, args).toString()
            } catch (e: RhinoException) {
                throw JavaScriptException(e.message ?: "JavaScript error", e)
            }
        }

    override fun compileOrThrow(function: String) {
        Context.enter().use { context ->
            context.setInterpretedMode(true)
            try {
                context.compileString(function, null, 1, null)
            } catch (e: RhinoException) {
                throw JavaScriptException(e.message ?: "JavaScript syntax error", e)
            }
        }
    }
}
