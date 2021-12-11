package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.content.Context
import android.graphics.Point
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.facebook.drawee.view.SimpleDraweeView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.Albummedia
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.DoubleClickListener

class FeedSubViewPagerAdapter(
    val context: Context,
    val albummedia: List<Albummedia>, var mClickListener:ClickInterface?=null
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
    fun setClickListener(clickListener: ClickInterface) {
        this.mClickListener = clickListener
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.item_feed_sub_images, view, false)!!

        val imageView = imageLayout
            .findViewById(R.id.iv_feed_image) as ImageView

        //  getScrennwidth()

        Glide.with(context)
            .load(RestClient.image_base_url_post+albummedia[position].albummediaFile)
            .placeholder(R.drawable.banner_placeholder_camfire)
            .error(R.drawable.banner_placeholder_camfire)
            .centerCrop()
            .into(imageView!!)
//        imageView.setImageURI(RestClient.image_base_url_post+albummedia[position].albummediaFile)
        imageView.setOnClickListener (object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                if(mClickListener!=null)
                    mClickListener?.openPhotoDetails(position)
                Log.e("v","one")
            }

            override fun onDoubleClick(v: View?) {
                Log.e("v","two")

            }
        })
        /*imageView.setOnClickListener {
            if(mClickListener!=null)
                mClickListener?.openPhotoDetails(position)
        }*/
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

    interface ClickInterface {

        fun openPhotoDetails(i: Int)
    }
}