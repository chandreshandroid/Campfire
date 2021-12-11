package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.fragment_hash_tag_list.*
import kotlinx.android.synthetic.main.fragment_my_profile_following_followers.listpager
import kotlinx.android.synthetic.main.fragment_my_profile_following_followers.tabtablayout
import kotlinx.android.synthetic.main.header_back_with_text.*


class HashTagListFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null

    var sessionManager: SessionManager? = null
    var userData: RegisterPojo.Data? = null
    var viewPagerAdapter: ViewPagerAdapter? = null
    var hashTag: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_hash_tag_list, container, false)
        }
        return v
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager?.isLoggedIn()!!) {
            userData = sessionManager?.get_Authenticate_User()
        }

        if (arguments != null) {
            hashTag = arguments?.getString("hashTag")!!
        }

        tvPostTitle?.text = GetDynamicStringDictionaryObjectClass?.Posts
        tvHeaderText.text = hashTag
        tvHasTagName.text = hashTag

        imgCloseIcon.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

//        (activity as MainActivity).showHideBottomNavigation(false)

        setupViewPager(listpager!!)


    }

    fun setupViewPager(listpager: ViewPager?) {
        if (viewPagerAdapter == null) {
            viewPagerAdapter = ViewPagerAdapter(childFragmentManager)
            viewPagerAdapter?.addFragment(HahTagPostListFragment(), ""+GetDynamicStringDictionaryObjectClass?.Recent)
            viewPagerAdapter?.addFragment(HahTagPostListFragment(), ""+GetDynamicStringDictionaryObjectClass?.Top)

//            viewPagerAdapter?.addFragment(HahTagPostListFragment(), "Recent")
//            viewPagerAdapter?.addFragment(HahTagPostListFragment(), "Top")

        }
        listpager?.adapter = viewPagerAdapter
        listpager?.currentItem = 0
        tabtablayout!!.setupWithViewPager(listpager)
    }

    inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {

            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("hashTag", hashTag)
            if (position == 0)
                bundle.putString("type", "Recent")
            else if (position == 1)
                bundle.putString("type", "Top")
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


    fun setTabtitle(countPost: String?) {
        if(countPost?.toInt()!! > 0)
        {
            ll_sub_count.visibility = View.VISIBLE
            tvHashTagCount.text = countPost
        }
        else
        {
            ll_sub_count.visibility = View.GONE

        }



    }

    fun setShow(isVisiBle: Boolean, imageUri: String) {
        if (isVisiBle) {
            ll_sub_count.visibility = View.VISIBLE
            if (!imageUri.isNullOrEmpty()) {
                img_hashtag.setImageURI(RestClient.image_base_url_post + imageUri)
            }

        } else {
            ll_sub_count.visibility = View.GONE
        }


    }


}
