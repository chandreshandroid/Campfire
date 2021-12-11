package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GetLanguageListPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import retrofit2.Call
import retrofit2.Response

class GetLanguageListModel : ViewModel(){

    lateinit var getResponse: LiveData<List<GetLanguageListPojo>>
    lateinit var mContext: Context
    var isShowing: Boolean = false
    var json: String = ""

    fun apiFunction(context: Context, isShowing: Boolean,
                    json: String): LiveData<List<GetLanguageListPojo>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json
        getResponse = apiREsponse()
        return getResponse
    }
    private fun apiREsponse(): LiveData<List<GetLanguageListPojo>> {
        if (isShowing) {
            MyUtils.showProgressDialog(this!!.mContext!!)
        }
        val data = MutableLiveData<List<GetLanguageListPojo>>()
        val call = RestClient.get()!!.getLanguageListApi(json)
        call.enqueue(object : retrofit2.Callback<List<GetLanguageListPojo>> {
            override fun onResponse(call: Call<List<GetLanguageListPojo>>, response: Response<List<GetLanguageListPojo>>) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = response.body()
            }
            override fun onFailure(call: Call<List<GetLanguageListPojo>>, t: Throwable) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = null
            }
        })
        return data
    }

}