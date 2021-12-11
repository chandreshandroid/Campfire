package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder

import android.app.Activity

import android.view.View

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.FeedItemVideoAdapter
import kotlinx.android.synthetic.main.item_feed_list.view.*


/**
 * Created by dhavalkaka on 12/02/2018.
 */

class VideoViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {


    val context = itemView.context
    val linearLayoutManager = LinearLayoutManager(context)
    var feedThreeDots = itemView.feedThreeDots
    var trendingItemPhotoAdapter = FeedItemVideoAdapter(context)
    var recycle_view_feedhome = itemView.recycle_view_feedhome
    var ll_post_like = itemView.ll_post_like
    var img_like = itemView.img_like
    var imv_user_dp = itemView.imv_user_dp
    var ll_user_name = itemView.ll_user_name
    var btnFollowing = itemView.btnFollowing
    var clap_feed_post = itemView.clap_feed_post
    var ll_favourite = itemView.ll_favourite
    var img_favourite = itemView.img_favourite
    var ll_feed_share = itemView.ll_feed_share
    var img_share = itemView.img_share
    var tv_share_count = itemView.tv_share_count
    var ll_feed_comment = itemView.ll_feed_comment
    var img_comment = itemView.img_comment
    var tv_comment_count=itemView.tv_comment_count
    var tv_like_count=itemView.tv_like_count
    var tvViewPost=itemView.tvViewPost
    var postView=itemView.postView
    var tvFeedUserName=itemView.tvFeedUserName
    var tvFeedUserId=itemView.tvFeedUserId
    var img_user_verified=itemView.img_user_verified
    var img_user_trophy=itemView.img_user_trophy
    var btnFollow=itemView.btnFollow
    var tvFeedClapCount=itemView.tvFeedClapCount
    var tvFeedTimeLocation=itemView.tvFeedTimeLocation
    var tvFeedDescription=itemView.tvFeedDescription
//    var tvFeedDescriptionReadMore = itemView.tvFeedDescriptionReadMore
    var tvFeedDescriptionFullView = itemView.tvFeedDescriptionFullView
    var tvFeedLikedUserName = itemView.tvFeedLikedUserName
    var tvFeedRepost = itemView.tvFeedRepost
     var isDoubleTapImage=false
    init {
        recycle_view_feedhome.layoutManager = linearLayoutManager
        recycle_view_feedhome.adapter = trendingItemPhotoAdapter
        recycle_view_feedhome.setHasFixedSize(true)
    }


}
