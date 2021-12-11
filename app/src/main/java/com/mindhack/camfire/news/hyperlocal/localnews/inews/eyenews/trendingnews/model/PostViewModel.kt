package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CommonPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Call
import retrofit2.Response

class PostViewModel : ViewModel() {

    lateinit var userLogin: LiveData<List<CommonPojo>?>
    lateinit var mContext: Activity
    var from: Int = -1
    var jsonArray: String = ""
    var type=""

    fun apiCall(context: Activity, jsonArray: String,type:String): LiveData<List<CommonPojo>?> {
        this.jsonArray = jsonArray
        this.mContext = context
        this.type=type
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<CommonPojo>?> {
        val data = MutableLiveData<List<CommonPojo>>()
        var call: Call<List<CommonPojo>>? = null
        when(type)
        {
            "ViewPost"->{
                call = RestClient.get()!!.getPostView(jsonArray)
            }
            "LikePost"->{
                call = RestClient.get()!!.getPostLike(jsonArray)
            }
            "LikePostCommnet"->{
                call = RestClient.get()!!.getPostCommentLike(jsonArray)
            }
            "RemoveLikePostCommnet"->{
                call = RestClient.get()!!.getPostCommentRemoveLike(jsonArray)
            }
            "UnLikePost"->{
                call = RestClient.get()!!.getPostUnLike(jsonArray)
            }
            "SharePost"->{
                call = RestClient.get()!!.getPostShare(jsonArray)
            }

        }

        call?.enqueue(object : RestCallback<List<CommonPojo>?>(mContext) {
            override fun Success(response: Response<List<CommonPojo>?>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }
        })
        return data
    }
}