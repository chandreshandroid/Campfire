package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import java.util.regex.Pattern

/**
 * Created by dhavalkaka on 03/02/2018.
 */
object SpannableBuilder {
    fun hashTag(text: String, mContext: Context): SpannableStringBuilder {
        var text = text
        Log.d("hashText", text)
        if (text.contains("<Shase>")) {
            text = text.replace("<Shase>".toRegex(), "#")
            text = text.replace("<Chase>".toRegex(), "")
        }
        val ssb = SpannableStringBuilder(text)
        val MY_PATTERN = Pattern.compile("#(\\w+)")
        val mat = MY_PATTERN.matcher(text)
        while (mat.find()) {
            Log.d("match", mat.group())
            if (mat.group(1) != null && mat.group(1).length >= 1) {
                ssb.setSpan(
                    null,
                    mat.start(),
                    mat.start() + mat.group().length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                ssb.setSpan(
                    ForegroundColorSpan(mContext.resources.getColor(R.color.text_blue)),
                    mat.start(),
                    mat.start() + mat.group().length,
                    0
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
                val friendTagName = word.substring(9, word.length - 13)
                var friendId = ""
                if (word.contains(",")) {
                    friendId = word.substring(word.indexOf(",") + 2, word.length - 9)
                }
                // clear the string buffer
                val start = m.start() - matchesSoFar * 23
                val end = m.end() - matchesSoFar * 23
                Log.d("friendTagName", "$friendTagName in$start")
                ssb.setSpan(
                    FriendsClickableSpan(friendId),
                    start + 9,
                    start + 9 + friendTagName.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                ssb.setSpan(
                    ForegroundColorSpan(mContext.resources.getColor(R.color.text_blue)),
                    start + 9,
                    start + 9 + friendTagName.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                //ssb.setSpan(new StyleSpan(Typeface.BOLD), start + 9, start + 9 + friendTagName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(
                    AbsoluteSizeSpan(mContext.resources.getDimensionPixelSize(R.dimen._16sdp)),
                    start + 9,
                    start + 9 + friendTagName.length,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
                ssb.delete(start, start + 9)
                ssb.delete(end - 23, end - 9)
                matchesSoFar++
            }
        }
        return ssb
    }

    fun replaceHashTag(text: String): String {
        var text = text
        val MY_PATTERN = Pattern.compile("#(\\w+)")
        val mat = MY_PATTERN.matcher(text)
        while (mat.find()) {
            if (mat.group(1) != null && mat.group(1).length > 1) Log.d(
                "match",
                mat.group(1)
            )
            text = text.replace("#" + mat.group(1), "<Shase>" + mat.group(1) + "<Chase>")
        }
        return text
    }
}