package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.LoginActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OnBoardingModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException

/**
 * A simple [Fragment] subclass.
 */
class ChangePasswordFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager : SessionManager? = null
    var userData : RegisterPojo.Data? = null

    var titleOldPassword = ""
    var titleNewLanguage = ""
    var titleReEnterAccount = ""
    var titleChangePassword = ""
    var msg_password_leth_8 = ""
    var msg_no_Old_Password = ""
    var msg_no_New_password = ""
    var msg_confirm_password = ""
    var msg_confirm_valid_password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_change_password, container, false)

        v = inflater.inflate(R.layout.fragment_change_password, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        (mActivity as MainActivity).showHideBottomNavigation(false)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager!!.isLoggedIn()){
            userData = sessionManager?.get_Authenticate_User()
        }

        dynamicString()


        imgCloseIcon.setOnClickListener {
            (mActivity as  MainActivity).onBackPressed()
        }

//        tvHeaderText?.text = titleChangePassword
//        ChPasswordInputTypeOldPassword?.hint = titleChangePassword
//        ChPasswordInputTypeNewPassword?.hint = titleNewLanguage
//        ChPasswordInputTypeReEnterNewPassword?.hint = titleReEnterAccount
//        btnChangePasswordButton?.progressText = titleChangePassword

        tvHeaderText?.text = GetDynamicStringDictionaryObjectClass?.Change_Password
        ChPasswordInputTypeOldPassword?.hint = GetDynamicStringDictionaryObjectClass?.Enter_Old_Password
        ChPasswordInputTypeNewPassword?.hint = GetDynamicStringDictionaryObjectClass?.Enter_New_Password
        ChPasswordInputTypeReEnterNewPassword?.hint = GetDynamicStringDictionaryObjectClass?.Re_enter_New_Password

        btnChangePasswordButton?.progressText =  GetDynamicStringDictionaryObjectClass?.Change_Password

        btnChangePasswordButton?.setOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            if(validation() == true){
                getChangePassword()
            }
        }

        ChPasswordEditReEnterPassword?.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                    MyUtils.hideKeyboard1(mActivity!!)
                    if(validation() == true){
                        (mActivity as  MainActivity).onBackPressed()
                    }
                    return true

                }
                return false
            }

        })


    }

    private fun getChangePassword() {
        btnChangePasswordButton.startAnimation()
        MyUtils.setViewAndChildrenEnabled(rootChangePasswordLayoutMain, false)

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("userCurrentPassword", ChPasswordEditOldPassword.text.toString().trim())
            jsonObject.put("userNewPassword", ChPasswordEditNewPassword.text.toString().trim())
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val verifyOTP = ViewModelProviders.of(this@ChangePasswordFragment)
            .get(OnBoardingModel::class.java)

        verifyOTP.apiCall(mActivity!!, jsonArray.toString(), 8)
            .observe(this@ChangePasswordFragment,
                Observer<List<RegisterPojo>?> { response ->

                    if (response != null) {
                        btnChangePasswordButton.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(rootChangePasswordLayoutMain, true)
                        if (response[0].status.equals("true", true)) {
                            if(!response[0].message!!.isNullOrEmpty()){

                                MyUtils.showSnackbarkotlin(mActivity!!, rootChangePasswordLayoutMain, response[0].message!!)
                            }
                            Handler().postDelayed({
                                sessionManager?.clear_login_session()
                                val myIntent = Intent(mActivity!!, LoginActivity::class.java)
                                mActivity!!.startActivity(myIntent)
                                mActivity!!.finishAffinity()
                            }, 2000)
                        } else {
                            btnChangePasswordButton.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(rootChangePasswordLayoutMain, true)
                            MyUtils.showSnackbarkotlin(mActivity!!, rootChangePasswordLayoutMain, response[0].message!!)
                        }
                    } else {
                        btnChangePasswordButton.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(rootChangePasswordLayoutMain, true)
                        (activity as MainActivity).errorMethod()
                    }
                })

    }

    private fun dynamicString() {

        titleChangePassword = ""+mActivity!!.resources.getString(R.string.change_password)
        titleOldPassword = ""+mActivity!!.resources.getString(R.string.enter_old_password)
        titleNewLanguage = ""+mActivity!!.resources.getString(R.string.enter_new_password)
        titleReEnterAccount = ""+mActivity!!.resources.getString(R.string.re_enter_new_password)

        msg_password_leth_8 = ""+mActivity!!.resources.getString(R.string.err_password_leth_8)
        msg_no_Old_Password = ""+mActivity!!.resources.getString(R.string.please_enter_old_password)
        msg_no_New_password = ""+mActivity!!.resources.getString(R.string.please_enter_new_password)
        msg_confirm_password = ""+mActivity!!.resources.getString(R.string.please_enter_confirm_password)
        msg_confirm_valid_password = ""+mActivity!!.resources.getString(R.string.password_does_not_match)

    }

    fun validation() : Boolean {
        MyUtils.hideKeyboard1(mActivity!!)
        var checkFlag = true
        if (ChPasswordEditOldPassword.text.toString().trim().isNullOrEmpty()){
            checkFlag = false
            MyUtils.showSnackbarkotlin(mActivity!!, rootChangePasswordLayoutMain, msg_no_Old_Password)
        }else if (ChPasswordEditOldPassword.text.toString().trim().length < 6){
            checkFlag = false
            MyUtils.showSnackbarkotlin(mActivity!!, rootChangePasswordLayoutMain, msg_password_leth_8)
        }else if (ChPasswordEditNewPassword.text.toString().trim().isNullOrEmpty()){
            checkFlag = false
            MyUtils.showSnackbarkotlin(mActivity!!, rootChangePasswordLayoutMain, msg_no_New_password)
        }else if (ChPasswordEditNewPassword.text.toString().trim().length < 6){
            checkFlag = false
            MyUtils.showSnackbarkotlin(mActivity!!, rootChangePasswordLayoutMain, msg_password_leth_8)
        }else if (ChPasswordEditReEnterPassword.text.toString().trim().isNullOrEmpty()){
            checkFlag = false
            MyUtils.showSnackbarkotlin(mActivity!!, rootChangePasswordLayoutMain, msg_confirm_password)
        }else if (ChPasswordEditReEnterPassword.text.toString().trim().length < 6){
            checkFlag = false
            MyUtils.showSnackbarkotlin(mActivity!!, rootChangePasswordLayoutMain, msg_password_leth_8)
        }else if (!ChPasswordEditNewPassword.text.toString().trim().equals(ChPasswordEditReEnterPassword.text.toString().trim(), false)) {
            MyUtils.showSnackbarkotlin(mActivity!!,rootChangePasswordLayoutMain,msg_confirm_valid_password)
            checkFlag = false
        }

        return  checkFlag
    }



}
