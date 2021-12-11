package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.mindhack.flymyowncustomer.util.PrefDb
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.header_back_with_text.imgCloseIcon
import kotlinx.android.synthetic.main.header_back_with_text.tvHeaderText
import kotlinx.android.synthetic.main.transparent_toolbar_layout.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class SettingsFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    val TAG = ProfileDetailFragment::class.java.name
    var sessionManager : SessionManager? = null
    var userData : RegisterPojo.Data? = null

    var titleChangePassword = ""
    var titleChangeLanguage = ""
    var titleDeleteAccount = ""
    var titleNotifications = ""
    var titleHeader = ""
    var are_you_sure_want_to_delete=""
    var title_delete_account=""
    var title_cancel=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_settings, container, false)

        v = inflater.inflate(R.layout.fragment_settings, container, false)
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

        imgCloseIconTransparent.setOnClickListener {
            (mActivity as  MainActivity).onBackPressed()
        }

//        tvHeaderText?.text = titleHeader
//        settingsTextChangePassword?.text = titleChangePassword
//        settingsTextChangeLanguage?.text = titleChangeLanguage
//        settingsTextDeleteAccount?.text = titleDeleteAccount
//        settingsTextNotifications?.text = titleNotifications

        tvHeaderText?.text = GetDynamicStringDictionaryObjectClass?.Settings
        settingsTextChangePassword?.text = GetDynamicStringDictionaryObjectClass?.Change_Password
        settingsTextChangeLanguage?.text = GetDynamicStringDictionaryObjectClass?.Change_Language
        settingsTextDeleteAccount?.text = GetDynamicStringDictionaryObjectClass?.Delete_Account
        settingsTextNotifications?.text = GetDynamicStringDictionaryObjectClass?.Notifications

        settingsTextChangePassword?.setOnClickListener {
            (mActivity as MainActivity).navigateTo(ChangePasswordFragment(), ChangePasswordFragment::class.java.name, true)
        }

        settingsTextChangeLanguage?.setOnClickListener {
            (mActivity as MainActivity).navigateTo(LanguageFragment(), LanguageFragment::class.java.name, true)

        }

        settingsTextSignatureVideo?.setOnClickListener {
            (mActivity as MainActivity).navigateTo(AddSignutreVideoFragment(), AddSignutreVideoFragment::class.java.name, true)
        }

        settingsTextDeleteAccount?.setOnClickListener {
            (mActivity as MainActivity).showMessageOKCancelCustome(mActivity!!,
                are_you_sure_want_to_delete,"", title_delete_account, title_cancel,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    getUpdateSetting(userData?.languageID!!)
                })
        }

        settingsTextNotifications?.setOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)

            (activity as MainActivity).top_sheet.visibility = View.GONE
            if(sessionManager?.isLoggedIn()!!) {
                (mActivity as MainActivity).navigateTo(NotificationFragment(), NotificationFragment::class.java.name, true)
            }else{

                MyUtils.startActivity(mActivity!!,LoginActivity::class.java,true)

            }
        }

    }

    private fun dynamicString() {

        titleChangePassword = ""+GetDynamicStringDictionaryObjectClass?.Change_Password
        titleChangeLanguage = ""+GetDynamicStringDictionaryObjectClass?.Change_Language
        titleDeleteAccount = ""+GetDynamicStringDictionaryObjectClass?.Delete_Account
        titleNotifications = ""+GetDynamicStringDictionaryObjectClass?.Notifications
        titleHeader = ""+GetDynamicStringDictionaryObjectClass?.Settings
        are_you_sure_want_to_delete="Are you sure want to delete your account?"
        title_delete_account="" + GetDynamicStringDictionaryObjectClass?.Delete_Account
        title_cancel="" +GetDynamicStringDictionaryObjectClass?.Cancel_
    }


    private fun getUpdateSetting(languageId: String) {


        MyUtils.showProgressDialog(activity!!)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("languageID", languageId)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        val loginModel = ViewModelProviders.of(this@SettingsFragment).get(OnBoardingModel::class.java)
        loginModel.apiCall(mActivity!!,  jsonArray.toString(), 12)
            .observe(this@SettingsFragment,
                Observer<List<RegisterPojo>?> { loginPojo ->
                    if (loginPojo != null && loginPojo.isNotEmpty()) {

                        if (loginPojo[0].status.equals("true", true))
                        {
                            MyUtils.closeProgress()

                            PrefDb(mActivity!!).clearValue("ServiceNotProvide")

                            sessionManager?.clear_login_session()

                            val myIntent = Intent(mActivity!!, LoginActivity::class.java)
                            startActivity(myIntent)
                            (mActivity as MainActivity).finishAffinity()
                            (mActivity as MainActivity).overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )


                        } else {

                            MyUtils.closeProgress()
                            (activity as MainActivity).showSnackBar(loginPojo[0].message)

                        }

                    } else {

                        MyUtils.closeProgress()
                        (activity as MainActivity).errorMethod()
                    }
                })
    }



}
