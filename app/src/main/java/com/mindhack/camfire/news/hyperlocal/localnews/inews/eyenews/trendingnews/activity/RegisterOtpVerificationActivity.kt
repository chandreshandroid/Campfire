package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OnBoardingModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OtpResendAndCheckDuplicationModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RequestRegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonParseException
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.activity_otp_verification.*
import org.json.JSONArray
import org.json.JSONObject

class RegisterOtpVerificationActivity : AppCompatActivity(), View.OnKeyListener {


    var from: String? = null
    var from1: String? = null
    var phoneNumber: String? = null

    var OTP_verification_validation: String = ""
    var headertitle = ""
    val keyIsSocial = "SOCIALLOGIN"
    var isSocial = false


    var objRegister: RequestRegisterPojo? = null
    val keyRequestObj = "REQUESTOBJ"

    var sessionManager: SessionManager? = null
    var msgFailToRegister = ""
    var msgSomthingRong = ""
    var msgNoInternet = ""
    var msgFailToVerify = ""
    var msgFailToResen = ""
    var msgSuccessRegister = ""
    var userData: RegisterPojo.Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_registration_otp_verification)
        dynamicLable()
//        tvHeaderText.text = headertitle

        tvVerificationTitledescription?.text =
            GetDynamicStringDictionaryObjectClass?.Please_enter_the_4_digit_OTP_received_on_your_phone_number
        btnProceed?.progressText = GetDynamicStringDictionaryObjectClass?.Proceed_
        tvOtpresend?.text = GetDynamicStringDictionaryObjectClass?.Resend
        tvHeaderText.text = GetDynamicStringDictionaryObjectClass?.OTP_Verification

        sessionManager = SessionManager(this@RegisterOtpVerificationActivity)
        imgCloseIcon.setOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(keyRequestObj) && intent.getSerializableExtra(keyRequestObj) != null)
            objRegister = intent.getSerializableExtra(keyRequestObj) as RequestRegisterPojo


        from1 = if ((intent.getStringExtra("from")) != null) intent.getStringExtra("from") else ""


        isSocial = if (intent.hasExtra(keyIsSocial) && intent.getBooleanExtra(
                keyIsSocial,
                false
            ) != null
        ) intent.getBooleanExtra(keyIsSocial, false) else false


        if (from1.equals("beareporter", true)) {
            userData =
                if ((intent.getSerializableExtra("data")) != null) intent.getSerializableExtra("data") as RegisterPojo.Data else null
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
                    MyUtils.hideKeyboard1(this@RegisterOtpVerificationActivity)


                    if (TextUtils.isEmpty(edittext_pin_1.text.toString()) && TextUtils.isEmpty(
                            edittext_pin_2.text.toString()
                        )
                        && TextUtils.isEmpty(edittext_pin_3.text.toString()) && TextUtils.isEmpty(
                            edittext_pin_4.text.toString()
                        )
                    ) {
                        MyUtils.showSnackbar(
                            this@RegisterOtpVerificationActivity,
                            OTP_verification_validation,
                            ll_mainOtp
                        )

                    } else if (TextUtils.isEmpty(edittext_pin_1.text.toString())) {
                        MyUtils.showSnackbar(
                            this@RegisterOtpVerificationActivity,
                            OTP_verification_validation,
                            ll_mainOtp
                        )

                    } else if (TextUtils.isEmpty(edittext_pin_2.text.toString())) {
                        MyUtils.showSnackbar(
                            this@RegisterOtpVerificationActivity,
                            OTP_verification_validation,
                            ll_mainOtp
                        )


                    } else if (TextUtils.isEmpty(edittext_pin_3.text.toString())) {
                        MyUtils.showSnackbar(
                            this@RegisterOtpVerificationActivity,
                            OTP_verification_validation,
                            ll_mainOtp
                        )

                    } else if (TextUtils.isEmpty(edittext_pin_4.text.toString())) {
                        MyUtils.showSnackbar(
                            this@RegisterOtpVerificationActivity,
                            OTP_verification_validation,
                            ll_mainOtp
                        )

                    } else {
                        val OTPCOde = (edittext_pin_1.text.toString() + edittext_pin_2.text.toString()
                                    + edittext_pin_3.text.toString() + edittext_pin_4.text.toString())
                        if (MyUtils.isInternetAvailable(this@RegisterOtpVerificationActivity)) {
                            if (objRegister?.userOTP.equals(OTPCOde)) {
                                val myIntent = Intent(
                                    this@RegisterOtpVerificationActivity,
                                    RegisterPasswordActivity::class.java
                                )
                                myIntent.putExtra("from", "MyProfileEdit")

                                myIntent.putExtra(keyRequestObj, objRegister)
                                startActivity(myIntent)
                                finishAffinity()
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )
                            }
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@RegisterOtpVerificationActivity,
                                ll_mainOtp!!,
                                resources.getString(R.string.OTP_verification_valid_validation)
                            )
                        }
                    }
                    return true

                }
                return false
            }

        })

        btnProceed.setOnClickListener {
            MyUtils.hideKeyboard1(this@RegisterOtpVerificationActivity)
//
//running

            if (TextUtils.isEmpty(edittext_pin_1.text.toString()) && TextUtils.isEmpty(
                    edittext_pin_2.text.toString()
                )
                && TextUtils.isEmpty(edittext_pin_3.text.toString()) && TextUtils.isEmpty(
                    edittext_pin_4.text.toString()
                )
            ) {
                MyUtils.showSnackbar(
                    this@RegisterOtpVerificationActivity,
                    OTP_verification_validation,
                    ll_mainOtp
                )
            } else if (TextUtils.isEmpty(edittext_pin_1.text.toString())) {
                MyUtils.showSnackbar(
                    this@RegisterOtpVerificationActivity,
                    OTP_verification_validation,
                    ll_mainOtp
                )
            } else if (TextUtils.isEmpty(edittext_pin_2.text.toString())) {
                MyUtils.showSnackbar(
                    this@RegisterOtpVerificationActivity,
                    OTP_verification_validation,
                    ll_mainOtp
                )


            } else if (TextUtils.isEmpty(edittext_pin_3.text.toString())) {
                MyUtils.showSnackbar(
                    this@RegisterOtpVerificationActivity,
                    OTP_verification_validation,
                    ll_mainOtp
                )
            } else if (TextUtils.isEmpty(edittext_pin_4.text.toString())) {
                MyUtils.showSnackbar(
                    this@RegisterOtpVerificationActivity,
                    OTP_verification_validation,
                    ll_mainOtp
                )
            } else {
                val OTPCOde = (edittext_pin_1.text.toString() + edittext_pin_2.text.toString()
                        + edittext_pin_3.text.toString() + edittext_pin_4.text.toString())
//                        && userData?.userOTP.equals(OTPCOde)
                if (MyUtils.isInternetAvailable(this@RegisterOtpVerificationActivity)) {
                    if (objRegister?.userOTP.equals(OTPCOde)) {
                        val myIntent = Intent(
                            this@RegisterOtpVerificationActivity,
                            RegisterUserIdCreateActivity::class.java
                        )
                        myIntent.putExtra(keyRequestObj, objRegister)
                        myIntent.putExtra(keyIsSocial, isSocial)
                        startActivity(myIntent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    } else {
                        MyUtils.showSnackbarkotlin(
                            this@RegisterOtpVerificationActivity,
                            ll_mainOtp!!,
                            resources.getString(R.string.OTP_verification_valid_validation)
                        )

                    }
//                    FirebaseInstanceId.getInstance().instanceId
//                        .addOnCompleteListener(OnCompleteListener { task ->
//                            if (!task.isSuccessful) {
//                                return@OnCompleteListener
//                            }
//
//                            val newtoken = task.result?.token
////                                    if (MyUtils.isInternetAvailable(this@VerificationOTPActivity)){
//                            verifyOTP(newtoken!!, OTPCOde)
////                                    }else{
////                                        MyUtils.showSnackbarkotlin(this@VerificationOTPActivity, rootVerificationOTPLayout!!, resources.getString(R.string.error_common_network))
////                                    }
//                        })

                } else {
                    showSnackbar(
                        resources.getString(R.string.error_common_netdon_t_have_and_accountwork)

                    )
                }

            }
        }

        tvOtpresend.setOnClickListener {
            if (MyUtils.isInternetAvailable(this@RegisterOtpVerificationActivity)) {
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
//                val newtoken = "abcg3343nsdjhskd2jl"
                        val newtoken = task.result?.token
//                                    if (MyUtils.isInternetAvailable(this@VerificationOTPActivity)){
                        resendOTP(newtoken!!)
//                                    }else{
//                                        MyUtils.showSnackbarkotlin(this@VerificationOTPActivity, rootVerificationOTPLayout!!, resources.getString(R.string.error_common_network))
//                                    }
                    })

            } else {
                showSnackbar(
                    resources.getString(R.string.error_common_netdon_t_have_and_accountwork)
                )
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
        MyUtils.hideKeyboard1(this@RegisterOtpVerificationActivity)

        MyUtils.showMessageOKCancel(this@RegisterOtpVerificationActivity,
            "Are you sure want to exit ?",
            "Verification Code",
            DialogInterface.OnClickListener { dialogInterface, i ->
                MyUtils.finishActivity(
                    this@RegisterOtpVerificationActivity,
                    true
                )
            })
    }

    private fun dynamicLable() {
        headertitle = resources.getString(R.string.OTP_verification)
        OTP_verification_validation = resources.getString(R.string.OTP_verification_validation)
        msgFailToRegister = "Failed to login if you have any query please contact to admin."
        msgSomthingRong = GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong
        msgNoInternet = GetDynamicStringDictionaryObjectClass?.No_Internet_Connection
        msgFailToVerify = "Failed to verify mobile number."
        msgFailToResen = resources.getString(R.string.dealer_verificatrion_fail)
        msgSuccessRegister = "Registration successfully."
    }

    private fun verifyOTP(newtoken: String, otp: String) {

        if (!btnProceed.isStartAnim) btnProceed.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        var lID = sessionManager?.getsetSelectedLanguage()!!


        //val lID = if (PrefDb(this@RegisterOtpVerificationActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb( this@RegisterOtpVerificationActivity ).getString(MyUtils.SharedPreferencesenum.languageId.toString()) else "1"

        try {

            if (from1.equals("beareporter", true)) {
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put("userOTP", otp)
                jsonObject.put("languageID", lID)
                jsonObject.put("userDeviceID", newtoken)
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)
            } else {
                jsonObject.put("loginuserID", objRegister?.userId)
                jsonObject.put("userOTP", otp)
                jsonObject.put("languageID", lID)
                jsonObject.put("userDeviceID", newtoken)
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)

            }
            jsonArray.put(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val verifyOTP = ViewModelProviders.of(this@RegisterOtpVerificationActivity)
            .get(OnBoardingModel::class.java)

        verifyOTP.apiCall(this@RegisterOtpVerificationActivity, jsonArray.toString(), 1)
            .observe(this@RegisterOtpVerificationActivity, object : Observer<List<RegisterPojo>?> {
                override fun onChanged(response: List<RegisterPojo>?) {

                    if (!response.isNullOrEmpty()) {
                        if (btnProceed.isStartAnim) btnProceed.endAnimation()

                        if (response[0].status.equals("true", true)) {

                            MyUtils.hideKeyboard1(this@RegisterOtpVerificationActivity)

                            if (!response[0].data.isNullOrEmpty()) {
                                if (response[0].data!!.size > 0) {
                                    sessionManager?.clear_login_session()
                                    storeSessionManager(response[0]?.data!!)

                                    Handler().postDelayed({
                                        //
                                        if (from1.equals("password")) {
//                                            val myIntent = Intent(
//                                                this@RegisterOtpVerificationActivity,
//                                                BeaReporterActivity::class.java
//                                            )
//
                                            val myIntent = Intent(
                                                this@RegisterOtpVerificationActivity,
                                                RegisterPasswordActivity::class.java
                                            )
                                            myIntent.putExtra("from", "MyProfileEdit")

                                            myIntent.putExtra(keyRequestObj, objRegister)
                                            startActivity(myIntent)
                                            finishAffinity()
                                            overridePendingTransition(
                                                R.anim.slide_in_right,
                                                R.anim.slide_out_left
                                            )
                                        } else {
                                            val myIntent = Intent(
                                                this@RegisterOtpVerificationActivity,
                                                MainActivity::class.java
                                            )
                                            myIntent.putExtra("from", "MyProfileEdit")

                                            myIntent.putExtra(keyRequestObj, objRegister)
                                            startActivity(myIntent)
                                            finishAffinity()
                                            overridePendingTransition(
                                                R.anim.slide_in_right,
                                                R.anim.slide_out_left
                                            )
                                        }


//                                        }
                                    }, 500)
                                }
                            } else {
                            }
                        } else {
                            //No data and no internet
                            edittext_pin_1.setText("")
                            edittext_pin_2.setText("")
                            edittext_pin_3.setText("")
                            edittext_pin_4.setText("")
                            edittext_pin_1.requestFocus()
                            if (MyUtils.isInternetAvailable(this@RegisterOtpVerificationActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterOtpVerificationActivity,
                                    ll_mainOtp,
                                    response[0].message!!
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterOtpVerificationActivity,
                                    ll_mainOtp,
                                    msgNoInternet
                                )
                            }
                        }

                    } else {
                        if (btnProceed.isStartAnim) btnProceed.endAnimation()
                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(this@RegisterOtpVerificationActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@RegisterOtpVerificationActivity,
                                ll_mainOtp,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@RegisterOtpVerificationActivity,
                                ll_mainOtp,
                                msgNoInternet
                            )
                        }
                    }
                }
            })
    }

    private fun userRegister(newtoken: String) {

        if (objRegister != null) {

            objRegister?.apiType = RestClient.apiType
            objRegister?.apiVersion = RestClient.apiVersion
            /* objRegister?.languageID =
                 if (PrefDb(this@RegisterOtpVerificationActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb(
                     this@RegisterOtpVerificationActivity
                 ).getString(MyUtils.SharedPreferencesenum.languageId.toString()) else "1"*/
            objRegister?.languageID = sessionManager?.getsetSelectedLanguage()
            objRegister?.userDeviceType = RestClient.apiType
            objRegister?.userProfilePicture = ""
            objRegister?.userDeviceID = newtoken


        }
        val gson = Gson()
        val json = gson.toJson(objRegister)
        val jsonArray = JSONArray()
        try {
            val jsonObject = JSONObject(json)
            jsonArray.put(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val verifyOTP = ViewModelProviders.of(this@RegisterOtpVerificationActivity)
            .get(OnBoardingModel::class.java)

        verifyOTP.apiCall(this@RegisterOtpVerificationActivity, jsonArray.toString(), 0)
            .observe(this@RegisterOtpVerificationActivity, object : Observer<List<RegisterPojo>?> {
                override fun onChanged(response: List<RegisterPojo>?) {

                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(this@RegisterOtpVerificationActivity)

                            if (!response[0].data.isNullOrEmpty()) {
                                sessionManager?.clear_login_session()
                                storeSessionManager(response[0]?.data!!)

                                Handler().postDelayed({
                                    val myIntent = Intent(
                                        this@RegisterOtpVerificationActivity,
                                        BeaReporterActivity::class.java
                                    )
                                    startActivity(myIntent)
                                    finishAffinity()
                                    overridePendingTransition(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left
                                    )
                                }, 500)

                            } else {
                                MyUtils.showMessageOK(
                                    this@RegisterOtpVerificationActivity,
                                    msgFailToRegister,
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            dialog?.dismiss()
                                            finishAffinity()
                                        }
                                    })
                            }
                        } else {
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(this@RegisterOtpVerificationActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterOtpVerificationActivity,
                                    ll_mainOtp,
                                    response[0].message!!
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterOtpVerificationActivity,
                                    ll_mainOtp,
                                    msgNoInternet
                                )
                            }
                        }

                    } else {
                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(this@RegisterOtpVerificationActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@RegisterOtpVerificationActivity,
                                ll_mainOtp,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@RegisterOtpVerificationActivity,
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

        /*val lID =
            if (PrefDb(this@RegisterOtpVerificationActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb(
                this@RegisterOtpVerificationActivity
            ).getString(MyUtils.SharedPreferencesenum.languageId.toString()) else "1"*/
        var lID = sessionManager?.getsetSelectedLanguage()

        try {

            if (from1.equals("beareporter", true)) {
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put(
                    "userMobile", userData?.userMobile
                )
                jsonObject.put("languageID", lID)
                jsonObject.put("userDeviceID", token)
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)
            } else {
                jsonObject.put("loginuserID", objRegister?.userId)
                jsonObject.put(
                    "userMobile",
                    if (objRegister != null && !objRegister?.userMobile.isNullOrEmpty()) objRegister?.userMobile else ""
                )
                jsonObject.put("languageID", lID)
                jsonObject.put("userDeviceID", token)
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)
            }



            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val resendOTP = ViewModelProviders.of(this@RegisterOtpVerificationActivity)
            .get(OtpResendAndCheckDuplicationModel::class.java)
        resendOTP.apiCall(this@RegisterOtpVerificationActivity, jsonArray.toString(), 0)
            .observe(this@RegisterOtpVerificationActivity,
                object : Observer<List<CommonPojo?>?> {
                    override fun onChanged(response: List<CommonPojo?>?) {
                        if (!response.isNullOrEmpty()) {
                            if (response[0]?.status.equals("true", true)) {
                                MyUtils.hideKeyboard1(this@RegisterOtpVerificationActivity)
                                edittext_pin_1.setText("")
                                edittext_pin_2.setText("")
                                edittext_pin_3.setText("")
                                edittext_pin_4.setText("")
                                edittext_pin_1.requestFocus()
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterOtpVerificationActivity,
                                    ll_mainOtp,
                                    response[0]?.message!!
                                )
                            } else {
                                //No data and no internet
                                if (MyUtils.isInternetAvailable(this@RegisterOtpVerificationActivity)) {
                                    if (!response[0]?.message.isNullOrEmpty()) {
                                        MyUtils.showSnackbarkotlin(
                                            this@RegisterOtpVerificationActivity,
                                            ll_mainOtp,
                                            response[0]?.message!!
                                        )
                                    } else {
                                        MyUtils.showSnackbarkotlin(
                                            this@RegisterOtpVerificationActivity,
                                            ll_mainOtp,
                                            msgFailToResen
                                        )
                                    }
                                } else {
                                    edittext_pin_1.setText("")
                                    edittext_pin_2.setText("")
                                    edittext_pin_3.setText("")
                                    edittext_pin_4.setText("")
                                    edittext_pin_1.requestFocus()
                                    MyUtils.showSnackbarkotlin(
                                        this@RegisterOtpVerificationActivity,
                                        ll_mainOtp,
                                        msgNoInternet
                                    )
                                }
                            }
                        } else {
                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@RegisterOtpVerificationActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterOtpVerificationActivity,
                                    ll_mainOtp,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterOtpVerificationActivity,
                                    ll_mainOtp,
                                    msgNoInternet
                                )
                            }
                        }
                    }
                })
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
}