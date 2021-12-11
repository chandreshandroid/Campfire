package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class NotificationListPojo : Serializable {

    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null

    class Datum : Serializable{

        @SerializedName("notificationID")
        @Expose
        var notificationID: String? = null
        @SerializedName("notificationType")
        @Expose
        var notificationType: String? = null
        @SerializedName("notificationReferenceKey")
        @Expose
        var notificationReferenceKey: String? = null
        @SerializedName("notificationMessageText")
        @Expose
        var notificationMessageText: String? = null
        @SerializedName("notificationReadStatus")
        @Expose
        var notificationReadStatus: String? = null
        @SerializedName("notificationSendDate")
        @Expose
        var notificationSendDate: String? = null
            get() = if (field != null) field else ""
        @SerializedName("notificationSendTime")
        @Expose
        var notificationSendTime: String? = null
            get() = if (field != null) field else ""

        @SerializedName("notificationTitle")
        @Expose
        var notificationTitle: String? = null

        @SerializedName("userFirstName")
        @Expose
        var userFirstName: String? = null

        @SerializedName("userLastName")
        @Expose
        var userLastName: String? = null

        @SerializedName("userMentionID")
        @Expose
        var userMentionID: String? = null

        @SerializedName("userProfilePicture")
        @Expose
        var userProfilePicture: String? = null


    }
}
