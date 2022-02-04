package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.darsh.multipleimageselect.activities.AlbumSelectActivity
import com.darsh.multipleimageselect.helpers.Constants
import com.darsh.multipleimageselect.models.Image
import com.google.android.gms.common.util.IOUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.BuildConfig
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.CameraActionListAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.cameraview.Option
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.cameraview.OptionView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.AddImages
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.LocationProvider
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.otaliastudios.cameraview.*
import com.otaliastudios.cameraview.controls.*
import com.otaliastudios.cameraview.filter.Filters
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_take_picture_video.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class CameraActivityKotlin : AppCompatActivity(), View.OnClickListener, OptionView.Callback {

    private var camera: CameraView? = null

    // private var controlPanel: ViewGroup? = null
    private var mCaptureTime: Long = 0

    private var mCurrentFilter = 0
    private val mAllFilters = Filters.values()

    internal var arrayActionList = ArrayList<String>()
    var layoutManager: LinearLayoutManager? = null
    var adapterCameraAction: CameraActionListAdapter? = null
    var recyclerView: RecyclerView? = null

    internal var isVideo: Boolean? = true
    internal var canStopVideo: Boolean? = false
    var isGallary: Boolean? = false
    internal var flashOn: Boolean? = false
    var capturePicture: ImageView? = null
    var imageFlash: ImageView? = null


    internal var compressedImage: File? = null

    //private static TAKE_PICTURE = 1
    private var pictureUri: Uri? = null
    private var picturePath: String? = null
    internal var SELECT_PICTURE = 2
    internal var timeForImageName: Long? = 0L
    private var actualImage: File? = null

    //    private var mediaChooseBottomSheet = MediaChooseImageBottomsheet()
    //    internal var pbDialog: Dialog? = null
    internal var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    var counter = 0
    var mHandler = Handler()
    private lateinit var mRunnable: Runnable
    var mHandler1: Handler? = null
    var isStopActivityBackPress = false
    private var locationProvider: LocationProvider? = null
    var lat=""
    var lang=""
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_take_picture_video)
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE)
        mHandler1 = Handler()
        camera = findViewById(R.id.camera)
        camera!!.setLifecycleOwner(this)
        camera?.engine = Engine.CAMERA2
        camera!!.addCameraListener(Listener())
        edit.visibility = View.VISIBLE
        val currentapiVersion = Build.VERSION.SDK_INT
        if (currentapiVersion >= Build.VERSION_CODES.M) {
            permissionLocation()
        } else {

        }

       if (USE_FRAME_PROCESSOR) {
        camera!!.addFrameProcessor(object : FrameProcessor {
            private var lastTime = System.currentTimeMillis()
            override fun process(frame: Frame) {
                val newTime = frame.time
                val delay = newTime - lastTime
                lastTime = newTime
                LOG.e("Frame delayMillis:", delay, "FPS:", 1000 / delay)
                if (DECODE_BITMAP) {
                    val yuvImage = YuvImage(
                        frame.getData(), ImageFormat.JPEG,
                        frame.size.width,
                        frame.size.height, null
                    )
                    val jpegStream = ByteArrayOutputStream()
                    yuvImage.compressToJpeg(
                        Rect(
                            0, 0,
                            frame.size.width,
                            frame.size.height
                        ), 100, jpegStream
                    )
                    val jpegByteArray = jpegStream.toByteArray()
                    val bitmap =
                        BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.size)

                    bitmap.toString()
                }
            }
        })
       }


        findViewById<View>(R.id.edit).setOnClickListener(this)
        findViewById<View>(R.id.capturePicture).setOnClickListener(this)
//      findViewById<View>(R.id.capturePictureSnapshot).setOnClickListener(this)
        findViewById<View>(R.id.captureVideo).setOnClickListener(this)
        findViewById<View>(R.id.captureVideoSnapshot).setOnClickListener(this)
        findViewById<View>(R.id.toggleCamera).setOnClickListener(this)
        findViewById<View>(R.id.changeFilter).setOnClickListener(this)
        findViewById<View>(R.id.imageFlash).setOnClickListener(this)
        findViewById<View>(R.id.imagePrevious).setOnClickListener(this)

        //controlPanel = findViewById(R.id.controls)
        /*  controlPanel?.visibility = View.GONE
          val group = controlPanel!!.getChildAt(0) as ViewGroup*/

        val watermark = findViewById<View>(R.id.watermark)

        recyclerView = findViewById(R.id.recyclerviewList)
        capturePicture = findViewById(R.id.capturePicture)
        imageFlash = findViewById(R.id.imageFlash)

        val options = Arrays.asList(
            // Layout
            Option.Width(), Option.Height(),
            // Engine and preview
            Option.Mode(), Option.Engine(), Option.Preview(),
            // Some controls
            Option.Flash(), Option.WhiteBalance(), Option.Hdr(),
            Option.PictureMetering(), Option.PictureSnapshotMetering(),
            // Video recording
            Option.PreviewFrameRate(), Option.VideoCodec(), Option.Audio(),
            // Gestures
            Option.Pinch(), Option.HorizontalScroll(), Option.VerticalScroll(),
            Option.Tap(), Option.LongTap(),
            // Watermarks
            Option.OverlayInPreview(watermark),
            Option.OverlayInPictureSnapshot(watermark),
            Option.OverlayInVideoSnapshot(watermark),
            // Other
            Option.Grid(), Option.GridColor(), Option.UseDeviceOrientation()
        )
        val dividers = Arrays.asList(
            // Layout
            false, true,
            // Engine and preview
            false, false, true,
            // Some controls
            false, false, false, false, true,
            // Video recording
            false, false, true,
            // Gestures
            false, false, false, false, true,
            // Watermarks
            false, false, true,
            // Other
            false, false, true
        )

        val animator = ValueAnimator.ofFloat(1f, 0.8f)
        animator.duration = 300
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener { animation ->
            val scale = animation.animatedValue as Float
            watermark.scaleX = scale
            watermark.scaleY = scale
            watermark.rotation = watermark.rotation + 2
        }
        animator.start()

        layoutManager =
            LinearLayoutManager(this@CameraActivityKotlin, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.layoutManager = layoutManager
        setActionAdapter()

        val snapHelper = object : LinearSnapHelper() {
            override fun findTargetSnapPosition(
                layoutManager: RecyclerView.LayoutManager?,
                velocityX: Int,
                velocityY: Int
            ): Int {
                val centerView = findSnapView(layoutManager!!) ?: return RecyclerView.NO_POSITION

                val position = layoutManager.getPosition(centerView)
                var targetPosition = -1
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1
                    } else {
                        targetPosition = position + 1
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1
                    } else {
                        targetPosition = position + 1
                    }
                }

                val firstItem = 0
                val lastItem = layoutManager.itemCount - 1
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem))
                return targetPosition
            }
        }
        snapHelper.attachToRecyclerView(recyclerView)
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
        geocoder = Geocoder(this@CameraActivityKotlin, Locale.getDefault())

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

    private val REQUEST_CODE_Location_PERMISSIONS = 6

    @RequiresApi(Build.VERSION_CODES.M)
    private fun permissionLocation() {
        if (!addPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            val message = getString(R.string.grant_access_location)

            MyUtils.showMessageOKCancel(this@CameraActivityKotlin, message, "Use location service?",
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

    fun getCurrentLocation() {
        locationProvider = LocationProvider(
            this@CameraActivityKotlin,
            LocationProvider.HIGH_ACCURACY,
            object : LocationProvider.CurrentLocationCallback {
                override fun handleNewLocation(location: Location) {

                    //Log.d("currentLocation", location.toString())

                    locationProvider?.disconnect()
//                    MyUtils.currentLattitude=location.latitude
//                    MyUtils.currentLongitude=location.longitude

                    MyUtils.currentLattitudeFix = location.latitude
                    MyUtils.currentLongitudeFix = location.longitude
                    CurrentCityName(location.latitude, location.longitude)

                    // if driver is Online location send to server
                }
            })
        locationProvider!!.connect()
    }

    fun startVideoTimeCounter() {
        mHandler.postDelayed({
            counter = counter + 1
            setCounterString(counter)
        }, 1000)
    }

    private fun setCounterString(counter: Int) {
        var finalString = ""
        var finalCounter = counter

        if (counter < 60) {
            if (counter < 10) finalString = "00:0$counter" else finalString = "00:$counter"
        } else if (counter in 60..119) {
            finalCounter -= 60
            if (finalCounter < 10) finalString = "01:0$finalCounter" else finalString =
                "01:$finalCounter"
        }
        videoTime.text = finalString
        startVideoTimeCounter()
    }

    private fun setActionAdapter() {
        arrayActionList.clear()
        arrayActionList.add("" + GetDynamicStringDictionaryObjectClass?.Video)
        arrayActionList.add("" + GetDynamicStringDictionaryObjectClass?.Photo)

        adapterCameraAction = CameraActionListAdapter(
            this@CameraActivityKotlin,
            object : CameraActionListAdapter.OnItemClick {
                override fun onClicklisneter(
                    pos: Int,
                    name: String

                ) {
                    if (pos == 0) {

                        if (!isVideo!!) {
                            adapterCameraAction?.mSelection = pos
                            isVideo = true
                            canStopVideo = false
                            capturePicture?.setImageResource(R.drawable.icon_camera)

                            counter = 0
                            videoTime?.visibility = View.INVISIBLE
                            mHandler.removeCallbacksAndMessages(null)
                        }


                    } else {
                        if (!canStopVideo!!) {
                            adapterCameraAction?.mSelection = pos

                            if (camera?.isTakingVideo!!) camera?.stopVideo()
                            isVideo = false
                            canStopVideo = false
                            capturePicture?.setImageResource(R.drawable.icon_camera)
                        }
                    }
                    adapterCameraAction?.notifyDataSetChanged()
                }
            },
            arrayActionList
        )
        recyclerView?.adapter = adapterCameraAction
        adapterCameraAction?.mSelection = 0
        adapterCameraAction?.notifyDataSetChanged()
    }

    private fun message(content: String, important: Boolean) {
        if (important) {
            LOG.w(content)
            Toast.makeText(this, content, Toast.LENGTH_LONG).show()
        } else {
            LOG.i(content)
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
        }
    }

    private inner class Listener : CameraListener() {

    override fun onCameraOpened(options: CameraOptions) {
            /*val group = controlPanel!!.getChildAt(0) as ViewGroup
            for (i in 0 until group.childCount) {
                val view = group.getChildAt(i) as OptionView<*>
                view.onCameraOpened(camera, options)
            }*/
        }


    override fun onCameraError(exception: CameraException) {
            super.onCameraError(exception)
            if (isVideo!!) {
                mHandler.removeCallbacksAndMessages(null)
                counter = 0
                videoTime.visibility = View.INVISIBLE
                canStopVideo = false
                capturePicture?.setImageResource(R.drawable.icon_camera)
            }
            message("Got CameraException #" + exception.reason, true)
        }

    override fun onPictureTaken(result: PictureResult) {
            super.onPictureTaken(result)
            camera!!.flash = Flash.OFF
            if (camera!!.isTakingVideo) {
                // message("Captured while taking video. Size=" + result.getSize(), false);
                return
            }

            // This can happen if picture was taken with a gesture.

            val intent = Intent(this@CameraActivityKotlin, CreatePostActivity::class.java)

            result.toBitmap { bitmap ->
                //                    imageView.setImageBitmap(bitmap);
                try {
                    val arrayImages = ArrayList<AddImages>()
                    arrayImages.clear()
                    arrayImages.add(
                        AddImages(
                            savebitmap(bitmap!!).absolutePath,
                            false,
                            null,
                            -1,
                            "",
                            false,
                            "0",
                            null
                        )
                    )

                    intent.putExtra("from", "images")
                    intent.putExtra("from1", "Camera")
                    intent.putExtra("imagesArray", arrayImages)
                    startActivity(intent)
                    MyUtils.finishActivity(this@CameraActivityKotlin, true)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    override fun onVideoTaken(result: VideoResult) {
            super.onVideoTaken(result)
//            canStopVideo = false
            if (isStopActivityBackPress == false) {
                isStopActivityBackPress == false
                mHandler.removeCallbacksAndMessages(null)
                videoTime.visibility = View.INVISIBLE
                counter = 0

                camera!!.flash = Flash.OFF
                capturePicture?.setImageResource(R.drawable.icon_camera)

                val intent = Intent(this@CameraActivityKotlin, CreatePostActivity::class.java)

                val arrayImages = ArrayList<AddImages>()
                MyUtils.videoFile1 = result.file
                MyUtils.videoFile = null
                MyUtils.videoPath = result.file.toString()
                arrayImages.clear()
                arrayImages.add(
                    AddImages(
                        MyUtils.videoPath,
                        false,
                        null,
                        -1,
                        "",
                        false,
                        "0",
                        MyUtils.videoFile1
                    )
                )

                intent.putExtra("from", "video")
                intent.putExtra("from1", "Camera")
                intent.putExtra("imagesArray", arrayImages)
                startActivity(intent)
                MyUtils.finishActivity(this@CameraActivityKotlin, true)
            }


        }

    override fun onVideoRecordingStart() {
            super.onVideoRecordingStart()
            LOG.w("onVideoRecordingStart!")
        }

    override fun onVideoRecordingEnd() {
            super.onVideoRecordingEnd()
//            message("Video taken. Processing...", false)
            LOG.w("onVideoRecordingEnd!")
//            mHandler.removeCallbacksAndMessages(null)
//            counter = 0
//            videoTime.visibility = View.INVISIBLE
        }

    override fun onExposureCorrectionChanged(
        newValue: Float,
        bounds: FloatArray,
        fingers: Array<PointF>?
    ) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers)
            message("Exposure correction:$newValue", false)
        }

        override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onZoomChanged(newValue, bounds, fingers)
            message("Zoom:$newValue", false)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.edit ->
                //                edit();
            {
                isGallary = true
                openGallery()
            }


            R.id.capturePicture -> {
                if ((!isVideo!!)) {
                    capturePicture()
                } else {
                    if (canStopVideo!!) {
                        if (counter > 10) {
                            canStopVideo = false
                            stopVideoRecord()
                        } else Toast.makeText(
                            this@CameraActivityKotlin,
                            "Video minimum length required is 10 seconds.",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        canStopVideo = true
                        if (!camera?.isTakingVideo!!) captureVideo()
                    }

                }
            }

//            R.id.capturePictureSnapshot -> capturePictureSnapshot()
            R.id.captureVideo -> captureVideo()
            R.id.captureVideoSnapshot -> captureVideoSnapshot()
            R.id.toggleCamera -> toggleCamera()
            R.id.changeFilter -> changeCurrentFilter()
            R.id.imageFlash -> if (flashOn!!) {
                flashOn = false
                imageFlash?.setImageResource(R.drawable.flash_disabled_for_camera_screen_white)
                camera!!.flash = Flash.OFF
            } else {
                imageFlash?.setImageResource(R.drawable.flash_enabled_for_camera_screen_white)
                flashOn = true
                camera!!.flash = Flash.TORCH
            }
            R.id.imagePrevious ->
                onBackPressed()
        }
    }

    private fun openGallery() {
        if ((!isVideo!!)) {
            val intent = Intent(this, AlbumSelectActivity::class.java)
            //set limit on number of images that can be selected, default is 10
            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 5)
            startActivityForResult(intent, 1211)
        }
        else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            if (isVideo!!) {
                intent.type = "video/*"
            } else {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "image/*"
            }

            intent.action = Intent.ACTION_GET_CONTENT
            try {
                startActivityForResult(
                    Intent.createChooser(
                        intent,
                        "Select Photo/Video"
                    ), 1211
                )
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    this@CameraActivityKotlin, "Please install a File Manager.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun stopVideoRecord() {
        camera!!.stopVideo()
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

    override fun onBackPressed() {

        /*val b = BottomSheetBehavior.from(controlPanel!!)
        if (b.state != BottomSheetBehavior.STATE_HIDDEN) {
            b.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }*/

        showMessageOKCancelCustome(this@CameraActivityKotlin,
            "Your Post Data will be Discard If go back", "", "Ok", "Cancel",
            DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
                try {
                    isStopActivityBackPress = true
                    if (camera != null) {
                        camera?.close()
                        camera?.clearCameraListeners()

                    }
                    this@CameraActivityKotlin.finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

        //   super.onBackPressed()
        //   MyUtils.finishActivity(this@CameraActivityKotlin,true)
    }

    /*private fun edit() {
        val b = BottomSheetBehavior.from(controlPanel!!)
        b.state = BottomSheetBehavior.STATE_COLLAPSED
    }*/

    private fun capturePicture() {
        camera!!.mode = Mode.PICTURE
        capturePicture?.setImageResource(R.drawable.icon_camera)
        if (camera!!.mode == Mode.VIDEO) {
            //  message("Can't take HQ pictures while in VIDEO mode.", false);
            return
        }
        if (camera!!.isTakingPicture)
            return
        mCaptureTime = System.currentTimeMillis()
        //        message("Capturing picture...", false);
        camera!!.takePictureSnapshot()
    }

    private fun capturePictureSnapshot() {
        if (camera!!.isTakingPicture) return
        if (camera!!.preview != Preview.GL_SURFACE) {
            message("Picture snapshots are only allowed with the GL_SURFACE preview.", true)
            return
        }
        mCaptureTime = System.currentTimeMillis()
        message("Capturing picture snapshot...", false)
        camera!!.takePictureSnapshot()
    }

    private fun captureVideo() {
        edit.visibility = View.GONE
        capturePicture?.setImageResource(R.drawable.capture_btn_for_video_red_center)
        camera!!.mode = Mode.VIDEO
        if (camera!!.mode == Mode.PICTURE) {
            message("Can't record HQ videos while in PICTURE mode.", false)
            return
        }
//        if (camera!!.isTakingPicture || camera!!.isTakingVideo) return
//        message("Recording for 5 seconds...", true)
        counter = 0
        videoTime?.visibility = View.VISIBLE
        videoTime.text = "00:00"
        startVideoTimeCounter()
//        mRunnable = Runnable {
//            // Do something here
//            mHandler1?.removeCallbacks(mRunnable)
//        }

        mHandler1?.postDelayed(
            {
                mHandler1?.removeCallbacksAndMessages(null)
                camera!!.takeVideo(File(filesDir, "video.mp4"), 119000)
            }, 500 // Delay in milliseconds
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mHandler1 != null) {
            mHandler?.removeCallbacksAndMessages(null)
            camera?.clearFrameProcessors()
            camera?.clearCameraListeners()
            camera?.close()
        }
    }

    private fun captureVideoSnapshot() {
        if (camera!!.isTakingVideo) {
            message("Already taking video.", false)
            return
        }
        if (camera!!.preview != Preview.GL_SURFACE) {
            message("Video snapshots are only allowed with the GL_SURFACE preview.", true)
            return
        }
        message("Recording snapshot for 5 seconds...", true)
        camera!!.takeVideoSnapshot(File(filesDir, "video.mp4"), 5000)
    }

    private fun toggleCamera() {
        if (camera!!.isTakingPicture || camera!!.isTakingVideo)
            return
        when (camera!!.toggleFacing()) {
            Facing.BACK -> message("Switched to back camera!", false)

            Facing.FRONT -> message("Switched to front camera!", false)
        }
    }

    private fun changeCurrentFilter() {
        if (camera!!.preview != Preview.GL_SURFACE) {
            message("Filters are supported only when preview is Preview.GL_SURFACE.", true)
            return
        }
        if (mCurrentFilter < mAllFilters.size - 1) {
            mCurrentFilter++
        } else {
            mCurrentFilter = 0
        }
        val filter = mAllFilters[mCurrentFilter]
        message(filter.toString(), false)

        // Normal behavior:
        camera!!.filter = filter.newInstance()

        // To test MultiFilter:
        // DuotoneFilter duotone = new DuotoneFilter();
        // duotone.setFirstColor(Color.RED);
        // duotone.setSecondColor(Color.GREEN);
        // camera.setFilter(new MultiFilter(duotone, filter.newInstance()));
    }

    override fun <T> onValueChanged(option: Option<T>, value: T, name: String): Boolean {
        if (option is Option.Width || option is Option.Height) {
            val preview = camera!!.preview
            val wrapContent = value as Int == ViewGroup.LayoutParams.WRAP_CONTENT
            if (preview == Preview.SURFACE && !wrapContent) {
                message(
                    "The SurfaceView preview does not support width or height changes. " + "The view will act as WRAP_CONTENT by default.",
                    true
                )
                return false
            }
        }
        option.set(camera!!, value)
        /*val b = BottomSheetBehavior.from(controlPanel!!)
        b.state = BottomSheetBehavior.STATE_HIDDEN*/
        message("Changed " + option.name + " to " + name, false)
        return true
    }

    //region Permissions

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var valid = true
        if (requestCode == REQUEST_CODE_Location_PERMISSIONS) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //openNextActivity(locationCity)
                    getCurrentLocation()
                } else {
                    Snackbar.make(
                        root,
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

                    showSnackBar(
                        getString(R.string.read_location_permissions_senied)

                    )
                    Handler().postDelayed({ finish() }, 2000)
                }
            }
        } else {
            for (grantResult in grantResults) {
                valid = valid && grantResult == PackageManager.PERMISSION_GRANTED
            }
            if (valid && !camera!!.isOpened) {
                camera!!.open()
            }
        }
    }

    fun showSnackBar(message: String) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1211 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val arrayImages = ArrayList<AddImages>()
                    var duration = ""

                    if ((!isVideo!!)) {

                        val images =
                            data!!.getParcelableArrayListExtra<Image>(Constants.INTENT_EXTRA_IMAGES)
                        arrayImages.clear()
                        try {
                            getImageLocation(File(images!![0].path))
                        } catch (e: Exception) {
                        }
                        for (i in images!!.indices) {
                            arrayImages.add(
                                AddImages(
                                    images[i].path,
                                    false,
                                    null,
                                    -1,
                                    images[i].name,
                                    false,
                                    "0"
                                )
                            )
                        }
                    } else {
                        if (data != null) {
                            arrayImages.clear()
                            pictureUri = data.data

                            if (getPathVideo(pictureUri!!) != null) {
                                MyUtils.videoFile = File(getPathVideo(pictureUri!!))
                                MyUtils.videoFile1 = null
                                MyUtils.videoPath = getPathVideo(pictureUri!!)
                            } else {
                                if (getPathFromInputStreamUriVideo(pictureUri!!) != null) {
                                    MyUtils.videoFile =
                                        File(getPathFromInputStreamUriVideo(pictureUri!!))
                                    MyUtils.videoFile1 = null
                                    MyUtils.videoPath = getPathFromInputStreamUriVideo(pictureUri!!)
                                }
                            }

                            try {
                                val retriever = MediaMetadataRetriever()
                                retriever.setDataSource(
                                    this@CameraActivityKotlin,
                                    Uri.parse(MyUtils.videoPath)
                                )
                                duration =
                                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                                val retriever1 = MediaMetadataRetriever()
                                retriever1.setDataSource(
                                    this@CameraActivityKotlin,
                                    pictureUri
                                )

                                var loction=retriever1.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION)!!
                                 lat=if(!loction.isNullOrEmpty())loction.substring(0,8) else ""
                                 lang=if(!loction.isNullOrEmpty())loction.substring(9,17) else ""
                                retriever.release()
                                retriever1.release()
                                Log.d("System Log", "Duration of view is $duration")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }



                            arrayImages.clear()
                            arrayImages.add(
                                AddImages(
                                    MyUtils.videoPath,
                                    false,
                                    null,
                                    -1,
                                    "",
                                    false,
                                    "0",
                                    MyUtils.videoFile
                                )
                            )
                        }
                    }
                    if ((!isVideo!!)) {
                        val intent =
                            Intent(this@CameraActivityKotlin, CreatePostActivity::class.java)
                        if ((!isVideo!!))
                            intent.putExtra("from", "images")
                        else {
                            intent.putExtra("from", "video")
                            intent.putExtra("lat", lat)
                            intent.putExtra("lang", lang)
                        }
                        intent.putExtra("imagesArray", arrayImages)
                        startActivity(intent)
                        MyUtils.finishActivity(this@CameraActivityKotlin, true)

                    } else {
                        if (java.lang.Long.valueOf(duration) >= 10434) {
                            val intent =
                                Intent(this@CameraActivityKotlin, CreatePostActivity::class.java)
                            if ((!isVideo!!))
                                intent.putExtra("from", "images")
                            else {
                                intent.putExtra("from", "video")
                                intent.putExtra("lat", lat)
                                intent.putExtra("lang", lang)
                            }
                            intent.putExtra("imagesArray", arrayImages)
                            startActivity(intent)
                            MyUtils.finishActivity(this@CameraActivityKotlin, true)
                        } else {
                            MyUtils.showSnackbarkotlin(
                                this@CameraActivityKotlin,
                                root,
                                resources.getString(R.string.err_video_lth_10)
                            )
                        }

                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun customCompressImage(actualImage: File): File? {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        var compressedImage: File? = null
        try {
            compressedImage = Compressor(this@CameraActivityKotlin)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .setQuality(75)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ).absolutePath
                )
                .compressToFile(actualImage, "IMG_$timeStamp.jpg")
        } catch (e: IOException) {
            e.printStackTrace()
            //            showError(e.message)
        }

        return compressedImage
    }

    /*private fun getPath(contentUri: Uri): String {
        var res = ""
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res
    }

    private fun getPathFromInputStreamUri(uri: Uri): String {

        var inputStream: InputStream? = null
        var filePath = ""

        if (uri.authority != null) {

            try {
                inputStream = this.contentResolver.openInputStream(uri)
                val photoFile = createTemporalFileFrom(inputStream)
                filePath = photoFile!!.path

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    inputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return filePath
    }

    //    @Throws(IOException.class)
    @Throws(IOException::class)
    private fun createTemporalFileFrom(inputStream: InputStream?): File? {
        //    throw new IOException();

        if (inputStream != null) {
            var read1 = 0
            //            byte[] buffer = new ByteArray(8 * 1024);
            val buffer = ByteArray(1024)

            val targetFile = createTemporalFile()

            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(targetFile)
            } catch (e: FileNotFoundException) {

            }

            while (true) {
                try {
                    read1 = inputStream.read(buffer)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (read1 == -1) break
                try {
                    outputStream!!.write(buffer, 0, read1)
                } catch (e: FileNotFoundException) {

                }

            }

            outputStream!!.flush()

            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return targetFile
        } else
            return null
    }

    private fun createTemporalFile(): File {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File(path, "IMG_$timeStamp.jpg")
    }*/

    companion object {

        private val LOG = CameraLogger.create("DemoApp")
        private val USE_FRAME_PROCESSOR = false
        private val DECODE_BITMAP = true

        @Throws(IOException::class)
        fun savebitmap(bmp: Bitmap): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val bytes = ByteArrayOutputStream()

            bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes)
            val f = File(
                Environment.getExternalStorageDirectory().toString()
                        + File.separator + "$timeStamp.jpg"
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            fo.close()
            return f
        }

        fun getPath1(uri: Uri?, activity: Activity): String? {


            return getFilePathFromURI(activity, uri, CreateFileName(Date(), ""))

        }

        fun getFilePathFromURI(context: Context, contentUri: Uri?, fileName: String?): String? {
            //copy file and send new file path

            if (!TextUtils.isEmpty(fileName)) {

                val copyFile = File(context.cacheDir, fileName!!)

                copy(context, contentUri, copyFile)
                return copyFile.absolutePath
            }
            return null
        }

        fun getFileName(uri: Uri?): String? {
            if (uri == null) return null
            var fileName: String? = null
            val path = uri.path
            val cut = path!!.lastIndexOf('/')
            if (cut != -1) {
                fileName = path.substring(cut + 1)
            }
            return fileName
        }


        fun CreateFileName(today: Date, type: String): String? {
            var filename: String? = null
            try {
                val dateFormatter = SimpleDateFormat("dd_MM_yyyy_hh_mm_ss")
                dateFormatter.isLenient = false
                //Date today = new Date();
                val s = dateFormatter.format(today)
                val min = 1
                val max = 1000

                val r = Random()
                val i1 = r.nextInt(max - min + 1) + min

                if (type.equals("Video", ignoreCase = true)) {
                    filename = "VIDEO_$s$i1.mp4"
                } else if (type.equals("Audio", ignoreCase = true)) {
                    filename = "Audio$s$i1.mp3"
                } else {
                    filename = "IMG_$s$i1.jpg"
                }

                //filename = "IMG_"+s+String.valueOf(i1)+".JPEG";

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return filename
        }

        fun copy(context: Context, srcUri: Uri?, dstFile: File) {
            try {
                val inputStream = context.contentResolver.openInputStream(srcUri!!) ?: return
                val outputStream = FileOutputStream(dstFile)
                IOUtils.copyStream(inputStream, outputStream)
                inputStream.close()
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    //endregion

    fun getPathVideo(pictureUri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = this@CameraActivityKotlin.getContentResolver()
                .query(pictureUri, proj, null, null, null)

            val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()

            if (File(cursor?.getString(column_index!!)) != null) {
                return cursor?.getString(column_index!!)!!
            }
        } finally {
            if (cursor != null) {
                cursor?.close()
            }
            return null
        }
    }

    private fun getPathFromInputStreamUriVideo(uri: Uri): String {

        var inputStream: InputStream? = null
        var filePath = ""

        if (uri.authority != null) {

            try {
                inputStream = this.contentResolver.openInputStream(uri)
                val photoFile = createTemporalFileFromVideo(inputStream)
                filePath = photoFile!!.path

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    inputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return filePath
    }

    //    @Throws(IOException.class)
    @Throws(IOException::class)
    private fun createTemporalFileFromVideo(inputStream: InputStream?): File? {
        //    throw new IOException();

        if (inputStream != null) {
            var read1 = 0
            //            byte[] buffer = new ByteArray(8 * 1024);
            val buffer = ByteArray(1024)

            val targetFile = createTemporalFileVideo()

            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(targetFile)
            } catch (e: FileNotFoundException) {

            }

            while (true) {
                try {
                    read1 = inputStream.read(buffer)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (read1 == -1) break
                try {
                    outputStream!!.write(buffer, 0, read1)
                } catch (e: FileNotFoundException) {

                }

            }

            outputStream!!.flush()

            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return targetFile
        } else
            return null
    }

    private fun createTemporalFileVideo(): File {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File(path, "vid_$timeStamp.mp4")
    }

    override fun onPause() {
        super.onPause()
        try {
            isStopActivityBackPress = true
            if (!isGallary!!) {
                if (camera != null) {
                    camera?.close()
                    camera?.clearCameraListeners()
                }
                this@CameraActivityKotlin.finish()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getImageLocation(file: File) {
        try {
            var gpsLat = "";
            var gpsLong = "";
            var gpsLatRef = "";
            var gpsLongRef = "";

            val exifInterface = androidx.exifinterface.media.ExifInterface(file)
            gpsLat = exifInterface.getAttribute(ExifInterface.TAG_SUBJECT_LOCATION)!!
            Log.e("location121232132", gpsLat)
            /* gpsLong = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
             gpsLatRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
             gpsLongRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)*/
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}
