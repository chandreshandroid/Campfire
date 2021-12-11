package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.aws

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.application.MyApplication
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.AddImages
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.ImageSaver
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.vincent.videocompressor.VideoController
import id.zelory.compressor.Compressor
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by dhavalkaka on 08/01/2018.
 */
abstract class AwsMultipleUpload(
    var context: Context,
    var awsFileList: ArrayList<AddImages?>? = ArrayList(),
    var uploadPath: String, from: String
) {
    var observer: TransferObserver? = null
    var filename1: String? = null
    var transferUtility: TransferUtility
    var s3: AmazonS3Client
    var isShowPb = false
    var credentials: BasicAWSCredentials? = null
    private var isUpload = false
    private var isError = false
    var imageList: ArrayList<AddImages?>? = java.util.ArrayList()
    var form1: String = ""


    init {
        s3 = AmazonS3Client(MyApplication.credentialsProvider)
        imageList = awsFileList

        form1 = from
        transferUtility = TransferUtility.builder()
            .context(context)
            .s3Client(s3)
            .build()
        isError = false
        uploadAWS()
    }

    fun uploadAWS() {
        var uploadListener = UploadListener()

        if (form1.equals("images", ignoreCase = true))
        {
            compressFiles()
            for (i in imageList?.indices!!) {

                observer = transferUtility.upload(
                    uploadPath,
                    imageList!![i]!!.imageName
                    , File(imageList!![i]!!.sdcardPath!!)
                )

                imageList!![i]!!.transferId = observer?.getId()!!
                val listener: TransferListener =
                    uploadListener
                observer?.setTransferListener(listener)
            }
        }
        else if (form1.equals("video", ignoreCase = true))
        {

            if (!imageList!![0]!!.isCompress) {
                /*val fileName: String = MyUtils.createFileName(Date(), "Video")!!
                val file =
                    ImageSaver(context).setExternal(true).setFileName(fileName).createFile()

                object : Thread() {
                    override fun run() {
                        super.run()

                        VideoCompressor().compressVideo(imageList!![0]!!.sdcardPath, file.absolutePath)

                      }
                }.start()*/

               MyAsyncTask(
                    imageList,
                    context,
                    form1,
                    observer,
                    uploadPath,
                    transferUtility,
                    uploadListener
                ).execute()

            }

        }

    }

    fun compressFiles() {
        if (form1.equals("images", ignoreCase = true)) {
            for (i in imageList?.indices!!) {
                if (!imageList!![i]!!.isCompress) {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    val file1 = File(imageList!![i]!!.sdcardPath)
                    var file: File? = null
                    try {
                        file = Compressor(context)
                            .setQuality(90)
                            .setMaxHeight(225)
                            .setMaxWidth(720)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .compressToFile(file1)
                        BitmapFactory.decodeFile(file.absolutePath, options)

                        val imageHeight = options.outHeight
                        val imageWidth = options.outWidth
                        imageList!![i]!!.width = imageWidth
                        imageList!![i]!!.height = imageHeight
//                        val fileName =
//                            imageHeight.toString() + "X" + imageWidth + "X" + MyUtils.createFileName(Date(), "")
                        val fileName =
                            "IMG" + file.absolutePath.substring(file.absolutePath.lastIndexOf('/') + 1)

                        imageList!![i]!!.imageName = (fileName)
                        imageList!![i]!!.sdcardPath = file.absolutePath
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    imageList!![i]!!.isCompress = true
                }
            }
        }


    }


    internal class MyAsyncTask(
        val imageList: ArrayList<AddImages?>?,
        val mActivity: Context,
        val from: String,
        var observer: TransferObserver?,
        val uploadPath: String,
        val transferUtility: TransferUtility,
        var uploadListener: UploadListener

    ) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
        }

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg params: Void?): Void? {

            val fileName: String = MyUtils.createFileName(Date(), "Video")!!
            val file =
                ImageSaver(mActivity).setExternal(true).setFileName(fileName).createFile()
            val isCompress: Boolean = VideoController.getInstance().convertVideo(
                imageList!![0]!!.sdcardPath,
                file.absolutePath,
                VideoController.COMPRESS_QUALITY_MEDIUM
            ) { percent ->
                Log.d("percent video compress", "" + percent)
            }

            if (isCompress) {

                imageList[0]!!.imageName=(fileName)
                imageList[0]!!.sdcardPath = file.absolutePath
                //imageList!![0]!!.sdcardPath = imageList!![0]!!.sdcardPath
            }
            imageList!![0]!!.isCompress = true


            if (imageList != null) {
                for (i in imageList?.indices!!) {

                    observer = transferUtility.upload(
                        uploadPath,
                        imageList!![i]!!.imageName!!,  /* The bucket to upload to */ /* The key for the uploaded object */
                        File(imageList!![i]!!.sdcardPath) /* The file where the data to upload exists */
                    )
                    imageList!![i]!!.transferId = observer?.getId()!!
                    val listener: TransferListener = uploadListener
                    observer?.setTransferListener(listener)
                }
            }

            return null
        }
    }


    public inner class UploadListener :
        TransferListener {
        // Simply updates the UI list when notified.
        override fun onError(id: Int, e: Exception) {
            Log.e("uploadAws", "Error during upload: $id", e)
        }

        override fun onProgressChanged(
            id: Int,
            bytesCurrent: Long,
            bytesTotal: Long
        ) {
            val percentDonef =
                bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
            val percentDone = percentDonef.toInt()
            //   Log.d("uploadAws", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%"+" Size"+ Constant.getBytesString(bytesTotal));
        }

        override fun onStateChanged(
            id: Int,
            newState: TransferState
        ) {
            for (i in imageList?.indices!!) {
                if (imageList?.get(i)!!.transferId == id) {
                    imageList?.get(i)!!.state = newState
                }
            }
            isUpload = true
            for (i in imageList?.indices!!) {
                if (imageList?.get(i)!!.state != TransferState.COMPLETED) {
                    isUpload = false
                }
            }
            if (isUpload) {
                onSuccessUploadFile(imageList!!)
            }
            if (newState == TransferState.WAITING_FOR_NETWORK && !isError) {
                isError = true
                onErrorUploadFile(id, "No internet Connection")
            }
            if (newState == TransferState.FAILED && !isError) {
                isError = true
                onErrorUploadFile(id, "Failed Time Out")
            }
        }


    }

    //success response
    abstract fun onStateFileChanged(id: Int, state: TransferState?)
    //error
    abstract fun onProgressFileChanged(id: Int, bytesCurrent: Long, bytesTotal: Long)
    abstract fun onErrorUploadFile(id: Int, ex: String?)
    abstract fun onSuccessUploadFile(awsFileList: ArrayList<AddImages?>)


}