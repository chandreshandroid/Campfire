package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Call
import retrofit2.Response

class OtpResendAndCheckDuplicationModel : ViewModel() {

    lateinit var userLogin: LiveData<List<CommonPojo>?>
    lateinit var mContext: Context
    var from: Int = -1
    var jsonArray: String = ""

    fun apiCall(
        context: Context, jsonArray: String, apiName: Int
    ): LiveData<List<CommonPojo>?> {
        this.jsonArray = jsonArray
        this.mContext = context
        this.from = apiName
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<CommonPojo>?> {
        val data = MutableLiveData<List<CommonPojo>>()
        var call: Call<List<CommonPojo>>? = null
        /**
         *
         * 0 for otp resend
         * 1 for check user duplication
         * 2 for check user mentioned id
         * */
        when(from){
            0 -> call = RestClient.get()!!.otpResend(jsonArray)
            1 -> call = RestClient.get()!!.checkUserDuplication(jsonArray)
            2 -> call = RestClient.get()!!.checkMentionedDuplication(jsonArray)
        }
        if (call != null) {
            call.enqueue(object : RestCallback<List<CommonPojo>?>(mContext) {
                override fun Success(response: Response<List<CommonPojo>?>) {
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