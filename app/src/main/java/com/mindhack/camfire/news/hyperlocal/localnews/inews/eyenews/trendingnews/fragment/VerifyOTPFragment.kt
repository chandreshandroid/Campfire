package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OnBoardingModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OtpResendAndCheckDuplicationModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
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

/**
 * A simple [Fragment] subclass.
 */
class VerifyOTPFragment : Fragment(), View.OnKeyListener {
    private var v: View? = null
    var mActivity: AppCompatActivity? = null

    var OTP_verification_validation: String = ""
    var headertitle = ""
    val keyIsSocial = "SOCIALLOGIN"
    var isSocial = false


//    var objRegister: RequestRegisterPojo? = null
    val keyRequestObj = "REQUESTOBJ"

    var sessionManager: SessionManager? = null
    var msgFailToRegister = ""
    var msgSomthingRong = ""
    var msgNoInternet = ""
    var msgFailToVerify = ""
    var msgFailToResen = ""
    var msgSuccessRegister = ""
    var userData : RegisterPojo.Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.activity_otp_verification, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dynamicLable()
        tvVerificationTitledescription?.text = GetDynamicStringDictionaryObjectClass?.Please_enter_the_4_digit_OTP_received_on_your_phone_number
        btnProceed?.progressText = GetDynamicStringDictionaryObjectClass?.Proceed_
        tvOtpresend?.text= GetDynamicStringDictionaryObjectClass?.Resend
        tvHeaderText.text = GetDynamicStringDictionaryObjectClass?.OTP_Verification

//        tvHeaderText.text = headertitle
        sessionManager = SessionManager(mActivity!!)

        if (sessionManager != null && sessionManager!!.isLoggedIn()) userData = sessionManager?.get_Authenticate_User()

        imgCloseIcon.setOnClickListener {
            (mActivity as MainActivity).onBackPressed()
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
                    val imm = mActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
                    MyUtils.hideKeyboard1(mActivity!!)


                    if (TextUtils.isEmpty(edittext_pin_1.text.toString()) && TextUtils.isEmpty(
                            edittext_pin_2.text.toString()
                        )
                        && TextUtils.isEmpty(edittext_pin_3.text.toString()) && TextUtils.isEmpty(
                            edittext_pin_4.text.toString()
                        )
                    ) {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            OTP_verification_validation,
                            ll_mainOtp
                        )

                    } else if (TextUtils.isEmpty(edittext_pin_1.text.toString())) {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            OTP_verification_validation,
                            ll_mainOtp
                        )

                    } else if (TextUtils.isEmpty(edittext_pin_2.text.toString())) {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            OTP_verification_validation,
                            ll_mainOtp
                        )


                    } else if (TextUtils.isEmpty(edittext_pin_3.text.toString())) {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            OTP_verification_validation,
                            ll_mainOtp
                        )

                    } else if (TextUtils.isEmpty(edittext_pin_4.text.toString())) {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            OTP_verification_validation,
                            ll_mainOtp
                        )

                    } else {
                        val OTPCOde =
                            (edittext_pin_1.text.toString() + edittext_pin_2.text.toString()
                                    + edittext_pin_3.text.toString() + edittext_pin_4.text.toString())

                        if (MyUtils.isInternetAvailable(mActivity!!)) {

                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_common_netdon_t_have_and_accountwork),
                                ll_mainOtp
                            )


                        }

                    }
                    return true

                }
                return false
            }

        })

        btnProceed.setOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)

            if (TextUtils.isEmpty(edittext_pin_1.text.toString()) && TextUtils.isEmpty(
                    edittext_pin_2.text.toString()
                )
                && TextUtils.isEmpty(edittext_pin_3.text.toString()) && TextUtils.isEmpty(
                    edittext_pin_4.text.toString()
                )
            ) {
                MyUtils.showSnackbar(
                    mActivity!!,
                    OTP_verification_validation,
                    ll_mainOtp
                )
            } else if (TextUtils.isEmpty(edittext_pin_1.text.toString())) {
                MyUtils.showSnackbar(
                    mActivity!!,
                    OTP_verification_validation,
                    ll_mainOtp
                )
            } else if (TextUtils.isEmpty(edittext_pin_2.text.toString())) {
                MyUtils.showSnackbar(
                    mActivity!!,
                    OTP_verification_validation,
                    ll_mainOtp
                )


            } else if (TextUtils.isEmpty(edittext_pin_3.text.toString())) {
                MyUtils.showSnackbar(
                    mActivity!!,
                    OTP_verification_validation,
                    ll_mainOtp
                )
            } else if (TextUtils.isEmpty(edittext_pin_4.text.toString())) {
                MyUtils.showSnackbar(
                    mActivity!!,
                    OTP_verification_validation,
                    ll_mainOtp
                )
            } else {
                val OTPCOde = (edittext_pin_1.text.toString() + edittext_pin_2.text.toString()
                        + edittext_pin_3.text.toString() + edittext_pin_4.text.toString())

                if (MyUtils.isInternetAvailable(mActivity!!)) {
                                                FirebaseInstanceId.getInstance().instanceId
                                .addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        return@OnCompleteListener
                                    }
//                    val newtoken = "abcg3343nsdjhskd2jl"
                                    val newtoken = task.result?.token
//                                    if (MyUtils.isInternetAvailable(this@VerificationOTPActivity)){
                    verifyOTP(newtoken!!, OTPCOde)
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

        tvOtpresend.setOnClickListener {
            if (MyUtils.isInternetAvailable(mActivity!!)) {
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



    /*override fun onBackPressed() {
        MyUtils.hideKeyboard1(mActivity!!)
        MyUtils.showMessageOKCancel(mActivity!!,
            "Are you sure want to exit ?",
            "Verification Code",
            DialogInterface.OnClickListener { dialogInterface, i ->
                MyUtils.finishActivity(
                    mActivity!!,
                    true
                )
            })
    }*/

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

        /* [{
             "loginuserID": "107",
             "userOTP": "1234",
             "languageID": "1",
             "userDeviceID": "token",
             "apiType": "Android",
             "apiVersion": "1.0"
         }]*/


       // val lID = if (PrefDb(mActivity!!).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb(mActivity!!).getString(MyUtils.SharedPreferencesenum.languageId.toString()) else "1"
        val lID=sessionManager?.getsetSelectedLanguage()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("userOTP", otp)
            jsonObject.put("languageID", lID)
            jsonObject.put("userDeviceID", newtoken)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        val verifyOTP = ViewModelProviders.of(mActivity!!)
            .get(OnBoardingModel::class.java)

        verifyOTP.apiCall(mActivity!!, jsonArray.toString(), 1)
            .observe(mActivity!!, object : Observer<List<RegisterPojo>?> {
                override fun onChanged(response: List<RegisterPojo>?) {

                    if (!response.isNullOrEmpty()) {
                        if (btnProceed.isStartAnim) btnProceed.endAnimation()

                        if (response[0].status.equals("true", true)) {

                            MyUtils.hideKeyboard1(mActivity!!)

                            if (!response[0].data.isNullOrEmpty()) {
                                if (response[0].data!!.size > 0) {
                                    sessionManager?.clear_login_session()
                                    storeSessionManager(response[0]?.data!!)
                                    (mActivity as MainActivity).getUpdateSessionManager(mActivity!!)

                                    val intent1 = Intent("MobileVerified")
                                    intent1.putExtra("Successfully", "Yes")
                                    LocalBroadcastManager.getInstance(mActivity!!)
                                        .sendBroadcast(intent1)
                                    Handler().postDelayed({

                                        (mActivity as MainActivity).navigateTo(
                                            FeedFragment(),
                                            FeedFragment::class.java.name,
                                            true
                                        )
                                    }, 500)
                                }
                            } else {
                            }
                        } else {
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    ll_mainOtp,
                                    response[0].message!!
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    ll_mainOtp,
                                    msgNoInternet
                                )
                            }
                            edittext_pin_1.setText("")
                            edittext_pin_2.setText("")
                            edittext_pin_3.setText("")
                            edittext_pin_4.setText("")
                            edittext_pin_1.requestFocus()

                        }

                    } else {
                        if (btnProceed.isStartAnim) btnProceed.endAnimation()
                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                ll_mainOtp,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                ll_mainOtp,
                                msgNoInternet
                            )
                        }
                        edittext_pin_1.setText("")
                        edittext_pin_2.setText("")
                        edittext_pin_3.setText("")
                        edittext_pin_4.setText("")
                        edittext_pin_1.requestFocus()

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
        //val lID = if (PrefDb(mActivity!!).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb(mActivity!!).getString(MyUtils.SharedPreferencesenum.languageId.toString()) else "1"
        var lID=sessionManager?.getsetSelectedLanguage()

        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("userMobile", userData?.userMobile)
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

        val resendOTP = ViewModelProviders.of(mActivity!!)
            .get(OtpResendAndCheckDuplicationModel::class.java)
        resendOTP.apiCall(mActivity!!, jsonArray.toString(), 0)
            .observe(mActivity!!,
                object : Observer<List<CommonPojo?>?> {
                    override fun onChanged(response: List<CommonPojo?>?) {
                        if (!response.isNullOrEmpty()) {
                            if (response[0]?.status.equals("true", true)) {
                                MyUtils.hideKeyboard1(mActivity!!)
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    ll_mainOtp,
                                    response[0]?.message!!
                                )
                                edittext_pin_1.setText("")
                                edittext_pin_2.setText("")
                                edittext_pin_3.setText("")
                                edittext_pin_4.setText("")
                                edittext_pin_1.requestFocus()

                            } else {
                                //No data and no internet
                                if (MyUtils.isInternetAvailable(mActivity!!)) {
                                    if (!response[0]?.message.isNullOrEmpty()) {
                                        MyUtils.showSnackbarkotlin(
                                            mActivity!!,
                                            ll_mainOtp,
                                            response[0]?.message!!
                                        )
                                    } else {
                                        MyUtils.showSnackbarkotlin(
                                            mActivity!!,
                                            ll_mainOtp,
                                            msgFailToResen
                                        )
                                    }
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        ll_mainOtp,
                                        msgNoInternet
                                    )
                                }
                                edittext_pin_1.setText("")
                                edittext_pin_2.setText("")
                                edittext_pin_3.setText("")
                                edittext_pin_4.setText("")
                                edittext_pin_1.requestFocus()

                            }
                        } else {
                            //No internet and somting went rong
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    ll_mainOtp,
                                    msgSomthingRong
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    ll_mainOtp,
                                    msgNoInternet
                                )
                            }
                            edittext_pin_1.setText("")
                            edittext_pin_2.setText("")
                            edittext_pin_3.setText("")
                            edittext_pin_4.setText("")
                            edittext_pin_1.requestFocus()

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
