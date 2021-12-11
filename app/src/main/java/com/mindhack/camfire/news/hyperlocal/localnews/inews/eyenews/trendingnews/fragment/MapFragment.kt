package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.CameraActivityKotlin
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.LoginActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.clusterRenderer.MarkerClusterRenderer
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.HashTagPostListModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.Person
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.msgButton
import kotlinx.android.synthetic.main.header_icon_text.*
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.model.BitmapDescriptor
import com.prembros.googlemapsripples.lib.MapRipple


/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    var v: View? = null
    var mActivity: Activity? = null
    var mClusterManager: ClusterManager<Person>? = null
    var sessionManager: SessionManager? = null
    var userData: RegisterPojo.Data? = null
    var currentLocation: Location? = null
    private var mMap1: GoogleMap? = null
    var mapFragment: MapView? = null
    var hashTagPostListModel: HashTagPostListModel? = null

    var feedList: ArrayList<TrendingFeedData?>? = null
    var radius: Float = 0f
    var from = ""
    var flagZoom = 0

    var mapRipple : MapRipple?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_map, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        (activity as MainActivity).showHideBottomNavigation(true)
//        (activity as MainActivity).updateNavigationBarState(R.id.menu_map_view)

        setHeaderText()

//        msgButton?.text =
//            GetDynamicStringDictionaryObjectClass?.Please_check_your_internet_connectivity_and_try_again


        val width = rootHeaderLayout.width
        val height = MyUtils.getViewHeight(rootHeaderLayout)

        tv_selectLocation?.setOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)

            (activity as MainActivity).top_sheet.visibility = View.GONE
            //  if (sessionManager?.isLoggedIn()!!) {
            (activity as MainActivity).openTopSheet(width, height)
            /* } else {

                 MyUtils.startActivity(mActivity!!, LoginActivity::class.java, true)

             }*/
        }
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
        imgCloseIcon.setOnClickListener {
            (mActivity as MainActivity).mDrawerLayout.openDrawer(Gravity.LEFT)
        }

        imgCloseIcon1.setOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)

            (activity as MainActivity).top_sheet.visibility = View.GONE
            /* if(sessionManager?.isLoggedIn()!!)
             {*/
            var globalSearchFragment = GlobalSearchFragment()
            (activity as MainActivity).navigateTo(
                globalSearchFragment,
                globalSearchFragment::class.java.name,
                true
            )
            /* }
             else
             {
                 MyUtils.startActivity(mActivity!!,LoginActivity::class.java,true)
             }*/

        }

        sessionManager = SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager!!.isLoggedIn())
            userData = sessionManager?.get_Authenticate_User()

        mapFragment = v!!.findViewById(R.id.map) as MapView


        mapFragment?.onCreate(savedInstanceState)
        noDatafoundRelativelayout.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        relativeprogressBar.visibility = View.GONE

        map_frame.visibility = View.VISIBLE

        mapFragment?.onResume()
        mapFragment?.getMapAsync(this)

        hashTagPostListModel =
            ViewModelProviders.of(this@MapFragment).get(HashTagPostListModel::class.java)



        try {
            var location = Location("current")
            location.latitude = MyUtils.currentLattitude
            location.longitude = MyUtils.currentLongitude
            currentLocation = location

        } catch (e: Exception) {
        }

        ivAddPost.setOnClickListener {
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
                    (activity as MainActivity).navigateTo(SaveProfileFragment(), SaveProfileFragment::class.java.name, true)
                }
            } else {
                MyUtils.startActivity(activity as MainActivity, LoginActivity::class.java, false)
            }
        }

        centerMapFab.setOnClickListener {
            centerMap()
        }
    }

    fun showSnackBar(message: String) {

        if ((mDrawerLayout != null) and !(activity as MainActivity).isFinishing)
            Snackbar.make(this.mDrawerLayout!!, message, Snackbar.LENGTH_LONG).show()

    }


    fun GetImageGalleryCameraBottomSheet() {
        startActivity(Intent( activity as MainActivity, CameraActivityKotlin::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getWriteStoragePermissionOther() {
        val permissionCheck = activity?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getReadStoragePermissionOther()
        } else {
            ActivityCompat.requestPermissions(
                activity as MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MyUtils.Per_REQUEST_WRITE_EXTERNAL_STORAGE_1
            )
        }
    }

    fun getReadStoragePermissionOther() {
        val permissionCheck = activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getCameraPermissionOther()
        } else {
            ActivityCompat.requestPermissions(
                activity as MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MyUtils.Per_REQUEST_READ_EXTERNAL_STORAGE_1
            )
        }
    }

    fun getCameraPermissionOther() {
        val permissionCheck = activity?.checkSelfPermission(Manifest.permission.CAMERA)
        val permissionCheck1 = activity?.checkSelfPermission(Manifest.permission.RECORD_AUDIO)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED && permissionCheck1 == PackageManager.PERMISSION_GRANTED) {
//            getFlashCamera()
            GetImageGalleryCameraBottomSheet()
        } else {
            ActivityCompat.requestPermissions(
                activity as MainActivity,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                MyUtils.Per_REQUEST_CAMERA_1
            )
        }
    }

    fun getPostList(s: String) {
        Log.w("Logs", "getPostList" )

        when (s) {
            "main" -> {
                from = s
                if (mMap1 != null) {
                    mMap1!!.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                MyUtils.currentLattitude,
                                MyUtils.currentLongitude
                            ), 10f
                        )
                    )
                }
            }
        }

        if (s.equals("first", false)) {
            noDatafoundRelativelayout.visibility = View.GONE
            nointernetMainRelativelayout.visibility = View.GONE
            relativeprogressBar.visibility = View.GONE
            map_frame.visibility = View.GONE

        } else {
            msgButton.visibility = View.VISIBLE
            msgButton.text = "Getting Post...."
            map_frame.visibility = View.VISIBLE
        }


        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            if (sessionManager?.isLoggedIn()!!) {
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put("languageID", userData?.languageID)
            } else {
                jsonObject.put("loginuserID", "0")
                jsonObject.put("languageID", sessionManager?.getsetSelectedLanguage())
            }
            jsonObject.put("postLatitude", MyUtils.currentLattitude)
            jsonObject.put("postLongitude", MyUtils.currentLongitude)
            jsonObject.put("sortingwith", "Recent")
            jsonObject.put("searchWord", MyUtils.locationCityName.trim())
            jsonObject.put("page", "0")
            jsonObject.put("radius", "50")

            jsonObject.put("pagesize", "200")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        hashTagPostListModel?.apiFunction(mActivity!!, false, jsonArray.toString(), "location")
            ?.observe(this@MapFragment,
                androidx.lifecycle.Observer
                { trendingFeedDatum ->
                    if (trendingFeedDatum != null && trendingFeedDatum.isNotEmpty()) {
                        //   remove progress item
                        if (s.equals("first", false)) {
                            noDatafoundRelativelayout.visibility = View.GONE
                            nointernetMainRelativelayout.visibility = View.GONE
                            relativeprogressBar.visibility = View.GONE
                            map_frame.visibility = View.VISIBLE
                            msgButton.visibility = View.GONE

                        }
                        msgButton.visibility = View.GONE
                        if (trendingFeedDatum[0].status.equals("true")) {
                            feedList!!.clear()
                            feedList!!.addAll(trendingFeedDatum[0].data)
                            mMap1?.clear()
                            mapFragment?.onResume()
                            mapFragment?.getMapAsync(this)
                        } else {
                            relativeprogressBar.visibility = View.GONE
                            mMap1?.clear()
                            if (feedList?.size == 0) {
                                if (s.equals("first", false)) {
                                    map_frame.visibility = View.GONE
                                }
                                msgButton.text = "Not found post..."
                                msgButton.visibility = View.VISIBLE

                            } else {
                                if (s.equals("first", false)) {
                                    map_frame.visibility = View.GONE
                                }
                                msgButton.text = "Not found post..."
                                msgButton.visibility = View.VISIBLE
                            }
                        }

                    } else {
                        if (activity != null) {
                            if (s.equals("first", false)) {
                                relativeprogressBar.visibility = View.GONE
                                map_frame.visibility = View.VISIBLE
                            }
                            try {
                                nointernetMainRelativelayout.visibility = View.GONE
                                if (MyUtils.isInternetAvailable(activity!!)) {
                                    msgButton.text = (activity!!.getString(R.string.somethigwrong1))
                                    msgButton.visibility = View.VISIBLE
                                } else {
                                    msgButton.text =
                                        (activity!!.getString(R.string.error_common_network))
                                    msgButton.visibility = View.VISIBLE
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    }
                })
    }

    override fun onMapReady(mMap: GoogleMap?) {
        Log.w("Logs", "onMapReady" )

        this.mMap1 = mMap
        val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(activity, R.raw.style_json)
        this.mMap1?.setMapStyle(mapStyleOptions)


        val sydney = LatLng(MyUtils.currentLattitude, MyUtils.currentLongitude)





        this.mMap1?.addMarker(
            MarkerOptions()
                .title("My Location") // below line is use to add custom marker on our map.
                .icon(BitmapFromVector(activity!!.applicationContext, R.drawable.new_marker)).position(sydney)

        )

        mapRipple = MapRipple(mMap!!, sydney, activity!!.applicationContext)
        mapRipple!!.withNumberOfRipples(3)
        mapRipple!!.withFillColor(activity!!.resources.getColor(R.color.selectedMapColor))
        mapRipple!!.withStrokeColor(Color.TRANSPARENT)
        mapRipple!!.withDistance(2000.0)            // 2000 metres radius
        mapRipple!!.withRippleDuration(8000)     //12000ms
        mapRipple!!.withTransparency(0.5f)
        mapRipple!!.startRippleMapAnimation()
        mapRipple!!.withRepeatMode(ValueAnimator.RESTART)
//        withFadingOutRipple(true)

        when (from) {
            "main" -> {
                if (mMap1 != null) {
                    mMap1!!.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                MyUtils.currentLattitude,
                                MyUtils.currentLongitude
                            ), 15f
                        )
                    )
                }
            }
            else -> {
                mMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            currentLocation?.latitude!!,
                            currentLocation?.longitude!!
                        ), 15f
                    )
                )
            }
        }

        setupMap(mMap)

        val bounds: LatLngBounds = mMap1?.projection!!.visibleRegion.latLngBounds
        val llNeLat = bounds.northeast.latitude
        val llSwLat = bounds.southwest.latitude
        val llNeLng = bounds.northeast.longitude
        val llSwLng = bounds.southwest.longitude
        val results = FloatArray(5)

        Location.distanceBetween(llNeLat, llNeLng, llSwLat, llSwLng, results)
        radius = results[0]

        /*mMap1?.setOnCameraChangeListener {
        val bounds: LatLngBounds = mMap1?.projection!!.visibleRegion.latLngBounds
        val llNeLat = bounds.northeast.latitude
        val llSwLat = bounds.southwest.latitude
        val llNeLng = bounds.northeast.longitude
        val llSwLng = bounds.southwest.longitude
        val results = FloatArray(5)
          Location.distanceBetween(llNeLat, llNeLng, llSwLat, llSwLng, results)
          radius = results[0]
        getPostList("")
     }*/
        Log.w("Logs", "feedList == null" )

        if (feedList == null) {
            feedList = ArrayList()
//            "comment"
            getPostList("")
        }

        mClusterManager = ClusterManager<Person>(mActivity, mMap)
        val markerClusterRenderer =
            MarkerClusterRenderer(mActivity!!, mMap!!, mClusterManager!!)

        mClusterManager?.renderer = markerClusterRenderer

        mMap.setOnCameraIdleListener(mClusterManager)
        mMap.setOnMarkerClickListener(mClusterManager)
        mMap.setOnInfoWindowClickListener(mClusterManager)

        addPersonItems()
        mClusterManager?.cluster()



    }
    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable?.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun setupMap(mMap: GoogleMap?) {
        if (mMap != null) {
            return
        }
        mMap?.isBuildingsEnabled = true
        mMap?.isIndoorEnabled = true
        mMap?.isTrafficEnabled = true

        val mUiSettings = mMap?.uiSettings
        mUiSettings?.isZoomControlsEnabled = true
        mUiSettings?.isCompassEnabled = true
        mUiSettings?.isMyLocationButtonEnabled = true
        mUiSettings?.isScrollGesturesEnabled = true
        mUiSettings?.isZoomGesturesEnabled = true
        mUiSettings?.isTiltGesturesEnabled = true
        mUiSettings?.isRotateGesturesEnabled = true
        // permissions
        if (ActivityCompat.checkSelfPermission(
                mActivity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mActivity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap?.isMyLocationEnabled = true

    }

    private fun addPersonItems() {

        if (!feedList.isNullOrEmpty()) {
            for (i in feedList?.indices!!) {
                mClusterManager!!.addItem(
                    Person(
                        feedList!![i]!!.postLatitude.toDouble(),
                        feedList!![i]!!.postLongitude.toDouble(),
                        feedList!![i]!!.postHeadline,
                        feedList!![i]!!.postDescription, feedList!![i]!!
                    )
                )
            }
        }


        /*for (int i = 0; i < 3; i++) {
                 mClusterManager.addItem(new Person(23.052629, 72.524863, "PJ", "https://twitter.com/pjapplez"));
                 mClusterManager.addItem(new Person(23.046313, 72.529245, "PJ2", "https://twitter.com/pjapplez"));
                 mClusterManager.addItem(new Person(23.040045, 72.540173, "PJ3", "https://twitter.com/pjapplez"));
           }*/
/*Double lat = 23.0225;
        Double lng = 72.5714;

        for (int i=0;i<items.size();i++) {

            Double offset = i / 60.0;

            lat = lat + offset;
            lng = lng + offset;

            String title = "This is the title";
            String snippet = "and this is the snippet.";

            Person offsetItem =new Person(lat, lng, "PJ", "https://twitter.com/pjapplez");
            mClusterManager.addItem(offsetItem);
        }
*/
/*mClusterManager.addItem(new Person(-31.563910, 147.154312, "AhsenSaeed1", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-33.718234, 150.363181, "AhsenSaeed2", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-33.727111, 150.371124, "AhsenSaeed3", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-33.848588, 151.209834, "AhsenSaeed4", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-33.851702, 151.216968, "AhsenSaeed5", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-34.671264, 150.863657, "AhsenSaeed6", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-35.304724, 148.662905, "AhsenSaeed7", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-36.817685, 175.699196, "AhsenSaeed8", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-36.828611, 175.790222, "AhsenSaeed9", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-37.750000, 145.116667, "AhsenSaeed10", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-37.759859, 145.128708, "AhsenSaeed11", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-37.765015, 145.133858, "AhsenSaeed12", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-37.770104, 145.143299, "AhsenSaeed13", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-37.773700, 145.145187, "AhsenSaeed14", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-37.774785, 145.137978, "AhsenSaeed15", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-37.819616, 144.968119, "AhsenSaeed16", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-38.330766, 144.695692, "AhsenSaeed17", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-39.927193, 175.053218, "AhsenSaeed18", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(41.330162, 174.865694, "AhsenSaeed19", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-42.734358, 147.439506, "AhsenSaeed20", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-42.734358, 147.501315, "AhsenSaeed21", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-42.735258, 147.438000, "AhsenSaeed22", "https://www.ahsensaeed.com"));
        mClusterManager.addItem(new Person(-43.999792, 170.463352, "AhsenSaeed23", "https://www.ahsensaeed.com"));
*/

    }

    /*private fun getItems(): ArrayList<Person?> {
        val list: ArrayList<Person?> = ArrayList<Person?>()

        if (!feedList.isNullOrEmpty()) {
            for (i in 0 until feedList?.size!!) {
                list.add(
                    Person(
                        feedList!![i]!!.postLatitude.toDouble(),
                        feedList!![i]!!.postLongitude.toDouble(),
                        feedList!![i]!!.postHeadline,
                        feedList!![i]!!.postDescription
                    )
                )

            }
        }*/
    /*  list.add(Person(23.046313, 72.529245, "PJ2", "https://twitter.com/pjapplez"))
      list.add(Person(23.040045, 72.540173, "PJ3", "https://twitter.com/pjapplez"))
      list.add(Person(22.982312, 72.594283, "PJ3", "https://twitter.com/pjapplez"))
      list.add(Person(22.982480, 72.598425, "PJ2", "https://twitter.com/pjapplez"))
      list.add(Person(22.980910, 72.596977, "PJ2", "https://twitter.com/pjapplez"))
      list.add(Person(22.979685, 72.598361, "PJ2", "https://twitter.com/pjapplez"))*/
    //  return list

    /*new Person(-31.563910, 147.154312, "AhsenSaeed1", "https://www.ahsensaeed.com"),
            new Person(-33.718234, 150.363181, "AhsenSaeed2", "https://www.ahsensaeed.com"),
            new Person(-33.727111, 150.371124, "AhsenSaeed3", "https://www.ahsensaeed.com"),
            new Person(-33.848588, 151.209834, "AhsenSaeed4", "https://www.ahsensaeed.com"),
            new Person(-33.851702, 151.216968, "AhsenSaeed5", "https://www.ahsensaeed.com"),
            new Person(-34.671264, 150.863657, "AhsenSaeed6", "https://www.ahsensaeed.com"),
            new Person(-35.304724, 148.662905, "AhsenSaeed7", "https://www.ahsensaeed.com"),
            new Person(-36.817685, 175.699196, "AhsenSaeed8", "https://www.ahsensaeed.com"),
            new Person(-36.828611, 175.790222, "AhsenSaeed9", "https://www.ahsensaeed.com"),
            new Person(-37.750000, 145.116667, "AhsenSaeed10", "https://www.ahsensaeed.com"),
            new Person(-37.759859, 145.128708, "AhsenSaeed11", "https://www.ahsensaeed.com"),
            new Person(-37.765015, 145.133858, "AhsenSaeed12", "https://www.ahsensaeed.com"),
            new Person(-37.770104, 145.143299, "AhsenSaeed13", "https://www.ahsensaeed.com"),
            new Person(-37.773700, 145.145187, "AhsenSaeed14", "https://www.ahsensaeed.com"),
            new Person(-37.774785, 145.137978, "AhsenSaeed15", "https://www.ahsensaeed.com"),
            new Person(-37.819616, 144.968119, "AhsenSaeed16", "https://www.ahsensaeed.com"),
            new Person(-38.330766, 144.695692, "AhsenSaeed17", "https://www.ahsensaeed.com"),
            new Person(-39.927193, 175.053218, "AhsenSaeed18", "https://www.ahsensaeed.com"),
            new Person(41.330162, 174.865694, "AhsenSaeed19", "https://www.ahsensaeed.com"),
            new Person(-42.734358, 147.439506, "AhsenSaeed20", "https://www.ahsensaeed.com"),
            new Person(-42.734358, 147.501315, "AhsenSaeed21", "https://www.ahsensaeed.com"),
            new Person(-42.735258, 147.438000, "AhsenSaeed22", "https://www.ahsensaeed.com"),
            new Person(-43.999792, 170.463352, "AhsenSaeed23", "https://www.ahsensaeed.com")*/
    //  }


    private fun centerMap() {
        Log.w("SagarSagar","centerMap")
        Log.w("SagarSagar","centerMap"+currentLocation?.latitude)
        Log.w("SagarSagar","centerMap"+currentLocation?.longitude)
        if(flagZoom==0){
            flagZoom=1
            if (currentLocation != null && mMap1 != null) {
            mMap1!!.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        currentLocation?.latitude!!,
                        currentLocation?.longitude!!
                    ), 16f
                )
            )
                mapRipple!!.withDistance(2000.0)            // 2000 metres radius

            }}else{
            flagZoom=0
            if (currentLocation != null && mMap1 != null) {
                mMap1!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            currentLocation?.latitude!!,
                            currentLocation?.longitude!!
                        ), 15f
                    )
                )
            }
            mapRipple!!.withDistance(2000.0)            // 2000 metres radius
        }
    }

    fun setHeaderText() {
        if (MyUtils.locationCityName.isNullOrEmpty()) {
            val currentapiVersion = Build.VERSION.SDK_INT
            if (currentapiVersion >= Build.VERSION_CODES.M) {
                (activity as MainActivity).permissionLocation()
            }
        } else {
            tv_selectLocation.text = MyUtils.locationCityName

        }
    }

    fun getRadius1(): Float {

        val bounds: LatLngBounds = mMap1?.projection!!.visibleRegion.latLngBounds
        val llNeLat = bounds.northeast.latitude
        val llSwLat = bounds.southwest.latitude
        val llNeLng = bounds.northeast.longitude
        val llSwLng = bounds.southwest.longitude

        val results = FloatArray(5)

        Location.distanceBetween(llNeLat, llNeLng, llSwLat, llSwLng, results)
        radius = results[0]

        return radius
    }

    override fun onStop() {
        super.onStop()
//        if (mapRipple!!.isAnimationRunning()) {
//            mapRipple!!.stopRippleMapAnimation()
//        }
    }
}
