package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo
import com.google.gson.annotations.SerializedName


data class MyViewPoints(
    @SerializedName("data")
    var `data`: List<TrendingFeedData>,
    @SerializedName("message")
    var message: String,
    @SerializedName("otherPoint")
    var otherPoint: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("userdata")
    var userdata: List<RegisterPojo.Data>
)
