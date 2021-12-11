package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.fragment_my_profile_following_followers.*
import kotlinx.android.synthetic.main.header_back_with_text.*

/**
 * A simple [Fragment] subclass.
 */
class MyProfileFollowingFollowersFragment : Fragment() {

    var v: View? = null
    var viewPagerAdapter: ViewPagerAdapter? = null
    var headertitle = ""
    var mActivity: AppCompatActivity? = null
    var count = 0
    var title1 = ""
    var title2 = ""
    var StrFollower =""
    var StrFollowing =""

    var userData: RegisterPojo.Data? = null
    var sessionManager: SessionManager? = null
    var pos=0
    var userId=""
    var followers=""
    var following=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       if (v == null) {
            v = inflater.inflate(
                R.layout.fragment_my_profile_following_followers,
                container,
                false
            )
       }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dynamicLable()
        tvHeaderText.text = headertitle

        if(arguments!=null)
        {
            pos=arguments?.getInt("pos",0)!!
            userId=arguments?.getString("userId","")!!
            followers = arguments?.getString("followers","")!!
            following = arguments?.getString("following","")!!
        }

        imgCloseIcon.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        sessionManager = SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager?.isLoggedIn()!!) {
            if(userData==null){
                userData = sessionManager?.get_Authenticate_User()

            }

            if (!userData?.userTotalFollower.isNullOrEmpty()) {
                if (java.lang.Integer.valueOf(userData?.userTotalFollower!!) == 1 || java.lang.Integer.valueOf(
                        userData?.userTotalFollower!!
                    ) == 0
                ) {
                    if(userData?.userID.equals(userId))
                    {
                        title1 = userData?.userTotalFollower!!
                    }
                    else
                    {
                        title1=followers
                    }
                } else
                    if(userData?.userID.equals(userId))
                    {
                        title1 = userData?.userTotalFollower!!
                    }
                    else
                    {
                        title1=followers
                    }
            } else title1 = "0"

            if (!userData?.userTotalFollowing.isNullOrEmpty()) {
                if (java.lang.Integer.valueOf(userData?.userTotalFollowing!!) == 1 || java.lang.Integer.valueOf(
                        userData?.userTotalFollowing!!
                    ) == 0
                ) {
                    if(userData?.userID.equals(userId))
                    {
                        title2 = userData?.userTotalFollowing!!
                    }
                    else
                    {
                        title2=following
                    }

                } else if(userData?.userID.equals(userId))
                {
                    title2 = userData?.userTotalFollowing!!
                }
                else
                {
                    title2=following
                }
            } else title2 = "0 "
        }

//        (activity as MainActivity).showHideBottomNavigation(false)

        setupViewPager(listpager!!)

    }


    fun setupViewPager(listpager: ViewPager?) {
       // if (viewPagerAdapter == null) {
            viewPagerAdapter = ViewPagerAdapter(childFragmentManager)

            viewPagerAdapter?.addFragment(FollowesFollowingFragment(), "$title1 $StrFollower")
            viewPagerAdapter?.addFragment(FollowesFollowingFragment(), "$title2 $StrFollowing")
//            viewPagerAdapter?.addFragment(FollowesFollowingFragment(), "$title1 Follower")
//            viewPagerAdapter?.addFragment(FollowesFollowingFragment(), "$title2 Following")

     //   }
        listpager?.adapter = viewPagerAdapter
        listpager?.currentItem=pos
        tabtablayout!!.setupWithViewPager(listpager)
    }

    @SuppressLint("WrongConstant")
    inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
//            val bundle = Bundle()
//            bundle.putInt("position", position)
//            val fragment = mFragmentList[position]
//            fragment.arguments = bundle


            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("userId", userId)

            if (position == 0)
            {
                bundle.putString("type", "followerlist")
            }
            else if (position == 1)
            {
                bundle.putString("type", "followinglist")
            }
            val fragment = mFragmentList[position]
            fragment.arguments = bundle
            return fragment
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun saveState(): Parcelable? {
            return null
        }

        override fun getItemPosition(`object`: Any): Int {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return PagerAdapter.POSITION_NONE
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

    }

    private fun dynamicLable() {
//        headertitle = mActivity?.resources?.getString(R.string.my_profile)!!
        headertitle = GetDynamicStringDictionaryObjectClass?.My_Profile
        StrFollower = GetDynamicStringDictionaryObjectClass?.Followers
        StrFollowing = GetDynamicStringDictionaryObjectClass?.Following
    }

    fun setTabtitle(tabtitle: String?, pos: Int, s: String,from:String) {
         if(from.equals("unfollow",false))
         {
             title2 =  (title2?.toInt()!!-1).toString()
             userData?.userTotalFollowing=title2
             tabtablayout.getTabAt(pos)?.text = title2+ s
         }
        else
         {
             title2 =  (title2?.toInt()!!+1).toString()
             userData?.userTotalFollowing=title2
             tabtablayout.getTabAt(pos)?.text = title2+ s

         }

    }


    fun updateViewPager() {

        listpager.adapter!!.notifyDataSetChanged()
    }

}
