package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.CMSpageModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CMSpagePojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.fragment_termsand_conditions.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.webview.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException

class TermsandConditionsActivity : AppCompatActivity() {
    var fragmentType: String? = null
    val mTerms = "Terms"
    val mPrivacyPolicy = "Privacy Policy"
    var bundle: Bundle? = null
    val TAG: String? = TermsandConditionsActivity::class.java.name
    var WhichCMSPage: String? = null
    val TermsActivitySimpleName: String? = "Terms"
    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_termsand_conditions)
        sessionManager = SessionManager(this@TermsandConditionsActivity)
        tvHeaderText!!.visibility = View.VISIBLE
        tvHeaderText!!.text = resources.getString(R.string.terms_conditions)

        imgCloseIcon.visibility = View.VISIBLE

        bundle = intent.extras
        fragmentType = bundle?.getString("Type")
        btnRetry?.text = GetDynamicStringDictionaryObjectClass?.Retry
        webData()

        relativeprogressBar.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        noDatafoundRelativelayout.visibility = View.GONE
        webViewMainRelativeLayout.visibility = View.VISIBLE

        when (fragmentType) {

            mTerms -> {
                tvHeaderText!!.text = "" +GetDynamicStringDictionaryObjectClass?.Terms_Conditions
                WhichCMSPage = "TC"
                commonMethod()
            }
            mPrivacyPolicy -> {
                tvHeaderText!!.text = "" +GetDynamicStringDictionaryObjectClass?.Privacy_Prolicy
                WhichCMSPage = "PP"
                commonMethod()
            }

        }

        imgCloseIcon.setOnClickListener {
            onBackPressed()
        }
        btnRetry.setOnClickListener {
            commonMethod()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        MyUtils.finishActivity(this@TermsandConditionsActivity, true)
    }

    public fun webData() {

        val settings = webView.settings
        // Enable java script in web view
        settings.javaScriptEnabled = true
        // Enable zooming in web view
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        settings.setDefaultFontSize(40);
        // Zoom web view text
//        settings.textZoom = 125
        // Enable disable images in web view
        settings.blockNetworkImage = false
        // Whether the WebView should load image resources
        settings.loadsImagesAutomatically = true
        // More web view settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.safeBrowsingEnabled = true  // api 26
        }

        //settings.pluginState = WebSettings.PluginState.ON
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.mediaPlaybackRequiresUserGesture = false
        // More optional settings, you can enable it by yourself
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.loadWithOverviewMode = true
        settings.allowContentAccess = true
        settings.setGeolocationEnabled(true)
        settings.allowUniversalAccessFromFileURLs = true
        settings.allowFileAccess = true
        // WebView settings
        webView.fitsSystemWindows = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        /*
            if SDK version is greater of 19 then activate hardware acceleration
            otherwise activate software acceleration
        */
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        // Set web view client
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                // Page loading started
                // Do something
            }

            override fun onPageFinished(view: WebView, url: String) {
                // Page loading finished
                // Display the loaded page title in a toast message
            }
        }
        //  Case 2 .. Create your own html page...
    }

    public fun commonMethod() {

        if (MyUtils.internetConnectionCheck(this@TermsandConditionsActivity)) {
            getCMSPages()

            /*var mHtmlText: String =
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."

            webView.loadDataWithBaseURL(null, mHtmlText, "text/HTML", "UTF-8", null)*/
        } else {

            relativeprogressBar?.visibility = View.GONE
            llMainTermconfitions?.visibility = View.GONE

            nointernetImageview.visibility = View.VISIBLE
            nointernetImageview.setImageDrawable(this@TermsandConditionsActivity.getDrawable(R.drawable.no_internet_connection))
            nointernetMainRelativelayout?.visibility = View.VISIBLE
            nointernettextview.text =
                this@TermsandConditionsActivity.resources.getString(R.string.error_common_network)
            nointernettextview1.visibility = View.VISIBLE
            noDatafoundRelativelayout?.visibility = View.GONE
            webViewMainRelativeLayout?.visibility = View.GONE
        }
    }

    private fun getCMSPages() {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            // val lID = if (PrefDb(this@TermsandConditionsActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) != null) PrefDb(this@TermsandConditionsActivity).getString(MyUtils.SharedPreferencesenum.languageId.toString()) else "1"
            var lID = ""

            if (sessionManager?.isLoggedIn()!!) {
                lID = sessionManager?.get_Authenticate_User()?.languageID!!
            } else {
                lID = sessionManager?.getsetSelectedLanguage()!!

            }

            jsonObject.put("loginuserID", "0")
            jsonObject.put("languageID", lID)
            jsonObject.put("cmspageName", WhichCMSPage)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
            Log.d(TAG, "Terms api call := " + jsonArray.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        relativeprogressBar?.visibility = View.VISIBLE
        nointernetMainRelativelayout?.visibility = View.GONE
        noDatafoundRelativelayout?.visibility = View.GONE
        llMainTermconfitions?.visibility = View.GONE

        val getCmspageModel =
            ViewModelProviders.of(this@TermsandConditionsActivity).get(CMSpageModel::class.java)
        getCmspageModel.getCMSPage(this@TermsandConditionsActivity, false, jsonArray.toString())
            .observe(this@TermsandConditionsActivity, object :
                Observer<List<CMSpagePojo>> {
                override fun onChanged(@Nullable cmspagePoJos: List<CMSpagePojo>?) {

                    if (cmspagePoJos != null) {

                        if (cmspagePoJos[0].status.equals("true", true)) {
                            relativeprogressBar.visibility = View.GONE
                            llMainTermconfitions?.visibility = View.VISIBLE
                            webViewMainRelativeLayout.visibility = View.VISIBLE
                            Log.e(TAG, "Username ${cmspagePoJos[0].data!![0].cmspageContents!!}")
                            webView.loadDataWithBaseURL(
                                null,
                                cmspagePoJos[0].data!![0].cmspageContents!!,
                                "text/HTML",
                                "UTF-8",
                                null
                            )
                        } else {
                            noDatafoundRelativelayout.visibility = View.VISIBLE
                        }
                    } else {
                        nodatafound()
                    }
                }
            })
    }

    private fun nodatafound() {
        try {
            relativeprogressBar.visibility = View.GONE
            llMainTermconfitions?.visibility = View.GONE
            webViewMainRelativeLayout.visibility = View.GONE
            noDatafoundRelativelayout.visibility = View.GONE

            if (MyUtils.internetConnectionCheck(this@TermsandConditionsActivity) == true) {
                nointernetImageview.visibility = View.VISIBLE
                nointernetImageview.setImageDrawable(this@TermsandConditionsActivity.getDrawable(R.drawable.something_went_wrong))
                nointernettextview.setText(GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong)
                nointernettextview1.visibility = View.GONE
                nointernetMainRelativelayout.setVisibility(View.VISIBLE)
            } else {
                nointernetImageview.visibility = View.GONE
                nointernetImageview.setImageDrawable(this@TermsandConditionsActivity.getDrawable(R.drawable.no_internet_connection))
                nointernetMainRelativelayout.setVisibility(View.VISIBLE)
                nointernettextview1.visibility = View.VISIBLE
                nointernettextview.setText(GetDynamicStringDictionaryObjectClass?.Please_check_your_internet_connectivity_and_try_again)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}