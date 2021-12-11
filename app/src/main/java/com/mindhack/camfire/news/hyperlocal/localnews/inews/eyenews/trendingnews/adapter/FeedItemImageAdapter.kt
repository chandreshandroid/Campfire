package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.app.Activity

import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.item_feed_image.view.*
import java.util.*
import kotlin.collections.ArrayList
import androidx.viewpager.widget.ViewPager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import kotlinx.android.synthetic.main.item_feed_image.view.userIdWaterMark
import kotlinx.android.synthetic.main.item_feed_video.view.*


/**
 * Created by ADMIN on 26/12/2017.
 */

class FeedItemImageAdapter(internal var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
     var trendingFeedDatum = ArrayList<TrendingFeedData>()
     var string :String=""
     var sessionManager: SessionManager?=null
     var postposition: Int = 0
     var width: Int = 0
     var targetHeight: Float = 0.toFloat()
     var trendingFeedDatum1: TrendingFeedData?=null
     var mClickListener: ClickInterface?=null
     var page_position = 0
     private  var dots: Array<ImageView?>?=null
     private var dotsCount: Int = 0
    var widthNew = 0
    var heightNew = 0
    fun setData(feeddata: TrendingFeedData, postposition: Int) {

        this.trendingFeedDatum1 = feeddata
        this.postposition = postposition
        notifyDataSetChanged()

    }

    fun setActivity(context: Context, postposition: Int) {
        this.context = context
        this.postposition = postposition
        this.postposition = postposition
        sessionManager = SessionManager(context)
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
    }

    fun setClickListener(clickListener: ClickInterface) {
        this.mClickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.TYPE_SINGLE) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_feed_image, parent, false)
            return ImgHolder(view)
        } else {
            return null!!
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ImgHolder) {

            val holder1 = holder
            getScrennwidth()

            val imageViewParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,heightNew)
            holder1.ll_Images?.layoutParams = imageViewParam

            holder1.ll_Images.visibility=View.VISIBLE
            holder1.view_pager2.adapter =FeedSubViewPagerAdapter(context,trendingFeedDatum1?.postSerializedData?.get(0)?.albummedia!!,object :FeedSubViewPagerAdapter.ClickInterface{
                override fun openPhotoDetails(i: Int) {
                    if (mClickListener != null) {
                        mClickListener?.favimage(0)
                      //  mClickListener?.openPhotoDetails(0)


                    }
                }

            })

            holder1.itemView.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener?.favimage(0)
                }
            }



            holder1.userIdWaterMark.text="@"+trendingFeedDatum1?.userMentionID



            if(!trendingFeedDatum1?.postSerializedData?.get(0)?.albummedia!!.isNullOrEmpty() && trendingFeedDatum1?.postSerializedData?.get(0)?.albummedia!!.size>1)
            {
                addBottomDots(0,holder1.layoutDotsTutorial)
                holder1.layoutDotsTutorial.visibility=View.VISIBLE
            }
            else
            {
                holder1.layoutDotsTutorial.visibility=View.GONE

            }



            holder1.view_pager2?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    page_position = position

                }

                override fun onPageSelected(position: Int) {
                    page_position = position
                    addBottomDots(position,holder1.layoutDotsTutorial)

                }

                override fun onPageScrollStateChanged(state: Int) {
                }
            })

        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return getTypeView(position)
    }

    @Throws(IndexOutOfBoundsException::class)
    fun getTypeView(position: Int): Int {
          var i:Int=0
       when (if (trendingFeedDatum1?.postSerializedData?.get(0)?.albummedia!!.size >= 4) 4 else trendingFeedDatum1?.postSerializedData?.get(0)!!.albummedia.size)
      //  when (i)
        {
           0 -> {

               return MyUtils.TYPE_SINGLE
            }
           else -> return MyUtils.TYPE_SINGLE
        }


    }


    interface ClickInterface {

        fun openPhotoDetails(i: Int)
        fun favimage(i: Int)
    }

    class ImgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var view_pager2=itemView.view_pager2
        var layoutDotsTutorial=itemView.layoutDotsTutorial
//        var feedThreeDots = itemView.feedThreeDots

        var userIdWaterMark=itemView.userIdWaterMark
        var ll_main_images=itemView.ll_main_images
        var rv_Images=itemView.rv_Images
        var ll_Images=itemView.ll_Images

    }


    fun addBottomDots(currentPage: Int, layoutDotsTutorial: LinearLayout) {
       // dotsCount = stichingServicData?.stitchingserviceImages!!.size
        dotsCount=trendingFeedDatum1?.postSerializedData?.get(0)?.albummedia!!.size
        layoutDotsTutorial?.removeAllViews()

        dots = arrayOfNulls(dotsCount)

        for (i in 0 until dotsCount) {

            dots!![i] = ImageView(context)
            dots!![i]?.setImageResource(R.drawable.carousel_dot_unselected)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            dots!![i]?.setPadding(0, 0, 0, 0)
            dots!![i]?.layoutParams = params
            layoutDotsTutorial?.addView(dots!![i]!!, params)
            layoutDotsTutorial?.bringToFront()

        }
        if (dots!!.size > 0 && dots!!.size > currentPage) {
            dots!![currentPage]?.setImageResource(R.drawable.carousel_dot_selected)
        }

    }

    fun sliderBanner(i: Int, viewPager2: ViewPager2) {
        val handler = Handler()
        val update = Runnable {
            try {
//                if (images != null && images!!.size > 0) {
                if (i > 0) {
                    if (page_position == i) {
                        page_position = 0

                    } else {
                        page_position = page_position + 1
                    }
                    viewPager2?.setCurrentItem(page_position, true)
                }
            } catch (e: NullPointerException) {

            }
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 3000, 15000)
    }


    private fun getScrennwidth(): Int {

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        widthNew = (width).toInt()
        heightNew = (height / 1.5).toInt()
        Log.e("System out","Print heightNew := "+ heightNew)

        return height
    }
}

