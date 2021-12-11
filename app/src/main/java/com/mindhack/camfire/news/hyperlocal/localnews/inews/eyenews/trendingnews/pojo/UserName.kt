package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo
import com.google.gson.annotations.SerializedName


data class UserName(
    @SerializedName("data")
    val `data`: List<UserNameData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)

data class UserNameData(
    @SerializedName("userName")
    val userName: String
)