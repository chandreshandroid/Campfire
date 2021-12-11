package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import kotlinx.android.synthetic.main.mediachoose_images_bottomsheet.*

import java.io.File

class MediaChooseVideoBottomsheet() : DialogFragment(){
    private var file = File("")
    private var timeForImageName: Long = 0
    private var imgName = ""
    private var mIntent = Intent()
    private var pictureUri = Uri.fromFile(file)

    private val TAG = MediaChooseVideoBottomsheet::class.java.name
    private var mActivity = AppCompatActivity()
    var v:View?= null
//    var OnSelectionItemClickListener : OnItemClickListener?=null;


    var title_gallery = ""
    var title_select_image = ""
    var title_take_photo = ""
    var title_cancel = ""
    var from=""

    @SuppressLint("SetTextI18n")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.mediachoose_video_bottomsheet, null)

        title_gallery = ""+GetDynamicStringDictionaryObjectClass?.Gallery_
        title_select_image = ""+GetDynamicStringDictionaryObjectClass?.Select_Video
        title_take_photo = ""+GetDynamicStringDictionaryObjectClass?.Take_Video
        title_cancel = ""+GetDynamicStringDictionaryObjectClass?.Cancel_

        title_tv?.setText(title_select_image)
        tvGalleryText?.setText(title_gallery)
        tvCameraText?.setText(title_take_photo)
        tvCancel?.setText(title_cancel)

        dialog.setContentView(contentView)
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false)

        dialog.llCameraLayout.setOnClickListener {

            startCameraActivity()

        }

        dialog.tvCancel.setOnClickListener {
            if (dialog != null) {
                dismiss()
            }
        }

        dialog.llGalleryLayout.setOnClickListener {

            openGallery()

        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    /*fun setListener(listener: OnItemClickListener) {
        this.OnSelectionItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onSellerSelectionClick(position: Int,typeofOperation: String)
    }
*/
    private fun openGallery() {
        mIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        mIntent.type = "video/*"
        activity!!.startActivityForResult(Intent.createChooser(mIntent, title_select_image),
            MediaChooseVideoBottomsheet.SELECT_PICTURE
        )

//        val intent = Intent()
//        mIntent.type = "image/*"
//        mIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 3);
//        mIntent.action = Intent.ACTION_PICK
//        activity!!.startActivityForResult(Intent.createChooser(mIntent, "Select Picture"), MediaChooseImageBottomsheet.SELECT_PICTURE)

        if (dialog != null){
            dismiss()
        }
    }

    private fun startCameraActivity() {

      mIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

        timeForImageName = System.currentTimeMillis()
        imgName = "img$timeForImageName.mp4"

        file = File(activity!!.getExternalFilesDir(null), imgName)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            mIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
           // mIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        } else {
            mIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(activity!!, "com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fileprovider", file))

            /*mIntent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT)
            mIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            mIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)*/
        }
        pictureUri = Uri.fromFile(file)
        activity!!.startActivityForResult(mIntent, TAKE_PICTURE)
        if(dialog != null){
            dismiss()
        }
    }

    fun selectedImage(): Uri? {
        return pictureUri
    }

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSIONS = 11
        private const val REQUEST_CODE_READ_PERMISSIONS = 12
        private const val REQUEST_CODE_WRITE_PERMISSIONS = 13
        private const val TAKE_PICTURE = 1
        private const val SELECT_PICTURE = 2
    }
}