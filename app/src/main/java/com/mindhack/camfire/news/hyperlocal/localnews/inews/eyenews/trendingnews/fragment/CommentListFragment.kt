package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.fragment


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.Service.NotifyInterface
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.activity.MainActivity
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.adapter.CommentListAdapter
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.CommentModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.PostViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.ReasonModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model.ReplyCommentModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.*
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.GetDynamicStringDictionaryObjectClass
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.generallistlayout.*
import kotlinx.android.synthetic.main.header_back_with_text.*
import kotlinx.android.synthetic.main.nodatafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.fragment_comment_list.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 */
class CommentListFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: RegisterPojo.Data? = null
    var titleChangePassword = ""

    var commentListAdapter: CommentListAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var pageNo = 0
    var commnethint = ""

    var arrayComment: ArrayList<CommentPojo.Data?>? = null
   var arrayReplyComment: ArrayList<ReplyCommentData?> = ArrayList()

    var msgAddCommnet = ""
    var msgErrorAddCommnet = ""
    var postID = ""
    var postUserID = ""
    var masemptyCommentBox = ""
    var msgNoInternet = ""
    var msgSomthingRong = ""
    var alertDeleteMessage = ""
    var msgSuccessCommentDelete = ""
    var msgFailCommentDelete = ""
    var msgSuccessCommentReport = ""
    var msgFailCommentReport = ""

    var objReason: ReasonList? = null
    var isCommentReply = false
    var commentID: String = ""
    var mPosition:Int=-1
    var progressBar:ProgressBar?=null

    var replyCommentId=""
    var commentReplyReply=""
    var replyCommentPos=-1
    var feedData:TrendingFeedData?=null
    var notifyInterface: NotifyInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_comment_list, container, false)
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
//        (activity as MainActivity).showHideBottomNavigation(false)

        if (sessionManager != null && sessionManager!!.isLoggedIn()) {
            userData = sessionManager?.get_Authenticate_User()
            setUserData1()
        }
        dynamicString()
        var strSub = GetDynamicStringDictionaryObjectClass?.Posts
        strSub = strSub.substring(0,GetDynamicStringDictionaryObjectClass?.Posts.length-1)

        tvCommentPost?.text = strSub
        tvHeaderText?.text = titleChangePassword
        imgCloseIcon.setOnClickListener {
            (mActivity as MainActivity).onBackPressed()
        }

        if (arguments != null && arguments?.getString("postID") != null) {
            postID = arguments?.getString("postID")!!
            postUserID = arguments?.getString("postUserID")!!
            feedData= arguments?.getSerializable("feedData") as TrendingFeedData?
        }

        noDatafoundRelativelayout.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        relativeprogressBar.visibility = View.GONE
//      editComment.hint = commnethint
        setReply(false, "")
        linearLayoutManager = LinearLayoutManager(mActivity!!)
        generalRecyclerView.layoutManager = linearLayoutManager


        generalRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = linearLayoutManager?.childCount!!
                totalItemCount = linearLayoutManager?.itemCount!!
                firstVisibleItemPosition = linearLayoutManager?.findFirstVisibleItemPosition()!!
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 10
                    ) {
                        isLoading = true

                        getCommentList()
                    }
                }
            }
        })

        setCommentAdapter()

        editComment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().length > 0) {
                    tvCommentPost.setTextColor(resources.getColor(R.color.text_blue_dark))
                    tvCommentPost.isClickable = true
                    tvCommentPost.isEnabled = true
                } else {
                    tvCommentPost.setTextColor(resources.getColor(R.color.text_gray))
                    tvCommentPost.isClickable = false
                    tvCommentPost.isEnabled = false
                }
            }
        })

        tvCommentPost.setOnClickListener {
            if (isCommentReply)
            {

                if (!editComment.text.toString().trim().isNullOrEmpty()) {
                    if(!replyCommentId.isEmpty()&& !replyCommentId.isEmpty() && replyCommentPos>-1)
                    {
                        editComment(replyCommentId,replyCommentPos,mPosition)
                    }
                    else
                    {
                        addReplyPostComment(commentID)

                    }
                }
            }
            else
            {
                if (!editComment.text.toString().isNullOrEmpty()) {
                    addPostComment()
                }
            }

        }

        btnRetry.setOnClickListener {
            if (MyUtils.isInternetAvailable(mActivity!!)) {
                getCommentList()
            } else {
                setLayoutAsPerCondition(false, false, false, true, false)

            }
        }

        img_clear.setOnClickListener {
            isCommentReply = false
            setReply(false, "")
        }
    }

    private fun setUserData1() {
        if (userData != null) {
            var imgUrl = ""
            if (!userData?.userProfilePicture.isNullOrEmpty()) {
                imgUrl = RestClient.image_base_url + "users/" + userData?.userProfilePicture
            }
            imv_user_dp_comment?.setImageURI(Uri.parse(imgUrl))

            var userName = ""
            if (!userData?.userFirstName.isNullOrEmpty() && !userData?.userLastName.isNullOrEmpty()) {
                userName = userData?.userFirstName + " " + userData?.userLastName
            } else if (!userData?.userFirstName.isNullOrEmpty() && userData?.userLastName.isNullOrEmpty()) {
                userName = userData?.userFirstName + ""
            } else if (userData?.userFirstName.isNullOrEmpty() && !userData?.userLastName.isNullOrEmpty()) {
                userName = userData?.userLastName + ""
            }

//            editComment.hint = "$commnethint $userName"

        }
    }

    private fun setCommentAdapter() {
        if (arrayComment.isNullOrEmpty()) {
            arrayComment = ArrayList<CommentPojo.Data?>()
            commentListAdapter = CommentListAdapter(
                mActivity!!, arrayComment!!,
                object : CommentListAdapter.OnItemClick {
                    override fun onClicklisneter(
                        pos: Int,
                        actionType: Int,
                        comentObj: CommentPojo.Data, v1: View,commentId:String,comment:String,replyPos:Int
                    ) {

                        mPosition=pos
                        commentID = comentObj.commentID



                        /**
                         * 0 for delete commment
                         * 1 for reprt comment
                         * */
                        when (actionType) {

                            0 -> {
                                //delete comment
                                MyUtils.showMessageYesNo(
                                    mActivity!!,
                                    alertDeleteMessage,
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            dialog?.dismiss()
                                            deleteComment(comentObj.commentID, pos)
                                        }

                                    })
                            }

                            1 -> {
                                //report commnet
                                if (objReason == null) {
                                    getReasonList(comentObj.commentID, pos)
                                } else {
                                    openReasonListDialog(comentObj.commentID, pos)
                                }
                            }
                            3 -> {
                                feedFavorite(v1 as AppCompatImageView, pos)
                            }
                            4 -> {
                                isCommentReply = true
                                //showKeyboard()

                                setReply(
                                    true,
                                    comentObj.userFirstName + " " + comentObj.userLastName
                                )

                                getReplyCommentList(pos,arrayComment?.get(pos)!!.commentID)

                                editComment.requestFocus()
                            }
                            5->{
                                progressBar=v1 as ProgressBar
                                getReplyCommentList(pos,arrayComment?.get(pos)!!.commentID)
                            }

                            6->
                            {
                                MyUtils.showMessageYesNo(
                                    mActivity!!,
                                    alertDeleteMessage,
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            dialog?.dismiss()
                                            deleteReplyComment(commentId, replyPos,pos)
                                        }

                                    })

                            }
                            7->
                            {
                                editComment.setText(comment)
                                editComment.setSelection(editComment.length())
                                val imm =
                                    mActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.showSoftInput(
                                    editComment,
                                    InputMethodManager.SHOW_IMPLICIT )

                                replyCommentPos=replyPos
                                replyCommentId=commentId
                                commentReplyReply=comment
                                isCommentReply = true
                                showKeyboard()
                                setReply(true, comentObj.userFirstName + " " + comentObj.userLastName)
                                editComment.requestFocus()

                            }
                        }
                    }

                }, "",arrayReplyComment
            )
            generalRecyclerView.adapter = commentListAdapter
            commentListAdapter?.notifyDataSetChanged()

            if (MyUtils.isInternetAvailable(mActivity!!)) {
                getCommentList()
            } else setLayoutAsPerCondition(false, false, false, true, false)
        }
    }


    fun feedFavorite(
        favouriteimageview: AppCompatImageView,
        feedPosition: Int
    ) {
        favouriteimageview.isEnabled = false
        val action: String
        if (arrayComment!!.get(feedPosition)!!.isYouLikedComment.equals("No", false)) {

            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.like_filled_small))
            var animation = AnimationUtils.loadAnimation(mActivity, R.anim.bounce)
            favouriteimageview.startAnimation(animation)

            action = "LikePostCommnet"
            var count = 0
            try {
                count = Integer.valueOf(arrayComment!!.get(feedPosition)!!.commentLike) + 1
            } catch (e: java.lang.Exception) {
            }
            arrayComment!!.get(feedPosition)!!.commentLike = count.toString()
            arrayComment?.get(feedPosition)!!.isYouLikedComment = ("Yes")
            commentListAdapter?.notifyItemChanged(feedPosition)
        } else {
            favouriteimageview.setImageDrawable(resources.getDrawable(R.drawable.like_reaction_icon_small))
            action = "RemoveLikePostCommnet"
            var count = 0
            try {
                count = Integer.valueOf(arrayComment!!.get(feedPosition)!!.commentLike) - 1
            } catch (e: java.lang.Exception) {
            }
            arrayComment!!.get(feedPosition)!!.commentLike = count.toString()
            arrayComment?.get(feedPosition)!!.isYouLikedComment = ("No")
            commentListAdapter?.notifyItemChanged(feedPosition)
        }
        val json = Gson().toJson(arrayComment?.get(feedPosition))
        val trendingFeedDatum =
            Gson().fromJson(json, TrendingFeedData::class.java)
        setCommentLike(
            arrayComment?.get(feedPosition)!!.commentID,
            action,
            feedPosition,
            trendingFeedDatum
        )
        favouriteimageview.isEnabled = true
    }

    private fun setCommentLike(
        commentID: String,
        action: String,
        feedPosition: Int,
        trendingFeedDatum: TrendingFeedData?
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("commentID", commentID)
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        val postViewModel: PostViewModel =
            ViewModelProviders.of((this@CommentListFragment)).get(
                PostViewModel::class.java
            )
        postViewModel.apiCall(mActivity!!, jsonArray.toString(), action).observe(
            this@CommentListFragment,
            Observer { postlikePojos ->
                if (mActivity != null) {
                    if (postlikePojos?.get(0)?.status.equals("true", false)) {
                        when (action) {
                            "LikePostCommnet" -> {
                                arrayComment?.get(feedPosition)!!.isYouLikedComment = "Yes"
                            }
                            "RemoveLikePostCommnet" -> {
                                arrayComment?.get(feedPosition)!!.isYouLikedComment = "No"
                            }
                        }

                        // (activity as MainActivity).showSnackBar(postlikePojos?.get(0)?.message!!)


                    } else {

                        (activity as MainActivity).showSnackBar(postlikePojos?.get(0)?.message!!)
                    }

                }
            })
    }

    private fun dynamicString() {
        titleChangePassword = "" + GetDynamicStringDictionaryObjectClass?.Comments
        commnethint = "Comment as"

        msgAddCommnet = resources.getString(R.string.success_add_commnet)
        msgErrorAddCommnet = resources.getString(R.string.failed_add_commnet)

        msgNoInternet = resources.getString(R.string.error_common_network)
        msgSomthingRong = resources.getString(R.string.error_crash_error_message)
        alertDeleteMessage = "Are you sure to delete your comment?"
        msgSuccessCommentDelete = "Comment deleted successfully"
        msgFailCommentDelete = "Failed to delete comment"
        msgSuccessCommentReport = resources.getString(R.string.success_comment_report)
        msgFailCommentReport = resources.getString(R.string.fail_comment_report)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        try {
            notifyInterface = activity as NotifyInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement TextClicked"
            )
        }
    }

    fun getCommentList() {
        if (pageNo == 0) {
            setLayoutAsPerCondition(false, true, false, false, false)
            arrayComment!!.clear()
            commentListAdapter?.notifyDataSetChanged()
        } else {
            setLayoutAsPerCondition(false, false, true, false, false)
            arrayComment!!.add(null)
            commentListAdapter?.notifyItemInserted(arrayComment!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            if (sessionManager?.isLoggedIn()!!) jsonObject.put(
                "loginuserID",
                userData?.userID
            ) else jsonObject.put("loginuserID", "0")

            val lID = sessionManager?.getsetSelectedLanguage()

            jsonObject.put("languageID", lID)
            jsonObject.put("postID", postID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val apiCall = ViewModelProviders.of(mActivity!!).get(CommentModel::class.java)
        apiCall?.apiFunction(mActivity!!, false, jsonArray.toString(), 0)
            ?.observe(this@CommentListFragment,
                Observer<List<CommentPojo>?>
                { response ->
                    if (!response.isNullOrEmpty()) {

                        //data found

                        isLoading = false
                        //   remove progress item
                        setLayoutAsPerCondition(false, false, true, false, false)

                        if (pageNo > 0) {
                            arrayComment!!.removeAt(arrayComment!!.size - 1)
                            commentListAdapter?.notifyItemRemoved(arrayComment!!.size)
                        }

                        if (response[0].status?.equals("true")!!) {
                            if (pageNo == 0)
                                arrayComment!!.clear()
                            if (arrayComment?.size!! < 1) {
                                editComment.requestFocus()
                            }


                            if (!response[0].data.isNullOrEmpty()) {
                                arrayComment!!.addAll(response[0].data!!)
                                commentListAdapter?.notifyDataSetChanged()
                                pageNo += 1
                                if (response[0].data!!.size < 10) {
                                    isLastpage = true
                                }
                            } else {
                                if (!arrayComment.isNullOrEmpty()) {
                                    setLayoutAsPerCondition(false, false, true, false, false)
                                } else setLayoutAsPerCondition(true, false, false, false, false)
                            }
                        } else {
                            if (arrayComment?.size == 0) {
                                showKeyboard()
                                editComment.requestFocus()
                            }
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                if (!arrayComment.isNullOrEmpty()) {
                                    setLayoutAsPerCondition(false, false, true, false, false)
                                } else setLayoutAsPerCondition(true, false, false, false, false)
                            } else {
                                setLayoutAsPerCondition(false, false, false, true, false)
                            }
                        }
                    } else {
                        //No internet and somthing rong
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            setLayoutAsPerCondition(false, false, false, true, true)

                        } else {
                            setLayoutAsPerCondition(false, false, false, true, false)
                        }
                    }
                })
    }

    fun addPostComment() {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postID", postID)
            jsonObject.put("posterID", postUserID)
            jsonObject.put("commentComment", editComment.text.toString().trim())
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val apiCall = ViewModelProviders.of(mActivity!!).get(CommentModel::class.java)
        apiCall?.apiFunction(mActivity!!, true, jsonArray.toString(), 1)
            ?.observe(this@CommentListFragment,
                Observer<List<CommentPojo>?>
                { response ->

                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            //data found
                            /*if (response[0].message.isNullOrEmpty()){
                                MyUtils.showSnackbarkotlin(mActivity!!, commentLayoutMain!!, msgAddCommnet)
                            }else{
                                MyUtils.showSnackbarkotlin(mActivity!!, commentLayoutMain!!, response[0].message)
                            }*/


//                          arrayComment?.removeAt(arrayComment!!.size - 1)

                            if (!response[0].data.isNullOrEmpty()) {
                                arrayComment?.add(response[0].data[0])
                            }

                            commentListAdapter?.notifyDataSetChanged()

                            editComment.text?.clear()
                          //  sendBroadCast(0)
                            checkCommentList()
                            if (notifyInterface != null)
                            {
                                var count =0
                                count= feedData?.postComment!!.toInt()+1
                                feedData?.postComment=count.toString()

                                notifyInterface!!.notifyData(feedData,false,false, feedData?.postComment)
                            }
                        } else {
                            //data not find
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                if (response[0].message.isNullOrEmpty()) {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        msgErrorAddCommnet
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        response[0].message
                                    )
                                }
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    msgSomthingRong
                                )
                            }
                        }
                    } else {
                        //No internet and somthing rong
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgNoInternet
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgSomthingRong
                            )
                        }
                    }
                })
    }

    fun addReplyPostComment(commentID: String) {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postID", postID)
            jsonObject.put("commentID", commentID)
            jsonObject.put("commentreplyReply", editComment.text.toString())
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val apiCall = ViewModelProviders.of(mActivity!!).get(ReplyCommentModel::class.java)
        apiCall?.apiFunction(mActivity!!, true, jsonArray.toString(), 1)
            ?.observe(this@CommentListFragment,
                Observer
                { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            if (!response[0].data.isNullOrEmpty()) {
                               arrayReplyComment?.add(response[0].data[0])


                            }

                            commentListAdapter?.notifyDataSetChanged()
                            editComment.text?.clear()
                            setReply(false, "")
                              if(mPosition>-1){

                                  getReplyCommentList(mPosition,commentID)


                              }
                        } else {
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                if (response[0].message.isNullOrEmpty()) {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        msgErrorAddCommnet
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        response[0].message
                                    )
                                }
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    msgSomthingRong
                                )
                            }
                        }
                    } else {
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgNoInternet
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgSomthingRong
                            )
                        }
                    }
                })
    }

    private fun deleteComment(commentID: String, postionOfComment: Int) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("commentID", commentID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val apiCall = ViewModelProviders.of(mActivity!!).get(CommentModel::class.java)
        apiCall?.apiFunction(mActivity!!, true, jsonArray.toString(), 3)
            ?.observe(this@CommentListFragment,
                Observer<List<CommentPojo>?>
                { response ->

                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            //data found



                            if (response[0].message.isNullOrEmpty()) {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    msgSuccessCommentDelete
                                )

                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    response[0].message
                                )
                            }

                            arrayComment?.removeAt(postionOfComment)
                            checkCommentList()

                            if (notifyInterface != null)
                            {

                                var count=0
                                if(feedData?.postComment!!.toInt()>0)
                                {
                                    count= feedData?.postComment!!.toInt()-1

                                }

                                feedData?.postComment=count.toString()

                                notifyInterface!!.notifyData(
                                    feedData,
                                    false,
                                    true,
                                    feedData?.postComment
                                )
                            }

                            commentListAdapter?.notifyDataSetChanged()
                            //sendBroadCast(1)
                        } else {
                            //data not find
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                if (response[0].message.isNullOrEmpty()) {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        msgFailCommentDelete
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        response[0].message
                                    )
                                }
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    msgSomthingRong
                                )
                            }
                        }
                    } else {
                        //No internet and somthing rong
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgNoInternet
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgSomthingRong
                            )
                        }
                    }
                })
    }

    private fun deleteReplyComment(
        commentreplyID: String,
        postionOfReplyComment: Int,
        pos: Int
    ) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("commentreplyID", commentreplyID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val apiCall = ViewModelProviders.of(mActivity!!).get(ReplyCommentModel::class.java)
        apiCall?.apiFunction(mActivity!!, true, jsonArray.toString(), 3)
            ?.observe(this@CommentListFragment,
                Observer
                { response ->

                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true",false)) {

                            arrayComment?.get(pos)!!.replyCommentData.removeAt(postionOfReplyComment)
                            commentListAdapter?.notifyItemRemoved(  arrayComment?.get(pos)!!.replyCommentData!!.size)
                            commentListAdapter?.notifyDataSetChanged()

                            if (response[0].message.isNullOrEmpty()) {

                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    msgSuccessCommentDelete
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    response[0].message
                                )
                            }

                        } else {
                            //data not find
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                if (response[0].message.isNullOrEmpty()) {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        msgFailCommentDelete
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        response[0].message
                                    )
                                }
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    msgSomthingRong
                                )
                            }
                        }
                    } else {
                        //No internet and somthing rong
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgNoInternet
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgSomthingRong
                            )
                        }
                    }
                })
    }

    private fun reportComment(commentID: String, postionOfComment: Int, reasonID: String) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postID", postID)
            jsonObject.put("posterID", postUserID)
            jsonObject.put("reasonID", reasonID)
            jsonObject.put("commentID", commentID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val apiCall = ViewModelProviders.of(mActivity!!).get(CommentModel::class.java)
        apiCall?.apiFunction(mActivity!!, true, jsonArray.toString(), 5)
            ?.observe(this@CommentListFragment,
                Observer<List<CommentPojo>?>
                { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            //data found
                            /*if (notifyInterface != null)
                            {
                                notifyInterface!!.notifyData(
                                    feedData,
                                    false,
                                    true,
                                    feedData?.postComment
                                )
                            }*/
                            if (response[0].message.isNullOrEmpty()) {

                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    msgSuccessCommentReport
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    response[0].message
                                )
                            }

                            arrayComment?.removeAt(postionOfComment)
                            checkCommentList()

                            commentListAdapter?.notifyDataSetChanged()

                           // sendBroadCast(2)
                        } else {
                            //data not find
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                if (response[0].message.isNullOrEmpty()) {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        msgFailCommentReport
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        response[0].message
                                    )
                                }
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    msgSomthingRong
                                )
                            }
                        }
                    } else {
                        //No internet and somthing rong
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgNoInternet
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgSomthingRong
                            )
                        }
                    }
                })
    }

    fun getReasonList(cID: String, positionOFComment: Int) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val apiCall = ViewModelProviders.of(mActivity!!).get(ReasonModel::class.java)
        apiCall?.apiFunction(mActivity!!, true, jsonArray.toString())
            ?.observe(this@CommentListFragment,
                Observer<List<ReasonList>?>
                { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true")) {
                            //data found
                            objReason = response[0]
                            openReasonListDialog(cID, positionOFComment)
                        } else {
                            //data not find
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                reportComment(cID, positionOFComment, "")
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    msgSomthingRong
                                )
                            }
                        }
                    } else {
                        //No internet and somthing rong
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgNoInternet
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgSomthingRong
                            )
                        }
                    }
                })
    }

    private fun openReasonListDialog(cID: String, positionoFComment: Int) {
        val ft = mActivity!!.supportFragmentManager.beginTransaction()
        val dialogFragment = CustomPoupDialog.newInstance()
        val bundle = Bundle()

        bundle.putSerializable("ReasonList", objReason)

        dialogFragment.arguments = bundle
//        val params = window?.attributes
//        params?.gravity = Gravity.BOTTOM
//        params?.x = 0//b.getInt("x");
//        params?.y = 0//b.getInt("y");
//        window?.attributes = params

        dialogFragment.show(ft, "dialog")
        dialogFragment.setListener(object : CustomPoupDialog.OnItemClickListener {
            override fun onItemSelectionClick(typeButton: String, selectedID: String) {

                when (typeButton) {
                    "cancel" -> {

                    }
                    "done" -> {
                        reportComment(cID, positionoFComment, selectedID)
                    }
                }
            }
        })

    }

    private fun checkCommentList() {
        if (arrayComment.isNullOrEmpty()) {
            getCommentList()
        } else {
            setLayoutAsPerCondition(false, false, true, false, false)
        }
    }

    private fun getReplyCommentList(pos: Int, commentID: String) {
     if(progressBar!=null){
         progressBar?.visibility=View.VISIBLE

     }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            if (sessionManager?.isLoggedIn()!!) jsonObject.put(
                "loginuserID",
                userData?.userID
            ) else jsonObject.put("loginuserID", "0")

            val lID = sessionManager?.getsetSelectedLanguage()
            jsonObject.put("languageID", lID)
            jsonObject.put("commentreplyID","0")
            jsonObject.put("commentID", commentID)
            jsonObject.put("page", "0")
            jsonObject.put("pagesize", "100")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val apiCall = ViewModelProviders.of(mActivity!!).get(ReplyCommentModel::class.java)
        apiCall?.apiFunction(mActivity!!, false, jsonArray.toString(), 0)
            ?.observe(this@CommentListFragment,
                Observer
                { response ->
                    if (!response.isNullOrEmpty()) {
                        if (response[0].status.equals("true",false)) {
                            if(progressBar!=null){
                                progressBar?.visibility=View.GONE

                            }
                            if (!response[0].data.isNullOrEmpty()) {
                                  Log.w("SagarSagar",""+arrayComment?.get(pos)!!.replyCommentData)
                                arrayComment?.get(pos)!!.replyCommentData.clear()
                                arrayComment?.get(pos)!!.isVisibleComment=true
                                arrayComment?.get(pos)!!.replyCommentData.addAll(response[0].data)

                                commentListAdapter?.notifyDataSetChanged()

                            } else {
                                (activity as MainActivity).showSnackBar(response[0].message)
                           }
                        } else {
                            if(progressBar!=null){
                                progressBar?.visibility=View.GONE
                            }

                            if (arrayReplyComment?.size == 0) {
                                arrayComment?.get(pos)!!.isVisibleComment=false
                                showKeyboard()
                                editComment.requestFocus()
                            }
                        }
                    } else {
                        if( progressBar!=null){
                            progressBar?.visibility=View.GONE
                        }
                        (activity as MainActivity).errorMethod()
                    }
                })

    }

    fun setLayoutAsPerCondition(
        noData: Boolean,
        progress: Boolean,
        datafound: Boolean,
        noInternet: Boolean,
        somthingROng: Boolean
    ) {

        if (noData) {
            commentLayoutMain?.visibility = View.VISIBLE

            generalLayoutMain?.visibility = View.VISIBLE
            generalRecyclerView?.visibility = View.GONE

            relativeprogressBar?.visibility = View.GONE

            noDatafoundRelativelayout?.visibility = View.VISIBLE
            nodata1?.visibility = View.VISIBLE
            nodata1?.text = resources.getString(R.string.no_comment_list)
            nodatafoundtextview?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.GONE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview1?.visibility = View.VISIBLE
            nointernettextview?.visibility = View.VISIBLE

        } else if (progress) {
            commentLayoutMain?.visibility = View.VISIBLE

            generalLayoutMain?.visibility = View.VISIBLE
            generalRecyclerView?.visibility = View.GONE

            relativeprogressBar?.visibility = View.VISIBLE

            noDatafoundRelativelayout?.visibility = View.GONE
            nodata1?.visibility = View.VISIBLE
            nodata1?.text = resources.getString(R.string.no_comment_list)
            nodatafoundtextview?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.GONE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview1?.visibility = View.VISIBLE
            nointernettextview?.visibility = View.VISIBLE
        } else if (datafound) {
            commentLayoutMain?.visibility = View.VISIBLE

            generalLayoutMain?.visibility = View.VISIBLE
            generalRecyclerView?.visibility = View.VISIBLE

            relativeprogressBar?.visibility = View.GONE

            noDatafoundRelativelayout?.visibility = View.GONE
            nodata1?.visibility = View.VISIBLE
            nodata1?.text = resources.getString(R.string.no_comment_list)
            nodatafoundtextview?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.GONE
            nointernetImageview?.visibility = View.VISIBLE
            nointernettextview1?.visibility = View.VISIBLE
            nointernettextview?.visibility = View.VISIBLE
        } else if (noInternet) {
            commentLayoutMain?.visibility = View.VISIBLE

            generalLayoutMain?.visibility = View.VISIBLE
            generalRecyclerView?.visibility = View.GONE

            relativeprogressBar?.visibility = View.GONE

            noDatafoundRelativelayout?.visibility = View.GONE
            nodata1?.visibility = View.VISIBLE
            nodata1?.text = resources.getString(R.string.no_comment_list)
            nodatafoundtextview?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.VISIBLE
            nointernetImageview?.visibility = View.VISIBLE
            nointernetImageview?.setImageResource(R.drawable.no_internet_connection)
            nointernettextview1?.visibility = View.VISIBLE
            nointernettextview1?.text = resources.getString(R.string.internetmsg1)
            nointernettextview?.visibility = View.VISIBLE
            nointernettextview?.text = resources.getString(R.string.error_common_network)
        } else if (somthingROng) {
            commentLayoutMain?.visibility = View.VISIBLE

            generalLayoutMain?.visibility = View.VISIBLE
            generalRecyclerView?.visibility = View.GONE

            relativeprogressBar?.visibility = View.GONE

            noDatafoundRelativelayout?.visibility = View.GONE
            nodata1?.visibility = View.VISIBLE
            nodata1?.text = resources.getString(R.string.no_comment_list)
            nodatafoundtextview?.visibility = View.GONE

            nointernetMainRelativelayout?.visibility = View.VISIBLE
            nointernetImageview?.visibility = View.VISIBLE
            nointernetImageview?.setImageResource(R.drawable.something_went_wrong)
            nointernettextview1?.visibility = View.VISIBLE
            nointernettextview1?.text = resources.getString(R.string.error_crash_error_message)
            nointernettextview?.visibility = View.GONE
        }
    }

    fun sendBroadCast(from: Int) {

        /**
         * 0 for add comment
         * 1 for delete comment
         * 2 for report comment
         * */

        val intent1 = Intent("Comment")
        intent1.putExtra("Successfully", "CommentSuccess")
        intent1.putExtra("CommentAction", from)
        intent1.putExtra("postId", postID)
        LocalBroadcastManager.getInstance(mActivity!!)
            .sendBroadcast(intent1)
    }

    fun showKeyboard() {
        val imm =
            mActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.SHOW_IMPLICIT
        )

    }


    fun setReply(isCommentReply: Boolean, replyName: String) {
        if (isCommentReply) {
            ll_comment_reply.visibility = View.VISIBLE
            tvCommentReplyName.text = "Reply to ${replyName} Comment"

            editComment.hint = "Reply to comment..."
        } else {
            ll_comment_reply.visibility = View.GONE
            editComment.hint = GetDynamicStringDictionaryObjectClass.Write_a_Comments


        }
    }


    @Throws(JSONException::class)
    private fun editComment(
        replyCommentId: String,
        replyCommentPos: Int,
        mPosition: Int
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("loginuserID", userData?.userID)
        jsonObject.put("languageID", userData?.languageID)
        jsonObject.put("postID", postID)
        jsonObject.put("commentID", commentID)
        jsonObject.put("commentreplyID", replyCommentId)
        jsonObject.put("commentreplyReply", editComment.text.toString().trim())
        jsonObject.put("apiType",RestClient.apiType)
        jsonObject.put("apiVersion",RestClient.apiVersion)
        val jsonArray = JSONArray()
        jsonArray.put(jsonObject)
        Log.d("System out", "==>$jsonArray")
        MyUtils.showProgress(mActivity!!)


        val apiCall = ViewModelProviders.of(mActivity!!).get(ReplyCommentModel::class.java)
        apiCall?.apiFunction(mActivity!!, true, jsonArray.toString(), 2)
            ?.observe(this@CommentListFragment,
                Observer
                { response ->

                    if (!response.isNullOrEmpty()) {
                        editComment.setText("")
                        setReply(false, "")
                        if (response[0].status.equals("true",false)) {

                            arrayComment?.get(mPosition)!!.replyCommentData[replyCommentPos].commentreplyReply=(response[0].data[0].commentreplyReply)
                            commentListAdapter?.notifyItemChanged(mPosition)

                            if (response[0].message.isNullOrEmpty()) {

                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    msgSuccessCommentDelete
                                )
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    response[0].message
                                )
                            }

                        } else {
                            //data not find
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                if (response[0].message.isNullOrEmpty()) {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        msgFailCommentDelete
                                    )
                                } else {
                                    MyUtils.showSnackbarkotlin(
                                        mActivity!!,
                                        commentLayoutMain!!,
                                        response[0].message
                                    )
                                }
                            } else {
                                MyUtils.showSnackbarkotlin(
                                    mActivity!!,
                                    commentLayoutMain!!,
                                    msgSomthingRong
                                )
                            }
                        }
                    } else {
                        //No internet and somthing rong
                        if (MyUtils.isInternetAvailable(mActivity!!)) {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgNoInternet
                            )
                        } else {
                            MyUtils.showSnackbarkotlin(
                                mActivity!!,
                                commentLayoutMain!!,
                                msgSomthingRong
                            )
                        }
                    }
                })
    }


}
