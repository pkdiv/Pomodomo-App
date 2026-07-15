package com.pkdiv.pomodoro.webview

import android.webkit.WebSettings
import android.webkit.WebView
import com.pkdiv.pomodoro.BuildConfig

/**
 * Applies the WebView configuration required by AGENTS.md.
 *
 * The web app owns all UI, timer, and business logic. The native layer only
 * enables the WebView to host it well and securely:
 *  - JavaScript, DOM storage, localStorage, IndexedDB, cookies on
 *  - mixed content never allowed (HTTPS only)
 *  - file access enabled for uploads
 *  - debugging forced OFF in every build (security)
 */
fun WebView.configurePomodoroWebView() {
    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        allowFileAccess = true
        allowContentAccess = true
        setAllowFileAccessFromFileURLs(false)
        setAllowUniversalAccessFromFileURLs(false)

        cacheMode = WebSettings.LOAD_DEFAULT
        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        loadWithOverviewMode = true
        useWideViewPort = true
        builtInZoomControls = false
        displayZoomControls = false
        saveFormData = true
        userAgentString = "$userAgentString PomodoroApp/${BuildConfig.VERSION_NAME}"

        // Respect the web app's viewport / responsive layout.
        mediaPlaybackRequiresUserGesture = false
    }

    // Security: never expose WebView debugging, even in debug builds.
    WebView.setWebContentsDebuggingEnabled(false)

    // No unrestricted JavaScript interfaces are exposed to the page.
}

/**
 * Hides web elements that shouldn't appear in the native wrapper. The web app
 * owns its UI, but the Android layer hides elements that don't make sense here
 * (e.g. a "Buy me a chai" donation button). Matched by text so it survives
 * styling/class changes, and watched via MutationObserver for late SPA renders.
 */
internal fun WebView.hideUnsupportedElements() {
    val script = """
        (function () {
            function hide() {
                var nodes = document.querySelectorAll('a, button');
                for (var i = 0; i < nodes.length; i++) {
                    if (/buy\s*me\s*a\s*chai/i.test(nodes[i].textContent || '')) {
                        nodes[i].style.display = 'none';
                        var parent = nodes[i].parentElement;
                        if (parent && parent.childElementCount === 1) {
                            parent.style.display = 'none';
                        }
                        return true;
                    }
                }
                return false;
            }
            if (hide()) return;
            var observer = new MutationObserver(function () {
                if (hide()) observer.disconnect();
            });
            observer.observe(document.documentElement, { childList: true, subtree: true });
            setTimeout(function () { observer.disconnect(); }, 10000);
        })();
    """.trimIndent()
    evaluateJavascript(script, null)
}
