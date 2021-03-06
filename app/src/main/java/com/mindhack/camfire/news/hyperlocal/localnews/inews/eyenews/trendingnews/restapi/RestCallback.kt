package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi

import android.content.Context

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Chandresh on 25/12/2018.
 */

abstract class RestCallback<T>(internal var mContext: Context?) : Callback<T> {


    //success response
    abstract fun Success(response: Response<T>)

    //error
    abstract fun failure()

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.body() != null && response.body() is ArrayList<*> && (response.body() as ArrayList<*>).size > 0) {


            Success(response)


        } else {

            failure()

        }

    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        t.printStackTrace()
        failure()


    }
}
