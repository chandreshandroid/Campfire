package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import android.net.Uri
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import java.io.File
import java.io.Serializable

data class AddImages(
    var sdcardPath: String? = "",
    var isSelectedImage :Boolean= false,
    @Transient  var sdCardUri: Uri? = null,
    var imageDrawable: Int?=-1,
    var imageName: String="",
    var isEdited: Boolean=false,
    var num:String="0",
    var videoFile:File?=null,
    var formType:String="",
    var transferId: Int = 0,
    var state: TransferState? = null,

    var type: String? = null,
    var fromServer: String? = null,
    var width: Int = 0,
    var height: Int = 0,
    var file: File? = null,
    var from: String? = "",
    var isCompress: Boolean = false
):Serializable


