/*
 * SPDX-FileCopyrightText: 2026 StreamPort contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package org.schabi.newpipe.extractor.platform

/**
 * Supplies YouTube proof-of-origin tokens, mirroring the Java extractor's `PoTokenProvider`.
 *
 * Android generates these by running Google's BotGuard script in a hidden WebView
 * (`po_token.html` in the app). The iOS implementation will do the same with WKWebView.
 */
interface PoTokenSource {

    /** Returns tokens for the WEB InnerTube client, or null when none can be produced. */
    fun webClientPoToken(videoId: String): PoToken?
}

data class PoToken(
    val visitorData: String,
    val playerRequestPoToken: String,
    val streamingDataPoToken: String?,
)
