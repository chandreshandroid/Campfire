package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.media.*
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.transition.TransitionManager
import android.transition.Visibility
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.chand.progressbutton.ProgressButton
import com.facebook.FacebookSdk.getCacheDir
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.CreatePostViewPagerAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.aws.AWSConfiguration
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.aws.AwsMultipleUpload
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.GeocodingLocation
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.OnPlacesDetailsListener
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.PlaceAPI
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.PlacesAutoCompleteAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.model.Place
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.model.PlaceDetails
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.CreatePostModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.AddImages
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class CreatePostActivity : AppCompatActivity() {

    var actionType: Int = 1
    var headerTitle: String = ""
    var page_position = 0
    private var dots: Array<ImageView?>? = null
    private var dotsCount: Int = 0
    var exoPlayer: SimpleExoPlayer? = null
    var videoviewfullscreen: PlayerView? = null
    var cache: SimpleCache? = null
    var from = ""
    var sessionManager: SessionManager? = null
    var userData: RegisterPojo.Data? = null
    var msgSomthingRong = ""
    var msgNoInternet = ""
    var mag_failed_to_login = ""
    var numberOfImage: ArrayList<AddImages?>? = ArrayList()
    var widthNew = 0
    var heightNew = 0

    var bitMap: File? = null
    var urii: Uri? = null
    val MAX_PREVIEW_CACHE_SIZE_IN_BYTES = 20L * 1024L * 1024L

    var imageLocationName = ""
    var imageLocationLate: Float = 0f
    var imageLocationLong: Float = 0f
    var locationVerifed = "No"
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var fileNameAudio: String? = null
    private var lastProgress = 0
    private val mHandler: Handler = Handler()
    private var isPlaying = false
    private val RECORD_AUDIO_REQUEST_CODE = 123
    var selectionType = 0
    var lat = ""
    var lang = ""
    var videoloaction = ""
    var videoOrNot = 0;
    private val mYourBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // now you can call all your fragments method here
            MyUtils.finishActivity(this@CreatePostActivity, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        MyUtils.changeStatusBarColor(this, R.color.colorPrimary, true)
        dynamicLable()
        getScrennwidth()

        edit_add_headline?.setHint("" + GetDynamicStringDictionaryObjectClass?.Add_Headline + " " + GetDynamicStringDictionaryObjectClass?.Minimum_5_Characters)
        edit_add_description?.setHint("" + GetDynamicStringDictionaryObjectClass?.Add_Description + " " + GetDynamicStringDictionaryObjectClass?.Optional)
        tvAddAudio?.text = ("" + GetDynamicStringDictionaryObjectClass?.Add_Audio_over_Video)
        tvCurrentLocation?.setHint("" + GetDynamicStringDictionaryObjectClass?.Current_Location)
        btnSave?.progressText = GetDynamicStringDictionaryObjectClass?.Save
        btnPostNow?.progressText = GetDynamicStringDictionaryObjectClass?.Post_News
        tvChangePostPrivacy?.text = GetDynamicStringDictionaryObjectClass?.Change_to_Private
        tvPostVisiBility.text = "" + GetDynamicStringDictionaryObjectClass?.Public
        selectionType = 0
        msgSomthingRong = GetDynamicStringDictionaryObjectClass?.Something_Went_Wrong
        msgNoInternet = GetDynamicStringDictionaryObjectClass?.No_Internet_Connection
//        tvTextLeft.text =
//            (280).toString() + " " + GetDynamicStringDictionaryObjectClass?.Characters_left

        tvHeaderText.text = headerTitle
        sessionManager = SessionManager(this@CreatePostActivity)
        userData = sessionManager?.get_Authenticate_User()

        LocalBroadcastManager.getInstance(this@CreatePostActivity)
            .registerReceiver(mYourBroadcastReceiver, IntentFilter("CreatePost"))

        if (intent != null) {
            if (intent.hasExtra("from")) from = intent.getStringExtra("from")!!
            numberOfImage = intent.getSerializableExtra("imagesArray") as ArrayList<AddImages?>?
            if (intent.hasExtra("lat")) {
                lat = intent?.getStringExtra("lat")!!
            }
            if (intent.hasExtra("lang")) {
                lang = intent?.getStringExtra("lang")!!
            }

//            if (intent.hasExtra("CamraResult")) bitMap = intent.getSerializableExtra("CamraResult") as File
        }
        tvTag.text = getString(R.string.tag)

        imgCloseIcon.setOnClickListener {
            onBackPressed()
        }
        imv_user_dp.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
        tvFeedUserName.text = userData?.userFirstName + " " + userData?.userLastName

        if (bitMap != null) {
            thumnail.setImageURI(Uri.fromFile(bitMap))
        }
        var thumb: Bitmap? = null
        if (!numberOfImage.isNullOrEmpty()) {
            if (!numberOfImage!![0]?.sdcardPath.isNullOrEmpty()) {
                thumb = ThumbnailUtils.createVideoThumbnail(
                    numberOfImage!![0]?.sdcardPath!!,
                    MediaStore.Images.Thumbnails.MINI_KIND
                )
                thumnail.setImageBitmap(thumb)
            }
        }
        when (from) {
            "images" -> {
                videoOrNot = 0
                rvVideo.visibility = View.GONE
                val imageViewParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, heightNew
                )
                llAudio.visibility = View.GONE
//                rvImages?.layoutParams = imageViewParam
                rvImages?.visibility = View.VISIBLE
                view_pager2.adapter =
                    CreatePostViewPagerAdapter(this@CreatePostActivity, numberOfImage)

                if (!numberOfImage.isNullOrEmpty() && numberOfImage?.size!! > 1) {
                    addBottomDots(0, layoutDotsTutorial)
                    layoutDotsTutorial.visibility = View.VISIBLE
                } else {
                    layoutDotsTutorial.visibility = View.GONE
                }
                view_pager2?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        page_position = position

                    }

                    override fun onPageSelected(position: Int) {
                        page_position = position
                        addBottomDots(position, layoutDotsTutorial!!)
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }
                })
            }
            "video" -> {
                videoOrNot = 1;
                val fileName1: String = MyUtils.createFileName(Date(), "Image")!!
                if (!lat.isNullOrEmpty() && !lang.isNullOrEmpty()) {
                    imageLocationName = MyUtils.getLocationName(
                        this@CreatePostActivity,
                        lat.toDouble(),
                        lang.toDouble()
                    )!!
                    imageLocationLate = lat.toFloat()
                    imageLocationLong = lang.toFloat()
                    locationVerifed = "Yes"

                    if (!imageLocationName.isNullOrEmpty()) {
                        setLocation(0)
                    } else {
                        setLocation(1)
                    }
                }
                val file1 =
                    ImageSaver(this@CreatePostActivity).setExternal(true).setFileName(fileName1)
                        .save(thumb!!)
                numberOfImage?.add(
                    AddImages(
                        file1.path,
                        false,
                        null,
                        -1,
                        file1.name,
                        false,
                        "0"
                    )
                )
                llAudio.visibility = View.GONE
                rvVideo.visibility = View.VISIBLE
                rvImages.visibility = View.GONE
            }
        }

        tvCurrentLocation.setAdapter(
            PlacesAutoCompleteAdapter(
                this,
                PlaceAPI.Builder()?.apiKey(resources.getString(R.string.google_places_key))
                    .build(this@CreatePostActivity)
            )
        )
        tvCurrentLocation.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                MyUtils.hideKeyboard1(this@CreatePostActivity)
                val place = parent.getItemAtPosition(position) as Place
                tvCurrentLocation?.setText(place.description)
                tvCurrentLocation.setSelection(tvCurrentLocation?.text.toString().length)
                var locationAddress = GeocodingLocation.getAddressFromLocation(
                    tvCurrentLocation?.text.toString().trim(),
                    applicationContext, GeocoderHandler()
                )
            }

        if (!numberOfImage.isNullOrEmpty()) {
            if (intent.hasExtra("from1") && intent.getStringExtra("from1").equals("Camera")) {
                imageLocationLate = MyUtils.currentLattitudeFix.toFloat()
                imageLocationLong = MyUtils.currentLongitudeFix.toFloat()
                locationVerifed = "Yes"
                imageLocationName = MyUtils.locationCityNameFix

                if (!imageLocationName.isNullOrEmpty()) {
                    setLocation(0)
                } else setLocation(1)
            } else {
                if (!numberOfImage!![0]?.sdcardPath.isNullOrEmpty()) {
                    /*try {
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(this@CreatePostActivity, Uri.parse(numberOfImage!![0]?.sdcardPath))
                        imageLocationName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION)
                        retriever.release()
                        Log.e("System Log", "imageLocationName of view is $imageLocationName")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }*/

                    val exif = ExifInterface(numberOfImage!![0]?.sdcardPath!!)
                    var locationLatLong = geoDegree(exif, object : geoDegree.onValueReturn {
                        override fun returnValue(late: Float, long: Float) {
                            imageLocationName = "" + MyUtils.getLocationName(
                                this@CreatePostActivity,
                                if (late != null) late.toDouble() else 0.0,
                                if (long != null) long.toDouble() else 0.0
                            )
                            imageLocationLate = late
                            imageLocationLong = long
                            locationVerifed = "Yes"

                            if (!imageLocationName.isNullOrEmpty()) {
                                setLocation(0)
                            } else {
                                setLocation(1)
                            }
                        }
                    })
                } else {
                    setLocation(1)
                }

            }
        } else {
            setLocation(1)
        }

        if (!imageLocationName.isNullOrEmpty()) {
            setLocation(0)
        } else {
            setLocation(1)
        }

        playicon.setOnClickListener {
            playVideo(
                0,
                exoPlayer1,
                thumnail!!,
                thumnail!!,
                pb!!,
                playicon!!,
                ""

            )
        }


        tvChangePostPrivacy.setOnClickListener {
            tvChangePostPrivacy.isEnabled = false
            /*if (selectionType == 0) {
                selectionType = 1
            } else if (selectionType == 1) {
                selectionType = 0
            }*/
            when (selectionType) {
                0 -> {
                    selectionType = 1
                    tvPostVisiBility.text = "" + GetDynamicStringDictionaryObjectClass?.Private
                    tvChangePostPrivacy.text =
                        "" + GetDynamicStringDictionaryObjectClass?.Change_to_Public
                }
                1 -> {
                    selectionType = 0
                    tvPostVisiBility.text = "" + GetDynamicStringDictionaryObjectClass?.Public
                    tvChangePostPrivacy.text =
                        "" + GetDynamicStringDictionaryObjectClass?.Change_to_Private
                }
            }
            /*when (tvChangePostPrivacy.text.toString().trim()) {
                "Change to Private" -> {
                    tvPostVisiBility.text = "Private"+Get
                    tvChangePostPrivacy.text = "Change to Public"
                }
                "Change to Public" -> {
                    tvPostVisiBility.text = "Public"
                    tvChangePostPrivacy.text = "Change to Private"
                }
            }*/
            tvChangePostPrivacy.isEnabled = true

        }

        edit_add_description.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                tvTextLeft.text =
//                    (280 - s.toString().length).toString() + " " + GetDynamicStringDictionaryObjectClass?.Characters_left
            }
        })

        tvHasTag.setOnClickListener {
            edit_add_description.setText(edit_add_description.text.toString().trim() + " #")
            edit_add_description.setSelection(edit_add_description.text.toString().length)
        }

        tvTag.setOnClickListener {
            edit_add_description.setText(edit_add_description.text.toString().trim() + " @")
            edit_add_description.setSelection(edit_add_description.text.toString().length)
        }

        btnNext.setOnClickListener {
            if (btnPostNow.visibility == View.GONE)
                actionType = actionType + 1;
            if (edit_add_headline.text.toString().trim().isNullOrBlank()) {
                disableView(llTitle)
            } else {
                if (actionType == 2)
                    disableView(llHashCode)
                else if (actionType == 3) {
                    if (videoOrNot == 1)
                        disableView(llAudio)
                    else
                        disableView(llLocation)
                } else if (actionType == 4) {

                    if (videoOrNot == 1)
                        disableView(llLocation)
                    else {
                        disableView(llAudiance)
                        btnPostNow.visibility = View.VISIBLE
                        btnSave.visibility = View.VISIBLE
                        btnNext.visibility = View.GONE
                    }
                } else if (actionType == 5) {
                    if (videoOrNot == 1) {
                        disableView(llAudiance)
                        btnPostNow.visibility = View.VISIBLE
                        btnSave.visibility = View.VISIBLE
                        btnNext.visibility = View.GONE
                    }
                }

            }

        }

        btnSave.setOnClickListener {
            //hashTag(edit_add_headline.tagText.trim(),this@CreatePostActivity)
            if (numberOfImage != null && numberOfImage?.size!! > 0) {
                when {
                    edit_add_headline.text.toString().trim().isNullOrEmpty() -> {

                        edit_add_headline.visibility = View.VISIBLE
                        MyUtils.showSnackbar(
                            this@CreatePostActivity,
                            "Please enter news headline",
                            rootCreatePost
                        )
                    }


                    !edit_add_headline.text.toString().trim()
                        .isNullOrEmpty() && edit_add_headline.text.toString()
                        .trim().length < 10 -> {
                        MyUtils.showSnackbar(
                            this@CreatePostActivity,
                            "Please enter news headline minimum 10 character",
                            rootCreatePost
                        )
                    }
                    !edit_add_description.text.toString().trim()
                        .isNullOrEmpty() && edit_add_description.text.toString()
                        .trim().length < 25 -> {
                        MyUtils.showSnackbar(
                            this@CreatePostActivity,
                            "Please enter news descriptions minimum 25 character",
                            rootCreatePost
                        )
                    }
                    tvCurrentLocation.text.toString().isNullOrEmpty() -> {
                        MyUtils.showSnackbar(
                            this@CreatePostActivity,
                            "Please select your location",
                            rootCreatePost
                        )
                    }
                    else -> {
                        uploadPost("Save", btnSave)
                    }
                }
            } else {
                MyUtils.showSnackbar(
                    this@CreatePostActivity,
                    resources.getString(R.string.please_select_image_video),
                    rootCreatePost
                )
            }

        }

        btnPostNow.setOnClickListener {
            if (numberOfImage != null && numberOfImage?.size!! > 0) {
                when {
                    edit_add_headline.text.toString().trim().isNullOrEmpty() -> {
                        MyUtils.showSnackbar(
                            this@CreatePostActivity,
                            "Please enter news headline",
                            rootCreatePost
                        )
                    }
                    !edit_add_headline.text.toString().trim()
                        .isNullOrEmpty() && edit_add_headline.text.toString()
                        .trim().length < 10 -> {
                        MyUtils.showSnackbar(
                            this@CreatePostActivity,
                            "Please enter news headline minimum 10 character",
                            rootCreatePost
                        )
                    }
                    !edit_add_description.text.toString().trim()
                        .isNullOrEmpty() && edit_add_description.text.toString()
                        .trim().length < 25 -> {
                        MyUtils.showSnackbar(
                            this@CreatePostActivity,
                            "Please enter news descriptions minimum 25 character",
                            rootCreatePost
                        )
                    }
                    tvCurrentLocation.text.toString().isNullOrEmpty() -> {
                        MyUtils.showSnackbar(
                            this@CreatePostActivity,
                            "Please select your location",
                            rootCreatePost
                        )
                    }
                    else -> {
                        when (from) {
                            "images" -> {
                                uploadPost("Uploaded", btnPostNow)
                            }
                            "video" -> {
                                var postPrivacyType = ""
                                if (selectionType?.equals("0")!!) {
                                    postPrivacyType = "Public"
                                } else if (selectionType?.equals(0)!!) {
                                    postPrivacyType = "Private"
                                }
                                val intent2 = Intent("CreatePost")
                                intent2.putExtra("from", "Uploaded")
                                intent2.putExtra("UploadVideo", "Video")
                                intent2.putExtra(
                                    "postHeadline",
                                    edit_add_headline.text.toString().trim()
                                )
                                intent2.putExtra(
                                    "postDescription",
                                    edit_add_description.tagText.trim()
                                )
                                intent2.putExtra(
                                    "postLocation",
                                    tvCurrentLocation?.text.toString().trim()
                                )
                                intent2.putExtra("postLatitude", imageLocationLate.toString())
                                intent2.putExtra("postLongitude", imageLocationLong.toString())
                                intent2.putExtra(
                                    "postLocationVerified",
                                    if (!tvCurrentLocation.text.toString()
                                            .isNullOrEmpty()
                                    ) locationVerifed else ""
                                )
                                intent2.putExtra("postPrivacyType", postPrivacyType)
                                intent2.putExtra("postCreateType", "Uploaded")
                                intent2.putExtra("postHashText",
                                    if (!hashTag(
                                            edit_add_description.tagText.trim(),
                                            this@CreatePostActivity
                                        ).isNullOrEmpty()
                                    ) {
                                        hashTag(
                                            edit_add_description.tagText,
                                            this@CreatePostActivity
                                        ).joinToString {
                                            it.toString().trim()
                                        }
                                    } else {
                                        ""
                                    })
                                intent2.putExtra("posttag",
                                    if (!edit_add_description?.contentFriendsTagList?.isNullOrEmpty()!!) {
                                        edit_add_description?.contentFriendsTagList?.joinToString {
                                            it.userID.toString().trim()
                                        }
                                    } else {
                                        ""
                                    })
                                intent2.putExtra("VideoFile", numberOfImage)
                                LocalBroadcastManager.getInstance(this@CreatePostActivity)
                                    .sendBroadcast(intent2)
                            }
                        }
                    }
                }
            } else {
                MyUtils.showSnackbar(
                    this@CreatePostActivity,
                    resources.getString(R.string.please_select_image_video),
                    rootCreatePost
                )
            }


        }

        llAudio.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPermissionToRecordAudio()
            }

        }
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
                    imageLocationLate = parts[0].toFloat()
                    imageLocationLong = parts[1].toFloat()
                }
            }
//            latLongTV.setText(locationAddress)
        }
    }

    private fun setLocation(i: Int) {
        /**
         * 0 location found
         * 1 location not found
         * */
        when (i) {
            0 -> {
                tvCurrentLocation?.setText(imageLocationName)
                imv_edit_current_location.setImageResource(R.drawable.geological_location_verified)
//                setCustomeText(imageLocationName)
//                tvCurrentLocation.setText(imageLocationName)
//                tvCurrentLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.geological_location_verified)
                tvCurrentLocation.isEnabled = false
                tvCurrentLocation.isClickable = false
            }
            1 -> {
                Log.e("System out", "Print SetLocation = 1")
                tvCurrentLocation.hint = resources.getString(R.string.enter_location)
                tvCurrentLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                tvCurrentLocation.isEnabled = true
                tvCurrentLocation.isClickable = true
                locationVerifed = "No"

            }
        }
    }


    private fun dynamicLable() {
//        headerTitle = resources.getString(R.string.post_news)
        headerTitle = GetDynamicStringDictionaryObjectClass?.Post_News
    }

    fun addBottomDots(currentPage: Int, layoutDotsTutorial: LinearLayout) {
        // dotsCount = stichingServicData?.stitchingserviceImages!!.size
        //  dotsCount=trendingFeedDatum1?.postSerializedData?.get(0)?.albummedia!!.size
        dotsCount = if (numberOfImage.isNullOrEmpty()) 0 else numberOfImage?.size!!
        layoutDotsTutorial.removeAllViews()
        dots = arrayOfNulls(dotsCount)
        for (i in 0 until dotsCount) {

            dots!![i] = ImageView(this@CreatePostActivity)
            dots!![i]?.setImageResource(R.drawable.carousel_dot_unselected)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            dots!![i]?.setPadding(0, 0, 0, 0)
            dots!![i]?.layoutParams = params
            layoutDotsTutorial.addView(dots!![i]!!, params)
            layoutDotsTutorial.bringToFront()

        }
        if (dots!!.isNotEmpty() && dots!!.size > currentPage) {
            dots!![currentPage]?.setImageResource(R.drawable.carousel_dot_selected)
        }

    }

    fun stopPlayer() {
        /* for(i in 0 until feedList.size)
                           {
                               if(feedList.get(i)!=null&&feedList.get(i).postSerializedData!![0]!!.albummedia!=null&& feedList.get(
                                       i
                                   ).postSerializedData!![0]!!.albummedia.isNotEmpty()
                               )
                                   feedList.get(i).postSerializedData!![0]!!.albummedia[0].isPlaying=(false);
                           }*/
        if (exoPlayer != null) {
            exoPlayer?.stop(true)
            exoPlayer?.release()
            exoPlayer = null
        }


    }

    fun isMuteing(): Boolean {
        return MyUtils.isMuteing
    }

    fun setMuteing(muteing: Boolean) {
        MyUtils.isMuteing = muteing
    }

    fun muteVideo(isMute: Boolean) {
        if (exoPlayer != null) {
            if (isMute)
                exoPlayer?.volume = 0f
            else
                exoPlayer?.volume = 1f
            setMuteing(isMute)
        }
    }

    fun playVideo(
        videoPlayPosition: Int,
        playerView: PlayerView,
        thumnail: ImageView,
        volume: View?,
        progressBar: ProgressBar,
        playIcon: ImageView,
        from: String
    ) {

        runOnUiThread {
            val mHandler = Handler(Looper.getMainLooper())
            mHandler.post {
                //                val videoURI = MyUtils.videoURI
//                   numberOfImage?.get(0)!!.sdCardUri
                var dataSpec: DataSpec? = null

                if (MyUtils.videoFile == null) {
                    if (numberOfImage!![0]!!.videoFile != null)
                        dataSpec = DataSpec(Uri.fromFile(numberOfImage!![0]!!.videoFile))
                } else if (numberOfImage!![0]!!.videoFile != null) {
                    dataSpec =
                        DataSpec(Uri.parse("file:///" + numberOfImage!![0]!!.videoFile.toString()))
                }

                val bandwidthMeter: BandwidthMeter? = null
                val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
                val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

                val cacheDataSourceFactory =
                    CacheDataSourceFactory1(this@CreatePostActivity, 5 * 1024 * 1024)

                val fileDataSource = FileDataSource()
                try {
                    fileDataSource.open(dataSpec)
                } catch (e: FileDataSource.FileDataSourceException) {
                    e.printStackTrace()
                }

                val factory = DataSource.Factory {
                    fileDataSource
                }

                val mediaSource = ExtractorMediaSource(
                    fileDataSource.uri,
                    factory, DefaultExtractorsFactory(), null, null
                )


                stopPlayer()

                exoPlayer =
                    ExoPlayerFactory.newSimpleInstance(this@CreatePostActivity, trackSelector)


                playerView.setShutterBackgroundColor(Color.TRANSPARENT)
                playerView.player = exoPlayer

                if (volume != null) {
                    /* if (!isMuteing()) {

                                                          if (volume is ImageButton)
                                                              volume.background = context.resources
                                                                  .getDrawable(R.drawable.ic_volume_up_black_24dp)
                                                          else
                                                              (volume as ImageView).setImageDrawable(
                                                                  context.resources.getDrawable(
                                                                      R.drawable.ic_volume_up_black_24dp
                                                                  )
                                                              )

                                                      } else {
                                                          if (volume is ImageButton)
                                                              volume.background = context.resources
                                                                  .getDrawable(R.drawable.ic_volume_off_black_24dp)
                                                          else
                                                              (volume as ImageView).setImageDrawable(
                                                                  context.resources.getDrawable(
                                                                      R.drawable.ic_volume_off_black_24dp
                                                                  )
                                                              )
                                                      }*/
                }

                exoPlayer?.prepare(mediaSource)
                exoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
                if (from.equals("audio", false)) {
                    exoPlayer?.volume = 0f
                }
                //muteVideo(isMuteing())

                //  (exoPlayer as SimpleExoPlayer).seekTo( feedList.get(videoPlayPosition).postSerializedData.get(0).albummedia.get(0).duration)
                exoPlayer?.playWhenReady = true

                progressBar.visibility = View.VISIBLE
                if (playIcon.visibility == View.VISIBLE)
                    playIcon.visibility = View.GONE
                //  exoPlayer.seekTo(selectVideoList.get(pos).getSeekTo());
                exoPlayer?.addListener(object : Player.EventListener {

                    override fun onTimelineChanged(
                        timeline: Timeline?,
                        manifest: Any?,
                        reason: Int
                    ) {

                    }

                    override fun onTracksChanged(
                        trackGroups: TrackGroupArray?,
                        trackSelections: TrackSelectionArray?
                    ) {

                    }

                    override fun onLoadingChanged(isLoading: Boolean) {

                    }

                    override fun onPlayerStateChanged(
                        playWhenReady: Boolean,
                        playbackState: Int
                    ) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                progressBar.visibility = View.VISIBLE

                            }


                            Player.STATE_ENDED -> if (exoPlayer != null) {
                                exoPlayer?.seekTo(0)
                                thumnail.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                playIcon.visibility = View.VISIBLE
                                pausePlayer()
                                if (from.equals("audio", false)) {

                                    prepareforStop()
                                    stopRecording()
                                    muxing()
                                }


                            }
                            Player.STATE_IDLE -> {
                            }

                            Player.STATE_READY -> {
                                if (thumnail.visibility == View.VISIBLE)
                                    thumnail.visibility = View.GONE
                                if (from.equals("audio", false)) {
                                    Toast.makeText(
                                        this@CreatePostActivity, "Start Recording..",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    prepareforRecording()
                                    startRecording()
                                    exoPlayer?.volume = 0f
                                }

                                progressBar.visibility = View.GONE
                                if (playIcon.visibility == View.VISIBLE)
                                    playIcon.visibility = View.GONE


                            }
                            else -> {
                            }
                        }
                    }

                    override fun onRepeatModeChanged(repeatMode: Int) {

                    }

                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

                    }

                    override fun onPlayerError(error: ExoPlaybackException?) {
                        Log.d("ExoPlayer", error!!.toString())
                        pausePlayer()
                        /*  progressBar.setVisibility(View.GONE);
                                                       thumnail.setVisibility(View.VISIBLE);
                                                       stopPlayer();*/
                    }

                    override fun onPositionDiscontinuity(reason: Int) {

                    }

                    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

                    }

                    override fun onSeekProcessed() {

                    }
                })
            }
        }


    }

    fun pausePlayer() {
        try {
            runOnUiThread(Runnable {
                if (exoPlayer != null) {
                    exoPlayer?.stop(true)
                    exoPlayer?.release()
                    exoPlayer = null
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            stopPlayer()
        }

    }

    fun createPost(
        postCreateType: String,
        btn: ProgressButton,
        datumList: ArrayList<AddImages?>
    ) {


        var c = Calendar.getInstance()

        var df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var formattedDate = df.format(c.time)

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        val jsonObjectpostSerializedData = JSONObject()
        val jsonArraypostSerializedData = JSONArray()


        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put(
                "postHeadline",
                edit_add_headline.text.toString().trim()
            )
            jsonObject.put(
                "postDescription",
                edit_add_description.tagText.trim()
            )

            jsonObject.put("postLocation", tvCurrentLocation?.text.toString().trim())
            jsonObject.put("postLatitude", imageLocationLate.toString())
            jsonObject.put("postLongitude", imageLocationLong.toString())
            jsonObject.put(
                "postLocationVerified",
                if (!tvCurrentLocation.text.toString().isNullOrEmpty()) locationVerifed else ""
            )
            jsonObject.put("postSerializedData", jsonArraypostSerializedData)
            when (selectionType) {
                0 -> {
                    jsonObject.put("postPrivacyType", "Public")
                }
                1 -> {
                    jsonObject.put("postPrivacyType", "Private")
                }
            }
            jsonObject.put("postCreateType", postCreateType)
            jsonObject.put("postDateTime", formattedDate)
            jsonObject.put("postHashText",
                if (!hashTag(
                        edit_add_description.tagText.trim(),
                        this@CreatePostActivity
                    ).isNullOrEmpty()
                ) {
                    hashTag(edit_add_description.tagText, this@CreatePostActivity).joinToString {
                        it.toString().trim()
                    }
                } else {
                    ""
                })
            jsonObject.put("posttag",
                if (!edit_add_description?.contentFriendsTagList?.isNullOrEmpty()!!) {
                    edit_add_description?.contentFriendsTagList?.joinToString {
                        it.userID.toString().trim()
                    }
                } else {
                    ""
                }
            )
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        when (from) {
            "images" -> {
                jsonObject.put("postMediaType", "Photo")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()

                    for (i in 0 until datumList.size) {
                        val jsonObjectAlbummedia = JSONObject()
                        try {
                            jsonObjectAlbummedia.put("albummediaFile", datumList[i]!!.imageName)
                            jsonObjectAlbummedia.put("albummediaThumbnail", "")
                            jsonObjectAlbummedia.put("albummediaFileType", "Photo")
                            jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "video" -> {
                jsonObject.put("postMediaType", "Video")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    val jsonObjectAlbummedia = JSONObject()
                    try {
                        jsonObjectAlbummedia.put("albummediaFile", datumList[0]!!.imageName)
                        jsonObjectAlbummedia.put("albummediaThumbnail", datumList[1]!!.imageName)
                        jsonObjectAlbummedia.put("albummediaFileType", "Video")
                        jsonArrayAlbummedia.put(0, jsonObjectAlbummedia)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        jsonArray.put(jsonObject)

        val createPostModel =
            ViewModelProviders.of(this@CreatePostActivity).get(CreatePostModel::class.java)
        createPostModel.apiFunction(this@CreatePostActivity, false, jsonArray.toString())
            .observe(this@CreatePostActivity,
                Observer<List<CommonPojo>> { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(this@CreatePostActivity)
                            btn.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(rootCreatePost, true)

                            if (!response[0].message!!.isNullOrEmpty()) {
                                MyUtils.showSnackbarkotlin(
                                    this@CreatePostActivity,
                                    rootCreatePost,
                                    response[0].message!!
                                )

                                val intent2 = Intent("CreatePost")
                                intent2.putExtra("from", postCreateType)
                                intent2.putExtra("msg", response[0].message!!)
                                LocalBroadcastManager.getInstance(this@CreatePostActivity)
                                    .sendBroadcast(intent2)

                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@CreatePostActivity,
                                    rootCreatePost,
                                    mag_failed_to_login
                                )
                                val intent2 = Intent("CreatePost")
                                intent2.putExtra("from", postCreateType)
                                intent2.putExtra("msg", response[0].message!!)
                                LocalBroadcastManager.getInstance(this@CreatePostActivity)
                                    .sendBroadcast(intent2)
                            }
                        } else {
                            btn.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(rootCreatePost, true)
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(this@CreatePostActivity)) {
                                if (!response[0].message!!.isNullOrEmpty()) {
                                    MyUtils.showSnackbarkotlin(
                                        this@CreatePostActivity,
                                        rootCreatePost,
                                        response[0].message!!
                                    )
                                }

                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@CreatePostActivity,
                                    rootCreatePost,
                                    msgNoInternet
                                )
                            }
                        }

                    } else {
                        btn.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(rootCreatePost, true)
                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(this@CreatePostActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@CreatePostActivity,
                                rootCreatePost,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@CreatePostActivity,
                                rootCreatePost,
                                msgNoInternet
                            )
                        }
                    }
                })
    }


    override fun onBackPressed() {
        stopPlayer()
        MyUtils.finishActivity(this@CreatePostActivity, true)
    }

    override fun onPause() {
        super.onPause()
        stopPlayer()

    }

    override fun onStop() {
        super.onStop()
        stopPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        cache?.release()
        cache = null
        LocalBroadcastManager.getInstance(this@CreatePostActivity)
            .unregisterReceiver(mYourBroadcastReceiver)
    }

    fun trimCache(context: Context) {
        try {
            val dir = context.cacheDir
            if (dir != null && dir.isDirectory) {
                deleteDir(dir)
            }
        } catch (e: Exception) {
            // TODO: handle exception
        }
    }

    fun deleteDir(dir: File): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()

            for (i in 0 until children.size) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete()
    }

    fun uploadPost(s: String, btnSave: ProgressButton) {
        //if (homeRadioButton.isChecked() || socialRadioButton.isChecked()){
        if (numberOfImage != null && numberOfImage?.size!! > 0) {
            /*val imageVideolist1:ArrayList<AddImages> =ArrayList()
            for (i in numberOfImage?.indices!!.reversed()) {
                if (numberOfImage?.get(i)!!.fromServer.equals("No",false)) {
                    imageVideolist1.add(numberOfImage!!.get(i)!!)
                    numberOfImage?.removeAt(i)
                }
            }*/

            btnSave.startAnimation()
            MyUtils.setViewAndChildrenEnabled(rootCreatePost, false)

            if (numberOfImage?.size!! > 0) {

                val awsMultipleUpload: AwsMultipleUpload =
                    object :
                        AwsMultipleUpload(this, numberOfImage!!, AWSConfiguration.mediaPath, from) {
                        override fun onStateFileChanged(
                            id: Int,
                            state: TransferState?
                        ) {
                        }

                        override fun onProgressFileChanged(
                            id: Int,
                            bytesCurrent: Long,
                            bytesTotal: Long
                        ) {
                        }

                        override fun onErrorUploadFile(id: Int, ex: String?) {
                            btnSave.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(rootCreatePost, true)
                            if (MyUtils.isInternetAvailable(this@CreatePostActivity))
                                MyUtils.showSnackbar(
                                    this@CreatePostActivity,
                                    resources.getString(R.string.error_crash_error_message),
                                    rootCreatePost
                                )
                            else
                                MyUtils.showSnackbar(
                                    this@CreatePostActivity,
                                    resources.getString(R.string.error_common_network),
                                    rootCreatePost
                                )
                        }

                        override fun onSuccessUploadFile(awsFileList: ArrayList<AddImages?>) {

                            createPost(s, btnSave, awsFileList)
                        }
                    }


            } else {
                createPost(s, btnSave, numberOfImage!!)
            }

            /* UploadPhotoVideo(
                 this@CreatePostActivity,
                 numberOfImage,
                 sessionManager!!.get_Authenticate_User().userID,
                 rootCreatePost,
                 object : UploadPhotoVideo.OnSuccess {
                     override fun onSuccessUpload(datumList: List<MultiplefileData?>?) {
                         if (datumList != null && datumList.isNotEmpty()) {
                             //  numberOfImage?.addAll(datumList)
                             createPost(s, btnSave, datumList)
                         }
                     }

                     override fun onFailureUpload(msg: String?, datumList: List<AddImages?>?) {
                         btnSave.endAnimation()
                         MyUtils.setViewAndChildrenEnabled(rootCreatePost, true)

                         if (datumList != null && datumList.size > 0) {
                             // numberOfImage!!.clear()
                             //numberOfImage?.addAll(datumList)
                         }
                         if (msg?.length!! > 0) {
                             MyUtils.showSnackbar(
                                 this@CreatePostActivity,
                                 msg,
                                 rootCreatePost
                             )
                         } else if (MyUtils.isInternetAvailable(this@CreatePostActivity))
                             MyUtils.showSnackbar(
                                 this@CreatePostActivity,
                                 resources.getString(R.string.error_crash_error_message),
                                 rootCreatePost
                             ) else MyUtils.showSnackbar(
                             this@CreatePostActivity,
                             resources.getString(R.string.error_common_network),
                             rootCreatePost
                         )
                     }


                 }, from
             )*/


        } else {

            MyUtils.showSnackbar(
                this@CreatePostActivity,
                resources.getString(R.string.please_select_image_video),
                rootCreatePost
            )
        }
    }

    class VideoCache {
        private var sDownloadCache: SimpleCache? = null

        fun getInstance(): SimpleCache? {
            if (sDownloadCache == null) sDownloadCache =
                SimpleCache(File(getCacheDir(), "exoCache"), NoOpCacheEvictor())
            return sDownloadCache
        }
    }


    private fun setCustomeText(str: String) {
        var string = str + "e"

        val sb = SpannableStringBuilder(str)

        /*var drawable: Drawable? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = resources.getDrawable(R.drawable.geological_location_verified, theme)
        } else {
            drawable = resources.getDrawable(R.drawable.geological_location_verified)
        }

        drawable.setBounds(5, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        sb.setSpan(
            CenteredImageSpan(drawable, -5),
            string.length - 1,
            string.length,
            Spanned.SPAN_INTERMEDIATE
        )*/
        tvCurrentLocation?.text = sb
    }

    fun hashTag(
        text: String,
        mContext: Context?
    ): ArrayList<String> {
        var string = ArrayList<String>()

        var text = text
        if (text.contains("<Shase>")) {
            text = text.replace("<Shase>".toRegex(), "#")
            text = text.replace("<Chase>".toRegex(), ",")
        }
        val ssb = SpannableStringBuilder(text)
        val MY_PATTERN = Pattern.compile("#(\\w+)")
        val mat = MY_PATTERN.matcher(text)
        while (mat.find()) {

            Log.e("match1", mat.group())
            string.add(mat.group().trim())
        }

        return string
    }

    private fun getScrennwidth(): Int {

        val wm = this@CreatePostActivity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        widthNew = (width).toInt()
        heightNew = (height / 1.5).toInt()
        Log.e("System out", "Print heightNew := " + heightNew)

        return height
    }


    private fun showDialogBeAReporter(userName: String, userMentionID: String?) {

        val dialogs =
            MaterialAlertDialogBuilder(this@CreatePostActivity, R.style.ThemeOverlay_MyApp_Dialog)

        //   dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(true)

        val dialogView = layoutInflater.inflate(R.layout.dialog_record_video, null)
        dialogs.setView(dialogView)
        val alertDialog = dialogs.create()
        val chronometerTimer = dialogView.findViewById<Chronometer>(R.id.chronometerTimer)
        val imageViewRecord = dialogView.findViewById<AppCompatImageView>(R.id.imageViewRecord)
        val imageViewStop = dialogView.findViewById<AppCompatImageView>(R.id.imageViewStop)
        val linearLayoutRecorder = dialogView.findViewById<LinearLayout>(R.id.linearLayoutRecorder)

        imageViewRecord.setOnClickListener {
            /* prepareforRecording(linearLayoutRecorder,imageViewRecord,imageViewStop)
             startRecording(chronometerTimer,imageViewStop)*/
        }

        imageViewStop.setOnClickListener {

            /*prepareforStop(linearLayoutRecorder,imageViewRecord,imageViewStop)
            stopRecording(chronometerTimer)
            muxing()*/
            alertDialog.dismiss()

        }


        var lp = WindowManager.LayoutParams()
        lp.copyFrom(alertDialog.window?.attributes)
//        lp.width = 850
//        lp.height = 900
        alertDialog.window?.attributes = lp
        alertDialog.show()

    }

    private fun prepareforStop(

    ) {
        TransitionManager.beginDelayedTransition(rootCreatePost)

    }

    private fun stopRecording() {
        try {
            mRecorder!!.stop()
            mRecorder!!.release()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        mRecorder = null
        Toast.makeText(this, "Stop Recording", Toast.LENGTH_SHORT).show()
    }


    private fun prepareforRecording() {
        TransitionManager.beginDelayedTransition(rootCreatePost)
        /* imageViewRecord!!.visibility = View.GONE
         imageViewStop!!.visibility = View.VISIBLE*/
    }

    private fun startRecording() { //we use the MediaRecorder class to record

        try {
            mRecorder = MediaRecorder()
            mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            /**In the lines below, we create a directory VoiceRecorderSimplifiedCoding/Audios in the phone storage
             * and the audios are being stored in the Audios folder  */
            val root: File = Environment.getExternalStorageDirectory()
            val file =
                File(root.getAbsolutePath().toString() + "/Camfire/Audios")
            if (!file.exists()) {
                file.mkdirs()
            }
            fileNameAudio =
                root.getAbsolutePath().toString() + "/Camfire/Audios/" + (System.currentTimeMillis()
                    .toString() + ".aac")
            mRecorder?.setOutputFile(fileNameAudio)
            mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            try {
                mRecorder?.prepare()
                mRecorder?.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            lastProgress = 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        stopPlaying()
    }


    private fun stopPlaying() {
        try {
            mPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mPlayer = null
        //showing the play button

    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.M)
    fun getPermissionToRecordAudio() { // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
// checking the build version since Context.checkSelfPermission(...) is only available
// in Marshmallow
// 2) Always check for permission (even if permission has already been granted)
// since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                RECORD_AUDIO_REQUEST_CODE
            )
        } else {
            MyUtils.hideKeyboard1(this@CreatePostActivity)
            MyUtils.showMessageYesNo(
                this@CreatePostActivity,
                "Are you sure you want to add your voice to video?",
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                        playVideo(
                            0,
                            exoPlayer1,
                            thumnail!!,
                            thumnail!!,
                            pb!!,
                            playicon!!,
                            "audio"

                        )
                    }
                })

            //  showDialogBeAReporter("","")

        }
    }

    // Callback with the request from calling requestPermissions(...)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) { // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.size == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) { //Toast.makeText(this, "Record Audio permission granted", Toast.LENGTH_SHORT).show();
                MyUtils.hideKeyboard1(this@CreatePostActivity)
                MyUtils.showMessageYesNo(
                    this@CreatePostActivity,
                    "Are you sure you want to add your voice to audio?",
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            Toast.makeText(
                                this@CreatePostActivity, "Start Recording..",
                                Toast.LENGTH_SHORT
                            ).show()

                            dialog?.dismiss()
                            playVideo(
                                0,
                                exoPlayer1,
                                thumnail!!,
                                thumnail!!,
                                pb!!,
                                playicon!!,
                                "audio"
                            )
                        }
                    })


            } else {
                Toast.makeText(
                    this,
                    "You must give permissions to use this app. App is exiting.",
                    Toast.LENGTH_SHORT
                ).show()
                finishAffinity()
            }
        }
    }


    private fun muxing() {
        var outputFile = ""
        try {
            val file =
                File(
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator + "Camfire/" + "Merged_" + getTimeStamp() + ".mp4"
                )

            /*if (!file.exists()) {
                file.mkdirs()
            }*/

            file.createNewFile()
            outputFile = file.absolutePath

            val videoExtractor = MediaExtractor()

            // val afdd: AssetFileDescriptor = assets.openFd(video.name)
            Log.e("video file 1", numberOfImage!![0]!!.videoFile.toString())
            var videoPath = File(numberOfImage!![0]!!.videoFile.toString())
            videoExtractor.setDataSource(videoPath.path)

            var auioPath = File(fileNameAudio)

            val audioExtractor = MediaExtractor()
            audioExtractor.setDataSource(auioPath.path)

            Log.d("Video", "Video Extractor Track Count " + videoExtractor.trackCount)
            Log.d("Audio", "Audio Extractor Track Count " + audioExtractor.trackCount)

            val muxer =
                MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            muxer.setOrientationHint(retriveVideoFrameFromVideo(numberOfImage!![0]!!.videoFile.toString())!!)

            videoExtractor.selectTrack(0)
            val videoFormat: MediaFormat = videoExtractor.getTrackFormat(0)


            val videoTrack: Int = muxer.addTrack(videoFormat)
            audioExtractor.selectTrack(0)

            val audioFormat: MediaFormat = audioExtractor.getTrackFormat(0)
            val audioTrack: Int = muxer.addTrack(audioFormat)

            var sawEOS = false
            var frameCount = 0
            val offset = 100
            val sampleSize = 1024 * 1024
            val videoBuf: ByteBuffer = ByteBuffer.allocate(sampleSize)
            val audioBuf: ByteBuffer = ByteBuffer.allocate(sampleSize)

            val videoBufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
            val audioBufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
            videoExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            audioExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            muxer.start()

            while (!sawEOS) {
                videoBufferInfo.offset = offset
                videoBufferInfo.size = videoExtractor.readSampleData(videoBuf, offset)

                if (videoBufferInfo.size < 0 || audioBufferInfo.size < 0) {
                    Log.d("Audio", "saw input EOS.")
                    sawEOS = true
                    videoBufferInfo.size = 0
                } else {
                    videoBufferInfo.presentationTimeUs = videoExtractor.sampleTime
                    videoBufferInfo.flags = videoExtractor.sampleFlags
                    muxer.writeSampleData(videoTrack, videoBuf, videoBufferInfo)
                    videoExtractor.advance()
                    frameCount++
                    Log.d(
                        "Audio",
                        "Frame (" + frameCount + ") Video PresentationTimeUs:" + videoBufferInfo.presentationTimeUs + " Flags:" + videoBufferInfo.flags + " Size(KB) " + videoBufferInfo.size / 1024
                    )
                    Log.d(
                        "Audio",
                        "Frame (" + frameCount + ") Audio PresentationTimeUs:" + audioBufferInfo.presentationTimeUs + " Flags:" + audioBufferInfo.flags + " Size(KB) " + audioBufferInfo.size / 1024
                    )
                }
            }

            var sawEOS2 = false
            var frameCount2 = 0
            while (!sawEOS2) {
                frameCount2++
                audioBufferInfo.offset = offset
                audioBufferInfo.size = audioExtractor.readSampleData(audioBuf, offset)
                if (videoBufferInfo.size < 0 || audioBufferInfo.size < 0) {
                    Log.d("Audio", "saw input EOS.")
                    sawEOS2 = true
                    audioBufferInfo.size = 0
                } else {
                    audioBufferInfo.presentationTimeUs = audioExtractor.sampleTime
                    audioBufferInfo.flags = audioExtractor.sampleFlags
                    muxer.writeSampleData(audioTrack, audioBuf, audioBufferInfo)
                    audioExtractor.advance()
                    Log.d(
                        "Audio",
                        "Frame (" + frameCount + ") Video PresentationTimeUs:" + videoBufferInfo.presentationTimeUs + " Flags:" + videoBufferInfo.flags + " Size(KB) " + videoBufferInfo.size / 1024
                    )
                    Log.d(
                        "Audio",
                        "Frame (" + frameCount + ") Audio PresentationTimeUs:" + audioBufferInfo.presentationTimeUs + " Flags:" + audioBufferInfo.flags + " Size(KB) " + audioBufferInfo.size / 1024
                    )
                }
            }

            numberOfImage!![0]!!.videoFile = file
            numberOfImage!![0]!!.sdcardPath = file.toString()

            Toast.makeText(applicationContext, "Your voice added successfully", Toast.LENGTH_LONG)
                .show()
            muxer.stop()
            muxer.release()
        } catch (e: IOException) {
            Log.d("Audio", "Mixer Error 1 " + e.message)
        } catch (e: Exception) {
            Log.d("Audio", "Mixer Error 2 " + e.message)
        }
    }

    fun getTimeStamp(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
    }


    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath: String): Int? {
        var degree: Int = 0
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()

            mediaMetadataRetriever.setDataSource(videoPath)

            degree =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                    ?.toInt()!!

        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable(
                "Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message
            )

        } finally {
            mediaMetadataRetriever?.release()
        }
        return degree
    }


    fun disableView(view: View) {

        llTitle.visibility = View.GONE
        llAudio.visibility = View.GONE
        llHashCode.visibility = View.GONE
//        llHashtag.visibility = View.GONE
        llLocation.visibility = View.GONE
        llAudiance.visibility = View.GONE
        btnPostNow.visibility = View.GONE
        if (view != null) {
            view.visibility = View.VISIBLE
        }
    }


}