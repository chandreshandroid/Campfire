package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.MyViewPoints
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Response

class MyViewPointsModel : ViewModel() {

    lateinit var userLogin: LiveData<List<MyViewPoints>?>
    lateinit var mContext: Activity
    var from: Int = -1
    var jsonArray: String = ""

    fun apiCall(
        context: Activity, jsonArray: String
    ): LiveData<List<MyViewPoints>?> {
        this.jsonArray = jsonArray
        this.mContext = context
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<MyViewPoints>?> {
        val data = MutableLiveData<List<MyViewPoints>>()
        val call = RestClient.get()!!.getUserPoints(jsonArray)

        if (call != null) {
            call.enqueue(object : RestCallback<List<MyViewPoints>?>(mContext) {
                override fun Success(response: Response<List<MyViewPoints>?>) {
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