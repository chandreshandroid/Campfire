package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.gson.annotations.SerializedName

data class UserForgotPassword(
    @SerializedName("data")
    val `data`: List<Data?>? = listOf(),
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("status")
    val status: String? = ""
) {
    data class Data(
        @SerializedName("userID")
        val userID: String? = "",
        @SerializedName("userMobile")
        val userMobile: String? = ""
    )
}