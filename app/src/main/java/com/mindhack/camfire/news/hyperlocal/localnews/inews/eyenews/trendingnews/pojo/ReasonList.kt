package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReasonList(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) : Serializable{
    data class Data(
        var isSelected : Boolean = false,
        @SerializedName("reasonAppliesTo")
        val reasonAppliesTo: String,
        @SerializedName("reasonCreatedDate")
        val reasonCreatedDate: String,
        @SerializedName("reasonID")
        val reasonID: String,
        @SerializedName("reasonName")
        val reasonName: String,
        @SerializedName("reasonStatus")
        val reasonStatus: String
    ): Serializable
}