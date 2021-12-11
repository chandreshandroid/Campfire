package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.PostReactUserListAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.FollowModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.PostLikeListModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.PostLikeListData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.generallistlayout.*
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ReactUserListFragment : Fragment() {

    var v: View? = null
    var mActivity: Activity? = null
    private var tabposition: Int = 0
    var postReactUserListAdapter: PostReactUserListAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var pageNo = 0
    var sessionManager: SessionManager? = null

    var postLikeList: ArrayList<PostLikeListData?>? = null
    var userData: RegisterPojo.Data? = null
    var type: String = ""
    var postId: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_react_user_list, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mActivity = context as Activity
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(mActivity!!)
        userData = sessionManager?.get_Authenticate_User()

        if (arguments != null) {
            tabposition = arguments?.getInt("position", 0)!!
            type = arguments?.getString("type")!!
            postId = arguments?.getString("postId")!!
        }

        nodata1?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
        nodatafoundtextview?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
        btnRetry?.text = GetDynamicStringDictionaryObjectClass?.Retry
        noDatafoundRelativelayout.visibility = View.GONE
        relativeprogressBar.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        linearLayoutManager = LinearLayoutManager(mActivity!!)
        //   if (postLikeList == null) {
        postLikeList = ArrayList()
        postLikeList?.clear()

        postReactUserListAdapter = PostReactUserListAdapter(
            mActivity!!,
            object : PostReactUserListAdapter.OnItemClick {
                override fun onClicklisneter(pos: Int, name: String, v: View) {
                    when (name) {
                        "Follow" -> {
                            getUserFollow("follow", postLikeList!![pos]!!.userID, pos)
                        }
                        "Following" -> {
                            getUserFollow("unfollow", postLikeList!![pos]!!.userID, pos)
                        }
                    }
                }

            }, "", tabposition, postLikeList!!
        )
        generalRecyclerView.layoutManager = linearLayoutManager
        generalRecyclerView.adapter = postReactUserListAdapter
        postLikeListList(type)

        // }


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
                        postLikeListList(type)
                    }
                }
            }
        })
        btnRetry?.text = GetDynamicStringDictionaryObjectClass?.Retry
        btnRetry.setOnClickListener {
            pageNo = 0
            postLikeListList(type)
        }

    }

    private fun postLikeListList(type: String) {
        noDatafoundRelativelayout.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            postLikeList?.clear()
            postReactUserListAdapter?.notifyDataSetChanged()
        } else {
            relativeprogressBar.visibility = View.GONE
            generalRecyclerView.visibility = (View.VISIBLE)
            postLikeList?.add(null)
            postReactUserListAdapter?.notifyItemInserted(postLikeList!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("postlikeType", type)
            jsonObject.put("postID", postId)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var getPostLikeListModel =
            ViewModelProviders.of(this@ReactUserListFragment).get(PostLikeListModel::class.java)
        getPostLikeListModel.apiCall(mActivity!!, jsonArray.toString(), type)
            .observe(this@ReactUserListFragment,
                Observer {
                    if (it != null && it.isNotEmpty()) {
                        isLoading = false
                        //   remove progress item
                        noDatafoundRelativelayout.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE
                        if (pageNo > 0) {
                            postLikeList!!.removeAt(postLikeList!!.size - 1)
                            postReactUserListAdapter?.notifyItemRemoved(postLikeList!!.size)
                        }
                        if (it[0].status?.equals("true")!!) {

                            if (pageNo == 0) {
                                postLikeList!!.clear()
                              if(!it[0].countArray.isNullOrEmpty())
                              {
                                  if ((parentFragment is PostReactUserListFragment))
                                      (parentFragment as PostReactUserListFragment?)!!.setupTabIcons(it[0].countArray[0])
                              }

                            }

                            postLikeList?.addAll(it[0].data!!)
                            postReactUserListAdapter?.notifyDataSetChanged()
                            pageNo += 1
                            if (it[0].data!!.size < 10) {
                                isLastpage = true
                            }

                        } else {
                            relativeprogressBar.visibility = View.GONE

                            if (postLikeList!!.size == 0) {
                                noDatafoundRelativelayout.visibility = View.VISIBLE
                                generalRecyclerView.visibility = View.GONE
                                nodatafoundtextview.visibility = View.GONE

                            } else {
                                noDatafoundRelativelayout.visibility = View.GONE
                                generalRecyclerView.visibility = View.VISIBLE

                            }
                        }

                    } else {
                        if (activity != null) {
                            relativeprogressBar.visibility = View.GONE
                            generalRecyclerView.visibility = (View.GONE)
                            try {
                                nointernetMainRelativelayout.visibility = View.VISIBLE
                                if (MyUtils.isInternetAvailable(activity!!)) {
                                    nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.something_went_wrong))
                                    nointernettextview.text =
                                        (GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
                                    nointernettextview.visibility=View.GONE
                                    nointernettextview1.text =
                                        (GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
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
                }
            )

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
            ViewModelProviders.of(this@ReactUserListFragment).get(FollowModel::class.java)
        getFollowModel.apiCall(mActivity!!, jsonArray.toString(), s)
            .observe(this@ReactUserListFragment,
                Observer
                { trendingFeedDatum ->
                    if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                        MyUtils.closeProgress()
                        if (trendingFeedDatum[0].status?.equals("true")!!) {
                            when (s) {
                                "follow" -> {
                                    postLikeList?.get(pos)!!.isYouFollowing = "Yes"
                                    postReactUserListAdapter?.notifyDataSetChanged()

                                }
                                "unfollow" -> {

                                    postLikeList?.get(pos)!!.isYouFollowing = "No"
                                    postReactUserListAdapter?.notifyDataSetChanged()
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


}
