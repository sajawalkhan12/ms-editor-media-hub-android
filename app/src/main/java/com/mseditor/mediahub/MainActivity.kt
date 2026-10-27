package com.mseditor.mediahub

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    private val bg = Color.rgb(8, 12, 11)
    private val panel = Color.rgb(18, 27, 23)
    private val emerald = Color.rgb(0, 190, 120)
    private val gold = Color.rgb(230, 190, 90)
    private val white = Color.rgb(244, 247, 245)
    private lateinit var root: LinearLayout
    private lateinit var webView: WebView
    private var showingWebsite = false
    private var fileCallback: ValueCallback<Array<Uri>>? = null

    private val filePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val callback = fileCallback
        fileCallback = null
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uris = mutableListOf<Uri>()
            data?.clipData?.let { clip -> for (i in 0 until clip.itemCount) uris.add(clip.getItemAt(i).uri) }
            if (uris.isEmpty()) data?.data?.let { uris.add(it) }
            callback?.onReceiveValue(uris.toTypedArray())
        } else callback?.onReceiveValue(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (showingWebsite) showHome() else finish()
            }
        })
        setupWebView()
        showHome()
    }

    private fun setupWebView() {
        webView = WebView(this)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(view: WebView?, callback: ValueCallback<Array<Uri>>?, params: FileChooserParams?): Boolean {
                fileCallback?.onReceiveValue(null)
                fileCallback = callback
                val intent = params?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE); type = "*/*"
                }
                return try { filePicker.launch(intent); true } catch (_: Exception) {
                    fileCallback?.onReceiveValue(null); fileCallback = null; false
                }
            }
        }
    }

    private fun showHome() {
        showingWebsite = false
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(18), dp(20), dp(18))
        }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val brand = TextView(this).apply {
            text = "MS EDITOR\nMEDIA HUB"
            textSize = 29f; setTextColor(gold); typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
            gravity = Gravity.CENTER; letterSpacing = 0.08f
        }
        content.addView(brand, LinearLayout.LayoutParams(-1, dp(92)))
        val subtitle = TextView(this).apply {
            text = "YOUR MEDIA. YOUR CREATIVE WORLD."
            textSize = 11f; setTextColor(emerald); gravity = Gravity.CENTER; letterSpacing = 0.12f
        }
        content.addView(subtitle, LinearLayout.LayoutParams(-1, dp(30)))
        val hero = TextView(this).apply {
            text = "Welcome to your\nMedia Library"
            textSize = 24f; setTextColor(white); typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL; setPadding(dp(18), dp(14), dp(18), dp(14))
            background = rounded(panel, 18, emerald)
        }
        val heroLp = LinearLayout.LayoutParams(-1, dp(115)); heroLp.setMargins(0, dp(14), 0, dp(18))
        content.addView(hero, heroLp)
        val categories = listOf(
            "🎬  Movie" to "Browse movies and media",
            "✨  Trailer" to "Watch movie trailers",
            "📚  Video Tutorial" to "Learn with video tutorials",
            "✍️  Video Prompt" to "Open saved video prompts",
            "🖼️  Image Prompt" to "Open saved image prompts",
            "🎵  Audio" to "Listen to audio content"
        )
        categories.forEach { (title, desc) ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(12), dp(16), dp(12))
                background = rounded(panel, 14, Color.rgb(39, 57, 48))
            }
            val name = TextView(this).apply { text = title; textSize = 17f; setTextColor(gold); typeface = Typeface.DEFAULT_BOLD }
            val detail = TextView(this).apply { text = desc; textSize = 12f; setTextColor(Color.LTGRAY) }
            card.addView(name); card.addView(detail)
            val lp = LinearLayout.LayoutParams(-1, dp(78)); lp.setMargins(0, 0, 0, dp(10))
            content.addView(card, lp)
            card.setOnClickListener { openMediaHub(title.substringAfter("  ")) }
        }
        val open = Button(this).apply {
            text = "OPEN MEDIA HUB  →"; textSize = 15f; setTextColor(Color.BLACK)
            background = rounded(emerald, 14, emerald); isAllCaps = false
            setOnClickListener { openMediaHub(null) }
        }
        val openLp = LinearLayout.LayoutParams(-1, dp(54)); openLp.setMargins(0, dp(4), 0, dp(8))
        content.addView(open, openLp)
        val exit = Button(this).apply {
            text = "EXIT APP"; textSize = 13f; setTextColor(gold); isAllCaps = false
            background = rounded(bg, 12, gold); setOnClickListener { finish() }
        }
        content.addView(exit, LinearLayout.LayoutParams(-1, dp(48)))
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun openMediaHub(category: String?) {
        showingWebsite = true
        val page = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(bg) }
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(4), dp(8), dp(4)); setBackgroundColor(panel)
        }
        val back = Button(this).apply { text = "‹ Home"; isAllCaps = false; setTextColor(gold); setBackgroundColor(Color.TRANSPARENT); setOnClickListener { showHome() } }
        val title = TextView(this).apply { text = category ?: "Media Hub"; textSize = 16f; typeface = Typeface.DEFAULT_BOLD; setTextColor(white); gravity = Gravity.CENTER }
        bar.addView(back, LinearLayout.LayoutParams(dp(92), dp(48)))
        bar.addView(title, LinearLayout.LayoutParams(0, dp(48), 1f))
        page.addView(bar)
        (webView.parent as? android.view.ViewGroup)?.removeView(webView)
        page.addView(webView, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(page)
        if (webView.url == null) webView.loadUrl("https://sajawalkhan12.github.io/ms-editor-media-hub-/")
        if (category != null) Toast.makeText(this, "Media Hub opened. Select ‘$category’ in the website if available.", Toast.LENGTH_LONG).show()
    }

    private fun rounded(color: Int, radius: Int, stroke: Int): GradientDrawable = GradientDrawable().apply {
        setColor(color); cornerRadius = dp(radius).toFloat(); setStroke(dp(1), stroke)
    }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
