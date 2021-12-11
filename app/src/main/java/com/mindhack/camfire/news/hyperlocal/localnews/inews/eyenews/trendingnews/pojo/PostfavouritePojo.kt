package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PostfavouritePojo (
    @SerializedName("status")
    @Expose
    var status: String? = "",
    @SerializedName("message")
    @Expose
    var message: String? = "",
    var feedDatum: TrendingFeedData? = null

)