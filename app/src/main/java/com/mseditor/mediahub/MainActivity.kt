
package com.mseditor.mediahub

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.widget.TextView
import android.view.ViewGroup
import android.widget.FrameLayout

class MainActivity : Activity() {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(12, 14, 16)
        window.navigationBarColor = Color.rgb(12, 14, 16)

        try {
            val container = FrameLayout(this)
            container.setBackgroundColor(Color.rgb(12, 14, 16))
            setContentView(container)

            val browser = WebView(this)
            webView = browser

            browser.setBackgroundColor(Color.rgb(12, 14, 16))

            browser.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
            }

            browser.webViewClient = WebViewClient()
            browser.webChromeClient = WebChromeClient()

            container.addView(
                browser,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )

            val restored = if (savedInstanceState != null) {
                browser.restoreState(savedInstanceState)
            } else {
                null
            }

            if (restored == null) {
                browser.loadUrl(
                    "https://sajawalkhan12.github.io/ms-editor-media-hub-/"
                )
            }

        } catch (e: Exception) {
            Log.e("MediaHubCrash", "Startup failed", e)

            val message = TextView(this).apply {
                setTextColor(Color.WHITE)
                textSize = 16f
                setPadding(32, 32, 32, 32)
                text = "App startup error:\n${e.javaClass.simpleName}\n${e.message}"
            }

            setContentView(message)
        } catch (e: LinkageError) {
            Log.e("MediaHubCrash", "WebView startup failed", e)

            val message = TextView(this).apply {
                setTextColor(Color.WHITE)
                textSize = 16f
                setPadding(32, 32, 32, 32)
                text = "Android WebView error:\n${e.javaClass.simpleName}\n${e.message}"
            }

            setContentView(message)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView?.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val browser = webView
        if (browser != null && browser.canGoBack()) {
            browser.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        webView?.destroy()
        webView = null
        super.onDestroy()
    }
}
