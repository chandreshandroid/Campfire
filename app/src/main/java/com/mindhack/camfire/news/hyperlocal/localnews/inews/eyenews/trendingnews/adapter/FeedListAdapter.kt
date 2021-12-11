package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.*
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.ReactionPopup.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.HashTagListFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.LocationListFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.PhotoGalleryFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.ProfileDetailFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.Posttag
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.ImageViewHolder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.VideoViewHolder
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.gson.Gson
import com.plattysoft.leonids.ParticleSystem
import com.plattysoft.leonids.modifiers.ScaleModifier
import kotlinx.android.synthetic.main.item_feed_list.view.*
import java.io.Serializable

class FeedListAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val type: String,
    val feedList: ArrayList<TrendingFeedData?>?

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mSelection = -1
    var widthNew = 0
    var heightNew = 0

    //private val strings = arrayOf("laugh", "angry", "worried", "like")
    private val strings = arrayOf("angry", "worried", "laugh", "like")

    var newHeight = 0
    var newWidth = 0

    /*var imgArray = intArrayOf(
        R.drawable.laughing_reaction_icon_big,
        R.drawable.angry_reaction_icon_big,
        R.drawable.worried_reaction_icon_big,
        R.drawable.like_filled_big
    )*/
    var imgArray = intArrayOf(
        R.drawable.angry_reaction_icon_big,
        R.drawable.worried_reaction_icon_big,
        R.drawable.laughing_reaction_icon_big,
        R.drawable.like_filled_big
    )

    var selectedimgArray = intArrayOf(
        R.drawable.angry_reaction_icon_small,
        R.drawable.worried_reaction_icon_small,
        R.drawable.laughing_reaction_icon_small,
        R.drawable.like_filled_small
    )

    var popup: ReactionPopup? = null
    var scale: ScaleAnimation? = null
    var exoPlayer: SimpleExoPlayer? = null
    var videoPlayPosition = -1
    var settingsManager: SettingsManager = SettingsManager(context)
    var cache: SimpleCache? = null
    var sessionManager: SessionManager = SessionManager(context)
    var isExpandPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder?

        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            viewHolder = LoaderViewHolder(view)

        } else if (viewType == MyUtils.Video_TYPE) run {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_feed_list, parent, false)
            viewHolder = VideoViewHolder(view, context)
        } else if (viewType == MyUtils.Image_TYPE) run {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_feed_list, parent, false)
            viewHolder = ImageViewHolder(view, context)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_feed_list, parent, false)
            viewHolder = ViewHolder(v, context)
        }

        return viewHolder!!
    }


    @TargetApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {
        }
        else if (holder is ViewHolder) {
            getScreenWidth()
            val margin = context.resources.getDimensionPixelSize(R.dimen._55sdp)

            val holder1 = holder
            // getScrennwidth()

            holder1.imv_user_dp.setImageURI(
                RestClient.image_base_url_users + feedList!!.get(
                    position
                )!!.userProfilePicture
            )

            holder1.btnFollowing.text = "" + GetDynamicStringDictionaryObjectClass.Following
            holder1.btnFollow.text = "" + GetDynamicStringDictionaryObjectClass.Follow
            Log.w("SagRSsa",""+feedList?.get(position)!!.postViews)
            if (feedList?.get(position)!!.postViews.isNullOrEmpty() && feedList?.get(position)!!.postViews.equals("0", false)) {

                holder1.tvViewPost.visibility = View.INVISIBLE
                holder1.postView.visibility = View.INVISIBLE
            }
            else
            {
                holder1.tvViewPost.visibility = View.VISIBLE
                holder1.postView.visibility = View.VISIBLE
                holder1.tvViewPost.text =feedList?.get(position)!!.postViews
            }


            holder1.tvFeedUserName.text =
                feedList?.get(position)!!.userFirstName + " " + feedList?.get(position)!!.userLastName
            holder1.tvFeedUserId.text = "@" + feedList?.get(position)!!.userMentionID
            holder1.img_user_trophy.setImageURI(RestClient.image_base_Level + feedList?.get(position)?.badgeIcon)

            if (feedList?.get(position)!!.celebrityVerified.equals("Yes", false)) {
                holder1.img_user_verified.visibility = View.VISIBLE
            } else {
                holder1.img_user_verified.visibility = View.GONE
            }
            var Sand = GetDynamicStringDictionaryObjectClass.and
            var sMore = GetDynamicStringDictionaryObjectClass.more
            var sLikedby = GetDynamicStringDictionaryObjectClass.Liked_By
            var sYou = GetDynamicStringDictionaryObjectClass.you_

            if (feedList?.get(position)!!.IsLiekedByYou.equals("Yes", false)) {
                holder1.tvFeedLikedUserName.visibility = View.VISIBLE

                if (!feedList?.get(position)!!.postAll.isNullOrEmpty()) {
                    if (feedList?.get(position)!!.postAll.toInt() > 1) {
                        holder1.tvFeedLikedUserName.text =
                            sLikedby + " " + sYou + " " + Sand + " " + sMore

                    } else {
//                        holder1.tvFeedLikedUserName.text ="Liked by You"
                        holder1.tvFeedLikedUserName.text = sLikedby + " " + sYou + " "

                    }
                } else {
                    holder1.tvFeedLikedUserName.text =
                        sLikedby + " " + sYou

                }
            } else {
                if (!feedList?.get(position)!!.postComment.isNullOrEmpty()) {

                    holder1.tvFeedLikedUserName.visibility = View.INVISIBLE

                    holder1.tvFeedLikedUserName.text =
                        sLikedby + " ${feedList?.get(position)!!.LikedList[0].userFirstName + " " + feedList?.get(position)!!.LikedList[0].userLastName} " + Sand + " " + sMore
                } else {
                    holder1.tvFeedLikedUserName.visibility = View.INVISIBLE
                }

            }



            if (feedList?.get(position)!!.IsYourFavorite.equals("Yes", false)) {

                holder1.img_favourite.setImageResource(R.drawable.favourite_icon_red)
            } else {

                holder1.img_favourite.setImageResource(R.drawable.favourite_icon_grey)
            }

            if (feedList?.get(position)!!.IsLiekedByYou.equals("Yes", false)) {
                holder1.img_like.setImageResource(R.drawable.like_filled_small)
            } else {
                holder1.img_like.setImageResource(R.drawable.reaction_like_icon_small)
            }


            if (feedList[position]?.IsClappeddByYou.equals("Yes", false)) {
                holder1.clap_feed_post.setImageResource(R.drawable.clap_seect)
            } else {
                holder1.clap_feed_post.setImageResource(R.drawable.likefill_icon)
            }
            holder1.tvUserId.text =  feedList?.get(position)!!.userID
            if (!feedList?.get(position)!!.userClapCount.isNullOrEmpty() && !feedList?.get(position)!!.userClapCount.equals(
                    "0",
                    false
                )
            ) {
                holder1.tvFeedClapCount.text = feedList?.get(position)!!.userClapCount
                holder1.tvFeedClapCount.visibility = View.VISIBLE
            } else {
                holder1.tvFeedClapCount.visibility = View.GONE
            }
            if (feedList?.get(position)!!.originalPostID.toInt() > 0) {

                holder1.tvFeedRepost.visibility = View.VISIBLE

                holder1.tvFeedRepost.text = "Reposted - ${MyUtils.formatDate(
                    feedList?.get(position)!!.postCreatedDate,
                    "yyyy-MM-dd HH:mm:ss",
                    "dd MMM yy"
                )}"

                holder1.tvFeedTimeLocation.text =
                    "From ${"@" + feedList?.get(position)!!.original_userMentionID} " +
                            feedList?.get(position)!!.original_postLocation + "-" + MyUtils.formatDate(
                        feedList?.get(position)!!.original_postCreatedDate!!,
                        "yyyy-MM-dd HH:mm:ss",
                        "dd MMM yy"
                    )


            } else {
                holder1.tvFeedTimeLocation.text =
                    feedList?.get(position)!!.postLocation + "-" + MyUtils.formatDate(
                        feedList?.get(position)!!.postCreatedDate,
                        "yyyy-MM-dd HH:mm:ss",
                        "dd MMM yy"
                    )
                holder1.tvFeedRepost.visibility = View.GONE
            }

            holder1.tvFeedTimeLocation.setOnClickListener {
                var locationListFragment = LocationListFragment()
                Bundle().apply {
                    putString("location", feedList[holder1.adapterPosition]!!.postLocation)
                    locationListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    locationListFragment,
                    locationListFragment::class.java.name,
                    true
                )
            }





            if (feedList?.get(position)!!.postLocationVerified.equals("Yes", false)) {
                holder1.tvFeedTimeLocation.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(context, R.drawable.geological_location_verified),
                    null
                )


            } else {
                holder1.tvFeedTimeLocation.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(context, R.drawable.geological_location_unverified),
                    null
                )

            }



            if (sessionManager.isLoggedIn()) {

                if (sessionManager.get_Authenticate_User().userID.equals(
                        feedList?.get(position)!!.userID,
                        false
                    )
                ) {
                    holder1.btnFollowing.visibility = View.GONE
                    holder1.btnFollow.visibility = View.GONE
                    holder1.clap_feed_post.visibility = View.GONE
                    holder1.tvFeedClapCount.visibility = View.GONE

                } else {

                    holder1.clap_feed_post.visibility = View.VISIBLE
                    holder1.tvFeedClapCount.visibility = View.VISIBLE
                    if (feedList?.get(position)!!.isYouFollowing.equals("Yes", false)) {
//                        holder1.btnFollowing.visibility = View.VISIBLE
                        holder1.btnFollow.visibility = View.GONE
                    } else {
                        holder1.btnFollowing.visibility = View.GONE
//                        holder1.btnFollow.visibility = View.VISIBLE
                    }
                }

            } else {
                holder1.btnFollowing.visibility = View.GONE
                holder1.btnFollow.visibility = View.VISIBLE
                holder1.clap_feed_post.visibility = View.VISIBLE
                holder1.tvFeedClapCount.visibility = View.VISIBLE
            }

//            holder1.tvFeedDescriptionReadMore.text =
//                "" + GetDynamicStringDictionaryObjectClass.Read_More + "..."
//            holder1.tvFeedDescriptionReadMore.post {
//
//                if (!feedList?.get(position)!!.isReadMore) {
//
//                    holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                    holder1.tvFeedDescriptionFullView.visibility = View.GONE
//
//                } else {
//
//                    holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                    holder1.tvFeedDescriptionFullView.visibility = View.VISIBLE
//
//                }
//            }


//            holder1.tvFeedDescriptionReadMore?.setOnClickListener {
//
//                if (holder1.adapterPosition > -1) {
//                    if (isExpandPosition > -1 && isExpandPosition != holder1.adapterPosition && feedList?.get(position)!!.isReadMore) {
//                        feedList?.get(isExpandPosition)!!.isReadMore = false
//                        if (!feedList?.get(position)!!.isReadMore) {
//
//                            holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                            holder1.tvFeedDescriptionFullView.visibility = View.GONE
//
//                        } else {
//
//                            holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                            holder1.tvFeedDescriptionFullView.visibility = View.VISIBLE
//
//                        }
//                    }
//
//                    feedList[holder1.adapterPosition]!!.isReadMore =
//                        !feedList[holder1.adapterPosition]!!.isReadMore
//                    if (feedList?.get(holder1.adapterPosition)!!.isReadMore)
//                        isExpandPosition = holder1.adapterPosition
//                    else
//                        isExpandPosition = -1
//
//                    if (!feedList?.get(position)!!.isReadMore) {
//
//                        holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                        holder1.tvFeedDescriptionFullView.visibility = View.GONE
//
//                    } else {
//
//                        holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                        holder1.tvFeedDescriptionFullView.visibility = View.VISIBLE
//
//                    }
//                }
//            }


            holder1.tvFeedDescription.text = feedList?.get(position)!!.postHeadline

            if (feedList?.get(position)!!.postDescription.isNotEmpty()) {

                val postdescription: String = feedList?.get(position)!!.postDescription
                holder1.tvFeedDescriptionFullView.displayFulltext(postdescription)
                holder1.tvFeedDescriptionFullView.tag = position
                holder1.tvFeedDescriptionFullView.setHashClickListener(object :
                    PostDesTextView.OnHashEventListener {
                    override fun onHashTagClick(friendsId: String?) {
                        var hashTagListFragment = HashTagListFragment()
                        Bundle().apply {
                            putString("hashTag", friendsId)
                            hashTagListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            hashTagListFragment,
                            hashTagListFragment::class.java.name,
                            true
                        )
                    }

                })

                holder1.tvFeedDescriptionFullView.setCustomEventListener(object :
                    PostDesTextView.OnCustomEventListener {
                    override fun onViewMore() {
                        val pos: String =
                            java.lang.String.valueOf(holder1.tvFeedDescriptionFullView.tag!!)
                        val gson = Gson()
                        val json = gson.toJson(feedList?.get(pos.toInt()))

                    }

                    override fun onFriendTagClick(friendsId: String?) {
                        var profileDetailFragment = ProfileDetailFragment()

                        if (!sessionManager.get_Authenticate_User().userID.equals(
                                friendsId,
                                false
                            )
                        ) {
                            var uName = ""
                            if (!feedList?.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && !feedList?.get(
                                    holder1.adapterPosition
                                )!!.userLastName.isNullOrEmpty()
                            ) {
                                uName =
                                    feedList?.get(holder1.adapterPosition)!!.userFirstName + " " + feedList?.get(
                                        holder1.adapterPosition
                                    )!!.userLastName
                            } else if (feedList?.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && !feedList?.get(
                                    holder1.adapterPosition
                                )!!.userLastName.isNullOrEmpty()
                            ) {
                                uName = "" + feedList?.get(holder1.adapterPosition)!!.userLastName
                            } else if (!feedList?.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && feedList?.get(
                                    holder1.adapterPosition
                                )!!.userLastName.isNullOrEmpty()
                            ) {
                                uName = feedList?.get(holder1.adapterPosition)!!.userFirstName + ""
                            }
                            Bundle().apply {
                                putString("userID", friendsId)
                                putString("userName", uName)
                                putSerializable("feedData", feedList?.get(holder1.adapterPosition)!!)
                                profileDetailFragment.arguments = this
                            }
                        }
                        (context as MainActivity).navigateTo(
                            profileDetailFragment,
                            profileDetailFragment::class.java.name,
                            true
                        )

                    }
                })
            } else {
//                holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
                holder1.tvFeedDescriptionFullView.visibility = View.GONE
            }

            /*if (!feedList?.get(position)!!.posttag.isNullOrEmpty())
            {
                holder1.ll_tag.visibility=View.VISIBLE
                inflateTagList(feedList?.get(position)!!.posttag, holder1.ll_tag)
            }
            else
            {
                holder1.ll_tag.visibility=View.GONE

            }*/


            if (!feedList?.get(position)!!.postShared.isNullOrEmpty() && !feedList?.get(position)!!.postShared.equals(
                    "0",
                    false
                )
            ) {
                holder1.tv_share_count.visibility = View.VISIBLE
                holder1.tv_share_count.text = feedList?.get(position)!!.postShared
            } else {
                holder1.tv_share_count.visibility = View.INVISIBLE
            }
            if (!feedList?.get(position)!!.postComment.isNullOrEmpty() && !feedList?.get(position)!!.postComment.equals(
                    "0",
                    false
                )
            ) {
                holder1.tv_comment_count.visibility = View.VISIBLE
                holder1.tv_comment_count.text = feedList?.get(position)!!.postComment
            } else {
                holder1.tv_comment_count.visibility = View.INVISIBLE
            }
            if (!feedList?.get(position)!!.postAll.isNullOrEmpty() && !feedList?.get(position)!!.postAll.equals(
                    "0",
                    false
                )
            ) {
                holder1.tv_like_count.visibility = View.VISIBLE
                holder1.tv_like_count.text = feedList?.get(position)!!.postAll
            } else {
                holder1.tv_like_count.visibility = View.INVISIBLE
            }

            holder1.feedThreeDots.setOnClickListener {
                onItemClick.onClicklisneter(
                    position,
                    "showDotMenu",
                    holder1.feedThreeDots,
                    feedList[holder1.adapterPosition]!!
                )
            }

            holder1.ll_user_name.setOnClickListener {
                onItemClick.onClicklisneter(
                    position,
                    "otherUserProfile",
                    holder1.ll_user_name,
                    feedList[holder1.adapterPosition]!!
                )

            }

            holder1.imv_user_dp.setOnClickListener {
                holder1.ll_user_name.performClick()
            }
            holder1.btnFollowing.setOnClickListener {
                onItemClick.onClicklisneter(
                    position,
                    "Following",
                    holder1.btnFollowing
                    , feedList[holder1.adapterPosition]!!
                )

            }
            holder1.img_feed_post.setOnClickListener {

                if (!feedList[holder1.adapterPosition]?.IsClappeddByYou.equals(
                        "Yes",
                        true
                    ) && sessionManager.isLoggedIn()
                ) {
                    holder1.itemView.clap_feed_post_big.visibility = View.VISIBLE

                    val mAnimation1 = TranslateAnimation(
                        0f,
                        if (holder1.itemView.image_round.x > 0) -holder1.itemView.image_round.x else
                            (-(((holder1.itemView.width) / 2) - context.resources.getDimensionPixelSize(
                                R.dimen._65sdp
                            ))).toFloat(),
                        holder1.itemView.y, ((holder1.itemView.height) / 2).toFloat()
                    )

                    mAnimation1.duration = 500
                    mAnimation1.fillAfter = true

                    holder1.itemView.clap_feed_post_big.startAnimation(mAnimation1)
                    Handler().postDelayed({
                        holder1.itemView.image_round.visibility = View.VISIBLE
                    }, 400)
                    Handler().postDelayed({
                        holder1.itemView.clap_feed_post_big.visibility = View.GONE
                        holder1.itemView.image_round.visibility = View.GONE

                        onItemClick.onClicklisneter(
                            position, "clap", holder1.clap_feed_post,
                            feedList[holder1.adapterPosition]!!
                        )
                    }, 700)
                } else {
                    onItemClick.onClicklisneter(
                        position, "clap", holder1.clap_feed_post,
                        feedList[holder1.adapterPosition]!!
                    )
                }

            }
            holder1.ll_favourite.setOnClickListener {
                onItemClick.onClicklisneter(
                    position,
                    "favourite",
                    holder1.img_favourite,
                    feedList[holder1.adapterPosition]!!
                )

            }
            holder1.img_favourite.setOnClickListener {
                onItemClick.onClicklisneter(
                    position,
                    "favourite",
                    holder1.img_favourite,
                    feedList[holder1.adapterPosition]!!
                )

            }
            holder1.ll_feed_share.setOnClickListener {
                onItemClick.onClicklisneter(
                    position,
                    "share",
                    holder1.img_share,
                    feedList[holder1.adapterPosition]!!
                )

            }


            holder1.ll_feed_comment.setOnClickListener {
                onItemClick.onClicklisneter(
                    position,
                    "comment",
                    holder1.img_comment,
                    feedList[holder1.adapterPosition]!!
                )

            }

            popup = ReactionPopup(context,ReactionsConfigBuilder(context).withReactions(imgArray)
                    .withReactionTexts { position ->
                        strings[position]

                    }
                    .withTextBackground(ColorDrawable(Color.TRANSPARENT))
                    .withTextColor(Color.BLACK)
                    .withHorizontalMargin(25)
                    .withPopupGravity(PopupGravity.PARENT_RIGHT)
                    .withPopupColor(context.getDrawable(R.drawable.reaction_rounded_rectangle_bg_with_shadow_white)!!)
                    .withTextHorizontalPadding(0)
                    .withTextVerticalPadding(0)
                    .withTextSize(context.resources.getDimension(R.dimen.reactions_text_size))
                    .build()
            ) { position ->
                if (position == -1) {
                    popup?.dismiss()
                } else {
                    holder1.img_like.setImageResource(selectedimgArray.get(position))
                    // var count=(feedList[holder1.adapterPosition]!!.postAll).toInt() + 1

                    ParticleSystem(context, 10, selectedimgArray.get(position), 3000)
                        .setSpeedByComponentsRange(0f, 0f, -0.2f, 0f)
                        .setAcceleration(0.000003f, 360)
                        .setInitialRotationRange(0, 90)
                        .setRotationSpeed(120f)
                        .setFadeOut(2000)
                        .addModifier(ScaleModifier(0f, 1.5f, 0, 1500))
                        .oneShot(holder1.ll_post_like, 10)

                    onItemClick.onClicklisneter(
                        holder1.adapterPosition,
                        strings[position],
                        holder1.ll_post_like
                        , feedList[holder1.adapterPosition]!!
                    )
                }
                true
            }
            holder1.ll_post_like.setOnLongClickListener(popup)
            holder1.img_like.setOnLongClickListener(popup)

            holder1.ll_post_like.setOnClickListener {
                if (popup?.isShowing!!) {
                    popup?.dismiss()
                }
                if (holder1.adapterPosition > -1) {
                    if (feedList?.get(position)!!.IsLiekedByYou.equals("Yes", false)) {
                        onItemClick.onClicklisneter(
                            holder1.adapterPosition,
                            "Unlike",
                            holder1.ll_post_like
                            , feedList[holder1.adapterPosition]!!
                        )
                    } else {
                        ParticleSystem(context, 10, R.drawable.like_filled_big, 3000)
                            .setSpeedByComponentsRange(0f, 0f, -0.2f, 0f)
                            .setAcceleration(0.000003f, 360)
                            .setInitialRotationRange(0, 90)
                            .setRotationSpeed(120f)
                            .setFadeOut(2000)
                            .addModifier(ScaleModifier(0f, 1.5f, 0, 1500))
                            .oneShot(holder1.ll_post_like, 10)
                        onItemClick.onClicklisneter(
                            holder1.adapterPosition,
                            "like",
                            holder1.ll_post_like
                            , feedList[holder1.adapterPosition]!!
                        )
                    }
                }
            }

            holder1.img_like.setOnClickListener {
                holder1.ll_post_like.performClick()
            }

            holder1.tvFeedLikedUserName.setOnClickListener {

                onItemClick.onClicklisneter(
                    holder1.adapterPosition,
                    "likelist",
                    holder1.tv_like_count
                    , feedList[holder1.adapterPosition]!!
                )

            }


        }
        else if (holder is VideoViewHolder) {

            Log.w("SagRSsa",""+ feedList?.get(position)!!.postViews)

            val holder1 = holder
            holder1.imv_user_dp.setImageURI(RestClient.image_base_url_users + feedList?.get(position)!!.userProfilePicture)
            holder1.img_user_trophy.setImageURI(RestClient.image_base_Level + feedList?.get(position)?.badgeIcon)

            holder1.trendingItemPhotoAdapter.setActivity(context, position)
            holder1.trendingItemPhotoAdapter.setData(feedList?.get(position)!!, position)
            if (feedList?.get(position)!!.postViews.isNullOrEmpty() && feedList?.get(position)!!.postViews.equals("0", false)) {

                holder1.tvViewPost.visibility = View.INVISIBLE
                holder1.postView.visibility = View.INVISIBLE
            }
            else
            {
                holder1.tvViewPost.visibility = View.VISIBLE
                holder1.postView.visibility = View.VISIBLE
                holder1.tvViewPost.text =feedList?.get(position)!!.postViews
            }

            holder1.trendingItemPhotoAdapter.setClickListener(object :
                FeedItemVideoAdapter.ClickInterface {
                override fun setVolume(from: String) {
                    holder1.itemView.performClick()
                }
                override fun openVideoFullScreen(position: Int) {

                    if (holder1.getAdapterPosition() > -1)

                        onItemClick.onClicklisneter(
                            holder1.getAdapterPosition(),
                            "fullScreen",
                            holder1.ll_user_name,
                            feedList[holder1.adapterPosition]!!
                        )
                }
                override fun openVideoDetails(pos: Int) {
                     if(holder1.adapterPosition>0 ){
                         if (feedList[position-1]!!.postSerializedData[0].albummedia[0].isPlaying)
                         {
                             videoPlayPosition = position
                             pausePlayer()
                             feedList?.get(position)!!.postSerializedData[0].albummedia[0].isPlaying =
                                 true


                             val cvh = holder as VideoViewHolder?
                             val viewHolder1 =
                                 cvh!!.recycle_view_feedhome.findViewHolderForAdapterPosition(
                                     0
                                 )

                             if (viewHolder1 is FeedItemVideoAdapter.ImgHolder) {
                                 val holder3 = viewHolder1

                                 playVideo(
                                     holder1.adapterPosition,
                                     holder3.galleryImageView,
                                     holder3.thumnail!!,
                                     holder3.thumnail!!/*holder1.volume!!*/,
                                     holder3.pb!!,
                                     holder3.playicon!!,
                                     holder1.tvViewPost!!,
                                     feedList
                                 )
                             }

                         }
                         else{
                             videoPlayPosition = position
                             pausePlayer()
                             val cvh = holder as VideoViewHolder?
                             val viewHolder1 =
                                 cvh!!.recycle_view_feedhome.findViewHolderForAdapterPosition(
                                     0
                                 )

                             if (viewHolder1 is FeedItemVideoAdapter.ImgHolder) {
                                 val holder3 = viewHolder1

                                 playVideo(
                                     holder1.adapterPosition,
                                     holder3.galleryImageView,
                                     holder3.thumnail!!,
                                     holder3.thumnail!!/*holder1.volume!!*/,
                                     holder3.pb!!,
                                     holder3.playicon!!,
                                     holder1.tvViewPost!!,
                                     feedList
                                 )
                             }
                         }

                     }else{
                         videoPlayPosition = position
                         pausePlayer()
                         val cvh = holder as VideoViewHolder?
                         val viewHolder1 =
                             cvh!!.recycle_view_feedhome.findViewHolderForAdapterPosition(
                                 0
                             )

                         if (viewHolder1 is FeedItemVideoAdapter.ImgHolder) {
                             val holder3 = viewHolder1

                             playVideo(
                                 holder1.adapterPosition,
                                 holder3.galleryImageView,
                                 holder3.thumnail!!,
                                 holder3.thumnail!!/*holder1.volume!!*/,
                                 holder3.pb!!,
                                 holder3.playicon!!,
                                 holder1.tvViewPost!!,
                                 feedList
                             )
                         }
                     }


                }

            })


            holder1.tvFeedUserName.text =
                feedList?.get(position)!!.userFirstName + " " + feedList?.get(position)!!.userLastName
            holder1.tvFeedUserId.text = "@" + feedList?.get(position)!!.userMentionID

            if (feedList?.get(position)!!.celebrityVerified.equals("Yes", false)) {
                holder1.img_user_verified.visibility = View.VISIBLE
            } else {
                holder1.img_user_verified.visibility = View.GONE
            }

            if (feedList?.get(position)!!.IsYourFavorite.equals("Yes", false)) {

                holder1.img_favourite.setImageResource(R.drawable.favourite_icon_red)
            } else {

                holder1.img_favourite.setImageResource(R.drawable.favourite_icon_grey)
            }


            if (feedList?.get(position)!!.IsClappeddByYou.equals("Yes", false)) {
                holder1.clap_feed_post.setImageResource(R.drawable.likefill_icon)
            } else {
                holder1.clap_feed_post.setImageResource(R.drawable.clap_seect)
            }
            if (!feedList?.get(position)!!.userClapCount.isNullOrEmpty() && !feedList?.get(position)!!.userClapCount.equals(
                    "0",
                    false
                )
            ) {
                holder1.tvFeedClapCount.text = feedList?.get(position)!!.userClapCount
                holder1.tvFeedClapCount.visibility = View.VISIBLE
            } else {
                holder1.tvFeedClapCount.visibility = View.GONE
            }
            if (feedList?.get(position)!!.originalPostID.toInt() > 0)
            {

                holder1.tvFeedRepost.visibility = View.VISIBLE

                holder1.tvFeedRepost.text = "Reposted - ${MyUtils.formatDate(
                    feedList?.get(position)!!.postCreatedDate,
                    "yyyy-MM-dd HH:mm:ss",
                    "dd MMM yy"
                )}"
                holder1.tvFeedTimeLocation.text =
                    "From ${"@" + feedList?.get(position)!!.original_userMentionID} " +
                            feedList?.get(position)!!.original_postLocation + "-" + MyUtils.formatDate(
                        feedList?.get(position)!!.original_postCreatedDate!!,
                        "yyyy-MM-dd HH:mm:ss",
                        "dd MMM yy "
                    )

            }
            else
            {
                holder1.tvFeedTimeLocation.text =
                    feedList?.get(position)!!.postLocation + "-" + MyUtils.formatDate(
                        feedList?.get(position)!!.postCreatedDate,
                        "yyyy-MM-dd HH:mm:ss",
                        "dd MMM yy"
                    )
                holder1.tvFeedRepost.visibility = View.GONE
            }
            holder1.tvFeedTimeLocation.setOnClickListener {
                var locationListFragment = LocationListFragment()
                Bundle().apply {
                    putString("location", feedList[holder1.adapterPosition]!!.postLocation)
                    locationListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    locationListFragment,
                    locationListFragment::class.java.name,
                    true
                )
            }
            var Sand = GetDynamicStringDictionaryObjectClass.and
            var sMore = GetDynamicStringDictionaryObjectClass.more
            var sLikedby = GetDynamicStringDictionaryObjectClass.Liked_By
            var sYou = GetDynamicStringDictionaryObjectClass.you_
            if (feedList?.get(position)!!.IsLiekedByYou.equals("Yes", false))
            {
                holder1.tvFeedLikedUserName.visibility = View.VISIBLE

                if (feedList?.get(position)!!.postAll.toInt() > 1) {
                    holder1.tvFeedLikedUserName.text =
                        Html.fromHtml(sLikedby + " " + "<b>" + sYou + "</b>" + " " + Sand + " " + sMore)
                } else {
                    holder1.tvFeedLikedUserName.text =
                        Html.fromHtml(sLikedby + " " + "<b>" + sYou + "</b>" + " ")

                }
            }
            else
            {
                // holder1.tvFeedLikedUserName.visibility = View.GONE

                if (!feedList?.get(position)!!.LikedList.isNullOrEmpty()) {

                    holder1.tvFeedLikedUserName.visibility = View.VISIBLE
                    if (!feedList?.get(position)!!.postLike.isNullOrEmpty() && feedList?.get(position)!!.postLike.toInt() > 1) {

                        holder1.tvFeedLikedUserName.text =
                            Html.fromHtml("Liked by " + "<b>" + feedList?.get(position)!!.LikedList[0].userFirstName + " " + feedList?.get(position)!!.LikedList[0].userLastName + "</b>" + " and more")
                    } else {
                        holder1.tvFeedLikedUserName.text =
                            Html.fromHtml("Liked by " + "<b>" + feedList?.get(position)!!.LikedList[0].userFirstName + " " + feedList?.get(position)!!.LikedList[0].userLastName + "</b>")
                    }
                } else {
                    holder1.tvFeedLikedUserName.visibility = View.INVISIBLE

                }

            }


            if (feedList?.get(position)!!.postLocationVerified.equals("Yes", false))
            {

                /*holder1.tvFeedTimeLocation.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(context, R.drawable.geological_location_verified),
                    null
                )*/

                var string =  holder1.tvFeedTimeLocation.text.toString() + "e"

                val sb = SpannableStringBuilder( holder1.tvFeedTimeLocation?.text.toString() + "e")

                var drawable: Drawable? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable = context.getResources().getDrawable(R.drawable.geological_location_verified, context?.getTheme())
                } else {
                    drawable = context.getResources().getDrawable(R.drawable.geological_location_verified)
                }

                drawable.setBounds(5, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                sb.setSpan(
                    CenteredImageSpan(drawable, -5),
                    string.length - 1,
                    string.length,
                    Spanned.SPAN_INTERMEDIATE
                )
                holder1.tvFeedTimeLocation?.text = sb


            }
            else
            {
               /* holder1.tvFeedTimeLocation.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(context, R.drawable.geological_location_unverified),
                    null
                )*/
                var string =  holder1.tvFeedTimeLocation.text.toString() + "e"

                val sb = SpannableStringBuilder( holder1.tvFeedTimeLocation?.text.toString() + "e")

                var drawable: Drawable? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable = context.getResources().getDrawable(R.drawable.geological_location_unverified, context?.getTheme())
                } else {
                    drawable = context.getResources().getDrawable(R.drawable.geological_location_unverified)
                }

                drawable.setBounds(5, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                sb.setSpan(
                    CenteredImageSpan(drawable, -5),
                    string.length - 1,
                    string.length,
                    Spanned.SPAN_INTERMEDIATE
                )
                holder1.tvFeedTimeLocation?.text = sb

            }


            if (feedList?.get(position)!!.IsLiekedByYou.equals("Yes", false))
            {
//                holder1.ll_post_like.setOnTouchListener(null)
                when (feedList?.get(position)!!.LikeReaction) {
                    "Like" -> {
                        holder1.img_like.setImageResource(R.drawable.like_filled_small)

                    }
                    "Sad" -> {
                        holder1.img_like.setImageResource(R.drawable.worried_reaction_icon_small)

                    }
                    "Angry" -> {
                        holder1.img_like.setImageResource(R.drawable.angry_reaction_icon_small)

                    }
                    "Laugh" -> {
                        holder1.img_like.setImageResource(R.drawable.laughing_reaction_icon_small)

                    }
                }
            }
            else
            {
                holder1.img_like.setImageResource(R.drawable.like_reaction_icon_small)
            }
            holder1.btnFollowing.text = "" + GetDynamicStringDictionaryObjectClass.Following
            holder1.btnFollow.text = "" + GetDynamicStringDictionaryObjectClass.Follow

            if (sessionManager.isLoggedIn()) {

                if (sessionManager.get_Authenticate_User().userID.equals(
                        feedList?.get(position)!!.userID,
                        false
                    )
                ) {
                    holder1.btnFollowing.visibility = View.GONE
                    holder1.btnFollow.visibility = View.GONE
                    holder1.clap_feed_post.visibility = View.GONE
                    holder1.tvFeedClapCount.visibility = View.GONE
                } else {
                    if (!feedList?.get(position)!!.userClapCount.isNullOrEmpty() && !feedList?.get(position)!!.userClapCount.equals(
                            "0",
                            false
                        )
                    ) {
                        holder1.clap_feed_post.visibility = View.VISIBLE
                        holder1.tvFeedClapCount.text = feedList?.get(position)!!.userClapCount
                        holder1.tvFeedClapCount.visibility = View.VISIBLE
                    } else {
                        holder1.clap_feed_post.visibility = View.VISIBLE
                        holder1.tvFeedClapCount.visibility = View.GONE
                    }
                    if (feedList?.get(position)!!.isYouFollowing.equals("Yes", false)) {
//                        holder1.btnFollowing.visibility = View.VISIBLE
                        holder1.btnFollow.visibility = View.GONE
                    } else {
                        holder1.btnFollowing.visibility = View.GONE
                        holder1.btnFollow.visibility = View.VISIBLE
                    }
                }

            } else {
                holder1.btnFollowing.visibility = View.GONE
                holder1.btnFollow.visibility = View.VISIBLE
                holder1.clap_feed_post.visibility = View.VISIBLE
                holder1.tvFeedClapCount.visibility = View.VISIBLE
            }

//            holder1.tvFeedDescriptionReadMore.post {
//                if(holder1.adapterPosition>-1)
//                if (feedList?.get(position)!!.postDescription.length > 1) {
//                    holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                    if (!feedList?.get(position)!!.isReadMore) {
//                        holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                        holder1.tvFeedDescriptionReadMore.text =  "" + GetDynamicStringDictionaryObjectClass.Read_More + "..."
//                        holder1.tvFeedDescriptionFullView.visibility = View.GONE
//
//                    } else {
//
//                        holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                        holder1.tvFeedDescriptionReadMore.text =  "" + GetDynamicStringDictionaryObjectClass.Read_less + "..."
//                        holder1.tvFeedDescriptionFullView.visibility = View.VISIBLE
//
//                    }
//                } else {
//                    holder1.tvFeedDescriptionFullView.visibility = View.GONE
//                    holder1.tvFeedDescriptionReadMore.visibility = View.INVISIBLE
//                }
//            }
//
//
//            holder1.tvFeedDescriptionReadMore?.setOnClickListener {
//
//                if (holder1.adapterPosition > -1) {
//                    if (isExpandPosition > -1 && isExpandPosition != holder1.adapterPosition && feedList?.get(position)!!.isReadMore) {
//                        feedList?.get(isExpandPosition)!!.isReadMore = false
//                        if (feedList?.get(position)!!.postDescription.length > 1) {
//                        if (!feedList?.get(position)!!.isReadMore) {
//                            holder1.tvFeedDescriptionReadMore.text = "" + GetDynamicStringDictionaryObjectClass.Read_More + "..."
//                            holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                            holder1.tvFeedDescriptionFullView.visibility = View.GONE
//
//                        } else {
//                            holder1.tvFeedDescriptionReadMore.text = "" + GetDynamicStringDictionaryObjectClass.Read_less + "..."
//                            holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                            holder1.tvFeedDescriptionFullView.visibility = View.VISIBLE
//
//                        }
//                        } else {
//                            holder1.tvFeedDescriptionFullView.visibility = View.GONE
//
//                            holder1.tvFeedDescriptionReadMore.visibility = View.INVISIBLE
//                        }
//                    }
//
//                    feedList[holder1.adapterPosition]!!.isReadMore = !feedList[holder1.adapterPosition]!!.isReadMore
//                    if (feedList?.get(holder1.adapterPosition)!!.isReadMore)
//                        isExpandPosition = holder1.adapterPosition
//                    else
//                        isExpandPosition = -1
//                    if (feedList?.get(position)!!.postDescription.length > 1) {
//
//                    if (!feedList?.get(position)!!.isReadMore) {
//                        holder1.tvFeedDescriptionReadMore.text =  "" + GetDynamicStringDictionaryObjectClass.Read_More + "..."
//                        holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                        holder1.tvFeedDescriptionFullView.visibility = View.GONE
//
//                    } else {
//                        holder1.tvFeedDescriptionReadMore.text = "" + GetDynamicStringDictionaryObjectClass.Read_less + "..."
//                        holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                        holder1.tvFeedDescriptionFullView.visibility = View.VISIBLE
//
//                    }
//                    } else {
//                        holder1.tvFeedDescriptionFullView.visibility = View.GONE
//                        holder1.tvFeedDescriptionReadMore.visibility = View.INVISIBLE
//                    }
//                    // notifyItemChanged(holder.adapterPosition)
//                }
//            }

            holder1.tvFeedDescription.text = feedList?.get(position)!!.postHeadline


            if (feedList?.get(position)!!.postDescription.isNotEmpty()) {
                val postdescription: String = feedList?.get(position)!!.postDescription
                holder1.tvFeedDescriptionFullView.displayFulltext(postdescription)
                holder1.tvFeedDescriptionFullView.tag = position

                holder1.tvFeedDescriptionFullView.setHashClickListener(object :
                    PostDesTextView.OnHashEventListener {
                    override fun onHashTagClick(friendsId: String?) {
                        var hashTagListFragment = HashTagListFragment()
                        Bundle().apply {
                            putString("hashTag", friendsId)
                            hashTagListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            hashTagListFragment,
                            hashTagListFragment::class.java.name,
                            true
                        )

                    }

                })
                holder1.tvFeedDescriptionFullView.setCustomEventListener(object :
                    PostDesTextView.OnCustomEventListener {
                    override fun onViewMore() {
                        val pos: String =
                            java.lang.String.valueOf(holder1.tvFeedDescriptionFullView.tag!!)
                        val gson = Gson()
                        val json = gson.toJson(feedList?.get(pos.toInt()))

                    }

                    override fun onFriendTagClick(friendsId: String?) {
                        var profileDetailFragment = ProfileDetailFragment()

                        if (!sessionManager.get_Authenticate_User().userID.equals(
                                friendsId,
                                false
                            )
                        ) {
                            var uName = ""
                            if (feedList?.get(holder1.adapterPosition)!!.userID.equals(
                                    friendsId,
                                    false
                                )
                            ) {
                                if (!feedList?.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && !feedList?.get(
                                        holder1.adapterPosition
                                    )!!.userLastName.isNullOrEmpty()
                                ) {
                                    uName =
                                        feedList?.get(holder1.adapterPosition)!!.userFirstName + " " + feedList?.get(
                                            holder1.adapterPosition
                                        )!!.userLastName
                                } else if (feedList?.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && !feedList?.get(
                                        holder1.adapterPosition
                                    )!!.userLastName.isNullOrEmpty()
                                ) {
                                    uName =
                                        "" + feedList?.get(holder1.adapterPosition)!!.userLastName
                                } else if (!feedList?.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && feedList?.get(
                                        holder1.adapterPosition
                                    )!!.userLastName.isNullOrEmpty()
                                ) {
                                    uName =
                                        feedList?.get(holder1.adapterPosition)!!.userFirstName + ""
                                }
                            }
                            Bundle().apply {
                                putString("userID", friendsId)
                                putString("userName", uName)
                                putSerializable("feedData", feedList?.get(holder1.adapterPosition)!!)
                                profileDetailFragment.arguments = this
                            }
                        }
                        (context as MainActivity).navigateTo(
                            profileDetailFragment,
                            profileDetailFragment::class.java.name,
                            true
                        )
                    }
                })
            } else {

                holder1.tvFeedDescriptionFullView.visibility = View.GONE
//                holder1.tvFeedDescriptionReadMore.visibility = View.INVISIBLE
            }

            /* if (!feedList?.get(position)!!.posttag.isNullOrEmpty())
             {
                 holder1.ll_tag.visibility=View.VISIBLE
                 inflateTagList(feedList?.get(position)!!.posttag, holder1.ll_tag)
             }
             else
             {
                 holder1.ll_tag.visibility=View.GONE

             }*/

            if (!feedList?.get(position)!!.postShared.isNullOrEmpty() && !feedList?.get(position)!!.postShared.equals(
                    "0",
                    false
                )
            ) {
                holder1.tv_share_count.visibility = View.VISIBLE
                holder1.tv_share_count.text = feedList?.get(position)!!.postShared
            } else {
                holder1.tv_share_count.visibility = View.INVISIBLE
            }
            if (!feedList?.get(position)!!.postComment.isNullOrEmpty() && !feedList?.get(position)!!.postComment.equals(
                    "0",
                    false
                )
            ) {
                holder1.tv_comment_count.visibility = View.VISIBLE
                holder1.tv_comment_count.text = feedList?.get(position)!!.postComment
            } else {
                holder1.tv_comment_count.visibility = View.INVISIBLE
            }
            if (!feedList?.get(position)!!.postAll.isNullOrEmpty() && !feedList?.get(position)!!.postAll.equals(
                    "0",
                    false
                )
            ) {
                holder1.tv_like_count.visibility = View.VISIBLE
                holder1.tv_like_count.text = feedList?.get(position)!!.postAll
            } else {
                holder1.tv_like_count.visibility = View.INVISIBLE
            }

            holder1.feedThreeDots.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "showDotMenu",
                        holder1.feedThreeDots,
                        feedList[holder1.adapterPosition]!!
                    )
            }

            holder1.ll_user_name.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "otherUserProfile",
                        holder1.ll_user_name,
                        feedList[holder1.adapterPosition]!!
                    )

            }
            holder1.imv_user_dp.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    holder1.ll_user_name.performClick()
            }
            holder1.btnFollowing.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "Following",
                        holder1.btnFollowing
                        , feedList[holder1.adapterPosition]!!
                    )

            }
            holder1.btnFollow.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "Follow",
                        holder1.btnFollow
                        , feedList[holder1.adapterPosition]!!
                    )

            }

            holder1.ll_favourite.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "favourite",
                        holder1.img_favourite,
                        feedList[holder1.adapterPosition]!!
                    )

            }
            holder1.itemView.setOnClickListener(object : DoubleClickListener() {
                override fun onSingleClick(v: View?) {
                }

                override fun onDoubleClick(v: View?) {
                        if (popup?.isShowing!!) {
                            popup?.dismiss()
                        }
                    if (holder1.adapterPosition > -1) {
                        if (feedList?.get(position)!!.IsLiekedByYou.equals("Yes", false)) {
                            onItemClick.onClicklisneter(
                                holder1.adapterPosition,
                                "Unlike",
                                holder1.ll_post_like
                                , feedList[holder1.adapterPosition]!!
                            )
                        } else {
                            ParticleSystem(context, 10, R.drawable.like_filled_big, 3000)
                                .setSpeedByComponentsRange(0f, 0f, -0.2f, 0f)
                                .setAcceleration(0.000003f, 360)
                                .setInitialRotationRange(0, 90)
                                .setRotationSpeed(120f)
                                .setFadeOut(2000)
                                .addModifier(ScaleModifier(0f, 1.5f, 0, 1500))
                                .oneShot(holder1.ll_post_like, 10)

                            onItemClick.onClicklisneter(
                                holder1.adapterPosition,
                                "like",
                                holder1.ll_post_like
                                , feedList[holder1.adapterPosition]!!
                            )
                        }
                    }
                }

            })


            /* holder1.itemView.setOnClickListener {
                val doubleTapRunnable = Runnable {   holder1.isDoubleTapImage = false }

                if (holder1.isDoubleTapImage) {
                    //your logic for double click action
                    if (holder.getAdapterPosition() > -1)
                        onItemClick.onClicklisneter(
                            position,
                            "favourite",
                            holder1.img_favourite,
                            feedList[holder1.adapterPosition]!!
                        )
                    holder1.isDoubleTapImage = false;
                } else {
                    holder1.isDoubleTapImage=true;
                    Handler().postDelayed(doubleTapRunnable, 500); // you can adjust delay in oder to check double tap
                }
            }*/

            holder1.img_favourite.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "favourite",
                        holder1.img_favourite,
                        feedList[holder1.adapterPosition]!!
                    )

            }

            holder1.clap_feed_post.setOnClickListener {
                if (!feedList?.isNullOrEmpty() && feedList[holder1.adapterPosition] != null) {

                    if (holder.getAdapterPosition() > -1) {

                        if (!feedList[holder1.adapterPosition]?.IsClappeddByYou.equals(
                                "Yes",
                                true
                            ) && sessionManager.isLoggedIn()
                        ) {

                            holder1.itemView.clap_feed_post_big.visibility = View.VISIBLE

                            val mAnimation1 = TranslateAnimation(
                                0f,
                                if (holder1.itemView.image_round.x > 0)
                                    -holder1.itemView.image_round.x
                                else
                                    (-((((holder1.recycle_view_feedhome.width) / 2) - context.resources.getDimensionPixelSize(
                                        R.dimen._65sdp
                                    )))).toFloat(),
                                holder1.recycle_view_feedhome.y,
                                (((holder1.recycle_view_feedhome.height) / 2) +
                                        ((holder1.itemView.header_ll.height) / 2)).toFloat()
                            )

                            mAnimation1.duration = 500
                            mAnimation1.fillAfter = true

                            holder1.itemView.clap_feed_post_big.startAnimation(mAnimation1)

                            Handler().postDelayed({
                                holder1.itemView.image_round.visibility = View.VISIBLE
                            }, 400)


                            Handler().postDelayed({
                                holder1.itemView.clap_feed_post_big.visibility = View.GONE
                                holder1.itemView.image_round.visibility = View.GONE

                                onItemClick.onClicklisneter(
                                    position, "clap", holder1.clap_feed_post,
                                    feedList[holder1.adapterPosition]!!
                                )
                            }, 700)
                        } else {
                            onItemClick.onClicklisneter(
                                position, "clap", holder1.clap_feed_post,
                                feedList[holder1.adapterPosition]!!
                            )
                        }
                    }

                }
            }

            holder1.ll_feed_share.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "share",
                        holder1.img_share,
                        feedList[holder1.adapterPosition]!!
                    )

            }

            holder1.ll_feed_comment.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "comment",
                        holder1.img_comment,
                        feedList[holder1.adapterPosition]!!
                    )

            }

            holder1.feedThreeDots.setOnClickListener {
                if (holder.getAdapterPosition() > -1)

                    onItemClick.onClicklisneter(
                        position,
                        "showDotMenu",
                        holder1.feedThreeDots,
                        feedList[holder1.adapterPosition]!!
                    )
            }

            popup = ReactionPopup(
                context,
                ReactionsConfigBuilder(context)
                    .withReactions(imgArray)
                    .withReactionTexts { position ->
                        strings[position]

                    }
                    .withTextBackground(ColorDrawable(Color.TRANSPARENT))
                    .withTextColor(Color.BLACK)
                    .withHorizontalMargin(25)
                    .withPopupGravity(PopupGravity.PARENT_RIGHT)
                    .withPopupColor(context.getDrawable(R.drawable.reaction_rounded_rectangle_bg_with_shadow_white)!!)
                    .withTextHorizontalPadding(0)
                    .withTextVerticalPadding(0)
                    .withTextSize(context.resources.getDimension(R.dimen.reactions_text_size))
                    .build()
            ) { position ->
                if (position == -1) {
                    popup?.dismiss()
                } else {
                    if (holder.getAdapterPosition() > -1) {
                        holder1.img_like.setImageResource(selectedimgArray.get(position))
                        //var count=(feedList[holder1.adapterPosition]!!.postAll).toInt() + 1
                        ParticleSystem(context, 10, selectedimgArray.get(position), 3000)
                            .setSpeedByComponentsRange(0f, 0f, -0.2f, 0f)
                            .setAcceleration(0.000003f, 360)
                            .setInitialRotationRange(0, 90)
                            .setRotationSpeed(120f)
                            .setFadeOut(2000)
                            .addModifier(ScaleModifier(0f, 1.5f, 0, 1500))
                            .oneShot(holder1.ll_post_like, 10)

                        onItemClick.onClicklisneter(
                            holder1.adapterPosition,
                            strings[position],
                            holder1.ll_post_like
                            , feedList[holder1.adapterPosition]!!
                        )

                    }

                }

                true
            }
            holder1.ll_post_like.setOnLongClickListener(popup)
            holder1.img_like.setOnLongClickListener(popup)

            holder1.ll_post_like.setOnClickListener {
                if (popup?.isShowing!!) {
                    popup?.dismiss()
                }
                if (holder1.adapterPosition > -1) {
                    if (feedList?.get(position)!!.IsLiekedByYou.equals("Yes", false)) {
                        onItemClick.onClicklisneter(
                            holder1.adapterPosition,
                            "Unlike",
                            holder1.ll_post_like
                            , feedList[holder1.adapterPosition]!!
                        )
                    } else {
                        ParticleSystem(context, 10, R.drawable.like_filled_big, 3000)
                            .setSpeedByComponentsRange(0f, 0f, -0.2f, 0f)
                            .setAcceleration(0.000003f, 360)
                            .setInitialRotationRange(0, 90)
                            .setRotationSpeed(120f)
                            .setFadeOut(2000)
                            .addModifier(ScaleModifier(0f, 1.5f, 0, 1500))
                            .oneShot(holder1.ll_post_like, 10)

                        onItemClick.onClicklisneter(
                            holder1.adapterPosition,
                            "like",
                            holder1.ll_post_like
                            , feedList[holder1.adapterPosition]!!
                        )
                    }
                }
            }

            holder1.img_like.setOnClickListener {
                holder1.ll_post_like.performClick()
            }

            holder1.tvFeedLikedUserName.setOnClickListener {
                onItemClick.onClicklisneter(
                    holder1.adapterPosition,
                    "likelist",
                    holder1.tv_like_count
                    , feedList[holder1.adapterPosition]!!
                )

            }

        }
        else if (holder is ImageViewHolder) {
            Log.w("SagRSsa",""+feedList?.get(position)!!.postViews)

            val holder1 = holder

            holder1.imv_user_dp.setImageURI(RestClient.image_base_url_users + feedList?.get(position)!!.userProfilePicture)
            holder1.img_user_trophy.setImageURI(RestClient.image_base_Level + feedList?.get(position)?.badgeIcon)

            holder1.tvFeedUserName.text =
                feedList?.get(position)!!.userFirstName + " " + feedList?.get(position)!!.userLastName

            holder1.tvFeedUserId.text = "@" + feedList?.get(position)!!.userMentionID

            if (feedList?.get(position)!!.postViews.isNullOrEmpty() && feedList?.get(position)!!.postViews.equals("0", false)) {

                holder1.tvViewPost.visibility = View.INVISIBLE
                holder1.postView.visibility = View.INVISIBLE
            }
            else
            {
                holder1.tvViewPost.visibility = View.VISIBLE
                holder1.postView.visibility = View.VISIBLE
                holder1.tvViewPost.text =feedList?.get(position)!!.postViews
            }


            if (feedList?.get(position)!!.celebrityVerified.equals("Yes", false)) {
                holder1.img_user_verified.visibility = View.VISIBLE
            } else {
                holder1.img_user_verified.visibility = View.GONE
            }

            if (feedList?.get(position)!!.IsYourFavorite.equals("Yes", false)) {
                holder1.img_favourite.setImageResource(R.drawable.favourite_icon_red)
            } else {
                holder1.img_favourite.setImageResource(R.drawable.favourite_icon_grey)
            }

            var Sand = GetDynamicStringDictionaryObjectClass.and
            var sMore = GetDynamicStringDictionaryObjectClass.more
            var sLikedby = GetDynamicStringDictionaryObjectClass.Liked_By
            var sYou = GetDynamicStringDictionaryObjectClass.you_

            if (feedList?.get(position)!!.IsLiekedByYou.equals("Yes", false)) {
                holder1.tvFeedLikedUserName.visibility = View.VISIBLE

                if (feedList?.get(position)!!.postAll.toInt() > 1) {
                    holder1.tvFeedLikedUserName.text =
                        Html.fromHtml(sLikedby + " " + "<b>" + sYou + "</b>" + " " + Sand + " " + sMore)
                } else {
                    holder1.tvFeedLikedUserName.text =
                        Html.fromHtml(sLikedby + " " + "<b>" + sYou + "</b>" + " ")
                }
            } else {
                //   holder1.tvFeedLikedUserName.visibility = View.GONE
                if (!feedList?.get(position)!!.LikedList.isNullOrEmpty()) {

                    holder1.tvFeedLikedUserName.visibility = View.VISIBLE
                    if (!feedList?.get(position)!!.postLike.isNullOrEmpty() && feedList?.get(position)!!.postLike.toInt() > 1) {

                        holder1.tvFeedLikedUserName.text =
                            Html.fromHtml("Liked by " + "<b>" + feedList?.get(position)!!.LikedList[0].userFirstName + " " + feedList?.get(position)!!.LikedList[0].userLastName + "</b>" + " and more")
                    } else {
                        holder1.tvFeedLikedUserName.text =
                            Html.fromHtml("Liked by " + "<b>" + feedList?.get(position)!!.LikedList[0].userFirstName + " " + feedList?.get(position)!!.LikedList[0].userLastName + "</b>")
                    }
                } else {
                    holder1.tvFeedLikedUserName.visibility = View.INVISIBLE

                }


            }
            if (feedList?.get(position)!!.IsLiekedByYou.equals("Yes", false)) {
                when (feedList?.get(position)!!.LikeReaction) {
                    "Like" -> {
                        holder1.img_like.setImageResource(R.drawable.like_filled_small)

                    }
                    "Sad" -> {
                        holder1.img_like.setImageResource(R.drawable.worried_reaction_icon_small)

                    }
                    "Angry" -> {
                        holder1.img_like.setImageResource(R.drawable.angry_reaction_icon_small)

                    }
                    "Laugh" -> {
                        holder1.img_like.setImageResource(R.drawable.laughing_reaction_icon_small)

                    }
                }
            } else {
                holder1.img_like.setImageResource(R.drawable.like_reaction_icon_small)
            }

            if (feedList?.get(position)!!.IsClappeddByYou.equals("Yes", false)) {
                holder1.clap_feed_post.setImageResource(R.drawable.likefill_icon)
            } else {
                holder1.clap_feed_post.setImageResource(R.drawable.clap_seect)
            }

            if (!feedList?.get(position)!!.userClapCount.isNullOrEmpty() && !feedList?.get(position)!!.userClapCount.equals(
                    "0",
                    false
                )
            ) {
                holder1.clap_feed_post.visibility = View.VISIBLE
                holder1.tvFeedClapCount.text = feedList?.get(position)!!.userClapCount
                holder1.tvFeedClapCount.visibility = View.VISIBLE
            } else {
                holder1.tvFeedClapCount.visibility = View.GONE
                holder1.clap_feed_post.visibility = View.GONE

            }


            if (feedList?.get(position)!!.originalPostID.toInt() > 0) {

                holder1.tvFeedRepost.visibility = View.VISIBLE

                holder1.tvFeedRepost.text = "Reposted - ${MyUtils.formatDate(
                    feedList?.get(position)!!.postCreatedDate,
                    "yyyy-MM-dd HH:mm:ss",
                    "dd MMM yy"
                )}"
                holder1.tvFeedTimeLocation.text =
                    "From ${"@" + feedList?.get(position)!!.original_userMentionID} " +
                            feedList?.get(position)!!.original_postLocation + "-" + MyUtils.formatDate(
                        feedList?.get(position)!!.original_postCreatedDate!!,
                        "yyyy-MM-dd HH:mm:ss",
                        "dd MMM yy"
                    )

            } else {
                holder1.tvFeedTimeLocation.text =
                    feedList?.get(position)!!.postLocation + "-" + MyUtils.formatDate(
                        feedList?.get(position)!!.postCreatedDate,
                        "yyyy-MM-dd HH:mm:ss",
                        "dd MMM yy"
                    )
                holder1.tvFeedRepost.visibility = View.GONE
            }

            if (feedList?.get(position)!!.postLocationVerified.equals("Yes", false)) {

                /*holder1.tvFeedTimeLocation.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    ContextCompat.getDrawable(context, R.drawable.geological_location_verified),
                    null
                )*/

                var string =  holder1.tvFeedTimeLocation.text.toString() + "e"

                val sb = SpannableStringBuilder( holder1.tvFeedTimeLocation?.text.toString() + "e")

                var drawable: Drawable? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable = context.getResources().getDrawable(R.drawable.geological_location_verified, context?.getTheme())
                } else {
                    drawable = context.getResources().getDrawable(R.drawable.geological_location_verified)
                }

                drawable.setBounds(5, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                sb.setSpan(
                    CenteredImageSpan(drawable, -5),
                    string.length - 1,
                    string.length,
                    Spanned.SPAN_INTERMEDIATE
                )
                holder1.tvFeedTimeLocation?.text = sb


            } else {
                /* holder1.tvFeedTimeLocation.setCompoundDrawablesWithIntrinsicBounds(
                     null,
                     null,
                     ContextCompat.getDrawable(context, R.drawable.geological_location_unverified),
                     null
                 )*/
                var string =  holder1.tvFeedTimeLocation.text.toString() + "e"

                val sb = SpannableStringBuilder( holder1.tvFeedTimeLocation?.text.toString() + "e")

                var drawable: Drawable? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable = context.getResources().getDrawable(R.drawable.geological_location_unverified, context?.getTheme())
                } else {
                    drawable = context.getResources().getDrawable(R.drawable.geological_location_unverified)
                }

                drawable.setBounds(5, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                sb.setSpan(
                    CenteredImageSpan(drawable, -5),
                    string.length - 1,
                    string.length,
                    Spanned.SPAN_INTERMEDIATE
                )
                holder1.tvFeedTimeLocation?.text = sb

            }

            holder1.tvFeedTimeLocation.setOnClickListener {
                var locationListFragment = LocationListFragment()
                Bundle().apply {
                    putString("location", feedList[holder1.adapterPosition]!!.postLocation)
                    locationListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    locationListFragment,
                    locationListFragment::class.java.name,
                    true
                )
            }


//            holder1.tvFeedDescriptionReadMore.post {
//                if(holder1.adapterPosition>-1)
//                if (feedList?.get(position)!!.postDescription.length > 1) {
//
//                    if (!feedList?.get(position)!!.isReadMore) {
//
//                    holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                    holder1.tvFeedDescriptionReadMore.text = "" + GetDynamicStringDictionaryObjectClass.Read_More + "..."
//                    holder1.tvFeedDescriptionFullView.visibility = View.GONE
//
//                } else {
//
//                    holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                    holder1.tvFeedDescriptionReadMore.text = " " + GetDynamicStringDictionaryObjectClass.Read_less + "..."
//                    holder1.tvFeedDescriptionFullView.visibility = View.VISIBLE
//
//                }
//                } else {
//                    holder1.tvFeedDescriptionFullView.visibility = View.GONE
//                    holder1.tvFeedDescriptionReadMore.visibility = View.INVISIBLE
//                }
//            }
//
//
//            holder1.tvFeedDescriptionReadMore?.setOnClickListener {
//
//                if (holder1.adapterPosition > -1) {
//                    if (isExpandPosition > -1 && isExpandPosition != holder1.adapterPosition && feedList?.get(position)!!.isReadMore) {
//                        feedList?.get(isExpandPosition)!!.isReadMore = false
//                        if (feedList?.get(position)!!.postDescription.length > 1) {
//
//                            if (!feedList?.get(position)!!.isReadMore) {
//                            holder1.tvFeedDescriptionReadMore.text = "" + GetDynamicStringDictionaryObjectClass.Read_More + "..."
//
//                            holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                            holder1.tvFeedDescriptionFullView.visibility = View.GONE
//
//                        } else {
//                            holder1.tvFeedDescriptionReadMore.text = "" + GetDynamicStringDictionaryObjectClass.Read_less + "..."
//
//                            holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                            holder1.tvFeedDescriptionFullView.visibility = View.VISIBLE
//
//                        }
//                    } else {
//                        holder1.tvFeedDescriptionFullView.visibility = View.GONE
//                        holder1.tvFeedDescriptionReadMore.visibility = View.INVISIBLE
//                    }
//                    }
//
//                    feedList[holder1.adapterPosition]!!.isReadMore =
//                        !feedList[holder1.adapterPosition]!!.isReadMore
//                    if (feedList?.get(holder1.adapterPosition)!!.isReadMore)
//                        isExpandPosition = holder1.adapterPosition
//                    else
//                        isExpandPosition = -1
//                    if (feedList?.get(position)!!.postDescription.length > 1) {
//
//                    if (!feedList?.get(position)!!.isReadMore) {
//
//                        holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                        holder1.tvFeedDescriptionFullView.visibility = View.GONE
//                        holder1.tvFeedDescriptionReadMore.text = "" + GetDynamicStringDictionaryObjectClass.Read_More + "..."
//                    } else {
//                        holder1.tvFeedDescriptionReadMore.text = "" + GetDynamicStringDictionaryObjectClass.Read_less + "..."
//                        holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
//                        holder1.tvFeedDescriptionFullView.visibility = View.VISIBLE
//
//                    }
//                    } else {
//                        holder1.tvFeedDescriptionFullView.visibility = View.GONE
//                        holder1.tvFeedDescriptionReadMore.visibility = View.INVISIBLE
//                    }
//                }
//            }
            holder1.tvFeedDescription.text = feedList?.get(position)!!.postHeadline

            if (feedList?.get(position)!!.postDescription.isNotEmpty()) {
                val postdescription: String = feedList?.get(position)!!.postDescription

                holder1.tvFeedDescriptionFullView.displayFulltext(postdescription)
                // holder1.tvFeedDescriptionFullView.text = holder1.tvFeedDescriptionFullView.hashTag(postdescription,context)
                holder1.tvFeedDescriptionFullView.tag = position
                holder1.tvFeedDescriptionFullView.setHashClickListener(object :
                    PostDesTextView.OnHashEventListener {
                    override fun onHashTagClick(friendsId: String?) {

                        var hashTagListFragment = HashTagListFragment()
                        Bundle().apply {
                            putString("hashTag", friendsId)
                            hashTagListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            hashTagListFragment,
                            hashTagListFragment::class.java.name,
                            true
                        )

                    }

                })
                holder1.tvFeedDescriptionFullView.setCustomEventListener(object :
                    PostDesTextView.OnCustomEventListener {

                    override fun onViewMore() {
                        val pos: String =
                            java.lang.String.valueOf(holder1.tvFeedDescriptionFullView.tag!!)
                        val gson = Gson()
                        val json = gson.toJson(feedList?.get(pos.toInt()))

                    }

                    override fun onFriendTagClick(friendsId: String?) {
                        var profileDetailFragment = ProfileDetailFragment()

                        if (sessionManager.isLoggedIn()) {
                            if (!sessionManager.get_Authenticate_User().userID.equals(
                                    friendsId,
                                    false
                                )
                            ) {
                                var uName = ""
                                if (feedList?.get(holder1.adapterPosition)!!.userID.equals(
                                        friendsId,
                                        false
                                    )
                                ) {
                                    if (!feedList?.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && !feedList?.get(
                                            holder1.adapterPosition
                                        )!!.userLastName.isNullOrEmpty()
                                    ) {
                                        uName =
                                            feedList?.get(holder1.adapterPosition)!!.userFirstName + " " + feedList?.get(
                                                holder1.adapterPosition
                                            )!!.userLastName
                                    } else if (feedList?.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && !feedList?.get(
                                            holder1.adapterPosition
                                        )!!.userLastName.isNullOrEmpty()
                                    ) {
                                        uName =
                                            "" + feedList?.get(holder1.adapterPosition)!!.userLastName
                                    } else if (!feedList?.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && feedList?.get(
                                            holder1.adapterPosition
                                        )!!.userLastName.isNullOrEmpty()
                                    ) {
                                        uName =
                                            feedList?.get(holder1.adapterPosition)!!.userFirstName + ""
                                    }
                                }
                                Bundle().apply {
                                    putString("userID", friendsId)
                                    putString("userName", uName)
                                    putSerializable(
                                        "feedData",
                                        feedList?.get(holder1.adapterPosition)!!
                                    )

                                    profileDetailFragment.arguments = this
                                }
                            }
                            (context as MainActivity).navigateTo(
                                profileDetailFragment,
                                profileDetailFragment::class.java.name,
                                true
                            )
                        } else {
                            Toast.makeText(context, "Please first login..", Toast.LENGTH_LONG)
                                .show()
                        }

                    }
                })
            }
            else
            {
//                holder1.tvFeedDescriptionReadMore.visibility = View.VISIBLE
                holder1.tvFeedDescriptionFullView.visibility = View.GONE
            }

            /*if (!feedList?.get(position)!!.posttag.isNullOrEmpty())
            {
                 holder1.ll_tag.visibility=View.VISIBLE
                 inflateTagList(feedList?.get(position)!!.posttag, holder1.ll_tag)
            }
            else
            {
                 holder1.ll_tag.visibility=View.GONE

            }*/

            if (!feedList?.get(position)!!.postShared.isNullOrEmpty() && !feedList?.get(position)!!.postShared.equals(
                    "0",
                    false
                )
            ) {
                holder1.tv_share_count.visibility = View.VISIBLE
                holder1.tv_share_count.text = feedList?.get(position)!!.postShared
            } else {
                holder1.tv_share_count.visibility = View.INVISIBLE
            }
            if (!feedList?.get(position)!!.postComment.isNullOrEmpty() && !feedList?.get(position)!!.postComment.equals(
                    "0",
                    false
                )
            ) {
                holder1.tv_comment_count.visibility = View.VISIBLE
                holder1.tv_comment_count.text = feedList?.get(position)!!.postComment
            } else {
                holder1.tv_comment_count.visibility = View.INVISIBLE
            }
            if (!feedList?.get(position)!!.postAll.isNullOrEmpty() && !feedList?.get(position)!!.postAll.equals(
                    "0",
                    false
                )
            ) {
                holder1.tv_like_count.visibility = View.VISIBLE
                holder1.tv_like_count.text = feedList?.get(position)!!.postAll
            } else {
                holder1.tv_like_count.visibility = View.INVISIBLE
            }


            holder1.feedThreeDots.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "showDotMenu",
                        holder1.feedThreeDots,
                        feedList[holder1.adapterPosition]!!
                    )
            }

            holder1.feedItemImageAdapter.setActivity(context, position)
            holder1.feedItemImageAdapter.setData(feedList?.get(position)!!, position)

            holder1.feedItemImageAdapter.setClickListener(object :
                FeedItemImageAdapter.ClickInterface {
                override fun openPhotoDetails(i: Int) {

                    //holder1.itemView.performClick()
                }

                override fun favimage(i: Int) {
                   // holder1.itemView.performClick()
                   var photoGalleryFragment = PhotoGalleryFragment()
                    Bundle().apply {
                        putSerializable(
                            "albummedia",
                            feedList?.get(position)!!.postSerializedData[0].albummedia as Serializable
                        )
                        photoGalleryFragment.arguments = this
                    }
                    (context as MainActivity).navigateTo(
                        photoGalleryFragment,
                        photoGalleryFragment::class.java.name,
                        true
                    )
                }

            })
            holder1.ll_user_name.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "otherUserProfile",
                        holder1.feedThreeDots,
                        feedList[holder1.adapterPosition]!!
                    )

            }

            holder1.btnFollowing.text = "" + GetDynamicStringDictionaryObjectClass.Following
            holder1.btnFollow.text = "" + GetDynamicStringDictionaryObjectClass.Follow

            holder1.imv_user_dp.setOnClickListener {
                holder1.ll_user_name.performClick()
            }
            holder1.btnFollowing.setOnClickListener {
                if (holder.getAdapterPosition() > -1) {
                    onItemClick.onClicklisneter(
                        position,
                        "Following",
                        holder1.btnFollowing
                        , feedList[holder1.adapterPosition]!!
                    )
                }

            }
            holder1.btnFollow.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "Follow",
                        holder1.btnFollow
                        , feedList[holder1.adapterPosition]!!
                    )

            }

            holder1.clap_feed_post.setOnClickListener {
                if (holder.getAdapterPosition() > -1) {


                    if (!feedList[holder1.adapterPosition]?.IsClappeddByYou.equals(
                            "Yes",
                            true
                        ) && sessionManager.isLoggedIn()
                    ) {
                        holder1.itemView.clap_feed_post_big.visibility = View.VISIBLE

                        val mAnimation1 = TranslateAnimation(
                            0f, -
                            if (holder1.itemView.image_round.x > 0) holder1.itemView.image_round.x else
                                (((holder1.recycle_view_feedhome.width) / 2) - context.resources.getDimensionPixelSize(
                                    R.dimen._65sdp
                                )).toFloat(),
                            holder1.recycle_view_feedhome.y,
                            (((holder1.recycle_view_feedhome.height) / 2) +
                                    ((holder1.itemView.header_ll.height) / 2)).toFloat()
                        )

                        mAnimation1.duration = 500
                        mAnimation1.fillAfter = true

                        holder1.itemView.clap_feed_post_big.startAnimation(mAnimation1)

                        Handler().postDelayed({
                            holder1.itemView.image_round.visibility = View.VISIBLE
                        }, 400)


                        Handler().postDelayed({
                            holder1.itemView.clap_feed_post_big.visibility = View.GONE
                            holder1.itemView.image_round.visibility = View.GONE

                            onItemClick.onClicklisneter(

                                position, "clap", holder1.clap_feed_post,
                                feedList[holder1.adapterPosition]!!
                            )
                        }, 700)
                    } else {
                        onItemClick.onClicklisneter(
                            position, "clap", holder1.clap_feed_post,
                            feedList[holder1.adapterPosition]!!
                        )
                    }

                }

            }
            holder1.ll_favourite.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "favourite",
                        holder1.img_favourite,
                        feedList[holder1.adapterPosition]!!
                    )

            }

            holder1.itemView.setOnClickListener(object : DoubleClickListener() {
                override fun onSingleClick(v: View?) {


                }

                override fun onDoubleClick(v: View?) {
                    if (popup?.isShowing!!) {
                        popup?.dismiss()
                    }
                    if (holder1.adapterPosition > -1) {
                        if (feedList?.get(position)!!.IsLiekedByYou.equals("Yes", false)) {
                            onItemClick.onClicklisneter(
                                holder1.adapterPosition,
                                "Unlike",
                                holder1.ll_post_like
                                , feedList[holder1.adapterPosition]!!
                            )
                        } else {
                            ParticleSystem(context, 10, R.drawable.like_filled_big, 3000)
                                .setSpeedByComponentsRange(0f, 0f, -0.2f, 0f)
                                .setAcceleration(0.000003f, 360)
                                .setInitialRotationRange(0, 90)
                                .setRotationSpeed(120f)
                                .setFadeOut(2000)
                                .addModifier(ScaleModifier(0f, 1.5f, 0, 1500))
                                .oneShot(holder1.ll_post_like, 10)

                            onItemClick.onClicklisneter(
                                holder1.adapterPosition,
                                "like",
                                holder1.ll_post_like
                                , feedList[holder1.adapterPosition]!!
                            )
                        }
                    }
                }

            })
            holder1.img_favourite.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "favourite",
                        holder1.img_favourite,
                        feedList!![holder1.adapterPosition]!!
                    )

            }
            holder1.ll_feed_share.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "share",
                        holder1.img_share,
                        feedList[holder1.adapterPosition]!!
                    )

            }
            holder1.img_share.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "share",
                        holder1.img_share,
                        feedList[holder1.adapterPosition]!!
                    )

            }
            holder1.ll_feed_comment.setOnClickListener {
                if (holder.getAdapterPosition() > -1)
                    onItemClick.onClicklisneter(
                        position,
                        "comment",
                        holder1.img_comment,
                        feedList[holder1.adapterPosition]!!
                    )

            }


            popup = ReactionPopup(
                context,
                ReactionsConfigBuilder(context)
                    .withReactions(
                        imgArray
                    )
                    .withReactionTexts { position ->
                        strings[position]

                    }
                    .withTextBackground(ColorDrawable(Color.TRANSPARENT))
                    .withTextColor(Color.BLACK)
                    .withHorizontalMargin(25)
                    .withPopupGravity(PopupGravity.PARENT_RIGHT)
                    .withPopupColor(context.getDrawable(R.drawable.reaction_rounded_rectangle_bg_with_shadow_white)!!)
                    .withTextHorizontalPadding(0)
                    .withTextVerticalPadding(0)
                    .withTextSize(context.resources.getDimension(R.dimen.reactions_text_size))
                    .build()

            ) { position ->
                if (position == -1) {
                    popup?.dismiss()
                } else {
                    if (holder1.getAdapterPosition() > -1) {
                        holder1.img_like.setImageResource(selectedimgArray.get(position))

                        //var count=(feedList[holder1.adapterPosition]!!.postAll).toInt() + 1
                        ParticleSystem(context, 10, selectedimgArray.get(position), 3000)
                            .setSpeedByComponentsRange(0f, 0f, -0.2f, 0f)
                            .setAcceleration(0.000003f, 360)
                            .setInitialRotationRange(0, 90)
                            .setRotationSpeed(120f)
                            .setFadeOut(2000)
                            .addModifier(ScaleModifier(0f, 1.5f, 0, 1500))
                            .oneShot(holder1.ll_post_like, 10)

                        onItemClick.onClicklisneter(
                            holder1.adapterPosition,
                            strings[position],
                            holder1.ll_post_like
                            , feedList?.get(holder1.adapterPosition)!!
                        )
                    }


                }

                true
            }
            holder1.ll_post_like.setOnLongClickListener(popup)
            holder1.img_like.setOnLongClickListener(popup)

            holder1.ll_post_like.setOnClickListener {
                if (popup?.isShowing!!) {
                    popup?.dismiss()
                }
                if (holder1.adapterPosition > -1) {
                    if (feedList?.get(position)!!.IsLiekedByYou.equals("Yes", false)) {
                        onItemClick.onClicklisneter(
                            holder1.adapterPosition,
                            "Unlike",
                            holder1.ll_post_like
                            , feedList?.get(holder1.adapterPosition)!!
                        )
                    } else {
                        ParticleSystem(context, 10, R.drawable.like_filled_big, 3000)
                            .setSpeedByComponentsRange(0f, 0f, -0.2f, 0f)
                            .setAcceleration(0.000003f, 360)
                            .setInitialRotationRange(0, 90)
                            .setRotationSpeed(120f)
                            .setFadeOut(2000)
                            .addModifier(ScaleModifier(0f, 1.5f, 0, 1500))
                            .oneShot(holder1.ll_post_like, 10)

                        onItemClick.onClicklisneter(
                            holder1.adapterPosition,
                            "like",
                            holder1.ll_post_like
                            , feedList[holder1.adapterPosition]!!
                        )
                    }
                }

            }

            holder1.img_like.setOnClickListener {
                holder1.ll_post_like.performClick()
            }

            holder1.tvFeedLikedUserName.setOnClickListener {
                onItemClick.onClicklisneter(
                    holder1.adapterPosition,
                    "likelist",
                    holder1.tv_like_count
                    , feedList[holder1.adapterPosition]!!
                )
            }

            if (sessionManager.isLoggedIn()) {

                if (sessionManager.get_Authenticate_User().userID.equals(
                        feedList?.get(position)!!.userID,
                        false
                    )
                ) {
                    holder1.btnFollowing.visibility = View.GONE
                    holder1.btnFollow.visibility = View.GONE
                    holder1.clap_feed_post.visibility = View.GONE
                    holder1.tvFeedClapCount.visibility = View.GONE
                } else {
                    holder1.clap_feed_post.visibility = View.VISIBLE

                    if (!feedList?.get(position)!!.userClapCount.isNullOrEmpty() && !feedList?.get(position)!!.userClapCount.equals(
                            "0",
                            false
                        )
                    ) {
                        holder1.tvFeedClapCount.text = feedList?.get(position)!!.userClapCount
                        holder1.tvFeedClapCount.visibility = View.VISIBLE
                    } else {
                        holder1.tvFeedClapCount.visibility = View.GONE
                    }

                    if (feedList?.get(position)!!.isYouFollowing.equals("Yes", false)) {
//                        holder1.btnFollowing.visibility = View.VISIBLE
                        holder1.btnFollow.visibility = View.GONE
                    } else {
                        holder1.btnFollowing.visibility = View.GONE
                        holder1.btnFollow.visibility = View.VISIBLE
                    }
                }

            } else {
                holder1.btnFollowing.visibility = View.GONE
                holder1.btnFollow.visibility = View.VISIBLE
                holder1.clap_feed_post.visibility = View.VISIBLE
                holder1.tvFeedClapCount.visibility = View.VISIBLE
            }
        }
    }


    private fun inflateTagList(
        posttag: List<Posttag>,
        llTag: LinearLayoutCompat
    ) {

        llTag.removeAllViews()
        for (i in 0 until posttag.size) {
            val view: View = context.layoutInflater.inflate(
                R.layout.item_tag_list, // Custom view/ layout
                llTag, // Root layout to attach the view
                false // Attach with root layout or not
            )
            val tvFeedUserTaglist =
                view.findViewById<PostDesTextView>(R.id.tvFeedUserTaglist)

            tvFeedUserTaglist.text = "@" + posttag[i].userFirstName + posttag[i].userLastName
            llTag.addView(view)
        }

    }

    override fun getItemCount(): Int {
        return feedList?.size!!
    }

    override fun getItemViewType(position: Int): Int {
        return if (feedList?.get(position) == null) MyUtils.Loder_TYPE else getTypeView(position)
    }


    fun getTypeView(position: Int): Int {
        var r = -1
        if (feedList!!.get(position) != null) {

            val type = feedList?.get(position)!!.postMediaType


            when (type) {
                "Video" -> {
                    r = MyUtils.Video_TYPE
                }
                "Photo" -> {
                    r = MyUtils.Image_TYPE
                }

                "Loder" -> {
                    r = MyUtils.Loder_TYPE
                }
            }
        } else {
            r = MyUtils.Loder_TYPE
        }
        return r

    }


    class ViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        var ll_post_like = itemView.ll_post_like
        var img_like = itemView.img_like
        var tv_like_count = itemView.tv_like_count
        var feedThreeDots = itemView.feedThreeDots
        var imv_user_dp = itemView.imv_user_dp
        var ll_user_name = itemView.ll_user_name
        var btnFollowing = itemView.btnFollowing
        var tvViewPost=itemView.tvViewPost
        var postView=itemView.postView
        var clap_feed_post = itemView.clap_feed_post
        var img_feed_post = itemView.img_feed_post
        var ll_favourite = itemView.ll_favourite
        var img_favourite = itemView.img_favourite
        var ll_feed_share = itemView.ll_feed_share
        var img_share = itemView.img_share
        var tv_share_count = itemView.tv_share_count
        var ll_feed_comment = itemView.ll_feed_comment
        var img_comment = itemView.img_comment
        var tv_comment_count = itemView.tv_comment_count
        var tvFeedUserName = itemView.tvFeedUserName
        var tvFeedUserId = itemView.tvFeedUserId
        var img_user_verified = itemView.img_user_verified
        var img_user_trophy = itemView.img_user_trophy
        var btnFollow = itemView.btnFollow
        var tvFeedClapCount = itemView.tvFeedClapCount
        var tvUserId = itemView.tvUserId
        var tvFeedTimeLocation = itemView.tvFeedTimeLocation
        var tvFeedDescription = itemView.tvFeedDescription
//        var tvFeedDescriptionReadMore = itemView.tvFeedDescriptionReadMore
        var tvFeedDescriptionFullView = itemView.tvFeedDescriptionFullView
        var tvFeedLikedUserName = itemView.tvFeedLikedUserName
        var tvFeedRepost = itemView.tvFeedRepost


    }

    fun TextView.setSubstringTypeface(vararg textsToStyle: Pair<String, Int>) {
        val spannableString = SpannableString(this.text)
        for (textToStyle in textsToStyle) {
            val startIndex = this.text.toString().indexOf(textToStyle.first)
            val endIndex = startIndex + textToStyle.first.length

            if (startIndex >= 0) {
                spannableString.setSpan(
                    StyleSpan(textToStyle.second),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String, v: View, objPost: TrendingFeedData)
    }

    private fun getScrennwidth(): Int {

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        widthNew = (width / 3).toInt()
        heightNew = (height / 4).toInt()

        return height
    }

    private fun flyEmoji(get: Int) {

        val animation = ZeroGravityAnimation()
        animation.setCount(1)
        animation.setScalingFactor(0.2f)
        animation.setOriginationDirection(Direction.BOTTOM)
        animation.setDestinationDirection(Direction.TOP)
        animation.setImage(get)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        }
        )

        //  ViewGroup container = findViewById(R.id.animation_holder);
        animation.play(context)


    }

    fun playAvailableVideos(newState: Int, recyclerView: RecyclerView?) {


        if (newState == 0 && recyclerView != null && recyclerView.layoutManager != null) {
            val firstVisiblePosition =
                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val lastVisiblePosition =
                (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()


            val rvRect = Rect()
            recyclerView.getGlobalVisibleRect(rvRect)
            var isAutoPlay = true
            /*if (settingsManager.get_DefaultSettings() != null && settingsManager.get_DefaultSettings().getGeneralaccountsettingsdata() != null && settingsManager.get_DefaultSettings().getGeneralaccountsettingsdata().size() > 0 && settingsManager.get_DefaultSettings().getGeneralaccountsettingsdata().get(
                    0
                ).getUserMediaSettings() != null
            ) {

                if (settingsManager.get_DefaultSettings().getGeneralaccountsettingsdata().get(0).getUserMediaSettings().equalsIgnoreCase(
                        mContext.getString(R.string.neverautoplayvideos)
                    )
                )
                    isAutoPlay = false
                else if (settingsManager.get_DefaultSettings().getGeneralaccountsettingsdata().get(0).getUserMediaSettings().equalsIgnoreCase(
                        mContext.getString(R.string.onwifionly)
                    ) && !Constant.checkNetworkConnectionType("WIFI", mContext)
                ) {

                    isAutoPlay = false


                }


            }*/

            if (firstVisiblePosition >= 0 && newState == 0 && isAutoPlay) {

                var isVideoView = false
                for (i in firstVisiblePosition until lastVisiblePosition) {


                    val holder = recyclerView.findViewHolderForAdapterPosition(i)
                    try {
                        if (i >= 0 && holder != null && holder is VideoViewHolder && feedList?.size!! > 0) {

                            val rowRect = Rect()
                            recyclerView.layoutManager?.findViewByPosition(i)
                                ?.getGlobalVisibleRect(rowRect)

                            var percentFirst: Int
                            if (rowRect.bottom >= rvRect.bottom) {
                                val visibleHeightFirst = rvRect.bottom - rowRect.top
                                percentFirst =
                                    visibleHeightFirst * 100 / recyclerView.layoutManager?.findViewByPosition(
                                        i
                                    )?.height!!
                            } else {
                                val visibleHeightFirst = rowRect.bottom - rvRect.top
                                percentFirst =
                                    visibleHeightFirst * 100 / recyclerView.layoutManager?.findViewByPosition(
                                        i
                                    )?.height!!
                            }

                            if (percentFirst > 100)
                                percentFirst = 100

                            if (percentFirst >= 50) {

                                isVideoView = true
                                pausePlayer(i)
                                if (!feedList[i]!!.postSerializedData[0].albummedia[0].isPlaying)
                                {
                                    videoPlayPosition = i
                                    pausePlayer();
                                    feedList[i]!!.postSerializedData[0].albummedia[0].isPlaying = true


                                    val cvh = holder as VideoViewHolder?
                                    val viewHolder1 =
                                        cvh!!.recycle_view_feedhome.findViewHolderForAdapterPosition(
                                            0
                                        )

                                    if (viewHolder1 is FeedItemVideoAdapter.ImgHolder) {
                                        val holder1 = viewHolder1
                                        playVideo(
                                            i,
                                            holder1.galleryImageView,
                                            holder1.thumnail!!,
                                            holder1.thumnail!!/*holder1.volume!!*/,
                                            holder1.pb!!,
                                            holder1.playicon!!,
                                            cvh.tvViewPost!! as AppCompatTextView,
                                            feedList
                                        )
                                    }

                                }

                                break

                            } else
                            {
                                if(feedList[i]!!.postSerializedData[0].albummedia[0].isPlaying) {
                                    feedList[i]!!.postSerializedData[0].albummedia[0].isPlaying=(false);
                                    videoPlayPosition=i
                                    pausePlayer()
                                }


                            }


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
                if (!isVideoView)
                    pausePlayer()
            }
        }
    }

    fun playVideo(
        videoPlayPosition: Int,
        playerView: PlayerView,
        thumnail: ImageView,
        volume: View?,
        progressBar: ProgressBar,
        playIcon: ImageView,
        tvViewPost: AppCompatTextView,
        feedList: ArrayList<TrendingFeedData?>?
    ) {

        context.runOnUiThread {
            val mHandler = Handler(Looper.getMainLooper())
            mHandler.post(object : Runnable {
                override fun run() {
                    var videoURI: Uri? = null
                    if (!feedList?.get(videoPlayPosition)!!.userSignatureVideo.isNullOrEmpty()) {
                        if (feedList?.get(videoPlayPosition)!!.postSignatureVideo.equals(
                                "Yes",
                                false
                            )
                        ) {
                            videoURI = Uri.parse(
                                RestClient.image_base_url_signutureVideo + feedList?.get(
                                    videoPlayPosition
                                )!!.postSerializedData.get(
                                    0
                                ).albummedia[0].albummediaFile
                            )
                        } else {
                            videoURI = Uri.parse(
                                RestClient.image_base_url_post + feedList?.get(videoPlayPosition)!!.postSerializedData.get(
                                    0
                                ).albummedia[0].albummediaFile
                            )
                        }

                    } else {
                        videoURI = Uri.parse(
                            RestClient.image_base_url_post + feedList?.get(videoPlayPosition)!!.postSerializedData.get(
                                0
                            ).albummedia[0].albummediaFile
                        )
                    }

                    //  Log.e("url",RestClient.image_base_url_post+ feedList?.get(videoPlayPosition)!!.postSerializedData.get(0).albummedia[0].albummediaFile)

                    val bandwidthMeter: BandwidthMeter? = null
                    val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
                    val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)


                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

                    val cacheDataSourceFactory = CacheDataSourceFactory1(context, 5 * 1024 * 1024)

                    val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                        .createMediaSource(videoURI)

                    stopPlayer()

                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)


                    playerView.setShutterBackgroundColor(Color.TRANSPARENT)
                    playerView.player = exoPlayer

                    if (volume != null) {
                        /* if (!isMuteing()) {

                                         if (volume is ImageButton)
                                             volume.background = context.resources
                                                 .getDrawable(R.drawable.ic_volume_up_black_24dp)
                                         else
                                             (volume as ImageView).setImageDrawable(
                                                 context.resources.getDrawable(
                                                     R.drawable.ic_volume_up_black_24dp
                                                 )
                                             )

                                     } else {
                                         if (volume is ImageButton)
                                             volume.background = context.resources
                                                 .getDrawable(R.drawable.ic_volume_off_black_24dp)
                                         else
                                             (volume as ImageView).setImageDrawable(
                                                 context.resources.getDrawable(
                                                     R.drawable.ic_volume_off_black_24dp
                                                 )
                                             )
                                     }*/
                    }

                    exoPlayer?.prepare(mediaSource)
                    exoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
                    muteVideo(isMuteing())

                    (exoPlayer as SimpleExoPlayer).seekTo(
                        feedList?.get(videoPlayPosition)?.postSerializedData?.get(0)!!.albummedia.get(
                            0
                        ).duration
                    )

                    exoPlayer?.playWhenReady = true

                    progressBar.visibility = View.VISIBLE

                    if (playIcon.visibility == View.VISIBLE)
                        playIcon.visibility = View.GONE


                    //  exoPlayer.seekTo(selectVideoList.get(pos).getSeekTo());
                    exoPlayer?.addListener(object : Player.EventListener {

                        override fun onTimelineChanged(
                            timeline: Timeline?,
                            manifest: Any?,
                            reason: Int
                        ) {


                        }

                        override fun onTracksChanged(
                            trackGroups: TrackGroupArray?,
                            trackSelections: TrackSelectionArray?
                        ) {

                        }

                        override fun onLoadingChanged(isLoading: Boolean) {

                        }

                        override fun onPlayerStateChanged(
                            playWhenReady: Boolean,
                            playbackState: Int
                        ) {

                            when (playbackState) {
                                Player.STATE_BUFFERING ->
                                    progressBar.visibility = View.VISIBLE


                                Player.STATE_ENDED -> if (exoPlayer != null) {
                                    exoPlayer?.seekTo(0)
                                    thumnail.visibility = View.VISIBLE
                                    progressBar.visibility = View.GONE
                                    playIcon.visibility = View.VISIBLE
                                    pausePlayer()

                                }
                                Player.STATE_IDLE -> {
                                }

                                Player.STATE_READY -> {
                                    progressBar.visibility = View.GONE
                                    thumnail.visibility = View.GONE
                                    if (thumnail.visibility == View.VISIBLE)
                                        thumnail.visibility = View.GONE

                                    progressBar.visibility = View.GONE
                                    if (playIcon.visibility == View.VISIBLE)
                                        playIcon.visibility = View.GONE
                                    if(videoPlayPosition >-1 &&  !feedList?.isNullOrEmpty())
                                    {
                                        try {
                                            Handler().postDelayed({

                                                onItemClick.onClicklisneter(
                                                    videoPlayPosition,
                                                    "videoView",
                                                    tvViewPost
                                                    , feedList[videoPlayPosition]!!
                                                )
                                            }, 5000)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                                else -> {
                                }
                            }
                        }

                        override fun onRepeatModeChanged(repeatMode: Int) {

                        }

                        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

                        }

                        override fun onPlayerError(error: ExoPlaybackException?)
                        {
                            Log.d("ExoPlayer", error!!.toString())
                            pausePlayer()
                            /*  progressBar.setVisibility(View.GONE);
                                    thumnail.setVisibility(View.VISIBLE);
                                    stopPlayer();*/
                        }

                        override fun onPositionDiscontinuity(reason: Int) {

                        }

                        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

                        }

                        override fun onSeekProcessed() {

                        }
                    })


                }

            })
        }


    }

    fun stopPlayer() {
        /* for(i in 0 until feedList?.size)
                           {
                               if(feedList?.get(i)!=null&&feedList?.get(i).postSerializedData!![0]!!.albummedia!=null&& feedList?.get(
                                       i
                                   ).postSerializedData!![0]!!.albummedia.isNotEmpty()
                               )
                                   feedList?.get(i).postSerializedData!![0]!!.albummedia[0].isPlaying=(false);
                           }*/
        if (exoPlayer != null) {
            exoPlayer?.stop(true)
            exoPlayer?.release()
            exoPlayer = null
        }


    }

    fun pausePlayer() {
        try {
            context.runOnUiThread(Runnable {

                videoPlayPosition = -1
                for (i in feedList!!.indices) {
                    if (feedList?.get(i) != null && !feedList?.get(i)?.postSerializedData.isNullOrEmpty() && !feedList?.get(
                            i
                        )?.postSerializedData?.get(0)?.albummedia.isNullOrEmpty()
                    )
                        if (feedList[i]!!.postSerializedData[0].albummedia[0].isPlaying) {
                            videoPlayPosition = i
                            break
                        }
                }

                if (videoPlayPosition > -1) {
                    if (exoPlayer != null)
                        feedList[videoPlayPosition]!!.postSerializedData[0].albummedia[0].duration =
                            (exoPlayer?.currentPosition!!)
                    feedList[videoPlayPosition]!!.postSerializedData[0].albummedia[0].isPlaying =
                        false
                    notifyItemChanged(videoPlayPosition)
                    videoPlayPosition = -1

                }
                if (exoPlayer != null) {
                    exoPlayer?.stop(true)
                    exoPlayer?.release()
                    exoPlayer = null
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            stopPlayer()
        }

    }

    fun pausePlayer(positionNot: Int) {


        try {
            context.runOnUiThread(Runnable {
                Handler().post {
                    videoPlayPosition = -1
                    for (i in feedList!!.indices) {
                        if (feedList?.get(i) != null && feedList?.get(i)!!.postSerializedData[0].albummedia != null && feedList?.get(
                                i
                            )!!.postSerializedData[0].albummedia.isNotEmpty()
                        )
                            if (feedList[i]!!.postSerializedData[0].albummedia[0].isPlaying && positionNot != i) {
                                videoPlayPosition = i
                                break
                            }
                    }

                    if (videoPlayPosition > -1) {
                        if (exoPlayer != null)
                            feedList?.get(videoPlayPosition)!!.postSerializedData[0].albummedia[0].duration =
                                (
                                        exoPlayer?.currentPosition!!
                                        )
                        feedList?.get(videoPlayPosition)!!.postSerializedData[0].albummedia[0].isPlaying =
                            (false)
                        notifyItemChanged(videoPlayPosition)
                        videoPlayPosition = -1


                        if (exoPlayer != null) {
                            exoPlayer?.stop(true)
                            exoPlayer?.release()
                            exoPlayer = null
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            stopPlayer()
        }

    }

    fun isMuteing(): Boolean {
        return MyUtils.isMuteing
    }

    fun setMuteing(muteing: Boolean) {
        MyUtils.isMuteing = muteing
    }

    fun muteVideo(isMute: Boolean) {
        if (exoPlayer != null) {
            if (isMute)
                exoPlayer?.volume = 0f
            else
                exoPlayer?.volume = 1f
            setMuteing(isMute)
        }
    }


    private fun getScreenWidth() {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        newHeight = height - ((height * 16) / 100)
        newWidth = width - ((width * 25) / 100)
    }

}