package com.mseditor.mediahub

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

private val black = Color.rgb(12, 14, 16)
private val emerald = Color.rgb(0, 190, 120)
private val gold = Color.rgb(224, 184, 92)

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    window.statusBarColor = black
    window.navigationBarColor = black

    val root = LinearLayout(this)
    root.orientation = LinearLayout.VERTICAL
    root.setPadding(24, 36, 24, 24)
    root.setBackgroundColor(black)

    val title = TextView(this)
    title.text = "MS Editor Media Hub"
    title.textSize = 25f
    title.setTextColor(gold)
    title.gravity = Gravity.CENTER
    title.setPadding(0, 20, 0, 12)
    root.addView(title)

    val subtitle = TextView(this)
    subtitle.text = "Your Creative Media Library"
    subtitle.textSize = 14f
    subtitle.setTextColor(Color.LTGRAY)
    subtitle.gravity = Gravity.CENTER
    root.addView(subtitle)

    val categories = listOf(
        "🎬  Movies",
        "🎞️  Trailers",
        "📹  Videos",
        "🖼️  Images",
        "🎵  Audio",
        "✍️  Video & Image Prompts"
    )

    for (category in categories) {
        val item = TextView(this)
        item.text = category
        item.textSize = 18f
        item.setTextColor(Color.WHITE)
        item.setPadding(20, 18, 20, 18)
        item.gravity = Gravity.CENTER_VERTICAL
        item.setBackgroundColor(Color.rgb(26, 34, 31))

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 12, 0, 0)
        root.addView(item, params)
    }

    val footer = TextView(this)
    footer.text = "Powered by MS Editor"
    footer.textSize = 12f
    footer.setTextColor(emerald)
    footer.gravity = Gravity.CENTER
    footer.setPadding(0, 28, 0, 0)
    root.addView(footer)

    setContentView(root)
}

}
