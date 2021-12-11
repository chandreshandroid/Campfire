package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.flymyowncustomer.model.ResetPasswordModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import org.json.JSONArray
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    var headertitle = ""
    var msg_no_password = ""
    var msg_confirm_password = ""
    var msg_confirm_valid_password = ""
    var msg_password_leth_8 = ""
    var msg_valid_password = ""

    var userId = ""
    var userMobile = ""
    var userEmail = ""
    var keyUserId = "keyUserId"
    var keyUserMobile = "keyUserMobile"
    var keyUserEmail = "keyUserEmail"

    var msgFailToRegister = ""
    var msgSomthingRong = ""
    var msgNoInternet = ""
    var change_password_success = ""
    var change_password_failed = ""
    var sessionManager:SessionManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        sessionManager= SessionManager(this@ResetPasswordActivity)
        if (intent != null) {
            userId = intent.getStringExtra(keyUserId)!!
        }
        dynamicLable()
        forgotpasswordEditEnterPasswordMobile?.setHint(""+GetDynamicStringDictionaryObjectClass?.Enter_New_Password)
        forgotpasswordEditReEnterPasswordMobile?.setHint(""+GetDynamicStringDictionaryObjectClass?.Re_enter_New_Password)
        forgotPasswordButton?.progressText=(""+GetDynamicStringDictionaryObjectClass?.Reset_Password)


        tvHeaderText.text = GetDynamicStringDictionaryObjectClass?.Reset_Password

        imgCloseIcon.setOnClickListener {
            onBackPressed()
        }

        forgotpasswordEditReEnterPasswordMobile?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                    validation()
                    return true

                }
                return false
            }

        })

        /*forgotpasswordEditReEnterPasswordMobile?.addTextChangedListener(object : TextWatcher {
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

                if (forgotpasswordEditReEnterPasswordMobile?.text.toString().isNotEmpty()) {
                    try {
                        var x: Char
                        val t = IntArray(forgotpasswordEditReEnterPasswordMobile?.text.toString().length)

                        for (i in 0 until forgotpasswordEditReEnterPasswordMobile?.text.toString().length) {
                            x = forgotpasswordEditReEnterPasswordMobile?.text.toString().toCharArray()[i]
                            val z = x.toInt()
                            t[i] = z

                            if (z in 65..90 || z in 97..122||z in 48..57)
                            {

                            }
                            else
                            {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
                                    "" + "Password should not contain space.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val ss = forgotpasswordEditReEnterPasswordMobile?.text.toString().substring(
                                    0,
                                    forgotpasswordEditReEnterPasswordMobile?.text.toString().length - 1
                                )
                                forgotpasswordEditReEnterPasswordMobile?.setText(ss)
                                forgotpasswordEditReEnterPasswordMobile?.setSelection(forgotpasswordEditReEnterPasswordMobile?.text.toString().length)
                            }
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        Log.d("System out", "IndexOutOfBoundsException : " + e.toString())
                    }
                }
            }
        })

        forgotpasswordEditEnterPasswordMobile?.addTextChangedListener(object : TextWatcher {
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

                if (forgotpasswordEditEnterPasswordMobile?.text.toString().isNotEmpty()) {
                    try {
                        var x: Char
                        val t = IntArray(forgotpasswordEditEnterPasswordMobile?.text.toString().length)

                        for (i in 0 until forgotpasswordEditEnterPasswordMobile?.text.toString().length) {
                            x = forgotpasswordEditEnterPasswordMobile?.text.toString().toCharArray()[i]
                            val z = x.toInt()
                            t[i] = z

                            if (z in 65..90 || z in 97..122 ||z in 48..57) {

                            } else {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
                                    "" + "Password should not contain space.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val ss = forgotpasswordEditEnterPasswordMobile?.text.toString().substring(
                                    0,
                                    forgotpasswordEditEnterPasswordMobile?.text.toString().length - 1
                                )
                                forgotpasswordEditEnterPasswordMobile?.setText(ss)
                                forgotpasswordEditEnterPasswordMobile?.setSelection(forgotpasswordEditEnterPasswordMobile?.text.toString().length)
                            }
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        Log.d("System out", "IndexOutOfBoundsException : " + e.toString())
                    }
                }
            }
        })*/



        forgotPasswordButton.setOnClickListener {
            MyUtils.hideKeyboard1(this@ResetPasswordActivity)
            validation()
        }
    }

    private fun validation() {
        MyUtils.hideKeyboard1(this@ResetPasswordActivity)
        if (TextUtils.isEmpty(forgotpasswordEditEnterPasswordMobile?.text.toString().trim())) {

            MyUtils.showSnackbarkotlin(this@ResetPasswordActivity,ll_reset_password,msg_no_password)

        } else if (forgotpasswordEditEnterPasswordMobile?.text.toString().trim().length < 6) {
            MyUtils.showSnackbarkotlin(this@ResetPasswordActivity,ll_reset_password,msg_password_leth_8)

        }/*else if (MyUtils.isValidPassword(forgotpasswordEditEnterPasswordMobile?.text.toString().trim()) == false){
            MyUtils.showSnackbarkotlin(this@ResetPasswordActivity, ll_reset_password, msg_valid_password)
        }*/
        else if (TextUtils.isEmpty(forgotpasswordEditReEnterPasswordMobile.text.toString().trim())) {
            MyUtils.showSnackbarkotlin(this@ResetPasswordActivity,ll_reset_password,msg_confirm_password)
        }else if (forgotpasswordEditReEnterPasswordMobile?.text.toString().trim().length < 6) {
            MyUtils.showSnackbarkotlin(this@ResetPasswordActivity,ll_reset_password,msg_password_leth_8)
        }
        /*else if (MyUtils.isValidPassword(forgotpasswordEditReEnterPasswordMobile?.text.toString().trim()) == false){
            MyUtils.showSnackbarkotlin(this@ResetPasswordActivity, ll_reset_password, msg_valid_password)
        }*/
        else if (!forgotpasswordEditEnterPasswordMobile.text.toString().trim().equals(forgotpasswordEditReEnterPasswordMobile.text.toString().trim(), false)) {
            MyUtils.showSnackbarkotlin(this@ResetPasswordActivity,ll_reset_password,msg_confirm_valid_password)

        } else{
            if (MyUtils.isInternetAvailable(this@ResetPasswordActivity)) {
                getUserChangePwdAPI()
            } else {
                MyUtils.showSnackbarkotlin(this@ResetPasswordActivity,ll_reset_password,msgNoInternet)
            }

        }


    }

    override fun onBackPressed() {
        //super.onBackPressed()
        MyUtils.hideKeyboard1(this@ResetPasswordActivity)
        MyUtils.showMessageYesNo(this@ResetPasswordActivity, "If you press yes then you will navigate to forgot password screen.", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
//                val i = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
//                startActivity(i)
//                finishAffinity()
                val intent1 = Intent("PostSuccess")
                intent1.putExtra("Successfully", "Success")
                LocalBroadcastManager.getInstance(this@ResetPasswordActivity).sendBroadcast(intent1)
                finish()
            }
        })
    }

    fun showSnackBar(message: String) {
        if ((ll_reset_password != null) and !isFinishing)
            Snackbar.make(this.ll_reset_password!!, message, Snackbar.LENGTH_LONG).show()
    }

    private fun dynamicLable() {
        headertitle = resources.getString(R.string.reset_pwd)
        msg_no_password = resources.getString(R.string.please_enter_new_password)
        msg_password_leth_8 = resources.getString(R.string.minimum_password_error)
        msg_valid_password = resources.getString(R.string.err_valid_password)
        msg_confirm_password = resources.getString(R.string.please_enter_confirm_password)
        msg_confirm_valid_password = resources.getString(R.string.password_does_not_match)

        msgSomthingRong =  GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong
        msgNoInternet = GetDynamicStringDictionaryObjectClass?.No_Internet_Connection

        change_password_success = ""+ resources.getString(R.string.change_password_success)
        change_password_failed = ""+ resources.getString(R.string.change_password_failed)
    }

    private fun getUserChangePwdAPI() {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            forgotPasswordButton?.startAnimation()
            MyUtils.setViewAndChildrenEnabled(ll_reset_password, false)

            jsonObject.put("loginuserID", userId)
           // jsonObject.put("languageID", PrefDb(this@ResetPasswordActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()))
            jsonObject.put("languageID", sessionManager?.getsetSelectedLanguage())

            jsonObject.put("userNewPassword", forgotpasswordEditReEnterPasswordMobile?.text.toString().trim())
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
            Log.e("System out", "Reset password api call := " + jsonArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var getUserChangePwdModel = ViewModelProviders.of(this@ResetPasswordActivity).get(
            ResetPasswordModel::class.java)
        getUserChangePwdModel.apiFunction(this@ResetPasswordActivity, false, jsonArray.toString())
            .observe(this@ResetPasswordActivity, Observer<List<RegisterPojo>> { changePwdPojo ->

                if (changePwdPojo != null) {
                    if (changePwdPojo[0].status.equals("true", true)) {
                        forgotPasswordButton?.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(ll_reset_password, true)
                        if (!changePwdPojo[0].message.isNullOrEmpty()){
                            MyUtils.showSnackbarkotlin(this@ResetPasswordActivity, ll_reset_password, changePwdPojo[0].message!!)
                        }else{
                            MyUtils.showSnackbarkotlin(this@ResetPasswordActivity, ll_reset_password, resources.getString(R.string.change_password_success))
                        }
                        Handler().postDelayed({
                         
                            val myIntent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                            this@ResetPasswordActivity.startActivity(myIntent)
                            this@ResetPasswordActivity.finishAffinity()
                        }, 1500)
                    } else {
                        forgotPasswordButton?.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(ll_reset_password, true)
                        if (!changePwdPojo[0].message.isNullOrEmpty()){
                            MyUtils.showSnackbarkotlin(this@ResetPasswordActivity, ll_reset_password, changePwdPojo[0].message!!)
                        }else {
                            MyUtils.showSnackbarkotlin(this@ResetPasswordActivity, ll_reset_password, resources.getString(R.string.change_password_failed))
                        }
                    }
                } else {
                    forgotPasswordButton?.endAnimation()
                    MyUtils.setViewAndChildrenEnabled(ll_reset_password, true)
                    errorMethod()
                }
            })

    }

    fun errorMethod() {
        try {
            if (!MyUtils.isInternetAvailable(this@ResetPasswordActivity)) {
                MyUtils.showSnackbarkotlin(this@ResetPasswordActivity, ll_reset_password, msgNoInternet)

            } else {
                MyUtils.showSnackbarkotlin(this@ResetPasswordActivity, ll_reset_password, msgSomthingRong)

            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
}
