package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.PopupMenu
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.GetPostListModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.ReportPostModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedDatum
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class PostOwnerPostListFragment : Fragment() {

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
    var gridmanager: LinearLayoutManager? = null
//    var gridmanager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var feedList: ArrayList<TrendingFeedData?>? = null
    var pageNo = 0
    var getPostListModel: GetPostListModel? = null
    var tabposition: Int = 0
    var isYouFollowing = ""
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
        userData = sessionManager?.get_Authenticate_User()
        if (arguments != null) {
            tabposition = arguments?.getInt("position", -1)!!
            OriginaluserID = arguments?.getString("userID", "")!!
            OtherUseruserID = arguments?.getString("OtherUseruserID", "")!!
            isYouFollowing = arguments?.getString("isYouFollowing", "")!!

        }

        gridmanager = GridLayoutManager(mActivity!!, 2, GridLayoutManager.VERTICAL, false)
        generalRecyclerView.layoutManager = gridmanager
        nodata1?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
        nodatafoundtextview?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
        btnRetry?.text = GetDynamicStringDictionaryObjectClass?.Retry

        // setLayoutAsperCondition(false, false, true, false, false)
        noDatafoundRelativelayout.visibility = View.GONE
        relativeprogressBar.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        getPostListModel =
            ViewModelProviders.of(this@PostOwnerPostListFragment).get(GetPostListModel::class.java)

        if (feedList == null) {
            feedList = ArrayList()

            adapterNotification = PostOwnerPostListAdapter(
                mActivity!!, /*arrayNotification!!,*/
                object : PostOwnerPostListAdapter.BtnClickListener {
                    override fun onBtnClick(
                        position: Int,
                        buttonName: String,
                        itemPostOwnerPostImageDot: AppCompatImageView) {
                     when(buttonName){
                         "threeDots"->{
                             if(tabposition==0){
                                   showDotMenu(feedList?.get(position)?.postID!!,position,itemPostOwnerPostImageDot)
                             }
                         }
                      }
                    }
                }, feedList!!,isYouFollowing
            )
            generalRecyclerView.hasFixedSize()
            generalRecyclerView.adapter = adapterNotification
            adapterNotification?.notifyDataSetChanged()
            when (tabposition) {
                0 -> {
                    getPostList("getmyposts")
                }
                1 -> {
                    getPostList("getmetaggedposts")

                }
                2 -> {
                    getPostList("getmefavoriteposts")

                }
                3 -> {
                    getPostList("getmycommentedposts")

                }
            }

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
                        when (tabposition) {
                            0 -> {
                                getPostList("getmyposts")
                            }
                            1 -> {
                                getPostList("getmetaggedposts")

                            }
                            2 -> {
                                getPostList("getmefavoriteposts")

                            }
                            3 -> {
                                getPostList("getmycommentedposts")

                            }
                        }
                    }
                }
            }


        })



        btnRetry.setOnClickListener {
            when (tabposition) {
                0 -> {
                    getPostList("getmyposts")
                }
                1 -> {
                    getPostList("getmetaggedposts")

                }
                2 -> {
                    getPostList("getmefavoriteposts")

                }
                3 -> {
                    getPostList("getmycommentedposts")

                }
            }
        }

    }

    fun dynamicString() {

        msgNoInternet = GetDynamicStringDictionaryObjectClass?.Please_check_your_internet_connectivity_and_try_again
        msgSomthingrong =GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong

    }
    fun getPostList(s: String) {
        noDatafoundRelativelayout.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        if (pageNo == 0) {
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
            if (OriginaluserID.equals(OtherUseruserID)) {
                jsonObject.put("userID", userData?.userID)
            } else {
                jsonObject.put("userID", OtherUseruserID)
            }
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("sortingwith", "")
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("searchWord", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        getPostListModel?.apiFunction(mActivity!!, false, jsonArray.toString(), s)
            ?.observe(this@PostOwnerPostListFragment,
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
                            }


                            feedList!!.addAll(trendingFeedDatum[0].data)
                            adapterNotification?.notifyDataSetChanged()
                            pageNo = pageNo + 1
                            if (trendingFeedDatum[0].data.size < 10) {
                                isLastpage = true
                            }
                        } else {
                            relativeprogressBar.visibility = View.GONE

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
                            relativeprogressBar.visibility = View.GONE
                            generalRecyclerView.visibility = (View.GONE)
                            try {
                                nointernetMainRelativelayout.visibility = View.VISIBLE
                                if (MyUtils.isInternetAvailable(activity!!)) {
                                    nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.something_went_wrong))
                                    nointernettextview.visibility=View.GONE
                                    nointernettextview.text =
                                        (GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
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
                })


    }
    fun showDotMenu(
        postID: String,
        position: Int,
        itemPostOwnerPostImageDot: AppCompatImageView
    ) {
        //init the wrapper with style
        val wrapper = ContextThemeWrapper(mActivity!!, R.style.popmenu_style)
        //init the popup
        val popup = PopupMenu(wrapper, itemPostOwnerPostImageDot!!)

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
//        popup.menu.getItem(0).title = title_edit_image
//        popup.menu.getItem(1).title = title_settings
//        popup.menu.getItem(2).title = title_signature_Video
//        popup.menu.getItem(3).title = title_sign_out

        popup.menu.getItem(0).title = "Unhide Post"
        popup.menu.getItem(1).title = GetDynamicStringDictionaryObjectClass?.Settings
        popup.menu.getItem(2).title = GetDynamicStringDictionaryObjectClass?.Signature_Video
        popup.menu.getItem(3).title = GetDynamicStringDictionaryObjectClass?.Sign_Out

        popup.menu.getItem(0).setIcon(R.drawable.hide_post_icon_black)
        popup.menu.getItem(0).isVisible=true
        popup.menu.getItem(1).isVisible=false
        popup.menu.getItem(2).isVisible=false
        popup.menu.getItem(3).isVisible=false

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_profile -> {
                     unHidePost(postID,position)
                }
            }
            true
        }
        popup.show()
    }

    private fun unHidePost( postID: String?,pos: Int) {
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
                ViewModelProviders.of(this@PostOwnerPostListFragment).get(ReportPostModel::class.java)

            reportPostModel.apiFunction(mActivity!!, jsonArray.toString(), "PostActive")
                .observe(this@PostOwnerPostListFragment, Observer { it ->
                    //            MyUtils.closeProgress()

                    if (it != null && it.isNotEmpty()) {
                        MyUtils.closeProgress()

                        if (it[0].status.equals("true", true)) {
                            (activity as MainActivity).showSnackBar("Post un-hide to all user please refresh the home page.")
                            feedList!![pos]!!.postStatus="Active"
                            adapterNotification?.notifyDataSetChanged()
                        } else {
                            (activity as MainActivity).showSnackBar(it[0].message!!)
                        }

                    } else {
                        MyUtils.closeProgress()
                        (activity as MainActivity).errorMethod()
                    }
                })


    }

}