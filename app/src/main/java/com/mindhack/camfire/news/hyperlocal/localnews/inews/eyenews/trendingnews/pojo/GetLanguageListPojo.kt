package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetLanguageListPojo : Serializable {

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null

    inner class Datum : Serializable {

        @SerializedName("languageID")
        @Expose
        var languageID: String? = null
        @SerializedName("languageName")
        @Expose
        var languageName: String? = null

        var isSelected : Boolean = false
    }

}
