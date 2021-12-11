package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo
import com.google.gson.annotations.SerializedName


data class FollowPojo(
    @SerializedName("data")
    var `data`: List<FollowData>?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: String?
)

data class FollowData(
    @SerializedName("IsYouFollowing")
    var isYouFollowing: String?,
    @SerializedName("IsYouSentFollowingRequest")
    var isYouSentFollowingRequest: String?,
    @SerializedName("IsYourFollowingRequestRejected")
    var isYourFollowingRequestRejected: String?,
    @SerializedName("userFirstName")
    var userFirstName: String?,
    @SerializedName("userID")
    var userID: String?,
    @SerializedName("userLastName")
    var userLastName: String?,
    @SerializedName("userMentionID")
    var userMentionID: String?,
    @SerializedName("userProfilePicture")
    var userProfilePicture: String?
)