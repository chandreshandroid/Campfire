package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.PostLikeListPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Call
import retrofit2.Response

class PostLikeListModel : ViewModel() {

     var userLogin: LiveData<List<PostLikeListPojo>?>?=null
     var mContext: Activity?=null
     var from: String = ""
     var jsonArray: String = ""

    fun apiCall(
        context: Activity, jsonArray: String,from:String
    ): LiveData<List<PostLikeListPojo>?> {
        this.jsonArray = jsonArray
        this.mContext = context
        this.from = from
        userLogin = apiResponse()
        return userLogin!!
    }

    private fun apiResponse(): LiveData<List<PostLikeListPojo>?> {
        val data = MutableLiveData<List<PostLikeListPojo>>()
        var call: Call<List<PostLikeListPojo>>?=null

        call = RestClient.get()!!.getPostLikeList(jsonArray)

        call.enqueue(object : RestCallback<List<PostLikeListPojo>?>(mContext) {
            override fun Success(response: Response<List<PostLikeListPojo>?>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }
        })
        return data
    }
}