package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GetDynamicStringDictionaryPojo1
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils


import retrofit2.Call
import retrofit2.Response

class GetDynamicStringDictionaryModel : ViewModel(){

    lateinit var DynamicStringDictionaryresponse: LiveData<List<GetDynamicStringDictionaryPojo1>>
    lateinit var mContext: Context
    var isShowing: Boolean = false
    var json: String = ""

    fun getDynamicStringDictionaryresponseJson(
        context: Context, isShowing: Boolean,
        json: String
    ): LiveData<List<GetDynamicStringDictionaryPojo1>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json
        DynamicStringDictionaryresponse = getDynamicStringDictionaryresponseJsonFun()
        return DynamicStringDictionaryresponse
    }

    private fun getDynamicStringDictionaryresponseJsonFun(): LiveData<List<GetDynamicStringDictionaryPojo1>> {
        if (isShowing) {
            MyUtils.showProgressDialog(this!!.mContext!!)
        }
        val data = MutableLiveData<List<GetDynamicStringDictionaryPojo1>>()
        val call = RestClient.get()!!.getDynamicStringDictionary(json)
        call.enqueue(object : retrofit2.Callback<List<GetDynamicStringDictionaryPojo1>> {
            override fun onResponse(call: Call<List<GetDynamicStringDictionaryPojo1>>, response: Response<List<GetDynamicStringDictionaryPojo1>>) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = response.body()
            }

            override fun onFailure(call: Call<List<GetDynamicStringDictionaryPojo1>>, t: Throwable) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = null
            }
        })
        return data
    }



}