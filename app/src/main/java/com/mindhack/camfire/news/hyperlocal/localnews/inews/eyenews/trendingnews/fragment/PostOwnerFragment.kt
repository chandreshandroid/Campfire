package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.fragment_post_owner.*

/**
 * A simple [Fragment] subclass.
 */
class PostOwnerFragment : Fragment(), View.OnClickListener {


    private var v: View? = null
    var mActivity: AppCompatActivity? = null

    var sessionManager : SessionManager? = null
    var userData : RegisterPojo.Data? = null
    var postOwernerViewPagerAdapter:ViewPagerAdapter?=null
    var reportAccount = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_post_owner, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        (mActivity as MainActivity).toolbar_title!!.visibility = View.VISIBLE
//        (mActivity as MainActivity).toolbar_Imageview?.visibility = View.GONE
//
//        (mActivity as MainActivity).setTitle1(mActivity!!.resources.getString(R.string.my_orders))
//        (mActivity as MainActivity).drawertoggle!!.setHomeAsUpIndicator(R.drawable.menu_hamburger_icon)
//        (mActivity as MainActivity).bottomnavigation!!.setVisibility(View.VISIBLE)
//        (mActivity as MainActivity).handleSelection()
//        (mActivity as MainActivity).selectBottomNavigationOption(1)
//        (mActivity as MainActivity).setDrawerSwipe(true)

//        (mActivity as MainActivity).showHideBottomNavigation(true)

        dynamicString()

        sessionManager = SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager!!.isLoggedIn()){
            userData = sessionManager?.get_Authenticate_User()
        }

        addTabs(postOwnerViewPager)
        postOwnerTabLayout?.setupWithViewPager(postOwnerViewPager)
        setupTabIcons()


        postOwnerTextUserDotMenu.setOnClickListener(this)

    }

    private fun dynamicString() {

        reportAccount = mActivity!!.resources.getString(R.string.report_account)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.postOwnerTextUserDotMenu -> showDotMenu()
        }
    }

    fun showDotMenu(){
        //init the wrapper with style
        val wrapper = ContextThemeWrapper(mActivity!!, R.style.popmenu_style)
        //init the popup
        val popup = PopupMenu(wrapper, postOwnerTextUserDotMenu!!)

        /*  The below code in try catch is responsible to display icons*/
        if (true) {
            try {
                val fields = popup.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field.get(popup)
                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons =
                            classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //inflate menu
        popup.getMenuInflater().inflate(R.menu.popup_with_image, popup.menu)
        //implement click events

        popup.menu.getItem(0).title = reportAccount
        popup.menu.getItem(0).icon = resources.getDrawable(R.drawable.report_icon_black)
        popup.menu.getItem(1).isVisible = false
        popup.menu.getItem(2).isVisible = false

        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.edit_profile -> {
                        //Report account
                    }
                }
                return true
            }
        })
        popup.show()
    }

    private fun setupTabIcons() {
        postOwnerTabLayout?.getTabAt(0)?.setIcon(R.drawable.grid_icon_black)
        postOwnerTabLayout?.getTabAt(1)?.setIcon(R.drawable.tagged_post_icon_black)
    }

    private fun addTabs(vPager: ViewPager?) {
        if(postOwernerViewPagerAdapter==null)
        {
            postOwernerViewPagerAdapter = ViewPagerAdapter(childFragmentManager)

            /**
             * 0 for pending
             * 1 for completed
             * 2 for cancelled
             * 3 for return replace
             * */

            postOwernerViewPagerAdapter?.addFrag(PostOwnerPostListFragment(), "Post", 0)
            postOwernerViewPagerAdapter?.addFrag(PostOwnerPostListFragment(), "Tag", 1)
        }
        vPager?.adapter = postOwernerViewPagerAdapter
    }

    class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        val mFragmentList = ArrayList<Fragment>()
        val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFrag(fragment: Fragment, title: String, bundle : Int) {
            val bundle1 = Bundle()
//            bundle1.putInt(keyOrderType, bundle)
            fragment.arguments = bundle1
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList.get(position)
        }
    }


}
