package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.LocationProvider
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.BuildConfig
import kotlinx.android.synthetic.main.activity_splesh.*
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class SplashActivity : AppCompatActivity() {

    var sessionManager: SessionManager? = null
    private var locationProvider: LocationProvider? = null
    private val REQUEST_CODE_Location_PERMISSIONS = 6
    var locationCity = ""

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )


        setContentView(R.layout.activity_splesh)

        sessionManager = SessionManager(this@SplashActivity)

        getVersionInfo()

        printHasyKeyFb()


        val currentapiVersion = Build.VERSION.SDK_INT

        Log.w("SagaSagar","openNextActivity1");


        if (currentapiVersion >= Build.VERSION_CODES.M) {

            permissionLocation()
        } else {
            openNextActivity(locationCity)
        }


    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun permissionLocation() {

        if (!addPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            val message = getString(R.string.grant_access_location)

            MyUtils.showMessageOK(this@SplashActivity, "OK", "Use location service?", message,
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
        }

    }

    private fun addPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) !== PackageManager.PERMISSION_GRANTED) {
                return false
            }

        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_Location_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //openNextActivity(locationCity)
                        getCurrentLocation()
                    } else {
                        val message = "You need to grant access location permission through setting"

                        MyUtils.showMessageOK(this@SplashActivity,
                            "SETTING",
                            "Use location service?",
                            message,
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                val i = Intent(
                                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                                )
                                startActivity(i)
                            })
                        /*  Snackbar.make(
                              rootSplash,
                              R.string.read_location_permissions_senied,
                              Snackbar.LENGTH_LONG
                          ).setAction(
                              "Retry"
                          ) {
                              val i = Intent(
                                  android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                  Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                              )
                              startActivity(i)
                          }.show()*/

                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        when (requestCode) {

            LocationProvider.CONNECTION_FAILURE_RESOLUTION_REQUEST -> {
                getCurrentLocation()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }

    fun getCurrentLocation() {
        Log.w("SagaSagar","getCurrentLocation");

        locationProvider = LocationProvider(
            this@SplashActivity,
            LocationProvider.HIGH_ACCURACY,
            object : LocationProvider.CurrentLocationCallback {
                override fun handleNewLocation(location: Location) {

                    Log.d("currentLocation", location.toString())

                    locationProvider?.disconnect()

                    MyUtils.currentLattitude = location.latitude
                    MyUtils.currentLongitude = location.longitude

                    MyUtils.currentLattitudeFix = location.latitude
                    MyUtils.currentLongitudeFix = location.longitude

                    CurrentCityName(location.latitude, location.longitude)

                    openNextActivity(locationCity)

                    // if driver is Online location send to server
                }

            })

        locationProvider!!.connect()
//            start
        openNextActivity("")

    }

    /* private fun storeSessionManager(response: List<MasterPojo>) {
         val gson = Gson()
         val json = gson.toJson(response[0])
         PrefDb(this@SplashActivity!!).set_Mastersession(
             json)
     }*/


    fun openNextActivity(locationCity: String) {
        Log.w("SagaSagar","openNextActivity");

        if (sessionManager!!.isLoggedIn()) {
            if (!sessionManager?.get_Authenticate_User()!!.userMentionID.isNullOrEmpty() && !sessionManager?.get_Authenticate_User()!!.userMentionID.equals(
                    "@",
                    false
                )
            ) {

                if (sessionManager?.isLoggedIn()!!) {
                    Handler().postDelayed({
                        val myIntent = Intent(this@SplashActivity, MainActivity::class.java)
                        if (intent.hasExtra("Push"))
                            myIntent.putExtra("Push", intent.getSerializableExtra("Push"))
                        startActivity(myIntent)
                        finish()
                    }, 3000)
                } else {
                    Handler().postDelayed({
                        MyUtils.startActivity(this@SplashActivity, SignupActivity::class.java, false)
                        finish()
                    }, 3000)
                }


            } else {
                showSnackBar(resources.getString(R.string.please_enter_user_id))
                Handler().postDelayed({
                    val myIntent = Intent(this@SplashActivity, BeaReporterActivity::class.java)
                    startActivity(myIntent)
                    finishAffinity()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }, 3000)
            }
            Log.w("SagaSagar","openNextActivity1");

        } else {
            /*if (!PrefDb(this@SplashActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()).isNullOrEmpty()){
                Handler().postDelayed({
                    val myIntent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(myIntent)
                    finish()
                }, 3000)*/
            if (!sessionManager!!.getsetSelectedLanguage().isNullOrEmpty()) {
                Log.w("SagaSagar","if");

                if (sessionManager?.isLoggedIn()!!) {
                    Handler().postDelayed({
                        val myIntent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(myIntent)
                        finish()
                    }, 3000)
                } else {
                    Handler().postDelayed({
                        MyUtils.startActivity(this@SplashActivity, SignupActivity::class.java, false)
                        finish()
                    }, 3000)
                }


            } else {
                Log.w("SagaSagar","else");

                Handler().postDelayed({
                    val myIntent = Intent(this@SplashActivity, SelectLanguage::class.java)
                    startActivity(myIntent)
                    finish()
                }, 3000)
            }

            Log.w("SagaSagar","No");
        }

    }

    fun showSnackBar(message: String) {

        Snackbar.make(rootSplash, message, Snackbar.LENGTH_LONG).show()

    }

    private fun CurrentCityName(lattitude: Double, longitude: Double) {
        val geocoder: Geocoder
        var addresses: List<Address>? = null
        geocoder = Geocoder(this@SplashActivity, Locale.getDefault())

        try {

            addresses = geocoder.getFromLocation(
                lattitude,
                longitude,
                1
            )

            if (addresses != null) {

                MyUtils.locationCityName =
                    addresses[0].locality + ", " + addresses[0].adminArea + ", " + addresses[0].countryName
                MyUtils.locationCityNameFix =
                    addresses[0].locality + "," + addresses[0].adminArea + "," + addresses[0].countryName
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun getVersionInfo() {
        var versionName: String = ""
        var versionCode: Int = -1
        try {
            var packageInfo = packageManager.getPackageInfo(packageName, 0)
            versionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        versionInfo?.text = "" + String.format("V. %s", versionName).toString()
    }

    fun printHasyKeyFb() {
        // https://farmerprice.in/privacypolicy.html
        try {
            val info = packageManager.getPackageInfo(
                BuildConfig.APPLICATION_ID,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.d("System out", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("System out", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("System out", "printHashKey()", e)
        }

    }


    fun showMessageOKCancel(
        context: Context,
        message: String,
        title: String = "",
        okListener: DialogInterface.OnClickListener
    ): MaterialAlertDialogBuilder {
        val builder = MaterialAlertDialogBuilder(
            context,
            R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
        )
        builder.setMessage(message)
        builder.setTitle(title)
        builder.setCancelable(false)
        builder.setPositiveButton("Ok", okListener)

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()

            Snackbar.make(
                rootSplash,
                R.string.read_location_permissions_senied,
                Snackbar.LENGTH_LONG
            ).setAction(
                "Retry"
            ) {

                val i = Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                )
                startActivity(i)

            }.show()


        }

        builder.show()

        return builder
    }
}