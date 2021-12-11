package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PostLikeListPojo(
    @SerializedName("count_array")
    var countArray: List<CountArray>,
    @SerializedName("data")
    var `data`: List<PostLikeListData>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: String
):Serializable

data class CountArray(
    @SerializedName("postAll")
    var postAll: String,
    @SerializedName("postAngry")
    var postAngry: String,
    @SerializedName("postHaha")
    var postHaha: String,
    @SerializedName("postLike")
    var postLike: String,
    @SerializedName("postSad")
    var postSad: String
):Serializable

data class PostLikeListData(
    @SerializedName("IsYouFollowing")
    var isYouFollowing: String,
    @SerializedName("postID")
    var postID: String,
    @SerializedName("posterID")
    var posterID: String,
    @SerializedName("postlikeType")
    var postlikeType: String,
    @SerializedName("userEmail")
    var userEmail: String,
    @SerializedName("userFirstName")
    var userFirstName: String,
    @SerializedName("userID")
    var userID: String,
    @SerializedName("userLastName")
    var userLastName: String,
    @SerializedName("userMobile")
    var userMobile: String,
    @SerializedName("userProfilePicture")
    var userProfilePicture: String
):Serializable
