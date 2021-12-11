package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.ProfileDetailFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.PostLikeListData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.item_react_userlist.view.*

class PostReactUserListAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val type: String,
    val tabposition: Int,
    val postLikeList: ArrayList<PostLikeListData?>

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
                .inflate(R.layout.item_react_userlist, parent, false)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            val holder1 = holder as ViewHolder
           // getScrennwidth()
            holder1.bind(holder.adapterPosition, onItemClick,widthNew,heightNew,tabposition,postLikeList[position])


        }
    }

    override fun getItemCount(): Int {
        return postLikeList?.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (postLikeList[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
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
            postLikeListData: PostLikeListData?
        ) =
            with(itemView) {
                imv_user_dp.setImageURI(RestClient.image_base_url_users+postLikeListData?.userProfilePicture)

                when(tabposition)
            {

                0->{
                   when(postLikeListData?.postlikeType)
                   {
                       "Like"->{
                            imv_edit_react.setImageResource(R.drawable.like_filled_small)

                       }
                       "Sad"->{
                            imv_edit_react.setImageResource(R.drawable.worried_reaction_icon_small)

                       }
                       "Angry"->{
                            imv_edit_react.setImageResource(R.drawable.angry_reaction_icon_small)

                       }
                       "Laugh"->{
                            imv_edit_react.setImageResource(R.drawable.laughing_reaction_icon_small)

                       }
                   }


                }
                1->{
                    imv_edit_react.setImageResource(R.drawable.laughing_reaction_icon_small)

                }
                2->{
                    imv_edit_react.setImageResource(R.drawable.angry_reaction_icon_small)
                }
                3->{
                    imv_edit_react.setImageResource(R.drawable.worried_reaction_icon_small)

                }
                4->{
                    imv_edit_react.setImageResource(R.drawable.like_filled_small)

                }

            }

                btnFollow.text = ""+GetDynamicStringDictionaryObjectClass?.Follow
                btnFollowing.text = ""+GetDynamicStringDictionaryObjectClass?.Following

                if(!sessionManager.get_Authenticate_User().userID.equals(postLikeListData?.userID,false))
                {
                    if (postLikeListData?.isYouFollowing.equals("Yes", false)) {
                        btnFollowing.visibility = View.VISIBLE
                        btnFollow.visibility = View.GONE
                    } else {
                       btnFollowing.visibility = View.GONE
                       btnFollow.visibility = View.VISIBLE
                    }
                }else
                {
                    btnFollow.visibility=View.GONE
                    btnFollowing.visibility=View.GONE
                }
                tabContent.text=postLikeListData?.userFirstName+" "+postLikeListData?.userLastName


                btnFollowing.setOnClickListener {
                    onitemClick.onClicklisneter(position,
                    "Following",
                      btnFollowing)

                }
                btnFollow.setOnClickListener {

                    onitemClick.onClicklisneter(position,
                       "Follow",
                        btnFollow)

                }
                itemView.setOnClickListener{
                    var profileDetailFragment = ProfileDetailFragment()

                    if (!sessionManager.get_Authenticate_User().userID.equals(
                            postLikeListData?.userID,
                            false
                        )
                    ) {
                        var uName = ""
                        if (!postLikeListData?.userFirstName.isNullOrEmpty() && !postLikeListData?.userLastName.isNullOrEmpty()
                        ) {
                            uName =
                                postLikeListData?.userFirstName + " " + postLikeListData?.userLastName
                        } else if (postLikeListData?.userFirstName.isNullOrEmpty() && !postLikeListData?.userLastName.isNullOrEmpty()
                        ) {
                            uName = "" + postLikeListData?.userLastName
                        } else if (!postLikeListData?.userFirstName.isNullOrEmpty() && postLikeListData?.userLastName.isNullOrEmpty()
                        ) {
                            uName =postLikeListData?.userFirstName + ""
                        }
                        Bundle().apply {
                            putString("userID", postLikeListData?.userID)
                            putString("userName", uName)
                            profileDetailFragment.arguments = this
                        }
                    }
                    (context as MainActivity).navigateTo(
                        profileDetailFragment,
                        profileDetailFragment::class.java.name,
                        true
                    )
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