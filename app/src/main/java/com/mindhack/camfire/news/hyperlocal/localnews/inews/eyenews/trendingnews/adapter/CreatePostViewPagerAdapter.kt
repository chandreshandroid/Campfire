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
import com.facebook.drawee.view.SimpleDraweeView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.AddImages

class CreatePostViewPagerAdapter(
    val context: Context,
    val numberOfImage: ArrayList<AddImages?>?
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
        return numberOfImage?.size!!
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.item_feed_createpost_images, view, false)!!

        val imageView = imageLayout
            .findViewById(R.id.iv_feed_image) as SimpleDraweeView

        //  getScrennwidth()

        if (numberOfImage?.get(position)!!.sdCardUri != null)
            imageView.setImageURI(Uri.parse("file://"+ numberOfImage?.get(position)!!.sdCardUri),context)
        else if (!numberOfImage?.get(position)!!.sdcardPath.isNullOrEmpty())
            imageView.setImageURI(Uri.parse("file://" + numberOfImage?.get(position)!!.sdcardPath),context)

        view.addView(imageLayout, 0)
        return imageLayout
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