package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.item_points_adapter.view.*

import kotlin.collections.ArrayList

class PointsAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val feedData: ArrayList<TrendingFeedData?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelection = -1
    var sessionManager: SessionManager = SessionManager(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_points_adapter, parent, false)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder1: RecyclerView.ViewHolder, position: Int) {
        if (holder1 is LoaderViewHolder) {

        } else if (holder1 is ViewHolder) {
            val holder = holder1 as ViewHolder

            if(feedData[position]?.postMediaType.equals("Photo",true))
            {
                holder.itemPostOwnerPostImagePlay.visibility = View.GONE
                holder.itemPostOwnerPostListImage.setBackgroundColor(-1)
                if(!feedData[position]!!.postSerializedData?.get(0)?.albummedia[0].albummediaFile.isNullOrEmpty())
                {
                    holder.itemPostOwnerPostListImage.setImageURI(RestClient.image_base_url_post+ feedData[position]!!.postSerializedData?.get(0)?.albummedia[0].albummediaFile)

                }
                else
                {
                    holder.itemPostOwnerPostListImage.setImageResource(R.drawable.banner_placeholder_camfire)

                }
            }
            else if(feedData[position]?.postMediaType.equals("Video",true))
            {
                holder.itemPostOwnerPostImagePlay.visibility = View.VISIBLE
                holder.itemPostOwnerPostListImage.setBackgroundColor(Color.BLACK)
                holder.itemPostOwnerPostListImage.setImageURI(RestClient.image_base_url_post+ feedData[position]!!.postSerializedData?.get(0)?.albummedia[0].albummediaFile)

            }
            holder.itemPostOwnerPostTextLabel.text=feedData[position]!!.postHeadline
            holder.tvTextPoints.text=feedData[position]!!.postPoint
        }

    }

    override fun getItemCount(): Int {
        return feedData.size
    }

   override fun getItemViewType(position: Int): Int {
        return if (feedData[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class ViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

           var itemPostOwnerPostListImage=itemView.itemPostOwnerPostListImage
           var itemPostOwnerPostImagePlay=itemView.itemPostOwnerPostImagePlay
            var itemPostOwnerPostTextLabel=itemView.itemPostOwnerPostTextLabel
             var tvTextPoints=itemView.tvTextPoints
    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String)

    }

}