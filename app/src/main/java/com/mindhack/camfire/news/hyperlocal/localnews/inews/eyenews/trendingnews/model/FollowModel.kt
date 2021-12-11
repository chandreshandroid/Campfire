package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.FollowPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Call
import retrofit2.Response

class FollowModel : ViewModel() {

     var userLogin: LiveData<List<FollowPojo>?>?=null
     var mContext: Activity?=null
    var from: String = ""
    var jsonArray: String = ""

    fun apiCall(
        context: Activity, jsonArray: String,from:String
    ): LiveData<List<FollowPojo>?> {
        this.jsonArray = jsonArray
        this.mContext = context
        this.from = from
        userLogin = apiResponse()
        return userLogin!!
    }

    private fun apiResponse(): LiveData<List<FollowPojo>?> {
        val data = MutableLiveData<List<FollowPojo>>()
        var call: Call<List<FollowPojo>>?=null

        call = RestClient.get()!!.getUserFollow(jsonArray)

        if (call != null) {
            call.enqueue(object : RestCallback<List<FollowPojo>?>(mContext) {
                override fun Success(response: Response<List<FollowPojo>?>) {
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