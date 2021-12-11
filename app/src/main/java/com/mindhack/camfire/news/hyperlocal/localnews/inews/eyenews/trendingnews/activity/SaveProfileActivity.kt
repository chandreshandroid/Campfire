package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import kotlinx.android.synthetic.main.activity_save_profile.registerEmailEditEmail
import kotlinx.android.synthetic.main.activity_save_profile.registerPasswordEditPassword
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.activity_save_profile.*

class SaveProfileActivity : AppCompatActivity() {

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
    var enter_userID=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_profile)
        dynamicLable()
        tvHeaderText.text = headertitle
        imgCloseIcon.visibility=View.GONE


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
                                    this@SaveProfileActivity,
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
                                    this@SaveProfileActivity,
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
                                    this@SaveProfileActivity,
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


        btnSave.setOnClickListener {
            validation()
        }



    }

    private fun validation() {
        if (registerNameEditFirstName.text.toString().isNullOrEmpty() || registerNameEditFirstName.text.toString().isNullOrBlank()) {
            MyUtils.showSnackbarkotlin(
                this@SaveProfileActivity,
                rootProfileLayout,
                msg_no_first_name
            )
        } else if (registerNameEditFirstName.text.toString().length < 3) {

            MyUtils.showSnackbarkotlin(
                this@SaveProfileActivity,
                rootProfileLayout,
                msg_first_name_3_character
            )
        } else if (registerNameEditLastName.text.toString().isNullOrEmpty() || registerNameEditLastName.text.toString().isNullOrBlank()) {

            MyUtils.showSnackbarkotlin(
                this@SaveProfileActivity,
                rootProfileLayout,
                msg_no_last_name
            )
        } else if (registerNameEditLastName.text.toString().length < 3) {

            MyUtils.showSnackbarkotlin(
                this@SaveProfileActivity,
                rootProfileLayout,
                msg_last_name_3_character
            )
        }else if (registerEmailEditEmail.text.toString().isNullOrEmpty() || registerEmailEditEmail.text.toString().isNullOrBlank()) {
            MyUtils.showSnackbarkotlin(
                this@SaveProfileActivity,
                rootProfileLayout,
                msg_no_email
            )
        } else if (!MyUtils.isEmailValid(registerEmailEditEmail.text.toString())) {
            MyUtils.showSnackbarkotlin(
                this@SaveProfileActivity,
                rootProfileLayout,
                msg_valid_email
            )
        }else if (!registerMobileEditMobile.text.toString().isNullOrEmpty() && registerMobileEditMobile.text.toString().length < 14){
            MyUtils.showSnackbarkotlin(this@SaveProfileActivity, rootProfileLayout, msg_mobile_leth)
        }else if (registerPasswordEditPassword.text.toString().isNullOrEmpty()){

            MyUtils.showSnackbarkotlin(this@SaveProfileActivity, rootProfileLayout, msg_no_password)
        }else if (registerPasswordEditPassword.text.toString().length < 6){
            MyUtils.showSnackbarkotlin(this@SaveProfileActivity, rootProfileLayout, msg_password_leth_8)
        }else if (!MyUtils.isValidPassword(registerPasswordEditPassword.text.toString().trim())){

            MyUtils.showSnackbarkotlin(this@SaveProfileActivity, rootProfileLayout, msg_valid_password)
        }else if(registerNameInputTypeUserID.text.toString().trim().isEmpty())
        {
            MyUtils.showSnackbarkotlin(this@SaveProfileActivity, rootProfileLayout, enter_userID)

        }else
        {
            MyUtils.startActivity(this@SaveProfileActivity,MainActivity::class.java,true)
        }

    }

    private fun dynamicLable() {
        headertitle = resources.getString(R.string.be_repoter)
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
        enter_userID=resources.getString(R.string.please_enter_userid)
    }
}
