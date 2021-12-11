package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedDatum
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import retrofit2.Call
import retrofit2.Response

class GetPostListModel : ViewModel(){

    lateinit var getResponse: LiveData<List<TrendingFeedDatum>>
    lateinit var mContext: Context
    var isShowing: Boolean = false
    var json: String = ""
    var from:String=""
    fun apiFunction(context: Context, isShowing: Boolean,
                    json: String,from:String): LiveData<List<TrendingFeedDatum>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json
        this.from=from

        getResponse = apiREsponse()
        return getResponse
    }
    private fun apiREsponse(): LiveData<List<TrendingFeedDatum>> {
        if (isShowing) {
            MyUtils.showProgressDialog(this!!.mContext!!)
        }
        val data = MutableLiveData<List<TrendingFeedDatum>>()
        var call: Call<List<TrendingFeedDatum>>? = null
         when(from)
         {
            "postList"->{
                call = RestClient.get()!!.getPostList(json)
            }
             "getmyposts"->{
                call = RestClient.get()!!.getMyPostList(json)
            }
             "getmetaggedposts"->{
                call = RestClient.get()!!.getTaggedPostList(json)
            }
             "getmefavoriteposts"->{
                call = RestClient.get()!!.getFavoritePostList(json)
            }
             "getmycommentedposts"->{
                call = RestClient.get()!!.getCommentedPostList(json)
            }
         }


        call?.enqueue(object : retrofit2.Callback<List<TrendingFeedDatum>> {
            override fun onResponse(call: Call<List<TrendingFeedDatum>>, response: Response<List<TrendingFeedDatum>>) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = response.body()
            }
            override fun onFailure(call: Call<List<TrendingFeedDatum>>, t: Throwable) {
                if (isShowing)
                    MyUtils.closeProgress()
                data.value = null
            }
        })
        return data
    }

}