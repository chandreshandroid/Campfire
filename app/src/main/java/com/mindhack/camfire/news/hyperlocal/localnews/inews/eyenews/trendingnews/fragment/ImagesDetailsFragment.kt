package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.ReactionPopup.PopupGravity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.ReactionPopup.ReactionPopup
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.ReactionPopup.ReactionsConfigBuilder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.Service.NotifyInterface
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.LoginActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.PhotogalleryAdapterr
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.PostDesTextView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.gson.Gson
import com.plattysoft.leonids.ParticleSystem
import com.plattysoft.leonids.modifiers.ScaleModifier
import kotlinx.android.synthetic.main.fragment_images_details.*
import kotlinx.android.synthetic.main.fragment_images_details.btnFollow
import kotlinx.android.synthetic.main.fragment_images_details.btnFollowing
import kotlinx.android.synthetic.main.fragment_images_details.clap_feed_post
import kotlinx.android.synthetic.main.fragment_images_details.clap_feed_post_big
import kotlinx.android.synthetic.main.fragment_images_details.image_round
import kotlinx.android.synthetic.main.fragment_images_details.imgCloseIcon
import kotlinx.android.synthetic.main.fragment_images_details.img_favourite
import kotlinx.android.synthetic.main.fragment_images_details.img_like
import kotlinx.android.synthetic.main.fragment_images_details.img_user_verified
import kotlinx.android.synthetic.main.fragment_images_details.imv_user_dp
import kotlinx.android.synthetic.main.fragment_images_details.layoutDotsTutorial
import kotlinx.android.synthetic.main.fragment_images_details.linearlayoutheader
import kotlinx.android.synthetic.main.fragment_images_details.ll_favourite
import kotlinx.android.synthetic.main.fragment_images_details.ll_feed_comment
import kotlinx.android.synthetic.main.fragment_images_details.ll_feed_share
import kotlinx.android.synthetic.main.fragment_images_details.ll_post_like
import kotlinx.android.synthetic.main.fragment_images_details.tvFeedClapCount
import kotlinx.android.synthetic.main.fragment_images_details.tvFeedDescription
import kotlinx.android.synthetic.main.fragment_images_details.tvFeedDescriptionFullView
import kotlinx.android.synthetic.main.fragment_images_details.tvFeedLikedUserName
import kotlinx.android.synthetic.main.fragment_images_details.tvFeedRepost
import kotlinx.android.synthetic.main.fragment_images_details.tvFeedTimeLocation
import kotlinx.android.synthetic.main.fragment_images_details.tvFeedUserId
import kotlinx.android.synthetic.main.fragment_images_details.tvFeedUserName
import kotlinx.android.synthetic.main.fragment_images_details.tv_like_count
import kotlinx.android.synthetic.main.fragment_images_details.tv_share_count
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response


class ImagesDetailsFragment : Fragment() {
    var mActivity: Activity? = null
    var trendingFeedData: TrendingFeedData? = null
    var v: View? = null
    var sessionManager: SessionManager? = null

    var notifyInterface: NotifyInterface? = null
    var page_position = 0
    private var dots: Array<ImageView?>? = null
    private var dotsCount: Int = 0
    //private val strings = arrayOf("Laugh", "Angry", "Sad", "Like")
    private val strings = arrayOf( "Angry", "Sad","Laugh", "Like")
    var Sand :String=""
    var sMore :String=""
    var sLikedby: String=""
    var sYou: String=""

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
    var userData: RegisterPojo.Data? = null
    var tv_comment_count: AppCompatTextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_images_details, container, false)
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

        if (sessionManager?.isLoggedIn()!!) {
            if (sessionManager?.get_Authenticate_User() != null) {
                userData = sessionManager?.get_Authenticate_User()
            }
        }

//        (activity as MainActivity).showHideBottomNavigation(false)
        if (arguments != null) {
            if (arguments?.getString("postId") != null){
                getPostList()
            }else trendingFeedData = arguments?.getSerializable("feedData") as TrendingFeedData?
        }

        tv_comment_count = v?.findViewById(R.id.tv_comment_count1)

        if (trendingFeedData != null) {
             Sand = GetDynamicStringDictionaryObjectClass?.and
             sMore = GetDynamicStringDictionaryObjectClass?.more
             sLikedby = GetDynamicStringDictionaryObjectClass?.Liked_By
             sYou = GetDynamicStringDictionaryObjectClass?.you_
            setData()
        }

        imgCloseIcon.setOnClickListener {
            (activity as MainActivity).onBackPressed()
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
                        uName = trendingFeedData!!.userFirstName + " " + trendingFeedData!!.userLastName
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
            }        }
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

        viewpager.adapter =
            PhotogalleryAdapterr(mActivity!!, trendingFeedData?.postSerializedData!![0].albummedia)
        if (! trendingFeedData?.postSerializedData!![0].albummedia.isNullOrEmpty() &&  trendingFeedData?.postSerializedData!![0].albummedia?.size!! > 1) {
            addBottomDots(0, layoutDotsTutorial)
            layoutDotsTutorial.visibility = View.VISIBLE
        } else {
            layoutDotsTutorial.visibility = View.GONE
        }

        viewpager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                page_position = position

            }

            override fun onPageSelected(position: Int) {
                page_position = position
                addBottomDots(position, layoutDotsTutorial!!)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        if (!trendingFeedData?.userProfilePicture.isNullOrEmpty()) imv_user_dp.setImageURI(RestClient.image_base_url_users + trendingFeedData?.userProfilePicture)

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
                tvFeedLikedUserName.text =  Html.fromHtml( sLikedby +" "+ "<b>"+sYou + "</b>"+" "+Sand + " "+sMore)
            } else {
                tvFeedLikedUserName.text =  Html.fromHtml( sLikedby +" "+"<b>"+sYou+ "</b>")
            }

        } else {
           //tvFeedLikedUserName.visibility = View.GONE
            if (!trendingFeedData?.LikedList.isNullOrEmpty()) {

                tvFeedLikedUserName.visibility = View.VISIBLE
                if (!trendingFeedData?.postLike.isNullOrEmpty() && trendingFeedData?.postLike!!.toInt() > 1) {

                    tvFeedLikedUserName.text =
                        Html.fromHtml( "Liked by "+"<b>"+trendingFeedData?.LikedList!![0].userFirstName+" "+trendingFeedData?.LikedList!![0].userLastName+"</b>"+" and more")
                } else {
                    tvFeedLikedUserName.text =
                        Html.fromHtml("Liked by "+"<b>"+trendingFeedData?.LikedList!![0].userFirstName+" "+trendingFeedData?.LikedList!![0].userLastName+"</b>")
                }
            } else {
                tvFeedLikedUserName.visibility = View.GONE

            }
        }
        /*if(trendingFeedData?.IsLiekedByYou.equals("Yes",false))
        {
            tvFeedLikedUserName.visibility=View.VISIBLE
            if(!trendingFeedData?.LikedList.isNullOrEmpty()) {
                if(trendingFeedData?.LikedList!!.size>1) {
                tvFeedLikedUserName.text =
                        "Liked by ${userData?.userFirstName + " " + userData?.userLastName} and more"
                }else{
                  tvFeedLikedUserName.text =
                        "Liked by ${userData?.userFirstName + " " + userData?.userLastName}"

                }
            }else{
               tvFeedLikedUserName.text =
                    "Liked by ${userData?.userFirstName + " " + userData?.userLastName}"

            }
        }
        else
        {
            if(!trendingFeedData?.LikedList.isNullOrEmpty())
            {
              tvFeedLikedUserName.visibility=View.VISIBLE

                tvFeedLikedUserName.text="Liked by ${trendingFeedData?.LikedList!![0].userFirstName+" "+trendingFeedData?.LikedList!![0].userLastName} and more"

            }else{
               tvFeedLikedUserName.visibility=View.GONE

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
        if (trendingFeedData.IsYourFavorite.equals("Yes", false)) {
            img_favourite.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_red))
        } else {
            img_favourite.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_grey))
        }
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

        }
        catch
       (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var getFollowModel =
            ViewModelProviders.of(this@ImagesDetailsFragment).get(FollowModel::class.java)
        getFollowModel.apiCall(mActivity!!, jsonArray.toString(), s)
            .observe(this@ImagesDetailsFragment,
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
            ViewModelProviders.of((this@ImagesDetailsFragment)).get(
                PostClapModel::class.java
            )
        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@ImagesDetailsFragment,
            Observer { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos!![0].status.equals("true", false)) {
                        //MyUtils.closeProgress()
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
                            if (notifyInterface != null)
                            {
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
                       // MyUtils.closeProgress()
                        (activity as MainActivity).showSnackBar("" + postlikePojos[0].message)
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

                if(trendingFeedData?.LikeReaction.equals(s))
                {
                    if(trendingFeedData?.postHaha!!.toInt()>0)
                    {
                        feedDatum.postHaha = (trendingFeedData?.postHaha!!.toInt() - 1).toString()
                        feedDatum.postHaha = (trendingFeedData?.postHaha!!.toInt() + 1).toString()
                    }
                    else
                    {
                        feedDatum.postHaha = (trendingFeedData?.postHaha!!.toInt() + 1).toString()
                    }
                }
                else
                {
                    feedDatum.postHaha = (trendingFeedData?.postHaha!!.toInt() + 1).toString()
                }


                if (trendingFeedData?.LikeReaction.equals("Angry",false)&&feedDatum.postAngry.toInt() > 0)
                {
                    feedDatum.postAngry =
                        (trendingFeedData?.postAngry!!.toInt() - 1).toString()
                }
                /*else
                {
                    feedDatum.postAngry = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals("Sad",false)&&feedDatum.postSad.toInt() > 0)
                {
                    feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() - 1).toString()
                }
                /*else
                {
                    feedDatum.postSad = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals("Like")&&feedDatum.postLike.toInt() > 0)
                {
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
                if(trendingFeedData?.LikeReaction.equals(s))
                {
                    if(trendingFeedData?.postSad!!.toInt()>0)
                    {
                        feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() - 1).toString()
                        feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() + 1).toString()
                    }
                    else
                    {
                        feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() + 1).toString()
                    }
                }
                else
                {
                    feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() + 1).toString()
                }
                if (trendingFeedData?.LikeReaction.equals("Angry",false)&& feedDatum.postAngry.toInt() > 0) {
                    feedDatum.postAngry =
                        (trendingFeedData?.postAngry!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postAngry = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals("Laugh",false)&&feedDatum.postHaha.toInt() > 0) {
                    feedDatum.postHaha =
                        (trendingFeedData?.postHaha!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postHaha = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals("Like",false)&&feedDatum.postLike.toInt() > 0) {
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
                if(trendingFeedData?.LikeReaction.equals(s))
                {
                    if(trendingFeedData?.postAngry!!.toInt()>0)
                    {
                        feedDatum.postAngry = (trendingFeedData?.postAngry!!.toInt() - 1).toString()
                        feedDatum.postAngry = (trendingFeedData?.postAngry!!.toInt() + 1).toString()
                    }
                    else
                    {
                        feedDatum.postAngry = (trendingFeedData?.postAngry!!.toInt() + 1).toString()
                    }
                }
                else
                {
                    feedDatum.postAngry = (trendingFeedData?.postAngry!!.toInt() + 1).toString()
                }

                if (trendingFeedData?.LikeReaction.equals("Sad",false)&&feedDatum.postSad.toInt() > 0) {
                    feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postSad = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals("Laugh",false)&&feedDatum.postHaha.toInt() > 0) {
                    feedDatum.postHaha =
                        (trendingFeedData?.postHaha!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postHaha = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals("Like",false)&&feedDatum.postLike.toInt() > 0) {
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
                if(trendingFeedData?.LikeReaction.equals(s))
                {
                    if(trendingFeedData?.postLike!!.toInt()>0)
                    {
                        feedDatum.postLike = (trendingFeedData?.postLike!!.toInt() - 1).toString()
                        feedDatum.postLike = (trendingFeedData?.postLike!!.toInt() + 1).toString()
                    }
                    else
                    {
                        feedDatum.postLike = (trendingFeedData?.postLike!!.toInt() + 1).toString()
                    }
                }
                else
                {
                    feedDatum.postLike = (trendingFeedData?.postLike!!.toInt() + 1).toString()
                }
                if (trendingFeedData?.LikeReaction.equals("Sad",false)&&feedDatum.postSad.toInt() > 0) {
                    feedDatum.postSad = (trendingFeedData?.postSad!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postSad = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals("Laugh",false)&&feedDatum.postHaha.toInt() > 0) {
                    feedDatum.postHaha =
                        (trendingFeedData?.postHaha!!.toInt() - 1).toString()
                } /*else {
                    feedDatum.postHaha = "0"
                }*/

                if (trendingFeedData?.LikeReaction.equals("Angry",false)&&feedDatum.postAngry.toInt() > 0) {
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
           // jsonObject.put("postID", postId)
            jsonObject.put("posterID", trendingFeedData1.userID)
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
            ViewModelProviders.of(this@ImagesDetailsFragment).get(PostViewModel::class.java)

        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@ImagesDetailsFragment,
            Observer<List<CommonPojo?>?> {
                if (mActivity != null) {
                    if (it!![0]!!.status.equals("true", false)) {

                        if (action.equals("LikePost", false)) {
                            trendingFeedData1.IsLiekedByYou = "Yes"
                            trendingFeedData1.LikeReaction = s
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
                            trendingFeedData1.IsLiekedByYou = "No"
                            trendingFeedData1.LikeReaction = s

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


    fun setPostLikeData(trendingFeedData: TrendingFeedData) {

        if (!trendingFeedData.postAll.isNullOrEmpty() && !trendingFeedData.postAll.equals(
                "0",
                false
            )
        ) {
            tv_like_count.visibility = View.VISIBLE
            tv_like_count.text = trendingFeedData.postAll
        } else {
            tv_like_count.visibility = View.INVISIBLE
        }

        if (trendingFeedData?.IsLiekedByYou.equals("Yes", false)) {
            tvFeedLikedUserName.visibility = View.VISIBLE
            if(trendingFeedData?.postAll!!.toInt()>1)
            {
                tvFeedLikedUserName.text = Html.fromHtml( sLikedby +" "+"<b>"+sYou+"</b>" + " "+Sand + " "+sMore)
            }
            else
            {
                tvFeedLikedUserName.text =  Html.fromHtml( sLikedby +" "+"<b>"+sYou+"</b>")
            }

        } else {
            tvFeedLikedUserName.visibility = View.GONE
            /* if(!feedList[position]!!.LikedList.isNullOrEmpty())
             {

                 holder1.tvFeedLikedUserName.visibility=View.VISIBLE
                 if(feedList[position]!!.LikedList.size>1) {

                     holder1.tvFeedLikedUserName.text =
                         "Liked by ${feedList[position]!!.LikedList[0].userFirstName + " " + feedList[position]!!.LikedList[0].userLastName} and more"
                 }else
                 {
                     holder1.tvFeedLikedUserName.text =
                         "Liked by ${feedList[position]!!.LikedList[0].userFirstName + " " + feedList[position]!!.LikedList[0].userLastName}"
                 }
             }else{
                 holder1.tvFeedLikedUserName.visibility=View.GONE

             }*/
        }
        if (trendingFeedData.IsLiekedByYou.equals("Yes", false)) {
//                holder1.ll_post_like.setOnTouchListener(null)
            when (trendingFeedData.LikeReaction) {
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
            ViewModelProviders.of((this@ImagesDetailsFragment)).get(
                PostFavouriteModel::class.java
            )
        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@ImagesDetailsFragment,
            Observer { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos?.get(0)?.status.equals("true", false)) {
                        setFavoutiteData(trendingFeedDatum)
                    } else {
                        img_favourite.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_grey))
                        trendingFeedDatum.IsYourFavorite = ("No")
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
                        shareIntent.type = "text/plain"
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

    fun notifyData(feedDatum: TrendingFeedData, isDelete: Boolean,isDeleteComment:Boolean) {

        if (trendingFeedData != null && trendingFeedData?.postID.equals(
                feedDatum.postID,
                false
            )
        ) {
                trendingFeedData = feedDatum
                if(isDeleteComment)
                {
                    setCommentCount(feedDatum.postComment.toInt())

                }
                else
                {
                    setCommentCount(feedDatum.postComment.toInt())

                }
        }
    }

    public fun setCommentCount(postComment: Int) {
        if(postComment>0)
        {
            trendingFeedData?.postComment = postComment.toString()
            tv_comment_count?.visibility = View.VISIBLE
            tv_comment_count?.text = postComment.toString()
        }else{
            tv_comment_count?.visibility = View.INVISIBLE
        }
    }


    fun getPostList() {
        setLayoutAsPerCondition(false, true, false, false, false)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            if (sessionManager?.isLoggedIn()!!) {
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put("postLatitude", MyUtils.currentLattitude)
                jsonObject.put("postLongitude", MyUtils.currentLongitude)
            } else {
                jsonObject.put("loginuserID", "0")
                jsonObject.put("postLatitude", MyUtils.currentLattitude)
                jsonObject.put("postLongitude", MyUtils.currentLongitude)
            }

            jsonObject.put("languageID", "" + sessionManager?.getsetSelectedLanguage())
            jsonObject.put("userID", "0")
            jsonObject.put("postID", if (arguments?.getString("postId") != null)
                arguments?.getString("postId") else "0")
            jsonObject.put("page", "0")
            jsonObject.put("sortingwith", "Nearest")

            jsonObject.put("pagesize", "10")
            jsonObject.put("searchWord", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        val getPostListModel =
            ViewModelProviders.of(mActivity!! as MainActivity).get(GetPostListModel::class.java)
        getPostListModel?.apiFunction(mActivity!!, false, jsonArray.toString(), "postList")
            ?.observe(this@ImagesDetailsFragment,
                Observer<List<TrendingFeedDatum>>
                { trendingFeedDatum ->
                    if (!trendingFeedDatum.isNullOrEmpty()) {
                        if (trendingFeedDatum[0].status.equals("true")) {
                            setLayoutAsPerCondition(false, false, true, false, false)

                            trendingFeedData = trendingFeedDatum[0].data[0]
                            setData()
                        } else {
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                setLayoutAsPerCondition(true, false, false, false, false)

                            }else{
                                setLayoutAsPerCondition(false, false, false, true, false)
                            }
                        }

                    } else {
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            setLayoutAsPerCondition(false, false, false, false, true)
                        }else{
                            setLayoutAsPerCondition(false, false, false, true, false)
                        }
                    }
                })
    }


    fun setLayoutAsPerCondition(
        noData: Boolean,
        progress: Boolean,
        dataFound: Boolean,
        noInternet: Boolean,
        somthingRong: Boolean
    ) {

        if (noData) {
//            progressView?.hide()
            imageDetailLayoutData?.visibility = View.GONE

            noDatafoundRelativelayout?.visibility = View.VISIBLE
            nodata1?.visibility = View.VISIBLE
            nodata1?.text = mActivity!!.resources.getString(R.string.no_address_found)
            nodatafoundtextview?.visibility = View.GONE

            relativeprogressBar?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.GONE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview1?.visibility = View.VISIBLE
        } else if (progress) {
//            progressView?.show()
            imageDetailLayoutData?.visibility = View.GONE

            noDatafoundRelativelayout?.visibility = View.GONE
            nodata1?.visibility = View.VISIBLE
            nodatafoundtextview?.visibility = View.GONE

            relativeprogressBar?.visibility = View.VISIBLE

            nointernetMainRelativelayout?.visibility = View.GONE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview1?.visibility = View.VISIBLE
        } else if (dataFound) {
//            progressView?.hide()
            imageDetailLayoutData?.visibility = View.VISIBLE

            noDatafoundRelativelayout?.visibility = View.GONE
            nodata1?.visibility = View.VISIBLE
            nodatafoundtextview?.visibility = View.GONE

            relativeprogressBar?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.GONE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview1?.visibility = View.VISIBLE
        } else if (noInternet) {
//            progressView?.hide()
            imageDetailLayoutData?.visibility = View.GONE

            noDatafoundRelativelayout?.visibility = View.GONE
            nodata1?.visibility = View.VISIBLE
            nodatafoundtextview?.visibility = View.GONE

            relativeprogressBar?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.VISIBLE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview1?.visibility = View.VISIBLE
            nointernettextview1?.text =
                mActivity!!.resources.getString(R.string.error_common_network)
            nointernettextview1?.visibility = View.VISIBLE
        } else if (somthingRong) {
//            progressView?.hide()
            imageDetailLayoutData?.visibility = View.GONE

            noDatafoundRelativelayout?.visibility = View.GONE
            nodata1?.visibility = View.VISIBLE
            nodatafoundtextview?.visibility = View.GONE

            relativeprogressBar?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.VISIBLE
            nointernetImageview?.visibility = View.GONE
            nointernettextview1?.visibility = View.VISIBLE
            nointernettextview1?.text =
                mActivity!!.resources.getString(R.string.error_crash_error_message)
            nointernettextview1?.visibility = View.GONE
        }
    }

    fun addBottomDots(currentPage: Int, layoutDotsTutorial: LinearLayout) {
        // dotsCount = stichingServicData?.stitchingserviceImages!!.size
        //  dotsCount=trendingFeedDatum1?.postSerializedData?.get(0)?.albummedia!!.size
        dotsCount = if ( trendingFeedData?.postSerializedData!![0].albummedia.isNullOrEmpty()) 0 else  trendingFeedData?.postSerializedData!![0].albummedia?.size!!

        layoutDotsTutorial.removeAllViews()

        dots = arrayOfNulls(dotsCount)

        for (i in 0 until dotsCount) {

            dots!![i] = ImageView(mActivity!!)
            dots!![i]?.setImageResource(R.drawable.carousel_dot_unselected)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            dots!![i]?.setPadding(0, 0, 0, 0)
            dots!![i]?.layoutParams = params
            layoutDotsTutorial.addView(dots!![i]!!, params)
            layoutDotsTutorial.bringToFront()

        }
        if (dots!!.isNotEmpty() && dots!!.size > currentPage) {
            dots!![currentPage]?.setImageResource(R.drawable.carousel_dot_selected)
        }

    }

}
