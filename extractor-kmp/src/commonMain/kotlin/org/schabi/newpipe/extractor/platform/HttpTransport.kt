/*
 * SPDX-FileCopyrightText: 2026 StreamPort contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe.extractor.platform

/**
 * Minimal HTTP abstraction mirroring the Java extractor's `Downloader`
 * (GET / HEAD / POST with headers and a byte body).
 *
 * Implemented in Phase 1 with Ktor (OkHttp engine on JVM/Android, Darwin engine on iOS).
 */
interface HttpTransport {
    fun execute(request: HttpRequest): HttpResponse
}

class HttpRequest(
    val method: String,
    val url: String,
    val headers: Map<String, List<String>> = emptyMap(),
    val body: ByteArray? = null,
)

class HttpResponse(
    val code: Int,
    val message: String,
    val headers: Map<String, List<String>>,
    val body: String,
    val latestUrl: String,
)
