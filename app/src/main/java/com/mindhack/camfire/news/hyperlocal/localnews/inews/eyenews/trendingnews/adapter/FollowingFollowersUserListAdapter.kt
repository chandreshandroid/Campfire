package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.FollowData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.item_following_followers.view.*

class FollowingFollowersUserListAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val type: String,
    val tabposition: Int,
    val followData: ArrayList<FollowData?>

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelection = -1
    var widthNew = 0
    var heightNew = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_following_followers, parent, false)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            val holder1 = holder as ViewHolder
           // getScrennwidth()
            holder1.bind(holder.adapterPosition, onItemClick,widthNew,heightNew,tabposition,followData[position])


        }
    }

    override fun getItemCount(): Int {
        return followData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (followData[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class ViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        var sessionManager: SessionManager = SessionManager(context)
        init {

            sessionManager= SessionManager(context)
        }
        fun bind(
            position: Int,
            onitemClick: OnItemClick,
            widthNew: Int,
            heightNew: Int,
            tabposition: Int,
            followData: FollowData?
        ) =
            with(itemView) {
                btnFollow.text = ""+GetDynamicStringDictionaryObjectClass?.Follow
                btnFollowing.text = ""+GetDynamicStringDictionaryObjectClass?.Following
               if(!followData?.userProfilePicture.isNullOrEmpty())
               {
                   imv_user_dp.setImageURI(followData?.userProfilePicture)
               }else
               {
                   imv_user_dp.setImageResource(R.drawable.footer_icon_my_profile_grey)
               }
                tabContent.text=followData?.userFirstName+" "+followData?.userLastName
                tabContentUserId.text="@"+followData?.userMentionID
                if(!followData?.userID.equals(sessionManager?.get_Authenticate_User().userID))
                {
                    if(followData?.isYouFollowing.equals("Yes",false))
                    {
                        btnFollow.visibility=View.GONE
                        btnFollowing.visibility=View.VISIBLE
                    }else
                    {
                        btnFollow.visibility=View.VISIBLE
                        btnFollowing.visibility=View.GONE
                    }
                }else{
                    btnFollow.visibility=View.GONE
                    btnFollowing.visibility=View.GONE

                }

                btnFollow.setOnClickListener {
                    onitemClick.onClicklisneter(position,"follow",btnFollow)
                }
                btnFollowing.setOnClickListener {
                    onitemClick.onClicklisneter(position,"following",btnFollowing)
                }

                itemView.setOnClickListener {
                    onitemClick.onClicklisneter(position,"userProfile",btnFollowing)

                }
            }

    }


    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String, v: View)

    }

    private fun getScrennwidth(): Int {

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        widthNew = (width / 3).toInt()
        heightNew = (height / 4).toInt()

        return height
    }
}