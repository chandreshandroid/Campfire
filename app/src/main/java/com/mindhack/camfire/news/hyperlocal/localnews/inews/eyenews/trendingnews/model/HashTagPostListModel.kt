package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.TrendingFeedDatum
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util.MyUtils
import retrofit2.Call
import retrofit2.Response
import com.google.gson.Gson




class HashTagPostListModel : ViewModel() {

    lateinit var getResponse: LiveData<List<TrendingFeedDatum>>
    lateinit var mContext: Context
    var isShowing: Boolean = false
    var json: String = ""
    var from: String = ""
    fun apiFunction(
        context: Context, isShowing: Boolean,
        json: String, from: String
    ): LiveData<List<TrendingFeedDatum>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json
        this.from = from

        getResponse = apiREsponse()
        return getResponse
    }

    private fun apiREsponse(): LiveData<List<TrendingFeedDatum>> {
        if (isShowing) {
            MyUtils.showProgressDialog(this!!.mContext!!)
        }
        val data = MutableLiveData<List<TrendingFeedDatum>>()
        var call: Call<List<TrendingFeedDatum>>? = null
        when (from) {
            "hashTag" -> {
                call = RestClient.get()!!.getHashTagPostList(json)

            }
            "location" -> {
                call = RestClient.get()!!.getLocationPostList(json)

            }
        }

        Log.w("Logs_map", "Post Get Use by Location : " + call!!.request())

        call?.enqueue(object : retrofit2.Callback<List<TrendingFeedDatum>> {
            override fun onResponse(
                call: Call<List<TrendingFeedDatum>>,
                response: Response<List<TrendingFeedDatum>>
            ) {
                val gson = Gson()
                val successResponse = gson.toJson(response.body())
                Log.d("Logs_map", "successResponse: $successResponse")

                if (isShowing)
                    MyUtils.closeProgress()
                data.value = response.body()
                try {
                    Log.w("Logs_map", "djshds : " + data.value!!.size)
                } catch (e : Exception)
                {
                    Log.w("Logs_map", "djshds : " + e.message)

                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<List<TrendingFeedDatum>>, t: Throwable) {
                Log.w("Logs_map", "Error : " + t.message)

                if (isShowing)
                    MyUtils.closeProgress()
                data.value = null
            }
        })
        return data
    }

}