package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.gson.annotations.SerializedName

data class CommonPojo(
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("status")
    val status: String? = ""
)