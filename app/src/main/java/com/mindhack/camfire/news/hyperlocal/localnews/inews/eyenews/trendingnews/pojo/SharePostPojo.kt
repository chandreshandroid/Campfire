package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SharePostPojo (
    @SerializedName("data")
    @Expose
    var data: List<TrendingFeedDatum>? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("message")
    @Expose
    var message: String? = null

)