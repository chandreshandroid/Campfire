package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CountArray
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_post_react_user_list.*
import kotlinx.android.synthetic.main.header_back_with_text.*


class PostReactUserListFragment : Fragment() {


    var v: View? = null
    var viewPagerAdapter: ViewPagerAdapter? = null

    var tabIconsSelected = intArrayOf(
        0,
        R.drawable.laughing_reaction_icon_small,
        R.drawable.angry_reaction_icon_small,
        R.drawable.reaction_worried_icon_on_posts_small,
        R.drawable.reaction_like_icon_small
    )

    var viewcount: ArrayList<String?>? = null
    var headertitle = ""
    var mActivity: Activity? = null
    var feedListData: TrendingFeedData? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_post_react_user_list, container, false)

        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mActivity = context as Activity
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dynamicLable()

        tvHeaderText.text = headertitle

        imgCloseIcon.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        if (arguments != null) {
            feedListData = arguments?.getSerializable("postData") as TrendingFeedData?
        }
//        (activity as MainActivity).showHideBottomNavigation(false)
        if (feedListData != null) {
            viewcount = ArrayList()
            viewcount?.clear()
            viewcount?.add(feedListData?.postAll!!)
            viewcount?.add(feedListData?.postHaha)
            viewcount?.add( feedListData?.postAngry)
            viewcount?.add(feedListData?.postSad)
            viewcount?.add(feedListData?.postLike)



        }

                setupViewPager()

        viewlisttabtablayout.tabMode = TabLayout.MODE_FIXED
    }

    private fun dynamicLable() {
//        headertitle = mActivity?.resources?.getString(R.string.people_who_reacted)!!
        headertitle = "" + GetDynamicStringDictionaryObjectClass?.People_Who_reacted
    }

    fun setupViewPager() {

        viewPagerAdapter = ViewPagerAdapter(
            childFragmentManager,
            tabIconsSelected,
            viewcount!!,
            feedListData?.postID
        )
        viewlistpager.offscreenPageLimit = 5
        viewlistpager.adapter = viewPagerAdapter

    }

    class ViewPagerAdapter(
        manager: FragmentManager,
        val tabIconsSelected: IntArray,
        val viewcount: ArrayList<String?>?,
        val postID: String?
    ) : FragmentStatePagerAdapter(manager) {
        override fun getCount(): Int {
            return tabIconsSelected.size
        }


        override fun getItem(position: Int): Fragment {
            //  val bundle = Bundle()
            /*bundle.putString("postId", postId)
            bundle.putString("albummediaView", postId)
            bundle.putString("albumId", postId)
            bundle.putString("albummediaId", postId)
            bundle.putInt("position", position)*/
            var fragment: Fragment? = null
            var bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("postId", postID)

            when (position) {
                0 -> {
                    bundle.putSerializable("type", "")
                    fragment = ReactUserListFragment()
                }
                1 -> {
                    bundle.putSerializable("type", "Laugh")
                    fragment = ReactUserListFragment()
                }
                2 -> {
                    bundle.putSerializable("type", "Angry")
                    fragment = ReactUserListFragment()
                }
                3 -> {
                    bundle.putSerializable("type", "Sad")
                    fragment = ReactUserListFragment()
                }
                4 -> {
                    bundle.putSerializable("type", "Like")
                    fragment = ReactUserListFragment()
                }
            }
            fragment!!.arguments = bundle
            return fragment!!
        }


        override fun getPageTitle(position: Int): CharSequence {
            return viewcount?.get(position)!!
        }

        override fun saveState(): Parcelable? {
            return null
        }
    }


    public fun setupTabIcons(countArray: CountArray) {
        viewlisttabtablayout.setupWithViewPager(viewlistpager)
        viewlistpager.currentItem = 0
        viewcount = ArrayList()
        viewcount?.clear()
        viewcount?.add(countArray.postAll)
        viewcount?.add(countArray.postHaha)
        viewcount?.add( countArray.postAngry)
        viewcount?.add(countArray.postSad)
        viewcount?.add(countArray.postLike)

        for (i in 0 until viewlisttabtablayout.tabCount) {
            val yourlinearlayout = LayoutInflater.from(activity).inflate(
                R.layout.customtablayout,
                null
            ) as LinearLayoutCompat
            yourlinearlayout.orientation = LinearLayoutCompat.HORIZONTAL
            val tab_text = yourlinearlayout.findViewById<View>(R.id.tabContent) as AppCompatTextView
            val ivtabIcon =
                yourlinearlayout.findViewById<View>(R.id.ivTabIcon) as AppCompatImageView
            val tabContentAll =
                yourlinearlayout.findViewById<View>(R.id.tabContentAll) as AppCompatTextView

            tabContentAll.text = "" + GetDynamicStringDictionaryObjectClass?.All

            if (tabIconsSelected[i] == 0) {
                tabContentAll.visibility = View.VISIBLE
                ivtabIcon.visibility = View.GONE
            } else {
                tabContentAll.visibility = View.GONE
                ivtabIcon.visibility = View.VISIBLE
                ivtabIcon.setImageResource(tabIconsSelected[i])

            }
            if (!viewcount?.get(i).equals("0", false)) {
                if (viewcount?.get(i)!!.toInt() > 0) {
                    tab_text.visibility = View.VISIBLE
                    tab_text.text = viewcount?.get(i)
                } else {
                    tab_text.visibility = View.GONE
                }

            } else {
                tab_text.visibility = View.GONE

            }

            viewlisttabtablayout.getTabAt(i)?.customView = yourlinearlayout


        }
        /*if (feed != null) {
            if (feed.getYoupostLiked().equalsIgnoreCase("Yes"))
                setTabTextIcon(true, viewlisttabtablayout.getTabAt(0), 0)
            if (feed.getYoupostShared().equalsIgnoreCase("Yes"))
                setTabTextIcon(true, viewlisttabtablayout.getTabAt(1), 1)
            if (feed.getYoupostViews().equalsIgnoreCase("Yes"))
                setTabTextIcon(true, viewlisttabtablayout.getTabAt(2), 2)

        }*/
    }


}
