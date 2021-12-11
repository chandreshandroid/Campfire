package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util

import android.content.Context
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.R
import com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo.GetDynamicStringDictionaryPojo1
import com.mindhack.flymyowncustomer.util.PrefDb

object GetDynamicStringDictionaryObjectClass {

    var instance: GetDynamicStringDictionaryObjectClass? = null
    var context : Context? = null
    var prefDb : PrefDb? = null
    var sessionManager : SessionManager? =null

    var getDynamicStringDictionaryPojo: GetDynamicStringDictionaryPojo1? = null
    var listdatum: GetDynamicStringDictionaryPojo1.Datum? = null

    var titleForgotPWD = ""
    var titleContinue_with = ""
    var titleDon_t_have_an_account = ""
    var titleregister = ""
    var registration = ""
    var btnTitleSignIn = ""
    var EmailMobilenohint = ""
    var EditPWDhint = ""
    var msgFailToRegister = ""
    var msgSomthingRong = ""
    var msgNoInternet = ""
    var mag_failed_to_login = ""
    /* Register UI */
    var firstName = ""
    var lastName = ""
    var next = ""
    var Or_Register_With = ""
    var Email_Id = ""
    var Mobile_Number = ""
    var Optional = ""
    var Mobile_Number_Verification_is_required_to_post_News = ""
    var Passwrod = ""
    var title_terms = ""
    var title_privacyPolicy = ""
    var title_andConact = ""

    var Sign_Up = ""
    var Be_a_Reporter = ""
    var Save = ""
    var Verify = ""
    var Verified = ""

    var UserId = ""
    var Edit_Profile = ""
    var Settings = ""
    var Sign_Out = ""

    /* Reset Password*/
    var Reset_Password = ""
    var Enter_New_Password = ""
    var Re_enter_New_Password = ""

    /* Forgot Password */
    var Enter_your_Mobile_NoEmail_Id = ""
    var Send_OTP = ""
    var OTP_Verification = ""
    var Please_enter_the_4_digit_OTP_received_on_your_phone_number = ""
    var Proceed_ = ""
    var Resend = ""

    /* Setting */

    var Change_Password = ""
    var Change_Language = ""
    var Delete_Account = ""
    var Notifications = ""

    /* Change Password */
    var Enter_Old_Password = ""

    /* User Profile*/

    var View_Points = ""
    var Posts = ""
    var Followers = ""
    var Following = ""
    var Follow = ""
    var Draft = ""
    var User_Profile = ""
    var My_Profile = ""
    var Report_Account = ""

    /* Notification */

    var Mark_all_as_Read = ""

    /* Comments UI*/
    var Comments = ""
    var Reply = ""

    var Search_Followers = ""
    var Search_Following = ""

    var Recent = ""
    var Top = ""

    // Gobal Searching
    var Current_Location = ""
    var Search_Tags = ""
    var Tags = ""
    var Places = ""
    var People = ""
    var Custom = ""
    var Nearby_Location = ""
    var Points = ""
    var Reason_for_reporting = ""

    var Post_listing_Feeds = ""
    var Read_More = ""
    var Read_less = ""
    var Liked_By = ""
    var and = ""
    var more = ""
    var Report_this_Post = ""
    var Report_Post = ""
    var Hide_Post_For_Me = ""
    var Hide_Post = ""
    var People_Who_reacted = ""
    var All = ""

    /*  Create Post */
    var Video = ""
    var Photo = ""
    var Post_News = ""
    var Add_Headline = ""
    var Minimum_5_Characters = ""
    var Add_Description = ""
    var Characters_left = ""
    var Add_Audio_over_Video = ""
    var Public = ""
    var Private = ""
    var Change_to_Private = ""
    var Change_to_Public = ""
    var Save_Post_Now = ""
    var OK = ""

    var  Write_a_Comments= ""
    var No_Data_Found= "No Data Found"
    var Signature_Video= "Signature Video"
    var No_Internet_Connection= "No Internet Connection"
    var Something_Went_Wrong= "Something Went Wrong"
    var Please_check_your_internet_connectivity_and_try_again= "Please check your internet connectivity and try again"
    var  Retry= "Retry"
    var Search_= "Search"
    var Log_out= "Log out"
    var Repost_= "Repost"
    var you_= "You"
    var Hide_For_Everyone= "Hide For Everyone"
    var Terms_Conditions= "Terms & Conditions"
    var Privacy_Prolicy= "Privacy Prolicy"
    var By_signing_up_you_agree_with_the= "By signing up you agree with the"
    var Gallery_= "Gallery"
    var Select_Image= "Select Image"
    var Take_Photo= "Take Photo"
    var Cancel_= "Cancel"
    var Add_= "Add"
    var Select_Video= "Select Video"
    var  Take_Video= "Take Video"
    var  Are_you_sure_want_to_delete_your_account= "Are you sure you want to logout?"
    var  Are_you_sure_want_to_logout_your_account= "Are you sure you want to logout?"
    var  Add_Signature_Video= ""
    var  This_Video_will_be_merge_with_all_your_post= ""
    var  Add_this_Video= ""
    var  Enter_Location= ""


    /*msgFailToRegister = "Failed to login if you have any query please contact to admin."
    msgSomthingRong = resources.getString(R.string.error_crash_error_message)
    msgNoInternet = resources.getString(R.string.error_common_netdon_t_have_and_accountwork)
    mag_failed_to_login = resources.getString(R.string.failed_to_login)*/

    fun getInstance(context: Context) {
//        if (instance == null) {
            this.context = context
            prefDb = PrefDb(context)
            instance = GetDynamicStringDictionaryObjectClass
            getDynamicStringDictionaryPojo = prefDb?.get_DynamicStringDictionary()

            if (getDynamicStringDictionaryPojo !== null) {

                if (getDynamicStringDictionaryPojo?.data!!.isNotEmpty()) {
                    listdatum = getDynamicStringDictionaryPojo?.data!![0]!!
                    if(listdatum != null){
                        titleForgotPWD = listdatum?.forgotPassword!!
                        titleContinue_with = listdatum?.orContinueWith!!
                        titleDon_t_have_an_account = listdatum?.dontHaveAnAccount!!
                        titleregister = listdatum?.register!!
                        registration = listdatum?.registration!!
                        btnTitleSignIn = listdatum?.signIn!!
                        EmailMobilenohint = listdatum?.enterEmailMobile!!
                        EditPWDhint = listdatum?.enterPassword!!
                        /* Register UI */
                        firstName = listdatum?.firstName!!
                        lastName = listdatum?.lastName!!
                        next = listdatum?.next!!
                        Or_Register_With = listdatum?.orRegisterWith!!
                        Email_Id = listdatum?.emailId!!
                        Passwrod = listdatum?.passwrod!!

                        title_terms =if(!listdatum?.Terms_Conditions.isNullOrEmpty()){
                            listdatum?.Terms_Conditions!!
                        } else{
                            ""
                        }
                        title_privacyPolicy =if(!listdatum?.Privacy_Prolicy.isNullOrEmpty())
                        {
                            listdatum?.Privacy_Prolicy!!
                        }
                        else
                        {""}
                        title_andConact = " "+listdatum?.and+" "

                        Sign_Up = listdatum?.signUp!!
                        Be_a_Reporter = listdatum?.beAReporter!!
                        Save = listdatum?.save!!
                        Verify = listdatum?.verify!!
                        Verified = listdatum?.verified!!

                        UserId = listdatum?.userId!!
                        Edit_Profile = listdatum?.editProfile!!
                        Settings = listdatum?.settings!!
                        Sign_Out = listdatum?.signOut!!

                        // Reset Password
                        Reset_Password = listdatum?.resetPassword!!
                        Enter_New_Password = listdatum?.enterNewPassword!!
                        Re_enter_New_Password = listdatum?.reEnterNewPassword!!

                        // Forgot Password
                        Enter_your_Mobile_NoEmail_Id = listdatum?.enterYourMobileNoEmailId!!
                        Send_OTP = listdatum?.sendOTP!!
                        OTP_Verification = listdatum?.otpVerification!!
                        Please_enter_the_4_digit_OTP_received_on_your_phone_number = listdatum?.pleaseEnterThe4DigitOTPReceivedOnYourPhoneNumber!!
                        Proceed_ = listdatum?.proceed!!
                        Resend = listdatum?.resend!!

                        // Setting
                        Change_Password = listdatum?.changePassword!!
                        Change_Language = listdatum?.changeLanguage!!
                        Delete_Account = listdatum?.deleteAccount!!
                        Notifications = listdatum?.notifications!!

                        Enter_Old_Password= listdatum?.enterOldPassword!!

                        View_Points = ""+listdatum?.viewPoints!!
                        Posts = ""+listdatum?.posts!!
                        Followers = ""+listdatum?.followers!!
                        Following = ""+listdatum?.following!!
                        Follow = ""+listdatum?.follow!!
                        Draft = ""+listdatum?.draft!!
                        User_Profile = ""+if(!listdatum?.userProfile.isNullOrEmpty()){
                            listdatum?.userProfile!!
                        }else{
                            ""
                        }
                        My_Profile = ""+listdatum?.myProfile!!
                        Report_Account = ""+listdatum?.reportAccount!!

                        // Notification

                        Mark_all_as_Read = listdatum?.markAllAsRead!!

                        // Comment Ui
                        Comments = ""+ listdatum?.comments
                        Reply = ""+ listdatum?.reply

                        Search_Followers = ""+ listdatum?.searchFollowers
                        Search_Following = ""+ listdatum?.searchFollowing

                        Recent = ""+listdatum?.recent
                        Top = ""+ listdatum?.top

                        // Global Searching

                        Current_Location = ""+ listdatum?.currentLocation
                        Search_Tags = ""+ listdatum?.searchTags
                        Tags = ""+ listdatum?.tags
                        Places = ""+ listdatum?.places
                        People = ""+ listdatum?.people
                        Custom = ""+ listdatum?.custom
                        Nearby_Location = ""+ listdatum?.nearbyLocation
                        Points = ""+ listdatum?.points
                        Reason_for_reporting = ""+ listdatum?.reasonForReporting

                        Post_listing_Feeds = ""+ listdatum?.postListingFeeds
                        Read_More = ""+ listdatum?.readMore
                        Read_less = ""+ listdatum?.Read_less
                        Liked_By = ""+ listdatum?.likedBy
                        and = ""+ listdatum?.and
                        more = ""+ listdatum?.more
                        Report_this_Post = ""+ listdatum?.reportThisPost
                        Report_Post = ""+ listdatum?.reportPost
                        Hide_Post_For_Me = ""+ listdatum?.hidePostForMe
                        Hide_Post = ""+ listdatum?.hidePost
                        People_Who_reacted = ""+ listdatum?.peopleWhoReacted
                        All = ""+ listdatum?.all

                        Video = ""+ listdatum?.video
                        Photo = ""+ listdatum?.photo
                        Post_News = ""+ listdatum?.postNews
                        Add_Headline = ""+ listdatum?.addHeadline
                        Minimum_5_Characters = ""+ listdatum?.minimum5Characters
                        Add_Description = ""+ listdatum?.addDescription
                        Characters_left = ""+ listdatum?.charactersLeft
                        Add_Audio_over_Video = ""+ listdatum?.addAudioOverVideo
                        Public = ""+ listdatum?.public
                        Private = ""+ listdatum?.private
                        Change_to_Private = ""+ listdatum?.changeToPrivate
                        Change_to_Public = ""+ listdatum?.changeToPublic
                        Save_Post_Now = ""+ listdatum?.save
                        OK = ""+ listdatum?.ok

                        Write_a_Comments= ""+ listdatum?.Write_a_Comments
                        No_Data_Found= ""+ listdatum?.No_Data_Found
                        Signature_Video= ""+ listdatum?.Signature_Video
                        No_Internet_Connection= ""+ listdatum?.No_Internet_Connection
                        Something_Went_Wrong= ""+ listdatum?.Something_Went_Wrong
                        Please_check_your_internet_connectivity_and_try_again= ""+ listdatum?.Please_check_your_internet_connectivity_and_try_again
                         Retry= ""+ listdatum?.Retry
                        Search_= ""+ listdatum?.Search_
                        Log_out= ""+ listdatum?.Log_out
                        Repost_= ""+ listdatum?.Repost_

                        you_= ""+ listdatum?.you_

                        Hide_For_Everyone= ""+ listdatum?.Hide_For_Everyone
                        Terms_Conditions= ""+ listdatum?.Terms_Conditions
                        Privacy_Prolicy= ""+ listdatum?.Privacy_Prolicy
                        By_signing_up_you_agree_with_the= ""+ listdatum?.By_signing_up_you_agree_with_the
                        Gallery_= ""+ listdatum?.Gallery_
                        Select_Image= ""+ listdatum?.Select_Image
                        Take_Photo= ""+ listdatum?.Take_Photo
                        Cancel_= ""+ listdatum?.Cancel_
                        Add_= ""+ listdatum?.Add_
                        Select_Video= ""+ listdatum?.Select_Video
                         Take_Video= ""+ listdatum?.Take_Video

                        Mobile_Number = ""+ listdatum?.mobileNumber
                        Optional = ""+ listdatum?.optional
                        Mobile_Number_Verification_is_required_to_post_News = ""+ listdatum?.mobileNumberVerificationIsRequiredToPostNews

                        Are_you_sure_want_to_delete_your_account= listdatum?.Are_you_sure_want_to_delete_your_account!!
                        if(!listdatum?.Are_you_sure_want_to_logout_your_account!!.isNullOrEmpty())
                        {
                            Are_you_sure_want_to_logout_your_account=  listdatum?.Are_you_sure_want_to_logout_your_account!!
                        }
                        else
                        {
                            Are_you_sure_want_to_logout_your_account=""
                        }
                        Add_Signature_Video= listdatum?.Add_Signature_Video!!
                        This_Video_will_be_merge_with_all_your_post= listdatum?.This_Video_will_be_merge_with_all_your_post!!
                        Add_this_Video= listdatum?.Add_this_Video!!
                        Enter_Location= listdatum?.Enter_Location!!

                    }
                }
            }else {
                titleForgotPWD = this.context!!.resources.getString(R.string.forgot_password)
                titleContinue_with = this.context!!.resources.getString(R.string.or_continue_with)
                titleDon_t_have_an_account =
                    this.context!!.resources.getString(R.string.don_t_have_an_account)
                titleregister = this.context!!.resources.getString(R.string.register)
                registration = this.context!!.resources.getString(R.string.registration)
                btnTitleSignIn = this.context!!.resources.getString(R.string.sign_in)
                EmailMobilenohint = this.context!!.resources.getString(R.string.enter_email_mobile)
                EditPWDhint = this.context!!.resources.getString(R.string.enter_password)

                /* Register UI */
                firstName =  this.context!!.resources.getString(R.string.first_name)
                lastName =  this.context!!.resources.getString(R.string.last_name)
                next =  this.context!!.resources.getString(R.string.next)
                Or_Register_With =  this.context!!.resources.getString(R.string.or_register_with)
                Email_Id =  this.context!!.resources.getString(R.string.email_id)
                Mobile_Number = ""+"Mobile Number"
                Optional = ""+"(Optional)"
                Mobile_Number_Verification_is_required_to_post_News = ""+this.context!!.resources.getString(R.string.mobile_number_verification_is_required_to_post_news)
                Passwrod = ""+this.context!!.resources.getString(R.string.password)

                title_terms = this.context!!.resources.getString(R.string.terms1)
                title_privacyPolicy = this.context!!.resources.getString(R.string.accept_Privacy_Poilicy)
                title_andConact = " and "

                Sign_Up = ""+this.context!!.resources.getString(R.string.sign_up)
                Be_a_Reporter = ""+this.context!!.resources.getString(R.string.be_repoter)
                Save = ""+this.context!!.resources.getString(R.string.save)
                Verify = "Verify"
                Verified = ""+this.context!!.resources.getString(R.string.verified)

                UserId = ""+this.context!!.resources.getString(R.string.user_id)
                Edit_Profile = ""+this.context!!.resources.getString(R.string.edit_profile)
                Settings = ""+this.context!!.resources.getString(R.string.settings)
                Sign_Out = "Sign Out"

                // Reset Password
                Reset_Password = ""+this.context!!.resources.getString(R.string.reset_pwd)
                Enter_New_Password = ""+this.context!!.resources.getString(R.string.enter_new_password)
                Re_enter_New_Password = ""+this.context!!.resources.getString(R.string.re_enter_new_password)

                // Forgout Password
                Enter_your_Mobile_NoEmail_Id = ""+this.context!!.resources.getString(R.string.enter_your_mobile_no_email_id)
                Send_OTP = ""+this.context!!.resources.getString(R.string.send_otp)
                OTP_Verification = ""+this.context!!.resources.getString(R.string.OTP_verification)
                Please_enter_the_4_digit_OTP_received_on_your_phone_number = ""+this.context!!.resources.getString(R.string.please_type_the_verification_code_sent_to_tyour_mobile_number)
                Proceed_ = "Proceed "
                Resend = ""+this.context!!.resources.getString(R.string.resend)

                // Setting
                Change_Password = ""+this.context!!.resources.getString(R.string.change_password)
                Change_Language = ""+this.context!!.resources.getString(R.string.change_language)
                Delete_Account = ""+this.context!!.resources.getString(R.string.delete_account)
                Notifications = ""+this.context!!.resources.getString(R.string.notifications)

                Enter_Old_Password= ""+this.context!!.resources.getString(R.string.enter_old_password)

                // User Profile
                View_Points = "View Points"
                Posts = "Posts"
                Followers = "Followers"
                Following = "Following"
                Follow = "Follow"
                Draft = "Draft"
                User_Profile = "User Profile"
                My_Profile = "My Profile"
                Report_Account = "Report Account"

                Mark_all_as_Read = ""+this.context!!.resources.getString(R.string.markasallread)

                Comments = ""+this.context!!.resources.getString(R.string.comments)
                Reply = ""+this.context!!.resources.getString(R.string.reply)

                Search_Followers = ""+this.context!!.resources.getString(R.string.search_followers)
                Search_Following = ""+this.context!!.resources.getString(R.string.search_following)

                Recent = ""+this.context!!.resources.getString(R.string.recent)
                Top = ""+this.context!!.resources.getString(R.string.top)

                // Global Searching
                Current_Location = ""+this.context!!.resources.getString(R.string.Current_Location)
                Search_Tags = ""+this.context!!.resources.getString(R.string.Search_Tags)
                Tags = ""+this.context!!.resources.getString(R.string.Tags)
                Places = ""+this.context!!.resources.getString(R.string.Places)
                People = ""+this.context!!.resources.getString(R.string.People)
                Custom = ""+this.context!!.resources.getString(R.string.Custom)
                Nearby_Location = ""+this.context!!.resources.getString(R.string.Nearby_Location)
                Points = "Points"
                Reason_for_reporting = "Reason for reporting"

                Post_listing_Feeds = ""+ "Post listing (Feeds)"
                Read_More = ""+ "Read More.."
                Read_less = ""+ "Read less.."
                Liked_By = ""+ "Liked By"
                and = ""+ "and"
                more = ""+ "more"
                Report_this_Post = ""+ "Repost this Post"
                Report_Post = ""+ "Report Post"
                Hide_Post_For_Me = ""+ "Hide Post For Me"
                Hide_Post = ""+ "Hide Post"
                People_Who_reacted = ""+"People Who reacted"
                All = ""+ "All"

                Video= "Video"
                Photo= "Photo"
                Post_News= "Post News"
                Add_Headline= "Add Headline"
                Minimum_5_Characters= "(Minimum 5 Characters)"
                Add_Description= "Add Description"
                Characters_left= "Characters left"
                Add_Audio_over_Video= "Add Audio over Video"
                Public= "Public"
                Private= "Private"
                Change_to_Private= "Change to Private"
                Change_to_Public= "Change to Public"
                Save_Post_Now= "Save Post Now"
                OK= "OK"

                Write_a_Comments= "Write a comment..."
                No_Data_Found= "No Data Found"
                Signature_Video= "Signature Video"
                No_Internet_Connection= "No Internet Connection"
                Something_Went_Wrong= "Something Went Wrong"
                Please_check_your_internet_connectivity_and_try_again= "Please check your internet connectivity and try again"
                Retry= "Retry"
                Search_= "Search"
                Log_out= "Log out"
                Repost_= "Repost"
                you_= "You"
                Hide_For_Everyone= "Hide For Everyone"
                Terms_Conditions= "Terms & Conditions"
                Privacy_Prolicy= "Privacy Prolicy"
                By_signing_up_you_agree_with_the= "By signing up you agree with the"
                Gallery_= "Gallery"
                Select_Image= "Select Image"
                Take_Photo= "Take Photo"
                Cancel_= "Cancel"
                Add_= "Add"
                Select_Video= "Select Video"
                Take_Video= "Take Video"
                Are_you_sure_want_to_delete_your_account= "Are you sure you want to logout?"
                Are_you_sure_want_to_logout_your_account= "Are you sure you want to logout?"

            }
//        }
//        return instance!!
    }


}