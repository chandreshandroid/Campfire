package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.media.*
import android.net.Uri
import android.os.*
import android.text.*
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.chand.progressbutton.ProgressButton
import com.facebook.FacebookSdk.getCacheDir
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.CreatePostSaveViewPagerAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.aws.AWSConfiguration
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.aws.AwsMultipleUpload
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.GeocodingLocation
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.OnPlacesDetailsListener
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.PlaceAPI
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.PlacesAutoCompleteAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.model.Place
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.location.model.PlaceDetails
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.UpdatePostModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.*
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource

import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
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

class CreateSavePostActivity : AppCompatActivity() {

    var headerTitle: String = ""
    var page_position = 0
    private var dots: Array<ImageView?>? = null
    private var dotsCount: Int = 0
    var exoPlayer: SimpleExoPlayer? = null
    var videoviewfullscreen: PlayerView? = null
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
    private var mRecorder: MediaRecorder? = null

    var imageLocationName = ""
    var imageLocationLate: Float = 0f
    var imageLocationLong: Float = 0f
    var locationVerifed = "No"
    var postData: TrendingFeedData? = null
    var datumList: ArrayList<MultiplefileData?>? = null
    var selectionType = 0
    private val RECORD_AUDIO_REQUEST_CODE = 123

    private var mPlayer: MediaPlayer? = null
    private var fileNameAudio: String? = null
    private var lastProgress = 0
    private val mHandler: Handler = Handler()
    private var isPlaying = false

    private val mYourBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // now you can call all your fragments method here
            MyUtils.finishActivity(this@CreateSavePostActivity, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        MyUtils.changeStatusBarColor(this, R.color.colorPrimary, true)

        getScrennwidth()
        dynamicLable()

        edit_add_headline?.hint =
            "" + GetDynamicStringDictionaryObjectClass.Add_Headline + " " + GetDynamicStringDictionaryObjectClass.Minimum_5_Characters
        edit_add_description?.hint =
            "" + GetDynamicStringDictionaryObjectClass.Add_Description + " " + GetDynamicStringDictionaryObjectClass.Optional
        tvAddAudio?.text = ("" + GetDynamicStringDictionaryObjectClass.Add_Audio_over_Video)
        tvCurrentLocation?.hint = "" + GetDynamicStringDictionaryObjectClass.Current_Location
        btnSave?.progressText = GetDynamicStringDictionaryObjectClass.Save
        btnPostNow?.progressText = GetDynamicStringDictionaryObjectClass.Post_News
        msgSomthingRong = GetDynamicStringDictionaryObjectClass.Something_Went_Wrong
        msgNoInternet = GetDynamicStringDictionaryObjectClass.No_Internet_Connection

        btnSave.visibility = View.GONE
        tvHeaderText.text = headerTitle
        sessionManager = SessionManager(this@CreateSavePostActivity)
        userData = sessionManager?.get_Authenticate_User()

        LocalBroadcastManager.getInstance(this@CreateSavePostActivity)
            .registerReceiver(mYourBroadcastReceiver, IntentFilter("CreatePost"))

        if (intent != null) {
            if (intent.hasExtra("from")) from = intent.getStringExtra("from")!!
            postData = intent.getSerializableExtra("postData") as TrendingFeedData
//            numberOfImage = intent.getSerializableExtra("imagesArray") as ArrayList<AddImages?>?

        }

        if (postData != null) {

            if (!postData?.postLocationVerified.isNullOrEmpty()) {
                locationVerifed = "" + postData?.postLocationVerified
            }

            if (!postData?.postLocation.isNullOrEmpty()) {

                imageLocationName = "" + postData?.postLocation
                tvCurrentLocation?.setText("" + imageLocationName)
                setLocation(0)
            }

            if (!postData?.postLatitude.isNullOrEmpty()) {

                imageLocationLate = postData?.postLatitude!!.toFloat()
            }

            if (!postData?.postLongitude.isNullOrEmpty()) {

                imageLocationLong = postData?.postLongitude!!.toFloat()
            }

            tvPostVisiBility.text = "" + GetDynamicStringDictionaryObjectClass.Public
            selectionType = 0

            if (!postData?.postPrivacyType.isNullOrEmpty()) {
                if (postData?.postPrivacyType.equals("Public")) {
                    tvPostVisiBility?.text = "" + GetDynamicStringDictionaryObjectClass.Public
                    tvChangePostPrivacy?.text =
                        GetDynamicStringDictionaryObjectClass.Change_to_Private
                } else {
                    tvPostVisiBility.text = "" + GetDynamicStringDictionaryObjectClass.Private
                    tvChangePostPrivacy?.text =
                        GetDynamicStringDictionaryObjectClass.Change_to_Public
                }
            }
            if (!postData?.postDescription.isNullOrEmpty()) {
                if (!postData?.postDescription.isNullOrEmpty()) {
                    edit_add_description?.setEditData("" + postData?.postDescription.toString())
                    tvTextLeft.text =
                        (280 - edit_add_description.text.toString().length).toString() + " " + GetDynamicStringDictionaryObjectClass.Characters_left
                }
            }
            if (!postData?.postHeadline.isNullOrEmpty()) {
                if (!postData?.postHeadline.isNullOrEmpty()) {
                    edit_add_headline?.setText("" + postData?.postHeadline.toString())
                }
            }

            if (!postData?.postSerializedData.isNullOrEmpty()) {
                if (!postData?.postSerializedData!![0].albummedia.isNullOrEmpty()) {

                    datumList = ArrayList()
                    for (i in 0 until postData?.postSerializedData!![0].albummedia.size) {
                        var addImages: List<AddImages>? = null
                        datumList?.add(
                            MultiplefileData(
                                postData?.postSerializedData!![0].albummedia[i].albummediaFile,
                                "true",
                                addImages
                            )
                        )
                    }
                }
            }
        }

        tvTag.text = getString(R.string.tag)

        tvHeaderText.setOnClickListener {
            onBackPressed()
        }

        imv_user_dp.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
        tvFeedUserName.text = userData?.userFirstName + " " + userData?.userLastName

        /*if (bitMap != null) {
            thumnail.setImageURI(Uri.fromFile(bitMap))
        }*/

        when (from) {
            "images" -> {
                rvVideo.visibility = View.GONE
                ll_add_audio.visibility = View.GONE
                baseline_audio.visibility = View.GONE
                val imageViewParam = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, heightNew
                )

                rvImages?.layoutParams = imageViewParam
                rvImages?.visibility = View.VISIBLE

                /*view_pager2.adapter = FeedSubViewPagerAdapter(
                    this@CreateSavePostActivity,
                    postData?.postSerializedData?.get(0)?.albummedia!!
                )*/
//             view_pager2.adapter =CreatePostViewPagerAdapter(this@CreateSavePostActivity, numberOfImage)

                view_pager2.adapter =
                    CreatePostSaveViewPagerAdapter(this@CreateSavePostActivity, postData?.postSerializedData?.get(0)?.albummedia!!)

                if (!postData?.postSerializedData?.get(0)?.albummedia!!.isNullOrEmpty() && postData?.postSerializedData?.get(
                        0
                    )?.albummedia!!.size > 1
                ) {
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
                view_pager2?.setOnClickListener {
                }
            }
            "video" -> {

                rvVideo.visibility = View.VISIBLE
                ll_add_audio.visibility = View.VISIBLE
                baseline_audio.visibility = View.VISIBLE
                rvImages.visibility = View.GONE

                if (!postData?.postSerializedData.isNullOrEmpty()) {
                    if (!postData?.postSerializedData!![0].albummedia.isNullOrEmpty()) {

                        thumnail.setImageURI(RestClient.image_base_url_post + postData?.postSerializedData!![0].albummedia[0].albummediaThumbnail)
                        /*var bitmap: Bitmap?=null

                        bitmap = retriveVideoFrameFromVideo(postData?.postSerializedData!![0]?.albummedia[0]?.albummediaFile)
                        if (bitmap != null) {
                            bitmap = Bitmap.createScaledBitmap(bitmap, 240, 240, false)
                            thumnail.setImageBitmap(bitmap);
                        }*/


                    }

                }

            }
        }

        tvCurrentLocation.setAdapter(PlacesAutoCompleteAdapter(this,  PlaceAPI.Builder()?.apiKey(resources.getString(R.string.google_places_key)).build(this@CreateSavePostActivity)))
        tvCurrentLocation.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                MyUtils.hideKeyboard1(this@CreateSavePostActivity)
                val place = parent.getItemAtPosition(position) as Place
                tvCurrentLocation?.setText(place.description)
                tvCurrentLocation.setSelection(tvCurrentLocation?.text.toString().length)
                var locationAddress = GeocodingLocation.getAddressFromLocation(
                    tvCurrentLocation?.text.toString().trim(),
                    applicationContext, GeocoderHandler()
                )

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

        /*if (!numberOfImage.isNullOrEmpty()) {
            if (!numberOfImage!![0]?.sdcardPath.isNullOrEmpty()) {
                val thumb = ThumbnailUtils.createVideoThumbnail(
                    numberOfImage!![0]?.sdcardPath,
                    MediaStore.Images.Thumbnails.MINI_KIND
                )
                thumnail.setImageBitmap(thumb)
            }
        }*/

        tvChangePostPrivacy.setOnClickListener {

            tvChangePostPrivacy.isEnabled = false
            when (selectionType) {
                0 -> {
                    selectionType = 1
                    tvPostVisiBility.text = "" + GetDynamicStringDictionaryObjectClass.Private
                    tvChangePostPrivacy.text =
                        "" + GetDynamicStringDictionaryObjectClass.Change_to_Public
                }
                1 -> {
                    selectionType = 0
                    tvPostVisiBility.text = "" + GetDynamicStringDictionaryObjectClass.Public
                    tvChangePostPrivacy.text =
                        "" + GetDynamicStringDictionaryObjectClass.Change_to_Private
                }
            }
            /*when (tvChangePostPrivacy.text.toString().trim()) {
                "Change to Private" -> {
                    tvPostVisiBility.text = "Private"
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
                tvTextLeft.text = (280 - s.toString().length).toString() + " Characters left"
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

        btnSave.setOnClickListener {
            //hashTag(edit_add_headline.tagText.trim(),this@CreatePostActivity)
            if (postData != null) {
//            if (numberOfImage != null && numberOfImage?.size!! > 0) {
                when {
                    edit_add_headline.text.toString().trim().isNullOrEmpty() -> {
                        MyUtils.showSnackbar(
                            this@CreateSavePostActivity,
                            "Please enter news headline",
                            rootCreatePost
                        )
                    }
                    !edit_add_headline.text.toString().trim().isNullOrEmpty() && edit_add_headline.text.toString().trim().length < 10 -> {
                        MyUtils.showSnackbar(
                            this@CreateSavePostActivity,
                            "Please enter post headline minimum 10 character",
                            rootCreatePost
                        )
                    }!edit_add_description.text.toString().trim().isNullOrEmpty() && edit_add_description.text.toString().trim().length < 25 -> {
                    MyUtils.showSnackbar(
                        this@CreateSavePostActivity,
                        "Please enter news descriptions minimum 25 character",
                        rootCreatePost
                    )
                }
                    tvCurrentLocation.text.toString().isNullOrEmpty() -> {
                        MyUtils.showSnackbar(
                            this@CreateSavePostActivity,
                            "Please select your location",
                            rootCreatePost
                        )
                    }
                    else -> {
                        if (numberOfImage.isNullOrEmpty()) {
                            createPost("Save", btnSave, numberOfImage!!)

                        } else {
                            uploadPost("Save", btnSave)
                        }

                    }
                }
            } else {
                MyUtils.showSnackbar(
                    this@CreateSavePostActivity,
                    resources.getString(R.string.please_select_image_video),
                    rootCreatePost
                )
            }

        }

        btnPostNow.setOnClickListener {
            if (postData != null) {

//            if (numberOfImage != null && numberOfImage?.size!! > 0) {
                when {
                    edit_add_headline.text.toString().trim().isNullOrEmpty() -> {
                        MyUtils.showSnackbar(
                            this@CreateSavePostActivity,
                            "Please enter news headline",
                            rootCreatePost
                        )
                    }
                    !edit_add_headline.text.toString().trim().isNullOrEmpty() && edit_add_headline.text.toString().trim().length < 10-> {
                        MyUtils.showSnackbar(
                            this@CreateSavePostActivity,
                            "Please enter post headline minimum 10 character",
                            rootCreatePost
                        )
                    }
                    !edit_add_description.text.toString().trim().isNullOrEmpty() && edit_add_description.text.toString().trim().length < 25 -> {
                    MyUtils.showSnackbar(
                        this@CreateSavePostActivity,
                        "Please enter news descriptions minimum 25 character",
                        rootCreatePost
                    )
                }
                    tvCurrentLocation.text.toString().isNullOrEmpty() -> {
                        MyUtils.showSnackbar(
                            this@CreateSavePostActivity,
                            "Please select your location",
                            rootCreatePost
                        )
                    }
                    else -> {

                        if (numberOfImage.isNullOrEmpty()) {
                            createPost("Uploaded", btnSave, numberOfImage!!)
                        } else {
                            uploadPost("Uploaded", btnPostNow)
                        }

                    }
                }
            } else {
                MyUtils.showSnackbar(
                    this@CreateSavePostActivity,
                    resources.getString(R.string.please_select_image_video),
                    rootCreatePost
                )
            }


        }


        ll_add_audio.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPermissionToRecordAudio()
            }

        }

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
            MyUtils.hideKeyboard1(this@CreateSavePostActivity)
            MyUtils.showMessageYesNo(
                this@CreateSavePostActivity,
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
                MyUtils.hideKeyboard1(this@CreateSavePostActivity)
                MyUtils.showMessageYesNo(
                    this@CreateSavePostActivity,
                    "Are you sure you want to add your voice to audio?",
                    object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            Toast.makeText(
                                this@CreateSavePostActivity, "Start Recording..",
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

        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        /**In the lines below, we create a directory VoiceRecorderSimplifiedCoding/Audios in the phone storage
         * and the audios are being stored in the Audios folder  */
        val root: File = Environment.getExternalStorageDirectory()
        val file =
            File(root.absolutePath.toString() + "/Camfire/Audios")
        if (!file.exists()) {
            file.mkdirs()
        }
        fileNameAudio =
            root.absolutePath.toString() + "/Camfire/Audios/" + (System.currentTimeMillis().toString() + ".aac")
        //Log.d("filename", fileNameAudio)
        mRecorder?.setOutputFile(fileNameAudio)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        try {
            mRecorder?.prepare()
            mRecorder?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        lastProgress = 0
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
        headerTitle = GetDynamicStringDictionaryObjectClass.Post_News
    }

    fun addBottomDots(currentPage: Int, layoutDotsTutorial: LinearLayout) {
        // dotsCount = stichingServicData?.stitchingserviceImages!!.size
        //  dotsCount=trendingFeedDatum1?.postSerializedData?.get(0)?.albummedia!!.size
        dotsCount =
            if (postData?.postSerializedData?.get(0)?.albummedia!!.isNullOrEmpty()) 0 else postData?.postSerializedData?.get(
                0
            )?.albummedia!!.size

        layoutDotsTutorial.removeAllViews()

        dots = arrayOfNulls(dotsCount)

        for (i in 0 until dotsCount) {

            dots!![i] = ImageView(this@CreateSavePostActivity)
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
                var videoURI: Uri? = null

                if (numberOfImage.isNullOrEmpty()) {

                    videoURI = Uri.parse(
                        RestClient.image_base_url_post + postData?.postSerializedData!!.get(0).albummedia[0].albummediaFile
                    )

                } else {
                    videoURI =
                        Uri.parse("file:///" + numberOfImage!![0]!!.videoFile.toString())

                }

                val bandwidthMeter: BandwidthMeter? = null
                val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
                val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)


                exoPlayer =
                    ExoPlayerFactory.newSimpleInstance(this@CreateSavePostActivity, trackSelector)

                val cacheDataSourceFactory =
                    CacheDataSourceFactory1(this@CreateSavePostActivity, 5 * 1024 * 1024)

                val mediaSource = ExtractorMediaSource.Factory(cacheDataSourceFactory)
                    .createMediaSource(videoURI)
                stopPlayer()

                exoPlayer =
                    ExoPlayerFactory.newSimpleInstance(this@CreateSavePostActivity, trackSelector)


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
                //muteVideo(isMuteing())
                if (from.equals("audio", false)) {
                    exoPlayer?.volume = 0f
                }

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
                            Player.STATE_BUFFERING ->
                                progressBar.visibility = View.VISIBLE


                            Player.STATE_ENDED ->
                                if (exoPlayer != null) {
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
                                        this@CreateSavePostActivity, "Start Recording..",
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


        /* try {

             var videoURI = Uri.parse("file://"+numberOfImage?.get(0)!!.sdcardPath)

             var bandwidthMeter = DefaultBandwidthMeter()
             var videoTrackSelectionFactory =AdaptiveTrackSelection.Factory(bandwidthMeter)
             var trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)


 // This is the MediaSource representing the media to be played.

             exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)


             val cacheFolder = File(this@CreatePostActivity.cacheDir, "exoCache")

             val cacheEvictor =
                 LeastRecentlyUsedCacheEvictor(1 * 1024 * 1024) // My cache size will be 1MB and it will automatically remove least recently used files if the size is reached out.

             val databaseProvider: DatabaseProvider =
                 ExoDatabaseProvider(this@CreatePostActivity)


             if (cache == null)
                 try {
                     cache?.release()
                     cache = null
                     cache = SimpleCache(cacheFolder, cacheEvictor, databaseProvider)

                 } catch (e: Exception) {
                     e.printStackTrace()
                 }

             val cacheDataSourceFactory =
                 CacheDataSourceFactory(cache, DefaultHttpDataSourceFactory("ExoplayerDemo"))

             // This is the MediaSource representing the media to be played.
             val videoSource: MediaSource = ExtractorMediaSource.Factory(
                 CacheDataSourceFactory(
                     this@FullscreenVideoActivity,
                     100 * 1024 * 1024,
                     5 * 1024 * 1024
                 )
             )
                 .createMediaSource(videoURI)
             exoPlayer =
                 ExoPlayerFactory.newSimpleInstance(this@FullscreenVideoActivity, trackSelector)


             val videoSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                 .createMediaSource(videoURI)

             exoPlayer?.prepare(videoSource)
             videoviewfullscreen?.setPlayer(exoPlayer)
             exoPlayer?.playWhenReady = true
             exoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
             muteVideo(isMuteing())

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
                         Player.STATE_BUFFERING ->
                             progressBar.visibility = View.VISIBLE


                         Player.STATE_ENDED -> if (exoPlayer != null) {
                             exoPlayer?.seekTo(0)
                             thumnail.visibility = View.VISIBLE
                             progressBar.visibility = View.GONE
                             playIcon.visibility = View.VISIBLE
                             pausePlayer()

                         }
                         Player.STATE_IDLE -> {
                         }

                         Player.STATE_READY -> {
                             if (thumnail.visibility == View.VISIBLE)
                                 thumnail.visibility = View.GONE

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
                     stopPlayer()
                     progressBar.visibility = View.GONE
                     thumnail.visibility = View.VISIBLE
                     stopPlayer()
                 }

                 override fun onPositionDiscontinuity(reason: Int) {

                 }

                 override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

                 }

                 override fun onSeekProcessed() {

                 }
             })



         }catch (e:Exception) {
             Log.e("MainAcvtivity", " exoplayer error " + e.toString())
         }*/


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
        datumList1: List<AddImages?>
    ) {
         if(datumList1.isNullOrEmpty())
         {
             btn.startAnimation()
             MyUtils.setViewAndChildrenEnabled(rootCreatePost, false)
         }


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
            jsonObject.put("postID", postData?.postID)
            jsonObject.put(
                "postHeadline",
                edit_add_headline.text.toString().trim()
            )
            jsonObject.put(
                "postDescription",
                edit_add_description.tagText.toString().trim()
            )

            jsonObject.put("postLocation", tvCurrentLocation?.text.toString())
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
                        edit_add_description.tagText,
                        this@CreateSavePostActivity
                    ).isNullOrEmpty()
                ) {
                    hashTag(
                        edit_add_description.tagText,
                        this@CreateSavePostActivity
                    ).joinToString {
                        it.toString()
                    }
                } else {
                    ""
                })
            jsonObject.put("posttag",
                if (!edit_add_description?.contentFriendsTagList?.isNullOrEmpty()!!) {
                    edit_add_description?.contentFriendsTagList?.joinToString {
                        it.userID.toString()
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

                    for (i in 0 until postData?.postSerializedData!![0].albummedia.size) {
                        val jsonObjectAlbummedia = JSONObject()
                        try {
                            jsonObjectAlbummedia.put(
                                "albummediaFile",
                                postData?.postSerializedData!![0].albummedia[i].albummediaFile
                            )
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
                    var jsonObjectAlbummedia: JSONObject? = null

                    if (datumList1.isNullOrEmpty()) {
                        jsonObjectAlbummedia = JSONObject()

                        try {
                            jsonObjectAlbummedia?.put(
                                "albummediaFile",
                                postData?.postSerializedData!![0].albummedia[0].albummediaFile
                            )
                            jsonObjectAlbummedia?.put(
                                "albummediaThumbnail",
                                postData?.postSerializedData!![0].albummedia[0].albummediaThumbnail
                            )
                            jsonObjectAlbummedia?.put("albummediaFileType", "Video")
                            jsonArrayAlbummedia.put(0, jsonObjectAlbummedia)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        jsonObjectAlbummedia = JSONObject()
                        try {
                            jsonObjectAlbummedia.put("albummediaFile", datumList1[0]!!.imageName)
                            jsonObjectAlbummedia.put(
                                "albummediaThumbnail",
                                postData?.postSerializedData!![0].albummedia[0].albummediaThumbnail
                            )
                            jsonObjectAlbummedia.put("albummediaFileType", "Video")
                            jsonArrayAlbummedia.put(0, jsonObjectAlbummedia)
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
        }

        jsonArray.put(jsonObject)
        Log.e("System out", "Print final json :=  " + jsonArray.toString())

        val createPostModel =
            ViewModelProviders.of(this@CreateSavePostActivity).get(UpdatePostModel::class.java)
        createPostModel.apiFunction(this@CreateSavePostActivity, false, jsonArray.toString())
            .observe(this@CreateSavePostActivity,
                Observer<List<CommonPojo>> { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(this@CreateSavePostActivity)
                            btn.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(rootCreatePost, true)

                            if (!response[0].message!!.isNullOrEmpty()) {
                                MyUtils.showSnackbarkotlin(
                                    this@CreateSavePostActivity,
                                    rootCreatePost,
                                    response[0].message!!
                                )

                                val intent2 = Intent("CreatePost")
                                intent2.putExtra("from", postCreateType)
                                intent2.putExtra("msg", response[0].message!!)
                                LocalBroadcastManager.getInstance(this@CreateSavePostActivity)
                                    .sendBroadcast(intent2)

                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@CreateSavePostActivity,
                                    rootCreatePost,
                                    mag_failed_to_login
                                )
                                val intent2 = Intent("CreatePost")
                                intent2.putExtra("from", postCreateType)
                                intent2.putExtra("msg", response[0].message!!)
                                LocalBroadcastManager.getInstance(this@CreateSavePostActivity)
                                    .sendBroadcast(intent2)
                            }
                        } else {
                            btn.endAnimation()
                            MyUtils.setViewAndChildrenEnabled(rootCreatePost, true)
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(this@CreateSavePostActivity)) {
                                if (!response[0].message!!.isNullOrEmpty()) {
                                    MyUtils.showSnackbarkotlin(
                                        this@CreateSavePostActivity,
                                        rootCreatePost,
                                        response[0].message!!
                                    )
                                }

                            } else {
                                MyUtils.showSnackbarkotlin(
                                    this@CreateSavePostActivity,
                                    rootCreatePost,
                                    msgNoInternet
                                )
                            }
                        }

                    } else {
                        btn.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(rootCreatePost, true)
                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(this@CreateSavePostActivity)) {
                            MyUtils.showSnackbarkotlin(
                                this@CreateSavePostActivity,
                                rootCreatePost,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@CreateSavePostActivity,
                                rootCreatePost,
                                msgNoInternet
                            )
                        }
                    }
                })
    }


    override fun onBackPressed() {
        stopPlayer()
        MyUtils.finishActivity(this@CreateSavePostActivity, true)
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
        LocalBroadcastManager.getInstance(this@CreateSavePostActivity)
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
            string.add(mat.group())
        }

        return string
    }

    private fun getScrennwidth(): Int {

        val wm =
            this@CreateSavePostActivity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
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


    private fun muxing() {
        var outputFile = ""
        try {
            val root: File = Environment.getExternalStorageDirectory()
            val file =
                File(Environment.getExternalStorageDirectory().toString() + File.separator + "Camfire/" + "Merged_" + getTimeStamp() + ".mp4")

            /*if (!file.exists()) {
                file.mkdirs()
            }*/

            file.createNewFile()
            outputFile = file.absolutePath

            val videoExtractor = MediaExtractor()

            // val afdd: AssetFileDescriptor = assets.openFd(video.name)
            var videoPath =
                File("/storage/emulated/0/Pictures/Campfire/" + postData?.postSerializedData!!.get(0).albummedia[0].albummediaFile.toString())
            videoExtractor.setDataSource(videoPath.path)

            var auioPath = File(fileNameAudio)

            val audioExtractor = MediaExtractor()
            audioExtractor.setDataSource(auioPath.path)

            Log.d("Video", "Video Extractor Track Count " + videoExtractor.trackCount)
            Log.d("Audio", "Audio Extractor Track Count " + audioExtractor.trackCount)

            val muxer =
                MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            muxer.setOrientationHint(retriveVideoFrameFromVideo("/storage/emulated/0/Pictures/Campfire/" + postData?.postSerializedData!![0].albummedia[0].albummediaFile.toString())!!)

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
            numberOfImage?.clear()
            numberOfImage?.add(
                AddImages(
                    file.absolutePath,
                    false,
                    null,
                    -1,
                    "",
                    false,
                    "0",
                    file
                )
            )
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
                            if (MyUtils.isInternetAvailable(this@CreateSavePostActivity))
                                MyUtils.showSnackbar(
                                    this@CreateSavePostActivity,
                                    resources.getString(R.string.error_crash_error_message),
                                    rootCreatePost
                                )
                            else
                                MyUtils.showSnackbar(
                                    this@CreateSavePostActivity,
                                    resources.getString(R.string.error_common_network),
                                    rootCreatePost
                                )
                        }

                        override fun onSuccessUploadFile(awsFileList: ArrayList<AddImages?>) {
                            Log.e("dfsfsdf", "" + awsFileList)
                           createPost(s, btnSave, awsFileList)
                        }
                    }


            } else {
                //  createPost(s, btnSave, numberOfImage!!)
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
                this@CreateSavePostActivity,
                resources.getString(R.string.please_select_image_video),
                rootCreatePost
            )
        }
    }

}