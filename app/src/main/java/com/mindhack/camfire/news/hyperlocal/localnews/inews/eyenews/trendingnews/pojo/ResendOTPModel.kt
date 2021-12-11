package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.gson.annotations.SerializedName


data class ResendOTPModelItem(

    @SerializedName("otp")
    val otp: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: String? = null
)
