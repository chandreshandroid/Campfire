package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.google.gson.JsonParseException
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OtpResendAndCheckDuplicationModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RequestRegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass.msgNoInternet
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass.msgSomthingRong
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.activity_ragister_profile.*
import kotlinx.android.synthetic.main.activity_ragister_userid.*
import kotlinx.android.synthetic.main.activity_ragister_userid.registerNameInputTypeUserID
import org.json.JSONArray
import org.json.JSONObject

class RegisterUserIdCreateActivity : AppCompatActivity() {
    var msg_mobile_leth = ""


    var sessionManager: SessionManager? = null
    var objRegister: RequestRegisterPojo? = null
    val keyRequestObj = "REQUESTOBJ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this@RegisterUserIdCreateActivity)
        setContentView(R.layout.activity_ragister_userid)
        dynamicLable()

        if (intent.hasExtra(keyRequestObj) && intent.getSerializableExtra(keyRequestObj) != null)
            objRegister = intent.getSerializableExtra(keyRequestObj) as RequestRegisterPojo

        btnNext.setOnClickListener {
            MyUtils.hideKeyboard1(this@RegisterUserIdCreateActivity)
            checkForDuplicationUserMentionId()
        }
    }


    private fun dynamicLable() {
        msg_mobile_leth = resources.getString(R.string.please_enter_user_id)

    }

    override fun onBackPressed() {
        // super.onBackPressed()
        MyUtils.hideKeyboard1(this@RegisterUserIdCreateActivity)
        MyUtils.userRegisterData = RequestRegisterPojo()
        finish()
    }

  private fun checkForDuplicationUserMentionId() {

        if (!btnNext.isStartAnim) btnNext?.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        val lID = sessionManager?.getsetSelectedLanguage()

        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("userMobile", "")
            jsonObject.put("userMentionID", registerNameInputTypeUserID.text.toString())
            jsonObject.put("userEmail", "")
            jsonObject.put("languageID", lID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val resendOTP = ViewModelProviders.of(this@RegisterUserIdCreateActivity)
                .get(OtpResendAndCheckDuplicationModel::class.java)
        resendOTP.apiCall(this@RegisterUserIdCreateActivity, jsonArray.toString(), 1)
                .observe(this@RegisterUserIdCreateActivity,
                        { response ->
                            MyUtils.hideKeyboard1(this@RegisterUserIdCreateActivity)

                            if (!response.isNullOrEmpty()) {

                                if (response[0].status.equals("true", true)) {
                                    objRegister?.userMentionID = registerNameInputTypeUserID.text.toString()
                                    val myIntent = Intent(
                                            this@RegisterUserIdCreateActivity,
                                            RegisterPasswordActivity::class.java
                                    )
                                    myIntent.putExtra(keyRequestObj, objRegister)
                                    startActivity(myIntent)
                                    overridePendingTransition(
                                            R.anim.slide_in_right,
                                            R.anim.slide_out_left
                                    )
                                } else {

                                    if (btnNext.isStartAnim) btnNext?.endAnimation()
                                    //No data and no internet
                                    if (MyUtils.isInternetAvailable(this@RegisterUserIdCreateActivity)) {
                                        if (!response[0].message.isNullOrEmpty()) {
                                            MyUtils.showSnackbarkotlin(
                                                    this@RegisterUserIdCreateActivity,
                                                    rootProfileLayout,
                                                    response[0].message!!
                                            )
                                        } else {
                                            MyUtils.showSnackbarkotlin(
                                                    this@RegisterUserIdCreateActivity,
                                                    rootProfileLayout,
                                                    msgSomthingRong
                                            )
                                        }
                                    } else {
                                        MyUtils.showSnackbarkotlin(
                                                this@RegisterUserIdCreateActivity,
                                                rootProfileLayout,
                                                msgNoInternet
                                        )
                                    }
                                }
                            } else {

                                if (btnNext.isStartAnim) btnNext?.endAnimation()

                                //No internet and somting went rong
                                if (MyUtils.isInternetAvailable(this@RegisterUserIdCreateActivity)) {
                                    MyUtils.showSnackbarkotlin(
                                            this@RegisterUserIdCreateActivity,
                                            rootProfileLayout,
                                            msgSomthingRong
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                            this@RegisterUserIdCreateActivity,
                                            rootProfileLayout,
                                            msgNoInternet
                                    )
                                }
                            }
                        })

    }
}