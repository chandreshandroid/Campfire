package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Outline
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.ui.TimeBar.OnScrubListener
import kotlinx.android.synthetic.main.exo_playback_control_view.view.*
import kotlinx.android.synthetic.main.item_feed_list.view.*
import kotlinx.android.synthetic.main.item_feed_video.view.*


/**
 * Created by ADMIN on 26/12/2017.
 */

class FeedItemVideoAdapter(internal var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var trendingFeedDatum = ArrayList<TrendingFeedData>()
    var string: String = ""
    var sessionManager: SessionManager? = null
    var postposition: Int = 0
    var width: Int = 0
    var targetHeight: Float = 0.toFloat()
    var trendingFeedDatum1: TrendingFeedData? = null
    var mClickListener: ClickInterface? = null
    var lastPosition = -1
    var exoPlayer: SimpleExoPlayer? = null
    var videoPlayPosition = -1


    var thumbSize = 0

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

    fun setClickListener(clickListener: FeedItemVideoAdapter.ClickInterface) {
        this.mClickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.TYPE_SINGLE) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_feed_video, parent, false)
            return ImgHolder(view)
        } else {
            return null!!
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ImgHolder) {
            val holder1 = holder
            holder1.galleryImageView.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(0, 0, view?.width!!, view.height, 15f)

                }
            }
            holder1.galleryImageView.clipToOutline = true
            holder1.galleryImageView.findViewById<View>(R.id.ivFullScreen1)
            holder1.userIdWaterMark.text = "@" + trendingFeedDatum1?.userMentionID

//            if (!trendingFeedDatum1?.postViews.isNullOrEmpty() && !trendingFeedDatum1?.postViews.equals(
//                    "0",
//                    false
//                )
//            ) {
//                holder1.tvViewPost.visibility = View.VISIBLE
//                holder1.tvViewPost.text =trendingFeedDatum1?.postViews
//            } else {
//                holder1.tvViewPost.visibility = View.INVISIBLE
//            }

            if (trendingFeedDatum1?.postSerializedData?.get(0)!!.albummedia.get(0).isPlaying) {
                holder1.thumnail?.visibility = View.GONE
                holder1.playicon?.visibility = View.GONE
            } else {
                holder1.thumnail?.visibility = View.VISIBLE
                holder1.playicon?.visibility = View.VISIBLE
            }

            holder1.thumnail?.setImageURI(
                RestClient.image_base_url_post + trendingFeedDatum1?.postSerializedData?.get(
                    0
                )!!.albummedia.get(0).albummediaThumbnail
            )

            holder1.thumnail?.setOnClickListener(View.OnClickListener {
                if (mClickListener != null) {
                    mClickListener?.openVideoDetails(0)
                }
            })
            holder1.ivFullScreen1.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener?.openVideoFullScreen(0)
                }
            }

            //   holder1.galleryImageView.showController()
            holder1.galleryImageView.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener?.setVolume("playerclick")
                }
            }
            holder1.galleryImageView.exo_progress.setClickable(false)
            holder1.galleryImageView.exo_progress.setEnabled(false)
            holder1.galleryImageView.exo_progress.stopNestedScroll()
            holder1.galleryImageView.exo_progress.setFilterTouchesWhenObscured(false)
            holder1.galleryImageView.exo_progress.setHorizontalScrollBarEnabled(false)
            holder1.galleryImageView.exo_progress.setSelected(false)
            holder1.galleryImageView.exo_progress.addListener(object : OnScrubListener {
                override fun onScrubStart(timeBar: TimeBar, position: Long) {
                    timeBar.setEnabled(false)
                }

                override fun onScrubMove(timeBar: TimeBar, position: Long) {
                    timeBar.setEnabled(false)
                }

                override fun onScrubStop(
                    timeBar: TimeBar,
                    position: Long,
                    canceled: Boolean
                ) {
                    timeBar.setEnabled(false)
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
        var i: Int = 0
        when (if (trendingFeedDatum1?.postSerializedData?.get(0)?.albummedia!!.size >= 4) 4 else trendingFeedDatum1?.postSerializedData?.get(
            0
        )!!.albummedia.size)

            // when (i)
        {
            0 -> {
                return MyUtils.TYPE_SINGLE
            }
            else -> return MyUtils.TYPE_SINGLE
        }


    }

    class ImgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var galleryImageView = itemView.galleryImageView
        var playicon: ImageView? = itemView.playicon
        var thumnail: SimpleDraweeView? = itemView.thumnail
        var ivFullScreen1 = itemView.ivFullScreen1
        var userIdWaterMark = itemView.userIdWaterMark

        init {

            galleryImageView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH)

        }

        /*init{
            galleryImageView.exo_progress.isFocusable=false
            galleryImageView.exo_progress.isEnabled=false
        }*/

        //internal var volume: ImageButton=itemView.volume
        var pb: ProgressBar? = itemView.pb
        var selectImage: RelativeLayout? = null
        var ivFullScreen: AppCompatImageView = itemView.ivFullScreen1
        // var seekBar_exoplayer: SeekBar = itemView.seekBar_exoplayer
        //  var ll_seekbar: LinearLayout = itemView.ll_seekbar
        // var volume: ImageButton=galleryImageView.findViewById<View>(R.id.volume1) as ImageButton
    }


    fun stopPlayer() {
        if (videoPlayPosition > -1) {

            if (exoPlayer != null) {
                exoPlayer?.stop()
                exoPlayer?.release()
                exoPlayer = null
            }
        }


    }


    fun isMuteing(): Boolean {
        return MyUtils.isMuteing
    }

    fun setMuteing(muteing: Boolean) {
        MyUtils.isMuteing = muteing
    }

    interface ClickInterface {
        fun setVolume(from: String)
        fun openVideoFullScreen(position: Int)
        fun openVideoDetails(pos: Int)
    }
}

