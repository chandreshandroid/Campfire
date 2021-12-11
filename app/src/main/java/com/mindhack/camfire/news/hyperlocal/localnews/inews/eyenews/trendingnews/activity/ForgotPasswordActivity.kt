package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.ForgotPasswordModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.UserForgotPassword
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.gson.JsonParseException
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.json.JSONArray
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

     var emailPhoneValidation:String=""
    var headertitle = ""
    var forgot_err_enter_reg_mo = ""
    var err_enter_reg_mo = ""
    var reg_err_enter_valid_emailid = ""
    var msgFailToRegister = ""
    var msgSomthingRong = ""
    var msgNoInternet = ""

    var keyUserMobileCountryCode = "keyUserMobileCountryCode"
    var keyUserId = "keyUserId"
    var keyUserMobile = "keyUserMobile"
    var keyUserMobileRes = "keyUserMobileRes"
    var keyUserEmail = "keyUserEmail"
    var sendUserId = ""
    var msg_success_to_send = ""
    var msg_failed_to_send = ""
    var sessionManager: SessionManager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
              dynamicLable()
        sessionManager= SessionManager(this@ForgotPasswordActivity)

        forgotpasswordEditEmailMobile?.setHint(""+GetDynamicStringDictionaryObjectClass?.Enter_your_Mobile_NoEmail_Id)
        forgotPasswordButton?.progressText = GetDynamicStringDictionaryObjectClass?.Send_OTP
        var strForgot = GetDynamicStringDictionaryObjectClass?.titleForgotPWD
        tvHeaderText.text = ""+strForgot

        imgCloseIcon.setOnClickListener {
               onBackPressed()
        }
        forgotPasswordButton?.setOnClickListener {
            MyUtils.hideKeyboardFrom(this@ForgotPasswordActivity, rootForgotLayout)
            forgotpasswordEditEmailMobile.error = null // Clear the error

            if (forgotPasswordValidation()) {
                if (MyUtils.isInternetAvailable(this@ForgotPasswordActivity)){
                    loginAPI()
                }else{
                    MyUtils.showSnackbarkotlin(this@ForgotPasswordActivity, rootForgotLayout, msgNoInternet)
                }
            }
        }


    }

    private fun forgotPasswordValidation() : Boolean {

            var checkFlag = true
            var loginType = TextUtils.isDigitsOnly(forgotpasswordEditEmailMobile!!.text.toString().trim())

            if (forgotpasswordEditEmailMobile?.text.toString().trim().isEmpty()) {
                MyUtils.showSnackbarkotlin(
                    this@ForgotPasswordActivity,
                    rootForgotLayout,
                    emailPhoneValidation
                )
                checkFlag = false
            } else if (loginType && (forgotpasswordEditEmailMobile?.text.toString().trim().length < 10)) {
                MyUtils.showSnackbarkotlin(
                    this@ForgotPasswordActivity,
                    rootForgotLayout,
                    forgot_err_enter_reg_mo
                )
                checkFlag = false
            }else if (loginType && (forgotpasswordEditEmailMobile?.text.toString().trim().length > 10)) {
                MyUtils.showSnackbarkotlin(
                    this@ForgotPasswordActivity,
                    rootForgotLayout,
                    err_enter_reg_mo
                )
                checkFlag = false
            } else if (!loginType && !MyUtils.isEmailValid(forgotpasswordEditEmailMobile?.text.toString().trim())) {
                MyUtils.showSnackbarkotlin(
                    this@ForgotPasswordActivity,
                    rootForgotLayout,
                    reg_err_enter_valid_emailid
                )
                checkFlag = false
            }

            return checkFlag

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        MyUtils.finishActivity(this@ForgotPasswordActivity,true)
    }
    private fun dynamicLable() {
        headertitle = resources.getString(R.string.forgot_passwordS)
        emailPhoneValidation = resources.getString(R.string.forgot_err_msg)
        forgot_err_enter_reg_mo = resources.getString(R.string.forgot_err_enter_reg_mo)
        err_enter_reg_mo = resources.getString(R.string.err_enter_reg_mo)
        reg_err_enter_valid_emailid = resources.getString(R.string.reg_err_enter_valid_emailid)

        msgFailToRegister = "Failed to forgot password if you have any query please contact to admin."
        msgSomthingRong =  GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong
        msgNoInternet =  GetDynamicStringDictionaryObjectClass?.No_Internet_Connection
        msg_success_to_send = "OTP sent successfully."
        msg_failed_to_send = "This mobile number or email address not registered."
    }

    fun loginAPI() {

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        forgotPasswordButton?.startAnimation()
        MyUtils.setViewAndChildrenEnabled(rootForgotLayout, false)
        // users/user-forgot-password
        /*[{
	"userEmail": "",
	"userCountryCode": "91",
	"languageID": "1",
	"userMobile": "9824488033",
	"apiType": "Android",
	"apiVersion": "1.0"
}]*/

        try {

            val loginType = TextUtils.isDigitsOnly(forgotpasswordEditEmailMobile?.text.toString().trim())

            if (loginType && (forgotpasswordEditEmailMobile?.text.toString().trim().length >= 10)) {
                jsonObject.put("userMobile",forgotpasswordEditEmailMobile?.text.toString().trim())
                jsonObject.put("userEmail", "")
                jsonObject.put("userCountryCode", "+91")
            } else {
                jsonObject.put("userMobile", "")
                jsonObject.put("userEmail", forgotpasswordEditEmailMobile?.text.toString().trim())
                jsonObject.put("userCountryCode", "")
            }
//            jsonObject.put("userMobile", forgotpasswordEditEmailMobile?.text.toString().trim())

            //jsonObject.put("languageID", PrefDb(this@ForgotPasswordActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()))

            jsonObject.put("languageID", sessionManager?.getsetSelectedLanguage())

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        }catch (e : Exception){
            e.printStackTrace()
        }catch (e : JsonParseException){
            e.printStackTrace()
        }
        Log.e("System Out", "Forgot password Request $jsonArray")

        val userLogin = ViewModelProviders.of(this@ForgotPasswordActivity).get(ForgotPasswordModel::class.java)
        userLogin.apiCall(this@ForgotPasswordActivity, jsonArray.toString()).observe(this@ForgotPasswordActivity,
            object : Observer<List<UserForgotPassword>?> {
                override fun onChanged(response: List<UserForgotPassword>?) {
                    if (!response.isNullOrEmpty()){
                        if (response.size > 0){

                            if (response[0].status.equals("true", true)){
                                forgotPasswordButton?.endAnimation()
                                MyUtils.setViewAndChildrenEnabled(rootForgotLayout, true)
                                forgotpasswordEditEmailMobile?.setText("")
                                MyUtils.hideKeyboardFrom(this@ForgotPasswordActivity, forgotPasswordButton)
                                if (!response[0].message.isNullOrEmpty()){
                                    MyUtils.showSnackbarkotlin(this@ForgotPasswordActivity, rootForgotLayout, response[0].message!!)
                                }else{
                                    MyUtils.showSnackbarkotlin(this@ForgotPasswordActivity, rootForgotLayout, msg_success_to_send)
                                }

                                if (!response[0].data.isNullOrEmpty()){
                                    if (response[0].data!!.size > 0){
                                        sendUserId = response[0].data!![0]?.userID!!
                                        Handler().postDelayed({
                                            val myIntent = Intent(this@ForgotPasswordActivity, OtpVerificationActivity::class.java)
                                            myIntent.putExtra(keyUserId, sendUserId)
                                            myIntent.putExtra(keyUserMobile, forgotpasswordEditEmailMobile?.text.toString())
                                            myIntent.putExtra(keyUserEmail, forgotpasswordEditEmailMobile?.text.toString())
                                            myIntent.putExtra(keyUserMobileRes, response[0].data!![0]?.userMobile)
                                            startActivity(myIntent)
                                        }, 2000)
                                    }
                                }
                            }else{
                                forgotPasswordButton?.endAnimation()
                                MyUtils.setViewAndChildrenEnabled(rootForgotLayout, true)
                                //No data and no internet
                                if (MyUtils.isInternetAvailable(this@ForgotPasswordActivity)){
                                    if(!response[0].message!!.isNullOrEmpty()){
                                        MyUtils.showSnackbarkotlin(this@ForgotPasswordActivity, rootForgotLayout, response[0].message!!)
                                    }else{
                                        MyUtils.showSnackbarkotlin(this@ForgotPasswordActivity, rootForgotLayout, msg_failed_to_send)
                                    }
                                }else{
                                    MyUtils.showSnackbarkotlin(this@ForgotPasswordActivity, rootForgotLayout, msgNoInternet)
                                }
                            }
                        }
                    }else{
                        forgotPasswordButton?.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(rootForgotLayout, true)
                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(this@ForgotPasswordActivity)){
                            MyUtils.showSnackbarkotlin(this@ForgotPasswordActivity, rootForgotLayout, msgSomthingRong)
                        }else{
                            MyUtils.showSnackbarkotlin(this@ForgotPasswordActivity, rootForgotLayout, msgNoInternet)
                        }
                    }
                }
            })
    }

}
