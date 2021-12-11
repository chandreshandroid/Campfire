package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.FeedFragmentPushNotification
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment.ProfileDetailFragment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.NotificationListPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationAdapter(
    var context: Context,
    var arrayData: kotlin.collections.ArrayList<NotificationListPojo.Datum?>,
    val btnlistener: BtnClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification, parent, false)
            return ViewHolder(v)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (arrayData.get(position) == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    private fun showLoadingView(viewHolder: LoaderViewHolder, position: Int) {
        //ProgressBar would be displayed
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is LoaderViewHolder) {
            showLoadingView(holder, position)
//            holder.mProgressBar?.visibility = View.VISIBLE
//            return

        } else if (holder is ViewHolder) {
//            mClickListener = onItemClick
            val holder1 = holder


            var userName = ""
            if (!arrayData[position]?.userFirstName.isNullOrEmpty() && !arrayData[position]?.userLastName.isNullOrEmpty()){
                userName = "${arrayData[position]?.userFirstName} ${arrayData[position]?.userLastName}"
            }else if (arrayData[position]?.userFirstName.isNullOrEmpty() && !arrayData[position]?.userLastName.isNullOrEmpty()){
                userName = "${arrayData[position]?.userLastName}"
            }else if (!arrayData[position]?.userFirstName.isNullOrEmpty() && arrayData[position]?.userLastName.isNullOrEmpty()){
                userName = "${arrayData[position]?.userFirstName}"
            }

            if (TextUtils.isEmpty(userName)) {
                holder1.itemNotificationTextUserName?.visibility = View.GONE
            } else {
                holder1.itemNotificationTextUserName?.visibility = View.VISIBLE
                holder1.itemNotificationTextUserName.text = userName
            }

            if (!arrayData[position]?.userProfilePicture.isNullOrEmpty()){
                holder1.itemNotificationImage.setImageURI(Uri.parse(RestClient.image_base_url_users + arrayData[position]?.userProfilePicture))
            }


            if (arrayData[position]?.notificationReadStatus.equals("No",false))
            {
                holder1.itemNotificationLayoutMain.setBackgroundColor(context.resources.getColor(R.color.notificationunread))
            }
            else
            {
                holder1.itemNotificationLayoutMain.setBackgroundColor(context.resources.getColor(R.color.white))
            }

            if (!arrayData[position]?.notificationMessageText.isNullOrEmpty()) {
                holder1.itemNotificationTextDesc.text = MyUtils.decodePushText(arrayData[position]?.notificationMessageText!!, context)
                /*val postdescription: String = arrayData[position]?.notificationMessageText!!
                holder1.itemNotificationTextDesc.displayFulltext(postdescription)
                holder1.itemNotificationTextDesc.tag = position

                holder1.itemNotificationTextDesc.setHashClickListener(object :
                    PostDesTextView.OnHashEventListener {
                    override fun onHashTagClick(friendsId: String?) {
                        var hashTagListFragment = HashTagListFragment()
                        Bundle().apply {
                            putString("hashTag", friendsId)
                            hashTagListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            hashTagListFragment,
                            hashTagListFragment::class.java.name,
                            true
                        )

                    }

                })
                holder1.itemNotificationTextDesc.setCustomEventListener(object :
                    PostDesTextView.OnCustomEventListener {
                    override fun onViewMore() {
                        val pos: String =
                            java.lang.String.valueOf(holder1.itemNotificationTextDesc.tag!!)
                        val gson = Gson()
                        val json = gson.toJson(arrayData.get(pos.toInt()))

                    }

                    override fun onFriendTagClick(friendsId: String?) {
                        var profileDetailFragment = ProfileDetailFragment()
*//*
                        if (!sessionManager.get_Authenticate_User().userID.equals(
                                friendsId,
                                false
                            )
                        ) {
                            var uName = ""
                            if (feedList.get(holder1.adapterPosition)!!.userID.equals(
                                    friendsId,
                                    false
                                )
                            ) {
                                if (!feedList.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && !feedList.get(
                                        holder1.adapterPosition
                                    )!!.userLastName.isNullOrEmpty()
                                ) {
                                    uName =
                                        feedList.get(holder1.adapterPosition)!!.userFirstName + " " + feedList.get(
                                            holder1.adapterPosition
                                        )!!.userLastName
                                } else if (feedList.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && !feedList.get(
                                        holder1.adapterPosition
                                    )!!.userLastName.isNullOrEmpty()
                                ) {
                                    uName =
                                        "" + feedList.get(holder1.adapterPosition)!!.userLastName
                                } else if (!feedList.get(holder1.adapterPosition)!!.userFirstName.isNullOrEmpty() && feedList.get(
                                        holder1.adapterPosition
                                    )!!.userLastName.isNullOrEmpty()
                                ) {
                                    uName =
                                        feedList.get(holder1.adapterPosition)!!.userFirstName + ""
                                }
                            }
                            Bundle().apply {
                                putString("userID", friendsId)
                                putString("userName", uName)
                                putSerializable("feedData", feedList?.get(holder1.adapterPosition)!!)
                                profileDetailFragment.arguments = this
                            }
                        }
                        (context as MainActivity).navigateTo(
                            profileDetailFragment,
                            profileDetailFragment::class.java.name,
                            true
                        )*//*
                    }
                })*/
            } else {
                holder1.itemNotificationTextDesc.visibility = View.GONE

            }
            try {
                if (!arrayData[position]?.notificationSendDate.isNullOrEmpty() && !arrayData[position]?.notificationSendTime.isNullOrEmpty()) {
                    /*holder1.itemNotificationTextDateTime.text = MyUtils.findTimeDifference(
                        "${arrayData[position]?.notificationSendDate} ${arrayData[position]?.notificationSendTime}",
                        SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Date()),
                        "yyyy-MM-dd HH:mm:ss"
                    )*/
                    holder1.itemNotificationTextDateTime.text = MyUtils.covertTimeToText(
                        "${arrayData[position]?.notificationSendDate} ${arrayData[position]?.notificationSendTime}"
                    )


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            holder1.itemView.setOnClickListener {
                if (arrayData[position]?.notificationReadStatus.equals("No",false))
                {
                    btnlistener.onBtnClick(holder1.adapterPosition, "read")
                }
                if (holder1.adapterPosition > -1 && arrayData[position]?.notificationType.equals("post-liked") ||
                    arrayData[position]?.notificationType.equals("post-commented") ||
                    arrayData[position]?.notificationType.equals("post-shared") ||
                    arrayData[position]?.notificationType.equals("post-tagged")||
                    arrayData[position]?.notificationType.equals("new-post-created")
                ) {
                    val bundle = Bundle()
                    bundle.apply {
                        putString("postId", arrayData[position]?.notificationReferenceKey)
                    }
                    (context as MainActivity).navigateTo(
                        FeedFragmentPushNotification(),
                        bundle,
                        FeedFragmentPushNotification::class.java.name,
                        true
                    )
                } else if (arrayData[position]?.notificationType.equals("follow") || arrayData[position]?.notificationType.equals(
                        "clapped"
                    )
                ) {
                    var profileDetailFragment = ProfileDetailFragment()
                    Bundle().apply {
                        putString("userID", arrayData[position]?.notificationReferenceKey)
                        putString("userName", "")
                        putSerializable("feedData", null)
                        profileDetailFragment.arguments = this
                    }
                    (context as MainActivity).navigateTo(
                        profileDetailFragment,
                        profileDetailFragment::class.java.name,
                        true
                    )
                } else {

                }
            }

            holder1.itemView.deleteNotification.setOnClickListener {
//                if (btnlistener != null){
                    btnlistener.onBtnClick(holder1.adapterPosition, "")
//                }
            }

        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return arrayData.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemNotificationLayoutMain = itemView.itemNotificationLayoutMain
        var itemNotificationImage = itemView.itemNotificationImage
        var itemNotificationTextUserName = itemView.itemNotificationTextUserName
        var itemNotificationTextDateTime = itemView.itemNotificationTextDateTime
        var itemNotificationTextDesc = itemView.itemNotificationTextDesc
    }

    open interface BtnClickListener {
        fun onBtnClick(position: Int, buttonName: String)
    }
}