package com.pkdiv.pomodoro.webview

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebChromeClient.FileChooserParams

/**
 * Handles file uploads from the web app. The actual intent launch is delegated
 * to the host (MainActivity) via [onShowFileChooser] so it can use the modern
 * Activity Result API. No other native bridge is exposed.
 */
class PomoWebChromeClient(
    private val onShowFileChooser: (params: FileChooserParams) -> Unit
) : WebChromeClient() {

    // Kept here so the host can resolve/reject the pending upload.
    var pendingFilePathCallback: ValueCallback<Array<Uri>>? = null
        private set

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        // Reject any previously pending request to avoid leaks.
        pendingFilePathCallback?.onReceiveValue(null)
        pendingFilePathCallback = filePathCallback

        val params = fileChooserParams ?: run {
            pendingFilePathCallback = null
            return false
        }
        onShowFileChooser(params)
        return true
    }

    /**
     * Called by the host with the result of the file picker. Pass `null` to
     * cancel the upload.
     */
    fun deliverFileResult(uris: Array<Uri>?) {
        pendingFilePathCallback?.onReceiveValue(uris)
        pendingFilePathCallback = null
    }
}
