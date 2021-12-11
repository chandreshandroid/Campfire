package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.ReplyComment
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import retrofit2.Call
import retrofit2.Response

class ReplyCommentModel : ViewModel() {

    lateinit var userLogin: LiveData<List<ReplyComment>?>
    lateinit var mContext: Context
    var from: Int = -1
    var jsonArray: String = ""
    var progress: Boolean = false

    fun apiFunction(
        context: Context, progress1 : Boolean,jsonArray: String, apiName: Int
    ): LiveData<List<ReplyComment>?> {
        this.progress = progress1
        this.jsonArray = jsonArray
        this.mContext = context
        this.from = apiName
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<ReplyComment>?> {

        if (progress){
            MyUtils.showProgressDialog(mContext)
        }

        val data = MutableLiveData<List<ReplyComment>>()
        var call: Call<List<ReplyComment>>? = null
        /**
         *
         * 0 for comment list
         * 1 for comment add
         * 2 for comment edit
         * 3 for comment delete
         * */
        when(from){
            0 -> call = RestClient.get()!!.getPostCommentReplyList(jsonArray)
            1 -> call = RestClient.get()!!.getPostCommentReplyAdd(jsonArray)
            2 -> call = RestClient.get()!!.getPostCommentReplyEdit(jsonArray)
            3 -> call = RestClient.get()!!.getPostCommentReplyDelete(jsonArray)
        }
        if (call != null) {
            call.enqueue(object : RestCallback<List<ReplyComment>?>(mContext) {


                override fun Success(response: Response<List<ReplyComment>?>) {
                    if (progress){
                        MyUtils.closeProgress()
                    }
                    data.value = response.body()
                    Log.w("SagarSagar",""+ response.body()!!.size)
                    Log.w("SagarSagar",""+response.body().toString())

                }
                override fun failure() {
                    if (progress){
                        MyUtils.closeProgress()
                    }
                    data.value = null
                }
            })
        }
        return data
    }
}