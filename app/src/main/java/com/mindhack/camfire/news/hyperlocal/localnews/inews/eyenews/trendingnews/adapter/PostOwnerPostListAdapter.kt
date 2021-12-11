package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.CreateSavePostActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.item_post_owner_post_list.view.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.LoginActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.ImagesDetailsFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.VideoDetailsFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PostOwnerPostListAdapter(
    var context: Context,
    /*var arrayData: ArrayList<NotificationFragment.NotificationData>,*/
    val btnlistener: BtnClickListener,
    val feedList: ArrayList<TrendingFeedData?>,
    val youFollowing: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var sessionManager : SessionManager? = null
    var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

//        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_post_owner_post_list, parent, false)
//        return ViewHolder(v)

        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_post_owner_post_list, parent, false)
            return ViewHolder(v)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (feedList.get(position) == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    private fun showLoadingView(viewHolder: LoaderViewHolder, position: Int) {
        //ProgressBar would be displayed

    }

    override fun onBindViewHolder(holder1: RecyclerView.ViewHolder, position: Int) {
        sessionManager = SessionManager(context!!)
        if (holder1 is LoaderViewHolder)
        {
            showLoadingView(holder1, position)
//            holder.mProgressBar?.visibility = View.VISIBLE
//            return

        } else if (holder1 is ViewHolder) {
            val holder = holder1 as ViewHolder

            if(feedList[position]?.postMediaType.equals("Photo",true))
            {
                holder.itemPostOwnerPostImagePlay.visibility = View.GONE
                holder.itemPostOwnerPostListImage.setBackgroundColor(-1)
                 if(!feedList[position]!!.postSerializedData?.get(0)?.albummedia[0].albummediaFile.isNullOrEmpty())
                 {
                     holder.itemPostOwnerPostListImage.setImageURI(RestClient.image_base_url_post+ feedList[position]!!.postSerializedData?.get(0)?.albummedia[0].albummediaFile)

                 }
                else
                 {
                     holder.itemPostOwnerPostListImage.setImageResource(R.drawable.banner_placeholder_camfire)

                 }
            }
            else if(feedList[position]?.postMediaType.equals("Video",true))
            {
                holder.itemPostOwnerPostImagePlay.visibility = View.VISIBLE
                if(!feedList[position]!!.postSerializedData?.get(0)?.albummedia[0].albummediaThumbnail.isNullOrEmpty())
                {
                    holder.itemPostOwnerPostListImage.setImageURI(RestClient.image_base_url_post+feedList[position]!!.postSerializedData?.get(0)?.albummedia[0].albummediaThumbnail)
                }
                else{
                    holder.itemPostOwnerPostListImage.setBackgroundColor(context.resources.getColor(R.color.black))

                }
            }

            holder.itemPostOwnerPostTextLabel.text=feedList[position]!!.postHeadline
            holder.tvDraft.text = ""+GetDynamicStringDictionaryObjectClass?.Draft
            if(sessionManager?.isLoggedIn()!! && sessionManager?.get_Authenticate_User()?.userID.equals(feedList[holder.adapterPosition]?.userID)!! &&feedList[holder.adapterPosition]?.postCreateType.equals("Save"))
            {
                holder.llDraft.visibility = View.VISIBLE
            }else {
                holder.llDraft.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if(!feedList[holder.adapterPosition]?.postCreateType.equals("Save"))
                {
                    if (feedList[position]?.postMediaType.equals("Video", false))
                    {

                        if(sessionManager?.isLoggedIn()!!)
                        {
                            if(!youFollowing.isNullOrEmpty()){
                                feedList[position]?.isYouFollowing=youFollowing
                            }
                            var videoDeailsFragment = VideoDetailsFragment()
                                Bundle().apply {
                                    putSerializable("feedData", feedList[position])
                                    videoDeailsFragment.arguments = this
                                }
                                (context as MainActivity).navigateTo(
                                    videoDeailsFragment,
                                    videoDeailsFragment::class.java.name,
                                    true
                                )
                       }

                        else
                        {
                            MyUtils.startActivity(context,LoginActivity::class.java,true)

                        }


                    }
                    else if (feedList[position]?.postMediaType.equals("Photo", false))
                    {
                         if(sessionManager?.isLoggedIn()!!)
                         {
                             if(!youFollowing.isNullOrEmpty()){
                                 feedList[position]?.isYouFollowing=youFollowing
                             }
                             var imagesDetailsFragment = ImagesDetailsFragment()
                             Bundle().apply {
                                 putSerializable("feedData", feedList[position])
                                 imagesDetailsFragment.arguments = this
                             }
                             (context as MainActivity).navigateTo(
                                 imagesDetailsFragment,
                                 imagesDetailsFragment::class.java.name,
                                 true
                             )
                         }
                        else
                         {
                             MyUtils.startActivity(context,LoginActivity::class.java,true)
                         }
                    }
                }
                else
                {
                    if(sessionManager?.isLoggedIn()!! && sessionManager?.get_Authenticate_User()?.userID.equals(feedList[holder.adapterPosition]?.userID,false))
                    {
                       val myIntent = Intent(context!!, CreateSavePostActivity::class.java)
                       if(feedList[position]?.postMediaType.equals("Photo",true))
                       {
                        myIntent.putExtra("from", "images")
                        myIntent.putExtra("postData", feedList[holder.adapterPosition])
                        context.startActivity(myIntent)
                      } else if(feedList[position]?.postMediaType.equals("Video",true))
                       {
                          myIntent.putExtra("from", "video")
                          myIntent.putExtra("postData", feedList[holder.adapterPosition])
                          context.startActivity(myIntent)
                       }
                    }
                }
            }

            if(!feedList[position]?.postCreatedMinutesAgo.isNullOrEmpty()){
                var now = Calendar.getInstance()
                now = Calendar.getInstance()
                now.add(Calendar.MINUTE, - Integer.parseInt(feedList[position]?.postCreatedMinutesAgo!!))
                val today = now.getTime()
                val reportDate = sdf.format(today)
                Log.e("System out", "repodate : $reportDate")

                val dateText = MyUtils.getDisplayableTime(MyUtils.dateToMilliSeconds(reportDate))
                if (dateText.equals("Yesterday")) {
                    val data = ""
                    var tT = ""
                    try {

                        tT = MyUtils.formatDate(reportDate, "yyyy-MM-dd HH:mm:ss", "hh:mm a")
                        if (tT.contains("p.m.")) {
                            tT = tT.replace("p.m.", "PM")
                        } else if (tT.contains("a.m.")) {
                            tT = tT.replace("a.m.", "AM")
                        } else if (tT.contains("P.M.")) {
                            tT = tT.replace("P.M.", "PM")
                        } else if (tT.contains("A.M.")) {
                            tT = tT.replace("A.M.", "AM")
                        }
                        holder.itemPostOwnerPostTextTime.setText("Yesterday\n$tT")
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }


                }else if (dateText.equals("")) {
                    var data = ""
                    var tT = ""
                    try {
                        data = MyUtils.formatDate(reportDate, "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy")
                        tT = MyUtils.formatDate(reportDate, "yyyy-MM-dd HH:mm:ss", "hh:mm a")
                        if (tT.contains("p.m.")) {
                            tT = tT.replace("p.m.", "PM")
                        } else if (tT.contains("a.m.")) {
                            tT = tT.replace("a.m.", "AM")
                        } else if (tT.contains("P.M.")) {
                            tT = tT.replace("P.M.", "PM")
                        } else if (tT.contains("A.M.")) {
                            tT = tT.replace("A.M.", "AM")
                        }
                        holder.itemPostOwnerPostTextTime.setText(data +"\n"+ tT)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                }else {
                    holder.itemPostOwnerPostTextTime.setText(dateText)
                }


            }
            if(sessionManager?.isLoggedIn()!! && sessionManager?.get_Authenticate_User()?.userID.equals(feedList[holder.adapterPosition]?.userID!!)&&feedList[holder.adapterPosition]?.postStatus!!.equals("Banned",false) )
            {
                holder.itemPostOwnerPostImageDot.visibility=View.VISIBLE
            }
            else
            {
                holder.itemPostOwnerPostImageDot.visibility=View.GONE
            }

           /* holder.itemView.setOnClickListener {
                if(sessionManager?.get_Authenticate_User()?.userID!!.equals(feedList[holder.adapterPosition]?.userID!!))
                {

                    if(feedList[holder.adapterPosition]?.postCreateType.equals("Save")){

                        val myIntent = Intent(context!!, CreateSavePostActivity::class.java)
                        if(feedList[position]?.postMediaType.equals("Photo",true)){
                            myIntent.putExtra("from", "images")
                            myIntent.putExtra("postData", feedList[holder.adapterPosition])
                            context.startActivity(myIntent)
                        }else if(feedList[position]?.postMediaType.equals("Video",true)){
//                            myIntent.putExtra("from", "video")
                        }

                    }
                }else {

                }
            }*/
            holder.itemPostOwnerPostImageDot.setOnClickListener {
                if(btnlistener!=null)
                btnlistener.onBtnClick(holder1.adapterPosition,"threeDots",holder.itemPostOwnerPostImageDot)
            }
        }





    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return feedList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemPostOwnerPostListLayoutMain = itemView.itemPostOwnerPostListLayoutMain
        var itemPostOwnerPostListImage = itemView.itemPostOwnerPostListImage
        var itemPostOwnerPostImagePlay = itemView.itemPostOwnerPostImagePlay
        var itemPostOwnerPostTextLabel = itemView.itemPostOwnerPostTextLabel
        var itemPostOwnerPostTextTime = itemView.itemPostOwnerPostTextTime
        var itemPostOwnerPostImageDot = itemView.itemPostOwnerPostImageDot
        var llDraft = itemView.llDraft
        var tvDraft = itemView.tvDraft
    }

    open interface BtnClickListener {
        fun onBtnClick(
            position: Int,
            buttonName: String,
            itemPostOwnerPostImageDot: AppCompatImageView
        )
    }
}