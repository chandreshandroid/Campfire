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
import androidx.appcompat.widget.LinearLayoutCompat

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.LoginActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager

class HomeFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null

    var homeLayoutMain : LinearLayoutCompat? = null

    var sessionManager : SessionManager? = null
    var userData : RegisterPojo.Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false)
        return v
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            mActivity = context as AppCompatActivity
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager!!.isLoggedIn()) userData = sessionManager?.get_Authenticate_User()

//        (mActivity as MainActivity).showHideBottomNavigation(false)

        dynamicLable()
        homeLayoutMain = v?.findViewById<LinearLayoutCompat>(R.id.homeLayoutMain)

        homeLayoutMain?.setOnClickListener {

            if (sessionManager!!.isLoggedIn()){
                (mActivity as MainActivity).navigateTo(ProfileDetailFragment(), ProfileDetailFragment::class.java.name, true)
            }else{
                (mActivity as MainActivity).showMessageOKCancelCustome(mActivity!!, "To perform action please login.", "", "Login", "Cancel", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                        val myIntent = Intent(
                            (mActivity as MainActivity),
                            LoginActivity::class.java
                        )
                        startActivity(myIntent)
                        (mActivity as MainActivity).overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }
                })
            }
        }
    }

    private fun dynamicLable() {

    }
}