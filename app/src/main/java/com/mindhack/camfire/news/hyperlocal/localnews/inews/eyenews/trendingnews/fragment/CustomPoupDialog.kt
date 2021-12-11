package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.ReasonAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.ReasonList
import kotlinx.android.synthetic.main.custom_dialog_alter.view.*

class CustomPoupDialog : DialogFragment(), View.OnClickListener {

    //    var view1: View? = null
    var mActivity: AppCompatActivity? = null
    var OnSelectionItemClickListener: CustomPoupDialog.OnItemClickListener? = null

    var layoutManager: LinearLayoutManager? = null
    var adapterReason: ReasonAdapter? = null
    var arrayReason0 : ReasonList? = null
    var arrayReason = ArrayList<ReasonList.Data?>()
    var selectedID = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)

    }

    fun setMargins(
        dialog: Dialog,
        marginLeft: Int,
        marginTop: Int,
        marginRight: Int,
        marginBottom: Int
    ): Dialog {
        val window = dialog.window
            ?: // dialog window is not available, cannot apply margins
            return dialog
        val context = dialog.context

        // set dialog to fullscreen
        val root = RelativeLayout(context)
        root.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        // set background to get rid of additional margins
//        window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        // apply left and top margin directly
        window.setGravity(Gravity.CENTER)
        val attributes = window.attributes
        attributes.x = marginLeft
        attributes.y = marginBottom
        window.attributes = attributes

        // set right and bottom margin implicitly by calculating width and height of dialog
        val displaySize = getDisplayDimensions(context)
        val width = displaySize.x - marginLeft - marginRight
        val height = displaySize.y - marginTop - marginBottom
        window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(resources.getDrawable(R.drawable.background_inset_white))

        return dialog
    }

    fun getDisplayDimensions(context: Context): Point {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay

        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val screenWidth = metrics.widthPixels
        var screenHeight = metrics.heightPixels

        // find out if status bar has already been subtracted from screenHeight
        display.getRealMetrics(metrics)
        val physicalHeight = metrics.heightPixels
        val statusBarHeight = getStatusBarHeight(context)
        val navigationBarHeight = getNavigationBarHeight(context)
        val heightDelta = physicalHeight - screenHeight
        if (heightDelta == 0 || heightDelta == navigationBarHeight) {
            screenHeight -= statusBarHeight
        }

        return Point(screenWidth, screenHeight)
    }

    fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val bundle = arguments
//        if (bundle != null)
//            dialogKey = bundle!!.getString(keyBundleFromWhere, "")

        if (arguments != null) {
//            btnPos =
//                if (arguments?.getString(keyButtonPos) != null) arguments?.getString(keyButtonPos)!! else ""
//            btnNev =
//                if (arguments?.getString(keyButtonNev) != null) arguments?.getString(keyButtonNev)!! else ""
//            textTitle =
//                if (arguments?.getString(keyTextTitle) != null) arguments?.getString(keyTextTitle)!! else ""
//            textMessage = if (arguments?.getString(keyTextMessage) != null) arguments?.getString(
//                keyTextMessage
//            )!! else ""

            if (arguments?.getSerializable("ReasonList") != null) arrayReason0 = arguments?.getSerializable("ReasonList") as ReasonList

            if (arrayReason0 != null && !arrayReason0?.data.isNullOrEmpty()) arrayReason.addAll(arrayReason0?.data!!)
        }

        val style = DialogFragment.STYLE_NO_FRAME
        val theme = R.style.DialogTheme
        setStyle(style, theme)
    }

    private fun setID() {
        for (i in 0 until arrayReason.size) {
            if (arrayReason[i]?.isSelected!!) {
                selectedID = arrayReason[i]?.reasonID!!
                break
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mActivity = context as AppCompatActivity
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view1 = inflater!!.inflate(R.layout.custom_dialog_alter, container, false)


        layoutManager = LinearLayoutManager(mActivity!!)
        view1?.recyclerViewReasonList?.layoutManager = layoutManager

        adapterReason = ReasonAdapter(mActivity!!, object : ReasonAdapter.OnItemClick {
            override fun onClicklisneter(pos: Int, reasonID: String) {
                for (i in 0 until arrayReason.size) {

                    if (pos == i) {
                        if (arrayReason[i]?.isSelected!!) {
                            arrayReason[i]?.isSelected = false
                        } else arrayReason[i]?.isSelected = true
                    } else {
                        arrayReason[i]?.isSelected = false
                    }

                    setID()

                }
            }
        }, arrayReason!!)
        view1?.recyclerViewReasonList?.adapter = adapterReason
        adapterReason?.notifyDataSetChanged()


        view1?.customDialogBittonNegative?.setOnClickListener(this)
        view1?.customDialogBittonSuccess?.setOnClickListener(this)


        setMargins(dialog!!, 0, 20, 20, 40)
        return view1
    }

    companion object {
        fun newInstance(): CustomPoupDialog {
            val f = CustomPoupDialog()
            // Supply num input as an argument.
            var args = Bundle()
//           args = bundle
//           Log.d("argsbundle", args.toString())

            f.arguments = args
            return f
        }
    }

    fun setListener(listener: OnItemClickListener) {
        this.OnSelectionItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemSelectionClick(typeButton: String, selectedID: String)
    }

    override fun onClick(v: View?) {
        /*
            0 -> For women fragment
            1 -> For men fragmnet
        */
        when (v?.id) {
            R.id.customDialogBittonNegative -> {
                if (OnSelectionItemClickListener != null) {
                    OnSelectionItemClickListener!!.onItemSelectionClick("cancel", "")
                    dismiss()
                }
            }
            R.id.customDialogBittonSuccess -> {
                if (selectedID.isNullOrEmpty()){
                    Toast.makeText(mActivity!!, "Please select reason", Toast.LENGTH_SHORT).show()
                }else{
                    OnSelectionItemClickListener!!.onItemSelectionClick("done", selectedID)
                    dismiss()
                }
            }
        }
    }
}