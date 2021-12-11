package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Response

class FollowingModel : ViewModel() {

    lateinit var userLogin: LiveData<List<CommonPojo>?>
    lateinit var mContext: Activity
    var from: Int = -1
    var jsonArray: String = ""

    fun apiCall(
        context: Activity, jsonArray: String
    ): LiveData<List<CommonPojo>?> {
        this.jsonArray = jsonArray
        this.mContext = context
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<CommonPojo>?> {
        val data = MutableLiveData<List<CommonPojo>>()
        val call = RestClient.get()!!.getPostView(jsonArray)

        if (call != null) {
            call.enqueue(object : RestCallback<List<CommonPojo>?>(mContext) {
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