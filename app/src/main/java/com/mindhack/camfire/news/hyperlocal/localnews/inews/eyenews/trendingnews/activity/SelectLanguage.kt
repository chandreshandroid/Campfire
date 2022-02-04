package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.SelectLanguageAdapter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.Nullable
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.GetDynamicStringDictionaryModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.GetLanguageListModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GetDynamicStringDictionaryPojo1
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GetLanguageListPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.flymyowncustomer.util.PrefDb
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.select_language.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException

class SelectLanguage : AppCompatActivity() {

    var arrayLanguageList: ArrayList<GetLanguageListPojo.Datum?>? = null
    var adapterLanguageList: SelectLanguageAdapter? = null
    var arrayDynamicStringDictionaryList: ArrayList<GetDynamicStringDictionaryPojo1>? = null
    var layoutManger: LinearLayoutManager? = null

    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var pageNo = 0
    var relativeprogressBar: RelativeLayout? = null
    var noDatafoundRelativelayout: RelativeLayout? = null
    var nointernetMainRelativelayout: RelativeLayout? = null
    var selectLanguageLayoutMain: LinearLayoutCompat? = null
    var languageId: String? = ""
    var sessionManager: SessionManager? = null
//    var sessionManager: SessionManager? = null
//    var userData: RegisterNewPojo.Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_language)
        sessionManager = SessionManager(this@SelectLanguage)
        relativeprogressBar = findViewById(R.id.relativeprogressBar) as RelativeLayout
        noDatafoundRelativelayout = findViewById(R.id.noDatafoundRelativelayout) as RelativeLayout
        nointernetMainRelativelayout =
            findViewById(R.id.nointernetMainRelativelayout) as RelativeLayout
        selectLanguageLayoutMain = findViewById(R.id.selectLanguageLayoutMain) as LinearLayoutCompat
        selectLanguageButtonContinue?.visibility = View.VISIBLE
        layoutManger = LinearLayoutManager(this@SelectLanguage)
        selectLanguageRecyclerview?.layoutManager = layoutManger

        setLanguageAdapter()

        selectLanguageButtonContinue?.setOnClickListener {

            if (languageId!!.isEmpty()) {
                /* MyUtils.showSnackbarkotlin(
                     this@SelectLanguage,
                     selectLanguageLayoutMain!!,
                     "Please Select Language First")*/
            } else {
                if (MyUtils.isInternetAvailable(this@SelectLanguage)) {
                    getGetDynamicStringDictionaryPojoList()
                } else {
                    MyUtils.showSnackbarkotlin(
                        this@SelectLanguage,
                        selectLanguageLayoutMain!!,
                        resources.getString(R.string.error_common_network)
                    )
                }

            }

        }

        btnRetry.setOnClickListener {
            getLanguageData()
        }
    }

    fun setLanguageOrNot(arrayLanguageList: ArrayList<GetLanguageListPojo.Datum?>): Int {

        var pos = -1
        if (!arrayLanguageList.isNullOrEmpty()) {
            for (i in 0 until arrayLanguageList!!.size) {
                if (arrayLanguageList!![i]?.isSelected!!) {
                    pos = i
                    break
                }
            }
        }

        return pos
    }

    private fun setLanguageAdapter() {
        if (arrayLanguageList.isNullOrEmpty()) {
            arrayLanguageList = ArrayList<GetLanguageListPojo.Datum?>()
            adapterLanguageList = SelectLanguageAdapter(
                this@SelectLanguage,
                arrayLanguageList!!,
                object : SelectLanguageAdapter.BtnClickListener {
                    override fun onBtnClick(position: Int, buttonName: String) {
                        for (i in 0 until arrayLanguageList!!.size) {

                            if (i == position) {
                                if (arrayLanguageList!![i]?.isSelected!!) {
                                    arrayLanguageList!![i]?.isSelected = false
                                } else {
                                    arrayLanguageList!![i]?.isSelected = true
                                }
                            } else {
                                arrayLanguageList!![i]?.isSelected = false
                            }
                        }
                        adapterLanguageList?.notifyDataSetChanged()

                        var selectionPos = setLanguageOrNot(arrayLanguageList!!)
                        if (selectionPos > -1) {
                            selectLanguageButtonContinue.isEnabled = true
                            selectLanguageButtonContinue.setBackgroundColor(resources.getColor(R.color.bg_button))
                            selectLanguageButtonContinue?.visibility = View.VISIBLE
                            languageId = arrayLanguageList!![selectionPos]?.languageID!!.toString()
                            //PrefDb(this@SelectLanguage).putString( MyUtils.SharedPreferencesenum.languageId.toString(), arrayLanguageList!![selectionPos]?.languageID!!)
                            sessionManager?.setSelectedLanguage(languageId)

                        } else {
                            languageId = ""
                            selectLanguageButtonContinue.setBackgroundColor(resources.getColor(R.color.eneble))

                            selectLanguageButtonContinue?.visibility = View.VISIBLE
                            selectLanguageButtonContinue.isEnabled = false

                        }
                    }
                })
            selectLanguageRecyclerview?.setHasFixedSize(true)
            selectLanguageRecyclerview?.adapter = adapterLanguageList
            adapterLanguageList?.notifyDataSetChanged()
            getLanguageData()
        }
    }

    private fun getLanguageData() {

        if (MyUtils.internetConnectionCheck(this@SelectLanguage) == true) {
            relativeprogressBar?.visibility = View.VISIBLE
            nointernetMainRelativelayout?.visibility = View.GONE
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            try {
//                jsonObject.put("loginuserID", "0")
                jsonObject.put("languageID", "")
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)
                jsonArray.put(jsonObject)
//                Log.d(TAG, "Language api call := " + jsonArray.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            var getLanguageModel =
                ViewModelProviders.of(this@SelectLanguage).get(GetLanguageListModel::class.java)
            getLanguageModel.apiFunction(this@SelectLanguage!!, false, jsonArray.toString())
                .observe(this@SelectLanguage, object :
                    Observer<List<GetLanguageListPojo>> {
                    override fun onChanged(@Nullable languagePoJos: List<GetLanguageListPojo>?) {

                        if (languagePoJos != null) {
                            relativeprogressBar?.visibility = View.GONE

                            if (languagePoJos[0]?.status.equals("true", true)) {
                                if (languagePoJos[0]?.data!!.size > 0) {

                                    arrayLanguageList?.addAll(languagePoJos[0]?.data!!)
                                    adapterLanguageList!!.notifyDataSetChanged()
                                }
                            } else {
                                if (!languagePoJos[0]?.message.isNullOrEmpty()) {
                                    MyUtils.showSnackbarkotlin(
                                        this@SelectLanguage,
                                        selectLanguageLayoutMain!!,
                                        "" + languagePoJos[0]?.message
                                    )
                                }

                            }
                        } else {
                            nodatafound()
                        }
                    }
                })

        } else {
            nodatafound()
        }
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

        val getDynamicStringDictionaryModel = ViewModelProviders.of(this@SelectLanguage).get(
            GetDynamicStringDictionaryModel::class.java
        )
        getDynamicStringDictionaryModel.getDynamicStringDictionaryresponseJson(
            this@SelectLanguage,
            true,
            jsonArray.toString()
        )
            .observe(this@SelectLanguage, object :
                Observer<List<GetDynamicStringDictionaryPojo1>> {
                override fun onChanged(response: List<GetDynamicStringDictionaryPojo1>?) {

                    if (response != null && response.size > 0) {
                        if (response[0].status.equals("true")) {
                            arrayDynamicStringDictionaryList =
                                ArrayList<GetDynamicStringDictionaryPojo1>()
                            arrayDynamicStringDictionaryList?.clear()
                            arrayDynamicStringDictionaryList?.addAll(response)

                            storePrefDb(arrayDynamicStringDictionaryList!!)
                            saveLanguage()

                        } else {
                            arrayDynamicStringDictionaryList?.clear()

                            //No data -- No internet
                            if (MyUtils.isInternetAvailable(this@SelectLanguage)) {
                                MyUtils.showSnackbarkotlin(
                                    this@SelectLanguage,
                                    selectLanguageLayoutMain!!,
                                    resources.getString(R.string.error_crash_error_message)
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@SelectLanguage,
                                    selectLanguageLayoutMain!!,
                                    resources.getString(R.string.error_common_network)
                                )
                            }
                        }
                    } else {
                        arrayDynamicStringDictionaryList?.clear()

                        //No internet -- Somthing went rong
                        if (MyUtils.isInternetAvailable(this@SelectLanguage)) {
                            MyUtils.showSnackbarkotlin(
                                this@SelectLanguage,
                                selectLanguageLayoutMain!!,
                                resources.getString(R.string.error_crash_error_message)
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@SelectLanguage,
                                selectLanguageLayoutMain!!,
                                resources.getString(R.string.error_common_network)
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
        PrefDb(this@SelectLanguage).setDynamicStringDictionary(json)
    }

    private fun saveLanguage() {
        PrefDb(this@SelectLanguage).putString(
            MyUtils.SharedPreferencesenum.languageId.toString(),
            languageId!!
        )
        var updateGetDynamicStringDictionary =
            GetDynamicStringDictionaryObjectClass.getInstance(this@SelectLanguage)
        if (sessionManager?.isLoggedIn()!!) {
            val myIntent = Intent(this@SelectLanguage, MainActivity::class.java)
            startActivity(myIntent)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            MyUtils.startActivity(this@SelectLanguage, SignupActivity::class.java, true)
        }

    }

    fun nodatafound() {
        relativeprogressBar?.visibility = View.GONE
        try {
            nointernetMainRelativelayout?.visibility = View.VISIBLE
            if (MyUtils.isInternetAvailable(this@SelectLanguage)) {
                nointernetImageview.setImageDrawable(this@SelectLanguage.getDrawable(R.drawable.something_went_wrong))
                nointernettextview.text = (this@SelectLanguage.getString(R.string.somethigwrong1))
                nointernettextview1.text = (this.getString(R.string.somethigwrong1))
            } else {
                nointernetImageview.setImageDrawable(this@SelectLanguage.getDrawable(R.drawable.no_internet_connection))
                nointernettextview1.text = (this.getString(R.string.internetmsg1))
                nointernettextview.text =
                    (this@SelectLanguage.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
