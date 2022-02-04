package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_register_mobile.*
import kotlinx.android.synthetic.main.header_back_with_text_space.*
import org.json.JSONArray
import org.json.JSONObject
import android.text.Selection
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonParseException
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.application.MyApplication.Companion.mContext
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OtpResendAndCheckDuplicationModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_ragister_profile.*
import kotlinx.android.synthetic.main.activity_register_email.*
import kotlinx.android.synthetic.main.activity_register_email.registerEmailEditEmail
import retrofit2.Response


class RegisterMobileActivity : AppCompatActivity() {

    var titleRegister = ""

    var msg_mobile_leth = ""

    var objRegister: RequestRegisterPojo? = null
    val keyRequestObj = "REQUESTOBJ"
    val keyIsSocial = "SOCIALLOGIN"
    var isSocial = false

    var sessionManager: SessionManager? = null
    var msgFailToRegister = ""
    var msgSomthingRong = ""
    var msgNoInternet = ""
    var msgFailToVerify = ""
    var msgFailToResen = ""
    var msgSuccessRegister = ""
    var msgAlreadyExist = ""
    var rootVerificationOTPLayout: View?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register_mobile)

        dynamicLable()
        sessionManager = SessionManager(this@RegisterMobileActivity)


        if (intent.hasExtra(keyRequestObj) && intent.getSerializableExtra(keyRequestObj) != null)
            objRegister = intent.getSerializableExtra(keyRequestObj) as RequestRegisterPojo

        isSocial = if (intent.hasExtra(keyIsSocial) && intent.getBooleanExtra(
                keyIsSocial,
                false
            ) != null
        ) intent.getBooleanExtra(keyIsSocial, false) else false

        registerMobileEditMobile?.setHint(""+GetDynamicStringDictionaryObjectClass?.Mobile_Number)
        tvMobile_info?.text = ""+GetDynamicStringDictionaryObjectClass.Mobile_Number_Verification_is_required_to_post_News
        registerMobileButtonContinue?.progressText = ""+GetDynamicStringDictionaryObjectClass.next

        tvHeaderText.text = GetDynamicStringDictionaryObjectClass?.registration

        imgCloseIcon.setOnClickListener {
            onBackPressed()
        }

        registerMobileButtonContinue.setOnClickListener {
            MyUtils.hideKeyboard1(this@RegisterMobileActivity)
          rootVerificationOTPLayout = it
            if (validation()) {
                objRegister?.userMobile = registerMobileEditMobile.text.toString()
                Log.w("Logs","OTP : "+ objRegister?.userMobile!!)
                FirebaseInstanceId.getInstance().instanceId
                    .addOnSuccessListener { instanceIdResult ->
                        val instanceId = instanceIdResult.id
                        var newtoken = instanceIdResult.token
                        Log.e("System out", "new token:= " + instanceIdResult.token)
                        if (MyUtils.isInternetAvailable(this@RegisterMobileActivity)) {
                            checkForDuplicationMobile(objRegister?.userMobile!!,newtoken)

                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@RegisterMobileActivity,
                                rootLoginMainLayout!!,
                                "" + msgNoInternet
                            )
                        }
                    }
            }
        }

        if (MyUtils.userRegisterData != null) {
            if (!MyUtils.userRegisterData.userMobile.isNullOrEmpty()) registerMobileEditMobile?.setText(
                "+91 " + MyUtils.userRegisterData.userMobile
            )
            registerMobileEditMobile?.setSelection(registerMobileEditMobile?.text?.length!!)
        }


        registerMobileEditMobile?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val text_value = registerMobileEditMobile.getText().toString()

                if (text_value.equals("+91 ", ignoreCase = true)) {
                    registerMobileEditMobile.setText("")
                } else {
                    if (!text_value.startsWith("+91 ")) {
                        if (text_value.isNotEmpty()) {
                            registerMobileEditMobile.setText("+91 " + s.toString())
                            Selection.setSelection(
                                registerMobileEditMobile.getText(),
                                registerMobileEditMobile?.text?.length!!
                            )
                        }
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
//        super.onBackPressed()

        var userMobile = ""

        if (!registerMobileEditMobile.text.toString()?.isNullOrEmpty() && registerMobileEditMobile.text.toString().contains(
                "+91 "
            )
        ) {
            userMobile = registerMobileEditMobile.text.toString().replace("+91 ", "")
        }

        if (MyUtils.userRegisterData != null) {
            MyUtils.userRegisterData.userMobile = userMobile
        }

        MyUtils.hideKeyboard1(this@RegisterMobileActivity)
        MyUtils.finishActivity(this@RegisterMobileActivity, true)

        //showAlert()
    }

    private fun appendString(s: String) {

        if (s.isNullOrEmpty()) {
            registerMobileEditMobile?.text?.clear()
        } else {
            val s1 = StringBuilder(100)
            s1.append("+91 ")
        }
    }





    private fun showAlert() {
        MyUtils.hideKeyboard1(this@RegisterMobileActivity)
        MyUtils.showMessageOKCancel(
            this@RegisterMobileActivity,
            resources.getString(R.string.msg_alert_data_loss),
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                    finish()
                }
            })
    }

    private fun dynamicLable() {
        titleRegister = resources.getString(R.string.registration)
        msg_mobile_leth = resources.getString(R.string.err_mobile_lenth)
        msgFailToRegister = "Failed to login if you have any query please contact to admin."
        msgSomthingRong = GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong
        msgNoInternet = GetDynamicStringDictionaryObjectClass?.No_Internet_Connection
        msgFailToVerify = "Failed to verify mobile number."
        msgFailToResen = resources.getString(R.string.dealer_verificatrion_fail)
        msgSuccessRegister = "Registration successfully."
        msgAlreadyExist = "Mobile already exists."
    }

    fun validation(): Boolean {
        var checkFlag = true
        if (registerMobileEditMobile.text.toString().isNullOrEmpty() && registerMobileEditMobile.text.toString().length < 14) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(
                this@RegisterMobileActivity,
                registerMobileLayoutMain,
                msg_mobile_leth
            )
        }

        return checkFlag
    }

    private fun storeSessionManager(driverdata: List<RegisterPojo.Data?>) {

        val gson = Gson()
        val json = gson.toJson(driverdata[0]!!)

        sessionManager?.create_login_session(
            json,
            driverdata[0]!!.userEmail!!,
            "",
            true,
            sessionManager?.isEmailLogin()!!,
            driverdata[0]!!.userFirstName!!+" "+driverdata[0]!!.userLastName!!,
            driverdata[0]!!.userProfilePicture!!

            )
    }

    fun sendOtp(mobile: String, token: String) {
        if (!registerMobileButtonContinue?.isStartAnim!!)
            registerMobileButtonContinue.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()


        var lID=sessionManager?.getsetSelectedLanguage()
        try {
            jsonObject.put("userMobile", mobile)
            jsonObject.put("languageID", lID)
            jsonObject.put("userDeviceID", token)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
       var callApi= RestClient.get()!!.otpResendNew(jsonArray.toString())
        Log.w("Logs","OTP : "+ callApi.request())
        callApi.enqueue(object : RestCallback<List<ResendOTPModelItem?>?>(mContext) {
            override fun Success(response: Response<List<ResendOTPModelItem?>?>) {

                if(response.isSuccessful)
                {

                    if (response!=null) {

                        if (registerMobileButtonContinue?.isStartAnim!!) registerMobileButtonContinue.endAnimation()


                        if (response.body() != null && response.body()!!.size > 0) {
//
                            objRegister?.userOTP = response.body()!!.get(0)!!.otp

                            if (!isSocial && !registerMobileEditMobile.text.toString()
                                    .isNullOrEmpty()
                            ) {
                                //If user enter mobile then navigate to otp verification screen
                                val myIntent = Intent(
                                    this@RegisterMobileActivity,
                                    RegisterOtpVerificationActivity::class.java
                                )
                                objRegister?.userOTP = response.body()!!.get(0)!!.otp
                                myIntent.putExtra("userOTP", response.body()!!.get(0)!!.otp)
                                myIntent.putExtra(keyRequestObj, objRegister)
                                myIntent.putExtra(keyIsSocial, isSocial)
                                startActivity(myIntent)
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )
                            } else if (isSocial && !registerMobileEditMobile.text.toString()
                                    .isNullOrEmpty()
                            ) {
                                val myIntent = Intent(
                                    this@RegisterMobileActivity,
                                    RegisterOtpVerificationActivity::class.java
                                )
                                myIntent.putExtra(keyRequestObj, objRegister)
                                myIntent.putExtra(keyIsSocial, isSocial)
                                startActivity(myIntent)
                                finishAffinity()
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )
                            }
                        } else {
                            //No data and no internet
                            MyUtils.showSnackbarkotlin(
                                this@RegisterMobileActivity,
                                registerMobileLayoutMain,
                                msgAlreadyExist
                            )
                        }
                    }} else {
                        if (registerMobileButtonContinue?.isStartAnim!!) registerMobileButtonContinue.endAnimation()

                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(this@RegisterMobileActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@RegisterMobileActivity,
                                registerMobileLayoutMain,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@RegisterMobileActivity,
                                registerMobileLayoutMain,
                                msgNoInternet
                            )
                        }
                    }

            }
            override fun failure() {
                Log.w("SagaSagar","failure")

            }
        })
    }
    fun checkForDuplicationMobile(mobile: String, token: String) {

        if (!registerMobileButtonContinue?.isStartAnim!!)
            registerMobileButtonContinue.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        val lID = sessionManager?.getsetSelectedLanguage()

        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("userMobile", mobile.replace("+91 ", ""))
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

        val resendOTP = ViewModelProviders.of(this@RegisterMobileActivity)
                .get(OtpResendAndCheckDuplicationModel::class.java)
        resendOTP.apiCall(this@RegisterMobileActivity, jsonArray.toString(), 1)
                .observe(this@RegisterMobileActivity,
                        { response ->
                            MyUtils.hideKeyboard1(this@RegisterMobileActivity)

                            if (!response.isNullOrEmpty()) {

                                if (response[0].status.equals("true", true)) {
                                   sendOtp(mobile,token)
                                } else {
                                    if (registerMobileButtonContinue.isStartAnim) registerMobileButtonContinue?.endAnimation()
                                    //No data and no internet
                                    if (MyUtils.isInternetAvailable(this@RegisterMobileActivity)) {
                                        if (!response[0].message.isNullOrEmpty()) {
                                            MyUtils.showSnackbarkotlin(
                                                    this@RegisterMobileActivity,
                                                    registerMobileLayoutMain,
                                                    response[0].message!!
                                            )
                                        } else {
                                            MyUtils.showSnackbarkotlin(
                                                    this@RegisterMobileActivity,
                                                    registerMobileLayoutMain,
                                                    msgAlreadyExist
                                            )
                                        }
                                    } else {
                                        MyUtils.showSnackbarkotlin(
                                                this@RegisterMobileActivity,
                                                registerMobileLayoutMain,
                                                msgNoInternet
                                        )
                                    }
                                }
                            } else {

                                if (registerMobileButtonContinue.isStartAnim) registerMobileButtonContinue?.endAnimation()

                                //No internet and somting went rong
                                if (MyUtils.isInternetAvailable(this@RegisterMobileActivity)) {
                                    MyUtils.showSnackbarkotlin(
                                            this@RegisterMobileActivity,
                                            registerMobileLayoutMain,
                                            msgSomthingRong
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                            this@RegisterMobileActivity,
                                            registerMobileLayoutMain,
                                            msgNoInternet
                                    )
                                }
                            }
                        })

    }
}
