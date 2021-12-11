package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient

import retrofit2.Call
import retrofit2.Response

class ReportPostModel : ViewModel() {

    lateinit var getDataList: LiveData<List<CommonPojo>?>
    lateinit var mContext: Context
    var apiName: String = ""
    var json: String = ""

    fun apiFunction(context: Context, json: String,apiName: String): LiveData<List<CommonPojo>?> {
        this.json = json
        this.mContext = context
        this.apiName = apiName
        return apiResponse()
    }

    private fun apiResponse(): LiveData<List<CommonPojo>?> {
        val data = MutableLiveData<List<CommonPojo>>()

        var call: Call<List<CommonPojo>>? = null
        if (apiName.equals("PostReport", true)) {
            call = RestClient.get()!!.getReportPost(json)
        }else if (apiName.equals("PostHide", true)) {
            call = RestClient.get()!!.getHidePost(json)
        }else if (apiName.equals("PostInActive", true)) {
            call = RestClient.get()!!.getPostInActive(json)
        }else if (apiName.equals("PostActive", true)) {
            call = RestClient.get()!!.getPostActive(json)
        }else if (apiName.equals("UserRePort", true)) {
            call = RestClient.get()!!.getUserReport(json)
        }

        if (call != null) {
            call?.enqueue(object : RestCallback<List<CommonPojo>?>(mContext) {
                override fun Success(response: Response<List<CommonPojo>?>) {
                    data.value = response.body()
                }

                override fun failure() {
                    data.value = null
                }
            })

        }

        return data
    }
}