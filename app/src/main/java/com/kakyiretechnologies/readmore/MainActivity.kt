package com.kakyiretechnologies.readmore

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import kotlin.math.min

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.tvLong)
        textView.text = getString(R.string.long_text)
        textView.addReadMore {
            Toast.makeText(this, "Clickable is working", Toast.LENGTH_SHORT).show()
        }
    }
}

fun TextView.addReadMore(
    actionLabel: String = "Read More",
    onClick: (String) -> Unit
) {
    var result = text.toString()
    val originalText = text.toString()

    val onPreDrawListener = object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)

            if (layout != null) {
                val lineCount = layout.lineCount
                val hasEllipses = if (lineCount > 0) {
                    layout.getEllipsisCount(maxLines - 1) > 0
                } else {
                    false
                }


                if (hasEllipses) {

                    val ellipsizeCount = layout.getEllipsisCount(lineCount - 1)


                    val readMore = "... $actionLabel"
                    result = result.substring(0, result.length - ellipsizeCount - readMore.length)
                        .plus(readMore)

                    val spannableText = SpannableString(result)

                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            widget.invalidate()
                            onClick(originalText)
                        }

                        override fun updateDrawState(textPaint: TextPaint) {
                            textPaint.apply {
                                isUnderlineText = true
                                this.color = Color.YELLOW
                            }
                        }
                    }

                    // Set the ClickableSpan for "Read More"
                    val start = result.indexOf(actionLabel, 0)
                    val end = start + actionLabel.length

                    spannableText.setSpan(
                        clickableSpan,
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    this@addReadMore.text =  spannableText

                }


            }
            movementMethod = LinkMovementMethod.getInstance()
            return true
        }
    }

    viewTreeObserver.addOnPreDrawListener(onPreDrawListener)

}


