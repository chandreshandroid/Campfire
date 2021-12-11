package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.PostfavouritePojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Call
import retrofit2.Response

class PostFavouriteModel : ViewModel() {

    lateinit var userLogin: LiveData<List<PostfavouritePojo>?>
    lateinit var mContext: Activity
    var from: Int = -1
    var jsonArray: String = ""
    var type=""

    fun apiCall(context: Activity, jsonArray: String,type:String): LiveData<List<PostfavouritePojo>?> {
        this.jsonArray = jsonArray
        this.mContext = context
        this.type=type
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<PostfavouritePojo>?> {
        val data = MutableLiveData<List<PostfavouritePojo>>()
        var call: Call<List<PostfavouritePojo>>? = null
        when(type)
        {
            "FavouritePost"->{
                call = RestClient.get()!!.getPostFavorite(jsonArray)
            }
            "UnFavouritePost"->{
                call = RestClient.get()!!.getPostUnFavorite(jsonArray)
            }

        }

        call?.enqueue(object : RestCallback<List<PostfavouritePojo>?>(mContext) {
            override fun Success(response: Response<List<PostfavouritePojo>?>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }
        })
        return data
    }
}