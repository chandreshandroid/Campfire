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

class DeleteNotificationModel : ViewModel() {

    lateinit var deleteaddressresponse: LiveData<List<CommonPojo>>
    lateinit var mContext: Context
    var isShowing: Boolean = false
    var json: String = ""
    var from: String = ""

    fun getDeleteNotification(
        context: Context, isShowing: Boolean,
        json: String,from:String): LiveData<List<CommonPojo>> {

        this.mContext = context
        this.isShowing = isShowing
        this.json = json
        this.from = from
        deleteaddressresponse = getDeleteNotificationFun()

        return deleteaddressresponse
    }

    private fun getDeleteNotificationFun(): LiveData<List<CommonPojo>> {
        try {
            if (isShowing)
                MyUtils.showProgressDialog(this!!.mContext!!)
        } catch (e: Exception) {
        }
        val data = MutableLiveData<List<CommonPojo>>()
        var call:Call<List<CommonPojo>> ?=null
           when(from)
           {
               "delete"->{
                   call  = RestClient.get()!!.deleteNotificationApi(json)

               }
               "read"->{
                   call  = RestClient.get()!!.allReadNotificationApi(json)

               }
               "oneread"->{
                   call  = RestClient.get()!!.getReadNotificationApi(json)

               }
           }

        call?.enqueue(object : retrofit2.Callback<List<CommonPojo>> {
            override fun onFailure(call: Call<List<CommonPojo>>, t: Throwable) {
                try {
                    if (isShowing)
                        MyUtils.closeProgress()
                } catch (e: Exception) {
                }
                data.value = null
            }

            override fun onResponse(call: Call<List<CommonPojo>>, response: Response<List<CommonPojo>>) {
                try {
                    if (isShowing)
                        MyUtils.closeProgress()
                } catch (e: Exception) {
                }
                data.value = response.body()
            }
        })
        return data
    }
}