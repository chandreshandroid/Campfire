package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import com.google.gson.annotations.Expose



class RequestRegisterPojo : Serializable {
    @SerializedName("apiType")
    @Expose
    var apiType: String? = ""
    @SerializedName("apiVersion")
    @Expose
    var apiVersion: String? = ""
    @SerializedName("languageID")
    @Expose
    var languageID: String? = ""
    @SerializedName("userFirstName")
    @Expose
    var userFirstName: String? = ""
    @SerializedName("userLastName")
    @Expose
    var userLastName: String? = ""
    @SerializedName("userEmail")
    @Expose
    var userEmail: String? = ""
    @SerializedName("userCountryCode")
    @Expose
    var userCountryCode: String? = ""
    @SerializedName("userOTP")
    @Expose
    var userOTP: String? = ""
    @SerializedName("userMobile")
    @Expose
    var userMobile: String? = ""
    @SerializedName("userPassword")
    @Expose
    var userPassword: String? = ""
    @SerializedName("userDeviceType")
    @Expose
    var userDeviceType: String? = ""
    @SerializedName("userProfilePicture")
    @Expose
    var userProfilePicture: String? = ""
    @SerializedName("userFBID")
    @Expose
    var userFBID: String? = ""
    @SerializedName("userGoogleID")
    @Expose
    var userGoogleID: String? = ""
    @SerializedName("userDeviceID")
    @Expose
    var userDeviceID: String? = ""
    @SerializedName("userTwiterID")
    @Expose
    var userTwiterID: String? = ""

    @SerializedName("userId")
    @Expose
    var userId: String? = ""
    @SerializedName("userMentionID")
    @Expose
    var userMentionID: String? = ""

    @SerializedName("userBio")
    @Expose
    var userBio: String? = ""

    @SerializedName("userDOB")
    @Expose
    var userDOB: String? = ""

    @SerializedName("userEmailVerified")
    @Expose
    var userEmailVerified: String? = ""
}