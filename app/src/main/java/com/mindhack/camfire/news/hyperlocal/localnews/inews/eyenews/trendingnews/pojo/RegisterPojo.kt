package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.gson.annotations.SerializedName
import java.io.Serializable


/*class RegisterPojo : Serializable {

    @SerializedName("data")
    @Expose
    val data: List<Data?>? = listOf()
    @SerializedName("message")
    @Expose
    val message: String? = ""
    @SerializedName("status")
    @Expose
    val status: String? = ""

    class Data : Serializable {
        @SerializedName("languageID")
        @Expose
        val languageID: String? = ""
        @SerializedName("userCountryCode")
        @Expose
        val userCountryCode: String? = ""
        @SerializedName("userCreatedDate")
        @Expose
        val userCreatedDate: String? = ""
        @SerializedName("userDeviceID")
        @Expose
        val userDeviceID: String? = ""
        @SerializedName("userDeviceType")
        @Expose
        val userDeviceType: String? = ""
        @SerializedName("userEmail")
        @Expose
        val userEmail: String? = ""
        @SerializedName("userEmailNotification")
        @Expose
        val userEmailNotification: String? = ""
        @SerializedName("userFBID")
        @Expose
        val userFBID: String? = ""
        @SerializedName("userFirstName")
        @Expose
        val userFirstName: String? = ""
        @SerializedName("userGoogleID")
        @Expose
        val userGoogleID: String? = ""
        @SerializedName("userID")
        @Expose
        val userID: String? = ""
        @SerializedName("userLastName")
        @Expose
        val userLastName: String? = ""
        @SerializedName("userMobile")
        @Expose
        val userMobile: String? = ""
        @SerializedName("userOTP")
        @Expose
        val userOTP: String? = ""
        @SerializedName("userPassword")
        @Expose
        val userPassword: String? = ""
        @SerializedName("userProfilePicture")
        @Expose
        val userProfilePicture: String? = ""
        @SerializedName("userPushNotification")
        @Expose
        val userPushNotification: String? = ""
        @SerializedName("userReferKey")
        @Expose
        val userReferKey: String? = ""
        @SerializedName("userReviewAvg")
        @Expose
        val userReviewAvg: String? = ""
        @SerializedName("userReviewCount")
        @Expose
        val userReviewCount: String? = ""
        @SerializedName("userStatus")
        @Expose
        val userStatus: String? = ""
        @SerializedName("userTwiterID")
        @Expose
        val userTwiterID: String? = ""
        @SerializedName("userVerified")
        @Expose
        val userVerified: String? = ""

        @SerializedName("userVerifiedMobile")
        @Expose
        val userVerifiedMobile: String? = ""

        @SerializedName("userEmailVerified")
        @Expose
        val userEmailVerified: String? = ""

        @SerializedName("userMentionID")
        @Expose
        val userMentionID: String? = ""
    }

}*/


 class RegisterPojo : Serializable{
    @SerializedName("data")
    var `data`: List<Data>? = listOf()
    @SerializedName("message")
    var message: String=""
    @SerializedName("status")
    var status: String=""

     class Data : Serializable{
         @SerializedName("celebrityVerified")
         var celebrityVerified: String = ""
         @SerializedName("languageID")
         var languageID: String = ""
         @SerializedName("MeTaggedPosts")
         var meTaggedPosts: List<MeTaggedPost>? = listOf()
         @SerializedName("MyCommentedPosts")
         var myCommentedPosts: List<MyCommentedPost>? = listOf()
         @SerializedName("MyFavoritePosts")
         var myFavoritePosts: List<MyFavoritePost>? = listOf()
         @SerializedName("MyPosts")
         var myPosts: List<MyPost>? = listOf()
         @SerializedName("userCountryCode")
         var userCountryCode: String = ""
         @SerializedName("userCreatedDate")
         var userCreatedDate: String = ""
         @SerializedName("userDeviceID")
         var userDeviceID: String = ""
         @SerializedName("userOTP")
         var userOTP: String = ""
         @SerializedName("userDeviceType")
         var userDeviceType: String = ""
         @SerializedName("userEmail")
         var userEmail: String = ""
         @SerializedName("userEmailNotification")
         var userEmailNotification: String = ""
         @SerializedName("userEmailVerified")
         var userEmailVerified: String = ""
         @SerializedName("userFBID")
         var userFBID:String=""
         @SerializedName("userFirstName")
         var userFirstName: String = ""
         @SerializedName("userGoogleID")
         var userGoogleID:String=""
         @SerializedName("userID")
         var userID: String = ""
         @SerializedName("userLastName")
         var userLastName: String = ""
         @SerializedName("userMentionID")
         var userMentionID: String = ""
         @SerializedName("userMobile")
         var userMobile: String = ""
         @SerializedName("userPassword")
         var userPassword: String = ""
         @SerializedName("userProfilePicture")
         var userProfilePicture: String = ""
         @SerializedName("userPushNotification")
         var userPushNotification: String = ""
         @SerializedName("userReferKey")
         var userReferKey: String = ""
         @SerializedName("userReviewAvg")
         var userReviewAvg: String = ""
         @SerializedName("userReviewCount")
         var userReviewCount: String = ""
         @SerializedName("userStatus")
         var userStatus: String = ""
         @SerializedName("userTotalFollower")
         var userTotalFollower: String = ""
         @SerializedName("userTotalFollowing")
         var userTotalFollowing: String = ""
         @SerializedName("userTotalPost")
         var userTotalPost: String = ""
         @SerializedName("userTwiterID")
         var userTwiterID:String=""
         @SerializedName("userVerified")
          var userVerified: String = ""
         @SerializedName("userClapCount")
         var userClapCount: String = ""
         @SerializedName("userClapByCount")
         var userClapByCount: String = ""
         @SerializedName("IsClappeddByYou")
         var IsClappeddByYou: String = ""
         @SerializedName("IsYouFollowing")
         var isYouFollowing: String = ""
         @SerializedName("isReportedByYou")
         var isReportedByYou: String = ""

         @SerializedName("badgeID")
         var badgeID: String = ""
         @SerializedName("userTotalPoint")
         var userTotalPoint: String = ""
         @SerializedName("badgeName")
         var badgeName: String = ""
         @SerializedName("badgeIcon")
         var badgeIcon: String = ""
         @SerializedName("userSignatureVideo")
         var userSignatureVideo: String = ""
         @SerializedName("userBio")
         var userBio: String = ""
         @SerializedName("userDOB")
         var userDOB: String = ""


     }

          class MeTaggedPost: Serializable{
             @SerializedName("celebrityVerified")
             var celebrityVerified: String=""
             @SerializedName("comments")
             var comments: List<Any>? = listOf()
             @SerializedName("IsLiekedByYou")
             var isLiekedByYou: String=""
             @SerializedName("IsYouFollowing")
             var isYouFollowing: String=""
             @SerializedName("IsYouReported")
             var isYouReported: String=""
             @SerializedName("IsYouSentFollowingRequest")
             var isYouSentFollowingRequest: String=""
             @SerializedName("IsYouViewPost")
             var isYouViewPost: String=""
             @SerializedName("IsYourFavorite")
             var isYourFavorite: String=""
             @SerializedName("IsYourFollowingRequestRejected")
             var isYourFollowingRequestRejected: String=""
             @SerializedName("languageID")
             var languageID: String=""
             @SerializedName("originalPostID")
             var originalPostID: String=""
             @SerializedName("originaluserID")
             var originaluserID: String=""
             @SerializedName("postAll")
             var postAll: String=""
             @SerializedName("postAngry")
             var postAngry: String=""
             @SerializedName("postClap")
             var postClap: String=""
             @SerializedName("postComment")
             var postComment: String=""
             @SerializedName("postCopyLink")
             var postCopyLink: String=""
             @SerializedName("postCreateType")
             var postCreateType: String=""
             @SerializedName("postCreatedDate")
             var postCreatedDate: String=""
             @SerializedName("postDateTime")
             var postDateTime: String=""
             @SerializedName("postDescription")
             var postDescription: String=""
             @SerializedName("postFavorite")
             var postFavorite: String=""
             @SerializedName("postHaha")
             var postHaha: String=""
             @SerializedName("postHashText")
             var postHashText: String=""
             @SerializedName("postHeadline")
             var postHeadline: String=""
             @SerializedName("postHide")
             var postHide: String=""
             @SerializedName("postID")
             var postID: String=""
             @SerializedName("postLatitude")
             var postLatitude: String=""
             @SerializedName("postLike")
             var postLike: String=""
             @SerializedName("postLocation")
             var postLocation: String=""
             @SerializedName("postLocationVerified")
             var postLocationVerified: String=""
             @SerializedName("postLongitude")
             var postLongitude: String=""
             @SerializedName("postMediaType")
             var postMediaType: String=""
             @SerializedName("postPrivacyType")
             var postPrivacyType: String=""
             @SerializedName("postReport")
             var postReport: String=""
             @SerializedName("postRepost")
             var postRepost: String=""
             @SerializedName("postSad")
             var postSad: String=""
             @SerializedName("postSerializedData")
             var postSerializedData: List<PostSerializedData>? = listOf()
             @SerializedName("postShared")
             var postShared: String=""
             @SerializedName("postStatus")
             var postStatus: String=""
             @SerializedName("postViews")
             var postViews: String=""
             @SerializedName("posttag")
             var posttag: List<Posttag>? = listOf()
             @SerializedName("userEmail")
             var userEmail: String=""
             @SerializedName("userFirstName")
             var userFirstName: String=""
             @SerializedName("userID")
             var userID: String=""
             @SerializedName("userLastName")
             var userLastName: String=""
             @SerializedName("userMentionID")
             var userMentionID: String=""
             @SerializedName("userProfilePicture")
             var userProfilePicture: String=""
     }

          class PostSerializedData: Serializable{
             @SerializedName("albumName")
             var albumName: String=""
             @SerializedName("albumType")
             var albumType: String=""
             @SerializedName("albummedia")
             var albummedia: List<Albummedia>? = listOf()
     }

          class Albummedia: Serializable{
             @SerializedName("albummediaFile")
             var albummediaFile: String=""
             @SerializedName("albummediaFileType")
             var albummediaFileType: String=""
             @SerializedName("albummediaThumbnail")
             var albummediaThumbnail: String=""
     }

          class Posttag: Serializable{
             @SerializedName("postID")
             var postID: String=""
             @SerializedName("posttagID")
             var posttagID: String=""
             @SerializedName("userFirstName")
             var userFirstName: String=""
             @SerializedName("userID")
             var userID: String=""
             @SerializedName("userLastName")
             var userLastName: String=""
             @SerializedName("userMentionID")
             var userMentionID: String=""
     }

          class MyCommentedPost: Serializable{
             @SerializedName("celebrityVerified")
             var celebrityVerified: String=""
             @SerializedName("comments")
             var comments: List<Comment>? = listOf()
             @SerializedName("IsLiekedByYou")
             var isLiekedByYou: String=""
             @SerializedName("IsYouFollowing")
             var isYouFollowing: String=""
             @SerializedName("IsYouReported")
             var isYouReported: String=""
             @SerializedName("IsYouSentFollowingRequest")
             var isYouSentFollowingRequest: String=""
             @SerializedName("IsYouViewPost")
             var isYouViewPost: String=""
             @SerializedName("IsYourFavorite")
             var isYourFavorite: String=""
             @SerializedName("IsYourFollowingRequestRejected")
             var isYourFollowingRequestRejected: String=""
             @SerializedName("languageID")
             var languageID: String=""
             @SerializedName("originalPostID")
             var originalPostID: String=""
             @SerializedName("originaluserID")
             var originaluserID: String=""
             @SerializedName("postAll")
             var postAll: String=""
             @SerializedName("postAngry")
             var postAngry: String=""
             @SerializedName("postClap")
             var postClap: String=""
             @SerializedName("postComment")
             var postComment: String=""
             @SerializedName("postCopyLink")
             var postCopyLink: String=""
             @SerializedName("postCreateType")
             var postCreateType: String=""
             @SerializedName("postCreatedDate")
             var postCreatedDate: String=""
             @SerializedName("postDateTime")
             var postDateTime: String=""
             @SerializedName("postDescription")
             var postDescription: String=""
             @SerializedName("postFavorite")
             var postFavorite: String=""
             @SerializedName("postHaha")
             var postHaha: String=""
             @SerializedName("postHashText")
             var postHashText: String=""
             @SerializedName("postHeadline")
             var postHeadline: String=""
             @SerializedName("postHide")
             var postHide: String=""
             @SerializedName("postID")
             var postID: String=""
             @SerializedName("postLatitude")
             var postLatitude: String=""
             @SerializedName("postLike")
             var postLike: String=""
             @SerializedName("postLocation")
             var postLocation: String=""
             @SerializedName("postLocationVerified")
             var postLocationVerified: String=""
             @SerializedName("postLongitude")
             var postLongitude: String=""
             @SerializedName("postMediaType")
             var postMediaType: String=""
             @SerializedName("postPrivacyType")
             var postPrivacyType: String=""
             @SerializedName("postReport")
             var postReport: String=""
             @SerializedName("postRepost")
             var postRepost: String=""
             @SerializedName("postSad")
             var postSad: String=""
             @SerializedName("postSerializedData")
             var postSerializedData: List<PostSerializedDataX>? = listOf()
             @SerializedName("postShared")
             var postShared: String=""
             @SerializedName("postStatus")
             var postStatus: String=""
             @SerializedName("postViews")
             var postViews: String=""
             @SerializedName("posttag")
             var posttag: List<PosttagX>? = listOf()
             @SerializedName("userEmail")
             var userEmail: String=""
             @SerializedName("userFirstName")
             var userFirstName: String=""
             @SerializedName("userID")
             var userID: String=""
             @SerializedName("userLastName")
             var userLastName: String=""
             @SerializedName("userMentionID")
             var userMentionID: String=""
             @SerializedName("userProfilePicture")
             var userProfilePicture: String=""
          }

          class Comment: Serializable{
             @SerializedName("commentComment")
             var commentComment: String=""
             @SerializedName("commentDate")
             var commentDate: String=""
             @SerializedName("commentFile")
             var commentFile: String=""
             @SerializedName("commentID")
             var commentID: String=""
             @SerializedName("commentMediaType")
             var commentMediaType: String=""
             @SerializedName("commentStatus")
             var commentStatus: String=""
             @SerializedName("commentType")
             var commentType: String=""
             @SerializedName("mintuesago")
             var mintuesago: String=""
             @SerializedName("postID")
             var postID: String=""
             @SerializedName("posterID")
             var posterID: String=""
             @SerializedName("userFirstName")
             var userFirstName: String=""
             @SerializedName("userID")
             var userID: String=""
             @SerializedName("userLastName")
             var userLastName: String=""
             @SerializedName("userProfilePicture")
             var userProfilePicture: String=""
     }

          class PostSerializedDataX: Serializable{
             @SerializedName("albumName")
             var albumName: String=""
             @SerializedName("albumType")
             var albumType: String=""
             @SerializedName("albummedia")
             var albummedia: List<AlbummediaX>? = listOf()
            }

          class AlbummediaX: Serializable{
             @SerializedName("albummediaFile")
             var albummediaFile: String=""
             @SerializedName("albummediaFileType")
             var albummediaFileType: String=""
             @SerializedName("albummediaThumbnail")
             var albummediaThumbnail: String=""
          }

          class PosttagX: Serializable{
             @SerializedName("postID")
             var postID: String=""
             @SerializedName("posttagID")
             var posttagID: String=""
             @SerializedName("userFirstName")
             var userFirstName: String=""
             @SerializedName("userID")
             var userID: String=""
             @SerializedName("userLastName")
             var userLastName: String=""
             @SerializedName("userMentionID")
             var userMentionID: String=""
     }

          class MyFavoritePost: Serializable{
             @SerializedName("celebrityVerified")
             var celebrityVerified: String=""
             @SerializedName("comments")
             var comments: List<Any>? = listOf()
             @SerializedName("IsLiekedByYou")
             var isLiekedByYou: String=""
             @SerializedName("IsYouFollowing")
             var isYouFollowing: String=""
             @SerializedName("IsYouReported")
             var isYouReported: String=""
             @SerializedName("IsYouSentFollowingRequest")
             var isYouSentFollowingRequest: String=""
             @SerializedName("IsYouViewPost")
             var isYouViewPost: String=""
             @SerializedName("IsYourFavorite")
             var isYourFavorite: String=""
             @SerializedName("IsYourFollowingRequestRejected")
             var isYourFollowingRequestRejected: String=""
             @SerializedName("languageID")
             var languageID: String=""
             @SerializedName("originalPostID")
             var originalPostID: String=""
             @SerializedName("originaluserID")
             var originaluserID: String=""
             @SerializedName("postAll")
             var postAll: String=""
             @SerializedName("postAngry")
             var postAngry: String=""
             @SerializedName("postClap")
             var postClap: String=""
             @SerializedName("postComment")
             var postComment: String=""
             @SerializedName("postCopyLink")
             var postCopyLink: String=""
             @SerializedName("postCreateType")
             var postCreateType: String=""
             @SerializedName("postCreatedDate")
             var postCreatedDate: String=""
             @SerializedName("postDateTime")
             var postDateTime: String=""
             @SerializedName("postDescription")
             var postDescription: String=""
             @SerializedName("postFavorite")
             var postFavorite: String=""
             @SerializedName("postHaha")
             var postHaha: String=""
             @SerializedName("postHashText")
             var postHashText: String=""
             @SerializedName("postHeadline")
             var postHeadline: String=""
             @SerializedName("postHide")
             var postHide: String=""
             @SerializedName("postID")
             var postID: String=""
             @SerializedName("postLatitude")
             var postLatitude: String=""
             @SerializedName("postLike")
             var postLike: String=""
             @SerializedName("postLocation")
             var postLocation: String=""
             @SerializedName("postLocationVerified")
             var postLocationVerified: String=""
             @SerializedName("postLongitude")
             var postLongitude: String=""
             @SerializedName("postMediaType")
             var postMediaType: String=""
             @SerializedName("postPrivacyType")
             var postPrivacyType: String=""
             @SerializedName("postReport")
             var postReport: String=""
             @SerializedName("postRepost")
             var postRepost: String=""
             @SerializedName("postSad")
             var postSad: String=""
             @SerializedName("postSerializedData")
             var postSerializedData: List<PostSerializedDataXX>? = listOf()
             @SerializedName("postShared")
             var postShared: String=""
             @SerializedName("postStatus")
             var postStatus: String=""
             @SerializedName("postViews")
             var postViews: String=""
             @SerializedName("posttag")
             var posttag: List<PosttagXX>? = listOf()
             @SerializedName("userEmail")
             var userEmail: String=""
             @SerializedName("userFirstName")
             var userFirstName: String=""
             @SerializedName("userID")
             var userID: String=""
             @SerializedName("userLastName")
             var userLastName: String=""
             @SerializedName("userMentionID")
             var userMentionID: String=""
             @SerializedName("userProfilePicture")
             var userProfilePicture: String=""
     }

          class PostSerializedDataXX: Serializable{
             @SerializedName("albumName")
             var albumName: String=""
             @SerializedName("albumType")
             var albumType: String=""
             @SerializedName("albummedia")
             var albummedia: List<AlbummediaXX>? = listOf()
         }

          class AlbummediaXX: Serializable{
             @SerializedName("albummediaFile")
             var albummediaFile: String=""
             @SerializedName("albummediaFileType")
             var albummediaFileType: String=""
             @SerializedName("albummediaThumbnail")
             var albummediaThumbnail: String=""
          }

          class PosttagXX: Serializable{
             @SerializedName("postID")
             var postID: String=""
             @SerializedName("posttagID")
             var posttagID: String=""
             @SerializedName("userFirstName")
             var userFirstName: String=""
             @SerializedName("userID")
             var userID: String=""
             @SerializedName("userLastName")
             var userLastName: String=""
             @SerializedName("userMentionID")
             var userMentionID: String=""
     }

          class MyPost: Serializable{
             @SerializedName("celebrityVerified")
             var celebrityVerified: String=""
             @SerializedName("comments")
             var comments: List<Any>? = listOf()
             @SerializedName("IsLiekedByYou")
             var isLiekedByYou: String=""
             @SerializedName("IsYouFollowing")
             var isYouFollowing: String=""
             @SerializedName("IsYouReported")
             var isYouReported: String=""
             @SerializedName("IsYouSentFollowingRequest")
             var isYouSentFollowingRequest: String=""
             @SerializedName("IsYouViewPost")
             var isYouViewPost: String=""
             @SerializedName("IsYourFavorite")
             var isYourFavorite: String=""
             @SerializedName("IsYourFollowingRequestRejected")
             var isYourFollowingRequestRejected: String=""
             @SerializedName("languageID")
             var languageID: String=""
             @SerializedName("originalPostID")
             var originalPostID: String=""
             @SerializedName("originaluserID")
             var originaluserID: String=""
             @SerializedName("postAll")
             var postAll: String=""
             @SerializedName("postAngry")
             var postAngry: String=""
             @SerializedName("postClap")
             var postClap: String=""
             @SerializedName("postComment")
             var postComment: String=""
             @SerializedName("postCopyLink")
             var postCopyLink: String=""
             @SerializedName("postCreateType")
             var postCreateType: String=""
             @SerializedName("postCreatedDate")
             var postCreatedDate: String=""
             @SerializedName("postDateTime")
             var postDateTime: String=""
             @SerializedName("postDescription")
             var postDescription: String=""
             @SerializedName("postFavorite")
             var postFavorite: String=""
             @SerializedName("postHaha")
             var postHaha: String=""
             @SerializedName("postHashText")
             var postHashText: String=""
             @SerializedName("postHeadline")
             var postHeadline: String=""
             @SerializedName("postHide")
             var postHide: String=""
             @SerializedName("postID")
             var postID: String=""
             @SerializedName("postLatitude")
             var postLatitude: String=""
             @SerializedName("postLike")
             var postLike: String=""
             @SerializedName("postLocation")
             var postLocation: String=""
             @SerializedName("postLocationVerified")
             var postLocationVerified: String=""
             @SerializedName("postLongitude")
             var postLongitude: String=""
             @SerializedName("postMediaType")
             var postMediaType: String=""
             @SerializedName("postPrivacyType")
             var postPrivacyType: String=""
             @SerializedName("postReport")
             var postReport: String=""
             @SerializedName("postRepost")
             var postRepost: String=""
             @SerializedName("postSad")
             var postSad: String=""
             @SerializedName("postSerializedData")
             var postSerializedData: List<PostSerializedDataXXX>? = listOf()
             @SerializedName("postShared")
             var postShared: String=""
             @SerializedName("postStatus")
             var postStatus: String=""
             @SerializedName("postViews")
             var postViews: String=""
             @SerializedName("posttag")
             var posttag: List<PosttagXXX>? = listOf()
             @SerializedName("userEmail")
             var userEmail: String=""
             @SerializedName("userFirstName")
             var userFirstName: String=""
             @SerializedName("userID")
             var userID: String=""
             @SerializedName("userLastName")
             var userLastName: String=""
             @SerializedName("userMentionID")
             var userMentionID: String=""
             @SerializedName("userProfilePicture")
             var userProfilePicture: String=""
     }

     class PostSerializedDataXXX: Serializable{
         @SerializedName("albumName")
         var albumName: String=""
         @SerializedName("albumType")
         var albumType: String=""
         @SerializedName("albummedia")
         var albummedia: List<AlbummediaXXX>? = listOf()
     }

     class AlbummediaXXX: Serializable{
         @SerializedName("albummediaFile")
         var albummediaFile: String=""
         @SerializedName("albummediaFileType")
         var albummediaFileType: String=""
         @SerializedName("albummediaThumbnail")
         var albummediaThumbnail: String=""
     }

     class PosttagXXX: Serializable{
         @SerializedName("postID")
         var postID: String=""
         @SerializedName("posttagID")
         var posttagID: String=""
         @SerializedName("userFirstName")
         var userFirstName: String=""
         @SerializedName("userID")
         var userID: String=""
         @SerializedName("userLastName")
         var userLastName: String=""
         @SerializedName("userMentionID")
         var userMentionID: String=""
     }
 }

