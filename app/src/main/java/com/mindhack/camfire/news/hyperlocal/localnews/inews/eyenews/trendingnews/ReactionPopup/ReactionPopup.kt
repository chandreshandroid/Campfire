package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.ReactionPopup

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow

/**
 * Entry point for reaction popup.
 */
class ReactionPopup @JvmOverloads constructor(
    context: Context,
    reactionsConfig: ReactionsConfig,
    var reactionSelectedListener: ReactionSelectedListener? = null
) : PopupWindow(context), View.OnLongClickListener {

    private val rootView = FrameLayout(context).also {
        it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
    }
    private val view: ReactionViewGroup by lazy(LazyThreadSafetyMode.NONE) {
        // Lazily inflate content during first display
        ReactionViewGroup(context, reactionsConfig).also {
            it.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)

            it.reactionSelectedListener = reactionSelectedListener

            rootView.addView(it)
        }.also { it.dismissListener = ::dismiss }
    }

    init {
        contentView = rootView
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
        isFocusable = true
        isTouchable = true
        isOutsideTouchable = true

        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    /*@SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {

        if (!isShowing) {
            // Show fullscreen with button as context provider
            showAtLocation(v, Gravity.NO_GRAVITY, 0, 0)
            view.show(event, v)
        }
        return view.onTouchEvent(event)
    }*/

    override fun dismiss() {
        view.dismiss()
        super.dismiss()
    }

    override fun onLongClick(p0: View?): Boolean {
        if (!isShowing) {
            showAtLocation(p0, Gravity.NO_GRAVITY, 0, 0)
            view.show(p0!!)
        }
        return true
    }


}
