package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.ReactionPopup.PopupGravity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.ReactionPopup.ReactionPopup
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.ReactionPopup.ReactionsConfigBuilder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.Service.NotifyInterface
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.LoginActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.FollowModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.PostClapModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.PostFavouriteModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.PostViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.SharePostPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.CacheDataSourceFactory1
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.PostDesTextView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.gson.Gson
import com.plattysoft.leonids.ParticleSystem
import com.plattysoft.leonids.modifiers.ScaleModifier
import kotlinx.android.synthetic.main.fragment_video_details.*
import kotlinx.android.synthetic.main.fragment_video_details.btnFollow
import kotlinx.android.synthetic.main.fragment_video_details.btnFollowing
import kotlinx.android.synthetic.main.fragment_video_details.clap_feed_post
import kotlinx.android.synthetic.main.fragment_video_details.clap_feed_post_big
import kotlinx.android.synthetic.main.fragment_video_details.image_round
import kotlinx.android.synthetic.main.fragment_video_details.imgCloseIcon
import kotlinx.android.synthetic.main.fragment_video_details.img_favourite
import kotlinx.android.synthetic.main.fragment_video_details.img_like
import kotlinx.android.synthetic.main.fragment_video_details.img_user_verified
import kotlinx.android.synthetic.main.fragment_video_details.imv_user_dp
import kotlinx.android.synthetic.main.fragment_video_details.linearlayoutheader
import kotlinx.android.synthetic.main.fragment_video_details.ll_favourite
import kotlinx.android.synthetic.main.fragment_video_details.ll_feed_comment
import kotlinx.android.synthetic.main.fragment_video_details.ll_feed_share
import kotlinx.android.synthetic.main.fragment_video_details.ll_post_like
import kotlinx.android.synthetic.main.fragment_video_details.tvFeedClapCount
import kotlinx.android.synthetic.main.fragment_video_details.tvFeedDescription
import kotlinx.android.synthetic.main.fragment_video_details.tvFeedDescriptionFullView
import kotlinx.android.synthetic.main.fragment_video_details.tvFeedLikedUserName
import kotlinx.android.synthetic.main.fragment_video_details.tvFeedRepost
import kotlinx.android.synthetic.main.fragment_video_details.tvFeedTimeLocation
import kotlinx.android.synthetic.main.fragment_video_details.tvFeedUserId
import kotlinx.android.synthetic.main.fragment_video_details.tvFeedUserName
import kotlinx.android.synthetic.main.fragment_video_details.tv_like_count
import kotlinx.android.synthetic.main.fragment_video_details.tv_share_count
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class VideoDetailsFragment : Fragment() {

    var mActivity: Activity? = null
    var trendingFeedData: TrendingFeedData? = null
    var v: View? = null

    var exoPlayer: SimpleExoPlayer? = null
    var notifyInterface: NotifyInterface? = null
    var userData: RegisterPojo.Data? = null

    private val strings = arrayOf("Angry", "Sad", "Laugh", "Like")

    /*var imgArray = intArrayOf(
        R.drawable.laughing_reaction_icon_big,
        R.drawable.angry_reaction_icon_big,
        R.drawable.worried_reaction_icon_big,
        R.drawable.like_filled_big_white
    )*/
    var imgArray = intArrayOf(
        R.drawable.angry_reaction_icon_big,
        R.drawable.worried_reaction_icon_big,
        R.drawable.laughing_reaction_icon_big,
        R.drawable.like_filled_big_white
    )

    var selectedimgArray = intArrayOf(
        R.drawable.angry_reaction_icon_small,
        R.drawable.worried_reaction_icon_small,
        R.drawable.laughing_reaction_icon_small,
        R.drawable.like_filled_small_white
    )
    var popup: ReactionPopup? = null


    /* var feedThreeDots
     var trendingItemPhotoAdapter
     var recycle_view_feedhome
     var ll_post_like
     var img_like
     var imv_user_dp
     var ll_user_name
     var btnFollowing
     var clap_feed_post
     var ll_favourite
     var img_favourite
     var ll_feed_share
     var img_share
     var tv_share_count
     var ll_feed_comment
     var img_comment
     var tv_comment_count
     var tv_like_count
     var tvFeedUserName
     var tvFeedUserId
     var img_user_verified
     var img_user_trophy
     var btnFollow
     var tvFeedClapCount
     var tvFeedTimeLocation
     var tvFeedDescription
     var tvFeedDescriptionReadMore
     var tvFeedDescriptionFullView
     var ll_tag
     var tvFeedLikedUserName
     var tvFeedRepost*/
    var tv_comment_count: AppCompatTextView? = null

    var Sand: String = ""
    var sMore: String = ""
    var sLikedby: String = ""
    var sYou: String = ""
    var sessionManager: SessionManager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_video_details, container, false)
        }



        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
        try {
            notifyInterface = activity as NotifyInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement TextClicked"
            )
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)

//        (activity as MainActivity).showHideBottomNavigation(false)
        if (arguments != null) {
            trendingFeedData = arguments?.getSerializable("feedData") as TrendingFeedData?
        }

        tv_comment_count = v?.findViewById(R.id.tv_comment_count)

        if (sessionManager?.isLoggedIn()!!) {
            if (sessionManager?.get_Authenticate_User() != null) {
                userData = sessionManager?.get_Authenticate_User()
            }
        }

        imgCloseIcon.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        if (trendingFeedData != null) {
            Sand = GetDynamicStringDictionaryObjectClass?.and
            sMore = GetDynamicStringDictionaryObjectClass?.more
            sLikedby = GetDynamicStringDictionaryObjectClass?.Liked_By
            sYou = GetDynamicStringDictionaryObjectClass?.you_

            setData()
        }

        imv_user_dp.setOnClickListener {
            if (sessionManager?.isLoggedIn()!!) {
                var profileDetailFragment = ProfileDetailFragment()
                if (!userData?.userID.equals(
                        trendingFeedData!!.userID,
                        false
                    )
                ) {
                    var uName = ""
                    if (!trendingFeedData!!.userFirstName.isNullOrEmpty() && !trendingFeedData!!.userLastName.isNullOrEmpty()
                    ) {
                        uName =
                            trendingFeedData!!.userFirstName + " " + trendingFeedData!!.userLastName
                    } else if (trendingFeedData!!.userFirstName.isNullOrEmpty() && !trendingFeedData!!.userLastName.isNullOrEmpty()
                    ) {
                        uName = "" + trendingFeedData!!.userLastName
                    } else if (!trendingFeedData!!.userFirstName.isNullOrEmpty() && trendingFeedData!!.userLastName.isNullOrEmpty()
                    ) {
                        uName = trendingFeedData!!.userFirstName + ""
                    }
                    Bundle().apply {
                        putString("userID", trendingFeedData!!.userID)
                        putString("userName", uName)
                        putSerializable("feedData", trendingFeedData!!)
                        profileDetailFragment.arguments = this
                    }
                }
                (activity as MainActivity).navigateTo(
                    profileDetailFragment,
                    profileDetailFragment::class.java.name,
                    true
                )
            } else {
                MyUtils.startActivity(
                    mActivity!!,
                    LoginActivity::class.java,
                    false
                )

            }
        }
        tvFeedUserName.setOnClickListener {
            imv_user_dp.performClick()
        }

        playicon.setOnClickListener {

            playVideo(
                galleryImageView,
                thumnail!!,
                thumnail!!/*holder1.volume!!*/,
                pb!!,
                playicon!!, trendingFeedData!!
            )
        }


        tvFeedTimeLocation.setOnClickListener {
            var locationListFragment = LocationListFragment()
            Bundle().apply {
                if (trendingFeedData != null) {
                    putString("location", trendingFeedData?.postLocation)
                }
                locationListFragment.arguments = this
            }
            (context as MainActivity).navigateTo(
                locationListFragment,
                locationListFragment::class.java.name,
                true
            )
        }

        btnFollowing?.text = GetDynamicStringDictionaryObjectClass?.Following
        btnFollowing.setOnClickListener {
            if (sessionManager?.isLoggedIn()!!){
                getUserFollow("unfollow", trendingFeedData?.userID!!)

            }else{
                MyUtils.startActivity(
                    mActivity!!,
                    LoginActivity::class.java,
                    false
                )
            }


        }
        btnFollow?.text = GetDynamicStringDictionaryObjectClass?.Follow

        btnFollow.setOnClickListener {
            if (sessionManager?.isLoggedIn()!!){
                getUserFollow("follow", trendingFeedData?.userID!!)

            }else{
            MyUtils.startActivity(
                mActivity!!,
                LoginActivity::class.java,
                false
            )
        }

        }
        clap_feed_post.setOnClickListener {
            if (sessionManager?.isLoggedIn()!!) {


                var action = ""
                if (trendingFeedData?.IsClappeddByYou.equals("Yes")) {
                    action = "UnClapPost"
                    setPostClap(action)
                } else {

                    clap_feed_post_big.visibility = View.VISIBLE
                    clap_feed_post_big.alpha = 1f

                    val mAnimation1 = TranslateAnimation(
                        0f, -
                        if (image_round.x > 0) image_round.x else
                            (((rvVideoDetails.width) / 2) - mActivity?.resources!!.getDimensionPixelSize(
                                R.dimen._65sdp
                            )).toFloat(),
                        rvVideoDetails.y,
                        (((rvVideoDetails.height) / 2) +
                                ((linearlayoutheader.height) / 2)).toFloat()
                    )

                    mAnimation1.duration = 500
                    mAnimation1.fillAfter = true

                    clap_feed_post_big.startAnimation(mAnimation1)

                    Handler().postDelayed({
                        image_round.visibility = View.VISIBLE
                    }, 400)

                    Handler().postDelayed({
                        clap_feed_post_big.visibility = View.GONE
                        image_round.visibility = View.GONE
                        clap_feed_post_big.alpha = 0f
                        action = "ClapPost"
                        setPostClap(action)

                    }, 700)

                }
            }else{
            MyUtils.startActivity(
                mActivity!!,
                LoginActivity::class.java,
                false
            )
        }

        }


        ll_favourite.setOnClickListener {
            if (sessionManager?.isLoggedIn()!!){
            feedFavorite(img_favourite)
            }else{
                MyUtils.startActivity(
                    mActivity!!,
                    LoginActivity::class.java,
                    false
                )
            }


        }

        ll_feed_share.setOnClickListener {

            postData(trendingFeedData!!, "Gmail")

        }

        ll_feed_comment.setOnClickListener {
            if (sessionManager?.isLoggedIn()!!){
                val bundle = Bundle()
                bundle.apply {
                    putString("postID", trendingFeedData?.postID)
                    putString("postUserID", trendingFeedData?.userID)
                    putSerializable("feedData", trendingFeedData)
                }
                (activity as MainActivity).navigateTo(
                    CommentListFragment(), bundle,
                    CommentListFragment::class.java.name,
                    true
                )
        }else{
            MyUtils.startActivity(
                mActivity!!,
                LoginActivity::class.java,
                false
            )
        }

        }


        popup = ReactionPopup(
            mActivity!!,
            ReactionsConfigBuilder(mActivity!!)
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
                .withPopupColor(mActivity!!.getDrawable(R.drawable.reaction_rounded_rectangle_bg_with_shadow_white)!!)
                .withTextHorizontalPadding(0)
                .withTextVerticalPadding(0)
                .withTextSize(mActivity!!.resources.getDimension(R.dimen.reactions_text_size))
                .build()

        ) { position ->
            if (position == -1) {
                popup?.dismiss()
            } else {
                img_like.setImageResource(selectedimgArray.get(position))




                ParticleSystem(mActivity, 10, selectedimgArray.get(position), 3000)
                    .setSpeedByComponentsRange(0f, 0f, -0.2f, 0f)
                    .setAcceleration(0.000003f, 360)
                    .setInitialRotationRange(0, 90)
                    .setRotationSpeed(120f)
                    .setFadeOut(2000)
                    .addModifier(ScaleModifier(0f, 1.5f, 0, 1500))
                    .oneShot(ll_post_like, 10)

                feedLike1(trendingFeedData!!, strings[position], ll_post_like)

            }

            true
        }
        ll_post_like.setOnLongClickListener(popup)
        img_like.setOnLongClickListener(popup)

        ll_post_like.setOnClickListener {
            if (sessionManager?.isLoggedIn()!!){
                if (popup?.isShowing!!) {
                    popup?.dismiss()
                }

                if (trendingFeedData?.IsLiekedByYou.equals("Yes", false)) {
                    feedLike1(
                        trendingFeedData!!,
                        "Unlike",
                        ll_post_like
                    )
                } else {
                    ParticleSystem(mActivity, 10, R.drawable.like_filled_big_white, 3000)
                        .setSpeedByComponentsRange(0f, 0f, -0.2f, 0f)
                        .setAcceleration(0.000003f, 360)
                        .setInitialRotationRange(0, 90)
                        .setRotationSpeed(120f)
                        .setFadeOut(2000)
                        .addModifier(ScaleModifier(0f, 1.5f, 0, 1500))
                        .oneShot(ll_post_like, 10)

                    feedLike1(
                        trendingFeedData!!,
                        "Like",
                        ll_post_like
                    )
                }
            }
            else{
                MyUtils.startActivity(
                    mActivity!!,
                    LoginActivity::class.java,
                    false
                )
            }
        }

        img_like.setOnClickListener {
            ll_post_like.performClick()
        }
        tvFeedLikedUserName.setOnClickListener {
            if (sessionManager?.isLoggedIn()!!) {

                var postReactUserListFragment = PostReactUserListFragment()
                Bundle().apply {
                    putSerializable("postData", trendingFeedData)
                    postReactUserListFragment.arguments = this
                }
                (activity as MainActivity).navigateTo(
                    postReactUserListFragment,
                    postReactUserListFragment::class.java.name,
                    true
                )
            } else {
                MyUtils.startActivity(
                    mActivity!!,
                    LoginActivity::class.java,
                    false
                )
            }
        }

    }


    private fun setData() {

        setClapData()

        imv_user_dp.setImageURI(RestClient.image_base_url_users + trendingFeedData?.userProfilePicture)
        thumnail.setImageURI(RestClient.image_base_url_post + trendingFeedData?.postSerializedData!![0].albummedia[0].albummediaThumbnail)
        tvFeedUserName.text =
            trendingFeedData?.userFirstName + " " + trendingFeedData?.userLastName
        tvFeedUserId.text = "@" + trendingFeedData?.userMentionID
        if (trendingFeedData?.celebrityVerified.equals("Yes", false)) {
            img_user_verified.visibility = View.VISIBLE
        } else {
            img_user_verified.visibility = View.GONE
        }

        if (trendingFeedData?.IsLiekedByYou.equals("Yes", false)) {
            tvFeedLikedUserName.visibility = View.VISIBLE
            if (trendingFeedData?.postAll!!.toInt() > 1) {
                tvFeedLikedUserName.text =
                    Html.fromHtml(sLikedby + " " + "<b>" + sYou + "</b>" + " " + Sand + " " + sMore)
            } else {
                tvFeedLikedUserName.text = Html.fromHtml(sLikedby + " " + "<b>" + sYou + "</b>")
            }

        } else {
            tvFeedLikedUserName.visibility = View.GONE
            if (!trendingFeedData?.LikedList.isNullOrEmpty()) {

                tvFeedLikedUserName.visibility = View.VISIBLE
                if (!trendingFeedData?.postLike.isNullOrEmpty() && trendingFeedData?.postLike!!.toInt() > 1) {

                    tvFeedLikedUserName.text =
                        Html.fromHtml("Liked by " + "<b>" + trendingFeedData?.LikedList!![0].userFirstName + " " + trendingFeedData?.LikedList!![0].userLastName + "</b>" + " and more")
                } else {
                    tvFeedLikedUserName.text =
                        Html.fromHtml("Liked by " + "<b>" + trendingFeedData?.LikedList!![0].userFirstName + " " + trendingFeedData?.LikedList!![0].userLastName + "</b>")
                }
            } else {
                // tvFeedLikedUserName.visibility = View.GONE
                if (!trendingFeedData?.LikedList.isNullOrEmpty()) {

                    tvFeedLikedUserName.visibility = View.VISIBLE
                    if (!trendingFeedData?.postLike.isNullOrEmpty() && trendingFeedData?.postLike!!.toInt() > 1) {

                        tvFeedLikedUserName.text =
                            Html.fromHtml("Liked by " + "<b>" + trendingFeedData?.LikedList!![0].userFirstName + " " + trendingFeedData?.LikedList!![0].userLastName + "</b>" + " and more")
                    } else {
                        tvFeedLikedUserName.text =
                            Html.fromHtml("Liked by " + "<b>" + trendingFeedData?.LikedList!![0].userFirstName + " " + trendingFeedData?.LikedList!![0].userLastName + "</b>")
                    }
                } else {
                    tvFeedLikedUserName.visibility = View.GONE

                }
            }
        }


        /* if (trendingFeedData?.IsLiekedByYou.equals("Yes", false)) {
             tvFeedLikedUserName.visibility = View.VISIBLE
             if (!trendingFeedData?.LikedList.isNullOrEmpty())
             {
                 if (trendingFeedData?.LikedList!!.size > 1)
                 {
                     tvFeedLikedUserName.text =
                         "Liked by ${userData?.userFirstName + " " + userData?.userLastName} and more"
                 } else {
                     tvFeedLikedUserName.text =
                         "Liked by ${userData?.userFirstName + " " + userData?.userLastName}"

                 }
             } else {
                 tvFeedLikedUserName.text =
                     "Liked by ${userData?.userFirstName + " " + userData?.userLastName}"

             }
         } else {
             if (!trendingFeedData?.LikedList.isNullOrEmpty()) {
                 tvFeedLikedUserName.visibility = View.VISIBLE

                 tvFeedLikedUserName.text =
                     "Liked by ${trendingFeedData?.LikedList!![0].userFirstName + " " + trendingFeedData?.LikedList!![0].userLastName} and more"

             } else {
                 tvFeedLikedUserName.visibility = View.GONE

             }

         }*/

        if (!trendingFeedData?.userClapCount.isNullOrEmpty() && !trendingFeedData?.userClapCount.equals(
                "0",
                false
            )
        ) {
            tvFeedClapCount.text = trendingFeedData?.userClapCount
            tvFeedClapCount.visibility = View.VISIBLE
        } else {
            tvFeedClapCount.visibility = View.GONE
        }
        setPostLikeData(trendingFeedData!!)


        if (trendingFeedData?.originalPostID!!.toInt() > 0) {

            tvFeedRepost.visibility = View.VISIBLE

            tvFeedRepost.text = "Reposted - ${MyUtils.formatDate(
                trendingFeedData?.postCreatedDate!!,
                "yyyy-MM-dd HH:mm:ss",
                "EEE, d yyyy"
            )}"
            tvFeedTimeLocation.text = "From ${"@" + trendingFeedData?.original_userMentionID} " +
                    trendingFeedData?.original_postLocation + "-" + MyUtils.formatDate(
                trendingFeedData?.original_postCreatedDate!!,
                "yyyy-MM-dd HH:mm:ss",
                "EEE, d yyyy"
            )

        } else {
            tvFeedTimeLocation.text =
                trendingFeedData?.postLocation + "-" + MyUtils.formatDate(
                    trendingFeedData?.postCreatedDate!!,
                    "yyyy-MM-dd HH:mm:ss",
                    "EEE, d yyyy"
                )
            tvFeedRepost.visibility = View.GONE
        }

        setFavoutiteData(trendingFeedData!!)


        if (trendingFeedData?.postLocationVerified.equals("Yes", false)) {
            tvFeedTimeLocation.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(mActivity!!, R.drawable.geological_location_verified),
                null
            )


        } else {
            tvFeedTimeLocation.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(mActivity!!, R.drawable.geological_location_unverified),
                null
            )

        }

        if (sessionManager?.isLoggedIn()!!) {
            if (sessionManager?.get_Authenticate_User()!!.userID.equals(
                    trendingFeedData?.userID,
                    false
                )
            ) {
                btnFollowing.visibility = View.GONE
                btnFollow.visibility = View.GONE
                clap_feed_post.visibility = View.GONE
                tvFeedClapCount.visibility = View.GONE
            } else {
                if (!trendingFeedData?.userClapCount.isNullOrEmpty() && !trendingFeedData?.userClapCount.equals(
                        "0",
                        false
                    )
                ) {
                    tvFeedClapCount.text = trendingFeedData?.userClapCount
                    tvFeedClapCount.visibility = View.VISIBLE
                } else {
                    tvFeedClapCount.visibility = View.GONE
                }
                setFollowData()
            }

        } else {
            btnFollowing.visibility = View.GONE
            btnFollow.visibility = View.VISIBLE
            clap_feed_post.visibility = View.VISIBLE
            tvFeedClapCount.visibility = View.VISIBLE
        }

        tvFeedDescription.text = trendingFeedData?.postHeadline

        if (trendingFeedData?.postDescription?.isNotEmpty()!!) {
            val postdescription: String = trendingFeedData?.postDescription!!

            tvFeedDescriptionFullView.displayFulltext(postdescription)

            tvFeedDescriptionFullView.setHashClickListener(object :
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
            tvFeedDescriptionFullView.setCustomEventListener(object :
                PostDesTextView.OnCustomEventListener {
                override fun onViewMore() {


                }

                override fun onFriendTagClick(friendsId: String?) {
                    var profileDetailFragment = ProfileDetailFragment()

                    if (!sessionManager?.get_Authenticate_User()!!.userID.equals(
                            friendsId,
                            false
                        )
                    ) {
                        var uName = ""
                        if (trendingFeedData?.userID.equals(
                                friendsId,
                                false
                            )
                        ) {
                            if (!trendingFeedData?.userFirstName.isNullOrEmpty() && !trendingFeedData?.userLastName.isNullOrEmpty()
                            ) {
                                uName =
                                    trendingFeedData?.userFirstName + " " + trendingFeedData?.userLastName
                            } else if (trendingFeedData?.userFirstName.isNullOrEmpty() && !trendingFeedData?.userLastName.isNullOrEmpty()
                            ) {
                                uName =
                                    "" + trendingFeedData?.userLastName
                            } else if (!trendingFeedData?.userFirstName.isNullOrEmpty() && trendingFeedData?.userLastName.isNullOrEmpty()
                            ) {
                                uName = trendingFeedData?.userFirstName + ""
                            }
                        }
                        Bundle().apply {
                            putString("userID", friendsId)
                            putString("userName", uName)
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
            tvFeedDescriptionFullView.text = ""
        }

        if (!trendingFeedData?.postShared.isNullOrEmpty() && !trendingFeedData?.postShared.equals(
                "0",
                false
            )
        ) {
            tv_share_count.visibility = View.VISIBLE
            tv_share_count.text = trendingFeedData?.postShared
        } else {
            tv_share_count.visibility = View.INVISIBLE
        }
        if (!trendingFeedData?.postComment.isNullOrEmpty() && !trendingFeedData?.postComment.equals(
                "0",
                false
            )
        ) {
            tv_comment_count?.visibility = View.VISIBLE
            setCommentCount(trendingFeedData?.postComment!!.toInt())
        } else {
            tv_comment_count?.visibility = View.INVISIBLE
        }
        if (!trendingFeedData?.postAll.isNullOrEmpty() && !trendingFeedData?.postAll.equals(
                "0",
                false
            )
        ) {
            tv_like_count.visibility = View.VISIBLE
            tv_like_count.text = trendingFeedData?.postAll
        } else {
            tv_like_count.visibility = View.INVISIBLE
        }
    }

    private fun setFavoutiteData(trendingFeedData: TrendingFeedData) {
        if (trendingFeedData?.IsYourFavorite.equals("Yes", false)) {
            img_favourite.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_red))
        } else {
            img_favourite.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_grey))
        }
    }

    fun setPostClap(action: String) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", sessionManager?.get_Authenticate_User()!!.userID)
            jsonObject.put("languageID", sessionManager?.get_Authenticate_User()!!.languageID)
           // jsonObject.put("userID", trendingFeedData?.userID)
            jsonObject.put("postID", trendingFeedData?.postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        // MyUtils.showProgressDialog(mActivity!!)
        jsonArray.put(jsonObject)
        val postLike: PostClapModel =
            ViewModelProviders.of((this@VideoDetailsFragment)).get(
                PostClapModel::class.java
            )
        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@VideoDetailsFragment,
            Observer { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos!![0].status.equals("true", false)) {
                        //  MyUtils.closeProgress()
                        if (action.equals("ClapPost", false)) {
                            trendingFeedData?.IsClappeddByYou = "Yes"
                            trendingFeedData?.userClapCount =
                                (trendingFeedData?.userClapCount?.toInt()!! + 1).toString()
                            tvFeedClapCount.text = trendingFeedData?.userClapCount
                            setClapData()

                            if (notifyInterface != null) {
                                trendingFeedData?.IsClappeddByYou = "Yes"
                                trendingFeedData?.userClapCount = trendingFeedData?.userClapCount

                                notifyInterface!!.notifyData(
                                    trendingFeedData,
                                    false,
                                    false,
                                    trendingFeedData?.postComment
                                )
                            }

                        } else if (action.equals("UnClapPost", false)) {

                            trendingFeedData?.IsClappeddByYou = "No"
                            trendingFeedData?.userClapCount =
                                (trendingFeedData?.userClapCount?.toInt()!! - 1).toString()
                            tvFeedClapCount.text = trendingFeedData?.userClapCount
                            setClapData()
                            if (notifyInterface != null) {
                                trendingFeedData?.IsClappeddByYou = "No"
                                trendingFeedData?.userClapCount = trendingFeedData?.userClapCount
                                notifyInterface?.notifyData(
                                    trendingFeedData,
                                    false,
                                    false,
                                    trendingFeedData?.postComment
                                )
                            }
                        }

                    } else {
                        //    MyUtils.closeProgress()
                        (activity as MainActivity).showSnackBar("" + postlikePojos!![0]?.message)
                    }
                }
            })
    }


    fun feedLike1(
        feedDatum: TrendingFeedData,
        s: String,
        likeImageview: LinearLayoutCompat
    ) {
        likeImageview.isEnabled = false
        var action: String = ""
        when (s) {
            "Laugh" -> {
                var count = 0
                if (feedDatum.IsLiekedByYou.equals("No", false)) {

                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        count = Integer.valueOf(trendingFeedData?.postAll!!) + 1

                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf(trendingFeedData?.postAll!!) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf(trendingFeedData?.postAll!!) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())

                if (trendingFeedData?.LikeReaction.equals(s)) {
                    if (trendingFeedData?.postHaha!!.toInt() > 0) {
                        feedDatum.postHaha = (trendingFeedData?.postHaha!!.toInt() - 1).toString()
                        feedDatum.postHaha = (trendingFeedData?.postHaha!!.toInt() + 1).toString()
                    } else {
                        feedDatum.postHaha = (trendingFeedData?.postHaha!!.toInt() + 1).toString()
                    }
                } else {
                    feedDatum.postHaha = (trendingFeedData?.postHaha!!.toInt() + 1).toString()
                }


                if (trendingFeedData?.LikeReaction.equals(
                        "Angry",
                        false
                    ) && feedDatum.postAngry.toInt() > 0
                ) {
                    feedDatum.postAngry =
                        (trendingFeedData?.postAngry!!.toInt() - 1).toString()
                }
                /*else
                {
                    feedDatum.postAngry = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals(
                        "Sad",
                        false
                    ) && feedDatum.postSad.toInt() > 0
                ) {
                    feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() - 1).toString()
                }
                /*else
                {
                    feedDatum.postSad = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals("Like") && feedDatum.postLike.toInt() > 0) {
                    feedDatum.postLike =
                        (trendingFeedData?.postLike!!.toInt() - 1).toString()
                }
                /*else {
                    feedDatum.postLike = "0"
                }*/
                action = "LikePost"
                setPostLikeData(feedDatum)
            }
            "Sad" -> {

                var count = 0
                if (feedDatum.IsLiekedByYou.equals("No", false)) {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        count = Integer.valueOf(trendingFeedData?.postAll!!) + 1

                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf(trendingFeedData?.postAll!!) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf(trendingFeedData?.postAll!!) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())
                if (trendingFeedData?.LikeReaction.equals(s)) {
                    if (trendingFeedData?.postSad!!.toInt() > 0) {
                        feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() - 1).toString()
                        feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() + 1).toString()
                    } else {
                        feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() + 1).toString()
                    }
                } else {
                    feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() + 1).toString()
                }
                if (trendingFeedData?.LikeReaction.equals(
                        "Angry",
                        false
                    ) && feedDatum.postAngry.toInt() > 0
                ) {
                    feedDatum.postAngry =
                        (trendingFeedData?.postAngry!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postAngry = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals(
                        "Laugh",
                        false
                    ) && feedDatum.postHaha.toInt() > 0
                ) {
                    feedDatum.postHaha =
                        (trendingFeedData?.postHaha!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postHaha = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals(
                        "Like",
                        false
                    ) && feedDatum.postLike.toInt() > 0
                ) {
                    feedDatum.postLike =
                        (trendingFeedData?.postLike!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postLike = "0"
                }*/


                action = "LikePost"
                setPostLikeData(feedDatum)
            }
            "Angry" -> {

                var count = 0
                if (feedDatum.IsLiekedByYou.equals("No", false)) {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        count = Integer.valueOf(trendingFeedData?.postAll!!) + 1

                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf(trendingFeedData?.postAll!!) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf(trendingFeedData?.postAll!!) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())
                if (trendingFeedData?.LikeReaction.equals(s)) {
                    if (trendingFeedData?.postAngry!!.toInt() > 0) {
                        feedDatum.postAngry = (trendingFeedData?.postAngry!!.toInt() - 1).toString()
                        feedDatum.postAngry = (trendingFeedData?.postAngry!!.toInt() + 1).toString()
                    } else {
                        feedDatum.postAngry = (trendingFeedData?.postAngry!!.toInt() + 1).toString()
                    }
                } else {
                    feedDatum.postAngry = (trendingFeedData?.postAngry!!.toInt() + 1).toString()
                }

                if (trendingFeedData?.LikeReaction.equals(
                        "Sad",
                        false
                    ) && feedDatum.postSad.toInt() > 0
                ) {
                    feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postSad = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals(
                        "Laugh",
                        false
                    ) && feedDatum.postHaha.toInt() > 0
                ) {
                    feedDatum.postHaha =
                        (trendingFeedData?.postHaha!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postHaha = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals(
                        "Like",
                        false
                    ) && feedDatum.postLike.toInt() > 0
                ) {
                    feedDatum.postLike =
                        (trendingFeedData?.postLike!!.toInt() - 1).toString()
                }/* else {
                    feedDatum.postLike = "0"
                }*/
                action = "LikePost"
                setPostLikeData(feedDatum)
            }
            "Unlike" -> {
                feedDatum.IsLiekedByYou = ("No")
                var count = 0
                try {
                    count = Integer.valueOf(trendingFeedData?.postAll!!) - 1


                } catch (e: java.lang.Exception) {
                }
                feedDatum.postAll = (count.toString())

                if (feedDatum.postLike.toInt() > 0) {
                    feedDatum.postLike =
                        (trendingFeedData?.postLike!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postSad = "0"
                }*/
                if (feedDatum.postSad.toInt() > 0) {
                    feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() - 1).toString()
                } else {
                    feedDatum.postSad = "0"
                }

                if (feedDatum.postHaha.toInt() > 0) {
                    feedDatum.postHaha =
                        (trendingFeedData?.postHaha!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postHaha = "0"
                }*/

                if (feedDatum.postAngry.toInt() > 0) {
                    feedDatum.postAngry =
                        (trendingFeedData?.postAngry!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postAngry = "0"
                }*/
                action = "UnLikePost"
                setPostLikeData(feedDatum)
            }
            "Like" -> {

                var count = 0
                if (feedDatum.IsLiekedByYou.equals("No", false)) {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        count = Integer.valueOf(trendingFeedData?.postAll!!) + 1
                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf(trendingFeedData?.postAll!!) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf(trendingFeedData?.postAll!!) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())
                if (trendingFeedData?.LikeReaction.equals(s)) {
                    if (trendingFeedData?.postLike!!.toInt() > 0) {
                        feedDatum.postLike = (trendingFeedData?.postLike!!.toInt() - 1).toString()
                        feedDatum.postLike = (trendingFeedData?.postLike!!.toInt() + 1).toString()
                    } else {
                        feedDatum.postLike = (trendingFeedData?.postLike!!.toInt() + 1).toString()
                    }
                } else {
                    feedDatum.postLike = (trendingFeedData?.postLike!!.toInt() + 1).toString()
                }
                if (trendingFeedData?.LikeReaction.equals(
                        "Sad",
                        false
                    ) && feedDatum.postSad.toInt() > 0
                ) {
                    feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postSad = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals(
                        "Laugh",
                        false
                    ) && feedDatum.postHaha.toInt() > 0
                ) {
                    feedDatum.postHaha =
                        (trendingFeedData?.postHaha!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postHaha = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals(
                        "Angry",
                        false
                    ) && feedDatum.postAngry.toInt() > 0
                ) {
                    feedDatum.postAngry =
                        (trendingFeedData?.postAngry!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postAngry = "0"
                }*/
                action = "LikePost"
                setPostLikeData(feedDatum)

            }
        }
        val json = Gson().toJson(trendingFeedData)
        val trendingFeedDatum =
            Gson().fromJson(json, TrendingFeedData::class.java)
        setPostLikeData(feedDatum.postID, action, s, trendingFeedDatum)


        likeImageview.isEnabled = true

    }

    fun setPostLikeData(
        postId: String,
        action: String,
        s: String,
        trendingFeedData1: TrendingFeedData

    ) {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postID", postId)
            jsonObject.put("posterID", trendingFeedData1?.userID)
            if (action.equals("LikePost", false)) {
                jsonObject.put("postlikeType", s)
            }
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        val postLike: PostViewModel =
            ViewModelProviders.of(this@VideoDetailsFragment).get(PostViewModel::class.java)

        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@VideoDetailsFragment,
            Observer<List<CommonPojo?>?> {
                if (mActivity != null) {
                    if (it!![0]!!.status.equals("true", false)) {

                        if (action.equals("LikePost", false)) {
                            trendingFeedData1?.IsLiekedByYou = "Yes"
                            trendingFeedData1?.LikeReaction = s

                            trendingFeedData?.IsLiekedByYou = "Yes"
                            trendingFeedData?.LikeReaction = s

                            setPostLikeData(trendingFeedData1)
                            if (notifyInterface != null) {
                                notifyInterface!!.notifyData(
                                    trendingFeedData1,
                                    false,
                                    false,
                                    trendingFeedData1?.postComment
                                )
                            }
                        } else if (action.equals("UnLikePost", false)) {
                            trendingFeedData1?.IsLiekedByYou = "No"
                            trendingFeedData1?.LikeReaction = s

                            trendingFeedData?.IsLiekedByYou = "No"
                            trendingFeedData?.LikeReaction = s
                            setPostLikeData(trendingFeedData1)
                            if (notifyInterface != null) {
                                notifyInterface!!.notifyData(
                                    trendingFeedData1,
                                    false,
                                    false,
                                    trendingFeedData1?.postComment
                                )
                            }
                        }

                    } else {

                    }
                }
            })
    }


    public fun setPostLikeData(trendingFeedData: TrendingFeedData) {

        if (!trendingFeedData?.postAll.isNullOrEmpty() && !trendingFeedData?.postAll.equals(
                "0",
                false
            )
        ) {
            tv_like_count.visibility = View.VISIBLE
            tv_like_count.text = trendingFeedData?.postAll
        } else {
            tv_like_count.visibility = View.INVISIBLE
        }

        if (trendingFeedData?.IsLiekedByYou.equals("Yes", false)) {
            tvFeedLikedUserName.visibility = View.VISIBLE
            if (trendingFeedData?.postAll!!.toInt() > 1) {
                tvFeedLikedUserName.text =
                    Html.fromHtml(sLikedby + " " + "<b>" + sYou + "</b>" + " " + Sand + " " + sMore)
            } else {
                tvFeedLikedUserName.text = Html.fromHtml(sLikedby + " " + "<b>" + sYou + "</b>")
            }

        } else {
            //  tvFeedLikedUserName.visibility=View.GONE
            if (!trendingFeedData?.LikedList.isNullOrEmpty()) {

                tvFeedLikedUserName.visibility = View.VISIBLE
                if (!trendingFeedData?.postLike.isNullOrEmpty() && trendingFeedData?.postLike!!.toInt() > 1) {

                    tvFeedLikedUserName.text =
                        Html.fromHtml("Liked by " + "<b>" + trendingFeedData?.LikedList!![0].userFirstName + " " + trendingFeedData?.LikedList!![0].userLastName + "</b>" + " and more")
                } else {
                    tvFeedLikedUserName.text =
                        Html.fromHtml("Liked by " + "<b>" + trendingFeedData?.LikedList!![0].userFirstName + " " + trendingFeedData?.LikedList!![0].userLastName + "</b>")
                }
            } else {
                tvFeedLikedUserName.visibility = View.GONE

            }
        }
        if (trendingFeedData?.IsLiekedByYou.equals("Yes", false)) {
//                holder1.ll_post_like.setOnTouchListener(null)
            when (trendingFeedData?.LikeReaction) {
                "Like" -> {
                    img_like.setImageResource(R.drawable.like_filled_small_white)
                }
                "Sad" -> {
                    img_like.setImageResource(R.drawable.worried_reaction_icon_small)
                }
                "Angry" -> {
                    img_like.setImageResource(R.drawable.angry_reaction_icon_small)
                }
                "Laugh" -> {
                    img_like.setImageResource(R.drawable.laughing_reaction_icon_small)

                }
            }
        } else {
            img_like.setImageResource(R.drawable.reaction_like_icon_small_white)
        }
    }

    fun feedFavorite(
        favouriteimageview: AppCompatImageView

    ) {
        favouriteimageview.isEnabled = false
        val action: String
        if (trendingFeedData?.IsYourFavorite.equals("No", false)) {
            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_red))
            var animation = AnimationUtils.loadAnimation(mActivity, R.anim.bounce)
            favouriteimageview.startAnimation(animation)
            action = "FavouritePost"
            trendingFeedData?.IsYourFavorite = ("Yes")
        } else {
            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_grey))
            action = "UnFavouritePost"
            trendingFeedData?.IsYourFavorite = ("No")
        }
        val json = Gson().toJson(trendingFeedData)
        val trendingFeedDatum =
            Gson().fromJson(json, TrendingFeedData::class.java)
        setPostFavourite(
            trendingFeedData?.postID!!,
            action, trendingFeedDatum
        )
        favouriteimageview.isEnabled = true
    }


    fun setPostFavourite(
        postId: String,
        action: String,
        trendingFeedDatum: TrendingFeedData
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postID", postId)
            jsonObject.put("posterID", trendingFeedDatum.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        val postLike: PostFavouriteModel =
            ViewModelProviders.of((this@VideoDetailsFragment)).get(
                PostFavouriteModel::class.java
            )
        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@VideoDetailsFragment,
            Observer { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos?.get(0)?.status.equals("true", false)) {
                        setFavoutiteData(trendingFeedDatum)
                    } else {
                        img_favourite.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_grey))
                        trendingFeedDatum?.IsYourFavorite = ("No")
                        setFavoutiteData(trendingFeedDatum)
                    }

                }
            })
    }

    private fun postData(
        feedDatum: TrendingFeedData, text: String?
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            /*if (Integer.valueOf(feedDatum.pos()) > 0) {
                jsonObject.put("postID", feedDatum.getOriginalPostID())
            } else {*/
            jsonObject.put("postID", feedDatum.postID)
            //}
            if(sessionManager?.isLoggedIn()!!){
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put("languageID", userData?.languageID)

            }else{
                jsonObject.put("loginuserID", "0")
                jsonObject.put("languageID",sessionManager?.getsetSelectedLanguage())

            }
            jsonObject.put("posterID", feedDatum.userID)
            jsonObject.put("postshareWhere", "Gmail")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        Log.d("json", "Array = $jsonArray")

        val call: Call<List<SharePostPojo>> =
            RestClient.get()?.getSharePost(jsonArray.toString())!!
        MyUtils.showProgress(mActivity!!)
        call.enqueue(object : RestCallback<List<SharePostPojo>?>(mActivity!!) {
            override fun Success(response: Response<List<SharePostPojo>?>) {
                MyUtils.closeProgress()
                if (mContext != null) {
                    if (response.body()!![0].status.equals("true")) {
                        var count = 0
                        try {
                            count = Integer.valueOf(feedDatum.postShared) + 1
                        } catch (e: java.lang.Exception) {
                        }
                        feedDatum.postShared = (count.toString())
                        feedDatum.youpostShared = ("Yes")
                        setData()

                        val shareIntent = Intent()
                        shareIntent.action = Intent.ACTION_SEND
                        shareIntent.setType("text/plain")
                        shareIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            "Hey check out news on Camfire" + "\n\nhttps://play.google.com/store/apps/details?id=${mActivity!!.packageName}"
                        )
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Camfire")
                        startActivity(Intent.createChooser(shareIntent, "Share post via"))

                    } else {
                        (activity as MainActivity).showSnackBar(response.body()!![0].message!!)
                    }
                }
            }

            override fun failure() {
                if (mActivity != null) {
                    MyUtils.closeProgress()
                    (activity as MainActivity).errorMethod()
                }
            }

        })
    }

    private fun getUserFollow(s: String, userID: String) {
        MyUtils.showProgressDialog(mActivity!!)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", sessionManager?.get_Authenticate_User()!!.userID)
            jsonObject.put("languageID", sessionManager?.get_Authenticate_User()!!.languageID)
            jsonObject.put("userID", userID)
            jsonObject.put("action", s)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var getFollowModel =
            ViewModelProviders.of(this@VideoDetailsFragment).get(FollowModel::class.java)
        getFollowModel.apiCall(mActivity!!, jsonArray.toString(), s)
            .observe(this@VideoDetailsFragment,
                Observer { trendingFeedDatum ->
                    if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                        MyUtils.closeProgress()
                        if (trendingFeedDatum[0].status?.equals("true")!!) {
                            when (s) {
                                "follow" -> {
                                    trendingFeedData?.isYouFollowing = "Yes"
                                    setFollowData()
                                    if (notifyInterface != null) {
                                        trendingFeedData?.isYouFollowing = "Yes"
                                        notifyInterface!!.notifyData(
                                            trendingFeedData,
                                            false,
                                            false,
                                            trendingFeedData?.postComment
                                        )
                                    }
                                }
                                "unfollow" -> {
                                    trendingFeedData?.isYouFollowing = "No"
                                    setFollowData()
                                    if (notifyInterface != null) {
                                        trendingFeedData?.isYouFollowing = "No"
                                        notifyInterface!!.notifyData(
                                            trendingFeedData,
                                            false,
                                            false,
                                            trendingFeedData?.postComment
                                        )
                                    }
                                }

                            }

                        } else {
                            (activity as MainActivity).showSnackBar(trendingFeedDatum[0].message!!)
                        }

                    } else {
                        if (activity != null) {
                            MyUtils.closeProgress()
                            (activity as MainActivity).errorMethod()

                        }
                    }
                })


    }

    fun playVideo(

        playerView: PlayerView,
        thumnail: ImageView,
        volume: View?,
        progressBar: ProgressBar,
        playIcon: ImageView,
        feedList: TrendingFeedData
    ) {

        mActivity!!.runOnUiThread {
            val mHandler = Handler(Looper.getMainLooper())
            mHandler.post(object : Runnable {
                override fun run() {
                    var videoURI: Uri? = null
                    if (!trendingFeedData?.userSignatureVideo.isNullOrEmpty()) {

                        if (trendingFeedData?.postSignatureVideo.equals("Yes", false)) {
                            videoURI = Uri.parse(
                                RestClient.image_base_url_signutureVideo + trendingFeedData?.postSerializedData!!.get(
                                    0
                                ).albummedia[0].albummediaFile
                            )
                        } else {
                            videoURI = Uri.parse(
                                RestClient.image_base_url_post + trendingFeedData?.postSerializedData!!.get(
                                    0
                                ).albummedia[0].albummediaFile
                            )
                        }
                    } else {
                        videoURI = Uri.parse(
                            RestClient.image_base_url_post + trendingFeedData?.postSerializedData!!.get(
                                0
                            ).albummedia[0].albummediaFile
                        )
                    }

                    //  Log.e("url",RestClient.image_base_url_post+ feedList?.get(videoPlayPosition)!!.postSerializedData.get(0).albummedia[0].albummediaFile)
                    val bandwidthMeter: BandwidthMeter? = null
                    val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
                    val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)



                    exoPlayer = ExoPlayerFactory.newSimpleInstance(mActivity!!, trackSelector)


                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

                    val cacheDataSourceFactory =
                        CacheDataSourceFactory1(mActivity!!, 5 * 1024 * 1024)

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
                        trendingFeedData?.postSerializedData!!.get(
                            0
                        ).albummedia.get(0).duration
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
                                    if (thumnail.visibility == View.VISIBLE)
                                        thumnail.visibility = View.GONE

                                    progressBar.visibility = View.GONE
                                    if (playIcon.visibility == View.VISIBLE)
                                        playIcon.visibility = View.GONE


                                }
                                else -> {
                                }
                            }
                        }

                        override fun onRepeatModeChanged(repeatMode: Int) {

                        }

                        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

                        }

                        override fun onPlayerError(error: ExoPlaybackException?) {
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

    fun stopPlayer() {
        /* for(i in 0 until feedList.size)
                           {
                               if(feedList.get(i)!=null&&feedList.get(i).postSerializedData!![0]!!.albummedia!=null&& feedList.get(
                                       i
                                   ).postSerializedData!![0]!!.albummedia.isNotEmpty()
                               )
                                   feedList.get(i).postSerializedData!![0]!!.albummedia[0].isPlaying=(false);
                           }*/
        if (exoPlayer != null) {
            exoPlayer?.stop(true)
            exoPlayer?.release()
            exoPlayer = null
        }


    }

    fun pausePlayer() {
        try {
            mActivity!!.runOnUiThread(Runnable {


                if (trendingFeedData != null)
                    if (exoPlayer != null)
                        trendingFeedData!!.postSerializedData[0].albummedia[0].duration =
                            (
                                    exoPlayer?.currentPosition!!
                                    )
                trendingFeedData?.postSerializedData!![0].albummedia[0]
                    .isPlaying = (false)
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


    override fun onStop() {
        super.onStop()
        stopPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayer()
    }


    fun setFollowData() {
        if (trendingFeedData?.isYouFollowing.equals("Yes", false)) {
            btnFollowing.visibility = View.VISIBLE
            btnFollow.visibility = View.GONE
        } else {
            btnFollowing.visibility = View.GONE
            btnFollow.visibility = View.VISIBLE
        }
    }

    fun setClapData() {
        if (trendingFeedData?.IsClappeddByYou.equals("Yes", false)) {
            clap_feed_post.setImageResource(R.drawable.clap_icon_filled_enabled)
        } else {
            clap_feed_post.setImageResource(R.drawable.clap_icon_border_disabled)
        }
    }


    fun setPostClap(
        action: String,
        pos: Int
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", sessionManager?.get_Authenticate_User()!!.userID)
            jsonObject.put("languageID", sessionManager?.get_Authenticate_User()!!.languageID)
            jsonObject.put("userID", trendingFeedData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        MyUtils.showProgressDialog(mActivity!!)
        jsonArray.put(jsonObject)
        val postLike: PostViewModel =
            ViewModelProviders.of((this@VideoDetailsFragment)).get(
                PostViewModel::class.java
            )
        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@VideoDetailsFragment,
            Observer { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos!![0].status.equals("true", false)) {
                        MyUtils.closeProgress()
                        if (action.equals("ClapPost", false)) {
                            trendingFeedData?.IsClappeddByYou = "Yes"
                            trendingFeedData?.userClapCount =
                                (trendingFeedData?.userClapCount?.toInt()!! + 1).toString()
                            tvFeedClapCount.text = trendingFeedData?.userClapCount
                            setClapData()

                            if (notifyInterface != null) {
                                trendingFeedData?.IsClappeddByYou = "Yes"
                                trendingFeedData?.userClapCount = trendingFeedData?.userClapCount

                                notifyInterface!!.notifyData(
                                    trendingFeedData,
                                    false,
                                    false,
                                    trendingFeedData?.postComment
                                )
                            }

                        } else if (action.equals("UnClapPost", false)) {

                            trendingFeedData?.IsClappeddByYou = "No"
                            trendingFeedData?.userClapCount =
                                (trendingFeedData?.userClapCount?.toInt()!! - 1).toString()
                            tvFeedClapCount.text = trendingFeedData?.userClapCount
                            setClapData()
                            if (notifyInterface != null) {
                                trendingFeedData?.IsClappeddByYou = "No"
                                trendingFeedData?.userClapCount = trendingFeedData?.userClapCount
                                notifyInterface?.notifyData(
                                    trendingFeedData,
                                    false,
                                    false,
                                    trendingFeedData?.postComment
                                )
                            }
                        }

                    } else {
                        MyUtils.closeProgress()
                        (activity as MainActivity).showSnackBar("" + postlikePojos!![0]?.message)
                    }
                }
            })
    }

    public fun notifyData(
        feedDatum: TrendingFeedData,
        isDelete: Boolean,
        isDeleteComment: Boolean
    ) {


        if (trendingFeedData != null && trendingFeedData?.postID.equals(
                feedDatum.postID,
                false
            )
        ) {
            if (isDeleteComment) {
                setCommentCount(feedDatum.postComment.toInt())

            } else {
                setCommentCount(feedDatum.postComment.toInt())

            }
        }


    }


    public fun setCommentCount(postComment: Int) {
        if (postComment > 0) {
            trendingFeedData?.postComment = postComment.toString()
            tv_comment_count?.visibility = View.VISIBLE
            tv_comment_count?.text = postComment.toString()
        } else {
            tv_comment_count?.visibility = View.INVISIBLE
        }

    }


}
