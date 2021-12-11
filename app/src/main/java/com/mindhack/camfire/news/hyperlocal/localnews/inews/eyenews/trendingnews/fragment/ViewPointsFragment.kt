package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.PointsAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.MyViewPointsModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.generallistlayout.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.fragment_view_points.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ViewPointsFragment : Fragment() {


    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager : SessionManager? = null
    var userData : RegisterPojo.Data? = null
    var titlePoints = ""
    private var linearLayoutManager: LinearLayoutManager? = null
    var pageNo = 0
    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var pointsAdapter:PointsAdapter?=null
       var feedData:ArrayList<TrendingFeedData?>?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if(v==null)
        {
            v = inflater.inflate(R.layout.fragment_view_points, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        (mActivity as MainActivity).showHideBottomNavigation(false)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager!!.isLoggedIn()) {
            userData = sessionManager?.get_Authenticate_User()
        }
        dynamicString()

        imgCloseIcon.setOnClickListener {
            (mActivity as MainActivity).onBackPressed()
        }

        tvHeaderText?.text = titlePoints
        /* if(arguments!=null)
         {
             userData= arguments?.getSerializable("userData") as RegisterPojo.Data?
         }*/

        img_user_trophy.setImageURI(RestClient.image_base_Level+userData?.badgeIcon)

        tvTextLevel.text = userData?.badgeName+" Level"
        tvTextPoints.text=userData?.userTotalPoint

        if (feedData == null){
            feedData = ArrayList()
        linearLayoutManager = LinearLayoutManager(activity)
        pointsAdapter = PointsAdapter(
            activity!!,
            object : PointsAdapter.OnItemClick {
                override fun onClicklisneter(pos: Int, name: String) {

                }
            },feedData!!
        )
        generalRecyclerView.layoutManager = linearLayoutManager
        generalRecyclerView.adapter = pointsAdapter
        getMyViewPointList()
    }


        /*generalRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                        getMyViewPointList()
                    }
                }
            }


        })*/

        btnRetry.setOnClickListener {
            pageNo=0
            getMyViewPointList()
        }

    }

    private fun dynamicString()
    {
//        titlePoints = ""+mActivity!!.resources.getString(R.string.points)
        titlePoints = ""+GetDynamicStringDictionaryObjectClass?.Points
    }


    private fun getMyViewPointList()
    {
        noDatafoundRelativelayout.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            feedData!!.clear()
            pointsAdapter?.notifyDataSetChanged()
        } else {
            relativeprogressBar.visibility = View.GONE
            generalRecyclerView.visibility = (View.VISIBLE)
            feedData?.add(null)
            pointsAdapter?.notifyItemInserted(feedData!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID",userData?.languageID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var myViewPointsModel= ViewModelProviders.of(this@ViewPointsFragment).get(MyViewPointsModel::class.java)
        myViewPointsModel.apiCall(mActivity!!,  jsonArray.toString()).observe(this@ViewPointsFragment,
            Observer { trendingFeedDatum ->
                if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                    isLoading = false
                    //   remove progress item
                    noDatafoundRelativelayout.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE
                    if (pageNo > 0) {
                        feedData!!.removeAt(feedData?.size!! - 1)
                        pointsAdapter?.notifyItemRemoved(feedData?.size!!)
                    }
                    if (trendingFeedDatum[0].status?.equals("true")!!){

                        if (pageNo == 0)
                            feedData!!.clear()

                        feedData!!.addAll(trendingFeedDatum[0].data!!)
                        pointsAdapter?.notifyDataSetChanged()
                        pageNo += 1
                        if (trendingFeedDatum[0].data!!.size < 10) {
                            isLastpage = true
                        }

                    } else {
                        relativeprogressBar.visibility = View.GONE

                        if (feedData!!.size == 0) {
                            noDatafoundRelativelayout.visibility = View.VISIBLE
                            nodatafoundtextview.visibility = View.GONE
                            generalRecyclerView.visibility = View.GONE

                        } else {
                            noDatafoundRelativelayout.visibility = View.GONE
                            nodatafoundtextview.visibility = View.GONE
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
                                nointernettextview.visibility=View.GONE
                                nointernettextview.text = (GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
                                nointernettextview1.text = (GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
                            } else {
                                nointernettextview.visibility=View.VISIBLE
                                nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.no_internet_connection))
                                nointernettextview1.text = (GetDynamicStringDictionaryObjectClass?.No_Internet_Connection)
                                nointernettextview.text = (GetDynamicStringDictionaryObjectClass?.Please_check_your_internet_connectivity_and_try_again)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            })
    }

}
