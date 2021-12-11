package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.frascozoomable.zoomable

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Matrix
import android.view.animation.DecelerateInterpolator
import androidx.annotation.Nullable
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.frascozoomable.gestures.TransformGestureDetector
import com.facebook.common.internal.Preconditions
import com.facebook.common.logging.FLog


class AnimatedZoomableControllerSupport(transformGestureDetector: TransformGestureDetector) :
    AbstractAnimatedZoomableController(transformGestureDetector) {

    private val mValueAnimator: ValueAnimator

    override val logTag: Class<*>
        get() = TAG

    init {
        mValueAnimator = ValueAnimator.ofFloat(0f, 1f)
        mValueAnimator.interpolator = DecelerateInterpolator()
    }

    override fun setTransformAnimated(
        newTransform: Matrix,
        durationMs: Long,
        @Nullable onAnimationComplete: Runnable?
    ) {
        FLog.v(logTag, "setTransformAnimated: duration %d ms", durationMs)
        stopAnimation()
        Preconditions.checkArgument(durationMs > 0)
        Preconditions.checkState(!isAnimating)
        isAnimating = true
        mValueAnimator.duration = durationMs
        transform.getValues(startValues)
        newTransform.getValues(stopValues)
        mValueAnimator.addUpdateListener { valueAnimator ->
            calculateInterpolation(workingTransform, valueAnimator.animatedValue as Float)
            super@AnimatedZoomableControllerSupport.transform = workingTransform
        }
        mValueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                FLog.v(logTag, "setTransformAnimated: animation cancelled")
                onAnimationStopped()
            }

            override fun onAnimationEnd(animation: Animator) {
                FLog.v(logTag, "setTransformAnimated: animation finished")
                onAnimationStopped()
            }

            private fun onAnimationStopped() {
                onAnimationComplete?.run()
                isAnimating = false
                detector.restartGesture()
            }
        })
        mValueAnimator.start()
    }

    public override fun stopAnimation() {
        if (!isAnimating) {
            return
        }
        FLog.v(logTag, "stopAnimation")
        mValueAnimator.cancel()
        mValueAnimator.removeAllUpdateListeners()
        mValueAnimator.removeAllListeners()
    }

    companion object {

        private val TAG = AnimatedZoomableControllerSupport::class.java

        fun newInstance(): AnimatedZoomableControllerSupport {
            return AnimatedZoomableControllerSupport(TransformGestureDetector.newInstance())
        }
    }

}