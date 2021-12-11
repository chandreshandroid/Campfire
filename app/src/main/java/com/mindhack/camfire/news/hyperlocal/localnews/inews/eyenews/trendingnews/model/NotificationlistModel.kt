package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.NotificationListPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient


import retrofit2.Response

class NotificationlistModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<NotificationListPojo>>
    lateinit var mContext: Context

    var isShowing:Boolean = false
    lateinit var pbDialog:Dialog
    var json: String = ""

    var searchkeyword: String = ""

    fun  getNotificationList(
        context: Context,
        isShowing: Boolean,
        json: String): LiveData<List<NotificationListPojo>> {
        this.json = json

        this.mContext = context
        this.isShowing = isShowing;
        this.searchkeyword = searchkeyword
        languageresponse = getNotificationListApi()

        return languageresponse
    }

    private fun getNotificationListApi(): LiveData<List<NotificationListPojo>> {
        val data = MutableLiveData<List<NotificationListPojo>>()

        if (isShowing) {
            showPb()
        }

        var call = RestClient.get()!!.getNotificationListApi(json)
        call.enqueue(object : RestCallback<List<NotificationListPojo>>(mContext) {
            override fun Success(response: Response<List<NotificationListPojo>>) {
                data.value=response.body()
            }

            override fun failure() {
                data.value=null
            }

        })





        return data
    }

    private fun closePb() {
        pbDialog.dismiss()
    }

    private fun showPb() {
        pbDialog = Dialog(mContext)
        pbDialog.show()
    }


}


