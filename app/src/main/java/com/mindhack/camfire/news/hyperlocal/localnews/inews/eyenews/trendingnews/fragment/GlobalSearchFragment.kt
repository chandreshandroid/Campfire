package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.GlobalSearchListModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GlobalSearchPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.fragment_global_search.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.no_data_internet.btnRetry
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class GlobalSearchFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: RegisterPojo.Data? = null
    var globalSearchPostList: ArrayList<GlobalSearchPojo>? = ArrayList()
    var adapter: ViewPagerAdapter? = null
    var editGlobalSearch: AppCompatEditText? = null
    var searchKeyWord: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
   //     if (v == null) {
            v = inflater.inflate(R.layout.fragment_global_search, container, false)
  //  }
        return v
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            mActivity = context
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)

        if (sessionManager?.isLoggedIn()!! && sessionManager?.get_Authenticate_User() != null)
        {
            userData = sessionManager?.get_Authenticate_User()
        }


//        (activity as MainActivity).showHideBottomNavigation(false)

        editGlobalSearch?.setHint(""+GetDynamicStringDictionaryObjectClass?.Search_+"...")
        tvHeaderText.text = ""+GetDynamicStringDictionaryObjectClass?.Search_

        editGlobalSearch = v?.findViewById<View>(R.id.editGlobalSearch) as AppCompatEditText

        noDatafoundRelativelayout?.visibility = View.GONE
        nointernetMainRelativelayout?.visibility = View.GONE

        relativeprogressBar?.visibility = View.GONE


        imgCloseIcon.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        btnRetry.setOnClickListener {


        }
        editGlobalSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable) {
                if (editGlobalSearch?.isPressed!!) {
                    return
                }
                if (editGlobalSearch?.text.toString().isNotEmpty()) {

                    if(searchResultTabLayout?.getTabAt(0)?.isSelected!!)
                    {
                        searchKeyWord = "#"+editGlobalSearch?.text.toString().trim()
                    }
                    else
                    {
                        searchKeyWord = editGlobalSearch?.text.toString().trim()
                    }
                    getGlobalSearchList(searchKeyWord)

                } else {

                    searchKeyWord = ""
                    getGlobalSearchList(searchKeyWord)

                }
            }
        })


        editGlobalSearch?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                MyUtils.hideKeyboard1(mActivity!!)
                if (editGlobalSearch?.text.toString().trim().isNullOrEmpty()) {
                    (activity as MainActivity).showSnackBar("Please enter... ")
                } else {
                    getGlobalSearchList(editGlobalSearch?.text.toString().trim())
                }

            }
            true
        }

        searchResultTabLayout?.visibility = View.VISIBLE
        searchResultViewPager?.visibility = View.VISIBLE

        addTabs(searchResultViewPager)
        searchResultTabLayout?.setupWithViewPager(searchResultViewPager)


    }


    private fun addTabs(vPager: ViewPager?) {

        adapter = ViewPagerAdapter(mActivity?.supportFragmentManager!!)
        adapter?.addFragment(SearchResultListFragment(), ""+GetDynamicStringDictionaryObjectClass?.Tags)
        adapter?.addFragment(SearchResultListFragment(), ""+GetDynamicStringDictionaryObjectClass?.Places)
        adapter?.addFragment(SearchResultListFragment(), ""+GetDynamicStringDictionaryObjectClass?.People)
        adapter?.addFragment(SearchResultListFragment(), ""+GetDynamicStringDictionaryObjectClass?.Custom)

//        adapter?.addFragment(SearchResultListFragment(), "Tags")
//        adapter?.addFragment(SearchResultListFragment(), "Places")
//        adapter?.addFragment(SearchResultListFragment(), "People")
//        adapter?.addFragment(SearchResultListFragment(), "Custom")
        vPager?.adapter = adapter

    }

    @SuppressLint("WrongConstant")
    inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        val mFragmentList = ArrayList<Fragment>()
        val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            val bundle1 = Bundle()

            bundle1.putInt("position", position)
            bundle1.putString("searchKeyWord", editGlobalSearch?.text.toString().trim())
            bundle1.putSerializable("post", globalSearchPostList)
            /*when (position) {
                0 -> {
                    if (globalSearchPostList != null) {
                        bundle1.putSerializable("post", globalSearchPostList)
                    }

                }
                1 -> {
                    if (globalSearchPostList != null) {
                        bundle1.putSerializable("user", globalSearchPostList)
                    }
                }
                2 -> {
                    if (globalSearchPostList != null) {
                        bundle1.putSerializable("college", globalSearchPostList)
                    }
                }
            }*/
            val fragment = mFragmentList[position]
            fragment.arguments = bundle1
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

    public fun getGlobalSearchList(searchWord: String) {
        for (frag1 in mActivity?.supportFragmentManager!!.fragments) {
            if (frag1 is SearchResultListFragment) {
                (frag1 as SearchResultListFragment).noDatafoundRelativelayout.visibility = View.GONE
            }
        }
        searchResultTabLayout?.visibility = View.VISIBLE
        searchResultViewPager?.visibility = View.VISIBLE
        if (globalSearchPostList.isNullOrEmpty()) {
            relativeprogressBar?.visibility = View.VISIBLE
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            if (sessionManager?.isLoggedIn()!!) {
                jsonObject.put("loginuserID", "" + userData?.userID)
                jsonObject.put("languageID", "" + userData?.languageID)
            } else {
                jsonObject.put("loginuserID", "" + "0")
                jsonObject.put("languageID", "" + sessionManager?.getsetSelectedLanguage())
            }
            jsonObject.put("postLatitude", "" + MyUtils.currentLattitude)
            jsonObject.put("postLongitude", "" + MyUtils.currentLongitude)
            jsonObject.put("searchWord", searchWord)
            jsonObject.put("page", "0")
            jsonObject.put("pagesize", "100")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)


        var globalSearchListModel =
            ViewModelProviders.of(this).get(GlobalSearchListModel::class.java)


        globalSearchListModel.getSearchData(mActivity!!, jsonArray.toString())
            .observe(this, Observer { it ->


                //            relativeprogressBar.visibility = View.GONE

                if (it != null && it.isNotEmpty()) {
                    if (it[0].status?.equals("true")!!) {
                        relativeprogressBar?.visibility = View.GONE
                        noDatafoundRelativelayout?.visibility = View.GONE
                        globalSearchPostList?.clear()
                        globalSearchPostList?.addAll(it)
                        searchResultTabLayout?.visibility = View.VISIBLE
                        searchResultViewPager?.visibility = View.VISIBLE
                        adapter?.notifyDataSetChanged()


                    } else {
                        relativeprogressBar?.visibility = View.GONE

                        if (it != null && it.isNotEmpty()) {
                            searchResultTabLayout?.visibility = View.VISIBLE
                            searchResultViewPager?.visibility = View.VISIBLE
                            noDatafoundRelativelayout?.visibility = View.GONE
                        } else {
                            searchResultTabLayout?.visibility = View.VISIBLE
                            searchResultViewPager?.visibility = View.VISIBLE
                            noDatafoundRelativelayout?.visibility = View.VISIBLE
                            nodatafoundtextview?.text = GetDynamicStringDictionaryObjectClass?.No_Data_Found
                            for (frag1 in mActivity?.supportFragmentManager!!.fragments) {
                                if (frag1 is SearchResultListFragment) {
                                    (frag1 as SearchResultListFragment).noDatafoundRelativelayout.visibility =
                                        View.VISIBLE
                                }
                            }
                        }
                        adapter?.notifyDataSetChanged()


                    }
                } else {
                    nodatafound()

                }
            })
    }


    fun nodatafound() {
        relativeprogressBar?.visibility = View.GONE
        try {
            nointernetMainRelativelayout?.visibility = View.VISIBLE

            if (MyUtils.isInternetAvailable(mActivity!!)) {
                nointernetImageview.setImageDrawable(
                    this@GlobalSearchFragment.resources.getDrawable(
                        R.drawable.something_went_wrong
                    )
                )
                nointernettextview.text = GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong
                nointernettextview1.text = GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong

//                nointernettextview.text = (this@GlobalSearchFragment.getString(R.string.somethigwrong1))
//                nointernettextview1.text = (this.getString(R.string.somethigwrong1))
            } else {
                nointernetImageview.setImageDrawable(
                    this@GlobalSearchFragment.resources.getDrawable(
                        R.drawable.no_internet_connection
                    )
                )
                nointernettextview1.text = GetDynamicStringDictionaryObjectClass?.No_Internet_Connection
                nointernettextview.text = GetDynamicStringDictionaryObjectClass?.Please_check_your_internet_connectivity_and_try_again
//                nointernettextview1.text = (this.getString(R.string.internetmsg1))
//                nointernettextview.text = (this@GlobalSearchFragment.resources.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showHideClearTextButton(s: CharSequence?) {
        if (s.isNullOrEmpty()) {
            editGlobalSearch?.visibility = View.GONE
        } else {
            editGlobalSearch?.visibility = View.VISIBLE
        }
    }


}
