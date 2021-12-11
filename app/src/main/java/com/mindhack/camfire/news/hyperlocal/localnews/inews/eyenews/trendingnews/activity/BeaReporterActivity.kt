package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.*
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.UserIdSuggestAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.aws.AWSConfiguration
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.aws.AwsMultipleUpload
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.MediaChooseImageBottomsheet
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.GetUserNameListModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OnBoardingModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OtpResendAndCheckDuplicationModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager

import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.BuildConfig
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.activity_save_profile.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BeaReporterActivity : AppCompatActivity() {

    var headertitle = ""

    var msg_no_first_name = ""
    var msg_first_name_3_character = ""
    var msg_no_last_name = ""
    var msg_last_name_3_character = ""
    var msg_special_character = ""

    var msg_no_email = ""
    var msg_valid_email = ""
    var msg_mobile_leth = ""

    var msg_no_password = ""
    var msg_valid_password = ""
    var msg_password_leth_8 = ""
    var enter_userID = ""

    var sessionManager: SessionManager? = null
    var userData: RegisterPojo.Data? = null

    var rootProfileLayout: LinearLayoutCompat? = null
    //    var profile_imv_dp: SimpleDraweeView? = null
    var profile_imv_dp: AppCompatImageView? = null
    var profile_imv_edit: AppCompatImageView? = null
    var registerNameEditFirstName: TextInputEditText? = null
    var registerNameEditLastName: TextInputEditText? = null
    var registerEmailEditEmail: TextInputEditText? = null
    var tvVerified: AppCompatTextView? = null
    var registerMobileEditMobile: TextInputEditText? = null
    var tvVerifiedMobile: AppCompatTextView? = null
    var registerPasswordEditPassword: TextInputEditText? = null
    var registerNameEditUserID: TextInputEditText? = null
    var registerNameInputTypeUserID: AutoCompleteTextView? = null

    var title_verified = ""
    var title_verify = ""

    var msgSomthingRong = ""
    var msgNoInternet = ""
    var selectedProfilePicture = ""
    var msg_change_filed = ""

    var compressedImage: File? = null
    private val TAKE_PICTURE = 1
    private var pictureUri: Uri? = null
    private var picturePath: String? = null
    private val SELECT_PICTURE = 2
    private var timeForImageName: Long = 0
    private var imgName: String? = null
    private var actualImage: File? = null
    //    private var mediaChooseBottomSheet = MediaChooseBottomSheet()
    private var mediaChooseBottomSheet = MediaChooseImageBottomsheet()

    var uploadImageFile: File? = null
    var msg_fail_to_upload_file = ""
    var msg_fail_to_update = ""
    var msg_update_success = ""
    var msgAlreadyExist = ""
    var strMobile = ""
    var strEmail = ""
    var strOr = ""
    var msg_mentioned_already_exists = ""
    var empty_mentioned_id = ""
    var from: String = ""
    var title_add = ""

    var TRIGGER_AUTO_COMPLETE: Int = 100
    var AUTO_COMPLETE_DELAY: Long = 300
    var handler: Handler? = null
    var userIdSuggestAdapter: UserIdSuggestAdapter? = null
    var isSocial = false

    var userNameData: ArrayList<UserNameData>? = ArrayList()

    var stringList: ArrayList<String>? = ArrayList()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_profile)

        dynamicLable()

        registerNameInputTypeFirstName?.setHint("" + GetDynamicStringDictionaryObjectClass.firstName)
        registerNameInputTypeLastName?.setHint("" + GetDynamicStringDictionaryObjectClass.lastName)
        registerEmail_text_input?.text = ("" + GetDynamicStringDictionaryObjectClass.Email_Id)
        registerPasswordInputTypePassword?.setHint("" + GetDynamicStringDictionaryObjectClass.Passwrod)

        mobileno_text_input?.text = ("" + GetDynamicStringDictionaryObjectClass.Mobile_Number)
        registerPasswordEditPassword?.setHint("" + GetDynamicStringDictionaryObjectClass.Passwrod)
        registerNameInputTypeUserID_title?.text =
            ("" + GetDynamicStringDictionaryObjectClass.UserId)
        btnSave?.progressText = GetDynamicStringDictionaryObjectClass.Save

        tvHeaderText.text = headertitle
        imgCloseIcon.visibility = View.GONE
        if (intent != null) {
            if (intent.hasExtra("from")) {
                from = intent.getStringExtra("from")!!
            }
        }

        sessionManager = SessionManager(this@BeaReporterActivity)
        if (sessionManager != null && sessionManager!!.isLoggedIn())
            userData = sessionManager?.get_Authenticate_User()

        if (userData != null && (!userData?.userGoogleID.isNullOrEmpty() || !userData?.userFBID.isNullOrEmpty() ||
                    !userData?.userTwiterID.isNullOrEmpty())
        ) {
            isSocial = true
        }


        rootProfileLayout = findViewById<LinearLayoutCompat>(R.id.rootProfileLayout)
        profile_imv_dp = findViewById<AppCompatImageView>(R.id.profile_imv_dp)
        profile_imv_edit = findViewById<AppCompatImageView>(R.id.profile_imv_edit)
        registerNameEditFirstName = findViewById<TextInputEditText>(R.id.registerNameEditFirstName)
        registerNameEditLastName = findViewById<TextInputEditText>(R.id.registerNameEditLastName)
        registerEmailEditEmail = findViewById<TextInputEditText>(R.id.registerEmailEditEmail)
        tvVerified = findViewById<AppCompatTextView>(R.id.tvVerified)
        registerMobileEditMobile = findViewById<TextInputEditText>(R.id.registerMobileEditMobile)
        tvVerifiedMobile = findViewById<AppCompatTextView>(R.id.tvVerifiedMobile)
        registerPasswordEditPassword =
            findViewById<TextInputEditText>(R.id.registerPasswordEditPassword)
        // registerNameEditUserID = findViewById<TextInputEditText>(R.id.registerNameEditUserID)
        registerNameInputTypeUserID =
            findViewById<AutoCompleteTextView>(R.id.registerNameInputTypeUserID)

        btnSave.setOnClickListener {
            validation()
        }

        profile_imv_edit?.setOnClickListener {
            val currentapiVersion = Build.VERSION.SDK_INT
            if (currentapiVersion >= Build.VERSION_CODES.M) {
                getWriteStoragePermissionOther()
            } else {
                mediaChooseBottomSheet.show(
                    this@BeaReporterActivity.supportFragmentManager,
                    "BottomSheet demoFragment"
                )
            }
        }
        if (isSocial) {
            registerPasswordEditPassword?.visibility = View.GONE
            registerPasswordInputTypePassword?.visibility = View.GONE
        } else {
            registerPasswordEditPassword?.visibility = View.VISIBLE
            registerPasswordInputTypePassword?.visibility = View.VISIBLE
        }

        if (userData != null) {
            setUserData()
        }

        registerMobileEditMobile?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val text_value = registerMobileEditMobile?.text.toString()
                if (text_value.equals("+91 ", ignoreCase = true)) {
                    registerMobileEditMobile?.setText("")
                } else {
                    if (!text_value.startsWith("+91 ")) {
                        if (text_value.isNotEmpty()) {
                            registerMobileEditMobile?.setText("+91 " + s.toString())
                            Selection.setSelection(
                                registerMobileEditMobile?.text,
                                registerMobileEditMobile?.text?.length!!
                            )
                        }
                    }
                }

                if (s!!.isNotEmpty())
                {
                    if ((userData != null && !userData?.userMobile.isNullOrEmpty()) && s.toString().trim().equals(
                            ("+91 " + userData?.userMobile).trim()))
                    {
                        tvVerifiedMobile?.visibility = View.VISIBLE
                        tvVerifiedMobile?.text = title_verified
                        tvVerifiedMobile?.setTextColor(this@BeaReporterActivity.resources.getColor(R.color.green))
                        tvVerifiedMobile?.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.green_tick,
                            0,
                            0,
                            0
                        )
                    } else {
                        tvVerifiedMobile?.visibility = View.VISIBLE
                        tvVerifiedMobile?.text = title_verify
                        tvVerifiedMobile?.setTextColor(this@BeaReporterActivity.resources.getColor(R.color.colorOnError))
                        tvVerifiedMobile?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }
                else
                {
                    tvVerifiedMobile?.visibility = View.VISIBLE
                    tvVerifiedMobile?.text = title_add
                    tvVerifiedMobile?.setTextColor(this@BeaReporterActivity.resources.getColor(R.color.colorOnError))
                    tvVerifiedMobile?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        })

        registerNameEditFirstName?.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
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
                                    this@BeaReporterActivity,
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

        registerNameEditLastName?.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
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
                                    this@BeaReporterActivity,
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
                                    this@BeaReporterActivity,
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

        userIdSuggestAdapter = UserIdSuggestAdapter(
            this@BeaReporterActivity,
            android.R.layout.simple_dropdown_item_1line
        )
        registerNameInputTypeUserID?.threshold = 2
        registerNameInputTypeUserID?.setAdapter(userIdSuggestAdapter)
        registerNameInputTypeUserID?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                registerNameInputTypeUserID?.setText(userIdSuggestAdapter?.getObject(position))
                registerNameInputTypeUserID?.setSelection(registerNameInputTypeUserID?.text.toString().length)
            }
        registerNameInputTypeUserID?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.length!! > 1) {


                    //registerNameInputTypeUserID?.setText("@" + s.toString())
                    // Selection.setSelection(registerNameInputTypeUserID?.getText(), registerNameInputTypeUserID?.text?.length!!)


                    handler?.removeMessages(TRIGGER_AUTO_COMPLETE)
                    handler?.sendEmptyMessageDelayed(
                        TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY
                    )
                    /*try {
                        makeApiCall()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }*/

                }
            }

        })

        handler = Handler(Handler.Callback { msg ->
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(registerNameInputTypeUserID?.text) && registerNameInputTypeUserID?.length()!! > 2) {
                    makeApiCall()
                }
            }
            false
        })



        tvVerified?.setOnClickListener {
            if(!userData?.userEmailVerified.equals("Yes"))
            {
                if (registerEmailEditEmail?.text.toString()
                        .isNullOrEmpty() || registerEmailEditEmail?.text.toString().isNullOrBlank()
                )
                {
                    MyUtils.showSnackbarkotlin(
                        this@BeaReporterActivity,
                        rootProfileLayout!!,
                        msg_no_email
                    )
                }else if (!MyUtils.isEmailValid(registerEmailEditEmail?.text.toString())) {
                    MyUtils.showSnackbarkotlin(
                        this@BeaReporterActivity,
                        rootProfileLayout!!,
                        msg_valid_email
                    )
                }else{
                    userVerifyEmailRegister()
                }
            }

        }
    }

    private fun makeApiCall() {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            if (sessionManager?.isLoggedIn()!!) {
                jsonObject.put("languageID", userData?.languageID)

            } else {
                jsonObject.put("languageID", sessionManager?.getsetSelectedLanguage())
            }
            jsonObject.put("userFirstName", userData?.userFirstName)
            jsonObject.put("userLastName", userData?.userLastName)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }


        var generateUserNameListModel = ViewModelProviders.of(this@BeaReporterActivity).get(
            GetUserNameListModel::class.java
        )
        generateUserNameListModel.apiFunction(this@BeaReporterActivity, false, jsonArray.toString())
            .observe(this@BeaReporterActivity,
                Observer<List<UserName>> { userNamePoJos ->
                    if (userNamePoJos != null) {
                        if (userNamePoJos[0].status.equals("true", true)) {
                            if (userNamePoJos[0]?.data.isNotEmpty()) {
                                userNameData?.clear()
                                userNameData?.addAll(userNamePoJos[0].data)
                                stringList = ArrayList()
                                stringList?.clear()
                                for (i in userNamePoJos[0].data.indices) {
                                    stringList?.add(userNamePoJos[0].data[i].userName)
                                }
                                userIdSuggestAdapter?.setData(stringList!!)
                                userIdSuggestAdapter!!.notifyDataSetChanged()
                            }
                        } else {
                            if (!userNamePoJos[0]?.message.isNullOrEmpty()) {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    "" + userNamePoJos[0]?.message
                                )
                            }

                        }
                    } else {
                        if (MyUtils.isInternetAvailable(this@BeaReporterActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@BeaReporterActivity,
                                rootProfileLayout!!,
                                "" + msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@BeaReporterActivity,
                                rootProfileLayout!!,
                                "" + msgNoInternet
                            )
                        }
                    }
                })

    }


    private fun setUserData() {
        var imgURI = ""

        if (!userData?.userProfilePicture.isNullOrEmpty()) {
            imgURI = RestClient.image_base_url_users + userData?.userProfilePicture!!
            selectedProfilePicture = userData?.userProfilePicture!!
            try {
                Glide.with(this@BeaReporterActivity!!)
                    .load(RestClient.image_base_url_users + userData?.userProfilePicture!!)
                    .placeholder(R.drawable.user_profile_pic_placeholder_white)
                    .error(R.drawable.user_profile_pic_placeholder_white)
                    .into(profile_imv_dp!!)
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            profile_imv_dp?.setImageURI(Uri.parse(imgURI), this@BeaReporterActivity)
        } else {
            profile_imv_dp?.setImageResource(R.drawable.user_profile_pic_placeholder_white)
        }

        registerNameEditFirstName?.setText(if (!userData?.userFirstName.isNullOrEmpty()) userData?.userFirstName!! else "")
        registerNameEditLastName?.setText(if (!userData?.userLastName.isNullOrEmpty()) userData?.userLastName!! else "")
        registerEmailEditEmail?.setText(if (!userData?.userEmail.isNullOrEmpty()) userData?.userEmail!! else "")

        registerPasswordEditPassword?.setText(
            if (!userData?.userPassword.isNullOrEmpty())
                userData?.userPassword
            else
                ""
        )
        if (!userData?.userEmailVerified.isNullOrEmpty() && userData?.userEmailVerified.equals(
                "Yes",
                true
            )
        ) {
            tvVerified?.text = title_verified
            tvVerified?.setTextColor(this@BeaReporterActivity.resources.getColor(R.color.green))
            tvVerified?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green_tick, 0, 0, 0)
        } else {
            tvVerified?.text = title_verify
            tvVerified?.setTextColor(this@BeaReporterActivity.resources.getColor(R.color.colorOnError))
            tvVerified?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        var mobileNumber = ""
        if (!userData?.userMobile.isNullOrEmpty() && !userData?.userCountryCode.isNullOrEmpty()) {
            mobileNumber = userData?.userCountryCode + " " + userData?.userMobile
        }
        registerMobileEditMobile?.setText(if (!mobileNumber.isNullOrEmpty()) mobileNumber else "")

        if (mobileNumber.isNullOrEmpty()|| !userData?.userVerified.equals("Yes")) {
            registerMobileEditMobile?.isEnabled = true
        } else {
            registerMobileEditMobile?.isEnabled = false
        }


        if (!userData?.userVerified.isNullOrEmpty() && userData?.userVerified.equals("Yes", true)) {
            tvVerifiedMobile?.visibility = View.VISIBLE
            tvVerifiedMobile?.text = title_verified
            tvVerifiedMobile?.setTextColor(this@BeaReporterActivity.resources.getColor(R.color.green))
            tvVerifiedMobile?.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.green_tick,
                0,
                0,
                0
            )
        } else {
            if (!userData?.userVerified.isNullOrEmpty() && userData?.userVerified.equals(
                    "No",
                    true
                ) && !registerMobileEditMobile?.text.toString().isNullOrEmpty()
            ) {
                tvVerifiedMobile?.visibility = View.VISIBLE
                tvVerifiedMobile?.text = title_verify
                tvVerifiedMobile?.setTextColor(this@BeaReporterActivity.resources.getColor(R.color.black_light_dark))
                tvVerifiedMobile?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            } else {
                tvVerifiedMobile?.visibility = View.VISIBLE
                tvVerifiedMobile?.text = title_add
                tvVerifiedMobile?.setTextColor(this@BeaReporterActivity.resources.getColor(R.color.black_light_dark))
                tvVerifiedMobile?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

        if (!userData?.userMentionID.isNullOrEmpty()) {
            val userMentionedId =
                if (userData?.userMentionID!!.contains("@")) userData?.userMentionID!!.replace(
                    "@",
                    ""
                ) else userData?.userMentionID!!
            if (!userMentionedId.isNullOrEmpty()) {
                registerNameInputTypeUserID?.setText(if (!userData?.userMentionID!!.contains("@")) "@" + userMentionedId else userMentionedId)
            }
        }
    }

    private fun validation() {
        if (registerNameEditFirstName?.text.toString().isNullOrEmpty() || registerNameEditFirstName?.text.toString().isNullOrBlank()) {
            MyUtils.showSnackbarkotlin(
                this@BeaReporterActivity,
                rootProfileLayout!!,
                msg_no_first_name
            )
        } else if (registerNameEditFirstName?.text.toString().length < 3) {

            MyUtils.showSnackbarkotlin(
                this@BeaReporterActivity,
                rootProfileLayout!!,
                msg_first_name_3_character
            )
        } else if (registerNameEditLastName?.text.toString().isNullOrEmpty() || registerNameEditLastName?.text.toString().isNullOrBlank()) {

            MyUtils.showSnackbarkotlin(
                this@BeaReporterActivity,
                rootProfileLayout!!,
                msg_no_last_name
            )
        } else if (registerNameEditLastName?.text.toString().length < 3) {

            MyUtils.showSnackbarkotlin(
                this@BeaReporterActivity,
                rootProfileLayout!!,
                msg_last_name_3_character
            )
        } else if (registerEmailEditEmail?.text.toString().isNullOrEmpty() || registerEmailEditEmail?.text.toString().isNullOrBlank()) {
            MyUtils.showSnackbarkotlin(
                this@BeaReporterActivity,
                rootProfileLayout!!,
                msg_no_email
            )
        } else if (!MyUtils.isEmailValid(registerEmailEditEmail?.text.toString())) {
            MyUtils.showSnackbarkotlin(
                this@BeaReporterActivity,
                rootProfileLayout!!,
                msg_valid_email
            )
        } /*else if (registerMobileEditMobile?.text.toString().isNullOrEmpty() && registerMobileEditMobile?.text.toString().length < 14) {
            MyUtils.showSnackbarkotlin(
                this@BeaReporterActivity,
                rootProfileLayout!!,
                msg_mobile_leth
            )
        }*/ else if (registerNameInputTypeUserID?.text.toString().trim().isNullOrEmpty()) {
            MyUtils.showSnackbarkotlin(
                this@BeaReporterActivity,
                rootProfileLayout!!,
                empty_mentioned_id
            )
        }

        /*else if (registerPasswordEditPassword?.text.toString().length < 6){
            MyUtils.showSnackbarkotlin(this@BeaReporterActivity, rootProfileLayout!!, msg_password_leth_8)
        }else if (!MyUtils.isValidPassword(registerPasswordEditPassword?.text.toString().trim())){

            MyUtils.showSnackbarkotlin(this@BeaReporterActivity, rootProfileLayout!!, msg_valid_password)
        }else if(registerNameEditUserID?.text.toString().trim().isEmpty())
        {
            MyUtils.showSnackbarkotlin(this@BeaReporterActivity, rootProfileLayout!!, enter_userID)

        }*/ else {
//            if (checkForIsEditOrNot()){
            //Check

            var userMobile = ""
            var userOldMentionedID = ""
            var userNewMentionedID = ""

            if (!registerMobileEditMobile?.text.toString().trim().isNullOrEmpty() && registerMobileEditMobile?.text.toString().trim().contains(
                    "+91 "
                )
            ) {
                userMobile = registerMobileEditMobile?.text.toString().replace("+91 ", "")
            } else {
                userMobile = registerMobileEditMobile?.text.toString()
            }


            if (!registerNameInputTypeUserID?.text.toString().trim().isNullOrEmpty() && registerNameInputTypeUserID?.text.toString().trim().contains(
                    "@"
                )
            ) {
                userNewMentionedID = registerNameInputTypeUserID?.text.toString().replace("@", "")
            } else {
                userNewMentionedID = registerNameInputTypeUserID?.text.toString()
            }

            if (!userData?.userMentionID.isNullOrEmpty() && userData?.userMentionID!!.contains("@")) {
                userOldMentionedID = userData?.userMentionID!!.replace("@", "")
            } else {
                userOldMentionedID = userData?.userMentionID!!
            }


            if (!userData?.userEmail.isNullOrEmpty() && userData?.userEmail.equals(
                    registerEmailEditEmail?.text.toString()
                ) &&
                !userData?.userMobile.isNullOrEmpty() && userData?.userMobile.equals(
                    registerMobileEditMobile?.text.toString()
                ) &&
                (userOldMentionedID != null && userOldMentionedID.equals(userNewMentionedID))
            ) {
                //Procced For Update Profile
                if (uploadImageFile != null) {
                    uploadImage(uploadImageFile!!)
                } else {
                    userRegister()
                }
            } else {
//                    check For Duplication

                var checkMobileEmailDuplication = false

                if (!userData?.userEmail.isNullOrEmpty() && !userData?.userEmail.equals(
                        registerEmailEditEmail?.text.toString()
                    )
                ) {
                    checkMobileEmailDuplication = true
                }

                if (!userMobile.isNullOrEmpty() && !userData?.userMobile.equals(userMobile)) {
                    checkMobileEmailDuplication = true
                }

                if (!checkMobileEmailDuplication) {
                    //User mentioned id
                    checkForDuplicationMentionedID()
                } else {
                    checkForDuplication()
                }
            }/*else{
                MyUtils.showSnackbarkotlin(this@BeaReporterActivity, rootProfileLayout!!, msg_change_filed)
            }*/
        }
    }

    private fun checkForIsEditOrNot(): Boolean {

        var userMobile = ""
        var userOldMentionedID = ""
        var userNewMentionedID = ""

        if (!registerMobileEditMobile?.text.isNullOrEmpty() && registerMobileEditMobile?.text.toString().contains(
                "+91 "
            )
        ) {
            userMobile = registerMobileEditMobile?.text.toString().replace("+91 ", "")
        } else {
            userMobile = registerMobileEditMobile?.text.toString()
        }

        if (!registerNameInputTypeUserID?.text.isNullOrEmpty() && registerNameInputTypeUserID?.text.toString().contains(
                "@"
            )
        ) {
            userNewMentionedID = registerNameInputTypeUserID?.text.toString().replace("@", "")
        } else {
            userNewMentionedID = registerNameInputTypeUserID?.text.toString()
        }

        if (!userData?.userMentionID.isNullOrEmpty() && userData?.userMentionID!!.contains("@")) {
            userOldMentionedID = userData?.userMentionID!!.replace("@", "")
        } else {
            userOldMentionedID = userData?.userMentionID!!
        }


        return !((userData?.userProfilePicture != null && userData?.userProfilePicture.equals(
            selectedProfilePicture
        )) && (userData?.userFirstName != null && userData?.userFirstName.equals(
            registerNameEditFirstName?.text.toString()
        )) &&
                (userData?.userLastName != null && userData?.userLastName.equals(
                    registerNameEditLastName?.text.toString()
                )) &&
                (userData?.userEmail != null && userData?.userEmail.equals(registerEmailEditEmail?.text.toString())) &&
                (userData?.userMobile != null && userData?.userMobile.equals(
                    registerMobileEditMobile?.text.toString()
                )) &&
                (userOldMentionedID != null && userOldMentionedID.equals(userNewMentionedID)))

    }

    private fun dynamicLable() {
        headertitle = GetDynamicStringDictionaryObjectClass?.Be_a_Reporter
        msg_no_first_name = resources.getString(R.string.err_empty_name)
        msg_first_name_3_character = resources.getString(R.string.err_first_name_leth)
        msg_no_last_name = resources.getString(R.string.err_last_name)
        msg_last_name_3_character = resources.getString(R.string.err_last_name_leth)
        msg_special_character = resources.getString(R.string.err_special_character)

        msg_no_email = resources.getString(R.string.err_empty_email)
        msg_valid_email = resources.getString(R.string.err_valid_email)

        msg_mobile_leth = resources.getString(R.string.err_mobile_lenth)

        msg_no_password = resources.getString(R.string.err_empty_password)
        msg_password_leth_8 = resources.getString(R.string.err_password_leth_8)
        msg_valid_password = resources.getString(R.string.err_valid_password)
        enter_userID = resources.getString(R.string.please_enter_userid)

        title_verified = "" + GetDynamicStringDictionaryObjectClass?.Verified
        title_verify = "" + GetDynamicStringDictionaryObjectClass?.Verify
        title_add = "" + GetDynamicStringDictionaryObjectClass?.Add_

        msgSomthingRong = GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong
        msgNoInternet = GetDynamicStringDictionaryObjectClass?.No_Internet_Connection

        msg_fail_to_upload_file = "Failed to upload image."
        msg_fail_to_update = "Failed to update profile."
        msg_update_success = "Profile updated successfully."

        msgAlreadyExist = "Already Exists."
        strMobile = "Mobile Number "
        strEmail = "Email Id "
        strOr = "Or "
        msg_change_filed = "Please change your profile details."
        msg_mentioned_already_exists = "User Id already exists."
        empty_mentioned_id = "Please enter user id."
    }


    private fun userRegister() {

        if (!btnSave?.isStartAnim!!) btnSave?.startAnimation()

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        var userMobile = ""
        var userNewMentionedID = ""

        if (!registerMobileEditMobile?.text.isNullOrEmpty() && registerMobileEditMobile?.text.toString().contains(
                "+91 "
            )
        ) {
            userMobile = registerMobileEditMobile?.text.toString().replace("+91 ", "")
        } else {
            userMobile = registerMobileEditMobile?.text.toString()
        }

        if (!registerNameInputTypeUserID?.text.isNullOrEmpty() && registerNameInputTypeUserID?.text.toString().contains(
                "@"
            )
        ) {
            userNewMentionedID = registerNameInputTypeUserID?.text.toString().replace("@", "")
        } else {
            userNewMentionedID = registerNameInputTypeUserID?.text.toString()
        }

        try {
            jsonObject.put("loginuserID", userData?.userID)

            if (sessionManager?.isLoggedIn()!!) {
                jsonObject.put("languageID", userData?.languageID)

            } else {
                jsonObject.put("languageID", sessionManager?.getsetSelectedLanguage())

            }
            jsonObject.put("userFirstName", registerNameEditFirstName?.text.toString())
            jsonObject.put("userLastName", registerNameEditLastName?.text.toString())
            jsonObject.put("userDeviceType", userData?.userDeviceType)
            jsonObject.put("userProfilePicture", selectedProfilePicture)
            jsonObject.put("userMobile", if (userMobile.isNullOrEmpty()) "" else userMobile)
            jsonObject.put(
                "userEmail",
                if (registerEmailEditEmail?.text.toString().isNullOrEmpty()) "" else registerEmailEditEmail?.text.toString()
            )
            jsonObject.put("userCountryCode", if (userMobile.isNullOrEmpty()) "" else "+91")
            jsonObject.put("userMentionID", userNewMentionedID)
            jsonObject.put("userDeviceID", userData?.userDeviceID)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("apiType", RestClient.apiType)

            jsonArray.put(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val verifyOTP = ViewModelProviders.of(this@BeaReporterActivity)
            .get(OnBoardingModel::class.java)

        verifyOTP.apiCall(this@BeaReporterActivity, jsonArray.toString(), 9)
            .observe(this@BeaReporterActivity,
                Observer<List<RegisterPojo>?> { response ->
                    if (!response.isNullOrEmpty()) {
                        if (btnSave?.isStartAnim!!) btnSave?.endAnimation()

                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(this@BeaReporterActivity)
                            if (!response[0].data.isNullOrEmpty()) {
                                sessionManager?.clear_login_session()
                                storeSessionManager(response[0].data!!)
                            }

                            if (MyUtils.isInternetAvailable(this@BeaReporterActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    response[0].message!!
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    msg_update_success
                                )
                            }

                            Handler().postDelayed({

                                if (userData?.userMobile.equals(userMobile)) {
                                    val myIntent = Intent(
                                        this@BeaReporterActivity,
                                        MainActivity::class.java
                                    )

                                    myIntent.putExtra("from", from)
                                    startActivity(myIntent)
                                    overridePendingTransition(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left
                                    )
                                } else {
                                    val myIntent = Intent(
                                        this@BeaReporterActivity,
                                        RegisterOtpVerificationActivity::class.java
                                    )

                                    myIntent.putExtra("from", "beareporter")
                                    myIntent.putExtra("data", response[0].data!![0])

                                    startActivity(myIntent)
                                    overridePendingTransition(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left
                                    )
                                }
                            }, 2000)
                        } else {
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(this@BeaReporterActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    response[0].message!!
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    msg_fail_to_update
                                )
                            }
                        }

                    } else {
                        if (btnSave?.isStartAnim!!) btnSave?.endAnimation()
                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(this@BeaReporterActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@BeaReporterActivity,
                                rootProfileLayout!!,
                                msgSomthingRong
                            )
                        } else {

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

    @RequiresApi(Build.VERSION_CODES.M)
    fun getWriteStoragePermissionOther() {
        val permissionCheck = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getReadStoragePermissionOther()
        } else {
            ActivityCompat.requestPermissions(
                this@BeaReporterActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MyUtils.Per_REQUEST_WRITE_EXTERNAL_STORAGE_1
            )
        }
    }

    fun getReadStoragePermissionOther() {
        val permissionCheck = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getCameraPermissionOther()
        } else {
            ActivityCompat.requestPermissions(
                this@BeaReporterActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MyUtils.Per_REQUEST_READ_EXTERNAL_STORAGE_1
            )
        }
    }


    fun getCameraPermissionOther() {

        val permissionCheck = checkSelfPermission(Manifest.permission.CAMERA)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mediaChooseBottomSheet.show(
                this@BeaReporterActivity.supportFragmentManager,
                "BottomSheet demoFragment"
            )
        } else {
            ActivityCompat.requestPermissions(
                this@BeaReporterActivity,
                arrayOf(Manifest.permission.CAMERA),
                MyUtils.Per_REQUEST_CAMERA_1
            )

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MyUtils.Per_REQUEST_WRITE_EXTERNAL_STORAGE_1 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getReadStoragePermissionOther()
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Snackbar.make(
                        rootProfileLayout as View,
                        resources.getString(R.string.grant_access_camera),
                        Snackbar.LENGTH_LONG
                    ).setAction(
                        "Setting"
                    ) {
                        val i = Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        )
                        startActivity(i)
                    }.show()
                } else {
                    getWriteStoragePermissionOther()
                }
            MyUtils.Per_REQUEST_READ_EXTERNAL_STORAGE_1 ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCameraPermissionOther()
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Snackbar.make(
                        rootProfileLayout as View,
                        resources.getString(R.string.grant_access_camera),
                        Snackbar.LENGTH_LONG
                    ).setAction(
                        "Setting"
                    ) {
                        val i = Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        )
                        startActivity(i)
                    }.show()
                } else {
                    getReadStoragePermissionOther()
                }
            MyUtils.Per_REQUEST_CAMERA_1 ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mediaChooseBottomSheet.show(
                        this@BeaReporterActivity.supportFragmentManager,
                        "BottomSheet demoFragment"
                    )
                } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Snackbar.make(
                        rootProfileLayout as View,
                        resources.getString(R.string.grant_access_camera),
                        Snackbar.LENGTH_LONG
                    ).setAction(
                        "Setting"
                    ) {
                        val i = Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        )
                        startActivity(i)
                    }.show()
                }else {
                    getCameraPermissionOther()
                }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            timeForImageName = System.currentTimeMillis()
            imgName = "img$timeForImageName.jpg"
            when (requestCode) {
                TAKE_PICTURE -> if (mediaChooseBottomSheet.selectedImage() != null) {
                    pictureUri = mediaChooseBottomSheet.selectedImage()
                    picturePath = pictureUri?.path
                    Log.d("compressedImage Name", picturePath.toString())
                    var bm = BitmapFactory.decodeFile(File(picturePath).absolutePath.toString())
                    actualImage = File(picturePath)
                    profile_imv_dp?.setImageBitmap(bm)

                    compressedImage = customCompressImage(actualImage!!)
                    uploadImageFile = compressedImage!!
//                profile_imv_dp?.setImageURI(Uri.fromFile(File(picturePath)), this@BeaReporterActivity)
//                profile_imv_dp?.setImageURI(Uri.fromFile(File(picturePath)), this@BeaReporterActivity)
                }
                SELECT_PICTURE -> {
                    if (data != null && data.data != null) {
                        try {
                            pictureUri = data.data
                            picturePath = MyUtils.getPath(pictureUri, this@BeaReporterActivity)
                            actualImage = File(picturePath)
                            compressedImage = customCompressImage(actualImage!!)
                        } catch (e: java.lang.Exception) {
                            actualImage = File(
                                MyUtils.getPathFromInputStreamUri(
                                    this@BeaReporterActivity,
                                    data.data
                                )
                            )
                            compressedImage = actualImage
                        }
                        uploadImageFile = compressedImage!!
//                    profile_imv_dp?.setImageURI(Uri.fromFile(File(picturePath)), this@BeaReporterActivity)
                        profile_imv_dp?.setImageURI(pictureUri)
                    }
                }
            }
        }
    }

    fun customCompressImage(actualImage: File?): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        1111
        var compressedImage: File? = null
        try {
            compressedImage = Compressor(this@BeaReporterActivity)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ).absolutePath
                )
                .compressToFile(actualImage, "IMG_" + timeStamp + ".jpg")
        } catch (e: IOException) {
            e.printStackTrace()
//            showError(e.message)
        }
        return compressedImage
    }

    fun uploadImage(file: File) {

        if (!btnSave.isStartAnim) btnSave.startAnimation()
        val arrayImages = java.util.ArrayList<AddImages?>()
        arrayImages.clear()
        arrayImages.add(
            AddImages(
                file.toString(),
                false,
                null,
                -1,
                "",
                false,
                "0",
                null
            )
        )


        if (arrayImages?.size!! > 0) {

            val awsMultipleUpload: AwsMultipleUpload =
                object : AwsMultipleUpload(this, arrayImages, AWSConfiguration.userPath, "images") {
                    override fun onStateFileChanged(
                        id: Int,
                        state: TransferState?
                    ) {
                    }

                    override fun onProgressFileChanged(
                        id: Int,
                        bytesCurrent: Long,
                        bytesTotal: Long
                    ) {
                    }

                    override fun onErrorUploadFile(id: Int, ex: String?) {
                        btnSave.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(rootProfileLayout!!, true)
                        if (MyUtils.isInternetAvailable(this@BeaReporterActivity))
                            MyUtils.showSnackbar(
                                this@BeaReporterActivity,
                                "" + GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong,
                                rootProfileLayout!!
                            )
                        else
                            MyUtils.showSnackbar(
                                this@BeaReporterActivity,
                                "" + GetDynamicStringDictionaryObjectClass?.Please_check_your_internet_connectivity_and_try_again,
                                rootProfileLayout!!
                            )
                    }

                    override fun onSuccessUploadFile(awsFileList: ArrayList<AddImages?>) {
                        selectedProfilePicture =
                            awsFileList[0]!!.imageName!!.toString()
                        userRegister()
                    }
                }


        } else {
            selectedProfilePicture =
                arrayImages[0]!!.imageName!!.toString()
            userRegister()
        }


        /* var filePart = MultipartBody.Part.createFormData(
             "FileField", file.name, RequestBody.create(
                 MediaType.parse("image*//*"), file
            )
        )

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val call = RestClient.get()!!.uploadFile(
            filePart,
            RequestBody.create(MediaType.parse("text/plain"), "users"),
            RequestBody.create(MediaType.parse("text/plain"), jsonArray.toString())
        )

        call.enqueue(object : Callback<List<UploadFilePojo>> {
            override fun onResponse(
                call: Call<List<UploadFilePojo>>,
                response: Response<List<UploadFilePojo>>
            ) {
                if (response.body() != null) {
                    if (response.body()!![0].status.equals("true")) {
                        if (!response.body()!![0].fileName.isNullOrEmpty()) selectedProfilePicture =
                            response.body()!![0].fileName!!
                        userRegister()
                    } else {
                        if (btnSave.isStartAnim) btnSave.endAnimation()
                        try {
                            if (!response.body()!![0].message.isNullOrEmpty()) {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    response.body()!![0].message!!
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    msg_fail_to_upload_file
                                )
                            }


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    if (btnSave.isStartAnim) btnSave.endAnimation()
                    try {
                        if (MyUtils.isInternetAvailable(this@BeaReporterActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@BeaReporterActivity,
                                rootProfileLayout!!,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@BeaReporterActivity,
                                rootProfileLayout!!,
                                msgNoInternet
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<List<UploadFilePojo>>, t: Throwable) {
                // Log error here since request failed
                if (btnSave.isStartAnim) btnSave.endAnimation()

            }
        })*/
    }

    fun checkForDuplication() {

        if (!btnSave?.isStartAnim!!) btnSave?.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        var userMobile = ""
        var userEmail = ""

        if (!registerMobileEditMobile?.text.isNullOrEmpty() && registerMobileEditMobile?.text.toString().contains(
                "+91 "
            )
        ) {
            userMobile = registerMobileEditMobile?.text.toString().replace("+91 ", "")
        } else {
            userMobile = registerMobileEditMobile?.text.toString()
        }

        if (!registerEmailEditEmail?.text.isNullOrEmpty()) {
            userEmail = registerEmailEditEmail?.text.toString()
        }

        var userOldMentionedID = ""
        var userNewMentionedID = ""

        if (!registerNameInputTypeUserID?.text.isNullOrEmpty() && registerNameInputTypeUserID?.text.toString().contains(
                "@"
            )
        ) {
            userNewMentionedID = registerNameInputTypeUserID?.text.toString().replace("@", "")
        } else {
            userNewMentionedID = registerNameInputTypeUserID?.text.toString()
        }

        if (!userData?.userMentionID.isNullOrEmpty() && userData?.userMentionID!!.contains("@")) {
            userOldMentionedID = userData?.userMentionID!!.replace("@", "")
        } else {
            userOldMentionedID = userData?.userMentionID!!
        }


        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put(
                "userMobile",
                if (!userMobile.isNullOrEmpty() && !userMobile.equals(userData?.userMobile)) userMobile else ""
            )
            jsonObject.put(
                "userEmail",
                if (!userEmail.isNullOrEmpty() && !userEmail.equals(userData?.userEmail)) userEmail else ""
            )
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val resendOTP = ViewModelProviders.of(this@BeaReporterActivity)
            .get(OtpResendAndCheckDuplicationModel::class.java)
        resendOTP.apiCall(this@BeaReporterActivity, jsonArray.toString(), 1)
            .observe(this@BeaReporterActivity,
                object : Observer<List<CommonPojo?>?> {
                    override fun onChanged(response: List<CommonPojo?>?) {
                        if (!response.isNullOrEmpty()) {

                            if (response[0]?.status.equals("true", true)) {
                                if ((userOldMentionedID != null && userOldMentionedID.equals(
                                        userNewMentionedID
                                    ))
                                ) {
                                    if (uploadImageFile != null) {
                                        uploadImage(uploadImageFile!!)
                                    } else {
                                        userRegister()
                                    }
                                } else {
                                    checkForDuplicationMentionedID()
                                }
                            } else {
                                if (btnSave?.isStartAnim!!) btnSave?.endAnimation()


                                //No data and no internet
                                if (MyUtils.isInternetAvailable(this@BeaReporterActivity)) {
                                    if (!response[0]?.message.isNullOrEmpty()) {
                                        MyUtils.showSnackbarkotlin(
                                            this@BeaReporterActivity,
                                            rootProfileLayout!!,
                                            response[0]?.message!!
                                        )
                                    } else {
                                        var msg = ""
                                        if (!userEmail.isNullOrEmpty() && !userMobile.isNullOrEmpty()) {
                                            msg = strEmail + strOr + strMobile + msgAlreadyExist
                                        } else if (!userEmail.isNullOrEmpty() && userMobile.isNullOrEmpty()) {
                                            msg = strEmail + msgAlreadyExist
                                        } else if (userEmail.isNullOrEmpty() && !userMobile.isNullOrEmpty()) {
                                            msg = strMobile + msgAlreadyExist
                                        }

                                        if (!msg.isNullOrEmpty()) {
                                            MyUtils.showSnackbarkotlin(
                                                this@BeaReporterActivity,
                                                rootProfileLayout!!,
                                                msg
                                            )
                                        }
                                    }
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        this@BeaReporterActivity,
                                        rootProfileLayout!!,
                                        msgNoInternet
                                    )
                                }
                            }
                        } else {
                            if (btnSave?.isStartAnim!!) btnSave?.endAnimation()
                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@BeaReporterActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    msgNoInternet
                                )
                            }
                        }
                    }
                })
    }

    fun checkForDuplicationMentionedID() {
        if (!btnSave?.isStartAnim!!) btnSave?.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        var userNewMentionedID = ""


        if (!registerNameInputTypeUserID?.text.isNullOrEmpty() && registerNameInputTypeUserID?.text.toString().contains(
                "@"
            )
        ) {
            userNewMentionedID = registerNameInputTypeUserID?.text.toString().replace("@", "")
        } else {
            userNewMentionedID = registerNameInputTypeUserID?.text.toString()
        }

        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("userMentionID", userNewMentionedID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val resendOTP = ViewModelProviders.of(this@BeaReporterActivity)
            .get(OtpResendAndCheckDuplicationModel::class.java)
        resendOTP.apiCall(this@BeaReporterActivity, jsonArray.toString(), 2)
            .observe(this@BeaReporterActivity,
                object : Observer<List<CommonPojo?>?> {
                    override fun onChanged(response: List<CommonPojo?>?) {
                        if (!response.isNullOrEmpty()) {

                            if (response[0]?.status.equals("true", true)) {
                                if (uploadImageFile != null) {
                                    uploadImage(uploadImageFile!!)
                                } else {
                                    userRegister()
                                }
                            } else {
                                if (btnSave?.isStartAnim!!) btnSave?.endAnimation()
                                //No data and no internet
                                if (MyUtils.isInternetAvailable(this@BeaReporterActivity)) {
                                    registerNameInputTypeUserID?.setText("@")
                                    registerNameInputTypeUserID?.visibility = View.VISIBLE
                                    MyUtils.showSnackbarkotlin(
                                        this@BeaReporterActivity,
                                        rootProfileLayout!!,
                                        msg_mentioned_already_exists
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        this@BeaReporterActivity,
                                        rootProfileLayout!!,
                                        msgNoInternet
                                    )
                                }
                            }
                        } else {
                            if (btnSave?.isStartAnim!!) btnSave?.endAnimation()
                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(this@BeaReporterActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    msgNoInternet
                                )
                            }
                        }
                    }
                })
    }
    private fun userVerifyEmailRegister() {
        MyUtils.showProgress(this@BeaReporterActivity)

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        //var userMobile = ""
        //var userNewMentionedID = ""

        /*if (!registerMobileEditMobile?.text.isNullOrEmpty() && registerMobileEditMobile?.text.toString()
                .contains("+91 ")
        )
        {
            userMobile = registerMobileEditMobile?.text.toString().replace("+91 ", "")
        }
        else
        {
            userMobile = registerMobileEditMobile?.text.toString()
        }*/

        /*if (!registerNameInputTypeUserID?.text.isNullOrEmpty() && registerNameInputTypeUserID?.text.toString()
                .contains("@")
        ) {
            userNewMentionedID = registerNameInputTypeUserID?.text.toString().replace("@", "")
        } else {
            userNewMentionedID = registerNameInputTypeUserID?.text.toString()
        }*/
        try {
            jsonObject.put("languageID", userData?.languageID)
            if (sessionManager?.isLoggedIn()!!) {
                jsonObject.put("loginuserID", userData?.userID)
            }
            jsonObject.put("userFirstName", userData?.userFirstName)
            jsonObject.put("userLastName", userData?.userLastName)
            jsonObject.put("userDeviceType", userData?.userDeviceType)
            jsonObject.put("userProfilePicture", userData?.userProfilePicture)
            jsonObject.put(
                "userMobile",
                if (userData?.userMobile.isNullOrEmpty()) "" else userData?.userMobile
            )
            jsonObject.put(
                "userEmail",
                if (registerEmailEditEmail?.text.toString()
                        .isNullOrEmpty()
                ) "" else registerEmailEditEmail?.text.toString()
            )
            jsonObject.put(
                "userCountryCode",
                if (userData?.userMobile.isNullOrEmpty()) "" else "+91"
            )
            jsonObject.put("userMentionID", userData?.userMentionID)
            jsonObject.put("userDeviceID", userData?.userDeviceID)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("apiType", RestClient.apiType)

            jsonArray.put(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val verifyOTP = ViewModelProviders.of(this@BeaReporterActivity)
            .get(OnBoardingModel::class.java)

        verifyOTP.apiCall(this@BeaReporterActivity, jsonArray.toString(), 9)
            .observe(this@BeaReporterActivity, object : Observer<List<RegisterPojo>?> {
                override fun onChanged(response: List<RegisterPojo>?) {

                    if (!response.isNullOrEmpty()) {
                        MyUtils.closeProgress()
                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(this@BeaReporterActivity)
                            if (!response[0].data.isNullOrEmpty()) {
                                /*sessionManager?.clear_login_session()
                                storeSessionManager(response[0].data!!)
                                (mActivity as MainActivity).getUpdateSessionManager(mActivity!!)*/
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    resources.getString(R.string.verification_link)
                                )
                            }

                        } else {
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(this@BeaReporterActivity)) {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    response[0].message!!
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@BeaReporterActivity,
                                    rootProfileLayout!!,
                                    msgNoInternet
                                )
                            }
                        }

                    } else {
                        MyUtils.closeProgress()
                        if (MyUtils.isInternetAvailable(this@BeaReporterActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@BeaReporterActivity,
                                rootProfileLayout!!,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@BeaReporterActivity,
                                rootProfileLayout!!,
                                msgNoInternet
                            )
                        }
                    }
                }
            })
    }

}
