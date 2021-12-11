package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class CMSpagePojo : Serializable {

    @Expose
    @SerializedName("message")
    var message: String? = null
    @Expose
    @SerializedName("status")
    var status: String? = null
    @Expose
    @SerializedName("data")
    var data: List<Data>? = null

    inner class Data : Serializable {
        @Expose
        @SerializedName("cmspageContents")
        var cmspageContents: String? = null
            get() = if (field != null) field else ""
        @Expose
        @SerializedName("cmspageImage")
        var cmspageImage: String? = null
        @Expose
        @SerializedName("cmspageName")
        var cmspageName: String? = null
    }
}
