package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo
import com.google.gson.annotations.SerializedName


data class ReplyComment(
    @SerializedName("data")
    var `data`: List<ReplyCommentData>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: String
)

data class ReplyCommentData(
    @SerializedName("commentID")
    var commentID: String,
    @SerializedName("commentreplyDate")
    var commentreplyDate: String,
    @SerializedName("commentreplyFile")
    var commentreplyFile: String,
    @SerializedName("commentreplyID")
    var commentreplyID: String,
    @SerializedName("commentreplyMediaType")
    var commentreplyMediaType: String,
    @SerializedName("commentreplyReply")
    var commentreplyReply: String,
    @SerializedName("commentreplyStatus")
    var commentreplyStatus: String,
    @SerializedName("commentreplyType")
    var commentreplyType: String,
    @SerializedName("mintuesago")
    var mintuesago: String,
    @SerializedName("postID")
    var postID: String,
    @SerializedName("userFirstName")
    var userFirstName: String,
    @SerializedName("userID")
    var userID: String,
    @SerializedName("userLastName")
    var userLastName: String,
    @SerializedName("userProfilePicture")
    var userProfilePicture: String
)