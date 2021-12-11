package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.CMSpagePojo
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi.RestClient
import retrofit2.Call
import retrofit2.Response

class CMSpageModel : ViewModel() {

    lateinit var cmspageresponse: LiveData<List<CMSpagePojo>>
    lateinit var mContext: Context
    var isShowing: Boolean = false
    var json: String = ""

    fun getCMSPage(context: Context,isShowing: Boolean,
        json: String): LiveData<List<CMSpagePojo>> {

        this.mContext = context
        this.isShowing = isShowing
        this.json = json
        cmspageresponse = getCMSPageFun()

        return cmspageresponse
    }

    private fun getCMSPageFun(): LiveData<List<CMSpagePojo>> {
//        if (isShowing)
//            MyUtils.showProgressDialog(mContext, "Uploading")

        val data = MutableLiveData<List<CMSpagePojo>>()
        val call = RestClient.get()!!.cmsPage(json)

        call.enqueue(object : retrofit2.Callback<List<CMSpagePojo>> {
            override fun onFailure(call: Call<List<CMSpagePojo>>, t: Throwable) {
//                if (isShowing)
//                    MyUtils.dismissProgressDialog()

                data.value = null
            }
            override fun onResponse(call: Call<List<CMSpagePojo>>, response: Response<List<CMSpagePojo>>) {
//                if (isShowing)
//                    MyUtils.dismissProgressDialog()

                data.value = response.body()

            }
        })
        return data
    }

}