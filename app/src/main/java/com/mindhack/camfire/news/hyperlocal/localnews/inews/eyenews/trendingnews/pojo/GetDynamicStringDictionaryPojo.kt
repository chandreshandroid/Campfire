package com.mindhack.tmf.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable

class GetDynamicStringDictionaryPojo : Serializable {

    @SerializedName("Status")
    @Expose
    var status: Boolean? = null
    @SerializedName("Message")
    @Expose
    var message: String? = null
    @SerializedName("Listdata")
    @Expose
    var listdata: List<Listdatum>? = null

    inner class Listdatum : Serializable {

        @SerializedName("No_Loan_Applied")
        @Expose
        var noLoanApplied1: String? = null

        @SerializedName("Please_upload_national_id_back_image")
        @Expose
        var pleaseUploadNationalIdBackImage: String? = null
        @SerializedName("Please_upload_national_id_front_image")
        @Expose
        var pleaseUploadNationalIdFrontImage: String? = null

        @SerializedName("enterApplicationNumbertoast")
        @Expose
        var enterApplicationNumbertoast: String? = null

        @SerializedName("enterCheckNumbertoast")
        @Expose
        var enterCheckNumbertoast: String? = null

        @SerializedName("enterDateOfBirthtoast")
        @Expose
        var enterDateOfBirthtoast: String? = null

        @SerializedName("enterDateOfRetirementtoast")
        @Expose
        var enterDateOfRetirementtoast: String? = null

        @SerializedName("enterEmployerNametoast")
        @Expose
        var enterEmployerNametoast: String? = null

        @SerializedName("enterFamilyNametoast")
        @Expose
        var enterFamilyNametoast: String? = null

        @SerializedName("enterFirstNametoast")
        @Expose
        var enterFirstNametoast: String? = null

        @SerializedName("enterIdentityCardNumbertoast")
        @Expose
        var enterIdentityCardNumbertoast: String? = null

        @SerializedName("enterLoanAmounttoast")
        @Expose
        var enterLoanAmounttoast: String? = null

        @SerializedName("enterMonthlyIncometoast")
        @Expose
        var enterMonthlyIncometoast: String? = null

        @SerializedName("enterOldPasswordtoast")
        @Expose
        var enterOldPasswordtoast: String? = null

        @SerializedName("enterOtherNametoast")
        @Expose
        var enterOtherNametoast: String? = null

        @SerializedName("enterPasswordtoast")
        @Expose
        var enterPasswordtoast: String? = null

        @SerializedName("enterPlaceOfBirthtoast")
        @Expose
        var enterPlaceOfBirthtoast: String? = null

        @SerializedName("selectDatetoast")
        @Expose
        var selectDatetoast: String? = null

        @SerializedName("About_Us")
        @Expose
        var aboutUs: String? = null
        @SerializedName("AC_No")
        @Expose
        var acNo: String? = null
        @SerializedName("Add_password_keep_your_account_safe")
        @Expose
        var addPasswordKeepYourAccountSafe: String? = null
        @SerializedName("and")
        @Expose
        var and: String? = null
        @SerializedName("Apply_for_Loan")
        @Expose
        var applyForLoan: String? = null
        @SerializedName("Approval_of_Loan")
        @Expose
        var approvalOfLoan: String? = null
        @SerializedName("Are_you_sure_you_want_to_delete_notification")
        @Expose
        var areYouSureYouWantToDeleteNotification: String? = null
        @SerializedName("Are_you_sure_you_want_to_logout")
        @Expose
        var areYouSureYouWantToLogout: String? = null
        @SerializedName("as_per_id")
        @Expose
        var asPerId: String? = null
        @SerializedName("Bank_Statement")
        @Expose
        var bankStatement: String? = null
        @SerializedName("Branch_Agent_Locator")
        @Expose
        var branchAgentLocator: String? = null
        @SerializedName("By_continuingyou_agreed_TMF")
        @Expose
        var byContinuingyouAgreedTMF: String? = null
        @SerializedName("Cancel")
        @Expose
        var cancel: String? = null
        @SerializedName("Change_language")
        @Expose
        var changeLanguage: String? = null
        @SerializedName("Change_Password")
        @Expose
        var changePassword: String? = null
        @SerializedName("Charges_Dr")
        @Expose
        var chargesDr: String? = null
        @SerializedName("Check_Number")
        @Expose
        var checkNumber: String? = null
        @SerializedName("City")
        @Expose
        var city: String? = null
        @SerializedName("Close")
        @Expose
        var close: String? = null
        @SerializedName("Company_Name")
        @Expose
        var companyName: String? = null
        @SerializedName("Completed")
        @Expose
        var completed: String? = null
        @SerializedName("Confirm_And_Continue")
        @Expose
        var confirmAndContinue: String? = null
        @SerializedName("Congratulation")
        @Expose
        var congratulation: String? = null
        @SerializedName("Contact_US")
        @Expose
        var contactUS: String? = null
        @SerializedName("Continue")
        @Expose
        var `continue`: String? = null
        @SerializedName("Continue_to_Apply")
        @Expose
        var continueToApply: String? = null
        @SerializedName("Date_of_Birth")
        @Expose
        var dateOfBirth: String? = null
        @SerializedName("Date_of_Retirement")
        @Expose
        var dateOfRetirement: String? = null
        @SerializedName("Delete")
        @Expose
        var delete: String? = null
        @SerializedName("Department")
        @Expose
        var department: String? = null
        @SerializedName("Didnt_receive_code")
        @Expose
        var didntReceiveCode: String? = null
        @SerializedName("Disbursed_Amount")
        @Expose
        var disbursedAmount: String? = null
        @SerializedName("Done")
        @Expose
        var done: String? = null
        @SerializedName("Dont_have_account")
        @Expose
        var dontHaveAccount: String? = null
        @SerializedName("Draft")
        @Expose
        var draft: String? = null
        @SerializedName("Email_Address")
        @Expose
        var emailAddress: String? = null
        @SerializedName("Email_Mobile")
        @Expose
        var emailMobile: String? = null
        @SerializedName("EMI_Reminder")
        @Expose
        var emiReminder: String? = null
        @SerializedName("Employee_Code_Cheaque_Number")
        @Expose
        var employeeCodeCheaqueNumber: String? = null
        @SerializedName("Employee_Code_or_Cheque_Number")
        @Expose
        var employeeCodeOrChequeNumber: String? = null
        @SerializedName("Employer_Name")
        @Expose
        var employerName: String? = null
        @SerializedName("Employment_ID_Proof")
        @Expose
        var employmentIDProof: String? = null
        @SerializedName("Ensure_you_are_taking_clear_photos_of_orignal_documents")
        @Expose
        var ensureYouAreTakingClearPhotosOfOrignalDocuments: String? = null
        @SerializedName("Enter_application_number")
        @Expose
        var enterApplicationNumber: String? = null
        @SerializedName("Enter_check_number")
        @Expose
        var enterCheckNumber: String? = null
        @SerializedName("Enter_company_name")
        @Expose
        var enterCompanyName: String? = null
        @SerializedName("Enter_date_of_birth")
        @Expose
        var enterDateOfBirth: String? = null
        @SerializedName("Enter_date_of_retirement")
        @Expose
        var enterDateOfRetirement: String? = null
        @SerializedName("Enter_email_address")
        @Expose
        var enterEmailAddress: String? = null
        @SerializedName("Enter_email_password")
        @Expose
        var enterEmailPassword: String? = null
        @SerializedName("Enter_employer_name")
        @Expose
        var enterEmployerName: String? = null
        @SerializedName("Enter_family_name")
        @Expose
        var enterFamilyName: String? = null
        @SerializedName("Enter_first_name")
        @Expose
        var enterFirstName: String? = null
        @SerializedName("Enter_identity_card_number")
        @Expose
        var enterIdentityCardNumber: String? = null
        @SerializedName("Enter_Loan_Amount")
        @Expose
        var enterLoanAmount: String? = null
        @SerializedName("Enter_mobile_number")
        @Expose
        var enterMobileNumber: String? = null
        @SerializedName("Enter_monthly_income")
        @Expose
        var enterMonthlyIncome: String? = null
        @SerializedName("National_Id")
        @Expose
        var nationalId: String? = null

        @SerializedName("Front")
        @Expose
        var front: String? = null
        @SerializedName("Back")
        @Expose
        var back: String? = null


        @SerializedName("Enter_new_password")
        @Expose
        var enterNewPassword: String? = null
        @SerializedName("Enter_old_password")
        @Expose
        var enterOldPassword: String? = null
        @SerializedName("Enter_other_name")
        @Expose
        var enterOtherName: String? = null
        @SerializedName("Enter_password")
        @Expose
        var enterPassword: String? = null
        @SerializedName("Enter_place_of_birth")
        @Expose
        var enterPlaceOfBirth: String? = null
        @SerializedName("Enter_re-enter_new_password")
        @Expose
        var enterReEnterNewPassword: String? = null
        @SerializedName("Enter_referral_code_Optional")
        @Expose
        var enterReferralCodeOptional: String? = null
        @SerializedName("Enter_the_verification_code_we_just_sent_you_text_message")
        @Expose
        var enterTheVerificationCodeWeJustSentYouTextMessage: String? = null
        @SerializedName("Enter_your_registered_email_address_mobile_number_reset_your_password")
        @Expose
        var enterYourRegisteredEmailAddressMobileNumberResetYourPassword: String? = null
        @SerializedName("enterCity")
        @Expose
        var enterCity: String? = null
        @SerializedName("enterCompanyType")
        @Expose
        var enterCompanyType: String? = null
        @SerializedName("enterCompnayName")
        @Expose
        var enterCompnayName: String? = null
        @SerializedName("enterCorrectEmail")
        @Expose
        var enterCorrectEmail: String? = null
        @SerializedName("enterCorrectMobile")
        @Expose
        var enterCorrectMobile: String? = null
        @SerializedName("enterDepartment")
        @Expose
        var enterDepartment: String? = null
        @SerializedName("enterEmailAdress")
        @Expose
        var enterEmailAdress: String? = null
        @SerializedName("enterEmailMobile")
        @Expose
        var enterEmailMobile: String? = null
        @SerializedName("enterEmployeeCode")
        @Expose
        var enterEmployeeCode: String? = null
        @SerializedName("enterLoanType")
        @Expose
        var enterLoanType: String? = null
        @SerializedName("enterMinimumEmployeeCode")
        @Expose
        var enterMinimumEmployeeCode: String? = null
        @SerializedName("enterMinPassword")
        @Expose
        var enterMinPassword: String? = null
        @SerializedName("enterMobileNo")
        @Expose
        var enterMobileNo: String? = null
        @SerializedName("enterNewpassword")
        @Expose
        var enterNewpassword: String? = null
        @SerializedName("enterNoOfInstallment")
        @Expose
        var enterNoOfInstallment: String? = null
        @SerializedName("enterOccupation")
        @Expose
        var enterOccupation: String? = null
        @SerializedName("enterOTP")
        @Expose
        var enterOTP: String? = null
        @SerializedName("enterRemark")
        @Expose
        var enterRemark: String? = null
        @SerializedName("enterReNewpassword")
        @Expose
        var enterReNewpassword: String? = null
        @SerializedName("enterRetypePassword")
        @Expose
        var enterRetypePassword: String? = null
        @SerializedName("enterSamePassword")
        @Expose
        var enterSamePassword: String? = null
        @SerializedName("enterSamepassword")
        @Expose
        var enterSamepassword: String? = null
        @SerializedName("enterValidMonthlyIncome")
        @Expose
        var enterValidMonthlyIncome: String? = null
        @SerializedName("Family_Name")
        @Expose
        var familyName: String? = null
        @SerializedName("FAQ")
        @Expose
        var faq: String? = null
        @SerializedName("Feedback")
        @Expose
        var feedback: String? = null
        @SerializedName("Female")
        @Expose
        var female: String? = null
        @SerializedName("First_Name")
        @Expose
        var firstName: String? = null
        @SerializedName("For_support_related_queries_write_to")
        @Expose
        var forSupportRelatedQueriesWriteTo: String? = null
        @SerializedName("Forgot_password")
        @Expose
        var forgotPassword: String? = null
        @SerializedName("Gallery")
        @Expose
        var gallery: String? = null
        @SerializedName("Gender")
        @Expose
        var gender: String? = null
        @SerializedName("Get_Direction")
        @Expose
        var getDirection: String? = null
        @SerializedName("Happy_with_this_service")
        @Expose
        var happyWithThisService: String? = null
        @SerializedName("History")
        @Expose
        var history: String? = null
        @SerializedName("Home")
        @Expose
        var home: String? = null
        @SerializedName("ID_Proof")
        @Expose
        var idProof: String? = null
        @SerializedName("Identity_Card_Number")
        @Expose
        var identityCardNumber: String? = null
        @SerializedName("In_Process")
        @Expose
        var inProcess: String? = null
        @SerializedName("Installment_Amount")
        @Expose
        var installmentAmount: String? = null
        @SerializedName("Installment_Due_Date")
        @Expose
        var installmentDueDate: String? = null
        @SerializedName("Installment_End_Date")
        @Expose
        var installmentEndDate: String? = null
        @SerializedName("Installment_Start_Date")
        @Expose
        var installmentStartDate: String? = null
        @SerializedName("installmentNotAvalible")
        @Expose
        var installmentNotAvalible: String? = null
        @SerializedName("Interest_Amount")
        @Expose
        var interestAmount: String? = null
        @SerializedName("Interest_Rate")
        @Expose
        var interestRate: String? = null
        @SerializedName("Interest_to_Be_Paid")
        @Expose
        var interestToBePaid: String? = null
        @SerializedName("Invite_Friend")
        @Expose
        var inviteFriend: String? = null
        @SerializedName("keep_you_ID_and_other_document_ready")
        @Expose
        var keepYouIDAndOtherDocumentReady: String? = null
        @SerializedName("Last_6_Months")
        @Expose
        var last6Months: String? = null
        @SerializedName("List")
        @Expose
        var list: String? = null
        @SerializedName("Loan_Agreement")
        @Expose
        var loanAgreement: String? = null
        @SerializedName("Loan_Application")
        @Expose
        var loanApplication: String? = null
        @SerializedName("Loan_Application_Number")
        @Expose
        var loanApplicationNumber: String? = null
        @SerializedName("Loan_Details")
        @Expose
        var loanDetails: String? = null
        @SerializedName("Loan_Disbursement")
        @Expose
        var loanDisbursement: String? = null
        @SerializedName("Loan_Eligibility")
        @Expose
        var loanEligibility: String? = null
        @SerializedName("Loan_Type")
        @Expose
        var loanType: String? = null
        @SerializedName("Logout")
        @Expose
        var logout: String? = null
        @SerializedName("Make_Payment")
        @Expose
        var makePayment: String? = null
        @SerializedName("Male")
        @Expose
        var male: String? = null
        @SerializedName("Map")
        @Expose
        var map: String? = null
        @SerializedName("Marital_Status")
        @Expose
        var maritalStatus: String? = null
        @SerializedName("Married")
        @Expose
        var married: String? = null
        @SerializedName("min_6_characters")
        @Expose
        var min6Characters: String? = null
        @SerializedName("minAge")
        @Expose
        var minAge: String? = null
        @SerializedName("minimuChequeNo")
        @Expose
        var minimuChequeNo: String? = null
        @SerializedName("minimuEmployerName")
        @Expose
        var minimuEmployerName: String? = null
        @SerializedName("minimuFamilyName")
        @Expose
        var minimuFamilyName: String? = null
        @SerializedName("minimuFirstName")
        @Expose
        var minimuFirstName: String? = null
        @SerializedName("minimuOtherName")
        @Expose
        var minimuOtherName: String? = null
        @SerializedName("minimuPlaceOfBirth")
        @Expose
        var minimuPlaceOfBirth: String? = null
        @SerializedName("minMaxLoanAmount")
        @Expose
        var minMaxLoanAmount: String? = null
        @SerializedName("Missed_Installment_Details")
        @Expose
        var missedInstallmentDetails: String? = null
        @SerializedName("Mobile_Number")
        @Expose
        var mobileNumber: String? = null
        @SerializedName("Month")
        @Expose
        var month: String? = null
        @SerializedName("Monthly_Income")
        @Expose
        var monthlyIncome: String? = null
        @SerializedName("More_Information")
        @Expose
        var moreInformation: String? = null
        @SerializedName("My_Loans")
        @Expose
        var myLoans: String? = null
        @SerializedName("Need_more_help")
        @Expose
        var needMoreHelp: String? = null
        @SerializedName("New_Password")
        @Expose
        var newPassword: String? = null
        @SerializedName("News_Offer")
        @Expose
        var newsOffer: String? = null
        @SerializedName("Next_Installment_Due_on")
        @Expose
        var nextInstallmentDueOn: String? = null
        @SerializedName("No")
        @Expose
        var no: String? = null
        @SerializedName("Notification_Settings")
        @Expose
        var notificationSettings: String? = null
        @SerializedName("Notifications")
        @Expose
        var notifications: String? = null
        @SerializedName("Occupation_Employment")
        @Expose
        var occupationEmployment: String? = null
        @SerializedName("Old_Password")
        @Expose
        var oldPassword: String? = null
        @SerializedName("Opening_Balance")
        @Expose
        var openingBalance: String? = null
        @SerializedName("Optional")
        @Expose
        var optional: String? = null
        @SerializedName("Other_Name")
        @Expose
        var otherName: String? = null
        @SerializedName("Others")
        @Expose
        var others: String? = null
        @SerializedName("Our_team_will_verify_the_document_and_get_back_to_you_sppn")
        @Expose
        var ourTeamWillVerifyTheDocumentAndGetBackToYouSppn: String? = null
        @SerializedName("Paid_Cr")
        @Expose
        var paidCr: String? = null
        @SerializedName("Password")
        @Expose
        var password: String? = null
        @SerializedName("Pay_at_the_Office")
        @Expose
        var payAtTheOffice: String? = null
        @SerializedName("Pay_by_Mobile_Money")
        @Expose
        var payByMobileMoney: String? = null
        @SerializedName("Pay_EMI")
        @Expose
        var payEMI: String? = null
        @SerializedName("Pay_Installment")
        @Expose
        var payInstallment: String? = null
        @SerializedName("Pay_into_Account")
        @Expose
        var payIntoAccount: String? = null
        @SerializedName("Place_of_Birth")
        @Expose
        var placeOfBirth: String? = null
        @SerializedName("Please_contact_our_branch_agent_for_more_details")
        @Expose
        var pleaseContactOurBranchAgentForMoreDetails: String? = null

        @SerializedName("please_enter_city")
        @Expose
        var pleaseEnterCity: String? = null

        @SerializedName("please_enter_occupation")
        @Expose
        var pleaseEnterOccupation: String? = null

        @SerializedName("Privacy_Policy")
        @Expose
        var privacyPolicy: String? = null
        @SerializedName("Profile")
        @Expose
        var profile: String? = null
        @SerializedName("Protect_Your_Account")
        @Expose
        var protectYourAccount: String? = null
        @SerializedName("Purpose_of_meeting")
        @Expose
        var purposeOfMeeting: String? = null
        @SerializedName("Rate_the_App")
        @Expose
        var rateTheApp: String? = null
        @SerializedName("Received")
        @Expose
        var received: String? = null
        @SerializedName("Re-enter_New_Password")
        @Expose
        var reEnterNewPassword: String? = null
        @SerializedName("Referral_Code")
        @Expose
        var referralCode: String? = null
        @SerializedName("Rejection_of_Loan")
        @Expose
        var rejectionOfLoan: String? = null
        @SerializedName("Remaining_Installments")
        @Expose
        var remainingInstallments: String? = null
        @SerializedName("Remarks")
        @Expose
        var remarks: String? = null
        @SerializedName("Request_A_Meeting")
        @Expose
        var requestAMeeting: String? = null
        @SerializedName("Request_Meeting")
        @Expose
        var requestMeeting: String? = null
        @SerializedName("Resend")
        @Expose
        var resend: String? = null
        @SerializedName("Reset_Password")
        @Expose
        var resetPassword: String? = null
        @SerializedName("Retype_your_password")
        @Expose
        var retypeYourPassword: String? = null
        @SerializedName("sameOldpassword")
        @Expose
        var sameOldpassword: String? = null
        @SerializedName("Select_City")
        @Expose
        var selectCity: String? = null
        @SerializedName("Select_Company_Type")
        @Expose
        var selectCompanyType: String? = null
        @SerializedName("Select_Contry_Code")
        @Expose
        var selectContryCode: String? = null
        @SerializedName("Select_Date")
        @Expose
        var selectDate: String? = null
        @SerializedName("Select_date_of_birth")
        @Expose
        var selectDateOfBirth: String? = null
        @SerializedName("Select_date_of_retirement")
        @Expose
        var selectDateOfRetirement: String? = null
        @SerializedName("Select_Department")
        @Expose
        var selectDepartment: String? = null
        @SerializedName("Select_Language")
        @Expose
        var selectLanguage: String? = null
        @SerializedName("Select_Loan_Type")
        @Expose
        var selectLoanType: String? = null
        @SerializedName("Select_Number_of_Installment")
        @Expose
        var selectNumberOfInstallment: String? = null
        @SerializedName("Select_Occupation_or_Employment")
        @Expose
        var selectOccupationOrEmployment: String? = null
        @SerializedName("Select_Photo")
        @Expose
        var selectPhoto: String? = null
        @SerializedName("Select_Purpose_of_Meeting")
        @Expose
        var selectPurposeOfMeeting: String? = null
        @SerializedName("selectAllDocuments")
        @Expose
        var selectAllDocuments: String? = null
        @SerializedName("selectPurposeMeeting")
        @Expose
        var selectPurposeMeeting: String? = null
        @SerializedName("Send_your_referral_code_to_your_friends_and_invite_to_join_us")
        @Expose
        var sendYourReferralCodeToYourFriendsAndInviteToJoinUs: String? = null
        @SerializedName("Settings")
        @Expose
        var settings: String? = null
        @SerializedName("Share")
        @Expose
        var share: String? = null
        @SerializedName("Share_few_words")
        @Expose
        var shareFewWords: String? = null
        @SerializedName("Sign_in")
        @Expose
        var signIn: String? = null
        @SerializedName("Sign_in_to_continue")
        @Expose
        var signInToContinue: String? = null
        @SerializedName("Sign_up")
        @Expose
        var signUp: String? = null
        @SerializedName("Single")
        @Expose
        var single: String? = null
        @SerializedName("Sorry")
        @Expose
        var sorry: String? = null
        @SerializedName("Statement")
        @Expose
        var statement: String? = null
        @SerializedName("strongPassword")
        @Expose
        var strongPassword: String? = null
        @SerializedName("Submit")
        @Expose
        var submit: String? = null
        @SerializedName("Success")
        @Expose
        var success: String? = null
        @SerializedName("Suitable_Date")
        @Expose
        var suitableDate: String? = null
        @SerializedName("Take_photo")
        @Expose
        var takePhoto: String? = null
        @SerializedName("Talk_with_our_expert_on_below_number")
        @Expose
        var talkWithOurExpertOnBelowNumber: String? = null
        @SerializedName("Terms_Conditions")
        @Expose
        var termsConditions: String? = null
        @SerializedName("Thanks_for_your_application")
        @Expose
        var thanksForYourApplication: String? = null
        @SerializedName("Total_Amount")
        @Expose
        var totalAmount: String? = null
        @SerializedName("Total_Amount_Maturity")
        @Expose
        var totalAmountMaturity: String? = null
        @SerializedName("Total_Installments")
        @Expose
        var totalInstallments: String? = null
        @SerializedName("Total_Outstanding")
        @Expose
        var totalOutstanding: String? = null
        @SerializedName("Update")
        @Expose
        var update: String? = null
        @SerializedName("Upload_Documents")
        @Expose
        var uploadDocuments: String? = null
        @SerializedName("Verification_Code")
        @Expose
        var verificationCode: String? = null
        @SerializedName("Verify_Mobile")
        @Expose
        var verifyMobile: String? = null
        @SerializedName("Verify_Password")
        @Expose
        var verifyPassword: String? = null
        @SerializedName("Verify_Your_Phone_Number")
        @Expose
        var verifyYourPhoneNumber: String? = null
        @SerializedName("View_All")
        @Expose
        var viewAll: String? = null
        @SerializedName("View_Follow_Up")
        @Expose
        var viewFollowUp: String? = null
        @SerializedName("View_missed_installment_make_payment")
        @Expose
        var viewMissedInstallmentMakePayment: String? = null
        @SerializedName("View_Statement")
        @Expose
        var viewStatement: String? = null
        @SerializedName("We_are_here_to_help_you")
        @Expose
        var weAreHereToHelpYou: String? = null
        @SerializedName("Welcome")
        @Expose
        var welcome: String? = null
        @SerializedName("Write_here")
        @Expose
        var writeHere: String? = null
        @SerializedName("Yes")
        @Expose
        var yes: String? = null
        @SerializedName("You_are_eligible")
        @Expose
        var youAreEligible: String? = null
        @SerializedName("You_are_not_eligible_for_this_loan")
        @Expose
        var youAreNotEligibleForThisLoan: String? = null
        @SerializedName("Your_Desired_Loan_Amount")
        @Expose
        var yourDesiredLoanAmount: String? = null
        @SerializedName("Your_Photo")
        @Expose
        var yourPhoto: String? = null
        @SerializedName("Your_Referral_Code")
        @Expose
        var yourReferralCode: String? = null

        @SerializedName("Please_enter_Date_of_retirement_and_monthly_income_in_profile")
        @Expose
        var pleaseenterDateofretirementandmonthlyincomeinprofile: String? = null

        @SerializedName("Months")
        @Expose
        var months: String? = null

        @SerializedName("OK")
        @Expose
        var ok: String? = null

        @SerializedName("Eligibility_Criteria")
        @Expose
        var eligibilitycriteria: String? = null

        @SerializedName("Valid_Age")
        @Expose
        var validage: String? = null

        @SerializedName("Valid_Retirement_Age")
        @Expose
        var validretirementage: String? = null

        @SerializedName("Min.Monthly_Income")
        @Expose
        var minmonthlyincome: String? = null

        @SerializedName("Camera")
        @Expose
        var camera: String? = null

        @SerializedName("please_upload_profile_picture")
        @Expose
        var pleaseuploadprofilepicture: String? = ""

        @SerializedName("Yrs")
        @Expose
        var yrs: String? = null

        @SerializedName("You_can_change_from_here")
        @Expose
        var youcanchangefromhere: String? = null

        @SerializedName("You_can_change_your_national_id_from_your_profile")
        @Expose
        var youcanchangeyournationalidfromyourprofile: String? = null

        @SerializedName("No_Feedback")
        @Expose
        var no_feedback: String? = null

        @SerializedName("Waiting_for_disbursement")
        @Expose
        var waiting_for_disbursement: String? = ""

        @SerializedName("Other")
        @Expose
        var other: String? = ""

        @SerializedName("Select_loan_application_number")
        @Expose
        var selectloanapplicationnumber : String? = ""

        @SerializedName("Desired_Amount")
        @Expose
        var desiredamount : String? = ""

        @SerializedName("No_Data_Found")
        @Expose
        var nodatafound : String? = ""

//        @SerializedName("No_Feedback")
//        @Expose
//        var nofeedback : String? = ""

        @SerializedName("No_Internet_Connection")
        @Expose
        var nointernet : String? = ""

        @SerializedName("Something_Went_Wrong")
        @Expose
        var somthingwentrong : String? = ""

        @SerializedName("Please_check_your_internet_connectivity_and_try_again")
        @Expose
        var checkinternetconnectivity : String? = ""

        @SerializedName("Approved")
        @Expose
        var approved : String? = ""

        @SerializedName("Are_you_sure_you_want_to_delete_this_loan")
        @Expose
        var areyousureyouwanttodeletethisloan : String? = ""

        @SerializedName("Please_enter_department_name")
        @Expose
        var pleaseenterdepartmentname : String? = ""

        @SerializedName("Special_Character_not_allowed")
        @Expose
        var specialcharacternotallowed : String? = ""

        @SerializedName("Are_you_sure_to_delete")
        @Expose
        var areyousuretodelete : String? = ""

        @SerializedName("It_will_remove_picture")
        @Expose
        var itwillremovepicture : String? = ""

        @SerializedName("Permission_denied")
        @Expose
        var permission_denied : String? = ""

        @SerializedName("Please_install_file_manager")
        @Expose
        var pleaseinstallfilemanager : String? = ""

        @SerializedName("Please_select_valid_image")
        @Expose
        var pleaseselectvalidimage : String? = ""

        @SerializedName("Minimum_Age")
        @Expose
        var minimumage : String? = ""

        @SerializedName("Disbursed")
        @Expose
        var disbursed : String? = ""

        @SerializedName("Disburse")
        @Expose
        var disburse : String? = ""

        @SerializedName("Approve")
        @Expose
        var approve : String? = ""

        @SerializedName("Select")
        @Expose
        var select : String? = ""

        @SerializedName("Mobile_Number_Should_Contain_9_Character")
        @Expose
        var mobile_number_should_contain_9_character : String? = ""

        @SerializedName("Failed_to_make_payment")
        @Expose
        var failedtomakepayment : String? = ""

        @SerializedName("Rating_can_not_be_zero")
        @Expose
        var ratingcannotbezero : String? = ""

        @SerializedName("Please_Select_different_date")
        @Expose
        var pleaseselectdifferentdate : String? = ""

        @SerializedName("Penalty_included")
        @Expose
        var penalty_included : String? = ""

    }
}