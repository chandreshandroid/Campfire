package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OnBoardingModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OtpResendAndCheckDuplicationModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.gson.JsonParseException
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.activity_otp_verification.*
import org.json.JSONArray
import org.json.JSONObject

class OtpVerificationActivity : AppCompatActivity(), View.OnKeyListener  {


    var from: String? = null
    var phoneNumber: String? = null

    var OTP_verification_validation:String=""
    var headertitle = ""
    var msgFailToRegister = ""
    var msgSomthingRong = ""
    var msgNoInternet = ""
    var msgFailToVerify = ""
    var msgFailToResen = ""
    var msgSuccessRegister = ""

    var keyUserId = "keyUserId"
    var keyUserMobile = "keyUserMobile"
    var keyUserMobileCountryCode = "keyUserMobileCountryCode"
    var keyUserEmail = "keyUserEmail"
    var keyUserMobileRes = "keyUserMobileRes"

    var userId = ""
    var userMobile = ""
    var userMobile1 = ""
    var userEmail = ""
    var userCountryCode = ""
    var isFinishActivityAfterPostSucess = false
    var sessionManager: SessionManager?=null
    private val postSuccessBroadCast = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("Successfully")) {
                isFinishActivityAfterPostSucess = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)
        sessionManager=SessionManager(this@OtpVerificationActivity)
        dynamicLable()

        tvVerificationTitledescription?.text = GetDynamicStringDictionaryObjectClass?.Please_enter_the_4_digit_OTP_received_on_your_phone_number
        btnProceed?.progressText = GetDynamicStringDictionaryObjectClass?.Proceed_
        tvOtpresend?.text= GetDynamicStringDictionaryObjectClass?.Resend

        tvHeaderText.text = GetDynamicStringDictionaryObjectClass?.OTP_Verification

        LocalBroadcastManager.getInstance(this@OtpVerificationActivity)
            .registerReceiver(postSuccessBroadCast, IntentFilter("PostSuccess"))
        try {
            if (intent != null) {
                userId = intent.getStringExtra(keyUserId)!!
                userMobile = intent.getStringExtra(keyUserMobile)!!
                userEmail = intent.getStringExtra(keyUserEmail)!!
                userMobile1 = intent.getStringExtra(keyUserMobileRes)!!
            }
        } catch (e: java.lang.Exception) {

        }

        imgCloseIcon.setOnClickListener {
            onBackPressed()
        }

        edittext_pin_1.setOnKeyListener(this)
        edittext_pin_2.setOnKeyListener(this)
        edittext_pin_3.setOnKeyListener(this)
        edittext_pin_4.setOnKeyListener(this)
        edittext_pin_1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edittext_pin_1.text.toString().length == 1)
                //size as per your requirement
                {
                    edittext_pin_2.requestFocus()
                }
            }

        })
        edittext_pin_2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edittext_pin_2.text.toString().length == 1)
                //size as per your requirement
                {
                    edittext_pin_3.requestFocus()
                } else if (edittext_pin_2.text.toString().isEmpty()) {
                    edittext_pin_1.requestFocus()
                }
            }

        })
        edittext_pin_3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edittext_pin_3.text.toString().length == 1)
                //size as per your requirement
                {
                    edittext_pin_4.requestFocus()
                } else if (edittext_pin_3.text.toString().isEmpty()) {
                    edittext_pin_2.requestFocus()
                }
            }

        })
        edittext_pin_4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (edittext_pin_4.text.toString().length == 1)
                //size as per your requirement
                {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(
                        edittext_pin_4.windowToken,
                        InputMethodManager.RESULT_UNCHANGED_SHOWN
                    )
                } else if (edittext_pin_4.text.toString().isEmpty()) {
                    edittext_pin_3.requestFocus()
                }
            }

        })

        edittext_pin_4.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                    MyUtils.hideKeyboard1(this@OtpVerificationActivity)


                    if (TextUtils.isEmpty(edittext_pin_1.text.toString()) && TextUtils.isEmpty(edittext_pin_2.text.toString())
                        && TextUtils.isEmpty(edittext_pin_3.text.toString()) && TextUtils.isEmpty(edittext_pin_4.text.toString())
                    ) {
                        MyUtils.showSnackbar(this@OtpVerificationActivity,OTP_verification_validation,ll_mainOtp)

                    } else if (TextUtils.isEmpty(edittext_pin_1.text.toString())) {
                        MyUtils.showSnackbar(this@OtpVerificationActivity,OTP_verification_validation,ll_mainOtp)

                    } else if (TextUtils.isEmpty(edittext_pin_2.text.toString())) {
                        MyUtils.showSnackbar(this@OtpVerificationActivity,OTP_verification_validation,ll_mainOtp)


                    } else if (TextUtils.isEmpty(edittext_pin_3.text.toString())) {
                        MyUtils.showSnackbar(this@OtpVerificationActivity,OTP_verification_validation,ll_mainOtp)

                    } else if (TextUtils.isEmpty(edittext_pin_4.text.toString())) {
                        MyUtils.showSnackbar(this@OtpVerificationActivity,OTP_verification_validation,ll_mainOtp)

                    } else {
                        val OTPCOde = (edittext_pin_1.text.toString() + edittext_pin_2.text.toString()
                                + edittext_pin_3.text.toString() + edittext_pin_4.text.toString())

                        if (MyUtils.isInternetAvailable(this@OtpVerificationActivity)) {
                                //MyUtils.startActivity(this@OtpVerificationActivity,ResetPasswordActivity::class.java,true)
                               // getVerified(OTPCOde.trim({ it <= ' ' }))

                                FirebaseInstanceId.getInstance().instanceId
                                    .addOnSuccessListener(object :
                                        OnSuccessListener<InstanceIdResult> {
                                        override fun onSuccess(instanceIdResult: InstanceIdResult) {
                                            val instanceId = instanceIdResult.id
                                            var newtoken = instanceIdResult.token
                                            Log.e("System out", "new token:= " + instanceIdResult.token)

                                            verifyOTP(newtoken,OTPCOde)
                                        }
                                    })


                        } else {
                         MyUtils.showSnackbarkotlin(this@OtpVerificationActivity,ll_mainOtp,msgNoInternet)
                        }

                    }
                    return true

                }
                return false
            }

        })

        btnProceed.setOnClickListener {
            MyUtils.hideKeyboard1(this@OtpVerificationActivity)

            if (TextUtils.isEmpty(edittext_pin_1.text.toString()) && TextUtils.isEmpty(edittext_pin_2.text.toString())
                && TextUtils.isEmpty(edittext_pin_3.text.toString()) && TextUtils.isEmpty(edittext_pin_4.text.toString())
            ) {
                MyUtils.showSnackbarkotlin(this@OtpVerificationActivity,ll_mainOtp,OTP_verification_validation)
            } else if (TextUtils.isEmpty(edittext_pin_1.text.toString())) {
                MyUtils.showSnackbarkotlin(this@OtpVerificationActivity,ll_mainOtp,OTP_verification_validation)
            } else if (TextUtils.isEmpty(edittext_pin_2.text.toString())) {
                MyUtils.showSnackbarkotlin(this@OtpVerificationActivity,ll_mainOtp,OTP_verification_validation)


            } else if (TextUtils.isEmpty(edittext_pin_3.text.toString())) {
                MyUtils.showSnackbarkotlin(this@OtpVerificationActivity,ll_mainOtp,OTP_verification_validation)
            } else if (TextUtils.isEmpty(edittext_pin_4.text.toString())) {
                MyUtils.showSnackbarkotlin(this@OtpVerificationActivity,ll_mainOtp,OTP_verification_validation)
            } else {
                val OTPCOde = (edittext_pin_1.text.toString() + edittext_pin_2.text.toString()
                        + edittext_pin_3.text.toString() + edittext_pin_4.text.toString())

                if (MyUtils.isInternetAvailable(this@OtpVerificationActivity)) {
                    //MyUtils.startActivity(this@OtpVerificationActivity,ResetPasswordActivity::class.java,true)
                    // getVerified(OTPCOde.trim({ it <= ' ' }))

                                FirebaseInstanceId.getInstance().instanceId
                                    .addOnSuccessListener(object : OnSuccessListener<InstanceIdResult> {
                                        override fun onSuccess(instanceIdResult: InstanceIdResult) {
                                            val instanceId = instanceIdResult.id
                                            var newtoken = instanceIdResult.token
                                            Log.e("System out", "new token:= " + instanceIdResult.token)

                    verifyOTP(newtoken,OTPCOde)
                                        }
                                    })

                } else {
                    MyUtils.showSnackbarkotlin(this@OtpVerificationActivity,ll_mainOtp,msgNoInternet)
                }

            }


        }

        tvOtpresend?.setOnClickListener {

            MyUtils.hideKeyboard1(this@OtpVerificationActivity)
            edittext_pin_1.setText("")
            edittext_pin_2.setText("")
            edittext_pin_3.setText("")
            edittext_pin_4.setText("")

            if (MyUtils.isInternetAvailable(this@OtpVerificationActivity)) {
                FirebaseInstanceId.getInstance().instanceId
                                    .addOnSuccessListener(object : OnSuccessListener<InstanceIdResult> {
                                        override fun onSuccess(instanceIdResult: InstanceIdResult) {
                                            val instanceId = instanceIdResult.id
                                            var newtoken = instanceIdResult.token
                                            Log.e("System out", "new token:= " + instanceIdResult.token)

                resendOTP(newtoken)
                                        }
                                    })
            } else {
                MyUtils.showSnackbarkotlin(this@OtpVerificationActivity,ll_mainOtp,msgNoInternet)
            }

        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            val id = v?.id
            when (id) {
                R.id.edittext_pin_4 -> if (keyCode == KeyEvent.KEYCODE_DEL) {

                    edittext_pin_3.requestFocus()
                    edittext_pin_4.setText("")

                    return true
                }

                R.id.edittext_pin_3 -> if (keyCode == KeyEvent.KEYCODE_DEL) {
                    //
                    edittext_pin_2.requestFocus()
                    edittext_pin_3.setText("")

                    return true
                }

                R.id.edittext_pin_2 -> if (keyCode == KeyEvent.KEYCODE_DEL) {
                    //
                    edittext_pin_1.requestFocus()
                    edittext_pin_2.setText("")

                    return true
                }

                R.id.edittext_pin_1 -> if (keyCode == KeyEvent.KEYCODE_DEL) {

                    edittext_pin_1.requestFocus()
                    edittext_pin_1.setText("")

                    return true
                }


                else -> return false
            }
        }

        return false
    }

    fun showSnackbar(message: String) {

        Snackbar.make(ll_mainOtp, message, Snackbar.LENGTH_LONG).show()

    }

    override fun onBackPressed() {
        MyUtils.hideKeyboard1(this@OtpVerificationActivity)
        MyUtils.showMessageYesNo(this@OtpVerificationActivity, "Are you sure you want to exit?", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                finish()
            }
        })
    }

    private fun dynamicLable() {
        headertitle = resources.getString(R.string.OTP_verification)
        OTP_verification_validation = resources.getString(R.string.OTP_verification_validation)

        msgFailToRegister = "Failed to login if you have any query please contact to admin."
        msgSomthingRong =  GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong
        msgNoInternet = GetDynamicStringDictionaryObjectClass?.No_Internet_Connection
        msgFailToVerify = "Failed to verify mobile number."
        msgFailToResen = resources.getString(R.string.dealer_verificatrion_fail)

    }

    private fun verifyOTP(newtoken: String, otp: String) {
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        btnProceed?.startAnimation()
        MyUtils.setViewAndChildrenEnabled(ll_mainOtp, false)

        /* [{
             "loginuserID": "107",
             "userOTP": "1234",
             "languageID": "1",
             "userDeviceID": "token",
             "apiType": "Android",
             "apiVersion": "1.0"
         }]*/

        try {
            jsonObject.put("loginuserID", userId)
            jsonObject.put("userOTP", otp)
            jsonObject.put("languageID",sessionManager?.getsetSelectedLanguage())
            jsonObject.put("userDeviceID", newtoken)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val verifyOTP = ViewModelProviders.of(this@OtpVerificationActivity)
            .get(OnBoardingModel::class.java)

        verifyOTP.apiCall(this@OtpVerificationActivity, jsonArray.toString(), 1)
            .observe(this@OtpVerificationActivity, object : Observer<List<RegisterPojo>?> {
                override fun onChanged(response: List<RegisterPojo>?) {

                    if (!response.isNullOrEmpty()) {

                        if (response[0].status.equals("true", true)) {
                            btnProceed?.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(ll_mainOtp, true)
                            MyUtils.hideKeyboard1(this@OtpVerificationActivity)

                            if (!response[0].data.isNullOrEmpty()) {
                                if (response[0].data!!.size > 0) {
                                    val sendUserId = response[0].data!![0]?.userID
                                    Handler().postDelayed({
                                        val myIntent = Intent(
                                            this@OtpVerificationActivity,
                                            ResetPasswordActivity::class.java
                                        )
                                        myIntent.putExtra(keyUserId, sendUserId)
                                        startActivity(myIntent)
                                    }, 500)
                                }
                            } else {
                            }
                        } else {
                            btnProceed?.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(ll_mainOtp, true)
                            //No data and no internet
                            edittext_pin_1.setText("")
                            edittext_pin_2.setText("")
                            edittext_pin_3.setText("")
                            edittext_pin_4.setText("")
                            edittext_pin_1.requestFocus()

                            if (MyUtils.isInternetAvailable(this@OtpVerificationActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@OtpVerificationActivity,
                                    ll_mainOtp,
                                    response[0].message!!
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@OtpVerificationActivity,
                                    ll_mainOtp,
                                    msgNoInternet
                                )
                            }
                        }

                    } else {
                        btnProceed?.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(ll_mainOtp, true)
                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(this@OtpVerificationActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@OtpVerificationActivity,
                                ll_mainOtp,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@OtpVerificationActivity,
                                ll_mainOtp,
                                msgNoInternet
                            )
                        }
                    }
                }
            })
    }

    private fun resendOTP(token: String) {

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()


/*
* [{
	"loginuserID": "107",
	"userMobile": "9824488033",
	"languageID": "1",
	"userDeviceID": "token",
	"apiType": "Android",
	"apiVersion": "1.0"
}]
* */

        try {
            jsonObject.put("loginuserID", userId)
            jsonObject.put("userMobile",userMobile1)
           // jsonObject.put("languageID", PrefDb(this@OtpVerificationActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()))
            jsonObject.put("languageID",sessionManager?.getsetSelectedLanguage())
            jsonObject.put("userDeviceID", token)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val resendOTP = ViewModelProviders.of(this@OtpVerificationActivity)
            .get(OtpResendAndCheckDuplicationModel::class.java)
        resendOTP.apiCall(this@OtpVerificationActivity, jsonArray.toString(), 0)
            .observe(this@OtpVerificationActivity,
                object : Observer<List<CommonPojo?>?> {
                    override fun onChanged(response: List<CommonPojo?>?) {
                        if (!response.isNullOrEmpty()) {
                            if (response[0]?.status.equals("true", true)) {
                                MyUtils.hideKeyboard1(this@OtpVerificationActivity)
                                edittext_pin_1.setText("")
                                edittext_pin_2.setText("")
                                edittext_pin_3.setText("")
                                edittext_pin_4.setText("")
                                edittext_pin_1.requestFocus()
                                MyUtils.showSnackbarkotlin(
                                    this@OtpVerificationActivity,
                                    ll_mainOtp,
                                    response[0]?.message!!
                                )
                            } else {
                                //No data and no internet
                                edittext_pin_1.setText("")
                                edittext_pin_2.setText("")
                                edittext_pin_3.setText("")
                                edittext_pin_4.setText("")
                                edittext_pin_1.requestFocus()
                                if (MyUtils.isInternetAvailable(this@OtpVerificationActivity)) {
                                    if (!response[0]?.message.isNullOrEmpty()) {
                                        MyUtils.showSnackbarkotlin(
                                            this@OtpVerificationActivity,
                                            ll_mainOtp,
                                            response[0]?.message!!
                                        )
                                    } else {
                                        MyUtils.showSnackbarkotlin(
                                            this@OtpVerificationActivity,
                                            ll_mainOtp,
                                            msgFailToResen
                                        )
                                    }
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        this@OtpVerificationActivity,
                                        ll_mainOtp,
                                        msgNoInternet
                                    )
                                }
                            }
                        } else {
                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@OtpVerificationActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@OtpVerificationActivity,
                                    ll_mainOtp,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@OtpVerificationActivity,
                                    ll_mainOtp,
                                    msgNoInternet
                                )
                            }
                        }
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@OtpVerificationActivity).unregisterReceiver(postSuccessBroadCast)
    }

    override fun onResume() {
        super.onResume()
        if (isFinishActivityAfterPostSucess) finish()
    }
}
