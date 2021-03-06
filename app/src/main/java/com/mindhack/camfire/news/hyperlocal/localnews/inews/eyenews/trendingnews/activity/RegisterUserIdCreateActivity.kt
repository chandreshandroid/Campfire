package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RequestRegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.activity_ragister_userid.*

class RegisterUserIdCreateActivity : AppCompatActivity() {
    var msg_mobile_leth = ""


    var sessionManager: SessionManager? = null
    var objRegister: RequestRegisterPojo? = null
    val keyRequestObj = "REQUESTOBJ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this@RegisterUserIdCreateActivity)
        setContentView(R.layout.activity_ragister_userid)
        dynamicLable()

        if (intent.hasExtra(keyRequestObj) && intent.getSerializableExtra(keyRequestObj) != null)
            objRegister = intent.getSerializableExtra(keyRequestObj) as RequestRegisterPojo

        btnNext.setOnClickListener {
            MyUtils.hideKeyboard1(this@RegisterUserIdCreateActivity)
            objRegister?.userMentionID =registerNameInputTypeUserID.text.toString();
            val myIntent = Intent(
                this@RegisterUserIdCreateActivity,
                RegisterPasswordActivity::class.java
            )
            myIntent.putExtra(keyRequestObj, objRegister)
            startActivity(myIntent)
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }




    private fun dynamicLable() {
        msg_mobile_leth = resources.getString(R.string.please_enter_user_id)

    }
    override fun onBackPressed() {
        // super.onBackPressed()
        MyUtils.hideKeyboard1(this@RegisterUserIdCreateActivity)
        MyUtils.userRegisterData = RequestRegisterPojo()
        finish()
    }

}