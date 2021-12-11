package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.frascozoomable.gestures

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent

class SwipeGestureDetector(private val mSwipeListener: OnSwipeListener?) : GestureDetector.SimpleOnGestureListener() {
    private var mInThisGesture: Boolean = false
    private var mStartX: Float = 0.toFloat()
    private var mStartY: Float = 0.toFloat()
    private var mCurrentX: Float = 0.toFloat()
    private var mCurrentY: Float = 0.toFloat()

    val translateY: Float
        get() = mCurrentY - mStartY

    val translateX: Float
        get() = mCurrentX - mStartX

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mCurrentX = event.x
                mStartX = mCurrentX
                mCurrentY = event.y
                mStartY = mCurrentY
                if (mSwipeListener != null && !mInThisGesture) {
                    mInThisGesture = mSwipeListener.onOpenSwipe()
                }
                if (mInThisGesture) {
                    mSwipeListener?.onSwipeBegin()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                mCurrentX = event.x
                mCurrentY = event.y
                Log.d(TAG, "onTouchEvent: start   X: $mStartX  start Y: $mStartY")
                Log.d(TAG, "onTouchEvent: current X: $mCurrentX  current Y: $mCurrentY")
                if (mSwipeListener != null && mInThisGesture) {
                    mSwipeListener.onSwiping(mCurrentX - mStartX, mCurrentY - mStartY)
                }
            }
            MotionEvent.ACTION_UP -> {
                mInThisGesture = false
                mSwipeListener?.onSwipeReleased()
            }

            MotionEvent.ACTION_CANCEL -> {
                mInThisGesture = false
                mSwipeListener?.onSwipeReleased()
            }
        }
        return mInThisGesture
    }

    interface OnSwipeListener {
        fun onOpenSwipe(): Boolean
        fun onSwipeBegin()
        fun onSwipeReleased()
        fun onSwiping(distanceX: Float, distanceY: Float)
    }

    companion object {
        private val TAG = "SwipeGestureDetector"
    }
}
