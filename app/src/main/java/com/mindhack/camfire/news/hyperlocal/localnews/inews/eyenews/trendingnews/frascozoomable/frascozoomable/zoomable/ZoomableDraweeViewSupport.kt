package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.frascozoomable.zoomable

import android.content.Context
import android.util.AttributeSet
import com.facebook.drawee.generic.GenericDraweeHierarchy

class ZoomableDraweeViewSupport : ZoomableDraweeView {

    override val logTag: Class<*>
        get() = TAG

    constructor(context: Context, hierarchy: GenericDraweeHierarchy) : super(context, hierarchy) {}

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun createZoomableController(): ZoomableController {
        return AnimatedZoomableControllerSupport.newInstance()
    }

    companion object {

        private val TAG = ZoomableDraweeViewSupport::class.java
    }
}