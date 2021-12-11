package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.SharePostPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.awsFile
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.ImageSaver
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils.showSnackbar
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Created by ADMIN on 14/02/2018.
 */
class SharingMediaSheetFragment : BottomSheetDialogFragment(),
    View.OnClickListener {
    var email: Intent? = null
    var shareWallTextView: TextView? = null
    var closeBottomSheetImageView: ImageView? = null
    var recyclerViewSharePost: RecyclerView? = null
    var feedDatum: TrendingFeedData? = null
    var imageSaver: ImageSaver? = null
    var i = -1
    var mFile: Array<File>?=null
    var sharePostPojos = ArrayList<SharePostPojo>()
    var snakView: View? = null
    var launchables: List<ResolveInfo> =
        ArrayList()
    @SuppressLint("RestrictedApi")
    var pm: PackageManager? = null
    var viewPager: ViewPager? = null
    var customUserFeedPagerAdapter: CustomUserFeedPagerAdapter? = null
    var customUserFeedShareAdapter: CustomUserFeedPagerAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    var ll_dots: LinearLayout? = null
    var mediaType: String? = null
    var shareFilePath: ArrayList<awsFile> = ArrayList<awsFile>()
    var serverFilePath: List<String> =
        ArrayList()
    var mediaList: String? = null
    var shareText: String? = null
    var mListener: OnItemSelectedListener? = null
    var name: ComponentName? = null
    var sessionManager: SessionManager? = null
    var CopiedLink: String? = null
    var PostId: String? = null
    var from: String? = null
    private var dots: Array<ImageView?>?=null
    private var mPhotoPermission = false
    var mfUser: File? = null
    var image: Bitmap?=null

    var filename: String =""

    var file: File?=null
    var ext: String=""
    var isDownLoadTrue:Boolean=false

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(
            context,
            R.layout.sharing_bottom_sheet,
            null
        )
        dialog.setContentView(contentView)
        sessionManager = SessionManager(activity!!)
        closeBottomSheetImageView =
            contentView.findViewById<View>(R.id.closeBottomSheetImageView) as ImageView
        recyclerViewSharePost =
            contentView.findViewById<View>(R.id.recyclerViewSharePost) as RecyclerView
        ll_dots =
            contentView.findViewById<View>(R.id.ll_dots) as LinearLayout
        viewPager =
            contentView.findViewById<View>(R.id.viewPager) as ViewPager
        closeBottomSheetImageView!!.setOnClickListener { dismiss() }

    }

    fun setSnakBarView(snakBarView: View?) {
        snakView = snakBarView
    }

    override fun onClick(view: View) {}
    fun setListener(listener: OnItemSelectedListener?) {
        mListener = listener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null) {
            mediaType = arguments!!.getString("mediaType")
            mediaList = arguments!!.getString("mediaList", "")
            PostId = arguments!!.getString("postid")
            from = arguments!!.getString("from")
            shareText = arguments!!.getString("shareText", "")

            filename=  MyUtils.createFileName(Date(), mediaType!!)!!

            ext =
                if (mediaType!!.contains("Video"))
                    ".mp4"
                else if (mediaType!!.contains("Photo"))
                    ".jpg"
                else if (mediaType!!.contains("audio"))
                    ".mp4" else ".tmp"
            /*file = File.createTempFile(
                filename,
                ext,
                activity!!.externalCacheDir
            )*/
            if (mediaList?.length!! > 1) {

                serverFilePath = Gson().fromJson<Any>(
                    mediaList,
                    object :
                        TypeToken<ArrayList<String?>?>() {}.type
                ) as ArrayList<String>

                for (i in serverFilePath.indices) {
                    try {

                        DownloadTask(context!!,filename,mediaList!!,
                                serverFilePath as ArrayList<String>
                           ,shareFilePath,isDownLoadTrue).execute(RestClient.image_base_url_post+serverFilePath[i])


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
       /* if(!shareFilePath.isNullOrEmpty())
        {
            for(i in 0 until shareFilePath.size)
            {
                if(shareFilePath[i].downloadFile)
                {
                    isDownLoadTrue=true
                }
            }

        }*/
        /*if(isDownLoadTrue)
        {*/

            MyUtils.closeProgress()
            email = Intent(Intent.ACTION_SEND)
            email!!.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf("velmurugan@fusion.com")
            )
            email!!.putExtra(Intent.EXTRA_SUBJECT, "Hi")
            email!!.putExtra(Intent.EXTRA_TEXT, "Hi,This is Test")
            email!!.type = "text/plain"

            pm = activity!!.packageManager
            launchables = pm?.queryIntentActivities(email!!, 0)!!

            Collections
                .sort(
                    launchables,
                    ResolveInfo.DisplayNameComparator(pm)
                )

        linearLayoutManager = LinearLayoutManager(context)

            customUserFeedShareAdapter = CustomUserFeedPagerAdapter(activity)
            viewPager!!.adapter = customUserFeedPagerAdapter
            viewPager!!.pageMargin = resources.getDimensionPixelOffset(R.dimen._50sdp)

            //addBottomDots(0)
        /*}*/

       /* viewPager!!.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position <= 2) addBottomDots(0) else addBottomDots(position /6)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })*/
    }
    override fun onResume() {
        super.onResume()
        if (mPhotoPermission) {
            startSharing(launchables[0].loadLabel(pm))
            mPhotoPermission = false
        }
    }

    fun shareText(
        text: String?,
        componentName: ComponentName?
    ) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.type = "text/plain"
        sendIntent.component = componentName
        activity!!.startActivity(sendIntent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { // TODO: inflate a fragment view
        val rootView =
            super.onCreateView(inflater, container, savedInstanceState)
        return rootView
    }

    private fun startSharing(loadLabel: CharSequence) {
        shareMedia(shareText, snakView, mediaType,loadLabel)
    }

    private fun downloadShare(name: ComponentName?, loadLabel: CharSequence) {
        /*if (from != null && from.equals("Post", ignoreCase = true)) {
            *//*try {
                CallCopyLink(PostId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }*//*
        } else {*/
            if (shareFilePath.size == 1) {
                mListener?.onInternalShare(shareText, name, mediaType,shareFilePath,loadLabel)
               // shareTextWithMedia(shareText, name, mediaType)
            } else {
                mListener?.onInternalShare(shareText, name, mediaType,shareFilePath,loadLabel)
                // shareTextWithMultipleImage(shareText, name, mediaType)
            }
        //}
    }

    private fun addBottomDots(currentPage: Int) {
        try {
            dots = arrayOfNulls(launchables.size / 3)
            ll_dots!!.removeAllViews()
            for (i in dots?.indices!!) {
                dots!![i] = ImageView(activity)
//                dots!![i]!!.setImageResource(R.drawable.carousel_dot_unselected)
                dots!![i]!!.setImageResource(R.drawable.selected_circle)
                ll_dots!!.addView(dots!![i])
            }
            if (dots?.size!! > 0) {
//                dots!![currentPage]!!.setImageResource(R.drawable.carousel_dot_selected)
                dots!![currentPage]!!.setImageResource(R.drawable.shape_deselected_dot51)
            }
        }catch (e: ArrayIndexOutOfBoundsException){
            e.printStackTrace()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun check_permission_Gallery(loadLabel: CharSequence) {
        if (!addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) { // Need Rationale
            val message = "You need to grant permission to share post."
            MyUtils.showMessageOKCancel(
                context!!,
                message,
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        3
                    )
                })
        } else {
            startSharing(loadLabel)
        }
    }

    private fun addPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity!!.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // Check for Rationale Option
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            3 -> {
                // Check for Permission
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhotoPermission = true
                } else {
                    showSnackbar(activity!!, "write media Permissions denied", snakView!!)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun shareMedia(
        shareText: String?,
        snackbar: View?,
        mediaType: String?,
        loadLabel: CharSequence
    ) {
        try {
            downloadShare(name,loadLabel)

          /*  var isDownload = true
            for (i in shareFilePath.indices) {
                if (shareFilePath[i].getState() !== TransferState.COMPLETED) {
                    isDownload = false
                }
            }
            if (!isDownload) {
                MyUtils.showProgress(activity!!)
              val awsMultipleUpload: AwsMultipleDownload = object :
                    AwsMultipleDownload(activity, shareFilePath, AWSConfiguration.mediaPath) {
                    fun onStateFileChanged(id: Int, state: TransferState?) {}
                    fun onProgressFileChanged(
                        id: Int,
                        bytesCurrent: Long,
                        bytesTotal: Long
                    ) {
                    }

                    fun onErrorUploadFile(id: Int, ex: String?) {
                        MyUtils.closeProgress()
                        if (isInternetAvailable(activity!!))
                            (activity as MainActivity).showSnackBar(activity!!.resources.getString(R.string.error_crash_error_message))
                        else
                            (activity as MainActivity).showSnackBar(activity!!.resources.getString(R.string.error_common_network))
                    }

                    fun onSuccessUploadFile() {
                        MyUtils.closeProgress()
                        downloadShare(name)
                    }
                }
            } else {

            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnItemSelectedListener {
        fun onInternalShare(
            text: String?,
            componentName: ComponentName?,
            mediaType: String?,
            shareFilePath: ArrayList<awsFile>,
            loadLabel: CharSequence
        )
    }

    inner class CustomUserFeedPagerAdapter(var mContext: Context?) : PagerAdapter() {
        var mLayoutInflater: LayoutInflater
        var sessionManager: SessionManager? = null
        override fun isViewFromObject(
            view: View,
            `object`: Any
        ): Boolean {
            return view === `object`
        }

        override fun instantiateItem(
            container: ViewGroup,
            position: Int
        ): Any {
            val inflater =
                mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val row = inflater.inflate(R.layout.row, null)

            val label =
                row.findViewById<View>(R.id.label) as TextView
            val ll_feed_share =
                row.findViewById<LinearLayout>(R.id.ll_feed_share) as LinearLayout

            label.text = launchables[position].loadLabel(pm)
            val icon =
                row.findViewById<View>(R.id.icon) as ImageView
            icon.setImageDrawable(launchables[position].loadIcon(pm))
            row.tag = position

            ll_feed_share.setOnClickListener { v ->
                val launchable =
                        launchables[v.tag as Int]
                    val activity = launchable.activityInfo
                    name = ComponentName(
                        activity.applicationInfo.packageName, activity.name
                    )
                    if (Build.VERSION.SDK_INT >= 23) {
                        check_permission_Gallery(launchables[position].loadLabel(pm))
                    } else {
                        startSharing(launchables[position].loadLabel(pm))
                    }

            }
            val viewPager = container as ViewPager
            viewPager.addView(row, 0)

            return row
        }

        override fun getCount(): Int {
            return launchables.size
        }

        override fun destroyItem(
            container: ViewGroup,
            position: Int,
            `object`: Any
        ) {
            val viewPager = container as ViewPager
            val view = `object` as View
            viewPager.removeView(view)
        }

        override fun getPageWidth(position: Int): Float {
            return .3f
        }

        init {
            this.mLayoutInflater = LayoutInflater.from(mContext)
        }
    }



     class DownloadTask(
        val context: Context,
        var filename: String,
        val mediaList: String,
        var serverFilePath: ArrayList<String>,
        val shareFilePath: ArrayList<awsFile>,
        var downLoadTrue: Boolean
    ) : AsyncTask<String?, Void?, Bitmap?>() {


        override fun onPreExecute() {

           // MyUtils.showProgressDialog(context)
        }
        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                 saveImageToInternalStorage(result,filename,shareFilePath)

            } else {
            }
        }


        override fun doInBackground(vararg params: String?): Bitmap? {

            var connection: HttpURLConnection? = null
            try {
                connection = URL(params.get(0)).openConnection() as HttpURLConnection
                connection.connect()

                val inputStream = connection!!.inputStream
                val bufferedInputStream =
                    BufferedInputStream(inputStream)
                return BitmapFactory.decodeStream(bufferedInputStream)

            } catch (e: IOException) {
                e.printStackTrace()
            } finally { // Disconnect the http url connection
                connection!!.disconnect()
            }
            return null
        }

        fun saveImageToInternalStorage(
            bitmap: Bitmap,
            filename: String,
            shareFilePath: ArrayList<awsFile>

        ): File {

            var file =  ImageSaver(context!!).setExternal(true).setFileName(filename).save(Bitmap.createBitmap(bitmap))

            val awsFile = awsFile()
            if (mediaList?.length!! > 1) {

                serverFilePath = Gson().fromJson<Any>(
                    mediaList,
                    object :
                        TypeToken<ArrayList<String?>?>() {}.type
                ) as ArrayList<String>
                for (i in serverFilePath.indices) {
                    try {
                        awsFile.fileName=(serverFilePath[i])
                        awsFile.uploadFile=(file)
                        awsFile.downloadFile=true

                        shareFilePath.add(awsFile)
                        SharingMediaSheetFragment().downloadTrue(true)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return file!!

            /*    val wrapper =
                    ContextWrapper(getApplicationContext())
                var file = wrapper.getDir("Images", Context.MODE_PRIVATE)

                file = File(file, "UniqueFileName" + ".jpg")
                try { // Initialize a new OutputStream
                    var stream: OutputStream? = null
                    // If the output file exists, it can be replaced or appended to it
                    stream = FileOutputStream(file)
                    // Compress the bitmap
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    // Flushes the stream
                    stream.flush()
                    // Closes the stream
                    stream.close()
                } catch (e: IOException) // Catch the exception
                {
                    e.printStackTrace()
                }
                // Parse the gallery image url to uri
                // Return the saved image Uri
                return Uri.parse(file.absolutePath)*/
        }

    }


    fun downloadTrue(b: Boolean):Boolean
    {
        isDownLoadTrue=b
        return isDownLoadTrue
    }

}