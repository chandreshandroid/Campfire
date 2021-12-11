package com.mindhack.flymyowncustomer.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils

import retrofit2.Call
import retrofit2.Response

class ResetPasswordModel : ViewModel(){

    lateinit var getResponse: LiveData<List<RegisterPojo>>
    lateinit var mContext: Context
    var isShowing: Boolean = false
    var json: String = ""

    fun apiFunction(context: Context, isShowing: Boolean,
                          json: String): LiveData<List<RegisterPojo>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json
        getResponse = apiREsponse()
        return getResponse
    }
    private fun apiREsponse(): LiveData<List<RegisterPojo>> {
        if (isShowing) {
            MyUtils.showProgressDialog(this!!.mContext!!)
        }
        val data = MutableLiveData<List<RegisterPojo>>()
        val call = RestClient.get()!!.userResetPassword(json)
        call.enqueue(object : retrofit2.Callback<List<RegisterPojo>> {
            override fun onResponse(call: Call<List<RegisterPojo>>, response: Response<List<RegisterPojo>>) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = response.body()
            }
            override fun onFailure(call: Call<List<RegisterPojo>>, t: Throwable) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = null
            }
        })
        return data
    }

}