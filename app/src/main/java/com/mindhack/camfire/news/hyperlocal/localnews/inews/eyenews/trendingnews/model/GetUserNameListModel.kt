package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.UserName
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import retrofit2.Call
import retrofit2.Response

class GetUserNameListModel : ViewModel(){

    lateinit var getResponse: LiveData<List<UserName>>
    lateinit var mContext: Context
    var isShowing: Boolean = false
    var json: String = ""

    fun apiFunction(context: Context, isShowing: Boolean,
                    json: String): LiveData<List<UserName>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json
        getResponse = apiREsponse()
        return getResponse
    }
    private fun apiREsponse(): LiveData<List<UserName>> {
        if (isShowing) {
            MyUtils.showProgressDialog(this!!.mContext!!)
        }
        val data = MutableLiveData<List<UserName>>()
        val call = RestClient.get()!!.generateUserId(json)
        call.enqueue(object : retrofit2.Callback<List<UserName>> {
            override fun onResponse(call: Call<List<UserName>>, response: Response<List<UserName>>) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = response.body()
            }
            override fun onFailure(call: Call<List<UserName>>, t: Throwable) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = null
            }
        })
        return data
    }

}