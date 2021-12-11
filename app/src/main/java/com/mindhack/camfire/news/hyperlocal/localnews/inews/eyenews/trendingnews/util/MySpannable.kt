package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

open class MySpannable(isUnderline: Boolean) : ClickableSpan() {
    private var isUnderline = true
    override fun onClick(p0: View) {
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderline
        ds.color = Color.parseColor("#1b76d3")
    }
    /**
     * Constructor
     */
    init {
        this.isUnderline = isUnderline
    }
}