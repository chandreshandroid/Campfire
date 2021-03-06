package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.frascozoomable.zoomable

import android.graphics.PointF
import android.view.GestureDetector
import android.view.MotionEvent

class DoubleTapGestureListener(private val mDraweeView: ZoomableDraweeView) :
    GestureDetector.SimpleOnGestureListener() {
    private val mDoubleTapViewPoint = PointF()
    private val mDoubleTapImagePoint = PointF()
    private var mDoubleTapScale = 1f
    private var mDoubleTapScroll = false

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
//        val zc = mDraweeView.getZoomableController() as AbstractAnimatedZoomableController
        val zc = mDraweeView.zoomableController as AbstractAnimatedZoomableController
        val vp = PointF(e.x, e.y)
        val ip = zc.mapViewToImage(vp)
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mDoubleTapViewPoint.set(vp)
                mDoubleTapImagePoint.set(ip)
                mDoubleTapScale = zc.scaleFactor
            }
            MotionEvent.ACTION_MOVE -> {
                mDoubleTapScroll = mDoubleTapScroll || shouldStartDoubleTapScroll(vp)
                if (mDoubleTapScroll) {
                    val scale = calcScale(vp)
                    zc.zoomToPoint(scale, mDoubleTapImagePoint, mDoubleTapViewPoint)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mDoubleTapScroll) {
                    val scale = calcScale(vp)
                    zc.zoomToPoint(scale, mDoubleTapImagePoint, mDoubleTapViewPoint)
                } else {
                    val maxScale = zc.maxScaleFactor
                    val minScale = zc.minScaleFactor
                    if (zc.scaleFactor < (maxScale + minScale) / 2) {
                        zc.zoomToPoint(
                            maxScale,
                            ip,
                            vp,
                            DefaultZoomableController.LIMIT_ALL,
                            DURATION_MS.toLong(),
                            null
                        )
                    } else {
                        zc.zoomToPoint(
                            minScale,
                            ip,
                            vp,
                            DefaultZoomableController.LIMIT_ALL,
                            DURATION_MS.toLong(), null
                        )
                    }
                }
                mDoubleTapScroll = false
            }
        }
        return true
    }

    private fun shouldStartDoubleTapScroll(viewPoint: PointF): Boolean {
        val dist = Math.hypot(
            (viewPoint.x - mDoubleTapViewPoint.x).toDouble(),
            (viewPoint.y - mDoubleTapViewPoint.y).toDouble()
        )
        return dist > DOUBLE_TAP_SCROLL_THRESHOLD
    }

    private fun calcScale(currentViewPoint: PointF): Float {
        val dy = currentViewPoint.y - mDoubleTapViewPoint.y
        val t = 1 + Math.abs(dy) * 0.001f
        return if (dy < 0) mDoubleTapScale / t else mDoubleTapScale * t
    }

    companion object {
        private val DURATION_MS = 300
        private val DOUBLE_TAP_SCROLL_THRESHOLD = 20
    }
}