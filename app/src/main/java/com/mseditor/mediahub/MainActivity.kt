
package com.mseditor.mediahub

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var fileCallback: ValueCallback<Array<Uri>>? = null

    private val filePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val callback = fileCallback
            fileCallback = null

            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val uris = mutableListOf<Uri>()

                data?.clipData?.let { clip ->
                    for (i in 0 until clip.itemCount) {
                        uris.add(clip.getItemAt(i).uri)
                    }
                }

                if (uris.isEmpty()) {
                    data?.data?.let { uris.add(it) }
                }

                callback?.onReceiveValue(uris.toTypedArray())
            } else {
                callback?.onReceiveValue(null)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    try {
        webView = WebView(this)
        setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode =
                WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@MainActivity.fileCallback
                    ?.onReceiveValue(null)

                this@MainActivity.fileCallback =
                    filePathCallback

                val intent = fileChooserParams?.createIntent()
                    ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                    }

                return try {
                    filePicker.launch(intent)
                    true
                } catch (e: Exception) {
                    Log.e("MSMediaHub", "File picker error", e)
                    this@MainActivity.fileCallback
                        ?.onReceiveValue(null)
                    this@MainActivity.fileCallback = null
                    false
                }
            }
        }

        webView.loadUrl(
            "https://sajawalkhan12.github.io/ms-editor-media-hub-/"
        )

    } catch (e: Exception) {
        Log.e("MSMediaHub", "Startup error", e)

        val errorText = android.widget.TextView(this).apply {
            text = "App startup error:\n${e.javaClass.simpleName}\n${e.message}"
            textSize = 16f
            setPadding(32, 32, 32, 32)
        }

        setContentView(errorText)
    }
}
}
