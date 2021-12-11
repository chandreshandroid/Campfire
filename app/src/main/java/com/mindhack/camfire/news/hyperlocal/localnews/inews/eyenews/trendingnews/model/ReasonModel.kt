package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.ReasonList
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import retrofit2.Response

class ReasonModel : ViewModel() {

    lateinit var userLogin: LiveData<List<ReasonList>?>
    lateinit var mContext: Context
    var from: Int = -1
    var jsonArray: String = ""
    var progress: Boolean = false

    fun apiFunction(
        context: Context, progress1 : Boolean,jsonArray: String
    ): LiveData<List<ReasonList>?> {
        this.progress = progress1
        this.jsonArray = jsonArray
        this.mContext = context
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<ReasonList>?> {

        if (progress){
            MyUtils.showProgressDialog(mContext)
        }

        val data = MutableLiveData<List<ReasonList>>()
        var call = RestClient.get()!!.reasonList(jsonArray)

        if (call != null) {
            call.enqueue(object : RestCallback<List<ReasonList>?>(mContext) {


                override fun Success(response: Response<List<ReasonList>?>) {
                    if (progress){
                        MyUtils.closeProgress()
                    }
                    data.value = response.body()
                }
                override fun failure() {
                    if (progress){
                        MyUtils.closeProgress()
                    }
                    data.value = null
                }
            })
        }
        return data
    }
}