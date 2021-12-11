package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragement_choiceof_media_layout.*

class SelectPhotoVideoBottomsheetFragment : BottomSheetDialogFragment(),View.OnClickListener {
    var mActivity: Activity? = null
    var llCameraVideo: LinearLayout? = null
    var Typeof: String? = ""
    private val mHandler: Handler? = null
    private var onItemSelectedListener: OnItemSelectedListener? =
        null
    var v: View? = null
    fun show(fragmentManager: FragmentManager, bottom: String?) {
        val ft = fragmentManager.beginTransaction()
        ft.commit()
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        mActivity = activity as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            Typeof = arguments!!.getString("Typeof")
        }
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragement_choiceof_media_layout, container)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvgym_offer_title.setOnClickListener(this)
        ivClose.setOnClickListener(this)
        txtGallery.setOnClickListener(this)
        llGallery.setOnClickListener(this)
        llCameraPhoto.setOnClickListener(this)
        txtGlobal.setOnClickListener(this)
        txtCameraVideo.setOnClickListener(this)
    }

    fun setListener(listener: OnItemSelectedListener?) {
        onItemSelectedListener = listener
    }


    interface OnItemSelectedListener {
        fun onselectoption(SelectName: String?)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.tvgym_offer_title -> { }
            R.id.ivClose -> dismiss()
            R.id.txtGallery -> {
                txtGallery.setTextColor(mActivity!!.resources.getColor(R.color.colorAccent))
                if (onItemSelectedListener != null) {
                    onItemSelectedListener!!.onselectoption("Gallery")
                    dismiss()
                }
            }
            R.id.llGallery -> {
                txtGallery.setTextColor(mActivity!!.resources.getColor(R.color.colorAccent))
                if (onItemSelectedListener != null) {
                    onItemSelectedListener!!.onselectoption("Gallery")
                    dismiss()
                }
            }
            R.id.txtGlobal -> {
                txtGlobal.setTextColor(mActivity!!.resources.getColor(R.color.colorAccent))
                if (onItemSelectedListener != null) {
                    onItemSelectedListener!!.onselectoption("CameraPhoto")
                    dismiss()
                }
            }
            R.id.llCameraPhoto -> {
                txtGlobal.setTextColor(mActivity!!.resources.getColor(R.color.colorAccent))
                if (onItemSelectedListener != null) {
                    onItemSelectedListener!!.onselectoption("CameraPhoto")
                    dismiss()
                }
            }
            R.id.txtCameraVideo -> {
                txtCameraVideo.setTextColor(mActivity!!.resources.getColor(R.color.colorAccent))
                if (onItemSelectedListener != null) {
                    onItemSelectedListener!!.onselectoption("CameraVideo")
                    dismiss()
                }
            }
        }
    }


}