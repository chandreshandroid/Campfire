package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by ADMIN on 21/12/2017.
 */


data class TrendingFeedDatum(
    @SerializedName("data")
    val `data`: List<TrendingFeedData>,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("totalpost")
    val totalpost: String = "",
    @SerializedName("status")
    val status: String = ""
) : Serializable

data class TrendingFeedData(
    @SerializedName("celebrityVerified")
    val celebrityVerified: String = "",
    @SerializedName("IsYouFollowing")
    var isYouFollowing: String = "",
    @SerializedName("IsYouSentFollowingRequest")
    val isYouSentFollowingRequest: String = "",
    @SerializedName("IsYouViewPost")
    var isYouViewPost: String = "",
    @SerializedName("IsYourFollowingRequestRejected")
    val isYourFollowingRequestRejected: String = "",
    @SerializedName("languageID")
    val languageID: String = "",
    @SerializedName("originalPostID")
    val originalPostID: String = "",
    @SerializedName("originaluserID")
    val originaluserID: String = "",
    @SerializedName("postAll")
    var postAll: String = "",
    @SerializedName("postAngry")
    var postAngry: String = "",
    @SerializedName("postClap")
    var postClap: String = "",
    @SerializedName("postComment")
    var postComment: String = "",
    @SerializedName("postCopyLink")
    val postCopyLink: String = "",
    @SerializedName("postCreateType")
    val postCreateType: String = "",
    @SerializedName("postCreatedDate")
    val postCreatedDate: String = "",
    @SerializedName("postDateTime")
    val postDateTime: String = "",
    @SerializedName("postDescription")
    val postDescription: String = "",
    @SerializedName("postFavorite")
    val postFavorite: String = "",
    @SerializedName("postHaha")
    var postHaha: String = "",
    @SerializedName("postHashText")
    val postHashText: String = "",
    @SerializedName("postHeadline")
    val postHeadline: String = "",
    @SerializedName("postHide")
    val postHide: String = "",
    @SerializedName("postID")
    val postID: String = "",
    @SerializedName("postLatitude")
    val postLatitude: String = "",
    @SerializedName("postLike")
    var postLike: String = "0",
    @SerializedName("postLocation")
    val postLocation: String = "",
    @SerializedName("postLocationVerified")
    val postLocationVerified: String = "",
    @SerializedName("postLongitude")
    val postLongitude: String = "",
    @SerializedName("postMediaType")
    val postMediaType: String = "",
    @SerializedName("postPrivacyType")
    val postPrivacyType: String = "",
    @SerializedName("postReport")
    val postReport: String = "",
    @SerializedName("postRepost")
    val postRepost: String = "",
    @SerializedName("postSad")
    var postSad: String = "",
    @SerializedName("postSerializedData")
    val postSerializedData: List<PostSerializedData>,
    @SerializedName("postShared")
    var postShared: String = "",
    @SerializedName("postStatus")
    var postStatus: String = "",
    @SerializedName("postViews")
    var postViews: String = "",
    @SerializedName("posttag")
    val posttag: List<Posttag>,
    @SerializedName("userEmail")
    val userEmail: String = "",
    @SerializedName("userFirstName")
    var userFirstName: String = "",
    @SerializedName("userID")
    val userID: String = "",
    @SerializedName("userLastName")
    val userLastName: String = "",
    @SerializedName("userMentionID")
    val userMentionID: String = "",
    @SerializedName("userProfilePicture")
    val userProfilePicture: String = "",
    @SerializedName("IsLiekedByYou")
    @Expose
    var IsLiekedByYou: String? = "",
    @SerializedName("IsYouReported")
    @Expose
    val IsYouReported: String? = null,
    @SerializedName("IsYourFavorite")
    @Expose
    var IsYourFavorite: String? = null,
    @SerializedName("youpostShared")
    @Expose
    var youpostShared: String? = null,
    @SerializedName("IsClappeddByYou")
    @Expose
    var IsClappeddByYou: String? = null,
    @SerializedName("LikeReaction")
    @Expose
    var LikeReaction: String? = null,
    @SerializedName("userClapCount")
    @Expose
    var userClapCount: String? = null,
    @SerializedName("PostCreatedMinutesAgo")
    @Expose
    var postCreatedMinutesAgo: String? = "",

    @SerializedName("original_postLocation")
    @Expose
    var original_postLocation: String? = "",
    @SerializedName("original_userID")
    @Expose
    var original_userID: String? = "",
    @SerializedName("original_postLocationVerified")
    @Expose
    var original_postLocationVerified: String? = "",
    @SerializedName("original_postLatitude")
    @Expose
    var original_postLatitude: String? = "",
    @SerializedName("original_postLongitude")
    @Expose
    var original_postLongitude: String? = "",
    @SerializedName("original_postCreatedDate")
    @Expose
    var original_postCreatedDate: String? = "",
    @SerializedName("original_userFirstName")
    @Expose
    var original_userFirstName: String? = "",
    @SerializedName("original_userLastName")
    @Expose
    var original_userLastName: String? = "",
    @SerializedName("original_userMentionID")
    @Expose
    var original_userMentionID: String? = "",
    var isReadMore: Boolean = false,
    @SerializedName("LikedList")
    @Expose
    var LikedList: List<PostLikeListData>,

    @SerializedName("badgeID")
    var badgeID: String = "",
    @SerializedName("userTotalPoint")
    var userTotalPoint: String = "",
    @SerializedName("badgeName")
    var badgeName: String = "",
    @SerializedName("badgeIcon")
    var badgeIcon: String = "",
    @SerializedName("postPoint")
    var postPoint: String = "",
    @SerializedName("userSignatureVideo")
    var userSignatureVideo : String = "",
    @SerializedName("postSignatureVideo")
    var postSignatureVideo : String = ""

) : Serializable
/*, ClusterItem {

override fun getSnippet(): String {
    return postDescription
}
override fun getTitle(): kotlin.String {
    return postHeadline
}

override fun getPosition(): LatLng {
   return (LatLng(postLatitude.toDouble(),postLongitude.toDouble()))
}
}*/



data class PostSerializedData(
    @SerializedName("albumName")
    val albumName: String = "",
    @SerializedName("albumType")
    val albumType: String = "",
    @SerializedName("albummedia")
    val albummedia: List<Albummedia>
) : Serializable

data class Albummedia(
    @SerializedName("albummediaFile")
    val albummediaFile: String = "",
    @SerializedName("albummediaThumbnail")
    val albummediaThumbnail: String = "",

    var duration: Long = 0,
    var isEditPost: Boolean = false,
    var isPlaying: Boolean = false
) : Serializable

data class Posttag(
    @SerializedName("postID")
    val postID: String = "",
    @SerializedName("posttagID")
    val posttagID: String = "",
    @SerializedName("userFirstName")
    val userFirstName: String = "",
    @SerializedName("userID")
    val userID: String = "",
    @SerializedName("userLastName")
    val userLastName: String = "",
    @SerializedName("userMentionID")
    val userMentionID: String = ""
) : Serializable