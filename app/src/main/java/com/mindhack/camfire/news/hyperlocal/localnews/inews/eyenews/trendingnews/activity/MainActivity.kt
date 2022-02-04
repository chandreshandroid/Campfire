package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.Manifest
import android.Manifest.permission
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap.CompressFormat
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.provider.MediaStore
import android.provider.MediaStore.Video
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.OnSuccessListener
import com.google.android.play.core.tasks.TaskExecutors
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.infideap.drawerbehavior.AdvanceDrawerLayout
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.BuildConfig
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.Service.NotifyInterface
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.Service.NotifyProfileInterface
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.SuggestedLocationListAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.interfacee.NavigationHost
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.GeocodingLocation
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.PlaceAPI
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.PlacesAutoCompleteAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.model.Place
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OnBoardingModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.AddImages
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.Push
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.roomdatabase.AppDatabase
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.roomdatabase.DatabaseClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.roomdatabase.User
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.*
import com.mindhack.flymyowncustomer.util.PrefDb
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_view.view.*
import kotlinx.android.synthetic.main.top_sheet_dialog_view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationHost, NotifyInterface, NotifyProfileInterface {

    lateinit var mDrawerLayout: AdvanceDrawerLayout
    private var doubleBackToExitPressedOnce = false
    var snackBarParent: View? = null

    var sessionManager: SessionManager? = null
    var userData: RegisterPojo.Data? = null
    var title_ok = ""
    var currently_we_are_building = ""
    var to_exit = ""
    var something_rong = ""
    var session_expire = ""
    var from: String = ""
    private var linearLayoutManager1: LinearLayoutManager? = null
    var suggestedLocationListAdapter: SuggestedLocationListAdapter? = null
    var suggestedLocationList: ArrayList<String>? = null
    var currentapiVersion = VERSION.SDK_INT
    var mfUser: File? = null

    private var mImageCaptureUri: Uri? = null
    var fileNameAddRecipieStep01 = ""
    var mfAddRecipieStep01_Image: File? = null
    var fileType = false
    var numberOfImage: ArrayList<AddImages>? = null


    var street = ""
    var city = ""
    var state = ""
    var country = ""
    var zipCode = ""

    var roomDB: AppDatabase? = null
    var userLocationSerach = User()
    var userLocationSerachList = ArrayList<User>()
    private var locationProvider: LocationProvider? = null

    var fromPush = false
    var drawer_view:View ?= null
    companion object {
        val TAG: String = SplashActivity::class.java.simpleName
        const val REQUEST_UPDATE_CODE = 1
    }

    var installStateUpdatedListener: InstallStateUpdatedListener? = null


    var appUpdateManager: AppUpdateManager? = null


    var playServiceExecutor: Executor? = null
    private val mYourBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // now you can call all your fragments method here
            var from = ""
            var msg = ""
            var UploadVideo = ""
            var numberOfImage: ArrayList<AddImages?>? = ArrayList()
            if (intent.hasExtra("from")) {
                from = intent.getStringExtra("from")!!

            }
            if (intent.hasExtra("msg")) {
                msg = intent.getStringExtra("msg")!!
            }
            if (intent.hasExtra("UploadVideo")) {
                UploadVideo = intent.getStringExtra("UploadVideo")!!
            }

            when (UploadVideo) {
                "Video" -> {
                    if (intent.hasExtra("VideoFile")) {
                        numberOfImage =
                            intent.getSerializableExtra("VideoFile") as ArrayList<AddImages?>?
                        var from = intent.getStringExtra("from")!!
                        var UploadVideo = intent.getStringExtra("UploadVideo")!!
                        var postHeadline = intent.getStringExtra("postHeadline")
                        var postDescription = intent.getStringExtra("postDescription")
                        var postLocation = intent.getStringExtra("postLocation")
                        var postLatitude = intent.getStringExtra("postLatitude")
                        var postLongitude = intent.getStringExtra("postLongitude")
                        var postLocationVerified = intent.getStringExtra("postLocationVerified")
                        var postPrivacyType = intent.getStringExtra("postPrivacyType")
                        var postCreateType = intent.getStringExtra("postCreateType")
                        var postHashText = intent.getStringExtra("postHashText")
                        var posttag = intent.getStringExtra("posttag")
                        if (getCurrentFragment() is FeedFragment) {
                            (getCurrentFragment() as FeedFragment).isVideoUpload(
                                numberOfImage,
                                from,
                                UploadVideo,
                                postHeadline,
                                postDescription,
                                postLatitude,
                                postLongitude,
                                postLocationVerified,
                                postPrivacyType,
                                postCreateType,
                                postHashText,
                                posttag,
                                postLocation
                            )
                        }
                    }

                }
                else -> {
                    if (getCurrentFragment() is FeedFragment) {
                        (getCurrentFragment() as FeedFragment).showDialogPost(msg)
                        (getCurrentFragment() as FeedFragment).pageNo = 0
                        (getCurrentFragment() as FeedFragment).getPostList()
                    } else if (getCurrentFragment() is ProfileDetailFragment) {
                        navigateTo(FeedFragment(), FeedFragment::class.java.name, true)
                        (getCurrentFragment() as ProfileDetailFragment).showDialogPost(msg)
                    } else if (getCurrentFragment() is TrendingFeedFragment) {
                        navigateTo(FeedFragment(), FeedFragment::class.java.name, true)
                        (getCurrentFragment() as TrendingFeedFragment).showDialogPost(msg)
                    } else if (getCurrentFragment() is ProfileDetailFragment) {
                        (getCurrentFragment() as ProfileDetailFragment).getOtherUserProfileData()
                    }
                }
            }

            // navigateTo(FeedFragment(), FeedFragment::class.java.name, true)

        }
    }

    /*private val mGpsSwitchStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent) {
          if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getStringExtra("Location"),false)) {

              var locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
              var isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
              var isNetworkEnabled =
                  locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

              if (isGpsEnabled || isNetworkEnabled) {
                  // Handle Location turned ON
                  Log.e("fdsf","true")
                  msgButton.visibility=View.GONE

              } else {
                  // Handle Location turned OFF
                  Log.e("fdsf","false")
                  msgButton.visibility=View.VISIBLE
                  msgButton.text="Please turn on your location.."
              }
          }
      }
  }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.activity_main)
        dynamicString()
        mDrawerLayout = findViewById(R.id.mDrawerLayout);
        LocalBroadcastManager.getInstance(this@MainActivity)
            .registerReceiver(mYourBroadcastReceiver, IntentFilter("CreatePost"))

        /*LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(mGpsSwitchStateReceiver, IntentFilter(
            LocationManager.PROVIDERS_CHANGED_ACTION))*/
        updateChecker()
        if (intent != null) {
            if (intent.hasExtra("from")) {
                from = intent.getStringExtra("from")!!
            }
        }
        sessionManager = SessionManager(this@MainActivity)

        roomDB = DatabaseClient.getInstance(applicationContext)?.appDatabase

        if (sessionManager != null && sessionManager!!.isLoggedIn()) userData =
            sessionManager?.get_Authenticate_User()

        if (sessionManager!!.isLoggedIn() /*&& FirebaseInstanceId.getInstance().token != null*/) {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("System out", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }
//                    val newtoken = "asdasd2321kjsd"
                    val newtoken = task.result?.token
                    Log.d("System out", "new token:= " + newtoken)

                    if (from.equals("MyProfileEdit", false)) {
                        showDialogBeAReporter(
                            userData?.userFirstName + " " + userData?.userLastName,
                            "@" + userData?.userMentionID
                        )
                    }

                    userRegister(newtoken!!)
                })
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, MapFragment(), MapFragment::class.java.name)
            .addToBackStack(null)
            .commit()

        initNavigation();

        if (intent.hasExtra("Push")) {
            fromPush = true

            val push = intent.getSerializableExtra("Push") as Push
            if (push != null) {
                if (push.type.equals("post-liked") ||
                    push.type.equals("post-commented") ||
                    push.type.equals("post-shared") ||
                    push.type.equals("post-tagged")
                ) {

                    val bundle = Bundle()
                    bundle.apply {
                        putString("postId", push.refKey)
                    }
                    navigateTo(
                        FeedFragmentPushNotification(),
                        bundle,
                        FeedFragmentPushNotification::class.java.name,
                        true
                    )
                } else if (push.type.equals("follow") || push.type.equals("clapped")) {

                    if (sessionManager?.isLoggedIn()!!) {
                        var profileDetailFragment = ProfileDetailFragment()
                        Bundle().apply {
                            putString("userID", push.refKey)
                            putString("userName", "")
                            putSerializable("feedData", null)
                            profileDetailFragment.arguments = this
                        }

                        navigateTo(
                            profileDetailFragment,
                            profileDetailFragment::class.java.name,
                            true
                        )
                    } else {
                        MyUtils.startActivity(
                            this,
                            LoginActivity::class.java,
                            false
                        )

                    }

                } else if (push.type.equals("level-up") || push.type.equals("new-post-created")) {
                    if (sessionManager?.isLoggedIn()!!) {
                        var profileDetailFragment = ProfileDetailFragment()
                        Bundle().apply {
                            putString("userID", push.refKey)
                            putString("userName", "")
                            putSerializable("feedData", null)
                            profileDetailFragment.arguments = this
                        }

                        navigateTo(
                            profileDetailFragment,
                            profileDetailFragment::class.java.name,
                            true
                        )
                    } else {
                        MyUtils.startActivity(
                            this,
                            LoginActivity::class.java,
                            false
                        )

                    }
                } else {

                }
            }
        }

        mDrawerLayout.setViewScale(Gravity.START, 0.8f);
        mDrawerLayout.setRadius(Gravity.START, 35F);
        mDrawerLayout.setViewElevation(Gravity.START, 80F);
    }


    fun getUpdateSessionManager(context: AppCompatActivity) {
        sessionManager = SessionManager(context)

        if (sessionManager != null && sessionManager!!.isLoggedIn()) {
            userData = sessionManager?.get_Authenticate_User()
        }

    }


    private fun dynamicString() {
        title_ok = "Ok"
        currently_we_are_building = "Currently we are building this so you can;t see that."
        to_exit = "To exit, press back again."
        something_rong = resources.getString(R.string.error_crash_error_message)
        session_expire = resources.getString(R.string.session_expire)
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.container)
    }

    public override fun navigateTo(fragment: Fragment, tag: String, addToBackstack: Boolean) {

        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, tag)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        transaction.commitAllowingStateLoss()

    }

    override fun navigateTo(
        fragment: Fragment,
        bundle: Bundle,
        tag: String,
        addToBackstack: Boolean
    ) {

        fragment.arguments = bundle
        val transaction = supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            .replace(R.id.container, fragment, tag)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        transaction.commitAllowingStateLoss()
    }


    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
            {
                mDrawerLayout.openDrawer(Gravity.LEFT)
            } else {
            val manager = supportFragmentManager

            if (manager.backStackEntryCount >= 1) {
                val f = supportFragmentManager.findFragmentById(R.id.container)
                top_sheet.visibility = View.GONE
                if (fromPush && manager.backStackEntryCount === 1) {
                    fromPush = false
                    navigateTo(MapFragment(), MapFragment::class.java.name, true)
                }   else if (f != null && f is MapFragment) {
                  showexit()
                } else
                    manager.popBackStack()
            } else {
                showexit()
            }

    }
}

fun showexit() {
    //Store Cart Data before exit app
    if (doubleBackToExitPressedOnce) {
        finishAffinity()
        return
    }
    doubleBackToExitPressedOnce = true

    showSnackBar(to_exit)
    Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 3000)

}

private fun addViewSnackBar() {
    snackBarParent = View(this)
    val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 200)
    layoutParams.gravity = Gravity.BOTTOM
    snackBarParent!!.layoutParams = layoutParams
    container.addView(snackBarParent)
}

fun showSnackBar(message: String) {

    if ((mDrawerLayout != null) and !isFinishing)
        Snackbar.make(this.mDrawerLayout!!, message, Snackbar.LENGTH_LONG).show()

    /*if (bottom_fab_button.isVisible) {
        bottom_fab_button.visibility = View.v
        Handler().postDelayed({
            bottom_fab_button.visibility = View.GONE
        }, 2000)
    }*/
}


fun showMessageOKCancelCustome(
    context: Context,
    message: String,
    title: String = "",
    pButton: String, nButton: String,
    okListener: DialogInterface.OnClickListener
): Dialog {
    val builder = MaterialAlertDialogBuilder(context)
    builder.setMessage(message)
    builder.setCancelable(false)
    builder.setPositiveButton(pButton, okListener)


    builder.setNegativeButton(nButton) { dialog, which -> dialog.dismiss() }
    val alert = builder.create()

//        alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
    alert.show()
    alert.getButton(AlertDialog.BUTTON_POSITIVE)
        .setBackgroundColor(context.resources.getColor(R.color.colorSurface))
    alert.getButton(AlertDialog.BUTTON_POSITIVE).elevation = 0f

    return alert
}

fun showMessageOK(
    context: Context,
    message: String,
    okListener: DialogInterface.OnClickListener
): Dialog {
//        val builder = android.app.AlertDialog.Builder(context)
    val builder = MaterialAlertDialogBuilder(context)
    builder.setMessage(message)
    builder.setCancelable(false)
    builder.setPositiveButton(title_ok, okListener)


    val alert = builder.create()
//        alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
    alert.show()
    alert.getButton(AlertDialog.BUTTON_POSITIVE)
        .setBackgroundColor(context.resources.getColor(R.color.colorSurface))
    alert.getButton(AlertDialog.BUTTON_POSITIVE).elevation = 0f
    return alert
}


private fun userRegister(newtoken: String) {

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

    val verifyOTP = ViewModelProviders.of(this@MainActivity)
        .get(OnBoardingModel::class.java)

    verifyOTP.apiCall(this@MainActivity, jsonArray.toString(), 5)
        .observe(this@MainActivity, object : Observer<List<RegisterPojo>?> {
            override fun onChanged(response: List<RegisterPojo>?) {

                if (!response.isNullOrEmpty()) {
                    if (response[0].status.equals("true", true)) {

                        if (newtoken.length == 0) {
                            val sessionManager = SessionManager(this@MainActivity)
//                                CartCalculateNew.clearCartItems()
                            PrefDb(this@MainActivity).clearValue("ServiceNotProvide")
//                                PrefDb(this@MainActivity).userFirstTime(true)
                            sessionManager.clear_login_session()
//                                PrefDb(this@MainActivity).clearPrefDb()
                            var intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                            this@MainActivity.finishAffinity()
                        } else {
                            if (response[0].data!!.size > 0) {
                                sessionManager?.clear_login_session()
                                storeSessionManager(response[0].data!![0])
                            } else {
//                                    CartCalculateNew.clearCartItems()
                                sessionExpire(
                                    this@MainActivity,
                                    DialogInterface.OnClickListener { dialogInterface, i ->
                                        sessionManager?.clear_login_session()
                                        var intent =
                                            Intent(this@MainActivity, LoginActivity::class.java)
                                        startActivity(intent)
                                        this@MainActivity.finishAffinity()
                                    })
                            }
                        }
                    } else {
                        if (newtoken.length > 0) {
                            sessionExpire(
                                this@MainActivity,
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                    PrefDb(this@MainActivity).clearValue("ServiceNotProvide")
//                                    PrefDb(this@MainActivity).userFirstTime(true)
//                                        CartCalculateNew.clearCartItems()
                                    sessionManager?.clear_login_session()
//                                        PrefDb(this@MainActivity).c()
                                    var intent =
                                        Intent(this@MainActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    this@MainActivity.finishAffinity()
                                })
                        } else {
                            errorMethod()

                        }

                    }

                } else {
                    errorMethod()
                }
            }
        })
}

private fun storeSessionManager(driverdata: RegisterPojo.Data) {
    val gson = Gson()
    val json = gson.toJson(driverdata)
    sessionManager?.create_login_session(
        json,
        driverdata.userEmail,
        "",
        true,
        sessionManager?.isEmailLogin()!!,
        driverdata!!.userFirstName!! + " " + driverdata!!.userLastName!!,
        driverdata!!.userProfilePicture!!

    )
}

fun sessionExpire(
    context: Context,
    okListener: DialogInterface.OnClickListener
): Dialog {
    val builder = MaterialAlertDialogBuilder(context)
    builder.setMessage(session_expire)
    builder.setCancelable(false)
    builder.setPositiveButton(title_ok, okListener)

    val alert = builder.create()
//        alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
    alert.show()
    alert.setCanceledOnTouchOutside(false)

    return alert
}


private fun showDialogBeAReporter(userName: String, userMentionID: String?) {

    val dialogs =
        MaterialAlertDialogBuilder(this@MainActivity, R.style.ThemeOverlay_MyApp_Dialog)

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

    tvUserNameDialog.text = userName
    tvUserIdDialog.text = userMentionID


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

@RequiresApi(VERSION_CODES.M)
fun openTopSheet(width: Int, height: Int) {
    val tt = TopSheetBehavior.from(top_sheet)

    if (top_sheet.visibility != View.VISIBLE) {
        top_sheet.visibility = View.VISIBLE
        tt.state = (TopSheetBehavior.STATE_EXPANDED)
    } else {
        MyUtils.hideKeyboard1(this@MainActivity)
        top_sheet.visibility = View.GONE
        tt.state = (TopSheetBehavior.STATE_COLLAPSED)

    }


    val editSearch = top_sheet.findViewById<AutoCompleteTextView>(R.id.editSearch)
    val tv_current_selectLocation =
        top_sheet.findViewById<AppCompatTextView>(R.id.tv_current_selectLocation)
    val suggestedLocation =
        top_sheet.findViewById<AppCompatTextView>(R.id.suggestedLocation)
    val recyclerview =
        top_sheet.findViewById<RecyclerView>(R.id.recyclerview)
    val ll_suggestion_height =
        top_sheet.findViewById<LinearLayoutCompat>(R.id.ll_suggestion_height)


    if (userLocationSerachList.isNullOrEmpty()) {
        recyclerview.visibility = View.GONE
        suggestedLocation.visibility = View.GONE
        var params = ll_suggestion_height.layoutParams
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        ll_suggestion_height.layoutParams = params

    } else {
        recyclerview.visibility = View.VISIBLE
        suggestedLocation.visibility = View.VISIBLE
        var params = ll_suggestion_height.layoutParams
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        ll_suggestion_height.layoutParams = params
    }

    editSearch.setAdapter(
        PlacesAutoCompleteAdapter(
            this,
            PlaceAPI.Builder().apiKey(resources.getString(R.string.google_places_key))
                .build(this@MainActivity)
        )
    )
    editSearch.onItemClickListener =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            val place = parent.getItemAtPosition(position) as Place
            //  editSearch.setText(place.description)
            MyUtils.locationCityName = place.description

            var locationAddress = GeocodingLocation.getAddressFromLocation(
                place.description.toString().trim(),
                applicationContext, GeocoderHandler()
            )
//                getPlaceDetails(place.id)

            /*MyUtils.hideKeyboard1(this@MainActivity)
            if (getCurrentFragment() is FeedFragment) {
                MyUtils.currentLattitude = MyUtils.currentLattitudeFix
                MyUtils.currentLongitude = MyUtils.currentLongitudeFix
                MyUtils.locationCityName = place.description

                (getCurrentFragment() as FeedFragment).pageNo = 0
                (getCurrentFragment() as FeedFragment).getPostList()
                (getCurrentFragment() as FeedFragment).setHeaderText()
            }

            userLocationSerach.searchLocationName = place.description
            userLocationSerach.searchLocationLate = MyUtils.currentLattitude
            userLocationSerach.searchLocationLong = MyUtils.currentLongitude*/
//                userLocationSerach.searchTime =
//                    SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Date())
//                storeInRoom()
            top_sheet.visibility = View.GONE
            tt.state = (TopSheetBehavior.STATE_COLLAPSED)
        }


    tv_current_selectLocation.setOnClickListener {

        val currentapiVersion = Build.VERSION.SDK_INT
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            permissionLocation()
        } else {

        }


        /*MyUtils.hideKeyboard1(this@MainActivity)
        if (getCurrentFragment() is FeedFragment) {
            MyUtils.currentLattitude = MyUtils.currentLattitudeFix
            MyUtils.currentLongitude = MyUtils.currentLongitudeFix
            MyUtils.locationCityName = MyUtils.locationCityNameFix

            (getCurrentFragment() as FeedFragment).pageNo = 0
            (getCurrentFragment() as FeedFragment).getPostList()
            (getCurrentFragment() as FeedFragment).setHeaderText()
        }*/

        top_sheet.visibility = View.GONE
        tt.state = (TopSheetBehavior.STATE_COLLAPSED)
    }

    tt.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
        override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            //  Log.d("TAG", "newState: $newState")

        }

        override fun onSlide(
            @NonNull bottomSheet: View, slideOffset: Float,
            isOpening: Boolean?
        ) {
            //  Log.d("TAG", "slideOffset: $slideOffset")
            if (isOpening != null) {
                MyUtils.hideKeyboard1(this@MainActivity)
                //   Log.d("TAG", "isOpening: $isOpening")
            }
        }
    })

    getRoomData()

//        setLocationSuggested()

    /* val dialogs = TopSheetDialog(this@MainActivity)


    dialogs.setContentView(R.layout.top_sheet_dialog_view)


    dialogs.show()*/
}

private inner class GeocoderHandler : Handler() {
    override fun handleMessage(message: Message) {
        var locationAddress: String? = null
        when (message.what) {
            1 -> {
                val bundle = message.data
                locationAddress = bundle.getString("address")
                Log.e("System out", "PP lat long:=  " + locationAddress)
            }
            else -> locationAddress = null
        }
        if (!locationAddress.isNullOrEmpty()) {
            val parts = locationAddress.split(",")
            if (!parts.isNullOrEmpty()) {
//                    imageLocationLate = parts[0].toFloat()
//                    imageLocationLong = parts[1].toFloat()
                MyUtils.hideKeyboard1(this@MainActivity)
                try {
                    MyUtils.currentLattitude = parts[0].toDouble()
                    MyUtils.currentLongitude = parts[1].toDouble()

                    if (getCurrentFragment() is FeedFragment) {
                        (getCurrentFragment() as FeedFragment).pageNo = 0
                        (getCurrentFragment() as FeedFragment).isLastpage = false
                        (getCurrentFragment() as FeedFragment).getPostList()
                        (getCurrentFragment() as FeedFragment).setHeaderText()
                    } else if (getCurrentFragment() is MapFragment) {
                        //(getCurrentFragment() as MapFragment).pageNo = 0
                        (getCurrentFragment() as MapFragment).getPostList("main")
                        (getCurrentFragment() as MapFragment).setHeaderText()

                    }
                    userLocationSerach.searchLocationName = MyUtils.locationCityName
                    userLocationSerach.searchLocationLate = MyUtils.currentLattitude
                    userLocationSerach.searchLocationLong = MyUtils.currentLongitude

                    userLocationSerach.searchTime =
                        SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Date())
                    storeInRoom()

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()

                }
            }
        }
//            latLongTV.setText(locationAddress)
    }
}

private fun getRoomData() {
    AsyncTask.execute(object : Runnable {
        override fun run() {
            try {
                // here I am inserting the item into database
                userLocationSerachList = ArrayList<User>()
                userLocationSerachList.clear()
                userLocationSerachList.addAll(roomDB?.userDao()!!.all.reversed())
                this@MainActivity.runOnUiThread(object : Runnable {
                    override fun run() {
                        if (!userLocationSerachList.isNullOrEmpty()) {
                            setLocationSuggested()
                        } /*else {
                                checkRecentFoundOrNot()
                            }*/
                    }
                })
            } catch (ex: SQLiteConstraintException) {
                this@MainActivity.runOnUiThread(object : Runnable {
                    override fun run() {
//                            checkRecentFoundOrNot()
                    }
                })
            }
        }
    })

}

private fun storeInRoom() {
    AsyncTask.execute(object : Runnable {
        override fun run() {
            try {
                // here I am inserting the item into database
                roomDB?.userDao()!!.insertAll(userLocationSerach)
                runOnUiThread(object : Runnable {
                    override fun run() {

                    }
                })
            } catch (ex: SQLiteConstraintException) {
                runOnUiThread(object : Runnable {
                    override fun run() {
//                                                Toast.makeText(
//                                                    getApplicationContext(),
//                                                    "Item already added",
//                                                    Toast.LENGTH_LONG
//                                                ).show();
                    }
                })
            }
        }
    })
}

fun setLocationSuggested() {
//        suggestedLocationList = ArrayList()
//        suggestedLocationList?.clear()
//        for (i in 0 until 10) {
//            suggestedLocationList?.add("Ahmedabad")
//        }
    linearLayoutManager1 = LinearLayoutManager(this@MainActivity)
    suggestedLocationListAdapter = SuggestedLocationListAdapter(
        this@MainActivity,
        object : SuggestedLocationListAdapter.OnItemClick {
            override fun onClicklisneter(
                pos: Int,
                name: String,
                late: Double,
                long: Double,
                v: View
            ) {
                MyUtils.hideKeyboard1(this@MainActivity)

                MyUtils.currentLattitude = late
                MyUtils.currentLongitude = long
                MyUtils.locationCityName = name

                if (getCurrentFragment() is FeedFragment) {
                    (getCurrentFragment() as FeedFragment).pageNo = 0
                    (getCurrentFragment() as FeedFragment).getPostList()
                    (getCurrentFragment() as FeedFragment).setHeaderText()
//                        Log.d("Array of Address", placeDetails.address.toString())
                } else
                    if (getCurrentFragment() is MapFragment) {
                        (getCurrentFragment() as MapFragment).getPostList("main")
                        (getCurrentFragment() as MapFragment).setHeaderText()
//                        Log.d("Array of Address", placeDetails.address.toString())
                    }

                top_sheet.visibility = View.GONE
                TopSheetBehavior.from(top_sheet).state = (TopSheetBehavior.STATE_COLLAPSED)
            }
        }, "", userLocationSerachList
    )
    recyclerview?.layoutManager = linearLayoutManager1
    recyclerview?.adapter = suggestedLocationListAdapter
}

fun errorMethod() {
    try {
        if (!MyUtils.isInternetAvailable(this@MainActivity)) {
            showSnackBar(GetDynamicStringDictionaryObjectClass.Please_check_your_internet_connectivity_and_try_again)
        } else {
            showSnackBar(GetDynamicStringDictionaryObjectClass.Something_Went_Wrong)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun getWriteStoragePermissionOther() {
    val permissionCheck = checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE)

    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        getReadStoragePermissionOther()
    } else {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.WRITE_EXTERNAL_STORAGE),
            MyUtils.Per_REQUEST_WRITE_EXTERNAL_STORAGE_1
        )
    }
}

private fun addPermissionWrite(permission: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (checkSelfPermission(permission) !== PackageManager.PERMISSION_GRANTED) {
            return false
        }

    }

    return true
}

fun getReadStoragePermissionOther() {
    val permissionCheck = checkSelfPermission(permission.READ_EXTERNAL_STORAGE)

    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        getCameraPermissionOther()
    } else {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.READ_EXTERNAL_STORAGE),
            MyUtils.Per_REQUEST_READ_EXTERNAL_STORAGE_1
        )
    }
}

fun getCameraPermissionOther() {
    val permissionCheck = checkSelfPermission(permission.CAMERA)
    val permissionCheck1 = checkSelfPermission(permission.RECORD_AUDIO)

    if (permissionCheck == PackageManager.PERMISSION_GRANTED && permissionCheck1 == PackageManager.PERMISSION_GRANTED) {
//            getFlashCamera()
        GetImageGalleryCameraBottomSheet()
    } else {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission.CAMERA, permission.RECORD_AUDIO),
            MyUtils.Per_REQUEST_CAMERA_1
        )
    }
}


fun GetImageGalleryCameraBottomSheet() {
    startActivity(Intent(this@MainActivity, CameraActivityKotlin::class.java))
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    @NonNull permissions: Array<String>,
    @NonNull grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
        MyUtils.Per_REQUEST_WRITE_EXTERNAL_STORAGE_1 ->
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getReadStoragePermissionOther()
            } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Snackbar.make(
                    mDrawerLayout,
                    resources.getString(R.string.grant_access_camera),
                    Snackbar.LENGTH_LONG
                ).setAction(
                    "Setting"
                ) {
                    val i = Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                    startActivity(i)
                }.show()
            } else {
                getWriteStoragePermissionOther()
            }
        MyUtils.Per_REQUEST_READ_EXTERNAL_STORAGE_1 ->
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GetImageGalleryCameraBottomSheet()
            } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Snackbar.make(
                    mDrawerLayout,
                    resources.getString(R.string.grant_access_camera),
                    Snackbar.LENGTH_LONG
                ).setAction(
                    "Setting"
                ) {
                    val i = Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                    startActivity(i)
                }.show()
            } else {
                getReadStoragePermissionOther()
            }
        REQUEST_CODE_Location_PERMISSIONS -> {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //openNextActivity(locationCity)
                    getCurrentLocation()
                } else {
                    Snackbar.make(
                        mDrawerLayout,
                        resources.getString(R.string.grant_access_camera),
                        Snackbar.LENGTH_LONG
                    ).setAction(
                        "Setting"
                    ) {
                        val i = Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                        )
                        startActivity(i)
                    }.show()

                }
            }
        }
        MyUtils.Per_REQUEST_CAMERA_1 -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                GetImageGalleryCameraBottomSheet()
            } else {
                Snackbar.make(
                    mDrawerLayout,
                    resources.getString(R.string.grant_access_camera),
                    Snackbar.LENGTH_LONG
                ).setAction(
                    "Setting"
                ) {
                    val i = Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                    startActivity(i)
                }.show()

            }
        }
    }
}


override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
) {
    super.onActivityResult(requestCode, resultCode, data)

    val fragment = supportFragmentManager.findFragmentById(R.id.container)
    fragment!!.onActivityResult(requestCode, resultCode, data)
    val picturePath = ""
    when (requestCode) {
        1312 -> if (resultCode == RESULT_OK && requestCode == 1312) {
            if (mfUser != null) {
                GetCameraTypedata(mfUser!!, "video")
            } else {
                showSnackBar("Please select valid data")
            }
        }
        1311 -> if (resultCode == RESULT_OK && requestCode == 1311) {
            if (data == null || data.data == null) {
                showSnackBar("Please select valid data")
            } else {
            }
        }
        1211 -> if (resultCode == RESULT_OK) {
            if (data != null && data.data != null) {
                val selectedMediaUri = data.data
                val columns = arrayOf(
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.MIME_TYPE
                )
                val cursor =
                    contentResolver.query(selectedMediaUri!!, columns, null, null, null)
                cursor!!.moveToFirst()

                val pathColumnIndex = cursor.getColumnIndex(columns[0])
                val mimeTypeColumnIndex = cursor.getColumnIndex(columns[1])
                val contentPath = cursor.getString(pathColumnIndex)

                val mimeType = cursor.getString(mimeTypeColumnIndex)

                cursor.close()
                numberOfImage = ArrayList()
                numberOfImage?.clear()
                if (data.clipData != null) {
                    for (i in 0 until data.clipData?.itemCount!!) {
                        numberOfImage?.add(
                            AddImages(
                                "",
                                false,
                                data.clipData?.getItemAt(i)!!.uri, -1,
                                "", false, "1"
                            )
                        )
                    }

                } else {
                    numberOfImage?.add(
                        AddImages(
                            "",
                            false,
                            selectedMediaUri, -1,
                            "", false, "1"
                        )
                    )
                }

                if (mimeType.toString().contains("image")) { //handle image
                    // GetGalleryTypedata(data, "Photo")
                    Intent(this@MainActivity, CreatePostActivity::class.java).apply {
                        putExtra("from", "images")
                        putExtra("imagesArray", numberOfImage)
                        startActivity(this)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                } else if (mimeType.toString().contains("video")) { //handle video
                    //GetGalleryTypedata(data, "Video")
                    Intent(this@MainActivity, CreatePostActivity::class.java).apply {
                        putExtra("from", "video")
                        putExtra("imagesArray", numberOfImage)
                        startActivity(this)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }

            } else {
                showSnackBar("Please select valid data")


            }
        }
        1212 -> if (resultCode == RESULT_OK) {
            if (mfUser != null) {
                GetCameraTypedata(mfUser!!, "image")
            } else {
                showSnackBar("Please select valid data")
            }
        }
        REQUEST_UPDATE_CODE -> {
            if (resultCode != RESULT_OK) {
                // If the update is cancelled or fails, you can request to start the update again.
                Log.e(MainActivity.TAG, "Update flow failed! Result code: $resultCode")
            }
        }
    }
}


private fun GetGalleryTypedata(data: Intent, Tye: String) {
    var picturePath: String? = ""
    try {
        val uri = data.data
        var fileName: String? = ""
        fileName = MyUtils.createFileName(Date(), Tye)
        mImageCaptureUri = uri
        picturePath = MyUtils.getFilePathFromURI(
            this@MainActivity,
            mImageCaptureUri!!,
            fileName
        )
        if (picturePath != null) {
            if (picturePath.contains("https:")) {
                showSnackBar(
                    this@MainActivity.resources.getString(R.string.please_select_another_pic)
                )
            } else {
                mfAddRecipieStep01_Image = File(picturePath)
                if (Tye.equals("Video", ignoreCase = true)) {
                    fileType = false
                    try {
                        val bmThumbnail = ThumbnailUtils.createVideoThumbnail(
                            picturePath,
                            Video.Thumbnails.MINI_KIND
                        )
                        val tempFile = File(this.cacheDir, "a.jpg")
                        val stream =
                            ByteArrayOutputStream()
                        bmThumbnail?.compress(CompressFormat.JPEG, 100, stream)
                        val byteArray = stream.toByteArray()
                        val out =
                            FileOutputStream(tempFile.path)
                        out.write(byteArray)
                        out.close()
                    } catch (e: java.lang.Exception) {
                    }

                } else {

                    fileType = true
                }

            }
        } else {
            showSnackBar(
                this@MainActivity.resources.getString(R.string.please_select_another_imagefile)
            )
        }
    } catch (e: Resources.NotFoundException) {
        e.printStackTrace()
    }
}


private fun GetCameraTypedata(mUser: File, Tye: String) {
    var picturePath: String? = ""
    try {
        picturePath = mUser.absolutePath
        mfAddRecipieStep01_Image =
            File(picturePath)
        numberOfImage = ArrayList()
        numberOfImage?.clear()

        numberOfImage?.add(
            AddImages(
                "",
                false,
                Uri.parse(mfAddRecipieStep01_Image?.toString()), -1,
                "", false, "1"
            )
        )
        if (Tye.toString().contains("image")) { //handle image
            Intent(this@MainActivity, CreatePostActivity::class.java).apply {
                putExtra("from", "images")
                putExtra("imagesArray", numberOfImage)
                startActivity(this)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        } else if (Tye.toString().contains("video")) { //handle video
            try {
                Intent(this@MainActivity, CreatePostActivity::class.java).apply {
                    putExtra("from", "video")
                    putExtra("imagesArray", numberOfImage)
                    startActivity(this)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        fileType = !Tye.equals("Video", ignoreCase = true)

        // UploadImageORVideo(picturePath);
    } catch (e: Resources.NotFoundException) {
        e.printStackTrace()
    }
}

private fun showDilaogPostReport() {

    val dialogs =
        MaterialAlertDialogBuilder(this@MainActivity, R.style.ThemeOverlay_MyApp_Dialog)

    //   dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialogs.setCancelable(true)

    val dialogView = layoutInflater.inflate(R.layout.item_photo_video, null)
    dialogs.setView(dialogView)
    val alertDialog = dialogs.create()

    val tv_Gallery_Images = dialogView.findViewById<AppCompatTextView>(R.id.tv_Gallery_Images)
    val tv_Gallery_Videos = dialogView.findViewById<AppCompatTextView>(R.id.tv_Gallery_Videos)

    tv_Gallery_Images.setOnClickListener {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, 1211)
    }
    tv_Gallery_Videos.setOnClickListener {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        startActivityForResult(intent, 1211)
    }

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

override fun onDestroy() {
    super.onDestroy()
    LocalBroadcastManager.getInstance(this@MainActivity)
        .unregisterReceiver(mYourBroadcastReceiver)
    /* LocalBroadcastManager.getInstance(this@MainActivity)
         .unregisterReceiver(mGpsSwitchStateReceiver)*/
}

private val REQUEST_CODE_Location_PERMISSIONS = 6

@RequiresApi(Build.VERSION_CODES.M)
fun permissionLocation() {
  /*  if (!addPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
        val message = getString(R.string.grant_access_location)

        MyUtils.showMessageOKCancel(this@MainActivity, message, "Use location service?",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_Location_PERMISSIONS
                )
            })
    } else {
        //openNextActivity()
        getCurrentLocation()
    }*/

}

fun getCurrentLocation() {
    locationProvider = LocationProvider(
        this@MainActivity,
        LocationProvider.HIGH_ACCURACY,
        object : LocationProvider.CurrentLocationCallback {
            override fun handleNewLocation(location: Location) {

              //  Log.d("currentLocation", location.toString())

                locationProvider?.disconnect()
//                    MyUtils.currentLattitude=location.latitude
//                    MyUtils.currentLongitude=location.longitude

                MyUtils.currentLattitudeFix = location.latitude
                MyUtils.currentLongitudeFix = location.longitude


                MyUtils.hideKeyboard1(this@MainActivity)
                if (getCurrentFragment() is FeedFragment) {
                    MyUtils.currentLattitude = location.latitude
                    MyUtils.currentLongitude = location.longitude
                    CurrentCityName(location.latitude, location.longitude)

                    (getCurrentFragment() as FeedFragment).pageNo = 0
                  //  (getCurrentFragment() as FeedFragment).getPostList()
                    (getCurrentFragment() as FeedFragment).setHeaderText()
                } else if (getCurrentFragment() is MapFragment) {
                    MyUtils.currentLattitude = location.latitude
                    MyUtils.currentLongitude = location.longitude
                    CurrentCityName(location.latitude, location.longitude)
                 //   (getCurrentFragment() as MapFragment).getPostList("main")
                    (getCurrentFragment() as MapFragment).setHeaderText()
                }
            }
        })


    locationProvider!!.connect()
}

private fun addPermission(permission: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (checkSelfPermission(permission) !== PackageManager.PERMISSION_GRANTED) {
            return false
        }

    }

    return true
}

private fun CurrentCityName(lattitude: Double, longitude: Double) {
    val geocoder: Geocoder
    var addresses: List<Address>? = null
    geocoder = Geocoder(this@MainActivity, Locale.getDefault())

    try {
        addresses = geocoder.getFromLocation(
            lattitude,
            longitude,
            1
        )
        if (!addresses.isNullOrEmpty()) {
            MyUtils.locationCityName =
                addresses[0].locality + ", " + addresses[0].adminArea + ", " + addresses[0].countryName
            MyUtils.locationCityNameFix =
                addresses[0].locality + ", " + addresses[0].adminArea + ", " + addresses[0].countryName
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

}


override fun notifyData(
    feedDatum: TrendingFeedData?,
    isDelete: Boolean,
    isDeleteComment: Boolean,
    postComment: String?
) {


    val frag: FeedFragment? = supportFragmentManager.findFragmentByTag(
        FeedFragment::class.java.name
    ) as FeedFragment?

    if (frag != null) {
        if (feedDatum != null)
            frag.notifyData(feedDatum, isDelete, isDeleteComment, postComment!!)
    }

    val frag3: TrendingFeedFragment? = supportFragmentManager.findFragmentByTag(
        TrendingFeedFragment::class.java.name
    ) as TrendingFeedFragment?
    if (frag3 != null) {
        if (feedDatum != null)
            frag3.notifyData(feedDatum, isDelete, isDeleteComment, postComment!!)
    }


    val frag1: ImagesDetailsFragment? = supportFragmentManager.findFragmentByTag(
        ImagesDetailsFragment::class.java.name
    ) as ImagesDetailsFragment?

    if (frag1 != null) {
        if (feedDatum != null)
            frag1.notifyData(feedDatum, isDelete, isDeleteComment)
    }

    val frag2: VideoDetailsFragment? = supportFragmentManager.findFragmentByTag(
        VideoDetailsFragment::class.java.name
    ) as VideoDetailsFragment?
    if (frag2 != null) {
        if (feedDatum != null)
            frag2.notifyData(feedDatum, isDelete, isDeleteComment)
    }

    /* val frag4: ProfileDetailFragment? = supportFragmentManager.findFragmentByTag(
         ProfileDetailFragment::class.java.name
     ) as ProfileDetailFragment?
     if (frag4 != null) {
         if (feedDatum != null)
             frag4.notifyData(feedDatum, isDelete, isDeleteComment)
     }*/


}

override fun ProfileNotifyData(postId: String, userId: String) {
    val frag: FeedFragment? = supportFragmentManager.findFragmentByTag(
        FeedFragment::class.java.name
    ) as FeedFragment?

    if (frag != null) {

        frag.notifyProfileData(postId, userId)
    }
    val frag3: TrendingFeedFragment? = supportFragmentManager.findFragmentByTag(
        TrendingFeedFragment::class.java.name
    ) as TrendingFeedFragment?
    if (frag3 != null) {
        frag3.notifyProfileData(postId, userId)
    }
}

override fun onResume() {

    super.onResume()

    appUpdateManager?.appUpdateInfo?.addOnSuccessListener(
        playServiceExecutor!!,
        OnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED)
                    updaterDownloadCompleted()
            } else {
                // for AppUpdateType.IMMEDIATE only
                // already executing updater
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager?.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        REQUEST_UPDATE_CODE
                    )
                }
            }
        })
}


private fun updateChecker() {

    appUpdateManager = AppUpdateManagerFactory.create(this)
    playServiceExecutor = TaskExecutors.MAIN_THREAD
    installStateUpdatedListener = InstallStateUpdatedListener { installState ->
        when (installState.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                Log.d(MainActivity.TAG, "Downloaded")
                updaterDownloadCompleted()
            }
            InstallStatus.INSTALLED -> {
                Log.d(MainActivity.TAG, "Installed")
                appUpdateManager?.unregisterListener(installStateUpdatedListener!!)
            }
            else -> {
                Log.d(MainActivity.TAG, "installStatus = " + installState.installStatus())
            }
        }
    }
    appUpdateManager?.registerListener(installStateUpdatedListener!!)

    val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
    appUpdateInfoTask?.addOnSuccessListener(
        playServiceExecutor!!,
        OnSuccessListener { appUpdateInfo ->
            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    val updateTypes = arrayOf(AppUpdateType.FLEXIBLE, AppUpdateType.IMMEDIATE)
                    run loop@{
                        updateTypes.forEach { type ->
                            if (appUpdateInfo.isUpdateTypeAllowed(type)) {
                                appUpdateManager?.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    type,
                                    this,
                                    REQUEST_UPDATE_CODE
                                )
                                return@loop
                            }
                        }
                    }
                }
                else -> {
                    Log.d(
                        MainActivity.TAG,
                        "updateAvailability = " + appUpdateInfo.updateAvailability()
                    )
                }
            }
        })
}

private fun updaterDownloadCompleted() {

    Snackbar.make(
        findViewById(R.id.rootSplash),
        "An update has just been downloaded.",
        Snackbar.LENGTH_INDEFINITE
    ).apply {
        setAction("RESTART") { appUpdateManager?.completeUpdate() }
        show()
    }
}


fun initNavigation() {
    drawer_view = LayoutInflater.from(this).inflate(R.layout.navigation_view, null)

    drawer_view?.llHome?.setOnClickListener {
        mDrawerLayout.closeDrawer(Gravity.LEFT)

        var mapFragment = MapFragment()

        navigateTo(
            mapFragment,
            mapFragment::class.java.name,
            true
        )
    }

    drawer_view!!.llProfil.setOnClickListener {
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        val currentFargment = getCurrentFragment()
        if (sessionManager?.isLoggedIn()!!) {
            if (currentFargment !is ProfileDetailFragment) {
                top_sheet.visibility = View.GONE


/*Bundle().apply {
                                    putString("userID", userData?.userID)
                                    profileDetailFragment.arguments = this
                                }*/
                if (sessionManager?.isLoggedIn()!!) {
                    var profileDetailFragment = ProfileDetailFragment()

                    navigateTo(
                        profileDetailFragment,
                        profileDetailFragment::class.java.name,
                        true
                    )
                } else {
                    MyUtils.startActivity(
                        this@MainActivity,
                        LoginActivity::class.java,
                        false
                    )

                }
            }
        } else {
            MyUtils.startActivity(this@MainActivity, LoginActivity::class.java, false)
        }

    }

    drawer_view!!.llPrivacyPolicy.setOnClickListener {
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        val currentFargment = getCurrentFragment()
        if (sessionManager?.isLoggedIn()!!) {
            if (currentFargment !is PrivacyPolicyFragment) {
                top_sheet.visibility = View.GONE


/*Bundle().apply {
                                    putString("userID", userData?.userID)
                                    profileDetailFragment.arguments = this
                                }*/
                if (sessionManager?.isLoggedIn()!!) {
                    var PrivacyPolicyFragment =
                        PrivacyPolicyFragment()

                    navigateTo(
                        PrivacyPolicyFragment,
                        PrivacyPolicyFragment::class.java.name,
                        true
                    )
                } else {
                    MyUtils.startActivity(
                        this@MainActivity,
                        LoginActivity::class.java,
                        false
                    )

                }
            }
        } else {
            MyUtils.startActivity(this@MainActivity, LoginActivity::class.java, false)
        }

    }

    drawer_view!!.llCondition.setOnClickListener {
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        val currentFargment = getCurrentFragment()
        if (sessionManager?.isLoggedIn()!!) {
            if (currentFargment !is TermsConditionsFramgment) {
                top_sheet.visibility = View.GONE


/*Bundle().apply {
                                    putString("userID", userData?.userID)
                                    profileDetailFragment.arguments = this
                                }*/
                if (sessionManager?.isLoggedIn()!!) {
                    var TermsConditionsFramgment =
                        TermsConditionsFramgment()

                    navigateTo(
                        TermsConditionsFramgment,
                        TermsConditionsFramgment::class.java.name,
                        true
                    )
                } else {
                    MyUtils.startActivity(
                        this@MainActivity,
                        LoginActivity::class.java,
                        false
                    )

                }
            }
        } else {
            MyUtils.startActivity(this@MainActivity, LoginActivity::class.java, false)
        }

    }


    drawer_view!!.llTrendingPost.setOnClickListener {
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        val currentFargment = getCurrentFragment()

        if (currentFargment !is TrendingFeedFragment) {
            top_sheet.visibility = View.GONE
            navigateTo(
                TrendingFeedFragment(),
                TrendingFeedFragment::class.java.name,
                true
            )
        }

    }

    drawer_view!!.llAddPost.setOnClickListener {
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        if (sessionManager?.isLoggedIn()!!) {
            if (userData?.userVerified.equals("Yes", false)) {
                val currentapiVersion = Build.VERSION.SDK_INT

                if (currentapiVersion >= Build.VERSION_CODES.M) {
                    getWriteStoragePermissionOther()
                } else {
                    GetImageGalleryCameraBottomSheet()
                }
            } else {
                showSnackBar(resources.getString(R.string.veify_mobile_number))
                navigateTo(SaveProfileFragment(), SaveProfileFragment::class.java.name, true)
            }
        } else {
            MyUtils.startActivity(applicationContext, LoginActivity::class.java, false)
        }

    }

    drawer_view!!.llFavourite.setOnClickListener {
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        val currentFargment = getCurrentFragment()

        if (currentFargment !is FeedFragment) {
            top_sheet.visibility = View.GONE

            navigateTo(FeedFragment(), FeedFragment::class.java.name, true)
        }

    }

    drawer_view!!.llSettings.setOnClickListener {
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        val currentFargment = getCurrentFragment()

        if (currentFargment !is SettingsFragment) {
            top_sheet.visibility = View.GONE

            navigateTo(SettingsFragment(), SettingsFragment::class.java.name, true)
        }
    }

    drawer_view!!.llSettings.setOnClickListener {
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        val currentFargment = getCurrentFragment()

        if (currentFargment !is SettingsFragment) {
            top_sheet.visibility = View.GONE

            navigateTo(SettingsFragment(), SettingsFragment::class.java.name, true)
        }
    }


    drawer_view!!.llSignOut.setOnClickListener {
        mDrawerLayout.closeDrawer(Gravity.LEFT)
        if (sessionManager?.isLoggedIn()!!) {
            sessionManager?.create_login_session("", "", "", false, false, false, "", "")
            MyUtils.startActivity(this@MainActivity, LoginActivity::class.java, true)
        } else {
            MyUtils.startActivity(this@MainActivity, LoginActivity::class.java, true)

        }
    }
    setProfileData()

    navView.addView(drawer_view)

}

    fun setProfileData() {
        if (sessionManager?.isLoggedIn()!!) {
            drawer_view!!.llSignOut.visibility = View.VISIBLE
            drawer_view!!.tx_name.text = sessionManager!!.get_UserName().toString()
            drawer_view!!.txUserId.text = sessionManager!!.get_Email().toString()
            drawer_view!!.img_profile!!.setImageURI(RestClient.image_base_url_users + sessionManager!!.get_Authenticate_User().userProfilePicture)

        } else {
            drawer_view!!.llSignOut.visibility = View.GONE

            drawer_view!!.tx_name.text = "-"
            drawer_view!!.txUserId.text = "-"
        }
    }
}