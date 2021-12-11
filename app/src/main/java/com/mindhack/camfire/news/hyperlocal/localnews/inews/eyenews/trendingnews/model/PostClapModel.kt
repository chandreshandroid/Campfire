package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.PostClapPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Call
import retrofit2.Response

class PostClapModel : ViewModel() {

    lateinit var userLogin: LiveData<List<PostClapPojo>?>
    lateinit var mContext: Activity
    var from: Int = -1
    var jsonArray: String = ""
    var type=""

    fun apiCall(context: Activity, jsonArray: String,type:String): LiveData<List<PostClapPojo>?> {
        this.jsonArray = jsonArray
        this.mContext = context
        this.type=type
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<PostClapPojo>?> {
        val data = MutableLiveData<List<PostClapPojo>>()
        var call: Call<List<PostClapPojo>>? = null
        when(type)
        {

            "ClapPost"->{
                call = RestClient.get()!!.getUserClapPost(jsonArray)
            }
            "UnClapPost"->{
                call = RestClient.get()!!.getUserUnClapPost(jsonArray)
            }
        }

        call?.enqueue(object : RestCallback<List<PostClapPojo>?>(mContext) {
            override fun Success(response: Response<List<PostClapPojo>?>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }
        })
        return data
    }
}