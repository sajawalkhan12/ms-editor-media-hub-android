package com.mseditor.mediahub

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.RenderProcessGoneDetail
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

class MainActivity : Activity() {

    companion object {
        private const val TAG = "MediaHubCrash"
        private const val WEBSITE_URL =
            "https://sajawalkhan12.github.io/ms-editor-media-hub-/"
    }

    private var webView: WebView? = null
    private var container: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            window.statusBarColor = Color.rgb(12, 14, 16)
            window.navigationBarColor = Color.rgb(12, 14, 16)

            val root = FrameLayout(this).apply {
                setBackgroundColor(Color.rgb(12, 14, 16))
            }

            container = root
            setContentView(root)

            createWebView(savedInstanceState)
Log.e("MediaHubCrash", "MainActivity onCreate started")
        } catch (e: Exception) {
            Log.e(TAG, "Startup exception", e)
            showError("Startup error", e)
        } catch (e: LinkageError) {
            Log.e(TAG, "Startup linkage error", e)
            showError("Android component error", e)
        }
    }

    private fun createWebView(savedState: Bundle? = null) {
        val root = container ?: return

        val browser = WebView(this)
        webView = browser

        browser.setBackgroundColor(Color.rgb(12, 14, 16))

        browser.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        browser.webChromeClient = WebChromeClient()

        browser.webViewClient = object : WebViewClient() {

            override fun onRenderProcessGone(
                view: WebView,
                detail: RenderProcessGoneDetail
            ): Boolean {
                Log.e(
                    TAG,
                    "WebView renderer terminated. " +
                        "Did crash: ${detail.didCrash()}"
                )

                val parent = view.parent as? ViewGroup
                parent?.removeView(view)

                view.stopLoading()
                view.destroy()

                if (webView === view) {
                    webView = null
                }

                try {
                    createWebView()
                } catch (e: Exception) {
                    Log.e(TAG, "WebView recovery failed", e)
                    showError("WebView recovery failed", e)
                } catch (e: LinkageError) {
                    Log.e(TAG, "WebView recovery linkage error", e)
                    showError("WebView recovery error", e)
                }

                return true
            }
        }

        root.addView(
            browser,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val restored = if (savedState != null) {
            browser.restoreState(savedState)
        } else {
            null
        }

        if (restored == null) {
            browser.loadUrl(WEBSITE_URL)
        }
    }

    private fun showError(title: String, error: Throwable) {
        val message = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 16f
            setPadding(32, 32, 32, 32)
            text = "$title\n${error.javaClass.simpleName}\n" +
                (error.message ?: "No additional details")
        }

        setContentView(message)
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
        webView?.let { browser ->
            (browser.parent as? ViewGroup)?.removeView(browser)
            browser.stopLoading()
            browser.destroy()
        }

        webView = null
        container = null

        super.onDestroy()
    }
}
