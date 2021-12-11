package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.restapi




import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


public interface RestApi {

    @FormUrlEncoded
    @POST("users/user-registration")
    fun userRegister(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("users/otp-verification")
    fun otpVerification(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("users/otp-resend")
    fun otpResend(@Field("json") jsonLogin: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("users/otp-resend")
    fun otpResendNew(@Field("json") jsonLogin: String): Call<List<ResendOTPModelItem?>?>

    @FormUrlEncoded
    @POST("users/user-login-otp")
    fun loginWithOtp(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("users/user-login-password")
    fun loginWithPassword(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("users/user-update-profile-picture")
    fun updateProfilePicture(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("users/user-update-device-token")
    fun updateDeviceToken(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("users/user-update-settings")
    fun updateSettings(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("users/check-user-duplication")
    fun checkUserDuplication(@Field("json") jsonLogin: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("users/user-forgot-password")
    fun userForgotPassword(@Field("json") jsonLogin: String): Call<List<UserForgotPassword>>

    @FormUrlEncoded
    @POST("users/reset-password")
    fun userResetPassword(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("users/change-password")
    fun userChangePassword(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("users/user-update-profile")
    fun userUpdateProfile(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @Multipart
    @POST("users/file-upload")
    fun uploadFile(@Part filePart: MultipartBody.Part, @Part("FilePath") filePath: RequestBody, @Part("json") json: RequestBody): Call<List<UploadFilePojo>>
    
    @FormUrlEncoded
    @POST("language/get-language-list")
    fun getLanguageListApi(@Field("json") jsonLogin: String): Call<List<GetLanguageListPojo>>

    @FormUrlEncoded
    @POST("cmspage/get-cmspage")
    fun cmsPage(@Field("json") jsonLogin: String): Call<List<CMSpagePojo>>

    @FormUrlEncoded
    @POST("users/check-mention-duplication")
    fun checkMentionedDuplication(@Field("json") jsonLogin: String): Call<List<CommonPojo>>

   @FormUrlEncoded
    @POST("users/generate-user-name")
    fun generateUserId(@Field("json") jsonLogin: String): Call<List<UserName>>

    @FormUrlEncoded
    @POST("users/user-social-login")
    fun socialLogin(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("post/get-post-list")
    fun getPostList(@Field("json") jsonLogin: String): Call<List<TrendingFeedDatum>>

    @FormUrlEncoded
    @POST("postviews/view-post")
    fun getPostView(@Field("json") jsonLogin: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("user-follower/user-follower")
    fun getUserFollow(@Field("json") jsonLogin: String): Call<List<FollowPojo>>

    @FormUrlEncoded
    @POST("post/create-post")
    fun createPost(@Field("json") jsonLogin: String): Call<List<CommonPojo>>

   /* @Multipart
    @POST("apilog/file-upload-multiple")
    fun uploadAttachmentArray(
        @Part("FilePath") firstParameter: RequestBody?,
        @Part filePath: Array<MultipartBody.Part?>?, @Part("json") json: RequestBody?
    ): Call<List<UploadFilePojo>>*/

    @Multipart
    @POST("apilog/file-upload-multiple")
    fun uploadAttachmentArray(@Part filePart : Array<MultipartBody.Part?>, /*@Part("FilePath") filePath: RequestBody,*/ @Part("json") json: RequestBody): Call<List<MultiplefileUpload>>

    @FormUrlEncoded
    @POST("postlike/post-like")
    fun getPostLike(@Field("json") jsonLogin: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("postlike/post-un-like")
    fun getPostUnLike(@Field("json") jsonLogin: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("postshare/post-share")
    fun getPostShare(@Field("json") jsonLogin: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("favoritepost/favorite-post")
    fun getPostFavorite(@Field("json") jsonLogin: String): Call<List<PostfavouritePojo>>
 
    @FormUrlEncoded
    @POST("postcomment/post-add-comment")
    fun coomentAdd(@Field("json") jsonLogin: String): Call<List<CommentPojo>>

    @FormUrlEncoded
    @POST("postcomment/post-edit-comment")
    fun coomentEdit(@Field("json") jsonLogin: String): Call<List<CommentPojo>>
   
     @FormUrlEncoded
     @POST("favoritepost/post-un-favorite")
    fun getPostUnFavorite(@Field("json") jsonLogin: String): Call<List<PostfavouritePojo>>

    @FormUrlEncoded
    @POST("postreport/post-report")
    fun getReportPost(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>
   
    @FormUrlEncoded
     @POST("postcomment/post-delete-comment")
    fun coomentDelete(@Field("json") jsonLogin: String): Call<List<CommentPojo>>

    @FormUrlEncoded
    @POST("reason/get-reason-list")
    fun reasonList(@Field("json") jsonLogin: String): Call<List<ReasonList>>

    @FormUrlEncoded
    @POST("postcomment/post-comment-report")
    fun reportComment(@Field("json") jsonLogin: String): Call<List<CommentPojo>>

    @FormUrlEncoded
    @POST("postcomment/post-comment-list")
    fun coomentList(@Field("json") jsonLogin: String): Call<List<CommentPojo>>


    @FormUrlEncoded
    @POST("posthide/post-hide")
    fun getHidePost(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>


    @FormUrlEncoded
    @POST("postshare/post-share")
    fun getSharePost(@Field("json") json: String): retrofit2.Call<List<SharePostPojo>>

    @FormUrlEncoded
    @POST("postclaps/post-claps")
    fun getClapPost(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("postclaps/post-un-clap")
    fun getUnClapPost(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("user-clap/user-add-clap")
    fun getUserClapPost(@Field("json") json: String): retrofit2.Call<List<PostClapPojo>>

    @FormUrlEncoded
    @POST("user-clap/user-remove-clap")
    fun getUserUnClapPost(@Field("json") json: String): retrofit2.Call<List<PostClapPojo>>

    @FormUrlEncoded
    @POST("users/get-my-posts")
    fun getMyPostList(@Field("json") json: String): retrofit2.Call<List<TrendingFeedDatum>>

    @FormUrlEncoded
    @POST("users/get-me-tagged-posts")
    fun getTaggedPostList(@Field("json") json: String): retrofit2.Call<List<TrendingFeedDatum>>

    @FormUrlEncoded
    @POST("users/get-my-favorite-posts")
    fun getFavoritePostList(@Field("json") json: String): retrofit2.Call<List<TrendingFeedDatum>>

    @FormUrlEncoded
    @POST("users/get-my-commented-posts")
    fun getCommentedPostList(@Field("json") json: String): retrofit2.Call<List<TrendingFeedDatum>>

    @FormUrlEncoded
    @POST("users/get-other-user-profile")
    fun getOtherUserProfile(@Field("json") json: String): retrofit2.Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("post/update-post")
    fun updatePost(@Field("json") jsonLogin: String): Call<List<CommonPojo>>
    
    @FormUrlEncoded
    @POST("post/make-post-inactive")
    fun getPostInActive(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("post/make-post-active")
    fun getPostActive(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("postlike/get-post-like-list")
    fun getPostLikeList(@Field("json") json: String): retrofit2.Call<List<PostLikeListPojo>>

    @FormUrlEncoded
    @POST("post/re-post")
    fun getRepost(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("post/get-hashtag-list")
    fun getHashTagPostList(@Field("json") jsonLogin: String): Call<List<TrendingFeedDatum>>

    @FormUrlEncoded
    @POST("post/get-location-list")
    fun getLocationPostList(@Field("json") jsonLogin: String): Call<List<TrendingFeedDatum>>

    @FormUrlEncoded
    @POST("userreport/user-report")
    fun getUserReport(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("postcommentlike/post-comment-like")
    fun getPostCommentLike(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("postcommentlike/post-comment-like-remove")
    fun getPostCommentRemoveLike(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("postcommentreply/post-comment-reply-add")
    fun getPostCommentReplyAdd(@Field("json") json: String): Call<List<ReplyComment>>

    @FormUrlEncoded
    @POST("postcommentreply/post-comment-reply-edit")
    fun getPostCommentReplyEdit(@Field("json") json: String): Call<List<ReplyComment>>

    @FormUrlEncoded
    @POST("postcommentreply/post-comment-reply-list")
    fun getPostCommentReplyList(@Field("json") json: String): Call<List<ReplyComment>>

    @FormUrlEncoded
    @POST("postcommentreply/post-comment-reply-delete")
    fun getPostCommentReplyDelete(@Field("json") json: String): Call<List<ReplyComment>>

    @FormUrlEncoded
    @POST("post/search")
    fun getGlobalSearch(@Field("json") json: String): Call<List<GlobalSearchPojo>>

    @FormUrlEncoded
    @POST("users/user-update-language")
    fun getUpdateChangeLanguage(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("users/user-delete-account")
    fun getUpdateDeleteAccount(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("user-earn-point/get-user-earn-point")
    fun getUserPoints(@Field("json") jsonLogin: String): Call<List<MyViewPoints>>

    @FormUrlEncoded
    @POST("users/user-update-signature-video")
    fun getUserAddSignatureVideo(@Field("json") jsonLogin: String): Call<List<RegisterPojo>>

    @FormUrlEncoded
    @POST("language/list-labels")
    fun getDynamicStringDictionary(@Field("json") jsonLogin: String): Call<List<GetDynamicStringDictionaryPojo1>>
    
    @FormUrlEncoded
    @POST("notification/get-notification-list")
    fun getNotificationListApi(@Field("json") jsonLogin: String): Call<List<NotificationListPojo>>

    @FormUrlEncoded
    @POST("notification/delete-notification")
    fun deleteNotificationApi(@Field("json") jsonLogin: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("notification/mark-all-as-read")
    fun allReadNotificationApi(@Field("json") jsonLogin: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("notification/update-notification-read-status")
    fun getReadNotificationApi(@Field("json") jsonLogin: String): Call<List<CommonPojo>>


}