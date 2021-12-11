package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.pojo

import java.io.File

/**
 * Created by dhavalkaka on 17/01/2018.
 */
data class awsFile (
    var fileName: String? = null,
    var uploadFile: File? = null,
    var downloadFile: Boolean = false

)