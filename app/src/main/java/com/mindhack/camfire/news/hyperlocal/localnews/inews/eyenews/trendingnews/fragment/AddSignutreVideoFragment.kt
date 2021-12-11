package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.CameraActivitySignatureKotlin
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.aws.AWSConfiguration
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.aws.AwsMultipleUpload
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.OnBoardingModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.AddImages
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.CacheDataSourceFactory1
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
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
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_add_signutre_video.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddSignutreVideoFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager : SessionManager? = null
    var userData : RegisterPojo.Data? = null

    var titleHeader=""
    private var mediaChooseBottomSheet = MediaChooseVideoBottomsheet()

    private val TAKE_PICTURE = 1
    private var pictureUri: Uri? = null
    private var picturePath: String? = null
    private val SELECT_PICTURE = 2
    private var timeForImageName: Long = 0
    private var imgName: String? = null
    private var actualImage: File? = null
    var uploadImageFile : File? = null

    var exoPlayer: SimpleExoPlayer? = null
    var videoviewfullscreen: PlayerView? = null
    var cache: SimpleCache? = null
    var msgNoInternet=""
    var msgSomthingRong=""
    var selectedProfilePicture = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if(v==null)
        {
            v=inflater.inflate(R.layout.fragment_add_signutre_video, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        (mActivity as MainActivity).showHideBottomNavigation(false)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager != null && sessionManager!!.isLoggedIn()){
            userData = sessionManager?.get_Authenticate_User()
        }
        dynamicString()


        if(userData!=null)
        {
            if(!userData?.userSignatureVideo.isNullOrEmpty())
            {
                ll_add_video.visibility=View.GONE
                cardViewVideo.visibility=View.VISIBLE
                ll_add_video_server.visibility=View.GONE

            }
            else{
                ll_add_video.visibility=View.VISIBLE
                cardViewVideo.visibility=View.GONE
                ll_add_video_server.visibility=View.GONE
            }
        }

        btnAddSignatureVideo.text=GetDynamicStringDictionaryObjectClass.Add_this_Video
        tvFeedClapCount.text=GetDynamicStringDictionaryObjectClass.This_Video_will_be_merge_with_all_your_post

        tvHeaderText?.text = titleHeader

        imgCloseIcon.setOnClickListener {
            (mActivity as  MainActivity).onBackPressed()
        }


        btnAddSignatureVideo.setOnClickListener {

            val currentapiVersion = Build.VERSION.SDK_INT
          if (currentapiVersion >= Build.VERSION_CODES.M) {
                getWriteStoragePermissionOther()
            } else {

              (activity as MainActivity).startActivityForResult(
                  Intent(
                      mActivity,
                      CameraActivitySignatureKotlin::class.java
                  ), 1313
              )
          }
        }


        playicon.setOnClickListener {
            playVideo(
                0,
                exoPlayer1,
                thumnail!!,
                thumnail!!,
                pb!!,
                playicon!!

            )
        }


        btnAddSignatureVideoServer.setOnClickListener {
            if (uploadImageFile != null){
                uploadVideo(uploadImageFile!!)
            }else{
                userAddSignatureVideo("Add")
            }
        }

        editicon.setOnClickListener {
            (activity as MainActivity).startActivityForResult(
                Intent(
                    mActivity,
                    CameraActivitySignatureKotlin::class.java
                ), 1313
            )
//            val currentapiVersion = Build.VERSION.SDK_INT
//            if (currentapiVersion >= Build.VERSION_CODES.M) {
//                getWriteStoragePermissionOther()
//            } else {
//
//                mediaChooseBottomSheet.show((mActivity!! as MainActivity).supportFragmentManager, "BottomSheet demoFragment")
//            }
        }

        deleteicon.setOnClickListener {
            uploadImageFile=File("")
            userData?.userSignatureVideo=""
            userAddSignatureVideo("")
        }



    }


    fun uploadVideo(file: File){

        if (!btnAddSignatureVideoServer.isStartAnim) btnAddSignatureVideoServer.startAnimation()
        val arrayImages = java.util.ArrayList<AddImages?>()
        arrayImages.clear()

        arrayImages.add(
            AddImages(
                file.toString(),
                false,
                null,
                -1,
                "",
                false,
                "0",
                null
            )
        )


        if (arrayImages.size > 0) {

            val awsMultipleUpload: AwsMultipleUpload =
                object : AwsMultipleUpload(mActivity!!, arrayImages, AWSConfiguration.mediaPath,"video") {
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
                        btnAddSignatureVideoServer.endAnimation()
                        MyUtils.setViewAndChildrenEnabled(rootAddSignatureLayoutMain!!, true)
                        if (MyUtils.isInternetAvailable(mActivity!!))
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_crash_error_message),
                                rootAddSignatureLayoutMain!!
                            )
                        else
                            MyUtils.showSnackbar(
                                mActivity!!,
                                resources.getString(R.string.error_common_network),
                                rootAddSignatureLayoutMain!!
                            )
                    }

                    override fun onSuccessUploadFile(awsFileList: ArrayList<AddImages?>) {
                        selectedProfilePicture =
                            awsFileList[0]!!.imageName.toString()
                        userAddSignatureVideo("Add")
                    }
                }


        } else {
            selectedProfilePicture =
                arrayImages[0]!!.imageName.toString()
            userAddSignatureVideo("Add")
        }

    }

    private fun userAddSignatureVideo(from:String) {
          if(from.equals("Add",false))
          {
              if (!btnAddSignatureVideoServer?.isStartAnim!!)
                  btnAddSignatureVideoServer?.startAnimation()
          }


        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            if(sessionManager?.isLoggedIn()!!)
            {
                jsonObject.put("loginuserID", userData?.userID)

            }else
            {
                jsonObject.put("loginuserID",sessionManager?.getsetSelectedLanguage())

            }
            jsonObject.put("languageID", userData?.languageID)
            if(from.equals("Add",false))
            {
                jsonObject.put("userSignatureVideo", selectedProfilePicture)

            }
            else
            {
                jsonObject.put("userSignatureVideo", "")

            }
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("apiType", RestClient.apiType)

            jsonArray.put(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val verifyOTP = ViewModelProviders.of(mActivity!!)
            .get(OnBoardingModel::class.java)

        verifyOTP.apiCall(mActivity!!, jsonArray.toString(), 13)
            .observe(mActivity!!, object : Observer<List<RegisterPojo>?> {
                override fun onChanged(response: List<RegisterPojo>?) {

                    if (!response.isNullOrEmpty()) {
                        if(from.equals("Add",false))
                        {
                            if (btnAddSignatureVideoServer?.isStartAnim!!)
                                btnAddSignatureVideoServer?.endAnimation()
                        }

                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(mActivity!!)
                            if (!response[0].data.isNullOrEmpty()) {
                                sessionManager?.clear_login_session()
                                storeSessionManager(response[0].data!!)

                                (mActivity as MainActivity).getUpdateSessionManager(mActivity!!)
                                 if(from.equals("Add",false))
                                 {

                                     ll_add_video_server.visibility=View.GONE
                                     ll_add_video.visibility=View.GONE
                                     cardViewVideo.visibility=View.VISIBLE
                                     MyUtils.showSnackbarkotlin(
                                         mActivity!!,
                                         rootAddSignatureLayoutMain!!,
                                         response[0].message
                                     )

                                     (activity as MainActivity).onBackPressed()
                                 }

                                else
                                 {

                                     ll_add_video_server.visibility=View.GONE
                                     ll_add_video.visibility=View.VISIBLE
                                     cardViewVideo.visibility=View.GONE

                                 }


                            }



                        } else {
                            //No data and no internet
                            if(from.equals("Add",false)) {
                                ll_add_video_server.visibility = View.GONE
                                ll_add_video.visibility = View.VISIBLE
                                cardViewVideo.visibility = View.GONE
                            }else{
                                ll_add_video_server.visibility = View.GONE
                                ll_add_video.visibility = View.GONE
                                cardViewVideo.visibility = View.VISIBLE
                            }


                            MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    rootAddSignatureLayoutMain!!,
                                response[0].message
                                )

                        }

                    } else {

                        if(from.equals("Add",false)) {
                            ll_add_video_server.visibility = View.GONE
                            ll_add_video.visibility = View.VISIBLE
                            cardViewVideo.visibility = View.GONE
                            if (btnAddSignatureVideoServer?.isStartAnim!!)
                                btnAddSignatureVideoServer?.endAnimation()

                        }else{
                            ll_add_video_server.visibility = View.GONE
                            ll_add_video.visibility = View.GONE
                            cardViewVideo.visibility = View.VISIBLE
                        }
                        //No internet and somting went rong
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                rootAddSignatureLayoutMain!!,
                                msgSomthingRong
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                rootAddSignatureLayoutMain!!,
                                msgNoInternet
                            )
                        }
                    }
                }
            })



    }


    fun getWriteStoragePermissionOther() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            mActivity as MainActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getReadStoragePermissionOther()
        } else {
            /*ActivityCompat.requestPermissions(
                mActivity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MyUtils.Per_REQUEST_WRITE_EXTERNAL_STORAGE_1
            )*/

            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MyUtils.Per_REQUEST_WRITE_EXTERNAL_STORAGE_1
            )
        }
    }

    fun getReadStoragePermissionOther() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            mActivity as MainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getCameraPermissionOther()
        } else {
            /* ActivityCompat.requestPermissions(
                 mActivity!!,
                 arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                 MyUtils.Per_REQUEST_READ_EXTERNAL_STORAGE_1
             )*/
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MyUtils.Per_REQUEST_READ_EXTERNAL_STORAGE_1
            )
        }
    }

    fun getCameraPermissionOther() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(mActivity as MainActivity, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Bundle().apply {
                putString("from","AddSignatureVideo")
                mediaChooseBottomSheet.arguments=this

            }
            (activity as MainActivity).startActivityForResult(
                Intent(
                    mActivity,
                    CameraActivitySignatureKotlin::class.java
                ), 1313
            )
        } else {
            /* ActivityCompat.requestPermissions(
                 mActivity!!,
                 arrayOf(Manifest.permission.CAMERA),
                 MyUtils.Per_REQUEST_CAMERA_1
             )*/
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MyUtils.Per_REQUEST_CAMERA_1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MyUtils.Per_REQUEST_WRITE_EXTERNAL_STORAGE_1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getReadStoragePermissionOther()
            } else {
                getWriteStoragePermissionOther()
            }
            MyUtils.Per_REQUEST_READ_EXTERNAL_STORAGE_1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCameraPermissionOther()
            } else {
                getReadStoragePermissionOther()
            }
            MyUtils.Per_REQUEST_CAMERA_1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                (activity as MainActivity).startActivityForResult(
                    Intent(
                        mActivity,
                        CameraActivitySignatureKotlin::class.java
                    ), 1313
                )
            } else {
                getCameraPermissionOther()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            timeForImageName = System.currentTimeMillis()
            when (requestCode) {
                TAKE_PICTURE -> if (mediaChooseBottomSheet.selectedImage() != null) {
                    pictureUri = mediaChooseBottomSheet.selectedImage()

                    picturePath = pictureUri?.path
                    Log.e("System out","Print jjj: == "+ picturePath)
                    var bm = BitmapFactory.decodeFile(File(picturePath).absolutePath.toString())
//                    profile_imv_dp?.setImageBitmap(bm)

//                    profile_imv_dp?.setImageURI(Uri.fromFile(File(picturePath)), this@SaveProfileFragment)
                    Log.d("compressedImage Name", picturePath.toString())
//                    createGroupImageGroup?.setImageURI(Uri.fromFile(File(picturePath)), mActivity!!)
                    actualImage = File(picturePath)

                    var si: Int=-1
                    try {
                        val mpl: MediaPlayer = MediaPlayer.create(mActivity!!, pictureUri)
                        si=mpl.duration
                    } catch (e: Exception) {

                    }
                      if(si>3769)
                      {
                          ll_add_video.visibility=View.VISIBLE
                          cardViewVideo.visibility=View.GONE
                          ll_add_video_server.visibility=View.GONE
                          (activity as MainActivity).showSnackBar("You can add only 3 second video")
                      }
                    else
                      {
                          uploadImageFile = actualImage!!

                          ll_add_video.visibility=View.GONE
                          cardViewVideo.visibility=View.VISIBLE
                          ll_add_video_server.visibility=View.VISIBLE
                      }





                }
                SELECT_PICTURE -> {
                    if (data != null && data.data != null){
                        try {
                            pictureUri = data.data
//                            profile_imv_dp?.setImageURI(data.data)
                            picturePath = MyUtils.getPath(pictureUri, mActivity!!)
                            actualImage = File(picturePath!!)
                        }catch (e : java.lang.Exception){
                            actualImage = File(MyUtils.getPathFromInputStreamUri(mActivity!!, data.data)!!)
                        }

                        var si: Int=-1
                        try {
                            val mpl: MediaPlayer = MediaPlayer.create(mActivity!!, pictureUri)
                            si=mpl.duration
                        } catch (e: Exception) {

                        }
                        if(si>3769)
                        {
                            ll_add_video.visibility=View.VISIBLE
                            cardViewVideo.visibility=View.GONE
                            ll_add_video_server.visibility=View.GONE
                            (activity as MainActivity).showSnackBar("You can add only 3 second video")

                        }
                        else
                        {
                            uploadImageFile = actualImage!!

                            ll_add_video.visibility=View.GONE
                            cardViewVideo.visibility=View.VISIBLE
                            ll_add_video_server.visibility=View.VISIBLE
                        }
                    }
                }
            }
        } else if (resultCode == 1313) {
            if (data != null) {
                uploadImageFile = File(data.getStringExtra("VideoFile"))

                if (data.getStringExtra("VideoPath") != null){
                    val filePath = data.getStringExtra("VideoPath")!!
                    val thumb = ThumbnailUtils.createVideoThumbnail(
                        filePath,
                        MediaStore.Images.Thumbnails.MINI_KIND
                    )
                    thumnail.setImageBitmap(thumb)
                }
                ll_add_video.visibility = View.GONE
                cardViewVideo.visibility = View.VISIBLE
                ll_add_video_server.visibility = View.VISIBLE
            }
        }
    }


    fun playVideo(
        videoPlayPosition: Int,
        playerView: PlayerView,
        thumnail: ImageView,
        volume: View?,
        progressBar: ProgressBar,
        playIcon: ImageView
    ) {

        mActivity?.runOnUiThread {
            val mHandler = Handler(Looper.getMainLooper())
            mHandler.post {
                //                val videoURI = MyUtils.videoURI
//                   numberOfImage?.get(0)!!.sdCardUri
                val bandwidthMeter: BandwidthMeter? = null

                var videoTrackSelectionFactory: AdaptiveTrackSelection.Factory? = null
                videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)

                var trackSelector: DefaultTrackSelector? = null
                trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

                exoPlayer =
                    ExoPlayerFactory.newSimpleInstance(mActivity, trackSelector)


                var dataSpec: DataSpec? = null

                if(userData!=null)
                {
                    if(!userData?.userSignatureVideo.isNullOrEmpty())
                    {

                        dataSpec =
                            DataSpec(Uri.parse(RestClient.image_base_url_post + userData?.userSignatureVideo))
                    }
                    else{
                        dataSpec =
                            DataSpec(Uri.parse("file:///" + uploadImageFile))

                    }
                }
                else{
                    dataSpec =
                        DataSpec(Uri.parse("file:///" + uploadImageFile))

                }

                val fileDataSource = FileDataSource()
                try {
                    fileDataSource.open(dataSpec)
                } catch (e: FileDataSource.FileDataSourceException) {
                    e.printStackTrace()
                }

                val factory = DataSource.Factory { fileDataSource }

                val cacheDataSourceFactory = CacheDataSourceFactory1(mActivity!!, 5 * 1024 * 1024)


                val mediaSource = ExtractorMediaSource(
                    fileDataSource.uri,
                    cacheDataSourceFactory, DefaultExtractorsFactory(), null, null
                )

                stopPlayer()

                exoPlayer =
                    ExoPlayerFactory.newSimpleInstance(mActivity, trackSelector)

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
                muteVideo(isMuteing())

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
            mActivity?.runOnUiThread(Runnable {
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
        stopPlayer()
    }

    private fun dynamicString() {

        titleHeader = GetDynamicStringDictionaryObjectClass.Add_Signature_Video

        msgSomthingRong = GetDynamicStringDictionaryObjectClass.msgSomthingRong
        msgNoInternet = GetDynamicStringDictionaryObjectClass.msgNoInternet

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
            driverdata[0]!!.userProfilePicture!!
            )
    }
}
