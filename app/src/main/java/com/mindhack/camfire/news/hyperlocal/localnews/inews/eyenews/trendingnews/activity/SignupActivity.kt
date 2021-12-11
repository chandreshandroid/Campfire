package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity


import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OnBoardingModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RequestRegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager

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
import kotlinx.android.synthetic.main.singup_activity.*
import org.json.JSONArray
import org.json.JSONObject
import android.os.Looper

class SignupActivity : AppCompatActivity() {

    var buttonFacebookLogin: LoginButton? = null
    private lateinit var callbackManager: CallbackManager
    var sessionManager: SessionManager? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    // [END declare_auth]
    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
    var user_Name = ""
    var user_Email = ""
    var fbID = ""
    var userGoogleID = ""
    var msgFailToRegister = ""
    var msgSomthingRong = ""
    var msgNoInternet = ""
    val keyIsSocial = "SOCIALLOGIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        FacebookSdk.sdkInitialize(getApplicationContext())
        callbackManager = CallbackManager.Factory.create()

        setContentView(R.layout.singup_activity)
        init()

    }
    fun init()
    {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(this@SignupActivity.resources.getString(R.string.server_client_id))
            .requestEmail()
            .build()

//            .requestIdToken(getString(R.string.default_web_client_id))

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        sessionManager = SessionManager(this@SignupActivity)



        buttonFacebookLogin?.setReadPermissions("public_profile", "email")
        buttonFacebookLogin?.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(SignupActivity.TAG, "facebook:onSuccess:$loginResult")
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
                            Log.e("System out", "Print Fblogin Name :=  " + user_Email)
                            MyUtils.showSnackbarkotlin(
                                this@SignupActivity,
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
                                    this@SignupActivity,
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
                Log.d(SignupActivity.TAG, "facebook:onCancel")
                // [START_EXCLUDE]
                Toast.makeText(
                    baseContext, "facebook:onCancel.",
                    Toast.LENGTH_SHORT
                ).show()
//                updateUI(null)
                // [END_EXCLUDE]
            }

            override fun onError(error: FacebookException) {
                Log.d(SignupActivity.TAG, "facebook:onError", error)
                // [START_EXCLUDE]
                Toast.makeText(
                    baseContext, "facebook:onError.",
                    Toast.LENGTH_SHORT
                ).show()
//                updateUI(null)
                // [END_EXCLUDE]
            }
        })


        tvSingInBtn.setOnClickListener {
            MyUtils.startActivity(this@SignupActivity, LoginActivity::class.java, false)
        }



        imgLoginSocialFacebook?.setOnClickListener {
            MyUtils.hideKeyboardFrom(this@SignupActivity, imgLoginSocialFacebook)

            buttonFacebookLogin?.performClick()
        }
        imgLoginSocialGooglePlus?.setOnClickListener {
            MyUtils.hideKeyboardFrom(this@SignupActivity, imgLoginSocialGooglePlus)
            signIn()
        }

        tvLoginSocialEmail?.setOnClickListener {
            val intent = Intent(this@SignupActivity, RegisterNameActivity::class.java)
            intent.putExtra(keyIsSocial, false)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

    }
    var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
//        super.onBackPressed()
//       MyUtils.startActivity(this@SignupActivity,MainActivity::class.java,true)
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        doubleBackToExitPressedOnce = true
        Toast.makeText(this, "click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)

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
            if (PrefDb(this@SignupActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb(
                this@SignupActivity
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

        val resendOTP = ViewModelProviders.of(this@SignupActivity)
            .get(OnBoardingModel::class.java)
        resendOTP.apiCall(this@SignupActivity, jsonArray.toString(), 10)
            .observe(this@SignupActivity,
                object : Observer<List<RegisterPojo?>?> {
                    override fun onChanged(response: List<RegisterPojo?>?) {
                        if (!response.isNullOrEmpty()) {
//                            if (registerEmailButtonContinue.isStartAnim) registerEmailButtonContinue?.endAnimation()

                            if (response[0]?.status.equals("true", true)) {
                                //User not found

                                sessionManager?.clear_login_session()
                                storeSessionManager(response[0]?.data!!)
                                MyUtils.userRegisterData = RequestRegisterPojo()
                                Handler().postDelayed({
                                    val myIntent = Intent(
                                        this@SignupActivity,
                                        MainActivity::class.java
                                    )
                                    startActivity(myIntent)
                                    overridePendingTransition(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left
                                    )
                                    finishAffinity()
                                }, 500)


                                /*if (MyUtils.isInternetAvailable(this@SignupActivity)) {
                                    val myIntent = Intent(
                                        this@SignupActivity,
                                        MainActivity::class.java
                                    )
                                    startActivity(myIntent)
                                    overridePendingTransition(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        this@SignupActivity,
                                        rootLoginMainLayout,
                                        msgNoInternet
                                    )
                                }*/

                            } else {
                                //No data and no internet


                                if (MyUtils.userRegisterData != null) {
                                    MyUtils.userRegisterData.userEmail = userEmail
                                    MyUtils.userRegisterData.userFirstName = userName

                                    when (from) {
                                        0 -> MyUtils.userRegisterData.userGoogleID = fbID
                                        1 -> MyUtils.userRegisterData.userFBID = fbID
                                        2 -> MyUtils.userRegisterData.userTwiterID = fbID
                                    }
                                }

                                val intent =
                                    Intent(this@SignupActivity, RegisterNameActivity::class.java)
                                intent.putExtra(keyIsSocial, true)
                                startActivity(intent)
                                overridePendingTransition(
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right
                                )


                            }
                        } else {
//                            if (registerEmailButtonContinue.isStartAnim) registerEmailButtonContinue?.endAnimation()

                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@SignupActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@SignupActivity,
                                    rootLoginMainLayout,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@SignupActivity,
                                    rootLoginMainLayout,
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
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // [START_EXCLUDE]
                MyUtils.showSnackbarkotlin(
                    this@SignupActivity,
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
                        this@SignupActivity,
                        rootLoginMainLayout,
                        "Authentication Failed."
                    )
                }
                hideProgressDialog()
            }
    }
    // [END auth_with_google]

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

}
