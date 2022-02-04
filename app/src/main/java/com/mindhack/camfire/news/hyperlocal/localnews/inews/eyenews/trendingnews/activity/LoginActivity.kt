package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity


import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.ParseException
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.GetDynamicStringDictionaryModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OnBoardingModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GetDynamicStringDictionaryPojo1
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RequestRegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.mindhack.flymyowncustomer.util.PrefDb

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
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    var titleForgotPWD = ""
    var titleContinue_with = ""
    var titleDon_t_have_an_account = ""
    var titleregister = ""
    var btnTitleSignIn = ""
    var EmailMobilenohint = ""
    var EditPWDhint = ""
    var sessionManager: SessionManager? = null
    var msgFailToRegister = ""
    var msgSomthingRong = ""
    var msgNoInternet = ""
    var mag_failed_to_login = ""
    var user_Name = ""
    var user_Email = ""
    var fbID = ""
    var userGoogleID = ""
    var buttonFacebookLogin: LoginButton? = null
    val keyIsSocial = "SOCIALLOGIN"
    var arrayDynamicStringDictionaryList : ArrayList<GetDynamicStringDictionaryPojo1>? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    var languageId :String? = ""
    //    var buttonFacebookLogin: LoginButton? = null
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth

    // [END declare_auth]
    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        getWindow().setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        );
        MyUtils.changeStatusBarColor(this, R.color.backgrounddarkcolor, true)

        FacebookSdk.sdkInitialize(getApplicationContext())
        callbackManager = CallbackManager.Factory.create()
        setContentView(R.layout.activity_login)
        buttonFacebookLogin = findViewById(R.id.buttonFacebookLoginScreen)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(this@LoginActivity.resources.getString(R.string.server_client_id))
            .requestEmail()
            .build()

//            .requestIdToken(getString(R.string.default_web_client_id))

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        sessionManager = SessionManager(this@LoginActivity)

        loginEmailMobilenoEditFirstName?.hint = ""+GetDynamicStringDictionaryObjectClass.EmailMobilenohint
        loginEditPWD?.hint = ""+GetDynamicStringDictionaryObjectClass.EditPWDhint
        tvloginForgotPWD?.text = ""+GetDynamicStringDictionaryObjectClass.titleForgotPWD+""
        btnSignIn?.progressText = ""+GetDynamicStringDictionaryObjectClass.btnTitleSignIn

        tvloginContinueWith?.text = ""+GetDynamicStringDictionaryObjectClass.titleContinue_with
        tvloginDon_t_have_an_account?.text = ""+GetDynamicStringDictionaryObjectClass.titleDon_t_have_an_account
        tvLoginRegisterBtn?.text = ""+GetDynamicStringDictionaryObjectClass.titleregister
        msgSomthingRong =   GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong
        msgNoInternet = GetDynamicStringDictionaryObjectClass?.No_Internet_Connection
        msgFailToRegister = "Failed to login if you have any query please contact to admin."

        mag_failed_to_login = resources.getString(R.string.failed_to_login)

//        staitcLabel()

       /* loginEditPWD?.addTextChangedListener(object : TextWatcher {
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

                if (loginEditPWD?.text.toString().isNotEmpty()) {
                    try {
                        var x: Char
                        val t = IntArray(loginEditPWD?.text.toString().length)

                        for (i in 0 until loginEditPWD?.text.toString().length) {
                            x = loginEditPWD?.text.toString().toCharArray()[i]
                            val z = x.toInt()
                            t[i] = z

                            if (z in 65..90 || z in 97..122 || z in 48..57) {

                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "" + "Password should not contain space.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val ss = loginEditPWD?.text.toString().substring(
                                    0,
                                    loginEditPWD?.text.toString().length - 1
                                )
                                loginEditPWD?.setText(ss)
                                loginEditPWD?.setSelection(loginEditPWD?.text.toString().length)
                            }
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        Log.d("System out", "IndexOutOfBoundsException : " + e.toString())
                    }
                }
            }
        })*/


        imgLoginCloseIcon?.setOnClickListener {
            onBackPressed()
        }

        tvloginForgotPWD?.setOnClickListener {
            MyUtils.hideKeyboardFrom(this@LoginActivity, tvloginForgotPWD)
            MyUtils.startActivity(
                this@LoginActivity,
                ForgotPasswordActivity::class.java,
                false,
                true
            )
        }

        btnSignIn.progressButton?.includeFontPadding = true
        btnSignIn.progressButton?.setPadding(0, 20, 0, 20)
        btnSignIn?.setOnClickListener {

            MyUtils.hideKeyboardFrom(this@LoginActivity, btnSignIn)
            if (checkValidation()) {
                FirebaseInstanceId.getInstance().instanceId
                    .addOnSuccessListener { instanceIdResult ->
                        val instanceId = instanceIdResult.id
                        var newtoken = instanceIdResult.token
                        Log.e("System out", "new token:= " + instanceIdResult.token)
                        if (MyUtils.isInternetAvailable(this@LoginActivity)) {
                            userRegister(newtoken)
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@LoginActivity,
                                rootLoginMainLayout!!,
                                "" + msgNoInternet
                            )
                        }
                    }
            }

        }

        tvLoginRegisterBtn.setOnClickListener {

            val intent = Intent(this@LoginActivity, RegisterNameActivity::class.java)
            intent.putExtra(keyIsSocial, false)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

//            MyUtils.startActivity(this@LoginActivity,RegisterNameActivity::class.java,false,true)
        }

        imgLoginSocialGooglePlus?.setOnClickListener {
            MyUtils.hideKeyboardFrom(this@LoginActivity, imgLoginSocialGooglePlus)
            signIn()
        }

        buttonFacebookLogin?.setReadPermissions("public_profile", "email","user_birthday");
        buttonFacebookLogin?.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
//                handleFacebookAccessToken(loginResult.accessToken)
                LoginManager.getInstance().logOut()

                val request =
                    GraphRequest.newMeRequest(loginResult.accessToken) { jsonObj, response ->

                        try {
                            //here is the data that you want
                            user_Email = jsonObj.opt("email").toString()
                            fbID = jsonObj.opt("id").toString()
                            user_Name = jsonObj.opt("name").toString()
                            userGoogleID = ""
                            Log.e("System out", "Print Fblogin Name :=  " + response)
                            Log.e("System out", "Print Fblogin Name :=  " + user_Email)
                            Log.e("System out", "Print Fblogin Name :=  " + fbID)
                            Log.e("System out", "Print Fblogin Name :=  " + user_Name)
                            MyUtils.showSnackbarkotlin(
                                this@LoginActivity,
                                rootLoginMainLayout!!,
                                "" + user_Name
                            )
                            if (!fbID.isNullOrEmpty() && !user_Email.isNullOrEmpty()) {
                                FirebaseInstanceId.getInstance().instanceId
                                    .addOnSuccessListener(object :
                                        OnSuccessListener<InstanceIdResult> {
                                        override fun onSuccess(instanceIdResult: InstanceIdResult) {
                                            val instanceId = instanceIdResult.id
                                            var newtoken = instanceIdResult.token
                                            checkForDuplication(
                                                user_Email,
                                                fbID,
                                                user_Name,
                                                1,
                                                newtoken
                                            )

                                        }
                                    })
                            } else {

                                MyUtils.showMessageOK(
                                    this@LoginActivity,
                                    "your privacy setting in facebook is not allowing us to acccess your data, please try another account or change your privacy settings for CamFire.",
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            dialog?.dismiss()
                                        }
                                    })

                            }
                            Log.d("FBLOGIN_JSON_RES", jsonObj.toString())
                            if (jsonObj.has("id")) {
//                            handleSignInResultFacebook(jsonObj)
                                Log.e("FBLOGIN_Success", jsonObj.toString())
                            } else {
                                Log.e("FBLOGIN_FAILD", jsonObj.toString())
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
//                        dismissDialogLogin()
                        }
                    }

                val parameters = Bundle()
                parameters.putString("fields", "name,email,id,picture.type(large)")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // [START_EXCLUDE]
                Toast.makeText(
                    baseContext, "facebook:onCancel.",
                    Toast.LENGTH_SHORT
                ).show()
//                updateUI(null)
                // [END_EXCLUDE]
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError : "+ error.message)
                // [START_EXCLUDE]
                Toast.makeText(
                    baseContext, "facebook:onError.",
                    Toast.LENGTH_SHORT
                ).show()
//                updateUI(null)
                // [END_EXCLUDE]
            }
        })
        // [END initialize_fblogin]

        imgLoginSocialFacebook?.setOnClickListener {
            MyUtils.hideKeyboardFrom(this@LoginActivity, imgLoginSocialFacebook)

            buttonFacebookLogin?.performClick()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
//       MyUtils.startActivity(this@LoginActivity,SignupActivity::class.java,true)
    }

    private fun checkValidation(): Boolean {

        var checkFlag = true
        val loginType =
            TextUtils.isDigitsOnly(loginEmailMobilenoEditFirstName?.text.toString().trim())

        if (loginEmailMobilenoEditFirstName?.text.toString().trim().isEmpty()) {
            MyUtils.showSnackbarkotlin(
                this@LoginActivity,
                rootLoginMainLayout!!,
                this@LoginActivity.resources.getString(R.string.err_empty_email_mobile)
            )
            checkFlag = false
        } else if (loginType && (loginEmailMobilenoEditFirstName?.text.toString().trim().length < 10)) {
            MyUtils.showSnackbarkotlin(
                this@LoginActivity,
                rootLoginMainLayout!!,
                this@LoginActivity.resources.getString(R.string.err_enter_reg_mo)
            )
            checkFlag = false
        } else if (!loginType && !MyUtils.isEmailValid(loginEmailMobilenoEditFirstName.text.toString().trim())) {
            MyUtils.showSnackbarkotlin(
                this@LoginActivity,
                rootLoginMainLayout!!,
                this@LoginActivity.resources.getString(R.string.err_valid_email)
            )
            checkFlag = false
        } else if (loginEditPWD?.text.toString().trim().isEmpty()) {
            MyUtils.showSnackbarkotlin(
                this@LoginActivity,
                rootLoginMainLayout!!,
                this@LoginActivity.resources.getString(R.string.err_empty_pwd)
            )
            checkFlag = false
        } else if (loginEditPWD?.text!!.toString().trim().length < 6) {
            MyUtils.showSnackbarkotlin(
                this@LoginActivity,
                rootLoginMainLayout!!,
                this@LoginActivity.resources.getString(R.string.err_pws_lth_8)
            )
            checkFlag = false
        } else {
            //
        }
        return checkFlag
    }

    private fun staitcLabel() {

        titleForgotPWD = this@LoginActivity!!.resources.getString(R.string.forgot_password)
        titleContinue_with = this@LoginActivity!!.resources.getString(R.string.or_continue_with)
        titleDon_t_have_an_account =
            this@LoginActivity!!.resources.getString(R.string.don_t_have_an_account)
        titleregister = this@LoginActivity!!.resources.getString(R.string.register)
        btnTitleSignIn = this@LoginActivity!!.resources.getString(R.string.sign_in)
        EmailMobilenohint = this@LoginActivity!!.resources.getString(R.string.enter_email_mobile)
        EditPWDhint = this@LoginActivity!!.resources.getString(R.string.enter_password)




    }

    private fun userRegister(newtoken: String) {
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        btnSignIn?.startAnimation()
        MyUtils.setViewAndChildrenEnabled(rootLoginMainLayout, false)

        try {
            val loginType = TextUtils.isDigitsOnly(loginEmailMobilenoEditFirstName?.text.toString().trim())
            jsonObject.put("userMobile", loginEmailMobilenoEditFirstName?.text.toString().trim())
            jsonObject.put("userPassword", loginEditPWD?.text.toString().trim())
          //  jsonObject.put("languageID", PrefDb(this@LoginActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()))
            jsonObject.put("languageID",sessionManager?.getsetSelectedLanguage())
            jsonObject.put("userDeviceID", "" + newtoken)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

            Log.e("System Out", "Login Request $jsonArray")
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val verifyOTP = ViewModelProviders.of(this@LoginActivity).get(OnBoardingModel::class.java)

        verifyOTP.apiCall(this@LoginActivity, jsonArray.toString(), 3)
            .observe(this@LoginActivity, object : Observer<List<RegisterPojo>?> {
                override fun onChanged(response: List<RegisterPojo>?) {

                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(this@LoginActivity)
                            btnSignIn?.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(rootLoginMainLayout, true)

                            if (!response[0].data.isNullOrEmpty()) {
                                sessionManager?.clear_login_session()
                                storeSessionManager(response[0]?.data!!)


                                languageId = response[0]?.data!![0]?.languageID!!

                                getGetDynamicStringDictionaryPojoList()

                            } else {
                                btnSignIn?.endAnimation()
                                MyUtils.setViewAndChildrenEnabled(rootLoginMainLayout, true)
                                if (!response[0].message!!.isNullOrEmpty()) {
                                    MyUtils.showSnackbarkotlin(
                                        this@LoginActivity,
                                        rootLoginMainLayout,
                                        response[0].message!!
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        this@LoginActivity,
                                        rootLoginMainLayout,
                                        mag_failed_to_login
                                    )
                                }

                            }
                        } else {
                            btnSignIn?.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(rootLoginMainLayout, true)
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(this@LoginActivity)) {
                                if (!response[0].message!!.isNullOrEmpty()) {
                                    MyUtils.showSnackbarkotlin(
                                        this@LoginActivity,
                                        rootLoginMainLayout,
                                        response[0].message!!
                                    )
                                }

                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@LoginActivity,
                                    rootLoginMainLayout,
                                    msgNoInternet
                                )
                            }
                        }

                    } else {
                        btnSignIn?.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(rootLoginMainLayout, true)
                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(this@LoginActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@LoginActivity,
                                rootLoginMainLayout,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@LoginActivity,
                                rootLoginMainLayout,
                                msgNoInternet
                            )
                        }
                    }
                }
            })
    }

    private fun getGetDynamicStringDictionaryPojoList() {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
//            /language/list-labels

            /*[{
                "loginuserID": "0",
                "languageID": "2",
                "langLabelApp": "User App",
                "apiType": "Android",
                "apiVersion": "1.0"
            }]*/

            jsonObject.put("loginuserID", "0")
            jsonObject.put("languageID", languageId.toString())
            jsonObject.put("langLabelApp", "User App")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

            Log.e("System out", "Register api call := " + jsonArray.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val getDynamicStringDictionaryModel = ViewModelProviders.of(this@LoginActivity).get(
            GetDynamicStringDictionaryModel::class.java)
        getDynamicStringDictionaryModel.getDynamicStringDictionaryresponseJson(this@LoginActivity, true, jsonArray.toString())
            .observe(this@LoginActivity, object :
                Observer<List<GetDynamicStringDictionaryPojo1>> {
                override fun onChanged(response: List<GetDynamicStringDictionaryPojo1>?) {

                    if (response != null && response.size > 0) {
                        if (response[0].status.equals("true")) {
                            arrayDynamicStringDictionaryList = ArrayList<GetDynamicStringDictionaryPojo1>()
                            arrayDynamicStringDictionaryList?.clear()
                            arrayDynamicStringDictionaryList?.addAll(response)

                            storePrefDb(arrayDynamicStringDictionaryList!!)
                            saveLanguage()

                        } else {
                            arrayDynamicStringDictionaryList?.clear()

                            //No data -- No internet
                            if (MyUtils.isInternetAvailable(this@LoginActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@LoginActivity,
                                    rootLoginMainLayout!!,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@LoginActivity,
                                    rootLoginMainLayout!!,
                                    msgNoInternet
                                )
                            }
                        }
                    } else {
                        arrayDynamicStringDictionaryList?.clear()

                        //No internet -- Somthing went rong
                        if (MyUtils.isInternetAvailable(this@LoginActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@LoginActivity,
                                rootLoginMainLayout!!,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@LoginActivity,
                                rootLoginMainLayout!!,
                                msgNoInternet
                            )
                        }
                    }

                }

            })
    }


    private fun storePrefDb(logindata: List<GetDynamicStringDictionaryPojo1>) {
        val gson = Gson()
        var json = gson.toJson(logindata[0])
//        Log.e("System out","Print DynamicStringDictionary :==  "+ json)
        PrefDb((this@LoginActivity)).setDynamicStringDictionary(json)
    }

    private fun saveLanguage() {
        PrefDb((this@LoginActivity)).putString(MyUtils.SharedPreferencesenum.languageId.toString(), languageId!!)
        var updateGetDynamicStringDictionary= GetDynamicStringDictionaryObjectClass.getInstance(this@LoginActivity)

        Handler().postDelayed({
            val myIntent = Intent(
                this@LoginActivity,
                MainActivity::class.java
            )
            startActivity(myIntent)
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            finishAffinity()
        }, 500)

    }

    private fun storeSessionManager(driverdata: List<RegisterPojo.Data?>) {
        val gson = Gson()
        val json = gson.toJson(driverdata[0]!!)
        Log.w("SagfarSg",""+driverdata[0]!!.userFirstName!!+" "+driverdata[0]!!.userLastName!!)
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

    // [START signin]
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // [START onactivityresult]
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                e.printStackTrace()
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e.message)
                // [START_EXCLUDE]
                MyUtils.showSnackbarkotlin(
                    this@LoginActivity,
                    rootLoginMainLayout!!,
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
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        userGoogleID = acct.id!!
        // [START_EXCLUDE silent]
        showProgressDialog()
        // [END_EXCLUDE]
        signOut()
        Log.d(TAG, "handleSignInResult:" + acct?.displayName)
        Log.e("login deatils : ", "GPluse :=  " + acct?.id)
        Log.e(TAG, "display name: " + acct!!.displayName!!)
        user_Name = acct.displayName!!
        user_Email = acct.email!!

// logout
        signOut()
        FirebaseInstanceId.getInstance().instanceId
                .addOnSuccessListener(object : OnSuccessListener<InstanceIdResult> {
                    override fun onSuccess(instanceIdResult: InstanceIdResult) {
                        val instanceId = instanceIdResult.id
                        var newtoken = instanceIdResult.token
                        checkForDuplication(user_Email, acct?.id, user_Name, 0, newtoken)
                    }
                })


    }
    // [END auth_with_google]
  /*  // [START auth_with_google]
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        userGoogleID = acct.id!!
        // [START_EXCLUDE silent]
        showProgressDialog()
        // [END_EXCLUDE]

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser

                        handleSignInResult(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        MyUtils.showSnackbarkotlin(
                                this@LoginActivity,
                                rootLoginMainLayout,
                                "Authentication Failed."
                        )
                    }
                    hideProgressDialog()
                }
    }
    // [END auth_with_google]*/

    private fun handleSignInResult(user: FirebaseUser?) {
        hideProgressDialog()
        Log.d(TAG, "handleSignInResult:" + user?.displayName)
        Log.e("login deatils : ", "GPluse :=  " + user?.uid)
        Log.e(TAG, "display name: " + user!!.displayName!!)
        user_Name = user.displayName!!
        user_Email = user.email!!

// logout
        signOut()
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


    fun checkForDuplication(userEmail: String, fbID: String, userName: String, from: Int, token : String) {
        /**
         * 0 from = google
         * 1 from = facebook
         * 2 from = twitter
         * */
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        /*val lID =
            if (PrefDb(this@LoginActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb(
                this@LoginActivity
            ).getString(MyUtils.SharedPreferencesenum.languageId.toString()) else "1"*/
        var lID=sessionManager?.getsetSelectedLanguage()

        try {

            jsonObject.put("languageID", lID)
            jsonObject.put("userFirstName", userName)
            jsonObject.put("userLastName", "")
            jsonObject.put("userEmail", userEmail)
            jsonObject.put("userMobile", "")
            jsonObject.put("userDeviceType", RestClient.apiType)

            when(from){
                0 ->{
                    jsonObject.put("userGoogleID", fbID)
                    jsonObject.put("userFBID", "")
                    jsonObject.put("userTwiterID", "")
                }
                1 ->{
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

        val resendOTP = ViewModelProviders.of(this@LoginActivity)
            .get(OnBoardingModel::class.java)
        resendOTP.apiCall(this@LoginActivity, jsonArray.toString(), 10)
            .observe(this@LoginActivity,
                object : Observer<List<RegisterPojo?>?> {
                    override fun onChanged(response: List<RegisterPojo?>?) {
                        hideProgressDialog()
                        if (!response.isNullOrEmpty()) {
//                            if (registerEmailButtonContinue.isStartAnim) registerEmailButtonContinue?.endAnimation()

                            if (response[0]?.status.equals("true", true)) {
                                //User not found

                                sessionManager?.clear_login_session()
                                storeSessionManager(response[0]?.data!!)
                                MyUtils.userRegisterData = RequestRegisterPojo()
                                Handler().postDelayed({
                                    val myIntent = Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    )
                                    startActivity(myIntent)
                                    overridePendingTransition(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left
                                    )
                                    finishAffinity()
                                }, 500)


                                /*if (MyUtils.isInternetAvailable(this@LoginActivity)) {
                                    val myIntent = Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    )
                                    startActivity(myIntent)
                                    overridePendingTransition(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        this@LoginActivity,
                                        rootLoginMainLayout,
                                        msgNoInternet
                                    )
                                }*/

                            } else {
                                //No data and no internet


                                if (MyUtils.userRegisterData != null) {
                                    MyUtils.userRegisterData.userEmail = userEmail
                                    MyUtils.userRegisterData.userFirstName = userName.split(" ")[0]
                                    MyUtils.userRegisterData.userLastName = if(userName.split(" ").size > 1) userName.split(" ")[1] else ""
                                    when (from) {
                                        0 -> MyUtils.userRegisterData.userGoogleID = fbID
                                        1 -> MyUtils.userRegisterData.userFBID = fbID
                                        2 -> MyUtils.userRegisterData.userTwiterID = fbID
                                    }
                                }

                                val intent =
                                    Intent(this@LoginActivity, RegisterNameActivity::class.java)
                                intent.putExtra(keyIsSocial, true)
                                startActivity(intent)
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )


                            }
                        } else {
//                            if (registerEmailButtonContinue.isStartAnim) registerEmailButtonContinue?.endAnimation()

                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@LoginActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@LoginActivity,
                                    rootLoginMainLayout,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@LoginActivity,
                                    rootLoginMainLayout,
                                    msgNoInternet
                                )
                            }
                        }
                    }
                })
    }
}
