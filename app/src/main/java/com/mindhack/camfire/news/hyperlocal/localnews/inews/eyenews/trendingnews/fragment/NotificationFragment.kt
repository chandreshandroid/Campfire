package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ParseException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.NotificationAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.DeleteNotificationModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.NotificationlistModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.NotificationListPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.flymyowncustomer.util.PrefDb
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.generallistlayout.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment : Fragment(), View.OnClickListener {


    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    val TAG = ProfileDetailFragment::class.java.name
    var sessionManager: SessionManager? = null
    var userData: RegisterPojo.Data? = null

    var titleNotification = ""
    var markallasread = ""

    var linearLayoutManager: LinearLayoutManager? = null
    var adapterNotification: NotificationAdapter? = null
//    var arrayNotification : ArrayList<NotificationData>? = null

    //    private lateinit var notificationAdapter: NotificationAdapter
    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var pageNo = 0
    private var notificationListData: ArrayList<NotificationListPojo.Datum?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if(v==null)
        {
            v = inflater.inflate(R.layout.fragment_notification, container, false)

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

        dynamicString()


        sessionManager = SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager!!.isLoggedIn()) {
            userData = sessionManager?.get_Authenticate_User()
        }
        noDatafoundRelativelayout.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        relativeprogressBar.visibility = View.GONE

        imgCloseIcon.setOnClickListener(this)

        linearLayoutManager = LinearLayoutManager(mActivity!!)
        generalRecyclerView.layoutManager = linearLayoutManager

        setNotificationAdapetr()

        itemNotificationTextMarkAsRead.text = markallasread
        tvHeaderText.text = titleNotification


        generalRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = linearLayoutManager!!.childCount
                totalItemCount = linearLayoutManager!!.itemCount
                firstVisibleItemPosition = linearLayoutManager!!.findFirstVisibleItemPosition()
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10
                    ) {

                        isLoading = true
                        getNotificationList()
                    }
                }
            }
        })

        btnRetry!!.setOnClickListener {
            pageNo = 0
            getNotificationList()
        }

        itemNotificationTextMarkAsRead.setOnClickListener {
            allReadNotification()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgCloseIcon -> (mActivity as MainActivity).onBackPressed()
        }
    }

    private fun setNotificationAdapetr() {
        if (notificationListData.isNullOrEmpty()) {
            notificationListData = ArrayList<NotificationListPojo.Datum?>()
            adapterNotification = NotificationAdapter(
                mActivity!!,
                notificationListData!!,
                object : NotificationAdapter.BtnClickListener {
                    override fun onBtnClick(position: Int, buttonName: String) {
                          when(buttonName)
                          {
                              "read"->{
                                 oneReadNotification(notificationListData!![position]?.notificationID!!,position)
                              }
                              else->{
                                  MyUtils.showMessageYesNo(
                                      mActivity!!,
                                      "Are you sure to delete this notification?",
                                      DialogInterface.OnClickListener { dialog, which ->
                                          dialog?.dismiss()
                                          deleteNotification(notificationListData!![position]?.notificationID!!)
                                      })
                              }
                          }

                    }
                })
            generalRecyclerView.hasFixedSize()
            generalRecyclerView.adapter = adapterNotification
            adapterNotification?.notifyDataSetChanged()
            getNotificationList()
        }
    }

    private fun getNotificationList() {
        if (pageNo == 0) {
            setLayoutAsPerCondition(false, true, false, false, false)
            notificationListData?.clear()
            adapterNotification?.notifyDataSetChanged()
        } else {
            setLayoutAsPerCondition(false, false, true, false, false)
            notificationListData?.add(null)
            adapterNotification?.notifyItemInserted(notificationListData!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        val notificationlistModel =
            ViewModelProviders.of(mActivity!!).get(NotificationlistModel::class.java)
        notificationlistModel.getNotificationList(
                activity as MainActivity,
                false,
                jsonArray.toString()
            )
            .observe(
                this@NotificationFragment,
                Observer<List<NotificationListPojo>>
                { notificationlistPojo ->
                    if (notificationlistPojo != null && !notificationlistPojo.isNullOrEmpty()) {
                        isLoading = false
                        //   remove progress item
                        setLayoutAsPerCondition(false, false, true, false, false)
                        if (pageNo > 0) {
                            notificationListData?.removeAt(notificationListData!!.size - 1)
                            adapterNotification?.notifyItemRemoved(notificationListData!!.size)
                        }

                        if (notificationlistPojo[0].status.equals("true")) {
                            try {
                                PrefDb(mActivity!!).putInt("unReadNotification", 0)
                                val intent1 = Intent("Push")
                                LocalBroadcastManager.getInstance(mActivity!!)
                                    .sendBroadcast(intent1)
                            } catch (e: java.lang.Exception) {
                            }
                            if (pageNo == 0)
                                notificationListData?.clear()

                            if (notificationlistPojo[0].data!! == null) {
                                isLastpage = true
                            } else isLastpage = notificationlistPojo[0].data!!.size < 10

                            notificationListData?.addAll(notificationlistPojo[0].data!!)
                            adapterNotification?.notifyDataSetChanged()
                            pageNo = pageNo + 1
//                            if (notificationlistPojo[0].data!!.size < 10) {
//                                isLastpage = true
//                            }

                        } else {
                            isLastpage = true

                            if (notificationListData!!.size == 0) {
                                noDatafoundRelativelayout.visibility = View.VISIBLE
                                generalRecyclerView.visibility = View.GONE

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
                                        (GetDynamicStringDictionaryObjectClass.Something_Went_Wrong)
                                    nointernettextview.visibility=View.GONE
                                    nointernettextview1.text =
                                        (GetDynamicStringDictionaryObjectClass.Something_Went_Wrong)
                                } else {
                                    nointernettextview.visibility=View.VISIBLE
                                    nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.no_internet_connection))
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

    fun allReadNotification() {

        var jsonArray = JSONArray()
        var jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
//            jsonObject.put("logindealerID", "0")
            //           jsonObject.put("notificationID", notificationId)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val deleteNotificationModel =
            ViewModelProviders.of(this@NotificationFragment)
                .get(DeleteNotificationModel::class.java)
        deleteNotificationModel.getDeleteNotification(
                mActivity!!,
                true,
                jsonArray.toString(),
                "read"
            )
            .observe(this@NotificationFragment, object : Observer<List<CommonPojo>> {
                override fun onChanged(commonResPojo: List<CommonPojo>?) {

                    if (commonResPojo != null && commonResPojo.isNotEmpty())
                    {

                        if (commonResPojo[0].status.equals("true", true)) {

                            /* MyUtils.showSnackbarkotlin(
                                 mActivity!!,
                                 notificationLayoutMain!!,
                                 commonResPojo[0].message!!)*/
                            for (i in 0 until notificationListData!!.size) {
                                notificationListData!![i]!!.notificationReadStatus = "Yes"
                            }
                            adapterNotification?.notifyDataSetChanged()

                        } else {
                            if (commonResPojo[0].message.isNullOrEmpty()) {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    notificationLayoutMain!!,
                                    mActivity!!.resources.getString(R.string.fail_delete_notification)
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    notificationLayoutMain!!,
                                    commonResPojo[0].message!!
                                )
                            }
                        }
                    } else {
                        (activity as MainActivity).errorMethod()

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
            itemNotificationTextMarkAsRead.visibility = View.GONE
            generalRecyclerView.visibility = View.GONE

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
            itemNotificationTextMarkAsRead.visibility = View.GONE
            generalRecyclerView.visibility = View.GONE

            noDatafoundRelativelayout?.visibility = View.GONE
            nodata1?.visibility = View.VISIBLE
            nodatafoundtextview?.visibility = View.GONE

            relativeprogressBar?.visibility = View.VISIBLE

            nointernetMainRelativelayout?.visibility = View.GONE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview1?.visibility = View.VISIBLE
        } else if (dataFound) {
//            progressView?.hide()
            itemNotificationTextMarkAsRead.visibility = View.VISIBLE
            generalRecyclerView.visibility = View.VISIBLE

            noDatafoundRelativelayout?.visibility = View.GONE
            nodata1?.visibility = View.VISIBLE
            nodatafoundtextview?.visibility = View.GONE

            relativeprogressBar?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.GONE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview1?.visibility = View.VISIBLE
        } else if (noInternet) {
            itemNotificationTextMarkAsRead.visibility = View.GONE
            generalRecyclerView.visibility = View.GONE

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
            itemNotificationTextMarkAsRead.visibility = View.GONE
            generalRecyclerView.visibility = View.GONE

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


    fun deleteNotification(notificationId: String) {

        var jsonArray = JSONArray()
        var jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
//            jsonObject.put("logindealerID", "0")
            jsonObject.put("notificationID", notificationId)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val deleteNotificationModel =
            ViewModelProviders.of(this@NotificationFragment)
                .get(DeleteNotificationModel::class.java)
        deleteNotificationModel.getDeleteNotification(
                mActivity!!,
                true,
                jsonArray.toString(),
                "delete"
            )
            .observe(this@NotificationFragment,
                Observer<List<CommonPojo>> { commonResPojo ->
                    if (commonResPojo != null && commonResPojo.isNotEmpty()) {

                        if (commonResPojo[0].status.equals("true", true)) {

                            if (notificationListData!!.size > 0) {

                                for (i in 0 until notificationListData!!.size) {

                                    if (notificationId.equals(notificationListData!![i]?.notificationID)) {
                                        notificationListData!!.removeAt(i)
                                        break
                                    }
                                }
                            }

                            //                            MyUtils.showSnackbarkotlin(
                            //                                mActivity!!,
                            //                                notificationLayoutMain!!,
                            //                                commonResPojo[0].message!!)

                            adapterNotification?.notifyDataSetChanged()

                        } else {
                            if (commonResPojo[0].message.isNullOrEmpty()) {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    notificationLayoutMain!!,
                                    mActivity!!.resources.getString(R.string.fail_delete_notification)
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    notificationLayoutMain!!,
                                    commonResPojo[0].message!!
                                )
                            }
                        }
                    } else {
                        (activity as MainActivity).errorMethod()

                    }
                })

    }

    fun oneReadNotification(notificationId: String, pos: Int) {

        var jsonArray = JSONArray()
        var jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("notificationID", notificationId)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val deleteNotificationModel =
            ViewModelProviders.of(this@NotificationFragment)
                .get(DeleteNotificationModel::class.java)
        deleteNotificationModel.getDeleteNotification(
                mActivity!!,
                true,
                jsonArray.toString(),
                "oneread"
            )
            .observe(this@NotificationFragment,
                Observer<List<CommonPojo>> { commonResPojo ->
                    if (commonResPojo != null && commonResPojo.isNotEmpty()) {

                        if (commonResPojo[0].status.equals("true", true)) {


                            notificationListData!![pos]!!.notificationReadStatus = "Yes"
                            adapterNotification?.notifyDataSetChanged()

                        } else {
                            if (commonResPojo[0].message.isNullOrEmpty()) {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    notificationLayoutMain!!,
                                    mActivity!!.resources.getString(R.string.fail_delete_notification)
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    notificationLayoutMain!!,
                                    commonResPojo[0].message!!
                                )
                            }
                        }
                    } else {
                        (activity as MainActivity).errorMethod()

                    }
                })

    }

    private fun dynamicString() {
//        markallasread = mActivity!!.resources.getString(R.string.markasallread)
//        titleNotification = mActivity!!.resources.getString(R.string.notifications)
        markallasread = GetDynamicStringDictionaryObjectClass.Mark_all_as_Read
        titleNotification = GetDynamicStringDictionaryObjectClass.Notifications
    }
}