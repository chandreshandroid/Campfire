package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.view.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommentPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.ReplyCommentData
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.viewholder.LoaderViewHolder
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import kotlinx.android.synthetic.main.item_comment_list.view.*
import com.chauthai.swipereveallayout.ViewBinderHelper




class CommentListAdapter(
    val context: Activity,
    val arrayData: ArrayList<CommentPojo.Data?>,
    val onItemClick: OnItemClick,
    val type: String,
    val arrayReplyComment: ArrayList<ReplyCommentData?>

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelection = -1
    var widthNew = 0
    var heightNew = 0
    var userID = ""
    var sessionManager: SessionManager? = null
    private val viewBinderHelper = ViewBinderHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comment_list, parent, false)
            return ViewHolder(v, context,arrayReplyComment)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            val holder1 = holder as ViewHolder
            // getScrennwidth()
            viewBinderHelper.bind(holder1.swipeRevealLayout, arrayData.get(position)!!.commentID);

            sessionManager = SessionManager(context)
            if (sessionManager != null && sessionManager?.isLoggedIn()!!) {
                userID = sessionManager?.get_Authenticate_User()?.userID!!
            }

            if (arrayData[holder1.adapterPosition] != null){
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
        return arrayData.size
    }

   override fun getItemViewType(position: Int): Int {
        return if (arrayData[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class ViewHolder(
        itemView: View,
        context: Activity,
        arrayReplyComment: ArrayList<ReplyCommentData?>
    ) : RecyclerView.ViewHolder(itemView) {

        public var progressComment=itemView.progressComment
        public var tvCommenViewAll=itemView.tvCommenViewAll
        public var rvViewAllComment=itemView.rvViewAllComment
        public var swipeRevealLayout=itemView.swipeRevealLayout

        fun bind(
            position: Int,
            onitemClick: OnItemClick,
            widthNew: Int,
            heightNew: Int,
            data: CommentPojo.Data,
            userID: String


        ) =
            with(itemView) {

                var imgUri = ""
                if (!data.userProfilePicture.isNullOrEmpty()) {
                    imgUri = RestClient.image_base_url_users+ data.userProfilePicture
                }
                imv_user_dp_comment.setImageURI(Uri.parse(imgUri))

                var userName = ""

                if (!data?.userFirstName.isNullOrEmpty() && !data?.userLastName.isNullOrEmpty()) {
                    userName = data?.userFirstName + " " + data?.userLastName
                } else if (!data?.userFirstName.isNullOrEmpty() && data?.userLastName.isNullOrEmpty()) {
                    userName = data?.userFirstName + ""
                } else if (data?.userFirstName.isNullOrEmpty() && !data?.userLastName.isNullOrEmpty()) {
                    userName = data?.userLastName + ""
                }
              tvCommentuserName.text = userName

                if (!data.mintuesago.isNullOrEmpty()) {
                    tvCommentTime.text = data.mintuesago
                }

                if (!data.commentComment.isNullOrEmpty()) {
                   tvCommentuser.text = data.commentComment
                }

                if (!data.commentComment.isNullOrEmpty()) {
                   tvCommentuser.text = data.commentComment
                }

                if (data.likeCount.isNullOrEmpty()) {
                   tv_like_count.text = "0"
                }else{
                    tv_like_count.text = java.lang.Integer.valueOf(data.likeCount).toString()+" Likes"
                }
                if(data.isYouLikedComment.equals("Yes",false))
                {
                    img_like.setImageResource(R.drawable.like_filled_small)
                }
                else
                {
                    img_like.setImageResource(R.drawable.fevorite_drawer_icon)
                }
                if(data.commentLike.toInt()>0)
                {
                    tv_like_count.visibility=View.VISIBLE
                    tv_like_count.text=data.commentLike +" Likes"

                }else
                {
                    tv_like_count.visibility=View.GONE

                }

                if (!userID.isNullOrEmpty() && userID.equals(data.userID, true)) {
                    ivlogoWaterMark.setImageResource(R.drawable.icon_delete)
                } else {
                   ivlogoWaterMark.setImageResource(R.drawable.report_icon_black)
                }

               ivlogoWaterMark.setOnClickListener {
                    if (!userID.isNullOrEmpty() && userID.equals(data.userID, true)) {
                        onitemClick.onClicklisneter(position, 0, data,itemView,data.commentID,data.commentComment,-1)
                    } else {
                        val wrapper = ContextThemeWrapper(context, R.style.popmenu_style,)
                        //init the popup
                        val popup = PopupMenu(wrapper, itemView.ivlogoWaterMark, Gravity.RIGHT)

                        /*  The below code in try catch is responsible to display icons*/
                        if (true) {
                            try {
                                val fields = popup.javaClass.declaredFields
                                for (field in fields) {
                                    if ("mPopup" == field.name) {
                                        field.isAccessible = true
                                        val menuPopupHelper = field.get(popup)
                                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                                        val setForceIcons =
                                            classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
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

                        popup.menu.getItem(0).title = "Report Comment"
                        popup.menu.getItem(0).setIcon(R.drawable.report_icon_black)
                        popup.menu.getItem(1).isVisible = false
                        popup.menu.getItem(2).isVisible = false
                        popup.menu.getItem(3).isVisible = false

                        popup.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.edit_profile -> {
                                    onitemClick.onClicklisneter(position, 1, data,itemView,data.commentID,data.commentComment,-1)
                                }
                            }
                            true
                        }
                        popup.show()
                    }
                }

                img_like.setOnClickListener {
                    if (position>-1)
                      onitemClick.onClicklisneter(position, 3, data,img_like,data.commentID,data.commentComment,-1)
                }

                tv_reply_comment.text = GetDynamicStringDictionaryObjectClass?.Reply

                tv_reply_comment.setOnClickListener {
                    if (position>-1)
                        onitemClick.onClicklisneter(position, 4, data,img_like,data.commentID,data.commentComment,-1)
                }


              if(data.isVisibleComment)
              {
                  if(!data.replyCommentData.isNullOrEmpty())
                  {
                      if(data.replyCommentData.size >10)
                      {
                          tvCommenViewAll.visibility=View.GONE
                      }
                      else
                      {
                          tvCommenViewAll.visibility=View.GONE
                      }

                      var linearLayoutManager = LinearLayoutManager(context!!)
                      subCommentsRecyclerView.layoutManager = linearLayoutManager
                      subCommentsRecyclerView.visibility=View.VISIBLE

                      var commentListAdapter = SubCommentsAdapter(
                          context, data?.replyCommentData,
                          object : SubCommentsAdapter.OnItemClick {
                              override fun onClicklisneter(
                                  pos: Int,
                                  actionType: String, v3: View,commentReplyId:String,comment:String
                              ) {
                                  when (actionType) {

                                      "EditReply" -> {
                                          onitemClick.onClicklisneter(position, 7, data,v3,commentReplyId,comment,pos)
                                      }
                                      "DeleteReply" -> {
                                          onitemClick.onClicklisneter(position, 6, data,v3,commentReplyId,comment,pos)

                                      }
                                  }
                              }

                          }, ""
                      )
                      subCommentsRecyclerView.adapter = commentListAdapter
                      commentListAdapter?.notifyDataSetChanged()

                  }
                  else
                  {
                      tvCommenViewAll.visibility=View.GONE
                      subCommentsRecyclerView.visibility=View.GONE
                  }
              }
                else
              {
                  subCommentsRecyclerView.visibility=View.GONE

              }



                tvCommenViewAll.setOnClickListener {
                    tvCommenViewAll.visibility=View.GONE
                    onitemClick.onClicklisneter(position, 5, data, progressComment,data.commentID,data.commentComment,-1)

                }


            }







    }
    interface OnItemClick {
        fun onClicklisneter(pos: Int, actionType: Int, comentObj : CommentPojo.Data,v:View,commentId:String,comment:String,replyPos:Int)
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