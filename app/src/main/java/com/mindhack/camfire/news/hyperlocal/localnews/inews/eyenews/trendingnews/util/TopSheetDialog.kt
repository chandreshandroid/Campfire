package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util

/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * Created by andrea on 23/08/16.
 */
class TopSheetDialog : AppCompatDialog {

    private var topSheetBehavior: TopSheetBehavior<FrameLayout>? = null

    private val mTopSheetCallback = object : TopSheetBehavior.TopSheetCallback() {
        override fun onStateChanged(
            topSheet: View,
            @BottomSheetBehavior.State newState: Int
        ) {
            if (newState == TopSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(topSheet: View, slideOffset: Float, isOpening: Boolean?) {}
    }

    constructor(context: Context, theme: Int) :super(context) {

        getThemeResId(context, theme)
        // We hide the title bar for any style configuration. Otherwise, there will be a gap
        // above the bottom sheet when it is expanded.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }
    constructor(context: Context) : super(context) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }
    protected constructor(
        context: Context, cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun setContentView(@LayoutRes layoutResId: Int) {
        super.setContentView(wrapInTopSheet(layoutResId, null, null))
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun setContentView(view: View) {
        super.setContentView(wrapInTopSheet(0, view, null))
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(wrapInTopSheet(0, view, params))
    }

    private fun wrapInTopSheet(
        layoutResId: Int,
        view: View?,
        params: ViewGroup.LayoutParams?
    ): View {
        var view = view
        val coordinator = View.inflate(
            context,
            R.layout.top_sheet_dialog, null
        ) as CoordinatorLayout
        if (layoutResId != 0 && view == null) {
            view = layoutInflater.inflate(layoutResId, coordinator, false)
        }
        val topSheet = coordinator.findViewById<View>(R.id.design_top_sheet) as FrameLayout
        topSheetBehavior = TopSheetBehavior.from(topSheet)
        topSheetBehavior!!.setTopSheetCallback(mTopSheetCallback)
        if (params == null) {
            topSheet.addView(view)
        } else {
            topSheet.addView(view, params)
        }
        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        if (shouldWindowCloseOnTouchOutside()) {
            coordinator.findViewById<View>(R.id.top_sheet_touch_outside).setOnClickListener {
                if (isShowing) {
                    cancel()
                }
            }
        }
        return coordinator
    }

    private fun shouldWindowCloseOnTouchOutside(): Boolean {
        if (true) {
            return true
        }

        if (Build.VERSION.SDK_INT < 11) {
            return true
        }
        val value = TypedValue()

        return if (context.theme
                .resolveAttribute(android.R.attr.windowCloseOnTouchOutside, value, true)
        ) {
            value.data != 0
        } else false
    }

    override fun show() {
        super.show()
        //topSheetBehavior?.state=(TopSheetBehavior.STATE_EXPANDED);
    }

     fun getThemeResId(context: Context, themeId: Int): Int {
        var themeId = themeId
        if (themeId == 0) {
            // If the provided theme is 0, then retrieve the dialogTheme from our theme
            val outValue = TypedValue()
            if (context.theme.resolveAttribute(
                    R.attr.bottomSheetDialogTheme, outValue, true
                )
            ) {
                themeId = outValue.resourceId
            } else {
                // bottomSheetDialogTheme is not provided; we default to our light theme
                themeId = R.style.Theme_Design_TopSheetDialog
            }
        }
        return themeId
    }


}
