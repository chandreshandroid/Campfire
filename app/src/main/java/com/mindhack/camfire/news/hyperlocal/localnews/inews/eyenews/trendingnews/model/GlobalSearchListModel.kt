package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GlobalSearchPojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestCallback
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Response

class GlobalSearchListModel: ViewModel(){
    
    lateinit var getSpecialityData: LiveData<List<GlobalSearchPojo>>
    lateinit var mContext: Context

    var json: String = ""

    fun getSearchData(context: Context, json: String): LiveData<List<GlobalSearchPojo>> {

        this.json = json
        this.mContext = context


        getSpecialityData = getApiResponse()

        return getSpecialityData
    }

    private fun getApiResponse(): LiveData<List<GlobalSearchPojo>> {
        val data = MutableLiveData<List<GlobalSearchPojo>>()


        var call = RestClient.get()!!.getGlobalSearch(json)
        Log.w("Logs_map", "Search Data : " + call!!.request())

        call.enqueue(object : RestCallback<List<GlobalSearchPojo>?>(mContext) {
            override fun Success(response: Response<List<GlobalSearchPojo>?>) {
                data.value = response.body()
            }

            override fun failure() {
                Log.w("Logs_map", "Error Response : Null")

                data.value = null
            }

        })

        return data
    }

}