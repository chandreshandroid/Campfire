package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import java.util.regex.Pattern

/**
 * Created by dhavalkaka on 06/02/2018.
 */
class PostDesTextView : AppCompatTextView {
    var mContext: Context?
    var mListener: OnCustomEventListener? = null
    var mHashListener: OnHashEventListener? = null
    /* Custom method because standard getMaxLines() requires API > 16 */ var myMaxLines =
        Int.MAX_VALUE
        private set
    var isFullText = false
    var expandText = "...Continue reading"

    constructor(context: Context?) : super(context!!) {
        mContext = context
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
        mContext = context
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr) {
        mContext = context
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        post {
            if (!isFullText && lineCount > MAX_LINES) {
                val lineEndIndex =
                    layout.getLineEnd(MAX_LINES - 1)
                val text = text.subSequence(
                    0,
                    lineEndIndex - expandText.length + 1
                )
                    .toString() + " " + expandText
                setText(text)
                movementMethod = LinkMovementMethod.getInstance()
                setText(
                    addClickablePartTextViewResizable(
                        getText()
                            .toString(), MAX_LINES, expandText,
                        true
                    ), BufferType.SPANNABLE
                )
            }
        }
    }

    override fun setMaxLines(maxLines: Int) {
        myMaxLines = maxLines
        super.setMaxLines(maxLines)
    }

    fun doResizeTextView(
        maxLine: Int,
        expandText: String?,
        viewMore: Boolean
    ) {
        MAX_LINES = maxLine
        maxLines = maxLine
        if (tag == null) {
            tag = text
        }
        val jo = text.toString()
        movementMethod = LinkMovementMethod.getInstance()
        val ssb = hashTag(jo, mContext)
        text = ssb
        /*    ViewTreeObserver vto = getViewTreeObserver();
      vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = getLayout().getLineEnd(0);
                    String text = getText().subSequence(0,
                            lineEndIndex - expandText.length() + 1)
                            + " " + expandText;
                    setText(text);
                    setMovementMethod(LinkMovementMethod.getInstance());
                    setText(
                            addClickablePartTextViewResizable(getText()
                                            .toString(),  maxLine, expandText,
                                    viewMore), BufferType.SPANNABLE);
                } else if (maxLine > 0 && getLineCount() >= maxLine) {
                    int lineEndIndex = getLayout().getLineEnd(maxLine - 1);
                    String text = getText().subSequence(0,
                            lineEndIndex - expandText.length() + 1)
                            + " " + expandText;
                    setText(text);
                    setMovementMethod(LinkMovementMethod.getInstance());
                    setText(
                            addClickablePartTextViewResizable(getText()
                                            .toString(),  maxLine, expandText,
                                    viewMore), BufferType.SPANNABLE);
                }
            }
        });
*/
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: String, maxLine: Int,
        spanableText: String, viewMore: Boolean
    ): SpannableStringBuilder {
        val ssb =
            SpannableStringBuilder(strSpanned)
        if (strSpanned.contains(spanableText)) {
            ssb.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        if (viewMore) {
                            mListener!!.onViewMore()
                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }, strSpanned.indexOf(spanableText),
                strSpanned.indexOf(spanableText) + spanableText.length, 0
            )
            ssb.setSpan(
                ForegroundColorSpan(Color.BLUE),
                strSpanned.indexOf(spanableText),
                strSpanned.indexOf(spanableText) + spanableText.length,
                0
            )
        }
        return ssb
    }

    interface OnCustomEventListener {
        fun onViewMore()
        fun onFriendTagClick(friendsId: String?)

    }
    interface OnHashEventListener {

        fun onHashTagClick(friendsId: String?)

    }

    fun setCustomEventListener(eventListener: OnCustomEventListener?) {
        mListener = eventListener
    }
    fun setHashClickListener(eventListener1: OnHashEventListener?) {
        mHashListener = eventListener1
    }

    inner class FriendsClickableSpan(var friendId: String) : ClickableSpan() {

        override fun onClick(widget: View) {

            mListener!!.onFriendTagClick(friendId)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            if (mContext != null) ds.color = mContext!!.resources.getColor(R.color.text_blue)
        }

    }


    inner class HashTagClickableSpan(var hashTag: String) : ClickableSpan() {

        override fun onClick(widget: View) {

            mHashListener!!.onHashTagClick(hashTag)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            if (mContext != null) ds.color = mContext!!.resources.getColor(R.color.text_blue)
        }

    }

    fun hashTag(
        text: String,
        mContext: Context?
    ): SpannableStringBuilder {
        var text = text
        if (text.contains("<Shase>")) {
            text = text.replace("<Shase>".toRegex(), "#")
            text = text.replace("<Chase>".toRegex(), "")
        }
        val ssb = SpannableStringBuilder(text)
        val MY_PATTERN = Pattern.compile("#(\\w+)")
        val mat = MY_PATTERN.matcher(text)
        while (mat.find()) {
            Log.d("match", mat.group())

            if (mat.group(1) != null && mat.group(1).isNotEmpty()) {
                ssb.setSpan(
                    HashTagClickableSpan(mat.group()),
                    mat.start(),
                    mat.start() + mat.group().length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                ssb.setSpan(
                    ForegroundColorSpan(
                        mContext!!.resources.getColor(
                            R.color.text_blue
                        )
                    ), mat.start(), mat.start() + mat.group().length, 0
                )
                ssb.setSpan(
                    StyleSpan(Typeface.BOLD),
                    mat.start(),
                    mat.start() + mat.group().length,
                    0
                )
            }
        }
        // create the pattern matcher
        val m =
            Pattern.compile("<SatRate>(.+?)<CatRate>").matcher(text)

        var matchesSoFar = 0
        // iterate through all matches
        while (m.find()) { // get the match
            if (m.group().contains(",")) {
                val word = m.group()
                // remove the first and last characters of the match
                val friendTagName = word.substring(9, word.indexOf(","))
                var id: String? = null
                var friendId = ""
                if (word.contains(",")) {
                    friendId = word.substring(word.indexOf(",") + 2, word.length - 9)
                }
                if (word.contains(",")) {
                    id = word.substring(word.indexOf(","), word.indexOf("<CatRate>"))
                }
                // clear the string buffer
                val start = m.start() - matchesSoFar
                val end = m.end() - matchesSoFar
                Log.d("friendTagName", "$friendTagName in$start")
                ssb.setSpan(
                    FriendsClickableSpan(friendId),
                    start + 9,
                    start + 9 + friendTagName.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                ssb.setSpan(
                    ForegroundColorSpan(
                        mContext!!.resources.getColor(
                            R.color.text_blue
                        )
                    ),
                    start + 9,
                    start + 9 + friendTagName.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                ssb.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start + 9,
                    start + 9 + friendTagName.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                ssb.delete(start, start + 9)
                val start1 = start + 9
                val end1 = start1 + friendTagName.length - 9
                ssb.delete(end1, end - 9)
                matchesSoFar += 18 + id!!.length
            }
        }
        return ssb
    }

    fun displayFulltext(text: String) {
        isFullText = true
        maxLines = myMaxLines
        setText(hashTag(text, mContext))
        movementMethod = LinkMovementMethod.getInstance()
    }

    companion object {
        private var MAX_LINES = 5
    }
}