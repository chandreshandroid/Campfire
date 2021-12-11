package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.FollowingFollowersUserListAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.FollowModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.FollowData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.fragment_followes_following.*
import kotlinx.android.synthetic.main.generallistlayout.*
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class FollowesFollowingFragment : Fragment() {

    var v: View? = null
    var mActivity: Activity? = null

    private var tabposition: Int = 0
    var followingFollowersUserListAdapter: FollowingFollowersUserListAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var pageNo = 0
    var sessionManager: SessionManager? = null
    var searchhint = ""
    var followData: ArrayList<FollowData?>? = null
    var userData: RegisterPojo.Data? = null
    var type: String = ""
    var searchKeyWord: String = ""
    var from: String = ""
    var noDatafoundRelativelayout: RelativeLayout? = null
    var nointernetMainRelativelayout: RelativeLayout? = null
    var relativeprogressBar: RelativeLayout? = null
    var userId=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_followes_following, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null)
        {
            tabposition = arguments?.getInt("position", -1)!!
            type = arguments?.getString("type")!!
            userId = arguments?.getString("userId")!!
        }

        noDatafoundRelativelayout =
            v?.findViewById(R.id.noDatafoundRelativelayout) as RelativeLayout
        nointernetMainRelativelayout =
            v?.findViewById(R.id.nointernetMainRelativelayout) as RelativeLayout
        relativeprogressBar = v?.findViewById(R.id.relativeprogressBar) as RelativeLayout

        dynamicString()
        sessionManager = SessionManager(mActivity!!)
        userData = sessionManager?.get_Authenticate_User()

        editSearch.hint = searchhint



        noDatafoundRelativelayout?.visibility = View.GONE
        nointernetMainRelativelayout?.visibility = View.GONE
        relativeprogressBar?.visibility = View.GONE



        linearLayoutManager = LinearLayoutManager(mActivity!!)

        if (followData == null) {
            followData = ArrayList()
            followData?.clear()
            followingFollowersUserListAdapter = FollowingFollowersUserListAdapter(
                mActivity!!,
                object : FollowingFollowersUserListAdapter.OnItemClick {
                    override fun onClicklisneter(pos: Int, name: String, v: View) {
                        when (name) {
                            "follow" -> {
                                getUserFollow("follow", followData!![pos]!!.userID!!, pos)
                            }
                            "following" -> {
                                getUserFollow("unfollow", followData!![pos]!!.userID!!, pos)
                            }
                            "userProfile" -> {
                                editSearch.tag = false
                                var profileDetailFragment = ProfileDetailFragment()

                                if (!sessionManager?.get_Authenticate_User()?.userID.equals(
                                        followData!![pos]!!.userID,
                                        false
                                    )
                                ) {
                                    var uName = ""
                                    if (!followData!![pos]!!.userFirstName.isNullOrEmpty() && !followData!![pos]!!.userLastName.isNullOrEmpty()
                                    ) {
                                        uName =
                                            followData!![pos]!!.userFirstName + " " + followData!![pos]!!.userLastName
                                    } else if (followData!![pos]!!.userFirstName.isNullOrEmpty() && !followData!![pos]!!.userLastName.isNullOrEmpty()
                                    ) {
                                        uName = "" + followData!![pos]!!.userLastName
                                    } else if (!followData!![pos]!!.userFirstName.isNullOrEmpty() && followData!![pos]!!.userLastName.isNullOrEmpty()
                                    ) {
                                        uName = followData!![pos]!!.userFirstName + ""
                                    }
                                    Bundle().apply {
                                        putString("userID", followData!![pos]!!.userID)
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
                        }
                    }

                }, "", tabposition, followData!!
            )
            generalRecyclerView.layoutManager = linearLayoutManager
            generalRecyclerView.adapter = followingFollowersUserListAdapter
            getFollowingList(searchKeyWord)
            //userVisibleHint = true
        }


        editSearch.tag = false


        editSearch.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->

            editSearch.tag = true


        }


        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable) {
                if (editSearch.isPressed) {
                    return
                }
                if (s.trim().isNotEmpty() && s.toString().trim().length > 1 && editSearch.tag as Boolean) {
                    img_clear_edit_text.visibility = View.VISIBLE
                    generalRecyclerView.visibility = View.VISIBLE
                    searchKeyWord = s.toString()
                    pageNo = 0
                    if (!searchKeyWord.isNullOrEmpty())
                        getFollowingList(searchKeyWord)
                } else if (editSearch.tag as Boolean && s.trim().isNullOrEmpty()) {
                    img_clear_edit_text.visibility = View.GONE
                    generalRecyclerView.visibility = View.VISIBLE
                    pageNo = 0
                    isLastpage=false
                    searchKeyWord=""
                    followData?.clear()
                    getFollowingList("")
                }
            }


        })
        img_clear_edit_text.setOnClickListener {
            editSearch.setText("")
        }

        generalRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = linearLayoutManager?.childCount!!
                totalItemCount = linearLayoutManager?.itemCount!!
                firstVisibleItemPosition = linearLayoutManager?.findFirstVisibleItemPosition()!!
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10
                    ) {
                        isLoading = true
                        getFollowingList(searchKeyWord)
                    }
                }
            }
        })
    }


    private fun getUserFollow(s: String, userID: String, pos: Int) {
        MyUtils.showProgressDialog(mActivity!!)
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
            ViewModelProviders.of(this@FollowesFollowingFragment).get(FollowModel::class.java)
        getFollowModel.apiCall(mActivity!!, jsonArray.toString(), s)
            .observe(this@FollowesFollowingFragment,
                Observer
                { trendingFeedDatum ->
                    if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                        MyUtils.closeProgress()
                        if (trendingFeedDatum[0].status?.equals("true")!!) {
                            when (s) {
                                "follow" -> {
                                    if(userData?.userID.equals(userId))
                                    {
                                        followData?.get(pos)!!.isYouFollowing = "Yes"
                                        followingFollowersUserListAdapter?.notifyDataSetChanged()

                                        var size: Int = 0
                                        (parentFragment as MyProfileFollowingFollowersFragment?)?.setTabtitle(
                                            "${size + 1}", 1, " Following", "follow"
                                        )
                                    }else{
                                        followData?.get(pos)!!.isYouFollowing = "Yes"
                                        followingFollowersUserListAdapter?.notifyDataSetChanged()
                                    }
                                }
                                "unfollow" -> {
                                    if (tabposition == 1) {
                                        if(userData?.userID.equals(userId))
                                        {
                                            followData?.removeAt(pos)
                                            followingFollowersUserListAdapter?.notifyItemRemoved(
                                                followData!!.size
                                            )
                                            followingFollowersUserListAdapter?.notifyDataSetChanged()
                                            (parentFragment as MyProfileFollowingFollowersFragment?)?.setTabtitle(
                                                followData?.size.toString(),
                                                1,
                                                " Following"
                                                , "unfollow"
                                            )
                                        }
                                        else
                                        {
                                            followData?.get(pos)!!.isYouFollowing = "No"
                                            followingFollowersUserListAdapter?.notifyDataSetChanged()
                                        }



                                        //parentFrag.viewPagerAdapter?.notifyDataSetChanged()

                                    } else {
                                        if(userData?.userID.equals(userId)) {
                                            followData?.get(pos)!!.isYouFollowing = "No"
                                            followingFollowersUserListAdapter?.notifyDataSetChanged()
                                            var size: Int = 0
                                            (parentFragment as MyProfileFollowingFollowersFragment?)?.setTabtitle(
                                                "$size-1", 1, " Following", "unfollow"
                                            )
                                        }else{
                                            followData?.get(pos)!!.isYouFollowing = "No"
                                            followingFollowersUserListAdapter?.notifyDataSetChanged()
                                        }
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

    private fun dynamicString() {
        when (tabposition) {
            0 -> {
//                searchhint = mActivity?.resources?.getString(R.string.search_followers)!!
                searchhint = GetDynamicStringDictionaryObjectClass?.Search_Followers


            }
            1 -> {
//                searchhint = mActivity?.resources?.getString(R.string.search_following)!!
                searchhint = GetDynamicStringDictionaryObjectClass?.Search_Following

            }
        }
    }


    private fun getFollowingList(searchKeyWord: String) {
        noDatafoundRelativelayout?.visibility = View.GONE
        nointernetMainRelativelayout?.visibility = View.GONE

        if (pageNo == 0) {
            relativeprogressBar?.visibility = View.VISIBLE
            followData!!.clear()
            followingFollowersUserListAdapter?.notifyDataSetChanged()
        } else {
            relativeprogressBar?.visibility = View.GONE
            generalRecyclerView.visibility = (View.VISIBLE)
            followData!!.add(null)
            followingFollowersUserListAdapter?.notifyItemInserted(followData!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("searchWord", searchKeyWord)
            jsonObject.put("userID", userId)
            jsonObject.put("action", type)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var getFollowModel =
            ViewModelProviders.of(this@FollowesFollowingFragment).get(FollowModel::class.java)
        getFollowModel.apiCall(mActivity!!, jsonArray.toString(), type)
            .observe(this@FollowesFollowingFragment,
                Observer
                { trendingFeedDatum ->
                    if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                        isLoading = false
                        //   remove progress item
                        noDatafoundRelativelayout?.visibility = View.GONE
                        nointernetMainRelativelayout?.visibility = View.GONE
                        relativeprogressBar?.visibility = View.GONE

                        if (pageNo > 0) {
                            followData!!.removeAt(followData!!.size - 1)
                            followingFollowersUserListAdapter?.notifyItemRemoved(followData!!.size)
                        }
                        if (trendingFeedDatum[0].status?.equals("true")!!) {

                            if (pageNo == 0)
                                followData!!.clear()

                            followData!!.addAll(trendingFeedDatum[0].data!!)
                            generalRecyclerView.visibility = View.VISIBLE

                            followingFollowersUserListAdapter?.notifyDataSetChanged()

                            if (trendingFeedDatum[0].data!!.size < 10) {
                                isLastpage = true
                            }else{
                                pageNo += 1
                            }

                            val intent1 = Intent("Follow")
                            intent1.putExtra("POSITION", tabposition)
                            intent1.putExtra("COUNT", followData?.size)
                            LocalBroadcastManager.getInstance(mActivity!!)
                                .sendBroadcast(intent1)

                        } else {
                            relativeprogressBar?.visibility = View.GONE

                            if (followData!!.size == 0) {
                                noDatafoundRelativelayout?.visibility = View.VISIBLE
                                generalRecyclerView.visibility = View.GONE
                                nodatafoundtextview.visibility = View.GONE

                            } else {
                                noDatafoundRelativelayout?.visibility = View.GONE
                                generalRecyclerView.visibility = View.VISIBLE

                            }
                        }

                    } else {
                        if (activity != null) {
                            relativeprogressBar?.visibility = View.GONE
                            generalRecyclerView.visibility = (View.GONE)
                            try {
                                nointernetMainRelativelayout?.visibility = View.VISIBLE
                                if (MyUtils.isInternetAvailable(activity!!)) {
                                    nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.something_went_wrong))
                                    nointernettextview.text =
                                        (GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
                                    nointernettextview1.text =
                                        (GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
                                    nointernettextview.visibility=View.GONE
                                } else {
                                    nointernettextview.visibility=View.VISIBLE
                                    nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.no_internet_connection))
                                    nointernettextview1.text =
                                        (GetDynamicStringDictionaryObjectClass?.No_Internet_Connection)
                                    nointernettextview.text =
                                        (GetDynamicStringDictionaryObjectClass?.Please_check_your_internet_connectivity_and_try_again)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }
                })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser)
            if (mActivity != null) {
                pageNo = 0
                followData?.clear()
                searchKeyWord=""
                getFollowingList(searchKeyWord)
            }

        // super.setUserVisibleHint(isVisibleToUser)
    }


}
