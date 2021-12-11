package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.LoginActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.VideoActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.FeedListAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.PostReportListAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.header_icon_text.*
import kotlinx.android.synthetic.main.header_icon_text.rootHeaderLayout
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.transparent_toolbar_layout.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class TrendingFeedFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: RegisterPojo.Data? = null
    var feedListAdapter: FeedListAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var linearLayoutManager1: LinearLayoutManager? = null
    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var feedList: ArrayList<TrendingFeedData?>? = null
    var pageNo = 0
    var getPostListModel: GetPostListModel? = null
    var postReportListAdapter: PostReportListAdapter? = null

    private var mIsAnimatingOut = false
    var isLastPage1 = false
    var reportReasonData: ArrayList<ReasonList.Data>? = ArrayList()


    private val mYourBroadcastReceiverResumeFilterFinish = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("Successfully")) {
                val commentAction = if (intent.hasExtra("CommentAction")) intent.getIntExtra(
                    "CommentAction",
                    -1
                ) else -1
                val postID = if (intent.hasExtra("postId")) intent.getStringExtra("postId") else ""
                for (i in 0 until feedList!!.size) {
                    if (feedList!![i]?.postID.equals(postID)) {
                        var oldCommentCount =
                            if (feedList!![i]?.postComment.isNullOrEmpty()) 0 else java.lang.Integer.valueOf(
                                feedList!![i]?.postComment!!
                            )
                        when (commentAction) {
                            0 -> oldCommentCount += 1
                            1 -> oldCommentCount -= 1
                            2 -> oldCommentCount -= 1
                        }
                        feedList!![i]?.postComment = oldCommentCount.toString()
                    }
                }
            }
            feedListAdapter?.notifyDataSetChanged()
            LocalBroadcastManager.getInstance(mActivity!!).unregisterReceiver(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_trending_feed, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            mActivity = context
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager!!.isLoggedIn()) userData =
            sessionManager?.get_Authenticate_User()

//        (activity as MainActivity).showHideBottomNavigation(true)
//        (activity as MainActivity).updateNavigationBarState(R.id.menu_trending)


        if (MyUtils.locationCityName.isNullOrEmpty()) {
            val currentapiVersion = Build.VERSION.SDK_INT
            if (currentapiVersion >= Build.VERSION_CODES.M) {
                (activity as MainActivity).permissionLocation()
            }
        }

        imgCloseIcon.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }


        tvHeaderText.text = resources.getString(R.string.trending_post)
        noDatafoundRelativelayout.visibility = View.GONE
        relativeprogressBar.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        getPostListModel =
            ViewModelProviders.of(this@TrendingFeedFragment).get(GetPostListModel::class.java)

        linearLayoutManager = LinearLayoutManager(mActivity!!)

        LocalBroadcastManager.getInstance(mActivity!!)
            .registerReceiver(mYourBroadcastReceiverResumeFilterFinish, IntentFilter("Comment"))

        val width = rvMainFeed.width
        val height = MyUtils.getViewHeight(rvMainFeed)

        var params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, height, 0, 0)

        (activity as MainActivity).coordinatorLocation.layoutParams = params



        if (feedList == null) {
            feedList = ArrayList()
            feedListAdapter = FeedListAdapter(
                mActivity!!,
                object : FeedListAdapter.OnItemClick {
                    override fun onClicklisneter(
                        pos: Int,
                        name: String,
                        v: View,
                        objPost: TrendingFeedData
                    ) {
                        when (name) {
                            "showDotMenu" -> {
                                showDotMenu(v, pos)
                            }
                            "otherUserProfile" -> {
                                if (sessionManager?.isLoggedIn()!!) {
                                    if (feedListAdapter != null) {
                                        feedListAdapter?.pausePlayer()
                                        feedListAdapter?.stopPlayer()
                                        feedList!![pos]!!.postSerializedData?.get(0)!!.albummedia.get(
                                            0
                                        ).isPlaying = false
                                        feedListAdapter?.notifyDataSetChanged()
                                    }
                                    var profileDetailFragment = ProfileDetailFragment()
                                    if (!userData?.userID.equals(
                                            feedList?.get(pos)!!.userID,
                                            false
                                        )
                                    ) {
                                        var uName = ""
                                        if (!feedList?.get(pos)!!.userFirstName.isNullOrEmpty() && !feedList?.get(
                                                pos
                                            )!!.userLastName.isNullOrEmpty()
                                        ) {
                                            uName =
                                                feedList?.get(pos)!!.userFirstName + " " + feedList?.get(
                                                    pos
                                                )!!.userLastName
                                        } else if (feedList?.get(pos)!!.userFirstName.isNullOrEmpty() && !feedList?.get(
                                                pos
                                            )!!.userLastName.isNullOrEmpty()
                                        ) {
                                            uName = "" + feedList?.get(pos)!!.userLastName
                                        } else if (!feedList?.get(pos)!!.userFirstName.isNullOrEmpty() && feedList?.get(
                                                pos
                                            )!!.userLastName.isNullOrEmpty()
                                        ) {
                                            uName = feedList?.get(pos)!!.userFirstName + ""
                                        }
                                        Bundle().apply {
                                            putString("userID", feedList?.get(pos)!!.userID)
                                            putString("userName", uName)
                                            putSerializable("feedData", feedList?.get(pos)!!)
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
                            "Follow" -> {
                                if (sessionManager?.isLoggedIn()!!) {
                                    getUserFollow("follow", feedList!![pos]!!.userID, pos, objPost)
                                } else {
                                    MyUtils.startActivity(
                                        mActivity!!,
                                        LoginActivity::class.java,
                                        false
                                    )

                                }
                            }
                            "Following" -> {
                                if (sessionManager?.isLoggedIn()!!) {
                                    getUserFollow(
                                        "unfollow",
                                        feedList!![pos]!!.userID,
                                        pos,
                                        objPost
                                    )

                                } else {
                                    MyUtils.startActivity(
                                        mActivity!!,
                                        LoginActivity::class.java,
                                        false
                                    )

                                }
                            }
                            "clap" -> {
                                if (sessionManager?.isLoggedIn()!!) {
                                    feedClap(v as AppCompatImageView, pos)
                                } else {
                                    MyUtils.startActivity(
                                        mActivity!!,
                                        LoginActivity::class.java,
                                        false
                                    )

                                }
                            }
                            "favourite" -> {
                                if (sessionManager?.isLoggedIn()!!) {
                                    feedFavorite(v as AppCompatImageView, pos)

                                } else {
                                    MyUtils.startActivity(
                                        mActivity!!,
                                        LoginActivity::class.java,
                                        false
                                    )

                                }
                            }
                            "share" -> {
//                                if (sessionManager?.isLoggedIn()!!) {
                                //feedShare(v as AppCompatImageView, pos)
                                postData(feedList?.get(pos)!!, pos, "Gmail")
                                /*} else {
                                        MyUtils.startActivity(
                                            mActivity!!,
                                            LoginActivity::class.java,
                                            false
                                        )

                                    }*/
                            }
                            "likelist" -> {
                                if (sessionManager?.isLoggedIn()!!) {

                                    var postReactUserListFragment = PostReactUserListFragment()
                                    Bundle().apply {
                                        putSerializable("postData", feedList!![pos]!!)
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
                            "comment" -> {
                                if (sessionManager?.isLoggedIn()!!) {
                                    if (feedListAdapter != null) {
                                        feedListAdapter?.pausePlayer()
                                        feedListAdapter?.stopPlayer()
                                        feedList!![pos]!!.postSerializedData?.get(0)!!.albummedia.get(
                                            0
                                        ).isPlaying = false
                                        feedListAdapter?.notifyDataSetChanged()
                                    }
                                    val bundle = Bundle()
                                    bundle.apply {
                                        putString("postID", objPost.postID)
                                        putString("postUserID", objPost.userID)
                                        putSerializable("feedData", objPost)
                                    }
                                    (activity as MainActivity).navigateTo(
                                        CommentListFragment(), bundle,
                                        CommentListFragment::class.java.name,
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
                            "laugh" -> {
                                if (sessionManager?.isLoggedIn()!!) {
                                    if (MyUtils.isInternetAvailable(mActivity!!)) {
                                        feedLike1(
                                            feedList!![pos]!!,
                                            pos,
                                            "Laugh",
                                            v as LinearLayoutCompat
                                        )
                                    } else {
                                        (activity as MainActivity).errorMethod()
                                    }

                                } else {
                                    MyUtils.startActivity(
                                        mActivity!!,
                                        LoginActivity::class.java,
                                        false
                                    )

                                }
                            }
                            "angry" -> {
                                if (sessionManager?.isLoggedIn()!!) {

                                    if (MyUtils.isInternetAvailable(mActivity!!)) {
                                        feedLike1(
                                            feedList!![pos]!!,
                                            pos,
                                            "Angry",
                                            v as LinearLayoutCompat
                                        )
                                    } else {
                                        (activity as MainActivity).errorMethod()
                                    }

                                } else {
                                    MyUtils.startActivity(
                                        mActivity!!,
                                        LoginActivity::class.java,
                                        false
                                    )

                                }
                            }
                            "worried" -> {
                                if (sessionManager?.isLoggedIn()!!) {
                                    if (MyUtils.isInternetAvailable(mActivity!!)) {
                                        feedLike1(
                                            feedList!![pos]!!,
                                            pos,
                                            "Sad",
                                            v as LinearLayoutCompat
                                        )
                                    } else {
                                        (activity as MainActivity).errorMethod()
                                    }

                                } else {
                                    MyUtils.startActivity(
                                        mActivity!!,
                                        LoginActivity::class.java,
                                        false
                                    )

                                }
                            }
                            "like" -> {

                                if (sessionManager?.isLoggedIn()!!) {
                                    if (MyUtils.isInternetAvailable(mActivity!!)) {
                                        feedLike1(
                                            feedList!![pos]!!,
                                            pos,
                                            "Like",
                                            v as LinearLayoutCompat
                                        )
                                    } else {
                                        (activity as MainActivity).errorMethod()
                                    }
                                } else {
                                    MyUtils.startActivity(
                                        mActivity!!,
                                        LoginActivity::class.java,
                                        false
                                    )

                                }
                            }
                            "Unlike" -> {
                                if (sessionManager?.isLoggedIn()!!) {
                                    if (MyUtils.isInternetAvailable(mActivity!!)) {
                                        feedLike1(
                                            feedList!![pos]!!,
                                            pos,
                                            name,
                                            v as LinearLayoutCompat
                                        )
                                    } else {
                                        (activity as MainActivity).errorMethod()
                                    }
                                } else {
                                    MyUtils.startActivity(
                                        mActivity!!,
                                        LoginActivity::class.java,
                                        false
                                    )

                                }
                            }
                            "videoView" -> {
                                if (sessionManager?.isLoggedIn()!!) {

                                    val firstPos =
                                        linearLayoutManager?.findFirstVisibleItemPosition()
                                    val lastPos = linearLayoutManager?.findLastVisibleItemPosition()

                                    if (feedListAdapter != null && feedList?.size!! > 0)
                                        setAdapterPosition(
                                            firstPos!!,
                                            lastPos!!,
                                            "Video",
                                            v as AppCompatTextView
                                        )
                                }
                            }
                            "fullScreen" -> {
                                if (feedListAdapter != null) {
                                    feedListAdapter?.pausePlayer()
                                    feedListAdapter?.stopPlayer()
                                    feedList!![pos]!!.postSerializedData?.get(0)!!.albummedia.get(0).isPlaying =
                                        false
                                    feedListAdapter?.notifyDataSetChanged()
                                }
                                var intent = Intent(mActivity!!, VideoActivity::class.java)
                                intent.putExtra(
                                    "url",
                                    RestClient.image_base_url_post + feedList!![pos]!!.postSerializedData[0].albummedia[0].albummediaFile
                                )
                                startActivity(intent)
                                mActivity?.overridePendingTransition(
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right
                                )
                            }


                        }
                    }

                }, "", feedList!!
            )
            feedRecyclerview?.layoutManager = linearLayoutManager
            feedRecyclerview?.adapter = feedListAdapter
            feedRecyclerview?.getItemAnimator()?.setChangeDuration(0);
            pageNo = 0
            getPostList()
        }

        feedRecyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                if (feedListAdapter?.popup != null)
                    feedListAdapter?.popup?.dismiss()
                visibleItemCount = linearLayoutManager?.childCount!!
                totalItemCount = linearLayoutManager?.itemCount!!
                firstVisibleItemPosition = linearLayoutManager?.findFirstVisibleItemPosition()!!
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10
                    ) {
                        isLoading = true
                        getPostList()
                    }
                } else if (isLastpage && y > 0) {
                    Log.e("fgsg", "" + recyclerView.scrollState)
                    // (activity as MainActivity).showSnackBar("No more post available.")
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (feedListAdapter?.popup != null)
                    feedListAdapter?.popup?.dismiss()
                if (y > 0) {
                    MyUtils.hideKeyboard1(mActivity!!)
                    // rvMainFeed.visibility=View.GONE
                    (activity as MainActivity).top_sheet.visibility = View.GONE

                } else if (y < 0) {
                    MyUtils.hideKeyboard1(mActivity!!)
                    // rvMainFeed.visibility=View.VISIBLE
                    (activity as MainActivity).top_sheet.visibility = View.GONE

                } else {

                }
                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    feedListAdapter?.playAvailableVideos(newState, feedRecyclerview)

                    val firstPos = linearLayoutManager?.findFirstVisibleItemPosition()
                    val lastPos = linearLayoutManager?.findLastVisibleItemPosition()

                    if (feedListAdapter != null && feedList?.size!! > 0)
                        setAdapterPosition(
                            firstPos!!,
                            lastPos!!,
                            "Photo",
                            AppCompatTextView(mActivity!!)
                        )

                }
            }
        })

        btnRetry.setOnClickListener {
            pageNo = 0
            getPostList()
        }

        swiperefresh.setOnRefreshListener {
            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            refreshItems()
        }
    }

    fun refreshItems() {
        swiperefresh.isRefreshing = true
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
            jsonObject.put("postID", "0")
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
        getPostListModel?.apiFunction(mActivity!!, false, jsonArray.toString(), "postList")!!
            .observe(
                mActivity!!,
                object : Observer<List<TrendingFeedDatum?>?> {
                    override fun onChanged(trendingFeedlistpojos: List<TrendingFeedDatum?>?) {
                        isLoading = false
                        if (trendingFeedlistpojos != null && trendingFeedlistpojos.size > 0) {
                            if (trendingFeedlistpojos[0]!!.status.equals("true", false)) {
                                pageNo = 1

                                feedListAdapter?.notifyDataSetChanged()
                                feedRecyclerview.scrollToPosition(0)
                            } else {
                            }
                            if (feedList == null && feedList!!.size == 0) {
                                noDatafoundRelativelayout.visibility = View.VISIBLE
                            } else {
                                noDatafoundRelativelayout.visibility = View.GONE
                            }
                        }
                        swiperefresh.isRefreshing = false
                    }
                })
    }

    fun getPostList() {
        noDatafoundRelativelayout.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        if (pageNo == 0) {
            relativeprogressBar.visibility = View.GONE
            shimmer_view_container.startShimmerAnimation()
            feedList!!.clear()
            feedListAdapter?.notifyDataSetChanged()
        } else {
            relativeprogressBar.visibility = View.GONE
            feedRecyclerview.visibility = (View.VISIBLE)
            feedList?.add(null)
            feedListAdapter?.notifyItemInserted(feedList!!.size - 1)
        }
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
            jsonObject.put("postID", "0")
            jsonObject.put("page", pageNo)
            jsonObject.put("sortingwith", "Trending")
            jsonObject.put("pagesize", "10")
            jsonObject.put("searchWord", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        getPostListModel?.apiFunction(mActivity!!, false, jsonArray.toString(), "postList")
            ?.observe(this@TrendingFeedFragment,
                Observer<List<TrendingFeedDatum>>
                { trendingFeedDatum ->
                    if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                        isLoading = false
                        //   remove progress item
                        noDatafoundRelativelayout.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE
                        shimmer_view_container.stopShimmerAnimation()
                        shimmer_view_container.visibility = View.GONE
                        feedRecyclerview.visibility = (View.VISIBLE)
                        if (pageNo > 0) {
                            feedList!!.removeAt(feedList!!.size - 1)
                            feedListAdapter?.notifyItemRemoved(feedList!!.size)
                        }
                        if (trendingFeedDatum[0].status.equals("true")) {

                            if (pageNo == 0) {
                                feedList!!.clear()
                            }

                            if (trendingFeedDatum[0].data == null) {
                                isLastpage = true
                            } else if (trendingFeedDatum[0].data.size < 10) {
                                isLastpage = true
                            }

                            feedList!!.addAll(trendingFeedDatum[0].data)
                            feedListAdapter?.notifyDataSetChanged()
                            pageNo = pageNo + 1
//                            pageNo += 1
//                            if (trendingFeedDatum[0].data.size < 10) {
//                                isLastpage = true
//                            }

                        } else {
                            relativeprogressBar.visibility = View.GONE
                            shimmer_view_container.stopShimmerAnimation()
                            shimmer_view_container.visibility = View.GONE
                            if (feedList!!.size == 0) {
                                noDatafoundRelativelayout.visibility = View.VISIBLE
                                feedRecyclerview.visibility = View.GONE
                                nodatafoundtextview.visibility = View.GONE

                            } else {
                                noDatafoundRelativelayout.visibility = View.GONE
                                feedRecyclerview.visibility = View.VISIBLE

                            }
                        }

                    } else {
                        if (activity != null) {
                            relativeprogressBar.visibility = View.GONE
                            feedRecyclerview.visibility = (View.GONE)
                            shimmer_view_container.stopShimmerAnimation()
                            shimmer_view_container.visibility = View.GONE
                            try {
                                nointernetMainRelativelayout.visibility = View.VISIBLE
                                if (MyUtils.isInternetAvailable(activity!!)) {
                                    nointernetImageview.setImageDrawable(
                                        activity!!.getDrawable(
                                            R.drawable.something_went_wrong
                                        )
                                    )
                                    nointernettextview.text =
                                        (GetDynamicStringDictionaryObjectClass.Something_Went_Wrong)
                                    nointernettextview1.text =
                                        (GetDynamicStringDictionaryObjectClass.Something_Went_Wrong)
                                } else {
                                    nointernetImageview.setImageDrawable(
                                        activity!!.getDrawable(
                                            R.drawable.no_internet_connection
                                        )
                                    )
                                    nointernettextview1.text =
                                        (GetDynamicStringDictionaryObjectClass.No_Internet_Connection)


                                    nointernettextview.text =
                                        (GetDynamicStringDictionaryObjectClass.Please_check_your_internet_connectivity_and_try_again)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }
                })
    }


    fun showDotMenu(v: View, pos: Int) {
        //init the wrapper with style
        val wrapper = ContextThemeWrapper(mActivity!!, R.style.popmenu_style)
        //init the popup
        val popup = PopupMenu(wrapper, v)

        /*  The below code in try catch is responsible to display icons*/
        if (true) {
            try {
                val fields = popup.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field.get(popup)
                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons =
                            classPopupHelper.getMethod(
                                "setForceShowIcon",
                                Boolean::class.javaPrimitiveType
                            )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //inflate menu
        popup.menuInflater.inflate(R.menu.popup_with_image, popup.menu)
        //implement click events
        popup.menu.getItem(0).setIcon(R.drawable.repost_icon_black)
        popup.menu.getItem(1).setIcon(R.drawable.report_icon_black)
        popup.menu.getItem(2).setIcon(R.drawable.hide_post_icon_black)
        popup.menu.getItem(3).setIcon(R.drawable.hide_post_icon_black)

        popup.menu.getItem(0).title = "" + GetDynamicStringDictionaryObjectClass.Report_this_Post
        popup.menu.getItem(1).title = "" + GetDynamicStringDictionaryObjectClass.Report_Post
        popup.menu.getItem(2).title = "" + GetDynamicStringDictionaryObjectClass.Hide_Post_For_Me
        popup.menu.getItem(3).title = "" + GetDynamicStringDictionaryObjectClass.Hide_For_Everyone

        if (!userData?.userID.equals(feedList?.get(pos)!!.userID, false)) {

            popup.menu.getItem(0).isVisible = true
            popup.menu.getItem(1).isVisible = true
            popup.menu.getItem(2).isVisible = true
            popup.menu.getItem(3).isVisible = false

        } else {
            popup.menu.getItem(0).isVisible = false
            popup.menu.getItem(1).isVisible = false
            popup.menu.getItem(2).isVisible = true
            popup.menu.getItem(3).isVisible = true

            /*if (feedList?.get(pos)!!.originalPostID.toInt() > 0) {

                 popup.menu.getItem(0).isVisible = true
                 popup.menu.getItem(1).isVisible = true
                 popup.menu.getItem(2).isVisible = true
                 popup.menu.getItem(3).isVisible = false
             } else {
                 popup.menu.getItem(0).isVisible = false
                 popup.menu.getItem(1).isVisible = false
                 popup.menu.getItem(2).isVisible = true
                 popup.menu.getItem(3).isVisible = true
             }*/

        }


        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_profile -> {
                    if (sessionManager?.isLoggedIn()!!) {
                        getRepost(pos, feedList?.get(pos)!!)
                    } else {
                        MyUtils.startActivity(mActivity!!, LoginActivity::class.java, false)

                    }
                }
                R.id.settings -> {
                    if (sessionManager?.isLoggedIn()!!) {
                        if (!reportReasonData.isNullOrEmpty()) {
                            showDilaogPostReport(
                                feedList?.get(pos)!!.postID,
                                reportReasonData!!,
                                pos
                            )
                        } else {
                            getReasonList(feedList?.get(pos)!!.postID, pos)
                        }
                    } else {
                        MyUtils.startActivity(mActivity!!, LoginActivity::class.java, false)

                    }
                }
                R.id.signature_Video -> {
                    if (sessionManager?.isLoggedIn()!!) {
                        getPostHide(feedList?.get(pos)?.postID, pos)
                    } else {
                        MyUtils.startActivity(mActivity!!, LoginActivity::class.java, false)

                    }
                }
                R.id.signout -> {
                    if (sessionManager?.isLoggedIn()!!) {
                        getPostInActive(feedList?.get(pos)?.postID, pos)
                    } else {
                        MyUtils.startActivity(mActivity!!, LoginActivity::class.java, false)

                    }
                }
            }
            true
        }
        popup.show()
    }

    private fun getRepost(pos: Int, trendingFeedData: TrendingFeedData) {


        var c = Calendar.getInstance()

        var df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var formattedDate = df.format(c.time)
        MyUtils.showProgressDialog(mActivity!!)
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        val jsonObjectpostSerializedData = JSONObject()
        val jsonArraypostSerializedData = JSONArray()


        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postHeadline", trendingFeedData.postHeadline)
            jsonObject.put("postDescription", trendingFeedData.postDescription)
            jsonObject.put("postLocation", trendingFeedData.postLocation)
            jsonObject.put("postLatitude", MyUtils.currentLattitudeFix.toFloat())
            jsonObject.put("postLongitude", MyUtils.currentLongitude.toFloat())
            jsonObject.put(
                "postLocationVerified",
                trendingFeedData.postLocationVerified
            )
            jsonObject.put("postSerializedData", jsonArraypostSerializedData)
            jsonObject.put("postPrivacyType", trendingFeedData.postPrivacyType)
            jsonObject.put("postCreateType", trendingFeedData.postCreateType)
            jsonObject.put("postDateTime", formattedDate)
            jsonObject.put("postHashText", trendingFeedData.postHashText)
            if (trendingFeedData.originalPostID.isNullOrEmpty() || trendingFeedData.originalPostID.equals(
                    "0",
                    false
                )
            ) {
                jsonObject.put("originalPostID", trendingFeedData.postID)
            } else {
                jsonObject.put("originalPostID", trendingFeedData.originalPostID)
            }
            if (trendingFeedData.originaluserID.isNullOrEmpty() || trendingFeedData.originaluserID.equals(
                    "0",
                    false
                )
            ) {
                jsonObject.put("originaluserID", trendingFeedData.userID)

            } else {
                jsonObject.put("originaluserID", trendingFeedData.originaluserID)

            }
            jsonObject.put("posttag",
                if (!trendingFeedData.posttag.isNullOrEmpty()) {
                    trendingFeedData.posttag.joinToString {
                        it.userID.toString()
                    }
                } else {
                    ""
                }
            )
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        when (trendingFeedData.postMediaType) {
            "Photo" -> {
                jsonObject.put("postMediaType", "Photo")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()

                    for (i in 0 until trendingFeedData.postSerializedData[0].albummedia.size) {
                        val jsonObjectAlbummedia = JSONObject()
                        try {
                            jsonObjectAlbummedia.put(
                                "albummediaFile",
                                trendingFeedData.postSerializedData[0].albummedia[i].albummediaFile
                            )
                            jsonObjectAlbummedia.put("albummediaThumbnail", "")
                            jsonObjectAlbummedia.put("albummediaFileType", "Photo")
                            jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "Video" -> {
                jsonObject.put("postMediaType", "Video")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    for (i in 0 until trendingFeedData.postSerializedData[0].albummedia.size) {
                        val jsonObjectAlbummedia = JSONObject()
                        try {
                            jsonObjectAlbummedia.put(
                                "albummediaFile",
                                trendingFeedData.postSerializedData[0].albummedia[i].albummediaFile
                            )
                            jsonObjectAlbummedia.put("albummediaThumbnail", "")
                            jsonObjectAlbummedia.put("albummediaFileType", "Video")
                            jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        jsonArray.put(jsonObject)

        val repostPostModel =
            ViewModelProviders.of(this@TrendingFeedFragment).get(RepostPostModel::class.java)
        repostPostModel.apiFunction(mActivity!!, false, jsonArray.toString())
            .observe(this@TrendingFeedFragment,
                Observer<List<CommonPojo>> { response ->

                    if (!response.isNullOrEmpty()) {
                        MyUtils.closeProgress()
                        if (response[0].status.equals("true", true)) {
                            MyUtils.closeProgress()
                            MyUtils.hideKeyboard1(mActivity!!)

                            (activity as MainActivity).showSnackBar(response[0].message!!)
                            pageNo = 0
                            getPostList()

                        } else {
                            MyUtils.closeProgress()
                            (activity as MainActivity).showSnackBar(response[0].message!!)
                            //No data and no internet

                        }

                    } else {
                        MyUtils.closeProgress()
                        //No internet and somting went rong
                        (activity as MainActivity).errorMethod()
                    }
                })

    }


    fun showDialogPost(msg: String) {

        val dialogs =
            MaterialAlertDialogBuilder(mActivity!!, R.style.ThemeOverlay_MyApp_Dialog)

        //   dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(true)

        val dialogView = layoutInflater.inflate(R.layout.be_a_reporter_dialog, null)
        dialogs.setView(dialogView)
        val alertDialog = dialogs.create()

        val ll_home_delivery = dialogView.findViewById<LinearLayout>(R.id.root_Dialog)
        val img_logo_dialog = dialogView.findViewById<AppCompatImageView>(R.id.img_logo_dialog)
        val tvUserNameDialog = dialogView.findViewById<AppCompatTextView>(R.id.tvUserNameDialog)
        val tvUserIdDialog = dialogView.findViewById<AppCompatTextView>(R.id.tvUserIdDialog)
        val tvyouare_reporterDialog =
            dialogView.findViewById<AppCompatTextView>(R.id.tvyouare_reporterDialog)
        img_logo_dialog.setImageResource(R.drawable.tick_circle_post_aired_successfully_prompt)
        tvUserNameDialog.visibility = View.GONE
        tvUserIdDialog.visibility = View.GONE
        tvyouare_reporterDialog.text = msg

        var lp = WindowManager.LayoutParams()
        lp.copyFrom(alertDialog.window?.attributes)
//        lp.width = 850
//        lp.height = 900

        alertDialog.window?.attributes = lp
        alertDialog.show()

        Handler().postDelayed({

            alertDialog.dismiss()

        }, 5000)


    }

    private fun animateOut(
        button: BottomNavigationView,
        bottomFabButton: FloatingActionButton
    ) {
        if (Build.VERSION.SDK_INT >= 14) {

            ViewCompat.animate(button).translationY((button.height).toFloat())
                .setInterpolator(LinearInterpolator()).withLayer()
                .setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationStart(view: View) {
                        this@TrendingFeedFragment.mIsAnimatingOut = true
                    }

                    override fun onAnimationCancel(view: View) {
                        this@TrendingFeedFragment.mIsAnimatingOut = false
                    }

                    override fun onAnimationEnd(view: View) {
                        this@TrendingFeedFragment.mIsAnimatingOut = false
                        view.visibility = View.GONE
                    }
                }).start()

            ViewCompat.animate(bottomFabButton).translationY((bottomFabButton.height).toFloat())
                .setInterpolator(LinearInterpolator()).withLayer()
                .setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationStart(view: View) {
                        this@TrendingFeedFragment.mIsAnimatingOut = true
                    }

                    override fun onAnimationCancel(view: View) {
                        this@TrendingFeedFragment.mIsAnimatingOut = false
                    }

                    override fun onAnimationEnd(view: View) {
                        this@TrendingFeedFragment.mIsAnimatingOut = false
                        view.visibility = View.GONE
                    }
                }).start()

        } else {
            val anim = AnimationUtils.loadAnimation(button.context, R.anim.bottom_sheet_out)
            anim.interpolator = LinearInterpolator()
            anim.duration = 200L
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    this@TrendingFeedFragment.mIsAnimatingOut = true
                }

                override fun onAnimationEnd(animation: Animation) {
                    this@TrendingFeedFragment.mIsAnimatingOut = false
                    button.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            button.startAnimation(anim)

            val anim1 =
                AnimationUtils.loadAnimation(bottomFabButton.context, R.anim.bottom_sheet_out)
            anim1.interpolator = LinearInterpolator()
            anim1.duration = 200L
            anim1.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    this@TrendingFeedFragment.mIsAnimatingOut = true
                }

                override fun onAnimationEnd(animation: Animation) {
                    this@TrendingFeedFragment.mIsAnimatingOut = false
                    bottomFabButton.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            bottomFabButton.startAnimation(anim1)
        }
    }

    // Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    private fun animateIn(
        button: BottomNavigationView,
        bottomFabButton: FloatingActionButton
    ) {
        button.visibility = View.VISIBLE
        bottomFabButton.visibility = View.VISIBLE

        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).translationY(0f)
                .setInterpolator(LinearInterpolator()).withLayer().setListener(null)
                .start()
            ViewCompat.animate(bottomFabButton).translationY(0f)
                .setInterpolator(LinearInterpolator()).withLayer().setListener(null)
                .start()
        } else {
            val anim = AnimationUtils.loadAnimation(button.context, R.anim.bottom_sheet_in)
            anim.duration = 200L
            anim.interpolator = LinearInterpolator()
            button.startAnimation(anim)
            val anim1 =
                AnimationUtils.loadAnimation(bottomFabButton.context, R.anim.bottom_sheet_in)
            anim1.duration = 200L
            anim1.interpolator = LinearInterpolator()
            bottomFabButton.startAnimation(anim1)
        }
    }

    fun setAdapterPosition(
        firstPos: Int,
        lastPos: Int,
        from: String,
        appCompatTextView: AppCompatTextView
    ) {
        try {
            if (sessionManager != null && sessionManager?.isLoggedIn()!!) {
                var userId = userData?.userID
                for (i in firstPos..lastPos) {
                    when (from) {
                        "Photo" -> {
                            if (feedList?.get(firstPos)!!.postMediaType.contains("Photo") && feedList?.get(
                                    firstPos
                                )!!.postSerializedData.size > 0 && feedList?.get(firstPos)!!.postSerializedData.get(
                                    0
                                ).albummedia.size === 1
                            ) {

                                setPostViewsCount(i, from, appCompatTextView)
                            }
                        }

                        "Video" -> {
                            if (feedList?.size!! > 0 && feedList?.get(i) != null) {
                                if (feedList?.get(firstPos)!!.postMediaType.contains("Video") && feedList?.get(
                                        firstPos
                                    )!!.postSerializedData.isNotEmpty() && feedList?.get(firstPos)!!.postSerializedData.get(
                                        0
                                    ).albummedia.size === 1
                                ) {

                                    setPostViewsCount(i, from, appCompatTextView)

                                }

                            }
                        }
                    }


                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun setPostViewsCount(
        feedPos: Int,
        from: String,
        appCompatTextView: AppCompatTextView
    ) {
        try {
            if (!feedList?.get(feedPos)?.isYouViewPost?.contains("Yes")!!) {
                feedList?.get(feedPos)?.isYouViewPost = "Yes"
                var count = 0
                try {
                    count = Integer.valueOf(feedList?.get(feedPos)!!.postViews) + 1

                } catch (e: Exception) {
                }

                feedList?.get(feedPos)!!.postViews = (count.toString())
                when (from) {

                    "Video" -> {
                        feedList?.get(feedPos)!!.postSerializedData.get(0).albummedia.get(0)
                            .isPlaying = true
                        appCompatTextView.text = feedList?.get(feedPos)!!.postViews
                        // feedListAdapter?.notifyItemChanged(feedPos)
                    }
                    else -> {
                        feedListAdapter?.notifyItemChanged(feedPos)
                    }


                }
                //  broadCastingData(feedlist.get(feedPos))
                val jsonArray = JSONArray()
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("loginuserID", userData?.userID)
                    jsonObject.put("languageID", userData?.languageID)
                    jsonObject.put("userID", feedList?.get(feedPos)!!.userID)
                    jsonObject.put("postID", feedList?.get(feedPos)!!.postID)
                    jsonObject.put("apiType", RestClient.apiType)
                    jsonObject.put("apiVersion", RestClient.apiVersion)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                jsonArray.put(jsonObject)
                val postView =
                    ViewModelProviders.of(this@TrendingFeedFragment).get(PostViewModel::class.java)
                postView.apiCall(mActivity!!, jsonArray.toString(), "ViewPost").observe(mActivity!!,
                    Observer { postViewPojos ->
                        if (postViewPojos!![0].status.equals("true", false)) {
                        } else {

                            /*for (i in feedList?.indices!!) {
                                if (feedList?.get(i)!!.postID.equals(postViewPojos[0].getFeedDatum().getPostID())) {
                                     break
                                }
                            }*/
                        }
                    })

            }
        } catch (e: Exception) {
        }

    }

    override fun onPause() {
        super.onPause()
        if (feedListAdapter != null) {
            feedListAdapter?.stopPlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (feedListAdapter != null) {
            feedListAdapter?.stopPlayer()
            feedListAdapter?.pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (feedListAdapter != null) {
            feedListAdapter?.stopPlayer()
            feedListAdapter?.pausePlayer()
        }

        LocalBroadcastManager.getInstance(mActivity!!)
            .unregisterReceiver(mYourBroadcastReceiverResumeFilterFinish)
    }

    private fun getUserFollow(
        s: String,
        userID: String,
        pos: Int,
        objPost: TrendingFeedData
    ) {
        // MyUtils.showProgressDialog(mActivity!!)
        when (s) {
            "follow" -> {
                for (i in 0 until feedList?.size!!) {
                    if (feedList?.get(i)!!.userID.equals(feedList?.get(pos)!!.userID, false)) {
                        feedList?.get(i)!!.isYouFollowing = "Yes"
                    }
                }

                feedListAdapter?.notifyDataSetChanged()

            }
            "unfollow" -> {
                for (i in 0 until feedList?.size!!) {
                    if (feedList?.get(i)!!.userID.equals(feedList?.get(pos)!!.userID, false)) {
                        feedList?.get(i)!!.isYouFollowing = "No"
                    }
                }
                feedListAdapter?.notifyDataSetChanged()
            }
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("userID", userID)
            jsonObject.put("action", s)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        var getFollowModel =
            ViewModelProviders.of(this@TrendingFeedFragment).get(FollowModel::class.java)
        getFollowModel.apiCall(mActivity!!, jsonArray.toString(), s)
            .observe(this@TrendingFeedFragment,
                Observer
                { trendingFeedDatum ->
                    if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                        // MyUtils.closeProgress()
                        if (trendingFeedDatum[0].status?.equals("true", false)!!) {

                        } else {
                            for (i in 0 until feedList?.size!!) {
                                if (feedList?.get(i)?.postID.equals(
                                        objPost.postID
                                    )
                                ) {
                                    feedList?.set(i, objPost)
                                    feedListAdapter?.notifyDataSetChanged()
                                    break
                                }
                            }
                        }

                    } else {
                        if (activity != null) {
                            //  MyUtils.closeProgress()
                            (activity as MainActivity).errorMethod()

                        }
                    }
                })
    }


    fun feedLike1(
        feedDatum: TrendingFeedData,
        feedPosition: Int,
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
                        count = Integer.valueOf(feedList!![feedPosition]!!.postAll) + 1

                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf(feedList!![feedPosition]!!.postAll) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf(feedList!![feedPosition]!!.postAll) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())
                action = "LikePost"
                feedListAdapter?.notifyItemChanged(feedPosition)
            }
            "Sad" -> {

                var count = 0
                if (feedDatum.IsLiekedByYou.equals("No", false)) {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        count = Integer.valueOf(feedList!![feedPosition]!!.postAll) + 1

                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf(feedList!![feedPosition]!!.postAll) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf(feedList!![feedPosition]!!.postAll) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())
                action = "LikePost"
                feedListAdapter?.notifyItemChanged(feedPosition)
            }
            "Angry" -> {
                var count = 0
                if (feedDatum.IsLiekedByYou.equals("No", false)) {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        count = Integer.valueOf(feedList!![feedPosition]!!.postAll) + 1

                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf(feedList!![feedPosition]!!.postAll) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf(feedList!![feedPosition]!!.postAll) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())
                action = "LikePost"
                feedListAdapter?.notifyItemChanged(feedPosition)
            }
            "Unlike" -> {
                feedDatum.IsLiekedByYou = ("No")
                var count = 0
                try {
                    count = Integer.valueOf(feedList!![feedPosition]!!.postAll) - 1


                } catch (e: java.lang.Exception) {
                }
                feedDatum.postAll = (count.toString())
                action = "UnLikePost"
                feedListAdapter?.notifyItemChanged(feedPosition)
            }
            "Like" -> {

                var count = 0
                if (feedDatum.IsLiekedByYou.equals("No", false)) {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        count = Integer.valueOf(feedList!![feedPosition]!!.postAll) + 1
                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf(feedList!![feedPosition]!!.postAll) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf(feedList!![feedPosition]!!.postAll) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())
                action = "LikePost"
                feedListAdapter?.notifyItemChanged(feedPosition)

            }
        }
        val json = Gson().toJson(feedList?.get(feedPosition))
        val trendingFeedDatum =
            Gson().fromJson(json, TrendingFeedData::class.java)
        setPostLike(feedDatum.postID, action, feedPosition, s, trendingFeedDatum)


        likeImageview.isEnabled = true

    }

    fun setPostLike(
        postId: String,
        action: String,
        pos: Int,
        s: String,
        trendingFeedDatum: TrendingFeedData

    ) {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postID", postId)
            jsonObject.put("posterID", feedList?.get(pos)!!.userID)
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
            ViewModelProviders.of(this@TrendingFeedFragment).get(PostViewModel::class.java)

        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@TrendingFeedFragment,
            Observer<List<CommonPojo?>?> {
                if (mActivity != null) {
                    try {
                        if (it!![0]!!.status.equals("true", false)) {

                            if (action.equals("LikePost", false)) {
                                feedList?.get(pos)!!.IsLiekedByYou = "Yes"
                                feedList?.get(pos)!!.LikeReaction = s
                            } else if (action.equals("UnLikePost", false)) {
                                feedList?.get(pos)!!.IsLiekedByYou = "No"
                                feedList?.get(pos)!!.LikeReaction = s
                            }
                            feedListAdapter?.notifyItemChanged(pos)
                        } else {
                            for (i in 0 until feedList?.size!!) {
                                if (feedList?.get(i)?.postID.equals(trendingFeedDatum.postID)) {
                                    feedList?.set(i, trendingFeedDatum)
                                    feedListAdapter?.notifyDataSetChanged()
                                    break
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            })
    }

    fun setPostFavourite(
        postId: String,
        action: String,
        pos: Int,
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
            ViewModelProviders.of((this@TrendingFeedFragment)).get(
                PostFavouriteModel::class.java
            )
        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@TrendingFeedFragment,
            Observer { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos?.get(0)?.status.equals("true", false)) {
                    } else {
                        for (i in 0 until feedList?.size!!) {
                            if (feedList?.get(i)?.postID.equals(
                                    trendingFeedDatum.postID
                                )
                            ) {
                                feedList?.set(i, trendingFeedDatum)
                                feedListAdapter?.notifyDataSetChanged()
                                break
                            }
                        }
                    }

                }
            })
    }

    fun feedFavorite(
        favouriteimageview: AppCompatImageView,
        feedPosition: Int
    ) {
        favouriteimageview.isEnabled = false
        val action: String
        if (feedList!!.get(feedPosition)!!.IsYourFavorite.equals("No", false)) {
            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_red))
            var animation = AnimationUtils.loadAnimation(mActivity, R.anim.bounce)
            favouriteimageview.startAnimation(animation)
            action = "FavouritePost"
            feedList?.get(feedPosition)!!.IsYourFavorite = ("Yes")
        } else {
            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_grey))
            action = "UnFavouritePost"
            feedList?.get(feedPosition)!!.IsYourFavorite = ("No")
        }
        val json = Gson().toJson(feedList?.get(feedPosition))
        val trendingFeedDatum =
            Gson().fromJson(json, TrendingFeedData::class.java)
        setPostFavourite(
            feedList?.get(feedPosition)!!.postID,
            action,
            feedPosition,
            trendingFeedDatum
        )
        favouriteimageview.isEnabled = true
    }


    private fun showDilaogPostReport(
        postId: String,
        data: List<ReasonList.Data>,
        feedpos: Int
    ) {

        val dialogs =
            MaterialAlertDialogBuilder(mActivity!!, R.style.ThemeOverlay_MyApp_Dialog)

        //dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(true)

        val dialogView = layoutInflater.inflate(R.layout.report_dialog_reson_list, null)
        dialogs.setView(dialogView)
        val alertDialog = dialogs.create()

        val tvReportTitle = dialogView.findViewById<AppCompatTextView>(R.id.tvReportTitle)
        tvReportTitle?.text = GetDynamicStringDictionaryObjectClass.Reason_for_reporting
        val rvReportReason = dialogView.findViewById<RecyclerView>(R.id.rvReportReason)

        linearLayoutManager1 = LinearLayoutManager(mActivity!!)
        postReportListAdapter = PostReportListAdapter(
            mActivity!!,
            object : PostReportListAdapter.OnItemClick {
                override fun onClicklisneter(
                    pos: Int,
                    name: String,
                    v: View,
                    reasonid: String
                ) {
                    alertDialog.dismiss()
                    getPostReport(postId, data[pos].reasonID, feedpos)
                }


            }, "", data
        )
        rvReportReason.layoutManager = linearLayoutManager1
        rvReportReason.adapter = postReportListAdapter

        var lp = WindowManager.LayoutParams()
        lp.copyFrom(alertDialog.window?.attributes)
//        lp.width = 850
//        lp.height = 900

        alertDialog.window?.attributes = lp
        alertDialog.show()


    }


    fun getReasonList(postID: String, pos: Int) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val apiCall =
            ViewModelProviders.of(this@TrendingFeedFragment).get(ReasonModel::class.java)
        apiCall.apiFunction(mActivity!!, true, jsonArray.toString())
            .observe(this@TrendingFeedFragment,
                Observer<List<ReasonList>?>
                { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            //data found
                            reportReasonData?.clear()
                            reportReasonData?.addAll(response[0].data)
                            showDilaogPostReport(postID, response[0].data, pos)

                        } else {
                            //data not find
                            (activity as MainActivity).showSnackBar(response[0].message)
                        }
                    } else {
                        //No internet and somthing rong
                        (activity as MainActivity).errorMethod()
                    }
                })
    }


    private fun getPostHide(
        postID: String?,
        pos: Int
    ) {
        MyUtils.showProgress(mActivity!!)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("posterID", feedList?.get(pos)?.userID)
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var reportPostModel =
            ViewModelProviders.of(this@TrendingFeedFragment).get(ReportPostModel::class.java)

        reportPostModel.apiFunction(mActivity!!, jsonArray.toString(), "PostHide")
            .observe(this@TrendingFeedFragment, Observer { it ->
                //            MyUtils.closeProgress()

                if (it != null && it.isNotEmpty()) {
                    MyUtils.closeProgress()

                    if (it[0].status.equals("true", true)) {
                        (activity as MainActivity).showSnackBar(it[0].message!!)
                        for (i in 0 until feedList?.size!!) {

                            if (feedList?.get(i)?.postID.equals(postID, false)) {
                                feedList?.removeAt(i)
                                feedListAdapter?.notifyItemRemoved(feedList!!.size)
                                feedListAdapter?.notifyDataSetChanged()
                                if (feedList?.size == 0) {
                                    isLastPage1 = true
                                }
                                break
                            }
                        }

                        if (isLastPage1) {
                            feedList?.clear()
                            pageNo = 0
                            getPostList()
                        }
                    } else {

                        (activity as MainActivity).showSnackBar(it[0].message!!)
                    }

                } else {
                    MyUtils.closeProgress()
                    (activity as MainActivity).errorMethod()
                }
            })

    }


    private fun getPostInActive(
        postID: String?,
        pos: Int
    ) {
        MyUtils.showProgress(mActivity!!)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("posterID", feedList?.get(pos)?.userID)
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var reportPostModel =
            ViewModelProviders.of(this@TrendingFeedFragment).get(ReportPostModel::class.java)

        reportPostModel.apiFunction(mActivity!!, jsonArray.toString(), "PostInActive")
            .observe(this@TrendingFeedFragment, Observer { it ->
                //MyUtils.closeProgress()

                if (it != null && it.isNotEmpty()) {
                    MyUtils.closeProgress()

                    if (it[0].status.equals("true", true)) {
                        (activity as MainActivity).showSnackBar(it[0].message!!)
                        for (i in 0 until feedList?.size!!) {

                            if (feedList?.get(i)?.postID.equals(postID, false)) {
                                feedList?.removeAt(i)
                                feedListAdapter?.notifyItemRemoved(feedList!!.size)
                                feedListAdapter?.notifyDataSetChanged()
                                if (feedList?.size == 0) {
                                    isLastPage1 = true
                                }
                                break
                            }
                        }

                        if (isLastPage1) {
                            feedList?.clear()
                            pageNo = 0
                            getPostList()
                        }
                    } else {

                        (activity as MainActivity).showSnackBar(it[0].message!!)
                    }

                } else {
                    MyUtils.closeProgress()
                    (activity as MainActivity).errorMethod()
                }
            })

    }

    private fun getPostReport(
        postID: String?,
        postreportreasonID: String,
        pos: Int
    ) {
        MyUtils.showProgress(mActivity!!)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("posterID", feedList?.get(pos)?.userID)
            jsonObject.put("reasonID", postreportreasonID)
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var reportPostModel =
            ViewModelProviders.of(this@TrendingFeedFragment).get(ReportPostModel::class.java)

        reportPostModel.apiFunction(mActivity!!, jsonArray.toString(), "PostReport")
            .observe(this@TrendingFeedFragment, Observer { it ->
                //            MyUtils.closeProgress()

                if (it != null && it.isNotEmpty()) {
                    MyUtils.closeProgress()

                    if (it[0].status.equals("true", true)) {
                        for (i in 0 until feedList?.size!!) {

                            if (feedList?.get(i)?.postID.equals(postID, false)) {
                                feedList?.removeAt(i)
                                feedListAdapter?.notifyItemRemoved(feedList!!.size)
                                feedListAdapter?.notifyDataSetChanged()
                                if (feedList?.size == 0) {
                                    isLastPage1 = true
                                }
                                break
                            }
                        }

                        if (isLastPage1) {
                            feedList?.clear()
                            pageNo = 0
                            getPostList()
                        }
                    } else {

                        (activity as MainActivity).showSnackBar(it[0].message!!)
                    }

                } else {
                    MyUtils.closeProgress()
                    (activity as MainActivity).errorMethod()
                }
            })

    }

    fun feedShare(shareImageview: AppCompatImageView?, feedPosition: Int) {

        val downloadList = java.util.ArrayList<String>()
        if (feedList?.get(feedPosition)!!.postMediaType.contains("Photo") || feedList?.get(
                feedPosition
            )!!.postMediaType.contains("Video")
        )
            downloadList.add(
                feedList?.get(feedPosition)!!.postSerializedData[0].albummedia.get(0).albummediaFile
            )
        val sharingBottomSheetFragment = SharingMediaSheetFragment()
        val bundle = Bundle()
        bundle.putString("mediaType", feedList?.get(feedPosition)?.postMediaType)
        if (downloadList.size > 0) bundle.putString(
            "postid",
            feedList?.get(feedPosition)!!.postID
        )
        bundle.putString("from", "Post")
        bundle.putString("mediaList", Gson().toJson(downloadList))
        bundle.putString(
            "shareText",
            MyUtils.decodeShareText(
                feedList?.get(feedPosition)!!.postDescription.toString(),
                mActivity!!
            )
        )
        sharingBottomSheetFragment.arguments = bundle
        sharingBottomSheetFragment.show(
            (activity as FragmentActivity).supportFragmentManager,
            "BottomSheet Fragment"
        )
        sharingBottomSheetFragment.setSnakBarView((activity as MainActivity).snackBarParent)
        sharingBottomSheetFragment.setListener(object :
            SharingMediaSheetFragment.OnItemSelectedListener {
            override fun onInternalShare(
                text: String?,
                componentName: ComponentName?,
                mediaType: String?,
                shareFilePath: java.util.ArrayList<awsFile>,
                loadLabel: CharSequence
            ) {
                if (!MyUtils.isInternetAvailable(mActivity!!))
                    (activity as MainActivity).showSnackBar(mActivity?.resources?.getString(R.string.error_crash_error_message)!!)
                else {
                    sharingBottomSheetFragment.dismiss()
                    postData(
                        feedList?.get(feedPosition)!!,
                        feedPosition,
                        text,
                        componentName,
                        mediaType,
                        shareFilePath,
                        loadLabel
                    )
                }
            }
        })
    }

    fun feedClap(
        favouriteimageview: ImageView,
        feedPosition: Int
    ) {
        favouriteimageview.isEnabled = false
        val action: String
        if (feedList!!.get(feedPosition)!!.IsClappeddByYou.equals("No", false)) {

            for (i in 0 until feedList?.size!!) {
                if (feedList?.get(i)!!.userID.equals(
                        feedList?.get(feedPosition)!!.userID,
                        false
                    )
                ) {
                    feedList?.get(i)!!.IsClappeddByYou = "Yes"
                    feedList?.get(i)!!.userClapCount =
                        (feedList?.get(i)!!.userClapCount?.toInt()!! + 1).toString()
                }
            }

            feedListAdapter?.notifyDataSetChanged()
            //  favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.clap_icon_filled_enabled))
            action = "ClapPost"
            feedList?.get(feedPosition)!!.IsClappeddByYou = ("Yes")

        } else {
            //favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.clap_icon_border_disabled))
            for (i in 0 until feedList?.size!!) {
                if (feedList?.get(i)!!.userID.equals(
                        feedList?.get(feedPosition)!!.userID,
                        false
                    )
                ) {
                    if (feedList?.get(i)!!.userID.equals(
                            feedList?.get(feedPosition)!!.userID,
                            false
                        )
                    ) {
                        feedList?.get(i)!!.IsClappeddByYou = "No"
                        if (feedList?.get(i)!!.userClapCount!!.toInt() > 1) {
                            feedList?.get(i)!!.userClapCount =
                                (feedList?.get(i)!!.userClapCount?.toInt()!! - 1).toString()
                        } else {
                            feedList?.get(i)!!.userClapCount = "0"
                        }
                    }
                }
            }
            feedListAdapter?.notifyDataSetChanged()
            action = "UnClapPost"
            feedList?.get(feedPosition)!!.IsClappeddByYou = ("No")
        }
        val json = Gson().toJson(feedList?.get(feedPosition))
        val trendingFeedDatum =
            Gson().fromJson(json, TrendingFeedData::class.java)
        setPostClap(
            feedList?.get(feedPosition)!!.postID,
            action,
            feedPosition,
            trendingFeedDatum
        )

        favouriteimageview.isEnabled = true
    }

    fun setPostClap(
        postId: String,
        action: String,
        pos: Int,
        trendingFeedDatum: TrendingFeedData
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("userID", feedList?.get(pos)!!.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        val postLike: PostClapModel =
            ViewModelProviders.of((this@TrendingFeedFragment)).get(
                PostClapModel::class.java
            )
        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@TrendingFeedFragment,
            Observer { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos!![0].status.equals("true", false)) {

                    } else {
                        for (i in 0 until feedList?.size!!) {
                            if (feedList?.get(i)?.userID.equals(trendingFeedDatum.userID)
                            ) {
                                feedList?.set(i, trendingFeedDatum)
                                feedListAdapter?.notifyDataSetChanged()
                                break
                            }
                        }

                    }
                }
            })
    }


    private fun postData(
        feedDatum: TrendingFeedData,
        pos: Int,
        text: String?,
        componentName: ComponentName?,
        mediaType: String?,
        shareFilePath: java.util.ArrayList<awsFile>,
        loadLabel: CharSequence
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            /*if (Integer.valueOf(feedDatum.pos()) > 0) {
                jsonObject.put("postID", feedDatum.getOriginalPostID())
            } else {*/
            jsonObject.put("postID", feedDatum.postID)
            //}
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
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

                        feedListAdapter?.notifyDataSetChanged()

                        if (shareFilePath.size == 1) {
                            shareTextWithMedia(text, componentName, mediaType, shareFilePath)
                        } else {
                            shareTextWithMultipleImage(
                                text,
                                componentName,
                                mediaType,
                                shareFilePath
                            )
                        }

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

    private fun postData(
        feedDatum: TrendingFeedData, pos: Int, text: String?
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            /*if (Integer.valueOf(feedDatum.pos()) > 0) {
                jsonObject.put("postID", feedDatum.getOriginalPostID())
            } else {*/

            //}
            if (sessionManager?.isLoggedIn()!!) {
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put("languageID", userData?.languageID)

            } else {
                jsonObject.put("loginuserID", "0")
                jsonObject.put("languageID", sessionManager?.getsetSelectedLanguage())

            }
            jsonObject.put("postID", feedDatum.postID)
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
                        feedListAdapter?.notifyItemChanged(pos)

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


    fun shareTextWithMultipleImage(
        text: String?,
        componentName: ComponentName?,
        mediaType: String?,
        shareFilePath: java.util.ArrayList<awsFile>
    ) {
        val shareIntent = Intent()
        if (shareFilePath.size > 1)
            shareIntent.action = Intent.ACTION_SEND_MULTIPLE else shareIntent.action =
            Intent.ACTION_SEND
        val files = java.util.ArrayList<Uri>()
        if (mediaType!!.contains("Video"))
            shareIntent.type = "video/*"
        else if (mediaType.contains("Photo"))
            shareIntent.type = "image/*"
        else if (mediaType.contains("audio"))
            shareIntent.type = "audio/*" else shareIntent.type = "*/*"

        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Camfire")
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
        shareIntent.component = componentName
        activity!!.startActivityForResult(shareIntent, 0)
    }

    fun shareTextWithMedia(
        text: String?,
        componentName: ComponentName?,
        mediaType: String?,
        shareFilePath: java.util.ArrayList<awsFile>
    ) {


        val shareIntent = Intent()

        if (shareFilePath.size > 1) shareIntent.action =
            Intent.ACTION_SEND_MULTIPLE else shareIntent.action = Intent.ACTION_SEND


        if (mediaType!!.contains("video"))
            shareIntent.type = "video/*"
        else if (mediaType.contains("Photo"))
            shareIntent.type = "image/*"

        val cameraFile: Uri
        cameraFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                activity!!,
                "com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fileprovider",
                File(shareFilePath[0].uploadFile?.absolutePath)

            )
        } else {
            Uri.fromFile(File(shareFilePath[0].uploadFile?.absolutePath))
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Camfire")
        shareIntent.putExtra(Intent.EXTRA_STREAM, cameraFile)
        shareIntent.component = componentName
        activity!!.startActivityForResult(shareIntent, 0)
    }

    fun notifyData(
        feedDatum: TrendingFeedData, isDelete: Boolean,
        isDeleteComment: Boolean,
        postComment: String?
    ) {

        if (isDelete) {
            for (i in feedList?.indices!!) {

                if (feedList?.get(i)?.userID.equals(feedDatum.userID, false)) {
                    feedList?.removeAt(i)
                    feedListAdapter?.notifyItemRemoved(feedList!!.size)
                    feedListAdapter?.notifyDataSetChanged()
                }
            }
        } else {
            for (i in feedList?.indices!!) {
                if (feedList?.get(i) != null && feedList?.get(i)!!.postID.equals(
                        feedDatum.postID,
                        false
                    )
                ) {
                    feedList?.set(i, feedDatum)
                    feedListAdapter?.notifyItemChanged(i)
                    break
                }
            }
        }

    }

    fun notifyProfileData(
        postId: String, userId: String
    ) {
        /*for (i in feedList?.indices!!) {
               if (feedList?.get(i)?.userID.equals(userId, false)) {
                   feedList?.removeAt(i)
                   break
               }
           }*/

        val it: MutableIterator<TrendingFeedData?>? = feedList?.iterator()
        while (it?.hasNext()!!) {
            var obj = it.next()!!
            if (obj.userID == userId) {
                it.remove()
            }
        }
        feedListAdapter?.notifyDataSetChanged()

    }

    fun refreshField() {
        val positionView =
            (feedRecyclerview.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (positionView == 0) {
            (activity as MainActivity?)!!.showexit()
        } else {
            feedRecyclerview.smoothScrollToPosition(0)
            refreshItems()
        }
    }

}

