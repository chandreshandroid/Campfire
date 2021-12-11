package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.gson.annotations.SerializedName

data class UploadFilePojo(
    @SerializedName("fileName")
    var fileName: String? = "",
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("status")
    val status: String? = "",
    var addImages:List<AddImages>?=null





)