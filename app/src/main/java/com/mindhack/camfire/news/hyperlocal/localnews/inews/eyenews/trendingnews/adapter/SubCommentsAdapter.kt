package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.ReplyCommentData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.subcommentslayout.view.*

/**
 * Created by ADMIN on 20/01/2018.
 */
class SubCommentsAdapter(
    val context: Context,
    val arrayData: List<ReplyCommentData?>?,
    val onItemClick: OnItemClick,
    val type: String

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelection = -1
    var widthNew = 0
    var heightNew = 0
    var userID = ""
    var sessionManager: SessionManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.subcommentslayout, parent, false)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            val holder1 = holder
            // getScrennwidth()

            sessionManager = SessionManager(context)
            if (sessionManager != null && sessionManager?.isLoggedIn()!!) {
                userID = sessionManager?.get_Authenticate_User()?.userID!!
            }

            if (arrayData?.get(holder1.adapterPosition) != null) {
                holder1.bind(
                    holder.adapterPosition,
                    onItemClick,
                    widthNew,
                    heightNew,
                    arrayData[holder1.adapterPosition]!!,
                    userID

                )
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayData?.size!!
    }

    /*override fun getItemViewType(position: Int): Int {
        return if (orderdetails[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }*/

    class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            position: Int,
            onitemClick: OnItemClick,
            widthNew: Int,
            heightNew: Int,
            data: ReplyCommentData,
            userID: String

        ) =
            with(itemView) {
                var imgUri = ""
                if (!data.userProfilePicture.isNullOrEmpty()) {
                    imgUri = RestClient.image_base_url_users + data.userProfilePicture
                }
                imv_user_dp_comment.setImageURI(Uri.parse(imgUri))

                var userName = ""
                if (!data.userFirstName.isNullOrEmpty() && !data.userLastName.isNullOrEmpty()) {
                    userName = data.userFirstName + " " + data.userLastName
                } else if (!data.userFirstName.isNullOrEmpty() && data.userLastName.isNullOrEmpty()) {
                    userName = data.userFirstName + ""
                } else if (data.userFirstName.isNullOrEmpty() && !data.userLastName.isNullOrEmpty()) {
                    userName = data.userLastName + ""
                }
                tvCommentuserName.text = userName

                if (!data.mintuesago.isNullOrEmpty()) {
                    tvCommentTime.text = data.mintuesago
                }

                if (!data.commentreplyReply.isNullOrEmpty()) {
                    tvCommentuser.text = data.commentreplyReply
                }

                if (!data.commentreplyReply.isNullOrEmpty()) {
                    tvCommentuser.text = data.commentreplyReply
                }

                if(userID.equals(data.userID,false))
                {
                    ivlogoWaterMark.visibility=View.VISIBLE
                }
                else
                {
                    ivlogoWaterMark.visibility=View.GONE

                }

                ivlogoWaterMark.setOnClickListener {
                    val wrapper = ContextThemeWrapper(context, R.style.popmenu_style)
                    //init the popup
                    val popup = PopupMenu(wrapper,ivlogoWaterMark)
                    /*  The below code in try catch is responsible to display icons*/
                    if (true) {
                        try {
                            val fields = popup.javaClass.declaredFields
                            for (field in fields) {
                                if ("mPopup" == field.name) {
                                    field.isAccessible = true
                                    val menuPopupHelper = field.get(popup)
                                    val classPopupHelper =
                                        Class.forName(menuPopupHelper.javaClass.name)
                                    val setForceIcons =
                                        classPopupHelper.getMethod(
                                            "setForceShowIcon",
                                            Boolean::class.javaPrimitiveType
                                        )
                                    setForceIcons.invoke(menuPopupHelper, true)
                                    break
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    //inflate menu
                    popup.menuInflater.inflate(R.menu.popup_with_image, popup.menu)
                    //implement click events
                    popup.menu.getItem(0).title = "Edit"
                    popup.menu.getItem(1).title = "Delete"

                    popup.menu.getItem(0).icon = null
                    popup.menu.getItem(1).icon = null

                    popup.menu.getItem(2).isVisible=false
                    popup.menu.getItem(3).isVisible=false
                    popup.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.edit_profile -> {
                                onitemClick.onClicklisneter(position, "EditReply", ivlogoWaterMark,data.commentreplyID,data.commentreplyReply)
                            }
                            R.id.settings -> {
                                onitemClick.onClicklisneter(position, "DeleteReply", ivlogoWaterMark,data.commentreplyID,data.commentreplyReply)
                            }
                        }
                        true
                    }
                    popup.show()

                }


            }
    }


    fun showDotMenu(v: View) {
        //init the wrapper with style

    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, actionType: String, v: View,commentReplyId:String,commentReply:String)
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