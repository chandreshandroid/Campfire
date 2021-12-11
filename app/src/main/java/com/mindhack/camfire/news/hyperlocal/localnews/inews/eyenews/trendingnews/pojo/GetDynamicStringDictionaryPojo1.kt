package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetDynamicStringDictionaryPojo1 : Serializable{

    @SerializedName("status")
    @Expose
    var status: String?=""
    @SerializedName("info")
    @Expose
    var info: String? = ""
    @SerializedName("data")
    @Expose
    var data: List<Datum>? = listOf()

    inner class Datum : Serializable {

        @SerializedName("Select_a_language")
        @Expose
        var selectALanguage: String? = ""
        @SerializedName("You_can_change_it_from_the_settings_later")
        @Expose
        var youCanChangeItFromTheSettingsLater: String? = ""
        @SerializedName("Continue")
        @Expose
        var `continue`: String? = ""
        @SerializedName("Enter_EmailMobile")
        @Expose
        var enterEmailMobile: String? = ""
        @SerializedName("Enter_Password")
        @Expose
        var enterPassword: String? = ""
        @SerializedName("Forgot_Password")
        @Expose
        var forgotPassword: String? = ""
        @SerializedName("Sign_In")
        @Expose
        var signIn: String? = ""
        @SerializedName("Or_Continue_With")
        @Expose
        var orContinueWith: String? = ""
        @SerializedName("Dont_have_an_account")
        @Expose
        var dontHaveAnAccount: String? = ""
        @SerializedName("Register")
        @Expose
        var register: String? = ""
        @SerializedName("Enter_your_Mobile_NoEmail_Id")
        @Expose
        var enterYourMobileNoEmailId: String? = ""
        @SerializedName("Send_OTP")
        @Expose
        var sendOTP: String? = ""
        @SerializedName("OTP_Verification")
        @Expose
        var otpVerification: String? = ""
        @SerializedName("Please_enter_the_4_digit_OTP_received_on_your_phone_number")
        @Expose
        var pleaseEnterThe4DigitOTPReceivedOnYourPhoneNumber: String? = ""
        @SerializedName("Proceed_")
        @Expose
        var proceed: String? = ""
        @SerializedName("Resend")
        @Expose
        var resend: String? = ""
        @SerializedName("Reset_Password")
        @Expose
        var resetPassword: String? = ""
        @SerializedName("Enter_New_Password")
        @Expose
        var enterNewPassword: String? = ""
        @SerializedName("Re-enter_New_Password")
        @Expose
        var reEnterNewPassword: String? = ""
        @SerializedName("Registration")
        @Expose
        var registration: String? = ""
        @SerializedName("First_Name")
        @Expose
        var firstName: String? = ""
        @SerializedName("Last_Name")
        @Expose
        var lastName: String? = ""
        @SerializedName("Next")
        @Expose
        var next: String? = ""
        @SerializedName("Or_Register_With")
        @Expose
        var orRegisterWith: String? = ""
        @SerializedName("Email_Id")
        @Expose
        var emailId: String? = ""
        @SerializedName("Mobile_Number")
        @Expose
        var mobileNumber: String? = ""
        @SerializedName("Optional")
        @Expose
        var optional: String? = ""
        @SerializedName("Mobile_Number_Verification_is_required_to_post_News")
        @Expose
        var mobileNumberVerificationIsRequiredToPostNews: String? = ""
        @SerializedName("Passwrod")
        @Expose
        var passwrod: String? = ""
        @SerializedName("By_signing_up_you_agree_with_the_Terms_Conditions_and_Privacy_Prolicy")
        @Expose
        var bySigningUpYouAgreeWithTheTermsConditionsAndPrivacyProlicy: String? = ""
        @SerializedName("Sign_Up")
        @Expose
        var signUp: String? = ""
        @SerializedName("Be_a_Reporter")
        @Expose
        var beAReporter: String? = ""
        @SerializedName("Save")
        @Expose
        var save: String? = ""
        @SerializedName("Verify")
        @Expose
        var verify: String? = ""
        @SerializedName("Verified")
        @Expose
        var verified: String? = ""
        @SerializedName("UserId")
        @Expose
        var userId: String? = ""
        @SerializedName("Edit_Profile")
        @Expose
        var editProfile: String? = ""
        @SerializedName("Settings")
        @Expose
        var settings: String? = ""
        @SerializedName("Sign_Out")
        @Expose
        var signOut: String? = ""
        @SerializedName("View_Points")
        @Expose
        var viewPoints: String? = ""
        @SerializedName("Posts")
        @Expose
        var posts: String? = ""
        @SerializedName("Followers")
        @Expose
        var followers: String? = ""
        @SerializedName("Following")
        @Expose
        var following: String? = ""
        @SerializedName("Report_Account")
        @Expose
        var reportAccount: String? = ""
        @SerializedName("Follow")
        @Expose
        var follow: String? = ""
        @SerializedName("Draft")
        @Expose
        var draft: String? = ""
        @SerializedName("User_Profile")
        @Expose
        var userProfile: String? = ""
        @SerializedName("My_Profile")
        @Expose
        var myProfile: String? = ""
        @SerializedName("Search_Followers")
        @Expose
        var searchFollowers: String? = ""
        @SerializedName("Search_Following")
        @Expose
        var searchFollowing: String? = ""
        @SerializedName("Points")
        @Expose
        var points: String? = ""
        @SerializedName("Broze_Level")
        @Expose
        var brozeLevel: String? = ""
        @SerializedName("Change_Password")
        @Expose
        var changePassword: String? = ""
        @SerializedName("Change_Language")
        @Expose
        var changeLanguage: String? = ""
        @SerializedName("Delete_Account")
        @Expose
        var deleteAccount: String? = ""
        @SerializedName("Notifications")
        @Expose
        var notifications: String? = ""
        @SerializedName("Enter_Old_Password")
        @Expose
        var enterOldPassword: String? = ""
        @SerializedName("Reason_for_reporting")
        @Expose
        var reasonForReporting: String? = ""
        @SerializedName("Comments")
        @Expose
        var comments: String? = ""
        @SerializedName("Reply")
        @Expose
        var reply: String? = ""
        @SerializedName("Post_listing_Feeds")
        @Expose
        var postListingFeeds: String? = ""
        @SerializedName("Read_More")
        @Expose
        var readMore: String? = ""
        @SerializedName("Read_less")
        @Expose
        var Read_less: String? = ""
        @SerializedName("Liked_By")
        @Expose
        var likedBy: String? = ""
        @SerializedName("and")
        @Expose
        var and: String? = ""
        @SerializedName("more")
        @Expose
        var more: String? = ""
        @SerializedName("Report_this_Post")
        @Expose
        var reportThisPost: String? = ""
        @SerializedName("Report_Post")
        @Expose
        var reportPost: String? = ""
        @SerializedName("Hide_Post_For_Me")
        @Expose
        var hidePostForMe: String? = ""
        @SerializedName("Hide_Post")
        @Expose
        var hidePost: String? = ""
        @SerializedName("People_Who_reacted")
        @Expose
        var peopleWhoReacted: String? = ""
        @SerializedName("All")
        @Expose
        var all: String? = ""
        @SerializedName("Mark_all_as_Read")
        @Expose
        var markAllAsRead: String? = ""
        @SerializedName("Current_Location")
        @Expose
        var currentLocation: String? = ""
        @SerializedName("Search_Tags")
        @Expose
        var searchTags: String? = ""
        @SerializedName("Tags")
        @Expose
        var tags: String? = ""
        @SerializedName("Places")
        @Expose
        var places: String? = ""
        @SerializedName("People")
        @Expose
        var people: String? = ""
        @SerializedName("Custom")
        @Expose
        var custom: String? = ""
        @SerializedName("Nearby_Location")
        @Expose
        var nearbyLocation: String? = ""
        @SerializedName("Recent")
        @Expose
        var recent: String? = ""
        @SerializedName("Top")
        @Expose
        var top: String? = ""
        @SerializedName("Video")
        @Expose
        var video: String? = ""
        @SerializedName("Photo")
        @Expose
        var photo: String? = ""
        @SerializedName("Post_News")
        @Expose
        var postNews: String? = ""
        @SerializedName("Add_Headline")
        @Expose
        var addHeadline: String? = ""
        @SerializedName("Minimum_5_Characters")
        @Expose
        var minimum5Characters: String? = ""
        @SerializedName("Add_Description")
        @Expose
        var addDescription: String? = ""
        @SerializedName("Characters_left")
        @Expose
        var charactersLeft: String? = ""
        @SerializedName("Add_Audio_over_Video")
        @Expose
        var addAudioOverVideo: String? = ""
        @SerializedName("Public")
        @Expose
        var public: String? = ""
        @SerializedName("Private")
        @Expose
        var private: String? = ""
        @SerializedName("Change_to_Private")
        @Expose
        var changeToPrivate: String? = ""
        @SerializedName("Change_to_Public")
        @Expose
        var changeToPublic: String? = ""
        @SerializedName("Save_Post_Now")
        @Expose
        var savePostNow: String? = ""
        @SerializedName("OK")
        @Expose
        var ok: String? = ""
        @SerializedName("Cancel")
        @Expose
        var cancel: String? = ""
        @SerializedName("Yes")
        @Expose
        var yes: String? = ""
        @SerializedName("No")
        @Expose
        var no: String? = ""

        @SerializedName("Write_a_Comments")
        @Expose
        var Write_a_Comments: String? = ""

        @SerializedName("No_Data_Found")
        @Expose
        var No_Data_Found: String? = ""

        @SerializedName("Signature_Video")
        @Expose
        var Signature_Video: String? = ""

        @SerializedName("No_Internet_Connection")
        @Expose
        var No_Internet_Connection: String? = ""

        @SerializedName("Something_Went_Wrong")
        @Expose
        var Something_Went_Wrong: String? = ""

        @SerializedName("Please_check_your_internet_connectivity_and_try_again")
        @Expose
        var Please_check_your_internet_connectivity_and_try_again: String? = ""

        @SerializedName("Retry")
        @Expose
        var Retry: String? = ""

        @SerializedName("Search_")
        @Expose
        var Search_: String? = ""

        @SerializedName("Log_out")
        @Expose
        var Log_out: String? = ""

        @SerializedName("Repost_")
        @Expose
        var Repost_: String? = ""

        @SerializedName("you_")
        @Expose
        var you_: String? = ""

        @SerializedName("Hide_For_Everyone")
        @Expose
        var Hide_For_Everyone: String? = ""

        @SerializedName("Terms_Conditions")
        @Expose
        var Terms_Conditions: String? = ""

        @SerializedName("Privacy_Prolicy")
        @Expose
        var Privacy_Prolicy: String? = ""

        @SerializedName("By_signing_up_you_agree_with_the")
        @Expose
        var By_signing_up_you_agree_with_the: String? = ""

        @SerializedName("Gallery_")
        @Expose
        var Gallery_: String? = ""

        @SerializedName("Select_Image")
        @Expose
        var Select_Image: String? = ""

        @SerializedName("Take_Photo")
        @Expose
        var Take_Photo: String? = ""

        @SerializedName("Cancel_")
        @Expose
        var Cancel_: String? = ""

        @SerializedName("Add_")
        @Expose
        var Add_: String? = ""

        @SerializedName("Select_Video")
        @Expose
        var Select_Video: String? = ""

        @SerializedName("Take_Video")
        @Expose
        var Take_Video: String? = ""

        @SerializedName("Are_you_sure_want_to_delete_your_account")
        @Expose
        var Are_you_sure_want_to_delete_your_account: String? = ""
        @SerializedName("Are_you_sure_want_to_logout_your_account")
        @Expose
        var Are_you_sure_want_to_logout_your_account: String? = ""
        @SerializedName("Add_Signature_Video")
        @Expose
        var Add_Signature_Video: String? = ""
        @SerializedName("This_Video_will_be_merge_with_all_your_post")
        @Expose
        var This_Video_will_be_merge_with_all_your_post: String? = ""

        @SerializedName("Add_this_Video")
        @Expose
        var Add_this_Video: String? = ""

        @SerializedName("Enter_Location")
        @Expose
        var Enter_Location: String? = ""

    }
}
