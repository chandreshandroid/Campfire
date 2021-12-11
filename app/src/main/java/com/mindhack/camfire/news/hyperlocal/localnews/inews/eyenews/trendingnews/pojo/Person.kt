package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.io.Serializable

/**
 * Created by Peter-John on 2017-04-11.
 * GoogleMapsTutorial
 */
class Person(
    lat: Double,
    lng: Double,
    var name: String,
    private val twitterHandle: String,
    var trendingFeedData: TrendingFeedData

) : ClusterItem,Serializable {


    private val mPosition: LatLng

    override fun getPosition(): LatLng {
        return mPosition
    }

    fun getTrendindData():TrendingFeedData
    {
        return trendingFeedData
    }



    override fun getTitle(): String {
        return name
    }

    override fun getSnippet(): String {
        return twitterHandle
    }

    init {
        mPosition = LatLng(lat, lng)
    }
}