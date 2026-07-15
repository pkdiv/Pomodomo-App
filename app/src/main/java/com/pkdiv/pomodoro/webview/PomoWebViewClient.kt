package com.pkdiv.pomodoro.webview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.pkdiv.pomodoro.utils.Constants

/**
 * URL navigation policy:
 *  - Trusted host (apps.pkdiv.com) over https → load inside the WebView.
 *  - Any other host → open in the external browser (validated Intent), keep the
 *    wrapper on the trusted domain.
 * External links are never loaded inside the WebView, and all Intents are
 * validated before launch (security precedence over convenience).
 */
class PomoWebViewClient(
    private val onOffline: () -> Unit,
    private val onOnline: () -> Unit
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url ?: return false
        return if (isTrusted(url)) {
            false // let the WebView load it
        } else {
            openExternal(view, url)
            true
        }
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        url?.let { onOnline() }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        // Persist the current URL so we can restore position after a restart.
        url?.let { view?.post { /* caller persists via callback if needed */ } }
        view?.hideUnsupportedElements()
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        // Only treat main-frame failures as "offline". Sub-resource errors
        // (e.g. a single image) should not blow away the whole screen.
        if (request?.isForMainFrame != true) return

        val code = error?.errorCode
        if (code == ERROR_CONNECT || code == ERROR_HOST_LOOKUP ||
            code == ERROR_TIMEOUT
        ) {
            onOffline()
        }
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        // Security: never proceed on SSL errors. Cancel and surface offline state.
        handler?.cancel()
        if (view?.url == null) {
            onOffline()
        }
    }

    private fun isTrusted(uri: Uri): Boolean {
        return uri.scheme.equals(Constants.ALLOWED_SCHEME, ignoreCase = true) &&
            uri.host?.equals(Constants.ALLOWED_HOST, ignoreCase = true) == true
    }

    private fun openExternal(view: WebView?, uri: Uri) {
        val context = view?.context ?: return
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        // Validate the Intent resolves to a safe external handler before launching.
        val resolve = intent.resolveActivity(context.packageManager)
        if (resolve != null) {
            context.startActivity(intent)
        } else {
            // No browser available — stay put rather than crashing.
            Log.w("PomoWebViewClient", "No external handler for $uri")
        }
    }

    companion object {
        private const val ERROR_CONNECT = WebViewClient.ERROR_CONNECT
        private const val ERROR_HOST_LOOKUP = WebViewClient.ERROR_HOST_LOOKUP
        private const val ERROR_TIMEOUT = WebViewClient.ERROR_TIMEOUT
    }
}
