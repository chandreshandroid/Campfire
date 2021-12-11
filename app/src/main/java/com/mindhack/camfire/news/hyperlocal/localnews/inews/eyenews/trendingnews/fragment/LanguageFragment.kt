package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.LanguageAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.GetDynamicStringDictionaryModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.GetLanguageListModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OnBoardingModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GetDynamicStringDictionaryPojo1
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GetLanguageListPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.flymyowncustomer.util.PrefDb
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager

import com.google.gson.Gson

import kotlinx.android.synthetic.main.fragment_language.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException


class LanguageFragment : Fragment() {

    var v: View? = null
    var activity: Activity? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    var languageAdapter: LanguageAdapter? = null
    var languagelistDatum: ArrayList<GetLanguageListPojo.Datum?>? = null
    var languagelistModel: GetLanguageListModel? = null
    var arrayDynamicStringDictionaryList : ArrayList<GetDynamicStringDictionaryPojo1>? = null
    var pageNo = 0
    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var sessionManager: SessionManager? = null
    var headertitle = ""
    var languageId :String? = ""
    var userdata:RegisterPojo.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_language, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as Activity

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dynamicLable()
        nodatafoundtextview?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
        nodata1?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
        btnRetry?.text = GetDynamicStringDictionaryObjectClass?.Retry

        sessionManager = SessionManager(activity!!)

        if(sessionManager!!.isLoggedIn()){
            languageId = sessionManager?.get_Authenticate_User()?.languageID
        }
        userdata=sessionManager?.get_Authenticate_User()

        tvHeaderText.text = headertitle
        imgCloseIcon.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
//        (activity as MainActivity).showHideBottomNavigation(false)


        languagelistModel = ViewModelProviders.of(this@LanguageFragment).get(GetLanguageListModel::class.java)

        if (languagelistDatum.isNullOrEmpty()) {
            languagelistDatum = ArrayList()
            linearLayoutManager = LinearLayoutManager(activity)
            languageAdapter = LanguageAdapter(
                activity!!,
                object : LanguageAdapter.OnItemClick {
                    override fun onClicklisneter(pos: Int, name: String) {
                        languageId = languagelistDatum!![pos]!!.languageID!!
                        getUpdateSetting(languagelistDatum!![pos]!!.languageID!!)
                    }
                },
                languagelistDatum!!
            )
            languageRecyclerview.layoutManager = linearLayoutManager
            languageRecyclerview.adapter = languageAdapter

            getLanguageList()
        }

        languageRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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
                        getLanguageList()
                    }
                }
            }
        })

        btnRetry.setOnClickListener {
            getLanguageList()
        }


    }

    private fun getLanguageList() {
        noDatafoundRelativelayout.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        if (pageNo == 0) {
            relativeprogressBar.visibility = View.VISIBLE
            languagelistDatum!!.clear()
            languageAdapter?.notifyDataSetChanged()
        } else {
            relativeprogressBar.visibility = View.GONE
            languageRecyclerview.visibility = (View.VISIBLE)
            languagelistDatum!!.add(null)
            languageAdapter?.notifyItemInserted(languagelistDatum!!.size - 1)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("languageID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        languagelistModel?.apiFunction(activity!!, false, jsonArray.toString())?.observe(this@LanguageFragment,
            Observer<List<GetLanguageListPojo>>
            { languageLiatPojo ->
                if (languageLiatPojo != null && languageLiatPojo.isNotEmpty()) {
                    isLoading = false
                    //   remove progress item
                    noDatafoundRelativelayout.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE
                    if (pageNo > 0) {
                        languagelistDatum!!.removeAt(languagelistDatum!!.size - 1)
                        languageAdapter?.notifyItemRemoved(languagelistDatum!!.size)
                    }
                    if (languageLiatPojo[0].status?.equals("true")!!){

                        if (pageNo == 0)
                            languagelistDatum!!.clear()

                        languagelistDatum!!.addAll(languageLiatPojo[0].data!!)
                        languageAdapter?.notifyDataSetChanged()
                        pageNo += 1
                        if (languageLiatPojo[0].data!!.size < 10) {
                            isLastpage = true
                        }

                    } else {
                        relativeprogressBar.visibility = View.GONE

                        if (languagelistDatum!!.size == 0) {
                            noDatafoundRelativelayout.visibility = View.VISIBLE
                            languageRecyclerview.visibility = View.GONE

                        } else {
                            noDatafoundRelativelayout.visibility = View.GONE
                            languageRecyclerview.visibility = View.VISIBLE

                        }
                    }

                } else {
                    if (activity != null) {
                        relativeprogressBar.visibility = View.GONE
                        languageRecyclerview.visibility = (View.GONE)

                        try {
                            nointernetMainRelativelayout.visibility = View.VISIBLE
                            if (MyUtils.isInternetAvailable(activity!!)) {
                                nointernetImageview.setImageDrawable(activity!!.getDrawable(R.drawable.something_went_wrong))
                                nointernettextview.text = (GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
                                nointernettextview1.text = (GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
                                nointernettextview.visibility=View.GONE
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


    private fun getUpdateSetting(languageId: String) {


        MyUtils.showProgressDialog(activity!!)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", userdata!!.userID)
            jsonObject.put("languageID", languageId)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        val loginModel = ViewModelProviders.of(this@LanguageFragment).get(OnBoardingModel::class.java)
        loginModel.apiCall(activity!!,  jsonArray.toString(), 11)
            .observe(this@LanguageFragment,
                Observer<List<RegisterPojo>?> { loginPojo ->
                    if (loginPojo != null && loginPojo.isNotEmpty()) {

                        if (loginPojo[0].status.equals("true", true))
                        {
                            MyUtils.closeProgress()

                            loginPojo[0].data!![0].languageID=languageId

                            sessionManager?.setSelectedLanguage(languageId)

                            StoreSessionManager(loginPojo[0].data!!)

                            languageAdapter?.notifyDataSetChanged()

                            getGetDynamicStringDictionaryPojoList()

                        } else {

                            MyUtils.closeProgress()
                            (activity as MainActivity).showSnackBar(loginPojo[0].message)

                        }

                    } else {

                        MyUtils.closeProgress()
                        (activity as MainActivity).errorMethod()
                    }
                })
    }


    private fun getGetDynamicStringDictionaryPojoList() {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
//            /language/list-labels

            /*[{
                "loginuserID": "0",
                "languageID": "2",
                "langLabelApp": "User App",
                "apiType": "Android",
                "apiVersion": "1.0"
            }]*/

            jsonObject.put("loginuserID", "0")
            jsonObject.put("languageID", languageId.toString())
            jsonObject.put("langLabelApp", "User App")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

            Log.e("System out", "Register api call := " + jsonArray.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val getDynamicStringDictionaryModel = ViewModelProviders.of(this@LanguageFragment).get(
            GetDynamicStringDictionaryModel::class.java)
        getDynamicStringDictionaryModel.getDynamicStringDictionaryresponseJson(activity!!, true, jsonArray.toString())
            .observe(this@LanguageFragment, object :
                Observer<List<GetDynamicStringDictionaryPojo1>> {
                override fun onChanged(response: List<GetDynamicStringDictionaryPojo1>?) {

                    if (response != null && response.size > 0) {
                        if (response[0].status.equals("true")) {
                            arrayDynamicStringDictionaryList = ArrayList<GetDynamicStringDictionaryPojo1>()
                            arrayDynamicStringDictionaryList?.clear()
                            arrayDynamicStringDictionaryList?.addAll(response)

                            storePrefDb(arrayDynamicStringDictionaryList!!)
                            saveLanguage()

                        } else {
                            arrayDynamicStringDictionaryList?.clear()

                            //No data -- No internet
                            if (MyUtils.isInternetAvailable(activity!!)) {
                                MyUtils.showSnackbarkotlin(
                                    activity as MainActivity,
                                    rootLanguageMainLayout!!,
                                    GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    activity as MainActivity,
                                    rootLanguageMainLayout!!,
                                    GetDynamicStringDictionaryObjectClass?.No_Internet_Connection
                                )
                            }
                        }
                    } else {
                        arrayDynamicStringDictionaryList?.clear()

                        //No internet -- Somthing went rong
                        if (MyUtils.isInternetAvailable(activity!!)) {
                            MyUtils.showSnackbarkotlin(
                                activity as MainActivity,
                                rootLanguageMainLayout!!,
                                GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                activity as MainActivity,
                                rootLanguageMainLayout!!,
                                GetDynamicStringDictionaryObjectClass?.No_Internet_Connection
                            )
                        }
                    }

                }

            })
    }


    private fun storePrefDb(logindata: List<GetDynamicStringDictionaryPojo1>) {
        val gson = Gson()
        var json = gson.toJson(logindata[0])
//        Log.e("System out","Print DynamicStringDictionary :==  "+ json)
        PrefDb((activity as MainActivity)).setDynamicStringDictionary(json)
    }

    private fun saveLanguage() {
        PrefDb((activity as MainActivity)).putString(MyUtils.SharedPreferencesenum.languageId.toString(), languageId!!)
        var updateGetDynamicStringDictionary= GetDynamicStringDictionaryObjectClass.getInstance((activity as MainActivity))

        Handler().postDelayed({
            (activity!! as MainActivity).onBackPressed()
        },1000)

    }



  private fun StoreSessionManager(uesedata: List<RegisterPojo.Data>) {

        val gson = Gson()

        val json = gson.toJson(uesedata[0])

      sessionManager!!.create_login_session(
            json,
            uesedata[0].userMobile,
            "",
            true,
            sessionManager!!.isEmailLogin(),
          uesedata[0]!!.userFirstName!!+" "+uesedata[0]!!.userLastName!!,
          uesedata[0]!!.userProfilePicture!!

          )
        languageAdapter?.notifyDataSetChanged()

    }

    private fun dynamicLable() {
//        headertitle=(activity as MainActivity)?.resources?.getString(R.string.change_language)!!
        headertitle=GetDynamicStringDictionaryObjectClass?.Change_Language
    }
}
