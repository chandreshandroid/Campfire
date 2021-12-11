package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.PhotogalleryAdapterr
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.Albummedia
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.RegisterPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.fragment_photo_gallery.*
import kotlinx.android.synthetic.main.fragment_photo_gallery.viewpager

/**
 * A simple [Fragment] subclass.
 */
class PhotoGalleryFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    val TAG = ProfileDetailFragment::class.java.name
    var sessionManager : SessionManager? = null
    var userData : RegisterPojo.Data? = null
    var photogalleryAdapterr: PhotogalleryAdapterr?=null
    var albummedia=ArrayList<Albummedia>()
    var page_position = 0
    private var dots: Array<ImageView?>? = null
    private var dotsCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_change_password, container, false)
        v = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
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
        if(arguments!=null)
        {
            albummedia= arguments!!.getSerializable("albummedia") as ArrayList<Albummedia>
        }

        viewpager.adapter = PhotogalleryAdapterr(mActivity!!,albummedia)
        if (! albummedia.isNullOrEmpty() &&  albummedia?.size!! > 1) {
            addBottomDots(0, layoutDotsTutorial)
            layoutDotsTutorial.visibility = View.VISIBLE
        } else {
            layoutDotsTutorial.visibility = View.GONE
        }

        viewpager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
        imgLoginCloseIcon.setOnClickListener {
            (mActivity as  MainActivity).onBackPressed()
        }




    }

    fun addBottomDots(currentPage: Int, layoutDotsTutorial: LinearLayout) {
        // dotsCount = stichingServicData?.stitchingserviceImages!!.size
        //  dotsCount=trendingFeedDatum1?.postSerializedData?.get(0)?.albummedia!!.size
        dotsCount = if ( albummedia.isNullOrEmpty()) 0 else  albummedia?.size!!

        layoutDotsTutorial.removeAllViews()

        dots = arrayOfNulls(dotsCount)

        for (i in 0 until dotsCount) {

            dots!![i] = ImageView(mActivity!!)
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


}
