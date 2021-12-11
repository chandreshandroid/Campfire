package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Call
import retrofit2.Response

class OnBoardingModel : ViewModel() {

    lateinit var userLogin: LiveData<List<RegisterPojo>?>
    lateinit var mContext: Context
    var from: Int = -1
    var jsonArray: String = ""

    fun apiCall(
        context: Context, jsonArray: String, apiName: Int
    ): LiveData<List<RegisterPojo>?> {
        this.jsonArray = jsonArray
        this.mContext = context
        this.from = apiName
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<RegisterPojo>?> {
        val data = MutableLiveData<List<RegisterPojo>?>()
        var call: Call<List<RegisterPojo>>? = null
        /**
         *
         * 0 for user register
         * 1 for otp verification
         * 2 for user login otp
         * 3 for user login password
         * 4 for user update profile picture
         * 5 for user update device token
         * 6 for user update settings
         * 7 for reset password
         * 8 fro change password
         * 9 for user update profile
         * 10 for social login
         * 13 for add signuture_Video*/
        when (from) {
            0 -> {
                call = RestClient.get()!!.userRegister(jsonArray)
                Log.e("Logs", "API : " + call.request())
                Log.e("Logs", "Verofy OTP")
            }
            1 -> call = RestClient.get()!!.otpVerification(jsonArray)
            2 -> call = RestClient.get()!!.loginWithOtp(jsonArray)
            3 -> call = RestClient.get()!!.loginWithPassword(jsonArray)
            4 -> call = RestClient.get()!!.updateProfilePicture(jsonArray)
            5 -> call = RestClient.get()!!.updateDeviceToken(jsonArray)
            6 -> call = RestClient.get()!!.updateSettings(jsonArray)
            7 -> call = RestClient.get()!!.userResetPassword(jsonArray)
            8 -> call = RestClient.get()!!.userChangePassword(jsonArray)
            9 -> call = RestClient.get()!!.userUpdateProfile(jsonArray)
            10 -> call = RestClient.get()!!.socialLogin(jsonArray)
            11 -> {
                call = RestClient.get()!!.getUpdateChangeLanguage(jsonArray)
                Log.e("Logs", "API : " + call.request())
                Log.e("Logs", "Change Language")
            }
            12 -> call = RestClient.get()!!.getUpdateDeleteAccount(jsonArray)
            13 -> call = RestClient.get()!!.getUserAddSignatureVideo(jsonArray)
        }
        if (call != null) {
            call.enqueue(object : RestCallback<List<RegisterPojo>?>(mContext) {
                override fun Success(response: Response<List<RegisterPojo>?>) {
                    Log.w("Logs", "" + response.body()?.get(0)?.status)
                    Log.e("Logs", "Response : " + response)

                    data.value = response.body()
                }

                override fun failure() {
                    data.value = null
                    Log.w("Logs", "NULL")

                }
            })
        }
        return data
    }
}