package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class GlobalSearchPojo(
    @SerializedName("Custom")
    var custom: ArrayList<TrendingFeedData?>?,
    @SerializedName("message")
    var message: String,
    @SerializedName("People")
    var people: List<People>,
    @SerializedName("Places")
    var places: List<Place>,
    @SerializedName("status")
    var status: String,
    @SerializedName("Tags")
    var tags: List<Tag>
):Serializable

data class Tag(
    @SerializedName("postHashText")
    var postHashText: String,
    @SerializedName("postID")
    var postID: String,
    @SerializedName("postLatitude")
    var postLatitude: String,
    @SerializedName("postLongitude")
    var postLongitude: String,
    @SerializedName("postSerializedData")
    var postSerializedData: List<PostSerializedData>,
    @SerializedName("posthashID")
    var posthashID: String,
    @SerializedName("posthashText")
    var posthashText: String,
    @SerializedName("TotalPost")
    var totalPost: String
):Serializable

data class People(
    @SerializedName("IsClappeddByYou")
    var isClappeddByYou: String,
    @SerializedName("isReportedByYou")
    var isReportedByYou: String,
    @SerializedName("IsYouFollowing")
    var isYouFollowing: String,
    @SerializedName("userEmail")
    var userEmail: String,
    @SerializedName("userFirstName")
    var userFirstName: String,
    @SerializedName("userID")
    var userID: String,
    @SerializedName("userLastName")
    var userLastName: String,
    @SerializedName("userMentionID")
    var userMentionID: String,
    @SerializedName("userMobile")
    var userMobile: String,
    @SerializedName("userProfilePicture")
    var userProfilePicture: String
):Serializable
data class Place(
    @SerializedName("postDescription")
    var postDescription: String,
    @SerializedName("postHeadline")
    var postHeadline: String,
    @SerializedName("postID")
    var postID: String,
    @SerializedName("postLatitude")
    var postLatitude: String,
    @SerializedName("postLocation")
    var postLocation: String,
    @SerializedName("postLongitude")
    var postLongitude: String,
    @SerializedName("postSerializedData")
    var postSerializedData: List<PostSerializedData>
):Serializable




