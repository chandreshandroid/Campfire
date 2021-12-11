package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.PostOwnerPostListAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.generallistlayout.*
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.HashTagPostListModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedDatum
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class LocationPostListFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null

    var sessionManager: SessionManager? = null
    var userData: RegisterPojo.Data? = null
    var OriginaluserID = ""
    var OtherUseruserID = ""

    var msgNoInternet = ""
    var msgSomthingrong = ""

    //    var linearLayoutManager : LinearLayoutManager? = null
    var adapterNotification: PostOwnerPostListAdapter? = null
    var gridmanager: GridLayoutManager? = null
//    var gridmanager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var feedList: ArrayList<TrendingFeedData?>? = null
    var pageNo = 0
    var hashTagPostListModel: HashTagPostListModel? = null
    var position: Int = 0
    var type: String = ""
    var hashTag = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_post_owner_post_list, container, false)

        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.isLoggedIn()!!) {
            userData = sessionManager?.get_Authenticate_User()
        }

        if (arguments != null) {
            position = arguments?.getInt("position", -1)!!
            type = arguments?.getString("type", "")!!
            hashTag = arguments?.getString("hashTag", "")!!

        }

        gridmanager = GridLayoutManager(mActivity!!, 2, GridLayoutManager.VERTICAL, false)
        generalRecyclerView.layoutManager = gridmanager


        // setLayoutAsperCondition(false, false, true, false, false)
        noDatafoundRelativelayout.visibility = View.GONE
        relativeprogressBar.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        hashTagPostListModel = ViewModelProviders.of(this@LocationPostListFragment)
            .get(HashTagPostListModel::class.java)

        if (feedList == null) {
            feedList = ArrayList()
            adapterNotification = PostOwnerPostListAdapter(
                mActivity!!, /*arrayNotification!!,*/
                object : PostOwnerPostListAdapter.BtnClickListener {
                    override fun onBtnClick(
                        position: Int,
                        buttonName: String,
                        itemPostOwnerPostImageDot: AppCompatImageView
                    ) {

                    }
                }, feedList!!, ""
            )
            generalRecyclerView.hasFixedSize()
            generalRecyclerView.adapter = adapterNotification
            adapterNotification?.notifyDataSetChanged()

            getPostList(type)
        }

        generalRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                y = dy
                visibleItemCount = gridmanager?.childCount!!
                totalItemCount = gridmanager?.itemCount!!
                firstVisibleItemPosition = gridmanager?.findFirstVisibleItemPosition()!!
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10
                    ) {
                        isLoading = true


                        getPostList(type)

                    }
                }
            }


        })

        btnRetry.setOnClickListener {
            pageNo = 0
            getPostList(type)
        }


    }

    fun dynamicString() {

        msgNoInternet = mActivity!!.resources.getString(R.string.error_common_network)
        msgSomthingrong = mActivity!!.resources.getString(R.string.error_crash_error_message)

    }

    fun getPostList(s: String) {
        noDatafoundRelativelayout.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        if (pageNo == 0) {
            (parentFragment as LocationListFragment).setShow(false, "")

            relativeprogressBar.visibility = View.VISIBLE
            feedList!!.clear()
            adapterNotification?.notifyDataSetChanged()
        } else {
            relativeprogressBar.visibility = View.GONE
            generalRecyclerView.visibility = (View.VISIBLE)
            feedList?.add(null)
            adapterNotification?.notifyItemInserted(feedList!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            if (sessionManager?.isLoggedIn()!!) {
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put("languageID", userData?.languageID)
            } else {
                jsonObject.put("loginuserID", "0")
                jsonObject.put("languageID", sessionManager?.getsetSelectedLanguage())
            }
            jsonObject.put("postLatitude", MyUtils.currentLattitude)
            jsonObject.put("postLongitude", MyUtils.currentLongitude)
            jsonObject.put("sortingwith", s)
            jsonObject.put("searchWord", hashTag)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        hashTagPostListModel?.apiFunction(mActivity!!, false, jsonArray.toString(), "location")
            ?.observe(this@LocationPostListFragment,
                Observer<List<TrendingFeedDatum>>
                { trendingFeedDatum ->
                    if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                        isLoading = false
                        //   remove progress item
                        noDatafoundRelativelayout.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE
                        if (pageNo > 0) {
                            feedList!!.removeAt(feedList!!.size - 1)
                            adapterNotification?.notifyItemRemoved(feedList!!.size)
                        }
                        if (trendingFeedDatum[0].status.equals("true", false)) {

                            if (pageNo == 0) {
                                feedList!!.clear()
                                for (i in 0 until trendingFeedDatum[0].data?.size!!) {
                                    if (trendingFeedDatum[0].data[i]!!.postMediaType.equals(
                                            "Photo",
                                            false
                                        )
                                    ) {
                                        if (!trendingFeedDatum[0].data[i].postSerializedData[0].albummedia[0].albummediaFile.isNullOrEmpty()) {
                                            (parentFragment as LocationListFragment).setShow(
                                                true,
                                                trendingFeedDatum[0].data[i].postSerializedData[0].albummedia[0].albummediaFile
                                            )
                                            break
                                        }

                                    }


                                }
                                try {
                                    (parentFragment as LocationListFragment?)?.setTabtitle(
                                        trendingFeedDatum[0].totalpost
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }


                            feedList!!.addAll(trendingFeedDatum[0].data)
                            adapterNotification?.notifyDataSetChanged()
                            pageNo += 1
                            if (trendingFeedDatum[0].data.size < 10) {
                                isLastpage = true
                            }

                        } else {
                            relativeprogressBar.visibility = View.GONE
                            if (pageNo == 0) {
                                (parentFragment as LocationListFragment).setShow(false, "")
                            }

                            if (feedList!!.size == 0) {
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
                            (parentFragment as LocationListFragment).setShow(false, "")

                            relativeprogressBar.visibility = View.GONE
                            generalRecyclerView.visibility = (View.GONE)
                            try {
                                nointernetMainRelativelayout.visibility = View.VISIBLE
                                if (MyUtils.isInternetAvailable(activity!!)) {
                                    nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.something_went_wrong))
                                    nointernettextview.text =
                                        (activity!!.getString(R.string.error_crash_error_message))
                                    nointernettextview1.text =
                                        (this.getString(R.string.somethigwrong1))
                                    nointernettextview.visibility = View.GONE
                                } else {
                                    nointernettextview.visibility = View.VISIBLE
                                    nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.no_internet_connection))
                                    nointernettextview1.text =
                                        (this.getString(R.string.internetmsg1))
                                    nointernettextview.text =
                                        (activity!!.getString(R.string.error_common_network))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }
                })


    }

}