package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewpager.widget.PagerAdapter
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.Albummedia
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.frascozoomable.zoomable.DoubleTapGestureListener
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.frascozoomable.zoomable.ZoomableDraweeView

class PhotogalleryAdapterr(
    val context: Context,
    val albummedia: List<Albummedia>
) : PagerAdapter() {

    private val inflater: LayoutInflater
    var heightNew = 0
    var widthNew = 0
    init {
        inflater = LayoutInflater.from(context)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return albummedia.size
    }

    override fun instantiateItem(view1: ViewGroup, position: Int): Any {
     //  val imageLayout = inflater.inflate(R.layout.userphotosgalleryadpter, view1, false)!!

        /* val useruploadImageview = imageLayout
            .findViewById(R.id.useruploadImageview) as ZoomableDraweeView
        useruploadImageview.setImageURI(Uri.parse(RestClient.image_base_url_post+albummedia[position].albummediaFile))
*/


        val view = ZoomableDraweeView(view1.context)
        view.controller = Fresco.newDraweeControllerBuilder()
            .setUri(Uri.parse(RestClient.image_base_url_post + albummedia[position].albummediaFile))
            .build()

        view.setTapListener(DoubleTapGestureListener(view))

        val hierarchy = GenericDraweeHierarchyBuilder(view1.resources)
            .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
            .setProgressBarImage(ProgressBarDrawable())
            .build()

        view.hierarchy = hierarchy

        view1.addView(view,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)


        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

    private fun getScrennwidth(): Int {

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        widthNew = width + (width/2)
        heightNew = height/5

        return height
    }
}