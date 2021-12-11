package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.gson.Gson
import com.google.gson.JsonParseException
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
import kotlinx.android.synthetic.main.activity_ragister_profile.*
import kotlinx.android.synthetic.main.activity_ragister_profile.registerEmailEditEmail
import kotlinx.android.synthetic.main.header_back_with_text_space.*
import org.json.JSONArray
import org.json.JSONObject

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.view.WindowManager
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.drawable.ColorDrawable
import android.widget.DatePicker
import androidx.fragment.app.FragmentActivity


class RegisterNameActivity : AppCompatActivity() {


//    var sessionManager: SessionManager? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        FacebookSdk.sdkInitialize(getApplicationContext())
//        sessionManager = SessionManager(this@RegisterNameActivity)
//        setContentView(R.layout.activity_ragister_profile)
//
////            .requestIdToken(getString(R.string.default_web_client_id))
//
//        btnNext.setOnClickListener {
//            MyUtils.hideKeyboard1(this@RegisterNameActivity)
//            startActivity(Intent(this@RegisterNameActivity, RegisterMobileActivity::class.java))
//
//
//        }
//    }
//
//
//    override fun onBackPressed() {
//        // super.onBackPressed()
//        MyUtils.hideKeyboard1(this@RegisterNameActivity)
//        MyUtils.userRegisterData = RequestRegisterPojo()
//        finish()
//    }

    var titleRegister = ""
    var msg_no_email = ""
    var msg_valid_email = ""
    var msgAlreadyExist = ""

    var msg_no_first_name = ""
    var msg_first_name_3_character = ""
    var msg_no_last_name = ""
    var msg_last_name_3_character = ""
    var msg_special_character = ""
    var msg_accept_terms = "Please accept terms and conditions."
    var title_bySigninup = ""
    var title_terms = ""
    var title_privacyPolicy = ""
    var title_andConact = ""

    var objRegister: RequestRegisterPojo? = null
    val keyRequestObj = "REQUESTOBJ"
    val keyIsSocial = "SOCIALLOGIN"
    var isSocial = false
    var validEmailOrNot = false
    var user_Name = ""
    var user_Email = ""
    var fbID = ""
    var userGoogleID = ""
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    private var mDateSetListener: OnDateSetListener? = null

    // [END declare_auth]
    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9002
    }

    var msgNoInternet = ""
    var msgSomthingRong = ""

    var sessionManager: SessionManager? = null
    var bod : String ?="0000-00-00"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getWindow().setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        );
        FacebookSdk.sdkInitialize(getApplicationContext())
        sessionManager = SessionManager(this@RegisterNameActivity)
        callbackManager = CallbackManager.Factory.create()
        setContentView(R.layout.activity_ragister_profile)
        dynamicLable()
        objRegister = RequestRegisterPojo()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(this@RegisterNameActivity.resources.getString(R.string.server_client_id))
            .requestEmail()
            .build()

//            .requestIdToken(getString(R.string.default_web_client_id))

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        isSocial = if (intent.getBooleanExtra(keyIsSocial, false))
            intent.getBooleanExtra(keyIsSocial, false) else false

        registerNameEditFirstName.setHint(GetDynamicStringDictionaryObjectClass.firstName)
        registerNameEditLastName.setHint(GetDynamicStringDictionaryObjectClass.lastName)
        registerNameButtonContinue.progressText = "" + GetDynamicStringDictionaryObjectClass.next
        registerEmailEditEmail.isEnabled = !isSocial
        registerEmailEditEmail?.setHint(""+ GetDynamicStringDictionaryObjectClass.Email_Id)
        msg_no_first_name = resources.getString(R.string.err_empty_name)
        msg_first_name_3_character = resources.getString(R.string.err_first_name_leth)
        msg_no_last_name = resources.getString(R.string.err_last_name)
        msg_last_name_3_character = resources.getString(R.string.err_last_name_leth)
        msg_special_character = resources.getString(R.string.err_special_character)

        registerdobEditDateOfBrith.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            val month = cal[Calendar.MONTH]
            val day = cal[Calendar.DAY_OF_MONTH]

            val dialog = DatePickerDialog(
                this@RegisterNameActivity,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
        mDateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month = month + 1

                val date = "$day-$month-$year"
                registerdobEditDateOfBrith.setText(date)
                bod="$year-$month-$day"
                objRegister?.userDOB = bod

            }

        tvHeaderText.text = GetDynamicStringDictionaryObjectClass.registration
        setSocialData()
        imgCloseIcon.setOnClickListener {
            onBackPressed()
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
//                                checkForDuplication()
                                Log.d("System out", "IndexOutOfBoundsException : Done")

                            } else {
                                Toast.makeText(
                                    this@RegisterNameActivity,
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

                if (validationEmail()) {
                    checkForDuplicationEmail()
                }
            }
        })


        //Set OlData
        if (MyUtils.userRegisterData != null) {
            if (!MyUtils.userRegisterData.userFirstName.isNullOrEmpty()) {
//                if (MyUtils.userRegisterData.userFirstName!!.contains(" ")){
//                    MyUtils.userRegisterData.userFirstName.replace(" ",  "")
                registerNameEditFirstName?.setText(
                    MyUtils.userRegisterData.userFirstName!!
                )
//                }else{
//                    registerNameEditFirstName?.setText(MyUtils.userRegisterData.userFirstName)
//                }
            }
            if (!MyUtils.userRegisterData.userLastName.isNullOrEmpty()) registerNameEditLastName?.setText(
                MyUtils.userRegisterData.userLastName
            )

            if (!MyUtils.userRegisterData.userEmail.isNullOrEmpty()) registerEmailEditEmail?.setText(
                MyUtils.userRegisterData.userEmail

            )
        }


        // [END initialize_fblogin]

        registerNameEditFirstName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
        registerNameEditFirstName?.addTextChangedListener(object : TextWatcher {
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

                if (registerNameEditFirstName?.text.toString().isNotEmpty()) {
                    try {
                        var x: Char
                        val t = IntArray(registerNameEditFirstName?.text.toString().length)

                        for (i in 0 until registerNameEditFirstName?.text.toString().length) {
                            x = registerNameEditFirstName?.text.toString().toCharArray()[i]
                            val z = x.toInt()
                            t[i] = z

                            if (z in 65..90 || z in 97..122) {

                            } else {
                                Toast.makeText(
                                    this@RegisterNameActivity,
                                    "" + msg_special_character,
                                    Toast.LENGTH_SHORT
                                ).show()
                                val ss = registerNameEditFirstName?.text.toString().substring(
                                    0,
                                    registerNameEditFirstName?.text.toString().length - 1
                                )
                                registerNameEditFirstName?.setText(ss)
                                registerNameEditFirstName?.setSelection(registerNameEditFirstName?.text.toString().length)
                            }
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        Log.d("System out", "IndexOutOfBoundsException : " + e.toString())
                    }
                }
            }
        })

        registerNameEditLastName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
        registerNameEditLastName?.addTextChangedListener(object : TextWatcher {
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

                if (registerNameEditLastName?.text.toString().isNotEmpty()) {
                    try {
                        var x: Char
                        val t = IntArray(registerNameEditLastName?.text.toString().length)

                        for (i in 0 until registerNameEditLastName?.text.toString().length) {
                            x = registerNameEditLastName?.text.toString().toCharArray()[i]
                            val z = x.toInt()
                            t[i] = z

                            if (z in 65..90 || z in 97..122) {

                            } else {
                                Toast.makeText(
                                    this@RegisterNameActivity,
                                    "" + msg_special_character,
                                    Toast.LENGTH_SHORT
                                ).show()
                                val ss = registerNameEditLastName?.text.toString().substring(
                                    0,
                                    registerNameEditLastName?.text.toString().length - 1
                                )
                                registerNameEditLastName?.setText(ss)
                                registerNameEditLastName?.setSelection(registerNameEditLastName?.text.toString().length)
                            }
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        Log.d("System out", "IndexOutOfBoundsException : " + e.toString())
                    }
                }
            }
        })

        registerNameButtonContinue.setOnClickListener {
            MyUtils.hideKeyboard1(this@RegisterNameActivity)
            if (validation()) {
                if(validEmailOrNot)
                checkForDuplication()


            }
        }
    }


    private fun setSocialData() {
//        if(isSocial)
//        {
//            ll_social_login.visibility= View.GONE
//            tvOrRegisterWith.visibility= View.GONE
//        }
//        else
//        {
//            ll_social_login.visibility=View.VISIBLE
//            tvOrRegisterWith.visibility=View.VISIBLE
//        }
    }

    private fun setObject() {

        if (objRegister != null) {
            objRegister?.userFirstName = registerNameEditFirstName?.text.toString()
            objRegister?.userLastName = registerNameEditLastName?.text.toString()

            if (isSocial && MyUtils.userRegisterData != null) {
                objRegister?.userFBID = MyUtils.userRegisterData.userFBID
                objRegister?.userGoogleID = MyUtils.userRegisterData.userGoogleID
                objRegister?.userTwiterID = MyUtils.userRegisterData.userTwiterID
            }

            if (MyUtils.userRegisterData != null) {
                MyUtils.userRegisterData.userFirstName = registerNameEditFirstName?.text.toString()
                MyUtils.userRegisterData.userLastName = registerNameEditLastName?.text.toString()
            }

            objRegister?.userEmail = registerEmailEditEmail?.text.toString()
            objRegister?.userBio = etBiocreate?.text.toString()


            if (MyUtils.userRegisterData != null){
                MyUtils.userRegisterData.userEmail = registerEmailEditEmail?.text.toString()
            }

            Log.w("SagarSagar1",""+objRegister?.userFirstName);
            Log.w("SagarSagar1",""+objRegister?.userLastName);
            Log.w("SagarSagar1",""+objRegister?.userEmail);
            Log.w("SagarSagar1",""+objRegister?.userBio);
            Log.w("SagarSagar1",""+objRegister?.userDOB);
            val myIntent =
                Intent(this@RegisterNameActivity, RegisterMobileActivity::class.java)
            myIntent.putExtra(keyRequestObj, objRegister)
            myIntent.putExtra(keyIsSocial, isSocial)
            startActivity(myIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

//            val myIntent = Intent(this@RegisterNameActivity, RegisterEmailActivity::class.java)
//            myIntent.putExtra(keyRequestObj, objRegister)
//            myIntent.putExtra(keyIsSocial, isSocial)
//            startActivity(myIntent)
//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }



    fun checkForDuplicationEmail(){

        if (!registerNameButtonContinue.isStartAnim) registerNameButtonContinue?.startAnimation()

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

        val resendOTP = ViewModelProviders.of(this@RegisterNameActivity)
            .get(OtpResendAndCheckDuplicationModel::class.java)
        resendOTP.apiCall(this@RegisterNameActivity, jsonArray.toString(), 1)
            .observe(this@RegisterNameActivity,
                object : Observer<List<CommonPojo?>?> {
                    override fun onChanged(response: List<CommonPojo?>?) {
                        MyUtils.hideKeyboard1(this@RegisterNameActivity)

                        if (!response.isNullOrEmpty()) {
                            if (registerNameButtonContinue.isStartAnim) registerNameButtonContinue?.endAnimation()

                            if (response[0]?.status.equals("true", true)) {
                                validEmailOrNot=true

                            } else {
                                validEmailOrNot=false

                                //No data and no internet
                                if (MyUtils.isInternetAvailable(this@RegisterNameActivity)) {
                                    if (!response[0]?.message.isNullOrEmpty()) {
                                        MyUtils.showSnackbarkotlin(
                                            this@RegisterNameActivity,
                                            registerNameLayoutMain,
                                            response[0]?.message!!
                                        )
                                    } else {
                                        MyUtils.showSnackbarkotlin(
                                            this@RegisterNameActivity,
                                            registerNameLayoutMain,
                                            msgAlreadyExist
                                        )
                                    }
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        this@RegisterNameActivity,
                                        registerNameLayoutMain,
                                        msgNoInternet
                                    )
                                }
                            }
                        } else {
                            validEmailOrNot=false
                            if (registerNameButtonContinue.isStartAnim) registerNameButtonContinue?.endAnimation()

                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@RegisterNameActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterNameActivity,
                                    registerNameLayoutMain,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterNameActivity,
                                    registerNameLayoutMain,
                                    msgNoInternet)
                            }
                        }
                    }
                })

    }


    fun checkForDuplication(){

        if (!registerNameButtonContinue.isStartAnim) registerNameButtonContinue?.startAnimation()

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

        //  val lID = if (PrefDb(this@RegisterNameActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb(this@RegisterNameActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) else "1"
        val lID =sessionManager?.getsetSelectedLanguage()

        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("userMobile", "")
            jsonObject.put("userEmail", registerEmailEditEmail.text.toString())
            jsonObject.put("languageID", lID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val resendOTP = ViewModelProviders.of(this@RegisterNameActivity)
            .get(OtpResendAndCheckDuplicationModel::class.java)
        resendOTP.apiCall(this@RegisterNameActivity, jsonArray.toString(), 1)
            .observe(this@RegisterNameActivity,
                object : Observer<List<CommonPojo?>?> {
                    override fun onChanged(response: List<CommonPojo?>?) {
                        if (!response.isNullOrEmpty()) {
                            if (registerNameButtonContinue.isStartAnim) registerNameButtonContinue?.endAnimation()

                            Log.w("SagarSagar",""+response[0]?.status)
                            if (response[0]?.status.equals("true", true)) {
                                setObject()
                            } else {
                                //No data and no internet
                                if (MyUtils.isInternetAvailable(this@RegisterNameActivity)) {
                                    if (!response[0]?.message.isNullOrEmpty()) {
                                        MyUtils.showSnackbarkotlin(
                                            this@RegisterNameActivity,
                                            registerNameLayoutMain,
                                            response[0]?.message!!
                                        )
                                    } else {
                                        MyUtils.showSnackbarkotlin(
                                            this@RegisterNameActivity,
                                            registerNameLayoutMain,
                                            msgAlreadyExist
                                        )
                                    }
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        this@RegisterNameActivity,
                                        registerNameLayoutMain,
                                        msgNoInternet
                                    )
                                }
                            }
                        } else {
                            if (registerNameButtonContinue.isStartAnim) registerNameButtonContinue?.endAnimation()

                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@RegisterNameActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterNameActivity,
                                    registerNameLayoutMain,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterNameActivity,
                                    registerNameLayoutMain,
                                    msgNoInternet)
                            }
                        }
                    }
                })

    }

    private fun dynamicLable() {
        titleRegister = resources.getString(R.string.registration)
        msg_no_email = resources.getString(R.string.err_empty_email)
        msg_valid_email = resources.getString(R.string.err_valid_email)
        msgAlreadyExist = "Email already exist."

        msg_no_first_name = resources.getString(R.string.err_empty_name)
        msg_first_name_3_character = resources.getString(R.string.err_first_name_leth)
        msg_no_last_name = resources.getString(R.string.err_last_name)
        msg_last_name_3_character = resources.getString(R.string.err_last_name_leth)
        msg_special_character = resources.getString(R.string.err_special_character)

        msgSomthingRong = resources.getString(R.string.error_crash_error_message)
        msgNoInternet = resources.getString(R.string.error_common_netdon_t_have_and_accountwork)

        title_bySigninup =
            GetDynamicStringDictionaryObjectClass?.By_signing_up_you_agree_with_the + " "
        title_terms = GetDynamicStringDictionaryObjectClass?.Terms_Conditions
        title_privacyPolicy = GetDynamicStringDictionaryObjectClass?.Privacy_Prolicy
        title_andConact = " " + GetDynamicStringDictionaryObjectClass?.and + " "

    }


    fun validationEmail(): Boolean {
        var checkFlag = true
        if (registerEmailEditEmail.text.toString().isNullOrEmpty() || registerEmailEditEmail.text.toString().isNullOrBlank()) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(
                this@RegisterNameActivity,
                registerNameLayoutMain,
                msg_no_email
            )
        } else if (!MyUtils.isEmailValid(registerEmailEditEmail.text.toString())) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(this@RegisterNameActivity,registerNameLayoutMain,msg_valid_email)
        }
        return checkFlag
    }


    fun validation(): Boolean {
        var checkFlag = true
        if (registerNameEditFirstName.text.toString()
                .isNullOrEmpty() || registerNameEditFirstName.text.toString().isNullOrBlank()
        ) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(
                this@RegisterNameActivity,
                registerNameLayoutMain,
                msg_no_first_name
            )
        } else if (registerNameEditFirstName.text.toString().length < 3) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(
                this@RegisterNameActivity,
                registerNameLayoutMain,
                msg_first_name_3_character
            )
        } else if (registerNameEditLastName.text.toString()
                .isNullOrEmpty() || registerNameEditLastName.text.toString().isNullOrBlank()
        ) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(
                this@RegisterNameActivity,
                registerNameLayoutMain,
                msg_no_last_name
            )
        } else if (registerNameEditLastName.text.toString().length < 3) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(
                this@RegisterNameActivity,
                registerNameLayoutMain,
                msg_last_name_3_character
            )
        }  else  if (registerEmailEditEmail.text.toString().isNullOrEmpty() || registerEmailEditEmail.text.toString().isNullOrBlank()) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(
                this@RegisterNameActivity,
                registerNameLayoutMain,
                msg_no_email
            )
        } else if (!MyUtils.isEmailValid(registerEmailEditEmail.text.toString())) {
            checkFlag = false
            MyUtils.showSnackbarkotlin(this@RegisterNameActivity,registerNameLayoutMain,msg_valid_email)
        }
        return checkFlag
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        MyUtils.hideKeyboard1(this@RegisterNameActivity)
        MyUtils.userRegisterData = RequestRegisterPojo()
        finish()
    }

    // [START signin]
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RegisterNameActivity.RC_SIGN_IN)
    }

    // [START onactivityresult]
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RegisterNameActivity.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)

                isSocial = true
                setSocialData()
            } catch (e: ApiException) {
                // Google Sign In failed,
                // update UI appropriately
                isSocial = false
                setSocialData()
                Log.w(RegisterNameActivity.TAG, "Google sign in failed", e)
                // [START_EXCLUDE]
                MyUtils.showSnackbarkotlin(
                    this@RegisterNameActivity,
                    registerNameLayoutMain!!,
                    "Authentication Failed."
                )
//                updateUI(null)
                // [END_EXCLUDE]
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(RegisterNameActivity.TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        userGoogleID = acct.id!!
        // [START_EXCLUDE silent]
        showProgressDialog()
        // [END_EXCLUDE]

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(RegisterNameActivity.TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    handleSignInResult(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(RegisterNameActivity.TAG, "signInWithCredential:failure", task.exception)
                    MyUtils.showSnackbarkotlin(
                        this@RegisterNameActivity,
                        registerNameLayoutMain,
                        "Authentication Failed."
                    )
//                    Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
//                    handleSignInResult(null)
                }

                // [START_EXCLUDE]
                hideProgressDialog()
                // [END_EXCLUDE]
            }
    }
    // [END auth_with_google]

    private fun handleSignInResult(user: FirebaseUser?) {
        hideProgressDialog()
        Log.d(RegisterNameActivity.TAG, "handleSignInResult:" + user?.displayName)
//        if (result.isSuccess) {

        // Signed in successfully, show authenticated UI.
//            val acct = result.signInAccount
        Log.e("login deatils : ", "GPluse :=  " + user?.uid)

        Log.e(RegisterNameActivity.TAG, "display name: " + user!!.displayName!!)

        user_Name = user.displayName!!
        user_Email = user.email!!
// logout


        signOut()
//        full_Name_edit_text?.setText(user_Name)
        Log.e("System out", "Print displayName := " + user_Name)
//        email_edit_text?.setText(user_Email)

        MyUtils.showSnackbarkotlin(
            this@RegisterNameActivity,
            registerNameLayoutMain!!,
            "" + user_Name
        )

        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(object : OnSuccessListener<InstanceIdResult> {
                override fun onSuccess(instanceIdResult: InstanceIdResult) {
                    val instanceId = instanceIdResult.id
                    var newtoken = instanceIdResult.token
                    checkForDuplication(user_Email, user?.uid, user_Name, 0, newtoken)
                }
            })
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()
        isSocial = false
        setSocialData()
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //            updateUI(null)
        }
    }

    @VisibleForTesting
    val progressDialog by lazy {
        ProgressDialog(this)
    }

    fun showProgressDialog() {
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.isIndeterminate = true
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun checkForDuplication(
        userEmail: String,
        fbID: String,
        userName: String,
        from: Int,
        token: String
    ) {

        /**
         * 0 from = google
         * 1 from = facebook
         * 2 from = twitter
         * */

//        if (!registerNameButtonContinue.isStartAnim) registerNameButtonContinue?.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()


        /*[{
           [{
"apiType": "Android",
"apiVersion": "1.0",
"languageID": "1",
"userFirstName": "SendyLast",
"userLastName": "",
"userEmail": "testlast11@gmail.com",
"userMobile": "",
"userDeviceType": "Android",
"userFBID": "1995017177268587",
"userGoogleID": "",
"userTwiterID": "",
"userDeviceID": "100"
}]
        }]*/

        // val lID = if (PrefDb(this@RegisterNameActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb(this@RegisterNameActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) else "1"
        val lID = sessionManager?.getsetSelectedLanguage()!!

        try {

            jsonObject.put("languageID", lID)
            jsonObject.put("userFirstName", userName)
            jsonObject.put("userLastName", "")
            jsonObject.put("userEmail", userEmail)
            jsonObject.put("userMobile", "")
            jsonObject.put("userDeviceType", RestClient.apiType)

            when (from) {
                0 -> {
                    jsonObject.put("userGoogleID", fbID)
                    jsonObject.put("userFBID", "")
                    jsonObject.put("userTwiterID", "")
                }
                1 -> {
                    jsonObject.put("userGoogleID", "")
                    jsonObject.put("userFBID", fbID)
                    jsonObject.put("userTwiterID", "")
                }
                2 -> {
                    jsonObject.put("userGoogleID", "")
                    jsonObject.put("userFBID", "")
                    jsonObject.put("userTwiterID", fbID)
                }
            }
            jsonObject.put("userDeviceID", token)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val resendOTP = ViewModelProviders.of(this@RegisterNameActivity)
            .get(OnBoardingModel::class.java)
        resendOTP.apiCall(this@RegisterNameActivity, jsonArray.toString(), 10)
            .observe(this@RegisterNameActivity,
                object : Observer<List<RegisterPojo?>?> {
                    override fun onChanged(response: List<RegisterPojo?>?) {
                        if (!response.isNullOrEmpty()) {
//                            if (registerNameButtonContinue.isStartAnim) registerNameButtonContinue?.endAnimation()

                            if (response[0]?.status.equals("true", true)) {
                                //User not found

                                sessionManager?.clear_login_session()
                                storeSessionManager(response[0]?.data!!)
                                MyUtils.userRegisterData = RequestRegisterPojo()
                                Handler().postDelayed({
                                    val myIntent = Intent(
                                        this@RegisterNameActivity,
                                        RegisterNameActivity::class.java
                                    )
                                    startActivity(myIntent)
                                    overridePendingTransition(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left
                                    )
                                    finishAffinity()

                                }, 500)

//                                val intent = Intent(this@RegisterNameActivity, RegisterNameActivity::class.java)
//                                intent.putExtra(keyIsSocial, true)
//                                startActivity(intent)
//                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                            } else {
                                //No data and no internet
                                isSocial = true

                                if (MyUtils.userRegisterData != null) {
                                    MyUtils.userRegisterData.userEmail = userEmail
                                    MyUtils.userRegisterData.userFirstName = userName

                                    when (from) {
                                        0 -> MyUtils.userRegisterData.userGoogleID = fbID
                                        1 -> MyUtils.userRegisterData.userFBID = fbID
                                        2 -> MyUtils.userRegisterData.userTwiterID = fbID
                                    }

//                                    if (userName.contains(" ")){
                                    var userName1 = userName.split(" ")
//                                    }
                                    registerNameEditFirstName?.setText(userName1[0])
                                    registerNameEditLastName?.setText(userName1[1])
                                    registerEmailEditEmail?.setText(userEmail)
                                }
                            }
                        } else {
//                            if (registerNameButtonContinue.isStartAnim) registerNameButtonContinue?.endAnimation()

                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@RegisterNameActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterNameActivity,
                                    registerNameLayoutMain,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@RegisterNameActivity,
                                    registerNameLayoutMain,
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
            driverdata[0]!!.userProfilePicture!!)
    }


}