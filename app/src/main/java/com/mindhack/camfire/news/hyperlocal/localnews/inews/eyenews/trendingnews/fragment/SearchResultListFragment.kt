package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.LoginActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.generallistlayout.*
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
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
class SearchResultListFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null

    val keySearchType = "SEARCHTYPE"
    var searchType = -1
    var userData: RegisterPojo.Data? = null


    var searchResultListLayoutMain : LinearLayoutCompat? = null

    var message = ""

    var layoutManger : LinearLayoutManager? = null

    var adapterHashTagList :  GlobalSearchAdapter? = null
    var adapterPeopletList :  GlobalSearchPeopledapter? = null
    var adapterLocationList : GlobalSearchLocationListAdapter? = null
    var adapterCustomList :   FeedListAdapter? = null


    var sessionManager:SessionManager?=null
    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var pageNo = 1
    var searchKeyWord:String=""

    var list: ArrayList<String>? = null

    var globalSearchList:ArrayList<GlobalSearchPojo>?=null
    var tabPos:Int=-1

    var reportReasonData: ArrayList<ReasonList.Data>? = ArrayList()

    var isLastPage1 = false
    var postReportListAdapter: PostReportListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_search_result_list, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager= SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager!!.isLoggedIn())
            userData = sessionManager?.get_Authenticate_User()


        searchResultListLayoutMain?.visibility = View.VISIBLE
        noDatafoundRelativelayout.visibility=View.GONE
        nointernetMainRelativelayout?.visibility = View.GONE
        relativeprogressBar?.visibility = View.GONE

        generalRecyclerView?.visibility = View.VISIBLE

        if (arguments != null)
        {
            tabPos = arguments?.getInt("position", -1)!!
            globalSearchList = (arguments?.getSerializable("post") as ArrayList<GlobalSearchPojo>?)!!

            searchKeyWord = (arguments?.getString("searchKeyWord"))!!

       }





        if (tabPos == 0)
            message = "Tag"
        else if (tabPos == 1)
            message = "Place"
        else if (tabPos == 2)
            message = "People"
        else if (tabPos == 3)
            message = "Custom"

        layoutManger = LinearLayoutManager(mActivity!!)
        generalRecyclerView?.layoutManager = layoutManger
         if(!globalSearchList.isNullOrEmpty())
         {
             when(tabPos){
                 0 -> {
                     if(!globalSearchList?.get(0)!!.tags.isNullOrEmpty())
                     {
                         noDatafoundRelativelayout?.visibility = View.GONE
                         generalRecyclerView?.visibility = View.VISIBLE
                         setTagAdpater()
                     }else
                     {
                         nodata1.text="" +GetDynamicStringDictionaryObjectClass?.No_Data_Found
                         noDaImageView.setImageResource(R.drawable.no_data_found)
                         noDatafoundRelativelayout?.visibility = View.VISIBLE
                         nodatafoundtextview?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
                         generalRecyclerView?.visibility = View.GONE
                         nodatafoundtextview?.visibility = View.GONE
                     }
                 }
                 1 -> {
                     if(!globalSearchList?.get(0)!!.places.isNullOrEmpty())
                     {
                         noDatafoundRelativelayout?.visibility = View.GONE
                         generalRecyclerView?.visibility = View.VISIBLE
                         setPlaceAdpater()
                     }else
                     {
                         nodata1.text=GetDynamicStringDictionaryObjectClass?.No_Data_Found
                         noDaImageView.setImageResource(R.drawable.no_data_found)
                         noDatafoundRelativelayout?.visibility = View.VISIBLE
                         nodatafoundtextview?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
                         generalRecyclerView?.visibility = View.GONE
                         nodatafoundtextview?.visibility = View.GONE
                     }
                 }
                 2 -> {
                     if (!globalSearchList?.get(0)!!.people.isNullOrEmpty()) {
                         noDatafoundRelativelayout?.visibility = View.GONE
                         generalRecyclerView?.visibility = View.VISIBLE
                         setPeopleAdpater()
                     }
                     else
                     {
                         nodata1.text=GetDynamicStringDictionaryObjectClass?.No_Data_Found
                         noDaImageView.setImageResource(R.drawable.no_data_found)

                         noDatafoundRelativelayout?.visibility = View.VISIBLE
                         nodatafoundtextview?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
                         generalRecyclerView?.visibility = View.GONE
                         nodatafoundtextview?.visibility = View.GONE
                     }
                 }
                 3 -> {
                     if (!globalSearchList?.get(0)!!.custom.isNullOrEmpty()) {
                         noDatafoundRelativelayout?.visibility = View.GONE
                         generalRecyclerView?.visibility = View.VISIBLE
                         setCustomAdpater()
                     }
                     else
                     {
                         nodata1.text=GetDynamicStringDictionaryObjectClass?.No_Data_Found
                         noDaImageView.setImageResource(R.drawable.no_data_found)
                         noDatafoundRelativelayout?.visibility = View.VISIBLE
                         nodatafoundtextview?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
                         generalRecyclerView?.visibility = View.GONE
                         nodatafoundtextview?.visibility = View.GONE
                     }
                 }
             }
         }else
         {
            /* noDatafoundRelativelayout?.visibility = View.VISIBLE
             nodata1?.text = "Please Search.."
             noDaImageView.setImageResource(R.drawable.search_icon_header_black)
             nodatafoundtextview?.visibility = View.GONE
             generalRecyclerView?.visibility = View.GONE*/

             nodata1.text=GetDynamicStringDictionaryObjectClass?.No_Data_Found
             noDaImageView.setImageResource(R.drawable.no_data_found)
             noDatafoundRelativelayout?.visibility = View.VISIBLE
             nodatafoundtextview?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
             generalRecyclerView?.visibility = View.GONE
             nodatafoundtextview?.visibility = View.GONE
         }


        generalRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = layoutManger?.childCount!!
                totalItemCount = layoutManger?.itemCount!!
                firstVisibleItemPosition = layoutManger?.findFirstVisibleItemPosition()!!
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10) {

                        isLoading = true
                        when(searchType){
                            0 ->
                            {
                                //getSearchPostList(searchKeyWord)
                            }
                            1 ->
                            {
                                 //getSearchUserList(searchKeyWord)
                            }
                            2 ->
                            {
                                 //getSearchCollegeList(searchKeyWord)
                            }
                        }

                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (y > 0) {
                    MyUtils.hideKeyboard1(mActivity!!)

                    // rootHeaderLayout.visibility=View.GONE
                    (activity as MainActivity).top_sheet.visibility = View.GONE

                } else if (y < 0) {
                    MyUtils.hideKeyboard1(mActivity!!)

                    // rootHeaderLayout.visibility=View.VISIBLE
                    (activity as MainActivity).top_sheet.visibility = View.GONE

                } else {

                }
                /*if (feedRecyclerview.SCROLL_STATE_IDLE === newState) {
                    Picasso.get().resumeTag(activity)
                } else {
                    Picasso.get().pauseTag(activity)
                }*/
                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    adapterCustomList?.playAvailableVideos(newState, feedRecyclerview)

                    // fragProductLl.setVisibility(View.VISIBLE);
                    /*if(y<=0){
                        (activity as MainActivity).bottom_navigation.clearAnimation()
                        (activity as MainActivity).bottom_navigation.animate().translationY(0f).duration =
                            200
                        (activity as MainActivity).bottom_navigation.visibility=View.VISIBLE
                    }
                    else{
                        y=0
                        (activity as MainActivity).bottom_navigation.clearAnimation()
                        (activity as MainActivity).bottom_navigation.animate().translationY(
                            resources.getDimension(R.dimen._50sdp))
                            .duration = 200
                        (activity as MainActivity).bottom_navigation.visibility=View.GONE

                    }*/

                    /* if (y <= 0) {
                         (activity as MainActivity).bottom_navigation.animate().translationY(0f)
                             .setInterpolator(LinearInterpolator())
                             .start()
                         (activity as MainActivity).bottom_fab_button.animate().translationY(0f)
                             .setInterpolator(LinearInterpolator())
                             .start()
                         (activity as MainActivity).bottom_fab_button.visibility = View.VISIBLE
                         (activity as MainActivity).bottom_navigation.visibility = View.VISIBLE

                     } else {
                         y = 0
                         (activity as MainActivity).bottom_navigation.animate()
                             .translationY(((activity as MainActivity).bottom_navigation.getHeight()).toFloat())
                             .setInterpolator(
                                 LinearInterpolator()
                             ).start()
                         (activity as MainActivity).bottom_fab_button.animate()
                             .translationY(((activity as MainActivity).bottom_navigation.getHeight()).toFloat())
                             .setInterpolator(LinearInterpolator()).start()

                         (activity as MainActivity).bottom_fab_button.visibility = View.GONE
                         (activity as MainActivity).bottom_navigation.visibility = View.GONE
                         //animation((activity as MainActivity).bottom_navigation)
                     }*/

                    /*if (y > 0 && ! mIsAnimatingOut && (activity as MainActivity).bottom_navigation.visibility == View.VISIBLE &&  (activity as MainActivity).bottom_fab_button.visibility == View.VISIBLE) {
                        // User scrolled down and the FAB is currently visible -> hide the FAB
                        animateOut((activity as MainActivity).bottom_navigation, (activity as MainActivity).bottom_fab_button)
                    } else if (y < 0 && (activity as MainActivity).bottom_navigation.visibility != View.VISIBLE &&  (activity as MainActivity).bottom_fab_button.visibility != View.VISIBLE) {
                        // User scrolled up and the FAB is currently not visible -> show the FAB
                        animateIn((activity as MainActivity).bottom_navigation, (activity as MainActivity).bottom_fab_button)
                    }*/


                    val firstPos = layoutManger?.findFirstVisibleItemPosition()
                    val lastPos = layoutManger?.findLastVisibleItemPosition()

                    if (adapterCustomList != null && globalSearchList!![0].custom?.size!! > 0)
                        setAdapterPosition(firstPos!!, lastPos!!)

                }
            }

        })

    }

    private fun setCustomAdpater() {
        adapterCustomList = FeedListAdapter(
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
                                var profileDetailFragment = ProfileDetailFragment()
                                if (!userData?.userID.equals(
                                        globalSearchList?.get(0)?.custom!!.get(pos)!!.userID,
                                        false
                                    )
                                ) {
                                    var uName = ""
                                    if (!globalSearchList?.get(0)?.custom!!.get(pos)!!.userFirstName.isNullOrEmpty() && !globalSearchList?.get(0)?.custom!!.get(
                                            pos
                                        )!!.userLastName.isNullOrEmpty()
                                    ) {
                                        uName =
                                            globalSearchList?.get(0)?.custom!!.get(pos)!!.userFirstName + " " + globalSearchList?.get(0)?.custom!!.get(
                                                pos
                                            )!!.userLastName
                                    } else if (globalSearchList?.get(0)?.custom!!.get(pos)!!.userFirstName.isNullOrEmpty() && !globalSearchList?.get(0)?.custom!!.get(
                                            pos
                                        )!!.userLastName.isNullOrEmpty()
                                    ) {
                                        uName = "" + globalSearchList?.get(0)?.custom!!.get(pos)!!.userLastName
                                    } else if (!globalSearchList?.get(0)?.custom!!.get(pos)!!.userFirstName.isNullOrEmpty() && globalSearchList?.get(0)?.custom!!.get(
                                            pos
                                        )!!.userLastName.isNullOrEmpty()
                                    ) {
                                        uName = globalSearchList?.get(0)?.custom!!.get(pos)!!.userFirstName + ""
                                    }
                                    Bundle().apply {
                                        putString("userID", globalSearchList?.get(0)?.custom!!.get(pos)!!.userID)
                                        putString("userName", uName)
                                        putSerializable("feedData", globalSearchList?.get(0)?.custom!!.get(pos)!!)
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
                                getUserFollow("follow", globalSearchList?.get(0)?.custom!![pos]!!.userID, pos, objPost)
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
                                    globalSearchList?.get(0)?.custom!![pos]!!.userID,
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
                            // feedShare(v as AppCompatImageView, pos)
                            postData(globalSearchList?.get(0)?.custom!!.get(pos)!!, pos, "Gmail")
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
                                    putSerializable("postData", globalSearchList?.get(0)?.custom!![pos]!!)
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
                                        globalSearchList?.get(0)?.custom!![pos]!!,
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
                                        globalSearchList?.get(0)?.custom!![pos]!!,
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
                                        globalSearchList?.get(0)?.custom!![pos]!!,
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
                                        globalSearchList?.get(0)?.custom!![pos]!!,
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
                                        globalSearchList?.get(0)?.custom!![pos]!!,
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
                    }
                }

            }, "", globalSearchList?.get(0)?.custom!!
        )
        generalRecyclerView?.isNestedScrollingEnabled = false
        generalRecyclerView?.setHasFixedSize(true)
        generalRecyclerView?.adapter = adapterCustomList
    }

    private fun setPeopleAdpater() {

        adapterPeopletList = GlobalSearchPeopledapter(
            mActivity!!,
            object : GlobalSearchPeopledapter.OnItemClick {
                override fun onClicklisneter(pos: Int, reasonID: String) {
                    MyUtils.hideKeyboard1(mActivity!!)
                    var profileDetailFragment = ProfileDetailFragment()
                    if(sessionManager?.isLoggedIn()!!)
                    {
                        if (!sessionManager?.get_Authenticate_User()?.userID.equals(
                                reasonID,
                                false
                            )
                        ) {
                            var uName = ""
                            if (!globalSearchList!![0].people[pos].userFirstName.isNullOrEmpty() && !globalSearchList!![0].people[pos].userLastName.isNullOrEmpty()
                            ) {
                                uName =
                                    globalSearchList!![0].people[pos].userFirstName + " " + globalSearchList!![0].people[pos].userLastName
                            } else if (globalSearchList!![0].people[pos].userFirstName.isNullOrEmpty() && !globalSearchList!![0].people[pos].userLastName.isNullOrEmpty()
                            ) {
                                uName = "" + globalSearchList!![0].people[pos].userLastName
                            } else if (!globalSearchList!![0].people[pos]!!.userFirstName.isNullOrEmpty() && globalSearchList!![0].people[pos].userLastName.isNullOrEmpty()
                            ) {
                                uName = globalSearchList!![0].people[pos].userFirstName + ""
                            }
                            Bundle().apply {
                                putString("userID", reasonID)
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
                    else
                    {
                        MyUtils.startActivity(
                            mActivity!!,
                            LoginActivity::class.java,
                            false
                        )
                    }


                }
            }, globalSearchList!![0].people as java.util.ArrayList<People>
        )
        generalRecyclerView?.isNestedScrollingEnabled = false
        generalRecyclerView?.setHasFixedSize(true)
        generalRecyclerView?.adapter = adapterPeopletList
    }

    private fun setPlaceAdpater() {
       adapterLocationList = GlobalSearchLocationListAdapter(
            mActivity!!,
            object : GlobalSearchLocationListAdapter.OnItemClick {
                override fun onClicklisneter(pos: Int, name: String) {
                    MyUtils.hideKeyboard1(mActivity!!)
                    var locationListFragment = LocationListFragment()
                    Bundle().apply {
                        putString("location", name)
                        locationListFragment.arguments = this
                    }
                    (context as MainActivity).navigateTo(
                        locationListFragment,
                        locationListFragment::class.java.name,
                        true
                    )
                }
            }, "", globalSearchList!![0].places as ArrayList<Place>
        )
        generalRecyclerView?.isNestedScrollingEnabled = false
        generalRecyclerView?.setHasFixedSize(true)
        generalRecyclerView?.adapter = adapterLocationList
    }

    private fun setTagAdpater() {
        adapterHashTagList = GlobalSearchAdapter(
            mActivity!!,
            object : GlobalSearchAdapter.OnItemClick {
                override fun onClicklisneter(pos: Int, reasonID: String) {
                    MyUtils.hideKeyboard1(mActivity!!)
                    var hashTagListFragment = HashTagListFragment()

                    Bundle().apply {
                        putString("hashTag", reasonID)
                        hashTagListFragment.arguments = this
                    }
                    (context as MainActivity).navigateTo(
                        hashTagListFragment,
                        hashTagListFragment::class.java.name,
                        true
                    )
                }
            }, globalSearchList!![0].tags as java.util.ArrayList<Tag>
        )
        generalRecyclerView?.isNestedScrollingEnabled = false
        generalRecyclerView?.setHasFixedSize(true)
        generalRecyclerView?.adapter = adapterHashTagList
    }



    private fun storeSessionManager(driverdata: RegisterPojo.Data?) {
        val gson = Gson()
        val json = gson.toJson(driverdata!!)
        sessionManager?.create_login_session(
            json,
            driverdata!!.userEmail!!,
            "",
            true,
            sessionManager?.isEmailLogin()!!,
            driverdata!!.userFirstName!!+" "+driverdata!!.userLastName!!,
            driverdata!!.userProfilePicture!!
        )
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

        popup.menu.getItem(0).title = "" +GetDynamicStringDictionaryObjectClass?.Report_this_Post
        popup.menu.getItem(1).title = ""+GetDynamicStringDictionaryObjectClass?.Report_Post
        popup.menu.getItem(2).title = ""+GetDynamicStringDictionaryObjectClass?.Hide_Post_For_Me
        popup.menu.getItem(3).title = ""+GetDynamicStringDictionaryObjectClass?.Hide_For_Everyone

        if (!userData?.userID.equals(globalSearchList?.get(0)?.custom!!.get(pos)!!.userID, false)) {

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
                        getRepost(pos, globalSearchList?.get(0)?.custom!!.get(pos)!!)
                    } else {
                        MyUtils.startActivity(mActivity!!, LoginActivity::class.java, false)

                    }
                }
                R.id.settings -> {
                    if (sessionManager?.isLoggedIn()!!) {
                        if (!reportReasonData.isNullOrEmpty()) {
                            showDilaogPostReport(
                                globalSearchList?.get(0)?.custom!!.get(pos)!!.postID,
                                reportReasonData!!,
                                pos
                            )
                        } else {
                            getReasonList(globalSearchList?.get(0)?.custom!!.get(pos)!!.postID, pos)
                        }
                    } else {
                        MyUtils.startActivity(mActivity!!, LoginActivity::class.java, false)

                    }
                }
                R.id.signature_Video -> {
                    if (sessionManager?.isLoggedIn()!!) {
                        getPostHide(globalSearchList?.get(0)?.custom!!.get(pos)?.postID, pos)
                    } else {
                        MyUtils.startActivity(mActivity!!, LoginActivity::class.java, false)

                    }
                }
                R.id.signout -> {
                    if (sessionManager?.isLoggedIn()!!) {
                        getPostInActive(globalSearchList?.get(0)?.custom!!.get(pos)?.postID, pos)
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
            jsonObject.put("originalPostID", trendingFeedData.postID)
            jsonObject.put("originaluserID", trendingFeedData.userID)
            jsonObject.put("posttag",
                if (!trendingFeedData.posttag.isNullOrEmpty()!!) {
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
            ViewModelProviders.of(mActivity!!).get(RepostPostModel::class.java)
        repostPostModel.apiFunction(mActivity!!, false, jsonArray.toString())
            .observe(this@SearchResultListFragment,
                Observer<List<CommonPojo>> { response ->

                    if (!response.isNullOrEmpty()) {
                        MyUtils.closeProgress()
                        if (response[0].status.equals("true", true)) {
                            MyUtils.closeProgress()
                            MyUtils.hideKeyboard1(mActivity!!)

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

    override fun onPause() {
        super.onPause()
        if (adapterCustomList != null) {
            adapterCustomList?.stopPlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (adapterCustomList != null) {
            adapterCustomList?.stopPlayer()
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        if (adapterCustomList != null) {
            if (adapterCustomList?.cache != null) {
                adapterCustomList?.cache?.release()
                adapterCustomList?.cache = null
            }

            adapterCustomList?.stopPlayer()


        }
    }



    fun setPostViewsCount(feedPos: Int) {
        try {
            if (!globalSearchList?.get(0)?.custom!!.get(feedPos)?.isYouViewPost?.contains("Yes")!!) {
                globalSearchList?.get(0)?.custom!!.get(feedPos)?.isYouViewPost = "Yes"
                var count = 0
                try {
                    count = Integer.valueOf(globalSearchList?.get(0)?.custom!!.get(feedPos)!!.postViews) + 1

                } catch (e: Exception) {
                }

                globalSearchList?.get(0)?.custom!!.get(feedPos)!!.postViews = (count.toString())

                adapterCustomList?.notifyItemChanged(feedPos)
                //  broadCastingData(feedlist.get(feedPos))
                val jsonArray = JSONArray()
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("loginuserID", userData?.userID)
                    jsonObject.put("languageID", userData?.languageID)
                    jsonObject.put("userID",globalSearchList?.get(0)?.custom!!.get(feedPos)!!.userID)
                    jsonObject.put("postID", globalSearchList?.get(0)?.custom!!.get(feedPos)!!.postID)
                    jsonObject.put("apiType", RestClient.apiType)
                    jsonObject.put("apiVersion", RestClient.apiVersion)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                jsonArray.put(jsonObject)
                val postView =
                    ViewModelProviders.of(this@SearchResultListFragment).get(PostViewModel::class.java)
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


    private fun getUserFollow(
        s: String,
        userID: String,
        pos: Int,
        objPost: TrendingFeedData
    ) {
        // MyUtils.showProgressDialog(mActivity!!)
        when (s) {
            "follow" -> {
                for (i in 0 until globalSearchList?.get(0)?.custom!!.size!!) {
                    if (globalSearchList?.get(0)?.custom!!.get(i)!!.userID.equals(globalSearchList?.get(0)?.custom!!.get(pos)!!.userID, false)) {
                        globalSearchList?.get(0)?.custom!!.get(i)!!.isYouFollowing = "Yes"
                    }
                }

                adapterCustomList?.notifyDataSetChanged()

            }
            "unfollow" -> {
                for (i in 0 until globalSearchList?.get(0)?.custom!!.size!!) {
                    if (globalSearchList?.get(0)?.custom!!.get(i)!!.userID.equals(globalSearchList?.get(0)?.custom!!.get(pos)!!.userID, false)) {
                        globalSearchList?.get(0)?.custom!!.get(i)!!.isYouFollowing = "No"
                    }
                }
                adapterCustomList?.notifyDataSetChanged()
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

        var getFollowModel = ViewModelProviders.of(this@SearchResultListFragment).get(FollowModel::class.java)
        getFollowModel.apiCall(mActivity!!, jsonArray.toString(), s).observe(this@SearchResultListFragment,
            Observer
            { trendingFeedDatum ->
                if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                    // MyUtils.closeProgress()
                    if (trendingFeedDatum[0].status?.equals("true", false)!!) {

                    } else {
                        for (i in 0 until globalSearchList?.get(0)?.custom!!.size!!) {
                            if (globalSearchList?.get(0)?.custom!!.get(i)?.postID.equals(
                                    objPost.postID
                                )
                            ) {
                                globalSearchList?.get(0)?.custom!!.set(i, objPost)
                                adapterCustomList?.notifyDataSetChanged()
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
                        count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) + 1

                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())

                if( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals(s))
                {
                    if( globalSearchList?.get(0)?.custom!![feedPosition]!!.postHaha.toInt()>0)
                    {
                        feedDatum.postHaha = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postHaha.toInt() - 1).toString()
                        feedDatum.postHaha = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postHaha.toInt() + 1).toString()
                    }
                    else
                    {
                        feedDatum.postHaha = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postHaha.toInt() + 1).toString()
                    }
                }
                else
                {
                    feedDatum.postHaha = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postHaha.toInt() + 1).toString()
                }


                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Angry",false)&&feedDatum.postAngry.toInt() > 0)
                {
                    feedDatum.postAngry =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAngry.toInt() - 1).toString()
                }
                /*else
                {
                    feedDatum.postAngry = "0"
                }*/

                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Sad",false)&&feedDatum.postSad.toInt() > 0)
                {
                    feedDatum.postSad = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postSad.toInt() - 1).toString()
                }
                /*else
                {
                    feedDatum.postSad = "0"
                }*/

                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Like")&&feedDatum.postLike.toInt() > 0)
                {
                    feedDatum.postLike =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postLike.toInt() - 1).toString()
                }
                /*else {
                    feedDatum.postLike = "0"
                }*/
                action = "LikePost"
                adapterCustomList?.notifyItemChanged(feedPosition)
            }
            "Sad" -> {

                var count = 0
                if (feedDatum.IsLiekedByYou.equals("No", false)) {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) + 1

                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())
                if( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals(s))
                {
                    if( globalSearchList?.get(0)?.custom!![feedPosition]!!.postSad.toInt()>0)
                    {
                        feedDatum.postSad = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postSad.toInt() - 1).toString()
                        feedDatum.postSad = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postSad.toInt() + 1).toString()
                    }
                    else
                    {
                        feedDatum.postSad = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postSad.toInt() + 1).toString()
                    }
                }
                else
                {
                    feedDatum.postSad = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postSad.toInt() + 1).toString()
                }
                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Angry",false)&& feedDatum.postAngry.toInt() > 0) {
                    feedDatum.postAngry =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAngry.toInt() - 1).toString()
                } /*else {
                    feedDatum.postAngry = "0"
                }*/

                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Laugh",false)&&feedDatum.postHaha.toInt() > 0) {
                    feedDatum.postHaha =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postHaha.toInt() - 1).toString()
                } /*else {
                    feedDatum.postHaha = "0"
                }*/

                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Like",false)&&feedDatum.postLike.toInt() > 0) {
                    feedDatum.postLike =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postLike.toInt() - 1).toString()
                } /*else {
                    feedDatum.postLike = "0"
                }*/


                action = "LikePost"
                adapterCustomList?.notifyItemChanged(feedPosition)
            }
            "Angry" -> {
                var count = 0
                if (feedDatum.IsLiekedByYou.equals("No", false)) {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) + 1

                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())
                if( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals(s))
                {
                    if( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAngry.toInt()>0)
                    {
                        feedDatum.postAngry = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAngry.toInt() - 1).toString()
                        feedDatum.postAngry = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAngry.toInt() + 1).toString()
                    }
                    else
                    {
                        feedDatum.postAngry = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAngry.toInt() + 1).toString()
                    }
                }
                else
                {
                    feedDatum.postAngry = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAngry.toInt() + 1).toString()
                }

                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Sad",false)&&feedDatum.postSad.toInt() > 0) {
                    feedDatum.postSad = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postSad.toInt() - 1).toString()
                } /*else {
                    feedDatum.postSad = "0"
                }*/

                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Laugh",false)&&feedDatum.postHaha.toInt() > 0) {
                    feedDatum.postHaha =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postHaha.toInt() - 1).toString()
                } /*else {
                    feedDatum.postHaha = "0"
                }*/

                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Like",false)&&feedDatum.postLike.toInt() > 0) {
                    feedDatum.postLike =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postLike.toInt() - 1).toString()
                }/* else {
                    feedDatum.postLike = "0"
                }*/
                action = "LikePost"
                adapterCustomList?.notifyItemChanged(feedPosition)
            }
            "Unlike" -> {
                feedDatum.IsLiekedByYou = ("No")
                var count = 0
                try {
                    count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) - 1


                } catch (e: java.lang.Exception) {
                }
                feedDatum.postAll = (count.toString())

                if (feedDatum.postLike.toInt() > 0) {
                    feedDatum.postLike =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postLike.toInt() - 1).toString()
                } /*else {
                    feedDatum.postSad = "0"
                }*/
                if (feedDatum.postSad.toInt() > 0) {
                    feedDatum.postSad = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postSad.toInt() - 1).toString()
                } else {
                    feedDatum.postSad = "0"
                }

                if (feedDatum.postHaha.toInt() > 0) {
                    feedDatum.postHaha =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postHaha.toInt() - 1).toString()
                } /*else {
                    feedDatum.postHaha = "0"
                }*/

                if (feedDatum.postAngry.toInt() > 0) {
                    feedDatum.postAngry =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAngry.toInt() - 1).toString()
                } /*else {
                    feedDatum.postAngry = "0"
                }*/
                action = "UnLikePost"
                 adapterCustomList?.notifyItemChanged(feedPosition)
            }
            "Like" -> {

                var count = 0
                if (feedDatum.IsLiekedByYou.equals("No", false)) {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) + 1
                    } catch (e: java.lang.Exception) {
                    }
                } else {
                    feedDatum.IsLiekedByYou = ("Yes")
                    try {
                        if (feedDatum.postAll.toInt() >= 1) {
                            count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) - 1
                            count += 1
                        } else {
                            count = Integer.valueOf( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAll) + 1
                        }
                    } catch (e: java.lang.Exception) {

                    }
                }
                feedDatum.postAll = (count.toString())
                if( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals(s))
                {
                    if( globalSearchList?.get(0)?.custom!![feedPosition]!!.postLike.toInt()>0)
                    {
                        feedDatum.postLike = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postLike.toInt() - 1).toString()
                        feedDatum.postLike = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postLike.toInt() + 1).toString()
                    }
                    else
                    {
                        feedDatum.postLike = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postLike.toInt() + 1).toString()
                    }
                }
                else
                {
                    feedDatum.postLike = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postLike.toInt() + 1).toString()
                }
                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Sad",false)&&feedDatum.postSad.toInt() > 0) {
                    feedDatum.postSad = ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postSad.toInt() - 1).toString()
                } /*else {
                    feedDatum.postSad = "0"
                }*/

                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Laugh",false)&&feedDatum.postHaha.toInt() > 0) {
                    feedDatum.postHaha =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postHaha.toInt() - 1).toString()
                } /*else {
                    feedDatum.postHaha = "0"
                }*/

                if ( globalSearchList?.get(0)?.custom!![feedPosition]!!.LikeReaction.equals("Angry",false)&&feedDatum.postAngry.toInt() > 0) {
                    feedDatum.postAngry =
                        ( globalSearchList?.get(0)?.custom!![feedPosition]!!.postAngry.toInt() - 1).toString()
                } /*else {
                    feedDatum.postAngry = "0"
                }*/
                action = "LikePost"
                 adapterCustomList?.notifyItemChanged(feedPosition)

            }
        }
        val json = Gson().toJson(globalSearchList?.get(0)?.custom!!.get(feedPosition))
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
            jsonObject.put("posterID",globalSearchList?.get(0)?.custom!!.get(pos)!!.userID)
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
            ViewModelProviders.of(this@SearchResultListFragment).get(PostViewModel::class.java)

        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@SearchResultListFragment,
            Observer<List<CommonPojo?>?> {
                if (mActivity != null) {
                    if (it!![0]!!.status.equals("true", false)) {

                        if (action.equals("LikePost", false)) {
                            globalSearchList?.get(0)?.custom!!.get(pos)!!.IsLiekedByYou = "Yes"
                            globalSearchList?.get(0)?.custom!!.get(pos)!!.LikeReaction = s
                        } else if (action.equals("UnLikePost", false)) {
                            globalSearchList?.get(0)?.custom!!.get(pos)!!.IsLiekedByYou = "No"
                            globalSearchList?.get(0)?.custom!!.get(pos)!!.LikeReaction = s
                        }
                        adapterCustomList?.notifyItemChanged(pos)
                    } else {
                        for (i in 0 until globalSearchList?.get(0)?.custom!!.size!!) {
                            if (globalSearchList?.get(0)?.custom!!.get(i)?.postID.equals(trendingFeedDatum.postID)) {
                                globalSearchList?.get(0)?.custom!!.set(i, trendingFeedDatum)
                                adapterCustomList?.notifyDataSetChanged()
                                break
                            }
                        }
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
            ViewModelProviders.of((this@SearchResultListFragment)).get(
                PostFavouriteModel::class.java
            )
        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@SearchResultListFragment,
            Observer { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos?.get(0)?.status.equals("true", false)) {
                    } else {
                        for (i in 0 until globalSearchList?.get(0)?.custom!!.size!!) {
                            if (globalSearchList?.get(0)?.custom!!.get(i)?.postID.equals(
                                    trendingFeedDatum.postID
                                )
                            ) {
                                globalSearchList?.get(0)?.custom!!.set(i, trendingFeedDatum)
                                adapterCustomList?.notifyDataSetChanged()
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
        if (globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.IsYourFavorite.equals("No", false)) {
            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_red))
            var animation = AnimationUtils.loadAnimation(mActivity, R.anim.bounce)
            favouriteimageview.startAnimation(animation)
            action = "FavouritePost"
            globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.IsYourFavorite = ("Yes")
        } else {
            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.favourite_icon_grey))
            action = "UnFavouritePost"
            globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.IsYourFavorite = ("No")
        }
        val json = Gson().toJson(globalSearchList?.get(0)?.custom!!.get(feedPosition))
        val trendingFeedDatum =
            Gson().fromJson(json, TrendingFeedData::class.java)
        setPostFavourite(
            globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.postID,
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

        var linearLayoutManager1 = LinearLayoutManager(mActivity!!)
        postReportListAdapter = PostReportListAdapter(
            mActivity!!,
            object : PostReportListAdapter.OnItemClick {
                override fun onClicklisneter(pos: Int, name: String, v: View, reasonid: String) {
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

        val apiCall = ViewModelProviders.of(this@SearchResultListFragment).get(ReasonModel::class.java)
        apiCall.apiFunction(mActivity!!, true, jsonArray.toString()).observe(this@SearchResultListFragment,
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
            jsonObject.put("posterID",globalSearchList?.get(0)?.custom!!.get(pos)?.userID)
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var reportPostModel =
            ViewModelProviders.of(this@SearchResultListFragment).get(ReportPostModel::class.java)

        reportPostModel.apiFunction(mActivity!!, jsonArray.toString(), "PostHide")
            .observe(this@SearchResultListFragment, Observer { it ->
                //            MyUtils.closeProgress()

                if (it != null && it.isNotEmpty()) {
                    MyUtils.closeProgress()

                    if (it[0].status.equals("true", true)) {
                        (activity as MainActivity).showSnackBar(it[0].message!!)
                        for (i in 0 until globalSearchList?.get(0)?.custom!!.size!!) {

                            if (globalSearchList?.get(0)?.custom!!.get(i)?.postID.equals(postID, false)) {
                                globalSearchList?.get(0)?.custom!!.removeAt(i)
                                adapterCustomList?.notifyItemRemoved(globalSearchList?.get(0)?.custom!!.size)
                                adapterCustomList?.notifyDataSetChanged()
                                if (globalSearchList?.get(0)?.custom!!.size == 0) {
                                    isLastPage1 = true
                                }
                                break
                            }
                        }

                        if (isLastPage1) {
                            globalSearchList?.get(0)?.custom!!.clear()
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
            jsonObject.put("posterID",globalSearchList?.get(0)?.custom!!.get(pos)?.userID)
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var reportPostModel =
            ViewModelProviders.of(this@SearchResultListFragment).get(ReportPostModel::class.java)

        reportPostModel.apiFunction(mActivity!!, jsonArray.toString(), "PostInActive")
            .observe(this@SearchResultListFragment, Observer { it ->
                //MyUtils.closeProgress()

                if (it != null && it.isNotEmpty()) {
                    MyUtils.closeProgress()

                    if (it[0].status.equals("true", true)) {
                        (activity as MainActivity).showSnackBar(it[0].message!!)
                        for (i in 0 until globalSearchList?.get(0)?.custom!!.size!!) {

                            if (globalSearchList?.get(0)?.custom!!.get(i)?.postID.equals(postID, false)) {
                                globalSearchList?.get(0)?.custom!!.removeAt(i)
                                adapterCustomList?.notifyItemRemoved(globalSearchList?.get(0)?.custom!!.size)
                                adapterCustomList?.notifyDataSetChanged()
                                if (globalSearchList?.get(0)?.custom!!.size == 0) {
                                    isLastPage1 = true
                                }
                                break
                            }
                        }

                        if (isLastPage1) {
                            globalSearchList?.get(0)?.custom!!.clear()
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
            jsonObject.put("posterID",globalSearchList?.get(0)?.custom!!.get(pos)?.userID)
            jsonObject.put("reasonID", postreportreasonID)
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var reportPostModel =
            ViewModelProviders.of(this@SearchResultListFragment).get(ReportPostModel::class.java)

        reportPostModel.apiFunction(mActivity!!, jsonArray.toString(), "PostReport")
            .observe(this@SearchResultListFragment, Observer { it ->
                //            MyUtils.closeProgress()

                if (it != null && it.isNotEmpty()) {
                    MyUtils.closeProgress()

                    if (it[0].status.equals("true", true)) {
                        for (i in 0 until globalSearchList?.get(0)?.custom!!.size!!) {

                            if (globalSearchList?.get(0)?.custom!!.get(i)?.postID.equals(postID, false)) {
                                globalSearchList?.get(0)?.custom!!.removeAt(i)
                                adapterCustomList?.notifyItemRemoved(globalSearchList?.get(0)?.custom!!.size)
                                adapterCustomList?.notifyDataSetChanged()
                                if (globalSearchList?.get(0)?.custom!!.size == 0) {
                                    isLastPage1 = true
                                }
                                break
                            }
                        }

                        if (isLastPage1) {
                            globalSearchList?.get(0)?.custom!!.clear()
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
        if (globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.postMediaType.contains("Photo") || globalSearchList?.get(0)?.custom!!?.get(
                feedPosition
            )!!.postMediaType.contains("Video")
        )
            downloadList.add(
                globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.postSerializedData[0].albummedia.get(0).albummediaFile
            )
        val sharingBottomSheetFragment = SharingMediaSheetFragment()
        val bundle = Bundle()
        bundle.putString("mediaType", globalSearchList?.get(0)?.custom!!.get(feedPosition)?.postMediaType)
        if (downloadList.size > 0) bundle.putString(
            "postid",
            globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.postID
        )
        bundle.putString("from", "Post")
        bundle.putString("mediaList", Gson().toJson(downloadList))
        bundle.putString(
            "shareText",
            MyUtils.decodeShareText(
                globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.postDescription.toString(),
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
                        globalSearchList?.get(0)?.custom!!.get(feedPosition)!!,
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
        if (globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.IsClappeddByYou.equals("No", false)) {

            for (i in 0 until globalSearchList?.get(0)?.custom!!.size!!) {
                if (globalSearchList?.get(0)?.custom!!?.get(i)!!.userID.equals(globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.userID, false)) {
                    globalSearchList?.get(0)?.custom!!.get(i)!!.IsClappeddByYou = "Yes"
                    globalSearchList?.get(0)?.custom!!.get(i)!!.userClapCount =
                        (globalSearchList?.get(0)?.custom!!.get(i)!!.userClapCount?.toInt()!! + 1).toString()
                }
            }

            adapterCustomList?.notifyDataSetChanged()
            //  favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.clap_icon_filled_enabled))
            action = "ClapPost"
            globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.IsClappeddByYou = ("Yes")

        } else {
            //favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.clap_icon_border_disabled))
            for (i in 0 until globalSearchList?.get(0)?.custom!!.size!!) {
                if (globalSearchList?.get(0)?.custom!!.get(i)!!.userID.equals(globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.userID, false)) {
                    if (globalSearchList?.get(0)?.custom!!.get(i)!!.userID.equals(
                            globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.userID,
                            false
                        )
                    ) {
                        globalSearchList?.get(0)?.custom!!.get(i)!!.IsClappeddByYou = "No"
                        if (globalSearchList?.get(0)?.custom!!.get(i)!!.userClapCount!!.toInt() > 1) {
                            globalSearchList?.get(0)?.custom!!.get(i)!!.userClapCount =
                                (globalSearchList?.get(0)?.custom!!.get(i)!!.userClapCount?.toInt()!! - 1).toString()
                        } else {
                            globalSearchList?.get(0)?.custom!!.get(i)!!.userClapCount = "0"
                        }
                    }
                }
            }
            adapterCustomList?.notifyDataSetChanged()
            action = "UnClapPost"
            globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.IsClappeddByYou = ("No")
        }
        val json = Gson().toJson(globalSearchList?.get(0)?.custom!!.get(feedPosition))
        val trendingFeedDatum =
            Gson().fromJson(json, TrendingFeedData::class.java)
        setPostClap(globalSearchList?.get(0)?.custom!!.get(feedPosition)!!.postID, action, feedPosition, trendingFeedDatum)

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
            jsonObject.put("userID",globalSearchList?.get(0)?.custom!!.get(pos)!!.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        val postLike: PostClapModel =
            ViewModelProviders.of((this@SearchResultListFragment)).get(
                PostClapModel::class.java
            )
        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@SearchResultListFragment,
            Observer { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos!![0].status.equals("true", false)) {

                    } else {
                        for (i in 0 until globalSearchList?.get(0)?.custom!!.size!!) {
                            if (globalSearchList?.get(0)?.custom!!.get(i)?.userID.equals(trendingFeedDatum.userID)
                            ) {
                                globalSearchList?.get(0)?.custom!!.set(i, trendingFeedDatum)
                                adapterCustomList?.notifyDataSetChanged()
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

                        adapterCustomList?.notifyDataSetChanged()

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
            jsonObject.put("postID", feedDatum.postID)
            //}
            if(sessionManager?.isLoggedIn()!!)
            {
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put("languageID", userData?.languageID)

            }
            else
            {
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
                        adapterCustomList?.notifyItemChanged(pos)

                        val shareIntent = Intent()
                        shareIntent.action = Intent.ACTION_SEND
                        shareIntent.setType("text/plain")
                        shareIntent.putExtra(
                            Intent.EXTRA_TEXT,
                            feedDatum.postDescription + "\n\nwww.google.com"
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

    fun notifyData(feedDatum: TrendingFeedData, isDelete: Boolean,isDeleteComment:Boolean) {

        if (isDelete) {
            for (i in globalSearchList?.get(0)?.custom!!.indices!!) {

                if (globalSearchList?.get(0)?.custom!!.get(i)?.userID.equals(feedDatum.userID, false)) {
                    globalSearchList?.get(0)?.custom!!.removeAt(i)
                    adapterCustomList?.notifyItemRemoved(globalSearchList?.get(0)?.custom!!.size)
                    adapterCustomList?.notifyDataSetChanged()
                }
            }
        } else {
            for (i in globalSearchList?.get(0)?.custom!!.indices!!) {
                if (globalSearchList?.get(0)?.custom!!.get(i) != null && globalSearchList?.get(0)?.custom!!.get(i)!!.postID.equals(
                        feedDatum.postID,
                        false
                    )
                ) {
                    globalSearchList?.get(0)?.custom!!.set(i, feedDatum)
                    //adapter.broadCastingData(feedDatum);
                    adapterCustomList?.notifyItemChanged(i)
                    break
                }
            }
        }

    }

    fun setAdapterPosition(firstPos: Int, lastPos: Int) {
        try {
            if (sessionManager != null && sessionManager?.isLoggedIn()!!) {
                var userId = userData?.userID
                for (i in firstPos..lastPos) {
                    if (globalSearchList?.get(0)?.custom?.size!! > 0 && globalSearchList?.get(0)?.custom?.get(i) != null) {

                        if (globalSearchList?.get(0)?.custom?.get(firstPos)!!.postMediaType.contains("Photo") && globalSearchList?.get(0)?.custom?.get(
                                firstPos
                            )!!.postSerializedData.size > 0 && globalSearchList?.get(0)?.custom?.get(firstPos)!!.postSerializedData.get(
                                0
                            ).albummedia.size === 1
                        ) {
                            setPostViewsCount(i)
                        }
                        if (globalSearchList?.get(0)?.custom?.get(firstPos)!!.postMediaType.contains("Video") && globalSearchList?.get(0)?.custom?.get(
                                firstPos
                            )!!.postSerializedData.isNotEmpty() && globalSearchList?.get(0)?.custom?.get(firstPos)!!.postSerializedData.get(
                                0
                            ).albummedia.size === 1
                        ) {
                            setPostViewsCount(i)
                        }

                    }
                }
            }
        } catch (e: Exception) {

        }


    }
}