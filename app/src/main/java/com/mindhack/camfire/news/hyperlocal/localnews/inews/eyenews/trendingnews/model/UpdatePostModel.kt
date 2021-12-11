package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import retrofit2.Call
import retrofit2.Response

class UpdatePostModel : ViewModel(){

     var getResponse: LiveData<List<CommonPojo>>?=null
     var mContext: Context?=null
    var isShowing: Boolean = false
    var json: String = ""

    fun apiFunction(context: Context, isShowing: Boolean,
                    json: String): LiveData<List<CommonPojo>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json
        getResponse = apiREsponse()
        return getResponse!!
    }
    private fun apiREsponse(): LiveData<List<CommonPojo>> {
        if (isShowing) {
            MyUtils.showProgressDialog(this!!.mContext!!)
        }
        val data = MutableLiveData<List<CommonPojo>>()

        val call = RestClient.get()!!.updatePost(json)
        call.enqueue(object : retrofit2.Callback<List<CommonPojo>> {
            override fun onResponse(call: Call<List<CommonPojo>>, response: Response<List<CommonPojo>>) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = response.body()
            }
            override fun onFailure(call: Call<List<CommonPojo>>, t: Throwable) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = null
            }
        })
        return data
    }

}