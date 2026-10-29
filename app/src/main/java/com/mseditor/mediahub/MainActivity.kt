
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
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
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

    private lateinit var webView: WebView
    private var showingWebsite = false
    private var fileCallback: ValueCallback<Array<Uri>>? = null

    private val websiteUrl =
        "https://sajawalkhan12.github.io/ms-editor-media-hub-/"

    private val filePicker =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

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

        setupWebView()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (showingWebsite) {
                        showHome()
                    } else {
                        finish()
                    }
                }
            }
        )

        showHome()
    }

    private fun setupWebView() {
        webView = WebView(this)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode =
                WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        webView.isVerticalScrollBarEnabled = true
        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView?,
                callback: ValueCallback<Array<Uri>>?,
                params: FileChooserParams?
            ): Boolean {

                fileCallback?.onReceiveValue(null)
                fileCallback = callback

                val intent = params?.createIntent()
                    ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                    }

                return try {
                    filePicker.launch(intent)
                    true
                } catch (_: Exception) {
                    fileCallback?.onReceiveValue(null)
                    fileCallback = null
                    false
                }
            }
        }
    }

    private fun showHome() {
        showingWebsite = false

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            isVerticalScrollBarEnabled = true
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(24))
        }

        // App logo: displays the image if it exists in drawable-nodpi.
        // Otherwise, a text logo appears automatically.
        val logoId = resources.getIdentifier(
            "ms_editor_logo",
            "drawable",
            packageName
        )

        if (logoId != 0) {
            val logo = ImageView(this).apply {
                setImageResource(logoId)
                scaleType = ImageView.ScaleType.FIT_CENTER
                contentDescription = "MS Editor Media Hub Logo"
            }

            val logoParams =
                LinearLayout.LayoutParams(-1, dp(150))
            logoParams.setMargins(0, 0, 0, dp(8))
            content.addView(logo, logoParams)

        } else {
            val brand = TextView(this).apply {
                text = "MS EDITOR\nMEDIA HUB"
                textSize = 29f
                setTextColor(gold)
                typeface = Typeface.create(
                    "sans-serif-black",
                    Typeface.BOLD
                )
                gravity = Gravity.CENTER
                letterSpacing = 0.08f
            }

            content.addView(
                brand,
                LinearLayout.LayoutParams(-1, dp(105))
            )
        }

        val subtitle = TextView(this).apply {
            text = "YOUR MEDIA. YOUR CREATIVE WORLD."
            textSize = 11f
            setTextColor(emerald)
            gravity = Gravity.CENTER
            letterSpacing = 0.12f
        }

        content.addView(
            subtitle,
            LinearLayout.LayoutParams(-1, dp(32))
        )

        val hero = TextView(this).apply {
            text = "Welcome to your\nMedia Library"
            textSize = 24f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(18), dp(14), dp(18), dp(14))
            background = rounded(panel, 18, emerald)
        }

        val heroParams =
            LinearLayout.LayoutParams(-1, dp(110))
        heroParams.setMargins(0, dp(12), 0, dp(20))
        content.addView(hero, heroParams)

        val sectionTitle = TextView(this).apply {
            text = "EXPLORE YOUR MEDIA"
            textSize = 14f
            setTextColor(gold)
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.06f
        }

        val sectionParams =
            LinearLayout.LayoutParams(-1, dp(35))
        content.addView(sectionTitle, sectionParams)

        val categories = listOf(
            Triple("🎵", "Video Songs", "Watch your video songs"),
            Triple("📚", "Video Tutorial", "Learn with video tutorials"),
            Triple("✍️", "Video Prompt", "Explore video prompts"),
            Triple("🖼️", "Image Prompt", "Explore image prompts"),
            Triple("📁", "Presets & Assets", "Browse creative assets")
        )

        categories.forEach { (icon, title, description) ->

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(15), dp(10), dp(12), dp(10))
                background = rounded(panel, 14, Color.rgb(39, 57, 48))
                isClickable = true
                isFocusable = true
            }

            val iconText = TextView(this).apply {
                text = icon
                textSize = 27f
                gravity = Gravity.CENTER
            }

            card.addView(
                iconText,
                LinearLayout.LayoutParams(dp(48), dp(48))
            )

            val textColumn = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(10), 0, dp(4), 0)
            }

            val name = TextView(this).apply {
                text = title
                textSize = 16f
                setTextColor(gold)
                typeface = Typeface.DEFAULT_BOLD
            }

            val detail = TextView(this).apply {
                text = description
                textSize = 12f
                setTextColor(Color.LTGRAY)
            }

            textColumn.addView(name)
            textColumn.addView(detail)

            card.addView(
                textColumn,
                LinearLayout.LayoutParams(0, -2, 1f)
            )

            val arrow = TextView(this).apply {
                text = "›"
                textSize = 30f
                setTextColor(emerald)
                gravity = Gravity.CENTER
            }

            card.addView(
                arrow,
                LinearLayout.LayoutParams(dp(28), dp(48))
            )

            val cardParams =
                LinearLayout.LayoutParams(-1, dp(76))
            cardParams.setMargins(0, 0, 0, dp(11))
            content.addView(card, cardParams)

            card.setOnClickListener {
                openMediaHub(title)
            }
        }

        val openButton = Button(this).apply {
            text = "OPEN MEDIA HUB  →"
            textSize = 15f
            setTextColor(Color.BLACK)
            isAllCaps = false
            background = rounded(emerald, 14, emerald)
            setOnClickListener {
                openMediaHub(null)
            }
        }

        val openParams =
            LinearLayout.LayoutParams(-1, dp(52))
        openParams.setMargins(0, dp(8), 0, dp(10))
        content.addView(openButton, openParams)

        val exitButton = Button(this).apply {
            text = "EXIT APP"
            textSize = 13f
            setTextColor(gold)
            isAllCaps = false
            background = rounded(bg, 12, gold)
            setOnClickListener {
                finish()
            }
        }

        content.addView(
            exitButton,
            LinearLayout.LayoutParams(-1, dp(48))
        )

        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun openMediaHub(category: String?) {
        showingWebsite = true

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }

        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(4), dp(8), dp(4))
            setBackgroundColor(panel)
        }

        val backButton = Button(this).apply {
            text = "‹ Home"
            isAllCaps = false
            setTextColor(gold)
            setBackgroundColor(Color.TRANSPARENT)
            setOnClickListener { showHome() }
        }

        val title = TextView(this).apply {
            text = category ?: "Media Hub"
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(white)
            gravity = Gravity.CENTER
        }

        bar.addView(
            backButton,
            LinearLayout.LayoutParams(dp(92), dp(48))
        )

        bar.addView(
            title,
            LinearLayout.LayoutParams(0, dp(48), 1f)
        )

        page.addView(bar)

        (webView.parent as? ViewGroup)?.removeView(webView)

        page.addView(
            webView,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        setContentView(page)

        if (webView.url == null) {
            webView.loadUrl(websiteUrl)
        }

        if (category != null) {
            Toast.makeText(
                this,
                "$category selected. Website content is shared.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun rounded(
        color: Int,
        radius: Int,
        stroke: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(1), stroke)
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
