package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo


import com.google.gson.annotations.SerializedName

data class CommentPojo(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("commentComment")
        val commentComment: String,
        @SerializedName("commentDate")
        val commentDate: String,
        @SerializedName("commentFile")
        val commentFile: String,
        @SerializedName("commentID")
        val commentID: String,
        @SerializedName("commentMediaType")
        val commentMediaType: String,
        @SerializedName("commentStatus")
        val commentStatus: String,
        @SerializedName("commentType")
        val commentType: String,
        @SerializedName("mintuesago")
        val mintuesago: String,
        @SerializedName("postID")
        val postID: String,
        @SerializedName("posterID")
        val posterID: String,
        @SerializedName("userFirstName")
        val userFirstName: String,
        @SerializedName("userID")
        val userID: String,
        @SerializedName("userLastName")
        val userLastName: String,
        @SerializedName("userProfilePicture")
        val userProfilePicture: String,
        @SerializedName("likeCount")
        val likeCount: String = "",
        @SerializedName("isYouLikedComment")
        var isYouLikedComment: String = "" ,
        @SerializedName("commentLike")
        var commentLike: String = "",

//        @SerializedName("commentReplylist")
        val replyCommentData: ArrayList<ReplyCommentData> =ArrayList(),
        var isVisibleComment:Boolean=false
    )



}