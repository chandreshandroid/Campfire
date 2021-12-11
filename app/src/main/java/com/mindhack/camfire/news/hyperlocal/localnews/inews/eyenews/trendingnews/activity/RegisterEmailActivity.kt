package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonParseException
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OtpResendAndCheckDuplicationModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RequestRegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.activity_register_email.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import org.json.JSONArray
import org.json.JSONObject



class RegisterEmailActivity : AppCompatActivity() {

    var titleRegister = ""
    var msg_no_email = ""
    var msg_valid_email = ""
    var msg_special_character = ""

    var objRegister : RequestRegisterPojo? = null
    val keyRequestObj = "REQUESTOBJ"
    val keyIsSocial = "SOCIALLOGIN"
    var isSocial = false
    var msgAlreadyExist = ""
    var msgSomthingRong = ""
    var msgNoInternet = ""
    var sessionManager: SessionManager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.activity_register_email)
        sessionManager=SessionManager(this@RegisterEmailActivity)
        dynamicLable()

        if (intent.hasExtra(keyRequestObj) && intent.getSerializableExtra(keyRequestObj) != null)
            objRegister = intent.getSerializableExtra(keyRequestObj) as RequestRegisterPojo

        isSocial = if (intent.hasExtra(keyIsSocial) && intent.getBooleanExtra(keyIsSocial, false) != null) intent.getBooleanExtra(keyIsSocial, false) else false

        registerEmailEditEmail.isEnabled = !isSocial
        registerEmailEditEmail?.setHint(""+ GetDynamicStringDictionaryObjectClass.Email_Id)

        registerEmailButtonContinue.progressText = ""+ GetDynamicStringDictionaryObjectClass.next

        tvHeaderText.text = GetDynamicStringDictionaryObjectClass.registration

        imgCloseIcon.setOnClickListener {
            onBackPressed()
        }

        if (MyUtils.userRegisterData != null){
            if (!MyUtils.userRegisterData.userEmail.isNullOrEmpty()){
                registerEmailEditEmail?.setText(MyUtils.userRegisterData.userEmail)
                registerEmailEditEmail?.setSelection(registerEmailEditEmail?.text?.length!!)
            }
        }

        registerEmailEditEmail?.addTextChangedListener(object : TextWatcher {
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

                if (registerEmailEditEmail?.text.toString().isNotEmpty()) {
                    try {
                        var x: Char
                        val t = IntArray(registerEmailEditEmail?.text.toString().length)

                        for (i in 0 until registerEmailEditEmail?.text.toString().length) {
                            x = registerEmailEditEmail?.text.toString().toCharArray()[i]
                            val z = x.toInt()
                            t[i] = z

                            if (z in 65..90 || z in 97..122 || z in 45..46 || z in 48..57 || z == 64 || z == 95) {

                            } else {
                                Toast.makeText(
                                    this@RegisterEmailActivity,
                                    "" + msg_special_character,
                                    Toast.LENGTH_SHORT
                                ).show()
                                val ss = registerEmailEditEmail?.text.toString().substring(
                                    0,
                                    registerEmailEditEmail?.text.toString().length - 1
                                )
                                registerEmailEditEmail?.setText(ss)
                                registerEmailEditEmail?.setSelection(registerEmailEditEmail?.text.toString().length)
                            }
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        Log.d("System out", "IndexOutOfBoundsException : " + e.toString())
                    }
                }
            }
        })


        registerEmailButtonContinue.setOnClickListener {
            MyUtils.hideKeyboard1(this@RegisterEmailActivity)
            if (validation()) {
                checkForDuplication()
            }
        }
    }

    private fun setObject() {
        if (objRegister != null){
            objRegister?.userEmail = registerEmailEditEmail?.text.toString()


            if (MyUtils.userRegisterData != null){
                MyUtils.userRegisterData.userEmail = registerEmailEditEmail?.text.toString()
            }

            val myIntent =
                Intent(this@RegisterEmailActivity, RegisterMobileActivity::class.java)
            myIntent.putExtra(keyRequestObj, objRegister)
            myIntent.putExtra(keyIsSocial, isSocial)
            startActivity(myIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()

        if (MyUtils.userRegisterData != null)
        {
            MyUtils.userRegisterData.userEmail = if (!registerEmailEditEmail?.text.toString().isNullOrEmpty()) registerEmailEditEmail?.text.toString() else ""
        }

        MyUtils.hideKeyboard1(this@RegisterEmailActivity)
        MyUtils.finishActivity(this@RegisterEmailActivity,true)
       // showAlert()
    }


    private fun showAlert() {
        MyUtils.hideKeyboard1(this@RegisterEmailActivity)
        MyUtils.showMessageOKCancel(
            this@RegisterEmailActivity,
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
        msg_no_email = resources.getString(R.string.err_empty_email)
        msg_valid_email = resources.getString(R.string.err_valid_email)
        msg_special_character = resources.getString(R.string.err_special_character)
        msgSomthingRong = GetDynamicStringDictionaryObjectClass.Something_Went_Wrong
        msgNoInternet = GetDynamicStringDictionaryObjectClass.No_Internet_Connection
        msgAlreadyExist = "Email already exist."
    }

    fun validation(): Boolean {
        var checkFlag = true
        if (registerEmailEditEmail.text.toString().isNullOrEmpty() || registerEmailEditEmail.text.toString().isNullOrBlank()) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(
                this@RegisterEmailActivity,
                registerEmailLayoutMain,
                msg_no_email
            )
        } else if (!MyUtils.isEmailValid(registerEmailEditEmail.text.toString())) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(this@RegisterEmailActivity,registerEmailLayoutMain,msg_valid_email)
        }
        return checkFlag
    }


    fun checkForDuplication(){

        if (!registerEmailButtonContinue.isStartAnim) registerEmailButtonContinue?.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()


        /*[{
            "loginuserID": "0",
            "userEmail": "dhavalgds@gmail.com",
            "userMobile": "",
            "languageID": "1",
            "apiType": "Android",
            "apiVersion": "1.0"
        }]*/

      //  val lID = if (PrefDb(this@RegisterEmailActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb(this@RegisterEmailActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) else "1"
        val lID =sessionManager?.getsetSelectedLanguage()

        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("userMobile", "")
            jsonObject.put("userEmail", registerEmailEditEmail.text.toString())
            jsonObject.put("languageID", lID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val resendOTP = ViewModelProviders.of(this@RegisterEmailActivity)
            .get(OtpResendAndCheckDuplicationModel::class.java)
        resendOTP.apiCall(this@RegisterEmailActivity, jsonArray.toString(), 1)
            .observe(this@RegisterEmailActivity,
                object : Observer<List<CommonPojo?>?> {
                    override fun onChanged(response: List<CommonPojo?>?) {
                        if (!response.isNullOrEmpty()) {
                            if (registerEmailButtonContinue.isStartAnim) registerEmailButtonContinue?.endAnimation()

                            if (response[0]?.status.equals("true", true)) {
                                setObject()
                            } else {
                                //No data and no internet
                                if (MyUtils.isInternetAvailable(this@RegisterEmailActivity)) {
                                    if (!response[0]?.message.isNullOrEmpty()) {
                                        MyUtils.showSnackbarkotlin(
                                            this@RegisterEmailActivity,
                                            registerEmailLayoutMain,
                                            response[0]?.message!!
                                        )
                                    } else {
                                        MyUtils.showSnackbarkotlin(
                                            this@RegisterEmailActivity,
                                            registerEmailLayoutMain,
                                            msgAlreadyExist
                                        )
                                    }
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        this@RegisterEmailActivity,
                                        registerEmailLayoutMain,
                                        msgNoInternet
                                    )
                                }
                            }
                        } else {
                            if (registerEmailButtonContinue.isStartAnim) registerEmailButtonContinue?.endAnimation()

                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@RegisterEmailActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterEmailActivity,
                                    registerEmailLayoutMain,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterEmailActivity,
                                    registerEmailLayoutMain,
                                    msgNoInternet)
                            }
                        }
                    }
                })

    }

}
