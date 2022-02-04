package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.button.MaterialButton
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.Service.NotifyInterface
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.Service.NotifyProfileInterface
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.LoginActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.PostReportListAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.ReasonList
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.flymyowncustomer.util.PrefDb
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_back_with_text_new.*
import kotlinx.android.synthetic.main.new_header_icon_text.*
//import kotlinx.android.synthetic.main.header_icon_text.*
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.profile_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException


/**
 * A simple [Fragment] subclass.
 */
class ProfileDetailFragment : Fragment(), View.OnClickListener {


    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    val TAG = ProfileDetailFragment::class.java.name

    var profileDetailTabLayout: TabLayout? = null
    var profileDetailViewPager: ViewPager? = null

    var sessionManager: SessionManager? = null
    var userData: RegisterPojo.Data? = null
    var userDataOtherdata: RegisterPojo.Data? = null

    var title_edit_image = ""
    var title_settings = ""
    var title_signature_Video = "Signature Video"
    var title_sign_out = ""

    var are_you_sure_logout = ""
    var title_cancel = ""
    var title_logout = ""
    var viewPagerAdapter: ViewPagerAdapter? = null
    var postReportListAdapter: PostReportListAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    var userID: String = ""
    var relativeprogressBar: RelativeLayout? = null
    var noDatafoundRelativelayout: RelativeLayout? = null
    var nointernetMainRelativelayout: RelativeLayout? = null
    var mainLayout: LinearLayout? = null
    var reportReasonData: ArrayList<ReasonList.Data>? = ArrayList()
    var notifyInterface: NotifyInterface? = null
    var notifyProfileInterface: NotifyProfileInterface? = null
    var feedData: TrendingFeedData? = null
    var btnFollow: AppCompatButton? = null
    var profileDetailTextViewPoints: AppCompatTextView? = null
    var btnFollowing: MaterialButton? = null
    private val tabIcons = intArrayOf(
        R.drawable.user_post_list_icon_unselect,
        R.drawable.user_list_icon_unselect,
        R.drawable.user_comment_icon_unselect
    )
    private val tabIconsProfile = intArrayOf(
        R.drawable.user_post_list_icon_unselect,
        R.drawable.user_list_icon_unselect
    )
    private val tabIconsSelected = intArrayOf(
        R.drawable.user_post_list_icon,
        R.drawable.user_list_icon,
        R.drawable.user_comment_icon
    )
    private val tabIconsSelectedProfile = intArrayOf(
        R.drawable.user_post_list_icon,
        R.drawable.user_list_icon,
        R.drawable.user_comment_icon
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.profile_detail, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        try {
            notifyInterface = activity as NotifyInterface
            notifyProfileInterface = activity as NotifyProfileInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement TextClicked"
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        relativeprogressBar = v?.findViewById(R.id.relativeprogressBar) as RelativeLayout
        noDatafoundRelativelayout =
            v?.findViewById(R.id.noDatafoundRelativelayout) as RelativeLayout
        nointernetMainRelativelayout =
            v?.findViewById(R.id.nointernetMainRelativelayout) as RelativeLayout
        mainLayout = v?.findViewById(R.id.mainLayout) as LinearLayout
        btnFollow = v?.findViewById(R.id.btnFollow) as AppCompatButton
        btnFollowing = v?.findViewById(R.id.btnFollowing) as MaterialButton
        profileDetailTextViewPoints =
            v?.findViewById(R.id.profileDetailTextViewPoints) as AppCompatTextView

        profileDetailTextViewPoints?.text = GetDynamicStringDictionaryObjectClass?.View_Points
        profileDetailTextUserPost?.text = GetDynamicStringDictionaryObjectClass?.Posts

        profileDetailTextUserFollowers?.text = GetDynamicStringDictionaryObjectClass?.Followers
        profileDetailTextUserFollowing?.text = GetDynamicStringDictionaryObjectClass?.Following

        btnFollow?.text = "" + GetDynamicStringDictionaryObjectClass?.Follow
        btnFollowing?.text = "" + GetDynamicStringDictionaryObjectClass?.Following

//        imgCloseIcon1.setOnClickListener {
//            MyUtils.hideKeyboard1(mActivity!!)
//
//            (activity as MainActivity).top_sheet.visibility = View.GONE
//            /* if (sessionManager?.isLoggedIn()!!) {*/
//            var globalSearchFragment = GlobalSearchFragment()
//            (activity as MainActivity).navigateTo(
//                globalSearchFragment,
//                globalSearchFragment::class.java.name,
//                true
//            )
//            /*} else {
//                MyUtils.startActivity(mActivity!!, ProfileDetailFragment::class.java, true)
//            }*/
//
//        }

        imgCloseIcon2.setOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)

            (activity as MainActivity).top_sheet.visibility = View.GONE
            if (sessionManager?.isLoggedIn()!!) {
                (mActivity as MainActivity).navigateTo(
                    NotificationFragment(),
                    NotificationFragment::class.java.name,
                    true
                )
            } else {

                MyUtils.startActivity(mActivity!!, LoginActivity::class.java, true)

            }

        }

        sessionManager = SessionManager(mActivity!!)

        if (sessionManager != null && sessionManager!!.isLoggedIn()) {
            userData = sessionManager?.get_Authenticate_User()

//            setUserData()
        }

        if (arguments != null) {
            userID = arguments?.getString("userID")!!

            feedData = arguments?.getSerializable("feedData") as TrendingFeedData?


            Log.e("System out", "fs Userid : " + userID)
//            (mActivity as MainActivity).showHideBottomNavigation(false)
            rootHeaderLayout1.visibility = View.GONE
            rootHeaderLayout.visibility = View.GONE

            tvHeaderText.text = arguments?.getString("userName")!!

            imgCloseIcon11.setOnClickListener {
                (mActivity as MainActivity).onBackPressed()
            }
            profileDetailTextViewPoints?.visibility = View.GONE
            if (userID.equals(userData?.userID, false)) {
                btnFollow?.visibility = View.GONE
                btnFollowing?.visibility = View.GONE

            } else {
                btnFollow?.visibility = View.GONE
                btnFollowing?.visibility = View.GONE


            }

        } else {


            if (userData != null) {
                userID = userData?.userID!!
            }
//            rootHeaderLayout.visibility = View.VISIBLE
//            rootHeaderLayout.visibility = View.GONE
            rootHeaderLayout1.visibility = View.GONE
//            (activity as MainActivity).updateNavigationBarState(R.id.menu_profile)
//            (mActivity as MainActivity).showHideBottomNavigation(true)
            if (MyUtils.locationCityName.isNullOrEmpty()) {
                val currentapiVersion = Build.VERSION.SDK_INT
                if (currentapiVersion >= Build.VERSION_CODES.M) {
                    (activity as MainActivity).permissionLocation()
                }
            } else {
//                tv_selectLocation.text = MyUtils.locationCityName

            }

            profileDetailTextViewPoints?.visibility = View.VISIBLE
            btnFollow?.visibility = View.GONE
            btnFollowing?.visibility = View.GONE

        }



        if (!userData?.userID.equals(userID, false)) {
            profileDetailTextViewPoints?.visibility = View.GONE
            if (userData?.isYouFollowing.equals("Yes", false)) {

                btnFollowing?.visibility = View.VISIBLE
                btnFollow?.visibility = View.GONE
            } else {
                btnFollowing?.visibility = View.GONE
                btnFollow?.visibility = View.VISIBLE
            }
        } else if (userData?.userID.equals(userID, false)) {
            btnFollowing?.visibility = View.GONE
            btnFollow?.visibility = View.GONE
            profileDetailTextViewPoints?.visibility = View.VISIBLE
        }
        imgCloseIcon.setOnClickListener {
            (mActivity as MainActivity).mDrawerLayout.openDrawer(Gravity.LEFT)
        }


        profileDetailTextViewPoints?.setOnClickListener {
            var viewPointsFragment = ViewPointsFragment()
            Bundle().apply {
                putSerializable("userData", userDataOtherdata)
                viewPointsFragment.arguments = this
            }
            (activity as MainActivity).navigateTo(
                viewPointsFragment,
                viewPointsFragment::class.java.name,
                true
            )
        }

        val width = profileDetailLayoutMain.width
        val height = MyUtils.getViewHeight(profileDetailLayoutMain)

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, height, 0, 0)

        (activity as MainActivity).coordinatorLocation.layoutParams = params

//        tv_selectLocation?.setOnClickListener {
//
//            (activity as MainActivity).openTopSheet(width, height)
//        }

        btnFollow?.setOnClickListener {
            getUserFollow("follow", userID)

        }
        btnFollowing?.setOnClickListener {
            getUserFollow("unfollow", userID)
        }

//        dynamicString()

        profileDetailTabLayout = v?.findViewById<TabLayout>(R.id.profileDetailTabLayout)
        profileDetailViewPager = v?.findViewById<ViewPager>(R.id.profileDetailViewPager)

        relativeprogressBar?.visibility = View.VISIBLE
        nointernetMainRelativelayout?.visibility = View.GONE
        noDatafoundRelativelayout?.visibility = View.GONE
        mainLayout?.visibility = View.GONE

        /*addTabs(profileDetailViewPager)
        profileDetailTabLayout?.setupWithViewPager(profileDetailViewPager)
        setupTabIcons()*/
       // if (userDataOtherdata == null) {
            getOtherUserProfileData()

//      /*  } else {
//            setUserData1(userDataOtherdata!!)
//
//            setupTabIcons()
//            mainLayout?.visibility = View.VISIBLE
//            viewPagerAdapter?.notifyDataSetChanged()
//
//        }*/

        profileDetailTextUserDotMenu.setOnClickListener(this)
        ll_following.setOnClickListener(this)
        ll_followers.setOnClickListener(this)

        profileDetailTextUserClap.setOnClickListener {
            /*if (!userData?.userID.equals(userDataOtherdata?.userID)) {
                val action: String
                if (userDataOtherdata?.IsClappeddByYou.equals("Yes")) {

                    action = "UnClapPost"
                    setPostClap(action, 0)
                } else {

                    action = "ClapPost"
                    setPostClap(action, 0)
                }
            }*/
        }

        btnRetry?.text = GetDynamicStringDictionaryObjectClass?.Retry
        btnRetry.setOnClickListener {
            getOtherUserProfileData()
        }

    }

    fun setPostClap(
        action: String,
        pos: Int
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("userID", userDataOtherdata?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        MyUtils.showProgressDialog(mActivity!!)
        jsonArray.put(jsonObject)
        val postLike: PostClapModel =
            ViewModelProviders.of((this@ProfileDetailFragment)).get(
                PostClapModel::class.java
            )
        postLike.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@ProfileDetailFragment,
            Observer { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos!![0].status.equals("true", false)) {
                        MyUtils.closeProgress()
                        if (action.equals("ClapPost", false)) {

                            userDataOtherdata?.IsClappeddByYou = "Yes"
                            userDataOtherdata?.userClapCount =
                                (userDataOtherdata?.userClapCount?.toInt()!! + 1).toString()

                            setUserClapData1(userDataOtherdata!!)
                            if (notifyInterface != null) {
                                feedData?.IsClappeddByYou = "Yes"
                                feedData?.userClapCount = userDataOtherdata?.userClapCount
                                notifyInterface!!.notifyData(
                                    feedData,
                                    false,
                                    false,
                                    feedData?.postComment
                                )
                            }

                        } else if (action.equals("UnClapPost", false)) {

                            userDataOtherdata?.IsClappeddByYou = "No"
                            if (userDataOtherdata?.userClapCount?.toInt() == 0) {
                                userDataOtherdata?.userClapCount = (0).toString()
                            } else {

                                userDataOtherdata?.userClapCount =
                                    (userDataOtherdata?.userClapCount?.toInt()!! - 1).toString()
                            }
                            if (notifyInterface != null) {
                                feedData?.IsClappeddByYou = "No"
                                feedData?.userClapCount = userDataOtherdata?.userClapCount
                                notifyInterface?.notifyData(
                                    feedData,
                                    false,
                                    false,
                                    feedData?.postComment
                                )
                            }
                            setUserClapData1(userDataOtherdata!!)

                        }

                    } else {
                        MyUtils.closeProgress()
                        MyUtils.showSnackbarkotlin(
                            mActivity!!,
                            profileDetailLayoutMain, "" + postlikePojos[0].message
                        )
                    }
                }
            })
    }

    fun setUserClapData1(userData1: RegisterPojo.Data) {
        if (userData1?.IsClappeddByYou.equals("Yes")) {
            profileDetailTextUserClap.setImageResource(R.drawable.clap_icon_filled_enabled)
        } else {
            profileDetailTextUserClap.setImageResource(R.drawable.clap_icon_border_disabled)
        }
        if (!userData1?.userClapCount.isNullOrEmpty() && !userData1?.userClapCount.equals(
                "0", false
            )
        ) {
            profileDetailTextUserClapCount.text =
                "" + MyUtils.compressLikeCount(Integer.parseInt(userData1?.userClapCount!!))
            profileDetailTextUserClapCount.visibility = View.VISIBLE

        } else {
            profileDetailTextUserClapCount.visibility = View.GONE
        }
    }

    fun setUserData1(userData1: RegisterPojo.Data?) {

        Log.e("System out", "Userid : " + userID)
        Log.e("System out", "userData?.userID : " + userData1?.userID)
        Log.e("System out", "Original userData?.userID : " + userData?.userID)

        profileDetailTextUserName.text = userData1?.userFirstName + " " + userData1?.userLastName
        profileDetailTextUserTagName.text = "@${userData1?.userMentionID}"
        if (!userData1?.userProfilePicture.isNullOrEmpty())
            profile_imv_dp.setImageURI(RestClient.image_base_url_users + userData1?.userProfilePicture)
        profileDetailTextUserBio.text = userData1?.userBio
        profileDetailTextUserPostCount.text = userData1?.userTotalPost
        profileDetailTextUserFollowersCount.text = userData1?.userTotalFollower
        profileDetailTextUserFollowingCount.text = userData1?.userTotalFollowing
        if (userData1?.badgeName.isNullOrEmpty()) {
            ll_level.visibility = View.VISIBLE
        } else {
            ll_level.visibility = View.VISIBLE
            bronz_Imageview.setImageURI(RestClient.image_base_Level + userData1?.badgeIcon)
            profileDetailTextUserLevel.text = userData1?.badgeName + " Level"
        }

        tvHeaderText.text = userData1?.userFirstName + " " + userData1?.userLastName

        if (userData1?.IsClappeddByYou.equals("Yes")) {
            profileDetailTextUserClap.setImageResource(R.drawable.clap_icon_filled_enabled)
        } else {
            profileDetailTextUserClap.setImageResource(R.drawable.clap_icon_border_disabled)
        }
        if (!userData1?.userClapCount.isNullOrEmpty() && !userData1?.userClapCount.equals(
                "0", false
            )
        ) {
            profileDetailTextUserClapCount.text =
                "" + MyUtils.compressLikeCount(Integer.parseInt(userData1?.userClapCount!!))
            profileDetailTextUserClapCount.visibility = View.VISIBLE

        } else {
            profileDetailTextUserClapCount.visibility = View.GONE
        }


        if (!userData?.userID.equals(userData1?.userID, false)) {
            Log.e("System out", "Printjjj :  " + userData1?.isYouFollowing)
            profileDetailTextViewPoints?.visibility = View.GONE
            if (userData1?.isYouFollowing.equals("Yes", false)) {

                btnFollowing?.visibility = View.VISIBLE
                btnFollow?.visibility = View.GONE
            } else {
                btnFollowing?.visibility = View.GONE
                btnFollow?.visibility = View.VISIBLE
            }
        } else if (userData?.userID.equals(userData1?.userID, false)) {
            btnFollowing?.visibility = View.GONE
            btnFollow?.visibility = View.GONE
            profileDetailTextViewPoints?.visibility = View.VISIBLE
        }

    }

    fun setupViewPager(userData1: RegisterPojo.Data?) {

        if (userData?.userID.equals(userData1?.userID, false)) {
            viewPagerAdapter = ViewPagerAdapter(
                mActivity!!.supportFragmentManager,
                tabIcons, profileDetailTabLayout!!, userData?.userID!!, userData1?.userID!!
            )
            profileDetailViewPager?.adapter = viewPagerAdapter
        } else {
            if (viewPagerAdapter == null) {
                viewPagerAdapter = ViewPagerAdapter(
                    mActivity!!.supportFragmentManager,
                    tabIconsProfile,
                    profileDetailTabLayout!!,
                    userData?.userID!!,
                    userData1?.userID!!
                )
                profileDetailViewPager?.adapter = viewPagerAdapter
            }

        }
        profileDetailViewPager?.offscreenPageLimit = 6
        profileDetailViewPager?.adapter = viewPagerAdapter

    }

    private fun dynamicString() {
        title_edit_image = "Edit Profile"
        title_settings = "Settings"
        title_sign_out = "Sign Out"
        are_you_sure_logout = "Are you sure you want to logout?"
        title_cancel = "Cancel"
        title_logout = "Logout"
        title_signature_Video = "Signature Video"
    }

    fun showDotMenu() {
        //init the wrapper with style
        val wrapper = ContextThemeWrapper(mActivity!!, R.style.popmenu_style)
        //init the popup
        val popup = PopupMenu(wrapper, profileDetailTextUserDotMenu!!,Gravity.END)

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
                            classPopupHelper.getMethod(
                                "setForceShowIcon",
                                Boolean::class.javaPrimitiveType
                            )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //inflate menu
        popup.menuInflater.inflate(R.menu.popup_with_image, popup.menu)
        //implement click events
//        popup.menu.getItem(0).title = title_edit_image
//        popup.menu.getItem(1).title = title_settings
//        popup.menu.getItem(2).title = title_signature_Video
//        popup.menu.getItem(3).title = title_sign_out

        popup.menu.getItem(0).title = GetDynamicStringDictionaryObjectClass?.Edit_Profile
        popup.menu.getItem(1).title = GetDynamicStringDictionaryObjectClass?.Settings
        popup.menu.getItem(2).title = GetDynamicStringDictionaryObjectClass?.Signature_Video
        popup.menu.getItem(3).title = GetDynamicStringDictionaryObjectClass?.Sign_Out

        if (userData?.userID!!.equals(userDataOtherdata?.userID)) {
            popup.menu.getItem(0).isVisible = true
            popup.menu.getItem(1).isVisible = true
            popup.menu.getItem(2).isVisible = true
            popup.menu.getItem(3).isVisible = true
        } else {
            popup.menu.getItem(0).isVisible = true
            popup.menu.getItem(1).isVisible = false
            popup.menu.getItem(2).isVisible = false
            popup.menu.getItem(3).isVisible = false

            popup.menu.getItem(0).title = GetDynamicStringDictionaryObjectClass?.Report_Account
            popup.menu.getItem(0).icon =
                mActivity!!.resources.getDrawable(R.drawable.report_icon_black)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_profile -> {
                    if (userData?.userID!!.equals(userDataOtherdata?.userID)) {
                        (mActivity as MainActivity).navigateTo(
                            SaveProfileFragment(),
                            SaveProfileFragment::class.java.name,
                            true
                        )
                    } else {
                        if (!reportReasonData.isNullOrEmpty()) {
                            showDilaogPostReport(userDataOtherdata?.userID!!, reportReasonData!!)
                        } else {
                            getReasonList(userDataOtherdata?.userID)
                        }
                    }
                }
                R.id.settings -> {
                    //showDilaogPostReport()
                    (activity as MainActivity).navigateTo(
                        SettingsFragment(),
                        SettingsFragment::class.java.name,
                        true
                    )
                }
                R.id.signature_Video -> {
                    (activity as MainActivity).navigateTo(
                        AddSignutreVideoFragment(),
                        AddSignutreVideoFragment::class.java.name,
                        true
                    )

                }
                R.id.signout -> {
                    logOut()
                }
            }
            true
        }
        popup.show()
    }

    private fun getReasonList(userID: String?) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val apiCall = ViewModelProviders.of(this@ProfileDetailFragment).get(ReasonModel::class.java)
        apiCall.apiFunction(mActivity!!, true, jsonArray.toString())
            .observe(this@ProfileDetailFragment,
                Observer<List<ReasonList>?>
                { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            //data found
                            reportReasonData?.clear()
                            reportReasonData?.addAll(response[0].data)
                            showDilaogPostReport(userID, response[0].data)

                        } else {
                            //data not find
                            (activity as MainActivity).showSnackBar(response[0].message)
                        }
                    } else {
                        //No internet and somthing rong
                        (activity as MainActivity).errorMethod()
                    }
                })
    }


    fun getOtherUserProfileData() {
        noDatafoundRelativelayout?.visibility = View.GONE
        nointernetMainRelativelayout?.visibility = View.GONE

        if (MyUtils.internetConnectionCheck(mActivity!!)) {
//            [{
//                "languageID": "1",
//                "loginuserID": "230",
//                "otherUserID": "230",
//                "apiType": "Android",
//                "apiVersion": "1.0"
//            }]
            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            try {

                jsonObject.put("languageID", "" + sessionManager?.getsetSelectedLanguage())
                jsonObject.put("loginuserID", "" + userData?.userID)
                if (userID.equals(userData?.userID)) {
                    jsonObject.put("otherUserID", "" + userData?.userID)
                } else {
                    jsonObject.put("otherUserID", "" + userID)
                }
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)
                jsonArray.put(jsonObject)
//                Log.d(TAG, "Language api call := " + jsonArray.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            var getLanguageModel = ViewModelProviders.of(this@ProfileDetailFragment).get(
                GetOtherUserProfileModel::class.java
            )
            getLanguageModel.apiFunction(mActivity!!, false, jsonArray.toString())
                .observe(this@ProfileDetailFragment,
                    Observer<List<RegisterPojo>> { languagePoJos ->
                        if (languagePoJos != null) {

                            if (languagePoJos[0].status.equals("true", true)) {

                                if (languagePoJos[0].data!!.size > 0) {

                                    relativeprogressBar?.visibility = View.GONE
                                    if (languagePoJos[0].data!![0].isReportedByYou.equals(
                                            "Yes",
                                            false
                                        )
                                    ) {
                                        mainLayout?.visibility = View.GONE
                                        noDatafoundRelativelayout?.visibility = View.VISIBLE
                                        nodata1.text = "This User Reported By you"
                                        nodatafoundtextview.visibility = View.GONE
                                        nointernetMainRelativelayout?.visibility = View.GONE
                                    } else {
                                        noDatafoundRelativelayout?.visibility = View.GONE
                                        nointernetMainRelativelayout?.visibility = View.GONE
                                        mainLayout?.visibility = View.VISIBLE
                                        if (userData?.userID.equals(languagePoJos[0].data!![0].userID)) {

                                            sessionManager?.clear_login_session()
                                            storeSessionManager(languagePoJos[0].data!!)
                                            sessionManager = SessionManager(mActivity!!)
                                            if (sessionManager != null && sessionManager!!.isLoggedIn()) {
                                                userData = sessionManager?.get_Authenticate_User()
                                            }
                                        }

                                        userDataOtherdata = languagePoJos[0].data!![0]

                                        if (userDataOtherdata != null) {
                                            setUserData1(userDataOtherdata!!)
                                            setupViewPager(userDataOtherdata)
                                            profileDetailTabLayout?.setupWithViewPager(
                                                profileDetailViewPager
                                            )
                                            setupTabIcons()
                                            profileDetailViewPager?.currentItem = 0

                                        }
                                    }


                                }
                            } else {
                                /*if(!languagePoJos[0]?.message.isNullOrEmpty()){
                                                    MyUtils.showSnackbarkotlin(
                                                        mActivity!!,
                                                        profileDetailLayoutMain!!,
                                                        "" + languagePoJos[0]?.message
                                                    )
                                                }*/

                                noDatafoundRelativelayout?.visibility = View.VISIBLE

                            }
                        } else {
                            /*if (MyUtils.isInternetAvailable(mActivity!!)) {
                                                MyUtils.showSnackbarkotlin(
                                                    mActivity!!,
                                                    profileDetailLayoutMain!!,
                                                    "" + mActivity!!.getString(R.string.somethigwrong1)
                                                )
                                            }else {
                                                MyUtils.showSnackbarkotlin(
                                                    mActivity!!,
                                                    profileDetailLayoutMain!!,
                                                    "" + mActivity!!.getString(R.string.error_common_network)
                                                )
                                            }*/
                            nodatafound()

                        }
                    })

        } else {
            /*if (MyUtils.isInternetAvailable(mActivity!!)) {
                MyUtils.showSnackbarkotlin(
                    mActivity!!,
                    profileDetailLayoutMain!!,
                    "" + mActivity!!.getString(R.string.somethigwrong1)
                )
            }else {
                MyUtils.showSnackbarkotlin(
                    mActivity!!,
                    profileDetailLayoutMain!!,
                    "" + mActivity!!.getString(R.string.error_common_network)
                )
            }*/
            nodatafound()
        }
    }

    private fun nodatafound() {
        try {
            relativeprogressBar?.visibility = View.GONE
            nointernetMainRelativelayout?.visibility = View.VISIBLE
            noDatafoundRelativelayout?.visibility = View.GONE
            mainLayout?.visibility = View.GONE

            if (MyUtils.isInternetAvailable(mActivity!!)) {
                nointernetImageview.setImageDrawable(resources.getDrawable(R.drawable.something_went_wrong))
                nointernettextview.text =
                    (GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
                nointernettextview1.text =
                    (GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
            } else {
                nointernetImageview.setImageDrawable(resources.getDrawable(R.drawable.no_internet_connection))
                nointernettextview1.text =
                    (GetDynamicStringDictionaryObjectClass?.No_Internet_Connection)


                nointernettextview.text =
                    (GetDynamicStringDictionaryObjectClass?.Please_check_your_internet_connectivity_and_try_again)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun storeSessionManager(driverdata: List<RegisterPojo.Data?>) {
        val gson = Gson()
        val json = gson.toJson(driverdata[0]!!)
        sessionManager?.create_login_session(
            json,
            driverdata[0]!!.userEmail,
            "",
            true,
            sessionManager?.isEmailLogin()!!,
            driverdata[0]!!.userFirstName!!+" "+driverdata[0]!!.userLastName!!,
            driverdata[0]!!.userProfilePicture!!)
    }

    private fun setupTabIcons() {

        for (i in 0 until profileDetailTabLayout?.tabCount!!) {
            val yourlinearlayout = LayoutInflater.from(activity).inflate(
                R.layout.item_profile_tablayout,
                null
            ) as LinearLayoutCompat
            yourlinearlayout.orientation = LinearLayoutCompat.HORIZONTAL
            val ivtabIcon =
                yourlinearlayout.findViewById<View>(R.id.ivTabIcon) as AppCompatImageView

            ivtabIcon.visibility = View.VISIBLE
            profileDetailTabLayout?.getTabAt(i)?.select()
            if (userID.equals(userData?.userID, false)) {
                ivtabIcon.setImageResource(tabIconsSelected[i])
            } else {
                ivtabIcon.setImageResource(tabIconsSelectedProfile[i])

            }
            profileDetailTabLayout?.getTabAt(i)?.customView = yourlinearlayout


        }
    }

    inner class ViewPagerAdapter(
        manager: FragmentManager,
        val tabIcons: IntArray,
        val profileDetailTabLayout: TabLayout?,
        var userID: String,
        var loginuserID1: String
    ) : FragmentStatePagerAdapter(manager) {

        override fun getCount(): Int {

            return tabIcons.size
        }

        override fun getItem(position: Int): Fragment {
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putString("userID", userID)
            bundle.putString("isYouFollowing", userDataOtherdata?.isYouFollowing)
            bundle.putString("OtherUseruserID", loginuserID1)
            var fragment: Fragment? = null

            if (userID.equals(loginuserID1, false)) {
                when (position) {

                    0 -> fragment = PostOwnerPostListFragment()
                    1 -> fragment = PostOwnerPostListFragment()
                    2 -> fragment = PostOwnerPostListFragment()
                    3 -> fragment = PostOwnerPostListFragment()

                }
            } else {
                when (position) {
                    0 -> fragment = PostOwnerPostListFragment()
                    1 -> fragment = PostOwnerPostListFragment()
                }
            }

            fragment!!.arguments = bundle


            return fragment
        }

        override fun saveState(): Parcelable? {
            return null
        }

        override fun getItemPosition(`object`: Any): Int {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return PagerAdapter.POSITION_NONE
        }
        /*override  fun getPageTitle(position: Int): CharSequence {
            return viewcount?.get(position)!!
        }*/


    }


    override fun onClick(v: View?) {
        when (v) {
            profileDetailTextUserDotMenu -> showDotMenu()
            ll_followers -> {
                //      if (userData?.userID.equals(userDataOtherdata?.userID)) {
                var myProfileFragment = MyProfileFollowingFollowersFragment()

                Bundle().apply {
                    putInt("pos", 0)
                    putString("userId", userID)
                    putString("followers", profileDetailTextUserFollowersCount.text.toString())
                    putString("following", profileDetailTextUserFollowingCount.text.toString())

                    myProfileFragment.arguments = this

                }
                (activity as MainActivity).navigateTo(
                    myProfileFragment,
                    myProfileFragment::class.java.name,
                    true
                )
                //  }
            }
            ll_following -> {
                //    if (userData?.userID.equals(userDataOtherdata?.userID)) {
                var myProfileFragment = MyProfileFollowingFollowersFragment()
                Bundle().apply {
                    putInt("pos", 1)
                    putString("userId", userID)

                    putString("followers", profileDetailTextUserFollowersCount.text.toString())
                    putString("following", profileDetailTextUserFollowingCount.text.toString())

                    myProfileFragment.arguments = this
                }
                (activity as MainActivity).navigateTo(
                    myProfileFragment,
                    myProfileFragment::class.java.name,
                    true
                )
                //   }
            }
        }
    }

    fun logOut() {
        (mActivity as MainActivity).showMessageOKCancelCustome(mActivity!!,
            GetDynamicStringDictionaryObjectClass?.Are_you_sure_want_to_logout_your_account,
            "",
            GetDynamicStringDictionaryObjectClass?.Log_out,
            GetDynamicStringDictionaryObjectClass?.Cancel_,
            DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
                userRegister("")
            })
    }


    private fun userRegister(newtoken: String) {
        MyUtils.showProgressDialog(mActivity!!)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("loginuserID", userData?.userID)

            if (sessionManager?.isLoggedIn()!!) {
                jsonObject.put("languageID", userData?.languageID)

            } else {
                jsonObject.put("languageID", sessionManager?.getsetSelectedLanguage())

            }
            jsonObject.put("userDeviceID", newtoken)
            jsonObject.put("userDeviceType", RestClient.apiType)
            jsonObject.put("loginuserID", userData?.userID)

            jsonArray.put(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val verifyOTP = ViewModelProviders.of(this@ProfileDetailFragment)
            .get(OnBoardingModel::class.java)

        verifyOTP.apiCall(mActivity!!, jsonArray.toString(), 5)
            .observe(this@ProfileDetailFragment, object : Observer<List<RegisterPojo>?> {
                override fun onChanged(response: List<RegisterPojo>?) {

                    if (!response.isNullOrEmpty()) {
                        MyUtils.closeProgress()
                        if (response[0].status.equals("true", true)) {

                            PrefDb(mActivity!!).clearValue("ServiceNotProvide")
                            sessionManager?.clear_login_session()

                            val myIntent =
                                Intent((mActivity as MainActivity), MainActivity::class.java)
                            startActivity(myIntent)
                            (mActivity as MainActivity).finishAffinity()
                            (mActivity as MainActivity).overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )

                        } else {
                            (activity as MainActivity).showSnackBar(response[0].message)
                        }

                    } else {
                        MyUtils.closeProgress()
                        (activity as MainActivity).errorMethod()
                    }
                }
            })
    }

    private fun showDilaogPostReport(
        postId: String?,
        data: List<ReasonList.Data>
    ) {

        val dialogs =
            MaterialAlertDialogBuilder(mActivity!!, R.style.ThemeOverlay_MyApp_Dialog)

        //dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(true)

        val dialogView = layoutInflater.inflate(R.layout.report_dialog_reson_list, null)
        dialogs.setView(dialogView)
        val alertDialog = dialogs.create()

        val tvReportTitle = dialogView.findViewById<AppCompatTextView>(R.id.tvReportTitle)
        tvReportTitle?.text = "" + GetDynamicStringDictionaryObjectClass?.Reason_for_reporting
        val rvReportReason = dialogView.findViewById<RecyclerView>(R.id.rvReportReason)

        linearLayoutManager = LinearLayoutManager(mActivity!!)
        postReportListAdapter = PostReportListAdapter(
            mActivity!!,
            object : PostReportListAdapter.OnItemClick {
                override fun onClicklisneter(pos: Int, name: String, v: View, reasonid: String) {
                    alertDialog.dismiss()
                    getPostReport(postId, data[pos].reasonID, name)
                }


            }, "", data
        )
        rvReportReason.layoutManager = linearLayoutManager
        rvReportReason.adapter = postReportListAdapter

        var lp = WindowManager.LayoutParams()
        lp.copyFrom(alertDialog.window?.attributes)
//        lp.width = 850
//        lp.height = 900

        alertDialog.window?.attributes = lp
        alertDialog.show()

    }

    private fun getPostReport(
        postID: String?,
        postreportreasonID: String,
        name: String
    ) {
        MyUtils.showProgress(mActivity!!)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("userID", postID)
            jsonObject.put("reasonID", postreportreasonID)
            jsonObject.put("userreportRemark", name)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        var reportPostModel =
            ViewModelProviders.of(this@ProfileDetailFragment).get(ReportPostModel::class.java)

        reportPostModel.apiFunction(mActivity!!, jsonArray.toString(), "UserRePort")
            .observe(this@ProfileDetailFragment, Observer { it ->
                //MyUtils.closeProgress()
                if (it != null && it.isNotEmpty()) {
                    MyUtils.closeProgress()
                    if (it[0].status.equals("true", true)) {
                        (activity as MainActivity).showSnackBar(it[0].message!!)
                        (activity as MainActivity).onBackPressed()
                        if (notifyProfileInterface != null) {
                            if (feedData != null) {
                                notifyProfileInterface!!.ProfileNotifyData(
                                    feedData?.postID!!,
                                    feedData?.userID!!
                                )
                            }
                        }
                    } else {
                        (activity as MainActivity).showSnackBar(it[0].message!!)
                    }

                } else {
                    MyUtils.closeProgress()
                    (activity as MainActivity).errorMethod()
                }
            })

    }

    private fun getUserFollow(s: String, userID: String) {
        MyUtils.showProgressDialog(mActivity!!)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("userID", userID)
            jsonObject.put("action", s)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var getFollowModel =
            ViewModelProviders.of(this@ProfileDetailFragment).get(FollowModel::class.java)
        getFollowModel.apiCall(mActivity!!, jsonArray.toString(), s)
            .observe(this@ProfileDetailFragment,
                Observer
                { trendingFeedDatum ->
                    if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                        MyUtils.closeProgress()
                        if (trendingFeedDatum[0].status?.equals("true", false)!!) {
                            when (s) {
                                "follow" -> {
                                    btnFollowing?.visibility = View.VISIBLE
                                    btnFollow?.visibility = View.GONE
                                    btnFollowing?.visibility = View.VISIBLE
                                    btnFollow?.visibility = View.GONE
                                    userDataOtherdata?.isYouFollowing = "Yes"
                                    viewPagerAdapter?.notifyDataSetChanged()
                                    profileDetailTextUserFollowersCount.text =
                                        (profileDetailTextUserFollowersCount.text.toString().trim()
                                            .toInt() + 1).toString()
                                    if (notifyInterface != null) {
                                        feedData?.isYouFollowing = "Yes"

                                        notifyInterface!!.notifyData(
                                            feedData,
                                            false,
                                            false,
                                            feedData?.postComment
                                        )
                                    }

                                }
                                "unfollow" -> {
                                    btnFollowing?.visibility = View.GONE
                                    btnFollow?.visibility = View.VISIBLE
                                    profileDetailTextUserFollowersCount.text =
                                        (profileDetailTextUserFollowersCount.text.toString().trim()
                                            .toInt() - 1).toString()
                                    userDataOtherdata?.isYouFollowing = "No"
                                    viewPagerAdapter?.notifyDataSetChanged()

                                    if (notifyInterface != null) {
                                        feedData?.isYouFollowing = "No"
                                        notifyInterface!!.notifyData(
                                            feedData,
                                            false,
                                            false,
                                            feedData?.postComment
                                        )
                                    }
                                }
                            }

                        } else {

                            (activity as MainActivity).showSnackBar(trendingFeedDatum[0].message!!)
                        }

                    } else {
                        if (activity != null) {
                            MyUtils.closeProgress()
                            (activity as MainActivity).errorMethod()

                        }
                    }
                })
    }

    fun showDialogPost(msg: String) {

        val dialogs =
            MaterialAlertDialogBuilder(mActivity!!, R.style.ThemeOverlay_MyApp_Dialog)

        //   dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(true)

        val dialogView = layoutInflater.inflate(R.layout.be_a_reporter_dialog, null)
        dialogs.setView(dialogView)
        val alertDialog = dialogs.create()

        val ll_home_delivery = dialogView.findViewById<LinearLayout>(R.id.root_Dialog)
        val img_logo_dialog = dialogView.findViewById<AppCompatImageView>(R.id.img_logo_dialog)
        val tvUserNameDialog = dialogView.findViewById<AppCompatTextView>(R.id.tvUserNameDialog)
        val tvUserIdDialog = dialogView.findViewById<AppCompatTextView>(R.id.tvUserIdDialog)
        val tvyouare_reporterDialog =
            dialogView.findViewById<AppCompatTextView>(R.id.tvyouare_reporterDialog)
        img_logo_dialog.setImageResource(R.drawable.tick_circle_post_aired_successfully_prompt)
        tvUserNameDialog.visibility = View.GONE
        tvUserIdDialog.visibility = View.GONE
        tvyouare_reporterDialog.text = msg

        var lp = WindowManager.LayoutParams()
        lp.copyFrom(alertDialog.window?.attributes)
//        lp.width = 850
//        lp.height = 900

        alertDialog.window?.attributes = lp
        alertDialog.show()

        Handler().postDelayed({

            alertDialog.dismiss()

        }, 5000)


    }

    fun getMyUserProfileData() {
        MyUtils.showProgressDialog(activity!!)
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("languageID", "" + sessionManager?.getsetSelectedLanguage())
            jsonObject.put("loginuserID", "" + userData?.userID)
            if (userID.equals(userData?.userID)) {
                jsonObject.put("otherUserID", "" + userData?.userID)
            }
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        var getLanguageModel = ViewModelProviders.of(this@ProfileDetailFragment).get(
            GetOtherUserProfileModel::class.java
        )
        getLanguageModel.apiFunction(mActivity!!, false, jsonArray.toString())
            .observe(this@ProfileDetailFragment,
                Observer<List<RegisterPojo>> { languagePoJos ->
                    if (languagePoJos != null) {

                        if (languagePoJos[0].status.equals("true", true)) {

                        } else {


                        }
                    } else {
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                profileDetailLayoutMain!!,
                                "" + mActivity!!.getString(R.string.somethigwrong1)
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                profileDetailLayoutMain!!,
                                "" + mActivity!!.getString(R.string.error_common_network)
                            )
                        }


                    }
                })

    }


    public fun notifyData(
        feedDatum: TrendingFeedData,
        isDelete: Boolean,
        isDeleteComment: Boolean
    ) {
        if (!userData?.userID.equals(feedDatum?.userID, false)) {
            Log.e("System out", "Printjjj :  " + feedDatum?.isYouFollowing)
            userDataOtherdata?.isYouFollowing = feedDatum?.isYouFollowing
            userDataOtherdata?.IsClappeddByYou = feedDatum?.IsClappeddByYou!!
            userDataOtherdata?.userClapCount =
                (feedDatum?.userClapCount?.toInt()).toString()
            setUserData1(userDataOtherdata)


        }
    }
}


