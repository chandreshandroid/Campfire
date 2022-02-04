package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OnBoardingModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RequestRegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.activity_ragister_profile.*
import kotlinx.android.synthetic.main.activity_register_password.*
import kotlinx.android.synthetic.main.activity_register_password.registerPasswordButtonContinue
import kotlinx.android.synthetic.main.header_back_with_text.*
import org.json.JSONArray
import org.json.JSONObject

class RegisterPasswordActivity : AppCompatActivity() {

    var titleRegister = ""

    var msg_no_password = ""
    var msg_valid_password = ""
    var msg_password_leth_8 = ""
    var isSocial = false


    var objRegister: RequestRegisterPojo? = null
    val keyRequestObj = "REQUESTOBJ"
    val keyIsSocial = "SOCIALLOGIN"

    var sessionManager: SessionManager? = null
    var msgFailToRegister = ""
    var msgSomthingRong = ""
    var msgNoInternet = ""
    var msgFailToVerify = ""
    var msgFailToResen = ""
    var msgSuccessRegister = ""
    var msg_accept_terms = ""
    var title_bySigninup = ""
    var title_terms = ""
    var title_privacyPolicy = ""
    var title_andConact = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_register_password)

        dynamicLable()


        sessionManager = SessionManager(this@RegisterPasswordActivity)

//        intent.hasExtra(keyRequestObj) &&
        if (intent.getSerializableExtra(keyRequestObj) != null)
            objRegister = intent.getSerializableExtra(keyRequestObj) as RequestRegisterPojo

        isSocial = if (intent.hasExtra(keyIsSocial) && intent.getBooleanExtra(
                        keyIsSocial,
                        false
                ) != null
        ) intent.getBooleanExtra(keyIsSocial, false) else false

        // registerPasswordEditPassword?.setHint("" + GetDynamicStringDictionaryObjectClass?.Passwrod)
        registerPasswordButtonContinue?.progressText =
                ("" + GetDynamicStringDictionaryObjectClass.Sign_Up)
// pending customerTextView
        customTextView(tv_signup_terms)

        tvHeaderText.text = GetDynamicStringDictionaryObjectClass.registration

        imgCloseIcon.setOnClickListener {
            onBackPressed()
        }

        /*  if (MyUtils.userRegisterData != null) {
              if (!MyUtils.userRegisterData.userPassword.isNullOrEmpty()) registerPasswordEditPassword?.setText(
                  MyUtils.userRegisterData.userPassword
              )
              registerPasswordEditPassword?.setSelection(registerPasswordEditPassword?.text?.length!!)
          }*/


        /* registerPasswordEditPassword?.addTextChangedListener(object : TextWatcher {
             override fun onTextChanged(
                 s: CharSequence, start: Int, before: Int,
                 count: Int
             ) {
                 // TODO Auto-generated method stub
             }

             override fun beforeTextChanged(
                 s: CharSequence, start: Int, count: Int,
                 after: Int
             ) {
                 // TODO Auto-generated method stub
             }

             @SuppressLint("LongLogTag")
             override fun afterTextChanged(s: Editable) {
                 // TODO Auto-generated method stub

                 if (registerPasswordEditPassword?.text.toString().isNotEmpty()) {
                     try {
                         var x: Char
                         val t = IntArray(registerPasswordEditPassword?.text.toString().length)

                         for (i in 0 until registerPasswordEditPassword?.text.toString().length) {
                             x = registerPasswordEditPassword?.text.toString().toCharArray()[i]
                             val z = x.toInt()
                             t[i] = z

                             if (z in 65..90 || z in 97..122||z in 48..57) {

                             } else {
                                 Toast.makeText(
                                     this@RegisterPasswordActivity,
                                     "" + "Password should not contain space.",
                                     Toast.LENGTH_SHORT
                                 ).show()
                                 val ss = registerPasswordEditPassword?.text.toString().substring(
                                     0,
                                     registerPasswordEditPassword?.text.toString().length - 1
                                 )
                                 registerPasswordEditPassword?.setText(ss)
                                 registerPasswordEditPassword?.setSelection(registerPasswordEditPassword?.text.toString().length)
                             }
                         }
                     } catch (e: IndexOutOfBoundsException) {
                         Log.d("System out", "IndexOutOfBoundsException : " + e.toString())
                     }
                 }
             }
         })*/


        registerPasswordButtonContinue.setOnClickListener {
            MyUtils.hideKeyboard1(this@RegisterPasswordActivity)
            if (validation()) {
                setObject()
            }
        }
    }

    private fun setObject() {
        if (objRegister != null) {
            objRegister?.userPassword = registerPasswordEditPassword?.text.toString()
            if (MyUtils.userRegisterData != null) {
                MyUtils.userRegisterData.userPassword =
                        registerPasswordEditPassword?.text.toString()
            }

            FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
//            val newtoken = "abcg3343nsdjhskd2jl"
                        val newtoken = task.result?.token
                        if (MyUtils.isInternetAvailable(this@RegisterPasswordActivity)) {
                            userRegister(newtoken!!)
                        } else {
                            MyUtils.showSnackbarkotlin(this@RegisterPasswordActivity, registerPasswordLayoutMain!!, resources.getString(R.string.error_common_network))
                        }
                    })


        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()

        if (MyUtils.userRegisterData != null) {
            MyUtils.userRegisterData.userPassword =
                    if (!registerPasswordEditPassword?.text.toString()
                                    .isNullOrEmpty()
                    ) registerPasswordEditPassword?.text.toString() else ""
        }

        MyUtils.hideKeyboard1(this@RegisterPasswordActivity)
        MyUtils.finishActivity(this@RegisterPasswordActivity, true)

        //showAlert()
    }

    private fun showAlert() {
        MyUtils.showMessageOKCancel(
                this@RegisterPasswordActivity,
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
        msg_no_password = resources.getString(R.string.err_empty_password)
        msg_password_leth_8 = resources.getString(R.string.err_password_leth_8)
        msg_valid_password = resources.getString(R.string.err_valid_password)

        msgFailToRegister = "Failed to login if you have any query please contact to admin."
        msgSomthingRong = GetDynamicStringDictionaryObjectClass.Something_Went_Wrong
        msgNoInternet = GetDynamicStringDictionaryObjectClass.No_Internet_Connection
        msgFailToVerify = "Failed to verify mobile number."
        msgFailToResen = resources.getString(R.string.dealer_verificatrion_fail)
        msgSuccessRegister = "Registration successfully."
        msg_accept_terms = "Please accept terms and conditions."

        title_bySigninup =
                GetDynamicStringDictionaryObjectClass.By_signing_up_you_agree_with_the + " "
        title_terms = GetDynamicStringDictionaryObjectClass.Terms_Conditions
        title_privacyPolicy = GetDynamicStringDictionaryObjectClass.Privacy_Prolicy
        title_andConact = " " + GetDynamicStringDictionaryObjectClass.and + " "

//        title_bySigninup = this@RegisterPasswordActivity.resources.getString(R.string.terms)  + " "
//        title_terms = this@RegisterPasswordActivity.resources.getString(R.string.terms1)
//        title_privacyPolicy = this@RegisterPasswordActivity.resources.getString(R.string.accept_Privacy_Poilicy)
//        title_andConact = " and "
    }

    fun validation(): Boolean {
        var checkFlag = true
        if (!isSocial && registerPasswordEditPassword.text.toString().trim().isNullOrEmpty()) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(
                    this@RegisterPasswordActivity,
                    registerPasswordLayoutMain,
                    msg_no_password
            )
        } else if (!isSocial && registerPasswordEditPassword.text.toString().trim().length < 6) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(
                    this@RegisterPasswordActivity,
                    registerPasswordLayoutMain,
                    msg_password_leth_8
            )
        }/*else if (!isSocial && !MyUtils.isValidPassword(registerPasswordEditPassword.text.toString().trim())){
            checkFlag = false
            MyUtils.showSnackbarkotlin(this@RegisterPasswordActivity, registerPasswordLayoutMain, msg_valid_password)
        }*/ else if (!signup_checkbox_terms.isChecked) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(
                    this@RegisterPasswordActivity,
                    registerPasswordLayoutMain,
                    msg_accept_terms
            )
        }
        return checkFlag
    }


    private fun userRegister(newtoken: String) {
        if (!registerPasswordButtonContinue?.isStartAnim!!) registerPasswordButtonContinue.startAnimation()

        if (objRegister != null) {
            objRegister?.apiType = RestClient.apiType
            objRegister?.apiVersion = RestClient.apiVersion
            //  objRegister?.languageID = if (PrefDb(this@RegisterPasswordActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb(this@RegisterPasswordActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) else "1"
            objRegister?.languageID = sessionManager?.getsetSelectedLanguage()!!
            objRegister?.userDeviceType = RestClient.apiType
            objRegister?.userProfilePicture = ""
            objRegister?.userDeviceID = newtoken

        } else {
            Toast.makeText(applicationContext, "Else : ", Toast.LENGTH_SHORT).show()
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        jsonObject.put("apiType", RestClient.apiType)
        jsonObject.put("apiVersion", RestClient.apiVersion)
        jsonObject.put("languageID", objRegister?.languageID)
        jsonObject.put("userCountryCode", "+91")
        jsonObject.put("userDeviceID", objRegister?.userDeviceID)
        jsonObject.put("userDeviceType", objRegister?.userDeviceType)
        jsonObject.put("userEmail", objRegister?.userEmail)
        jsonObject.put("userFirstName", objRegister?.userFirstName)
        jsonObject.put("userLastName", objRegister?.userLastName)
        jsonObject.put("userMobile", objRegister?.userMobile!!.replace("+91 ", ""))
        jsonObject.put("userPassword", objRegister?.userPassword)
        jsonObject.put("userMentionID", objRegister?.userMentionID)
        jsonObject.put("userProfilePicture", objRegister?.userProfilePicture)
        jsonObject.put("userBio", objRegister?.userBio)
        jsonObject.put("userDOB", objRegister?.userDOB)
        when {
            !objRegister?.userGoogleID.isNullOrEmpty() -> {
                jsonObject.put("userGoogleID", objRegister?.userGoogleID)
                jsonObject.put("userFBID", "")
                jsonObject.put("userTwiterID", "")
            }
            !objRegister?.userFBID.isNullOrEmpty() -> {
                jsonObject.put("userGoogleID", "")
                jsonObject.put("userFBID", !objRegister?.userFBID.isNullOrEmpty())
                jsonObject.put("userTwiterID", "")
            }

        }
        jsonArray.put(jsonObject)


        Log.w("SagarSagar2", "" + objRegister?.userEmail)
        Log.w("SagarSagar2", "" + objRegister?.userFirstName)
        Log.w("SagarSagar2", "" + objRegister?.userLastName)
        Log.w("SagarSagar2", "" + objRegister?.userMobile)
        Log.w("SagarSagar2", "" + objRegister?.userPassword)
        Log.w("SagarSagar2", "" + objRegister?.userMentionID)
        Log.w("SagarSagar2", "" + objRegister?.userProfilePicture)
        Log.w("SagarSagar2", "" + objRegister?.userBio)
        Log.w("SagarSagar2", "" + objRegister?.userDOB)

        val verifyOTP = ViewModelProviders.of(this@RegisterPasswordActivity)
                .get(OnBoardingModel::class.java)

        verifyOTP.apiCall(this@RegisterPasswordActivity, jsonArray.toString(), 0)
                .observe(this@RegisterPasswordActivity, object : Observer<List<RegisterPojo>?> {
                    override fun onChanged(response: List<RegisterPojo>?) {

                        if (!response.isNullOrEmpty()) {
                            if (registerPasswordButtonContinue?.isStartAnim!!) registerPasswordButtonContinue.endAnimation()

                            if (response[0].status.equals("true", true)) {
                                MyUtils.hideKeyboard1(this@RegisterPasswordActivity)

                                if (!response[0].data.isNullOrEmpty()) {
                                    sessionManager?.clear_login_session()
                                    storeSessionManager(response[0].data!!)

                                    Handler().postDelayed({

                                        if (!objRegister?.userMobile.isNullOrEmpty()) {

                                            objRegister?.userId = response[0].data!![0].userID

                                            val myIntent = Intent(
                                                    this@RegisterPasswordActivity,
                                                    MainActivity::class.java
                                            )
                                            myIntent.putExtra("from", "password")
                                            myIntent.putExtra(keyRequestObj, objRegister)
                                            myIntent.putExtra(keyIsSocial, isSocial)
                                            startActivity(myIntent)
                                            finishAffinity()
                                            overridePendingTransition(
                                                    R.anim.slide_in_right,
                                                    R.anim.slide_out_left
                                            )
                                        } else {
                                            val myIntent = Intent(
                                                    this@RegisterPasswordActivity,
                                                    MainActivity::class.java
                                            )
                                            myIntent.putExtra("from", "MyProfileEdit")
                                            startActivity(myIntent)
                                            finishAffinity()
                                            overridePendingTransition(
                                                    R.anim.slide_in_right,
                                                    R.anim.slide_out_left
                                            )
                                        }
                                    }, 500)
                                    MyUtils.userRegisterData = RequestRegisterPojo()

                                } else {
                                    MyUtils.showMessageOK(
                                            this@RegisterPasswordActivity,
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
                                if (MyUtils.isInternetAvailable(this@RegisterPasswordActivity)) {
                                    MyUtils.showSnackbarkotlin(
                                            this@RegisterPasswordActivity,
                                            registerPasswordLayoutMain,
                                            response[0].message
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                            this@RegisterPasswordActivity,
                                            registerPasswordLayoutMain,
                                            msgNoInternet
                                    )
                                }
                            }

                        } else {
                            if (registerPasswordButtonContinue?.isStartAnim!!) registerPasswordButtonContinue.endAnimation()

                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@RegisterPasswordActivity)) {
                                MyUtils.showSnackbarkotlin(
                                        this@RegisterPasswordActivity,
                                        registerPasswordLayoutMain,
                                        msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                        this@RegisterPasswordActivity,
                                        registerPasswordLayoutMain,
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
                driverdata[0]!!.userEmail,
                "",
                true,
                sessionManager?.isEmailLogin()!!,
                driverdata[0]!!.userFirstName + " " + driverdata[0]!!.userLastName,
                driverdata[0]!!.userProfilePicture
        )
    }

    private fun customTextView(view: AppCompatTextView) {
        val spanTxt = SpannableStringBuilder("" + title_bySigninup)
        val signup = title_terms
        val privacyPolicy = title_privacyPolicy
        val andContact = title_andConact

        spanTxt.append("\n" + signup)
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val myIntent =
                        Intent(this@RegisterPasswordActivity, TermsandConditionsActivity::class.java)
                myIntent.putExtra("Type", "Terms")
                startActivity(myIntent)
            }
        }, spanTxt.length - signup.length, spanTxt.length, 0)
        spanTxt.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.text_blue_dark)),
                spanTxt.length - signup.length,
                spanTxt.length,
                0
        )

        spanTxt.append(andContact)
        spanTxt.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.text_black)),
                spanTxt.length - andContact.length,
                spanTxt.length,
                0
        )

        spanTxt.append(privacyPolicy)
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val myIntent =
                        Intent(this@RegisterPasswordActivity, TermsandConditionsActivity::class.java)
                myIntent.putExtra("Type", "Privacy Policy")
                startActivity(myIntent)
            }
        }, spanTxt.length - privacyPolicy.length, spanTxt.length, 0)
        spanTxt.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.text_blue_dark)),
                spanTxt.length - privacyPolicy.length,
                spanTxt.length,
                0
        )
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }
}
