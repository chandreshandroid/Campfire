package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.UserForgotPassword
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Response

class ForgotPasswordModel : ViewModel() {

    lateinit var userLogin: LiveData<List<UserForgotPassword>?>
    lateinit var mContext: Context
    var from: Int = -1
    var jsonArray: String = ""

    fun apiCall(
        context: Context, jsonArray: String
    ): LiveData<List<UserForgotPassword>?> {
        this.jsonArray = jsonArray
        this.mContext = context
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<UserForgotPassword>?> {
        val data = MutableLiveData<List<UserForgotPassword>>()
        val call = RestClient.get()!!.userForgotPassword(jsonArray)

        if (call != null) {
            call.enqueue(object : RestCallback<List<UserForgotPassword>?>(mContext) {
                override fun Success(response: Response<List<UserForgotPassword>?>) {
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