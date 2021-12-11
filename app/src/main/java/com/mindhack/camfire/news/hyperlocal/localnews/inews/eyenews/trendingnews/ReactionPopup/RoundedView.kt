package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.ReactionPopup

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.View

/**
 * Reaction selector floating dialog background.
 */
@SuppressLint("ViewConstructor")
class RoundedView(context: Context, private val config: ReactionsConfig) : View(context) {

    private val tag = RoundedView::class.java.simpleName

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
       // background=(config.popupColor)
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.WHITE

        setShadowLayer(5.0f, 0.0f, 2.0f, Color.WHITE)
    }

    private var cornerSize = 0f

    private var xStart = 0f
    private var yStart = 0f
    private val radius = height / 2

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        Log.d(tag, "onSizeChanged: w = $w; h = $h; oldW = $oldW; oldH = $oldH")

        val xPad = if (paddingLeft + paddingRight <= 0) {
            config.horizontalMargin * 2f
        } else {
            (paddingLeft + paddingRight) / 2f
        }
        val bPad = xPad / 2
        val nIcons = config.reactions.size
        val regIconSize = (w - (2 * xPad + (nIcons - 1) * bPad)) / nIcons
        cornerSize = xPad + regIconSize / 2
        xStart = cornerSize
        yStart = 0f

        Log.d(tag, "onSizeChanged: padding left = " + paddingLeft + "; padding right = " + paddingRight +
                "; padding top = " + paddingTop + "; padding bottom = " + paddingBottom)
        Log.d(tag, "onSizeChanged: xStart = " + (x + xStart) + "; cornerSize = " + cornerSize + "; x = " + x)
    }

    private val path = Path()
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        // Draw the background rounded rectangle
        path.moveTo(xStart, yStart)

        // Top line between curves
        path.lineTo(xStart + (width - 2 * cornerSize), yStart)

        // First curve, right side
        rect.left = xStart + width - 2 * cornerSize
        rect.right = rect.left + cornerSize
        rect.top = yStart
        rect.bottom = yStart + height
        path.arcTo(rect, 270f, 180f)

        // Bottom line between curves
        path.lineTo(xStart, yStart + height)

        // Second curve, left side
        rect.left = xStart - cornerSize
        rect.right = xStart
        rect.top = yStart
        rect.bottom = yStart + height
        path.arcTo(rect, 90f, 180f)
        path.close()

        canvas.drawPath(path, paint)
        rect.set(LEFT, y, LEFT + WIDTH, y + height)
        canvas.drawRoundRect(rect, radius.toFloat(), radius.toFloat(), paint!!)
        path.reset()
    }

    companion object {
        var HORIZONTAL_SPACING = DisplayUtil.dpToPx(4)

        var VERTICAL_SPACING = DisplayUtil.dpToPx(8)

        var HEIGHT_VIEW_REACTION = DisplayUtil.dpToPx(450)

        val MAX_ALPHA = 255

        val SMALL_SIZE = DisplayUtil.dpToPx(24)
        val MEDIUM_SIZE = DisplayUtil.dpToPx(32)
        val LARGE_SIZE = DisplayUtil.dpToPx(72)

        internal val WIDTH = 6 * MEDIUM_SIZE + 7 * HORIZONTAL_SPACING

        internal val HEIGHT = DisplayUtil.dpToPx(48)

        internal val SCALED_DOWN_HEIGHT = DisplayUtil.dpToPx(38)

        internal val LEFT = DisplayUtil.dpToPx(16).toFloat()

        internal val BOTTOM = (HEIGHT_VIEW_REACTION - 200).toFloat()

        internal val TOP = BOTTOM - HEIGHT

        internal val BASE_LINE = TOP + MEDIUM_SIZE + VERTICAL_SPACING



    }

    object DisplayUtil {

        fun dpToPx(dp: Int): Int {
            val metrics = Resources.getSystem().displayMetrics
            return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}
