/*
 * SPDX-FileCopyrightText: 2026 StreamPort contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe.extractor.platform

/**
 * Runs YouTube's player JavaScript (signature and throttling-parameter deobfuscation).
 *
 * Same contract as the Java extractor's `JavaScript` utility and its
 * `JavaScriptInterpreter` hook, so implementations can be plugged into either.
 * JVM/Android use Mozilla Rhino; iOS uses the system JavaScriptCore.
 */
interface JavaScriptEngine {

    /**
     * Evaluates [function] (source that defines [functionName]), then calls
     * [functionName] with [parameters] and returns the result as a string.
     *
     * @throws JavaScriptException if evaluation or the call fails
     */
    fun run(function: String, functionName: String, vararg parameters: String): String

    /**
     * Checks that [function] is syntactically valid without running it.
     *
     * @throws JavaScriptException if it does not compile
     */
    fun compileOrThrow(function: String)
}

class JavaScriptException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** Returns the JavaScript engine for the current platform. */
expect fun createJavaScriptEngine(): JavaScriptEngine
