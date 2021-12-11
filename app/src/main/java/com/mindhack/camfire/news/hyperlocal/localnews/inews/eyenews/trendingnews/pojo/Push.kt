package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by Chandresh
 * FIL
 */

class Push : Serializable {
    companion object {
        val  action:String="Push"
        val bokingRequest:String="BookingRequest"
    }

    @SerializedName("RefKey")
    @Expose
    var refKey: String? = ""
    @SerializedName("priority")
    @Expose
    var priority: String? = ""
    @SerializedName("msgKey")
    @Expose
    var msgKey: String? = ""
    @SerializedName("msg")
    @Expose
    var msg: String? = ""
    @SerializedName("body")
    @Expose
    var body: String? = ""
    @SerializedName("type")
    @Expose
    var type: String? = ""
    @SerializedName("title")
    @Expose
    var title: String? = ""
    @SerializedName("msgType")
    @Expose
    var msgType: String? = ""

    @SerializedName("RefData")
    @Expose
    var `RefData`: String?=""


}
data class RefData (
    @SerializedName("msgType")
    @Expose
    var msgType: String? = ""
): Serializable
